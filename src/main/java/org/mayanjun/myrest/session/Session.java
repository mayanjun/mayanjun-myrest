package org.mayanjun.myrest.session;

import org.mayanjun.core.Assert;
import org.mayanjun.core.ServiceException;
import org.mayanjun.core.Status;
import org.mayanjun.util.Encryptions;
import org.mayanjun.util.KeyPairStore;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * WEB登录的session会话管理器
 * @author mayanjun
 * @since 28/02/2018
 */
public class Session<T> {

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
     * 密钥对管理器
     */
    private KeyPairStore keyPairStore;

    /**
     * 用户登录超时时间：单位秒
     */
    private long loginTimeout;

    /**
     * 登录成功后的Cookie名称
     */
    private String tokenName = DEFAULT_TOKEN_NAME;

    /**
     * 用户数据加载器
     */
    private UserLoader<T> userLoader;

    /**
     * 一个WEB请求结束后清除登录状态
     */
    public void clear() {
        currentUser.remove();
    }

    public SessionUser<T> getUser(HttpServletRequest request) {
        currentUser.remove();

        String token = getToken(request);
        Assert.notBlank(token, NO_SIGN_IN);

        String uat = Encryptions.decrypt(token, this.keyPairStore.getPublicKey());
        Assert.notBlank(uat, NO_SIGN_IN);

        String uats[] = uat.split(";");
        SessionUser<T> user = userLoader.getUserFromCache(uats[0]);
        Assert.notNull(user, NO_SIGN_IN);

        currentUser.set(user);
        return user;
    }

    public SessionUser<T> getCurrentUser() {
        SessionUser<T> user = currentUser.get();
        Assert.notNull(user, NO_SIGN_IN);
        return user;
    }

    public SessionUser<T> signIn(String username, String password, HttpServletResponse response) {
        SessionUser<T> user = userLoader.loadUser(username);
        Assert.notNull(user, USER_NOT_EXISTS);
        String dbPassword = decryptPassword(user.getPassword());
        Assert.isTrue(password.equals(dbPassword), PASSWORD_INCORRECT);

        SessionUser<T> loginUser = new SessionUser(user);
        loginUser.setOriginUser(user.getOriginUser());
        String cookiePlain = user.getUsername() + ";" + loginUser.getLastLoginTime();
        String token = Encryptions.encrypt(cookiePlain, this.keyPairStore.getPrivateKey());

        userLoader.setUserCache(loginUser);
        response.addCookie(createSigninCookie(token));
        return loginUser;
    }

    public void signOut(HttpServletRequest request, HttpServletResponse response) {
        SessionUser<T> user = getUser(request);
        userLoader.removeUserCache(user);
        response.addCookie(createSignoutCookie());
    }

    private String getToken(HttpServletRequest request) throws ServiceException {
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

    private Cookie createSigninCookie(String token) {
        Cookie cookie = new Cookie(this.tokenName, token);
        cookie.setDomain(this.domain);
        cookie.setPath("/");
        cookie.setMaxAge(3600 * 24 * 7);
        cookie.setVersion(1);
        return cookie;
    }

    private Cookie createSignoutCookie() {
        Cookie cookie = new Cookie(this.tokenName, "-");
        cookie.setDomain(this.domain);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setVersion(1);
        return cookie;
    }

    public String decryptPassword(String password) {
        return Encryptions.decrypt(password, keyPairStore.getPrivateKey());
    }

    public String encryptPassword(String password) {
        return Encryptions.encrypt(password, keyPairStore.getPublicKey());
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public KeyPairStore getKeyPairStore() {
        return keyPairStore;
    }

    public void setKeyPairStore(KeyPairStore keyPairStore) {
        this.keyPairStore = keyPairStore;
    }

    public long getLoginTimeout() {
        return loginTimeout;
    }

    public void setLoginTimeout(long loginTimeout) {
        this.loginTimeout = loginTimeout;
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        if(tokenName != null && !tokenName.trim().isEmpty()) {
            this.tokenName = tokenName;
        }
    }

    public UserLoader<T> getUserLoader() {
        return userLoader;
    }

    public void setUserLoader(UserLoader<T> userLoader) {
        this.userLoader = userLoader;
    }
}
