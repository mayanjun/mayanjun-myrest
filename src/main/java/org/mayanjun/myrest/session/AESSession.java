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

import org.mayanjun.util.AES;
import org.mayanjun.util.SecretKeyStore;
import org.springframework.beans.factory.annotation.Required;

/**
 * WEB登录的session会话管理器
 * @author mayanjun
 * @since 28/02/2018
 */
public class AESSession<T> extends AbstractSession<T> {

    /**
     * AES 秘钥管理器，用来创建Cookie
     */
    private SecretKeyStore secretKeyStore;


    public AESSession() {
        super();
    }

    public AESSession(String domain, SecretKeyStore keyPairStore, UserLoader<T> userLoader) {
        super(domain, DEFAULT_TOKEN_NAME, userLoader);
        this.secretKeyStore = keyPairStore;
    }

    public AESSession(String domain, SecretKeyStore keyPairStore, String tokenName, UserLoader<T> userLoader) {
        super(domain, tokenName, userLoader);
        this.secretKeyStore = keyPairStore;
    }

    @Override
    public String decryptPassword(String password) {
        return AES.decryptString(password, secretKeyStore.iv(), secretKeyStore.key());
    }

    @Override
    public String encryptPassword(String password) {
        return AES.encryptString(password, secretKeyStore.iv(), secretKeyStore.key());
    }

    @Override
    public String encryptToken(String tokenPlain) {
        return AES.encryptString(tokenPlain, secretKeyStore.iv(), secretKeyStore.key());
    }

    @Override
    public String decryptToken(String token) {
        return AES.decryptString(token, secretKeyStore.iv(), secretKeyStore.key());
    }

    public SecretKeyStore getSecretKeyStore() {
        return secretKeyStore;
    }

    @Required
    public void setSecretKeyStore(SecretKeyStore secretKeyStore) {
        this.secretKeyStore = secretKeyStore;
    }
}
