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

import org.mayanjun.util.Encryptions;
import org.mayanjun.util.KeyPairStore;

/**
 * WEB登录的session会话管理器
 * @author mayanjun
 * @since 28/02/2018
 */
public class RSASession<T> extends AbstractSession<T> {

    /**
     * 密钥对管理器
     */
    private KeyPairStore keyPairStore;

    public RSASession() {
        super();
    }

    public RSASession(String domain, KeyPairStore keyPairStore, UserLoader<T> userLoader) {
        super(domain, DEFAULT_TOKEN_NAME, userLoader);
        this.keyPairStore = keyPairStore;
    }

    public RSASession(String domain, KeyPairStore keyPairStore, String tokenName, UserLoader<T> userLoader) {
        super(domain, tokenName, userLoader);
        this.keyPairStore = keyPairStore;
    }

    public String decryptPassword(String password) {
        return Encryptions.decrypt(password, keyPairStore.getPrivateKey());
    }

    public String encryptPassword(String password) {
        return Encryptions.encrypt(password, keyPairStore.getPublicKey());
    }

    @Override
    public String encryptToken(String tokenPlain) {
        return Encryptions.encrypt(tokenPlain, this.keyPairStore.getPrivateKey());
    }

    @Override
    public String decryptToken(String token) {
        return Encryptions.decrypt(token, this.keyPairStore.getPublicKey());
    }


    public KeyPairStore getKeyPairStore() {
        return keyPairStore;
    }

    public void setKeyPairStore(KeyPairStore keyPairStore) {
        this.keyPairStore = keyPairStore;
    }
}
