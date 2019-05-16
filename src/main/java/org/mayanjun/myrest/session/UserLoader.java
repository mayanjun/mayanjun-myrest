package org.mayanjun.myrest.session;

/**
 * 用户加载器
 * @author mayanjun
 * @since 2018/7/19
 */
public interface UserLoader<T> {

    /**
     * 从数据库加载原始用户数据
     * @param username
     * @return
     */
    SessionUser<T> loadUser(String username);

    /**
     * 将登录用户存储到公共缓存中
     * @param user
     */
    void setUserCache(SessionUser<T> user);

    /**
     * 将已经登录的用户从缓存中删除
     * @param user
     */
    void removeUserCache(SessionUser<T> user);

    /**
     * 从缓存中获取已登录用户
     * @param username
     * @return
     */
    SessionUser<T> getUserFromCache(String username);

}
