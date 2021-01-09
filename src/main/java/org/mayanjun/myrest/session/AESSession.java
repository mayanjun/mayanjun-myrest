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

import org.mayanjun.util.Crypto;
import org.springframework.beans.factory.annotation.Required;

/**
 * WEB登录的session会话管理器
 * @author mayanjun
 * @since 28/02/2018
 */
public class AESSession<T> extends AbstractSession<T> {

    private Crypto crypto;


    public AESSession() {
        super();
    }

    public AESSession(String domain, Crypto crypto, UserLoader<T> userLoader) {
        super(domain, DEFAULT_TOKEN_NAME, userLoader);
        this.crypto = crypto;
    }

    public AESSession(String domain, Crypto crypto, String tokenName, UserLoader<T> userLoader) {
        super(domain, tokenName, userLoader);
        this.crypto = crypto;
    }

    @Override
    public String decryptPassword(String password) {
        return crypto.decrypt(password);
    }

    @Override
    public String encryptPassword(String password) {
        return crypto.encrypt(password);
    }

    @Override
    public String encryptToken(String tokenPlain) {
        return crypto.encrypt(tokenPlain);
    }

    @Override
    public String decryptToken(String token) {
        return crypto.decrypt(token);
    }

    public Crypto crypto() {
        return this.crypto;
    }

    @Required
    public void setCrypto(Crypto crypto) {
        this.crypto = crypto;
    }
}
