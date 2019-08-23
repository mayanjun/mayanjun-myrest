package org.mayanjun.myrest.session;

import org.mayanjun.core.Assert;
import org.mayanjun.core.ServiceException;
import org.mayanjun.core.Status;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractSession<T> implements Session<T> {

    // about login
    public static final Status NO_SIGN_IN = new Status(2001, "用户未登录");
    public static Status USER_NOT_EXISTS = new Status(2002, "用户不存在");
    public static Status PASSWORD_INCORRECT = new Status(2003, "密码错误");


    public static final String DEFAULT_TOKEN_NAME = "mytoken";
    private ThreadLocal<SessionUser<T>> currentUser = new ThreadLocal<SessionUser<T>>();

    /**
     * 系统的域名或者域
     */
    private String domain;

    /**
     * 登录成功后的Cookie名称
     */
    private String tokenName = DEFAULT_TOKEN_NAME;

    /**
     * 用户数据加载器
     */
    private UserLoader<T> userLoader;

    public AbstractSession() {
    }

    public AbstractSession(String domain, String tokenName, UserLoader<T> userLoader) {
        this.domain = domain;
        this.tokenName = tokenName;
        this.userLoader = userLoader;
    }

    /**
     * 一个WEB请求结束后清除登录状态
     */
    @Override
    public void clear() {
        currentUser.remove();
    }

    @Override
    public SessionUser<T> getUser(HttpServletRequest request) {
        currentUser.remove();

        String token = getToken(request);
        Assert.notBlank(token, NO_SIGN_IN);

        String uat = decryptToken(token);
        Assert.notBlank(uat, NO_SIGN_IN);

        String uats[] = uat.split(";");
        SessionUser<T> user = userLoader.getUserFromCache(uats[0]);
        Assert.notNull(user, NO_SIGN_IN);

        currentUser.set(user);
        return user;
    }

    protected String getToken(HttpServletRequest request) throws ServiceException {
        Cookie cookies[] = request.getCookies();
        Assert.notNull(cookies, NO_SIGN_IN);
        String token = null;
        String cookieName = this.tokenName;
        for(Cookie cookie : cookies) {
            if(cookieName.equals(cookie.getName())) {
                token = cookie.getValue();
                break;
            }
        }
        Assert.notEmpty(token, NO_SIGN_IN);
        return token;
    }

    @Override
    public SessionUser<T> getCurrentUser() {
        SessionUser<T> user = currentUser.get();
        Assert.notNull(user, NO_SIGN_IN);
        return user;
    }

    @Override
    public SessionUser<T> signIn(String username, String password, HttpServletResponse response) {
        SessionUser<T> user = userLoader.loadUser(username);
        Assert.notNull(user, USER_NOT_EXISTS);
        String dbPassword = decryptPassword(user.getPassword());
        Assert.isTrue(password.equals(dbPassword), PASSWORD_INCORRECT);

        SessionUser<T> loginUser = new SessionUser(user);
        loginUser.setOriginUser(user.getOriginUser());
        String cookiePlain = user.getUsername() + ";" + loginUser.getLastLoginTime();
        String token = encryptToken(cookiePlain);

        userLoader.setUserCache(loginUser);
        response.addCookie(createSigninCookie(token));
        return loginUser;
    }

    protected Cookie createSigninCookie(String token) {
        Cookie cookie = new Cookie(this.tokenName, token);
        cookie.setDomain(this.domain);
        cookie.setPath("/");
        cookie.setMaxAge(3600 * 24 * 7);
        cookie.setVersion(1);
        cookie.setHttpOnly(true);
        return cookie;
    }

    protected Cookie createSignoutCookie() {
        Cookie cookie = new Cookie(this.tokenName, "-");
        cookie.setDomain(this.domain);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setVersion(1);
        return cookie;
    }

    @Override
    public void signOut(HttpServletRequest request, HttpServletResponse response) {
        SessionUser<T> user = getUser(request);
        userLoader.removeUserCache(user);
        response.addCookie(createSignoutCookie());
    }

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public String getTokenName() {
        return tokenName;
    }

    @Override
    public void setTokenName(String tokenName) {
        if(tokenName != null && !tokenName.trim().isEmpty()) {
            this.tokenName = tokenName;
        }
    }

    @Override
    public UserLoader<T> getUserLoader() {
        return this.userLoader;
    }

    @Override
    public void setUserLoader(UserLoader<T> userLoader) {
        this.userLoader = userLoader;
    }

    /**
     * Decrypt database password
     * @param password encrypted password
     * @return plain password
     */
    public abstract String decryptPassword(String password);

    /**
     * Encrypt password
     * @param password plain password
     * @return encrypted password
     */
    public abstract String encryptPassword(String password);

    /**
     * Encrypt token
     * @param tokenPlain plain token
     * @return encrypted token
     */
    public abstract String encryptToken(String tokenPlain);

    /**
     * Decrypt token
     * @param token encrypted token
     * @return returns plain token
     */
    public abstract String decryptToken(String token);
}
