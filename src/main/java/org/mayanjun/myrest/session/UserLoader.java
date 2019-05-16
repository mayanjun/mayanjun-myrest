/*
 * Copyright 2016-2018 mayanjun.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
