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

import org.springframework.beans.factory.annotation.Required;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Session manager
 * @param <T>
 */
public interface Session<T> {

    /**
     * Clear current sign in status
     */
    void clear();

    /**
     * Returns current user
     * @param request HttpServletRequest
     * @return returns current user
     */
    SessionUser<T> getUser(HttpServletRequest request);

    /**
     * Returns current user
     * @return returns current user
     */
    SessionUser<T> getCurrentUser();

    /**
     * Sign in and returns user
     * @param username username
     * @param password password
     * @param response HttpServletResponse
     * @return returns user
     */
    SessionUser<T> signIn(String username, String password, HttpServletResponse response);

    /**
     * Sign out
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     */
    void signOut(HttpServletRequest request, HttpServletResponse response);

    /**
     * Returns domain
     * @return domain
     */
    String getDomain();

    /**
     * Set domain
     * @param domain domain
     */
    @Required
    void setDomain(String domain);

    /**
     * Returns token name
     * @return token name
     */
    String getTokenName();

    /**
     * Set token name
     * @param tokenName token name
     */
    void setTokenName(String tokenName);

    /**
     * Returns user loader
     * @return user loader
     */
    UserLoader<T> getUserLoader();

    /**
     * Set user loader
     * @param userLoader user loader
     */
    void setUserLoader(UserLoader<T> userLoader);
}
