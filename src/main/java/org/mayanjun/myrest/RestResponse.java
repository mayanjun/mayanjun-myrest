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

package org.mayanjun.myrest;

import org.mayanjun.core.Status;

import java.util.HashMap;
import java.util.Map;

/**
 * Result Object. Note that do not use 'code','msg' or 'desc' as a data key
 * @author mayanjun
 * @since 21/08/2017
 */
public class RestResponse extends HashMap<String, Object> {

    public static final String CODE_KEY = "code";
    public static final String MSG_KEY = "msg";
    public static final String DESC_KEY = "desc";
    public static final String DATA_KEY = "data";

    public RestResponse() {
        this(0, "OK");
    }

    public RestResponse(int code, String message) {
        super();
        setStatus(code, message);
    }

    private void setStatus(int code, String message) {
        this.put(CODE_KEY, code);
        this.put(MSG_KEY, message);
    }

    public static RestResponse ok() {
        return new RestResponse(Status.OK);
    }

    public static RestResponse ok(Object data) {
        return new RestResponse(Status.OK).setData(data);
    }

    public static RestResponse error() {
        return new RestResponse(Status.INTERNAL_ERROR);
    }

    public static RestResponse error(Object data) {
        return new RestResponse(Status.INTERNAL_ERROR).setData(data);
    }

    public RestResponse(Status status) {
        super();
        if(status != null) setStatus(status.getCode(), status.getMessage());
    }

    public RestResponse setData(Object object) {
        this.put(DATA_KEY, object);
        return this;
    }

    public Object getData() {
        return this.get(DATA_KEY);
    }

    public int getCode() {
        Object o = get(CODE_KEY);
        if (o != null && o instanceof Number) return ((Number) o).intValue();
        return 0;
    }

    public RestResponse setCode(int code) {
        this.put(CODE_KEY, code);
        return this;
    }

    public String getMessage() {
        Object msg = get(MSG_KEY);
        if(msg != null) return msg.toString();
        return null;
    }

    public RestResponse setMessage(String message) {
        this.put(MSG_KEY, message);
        return this;
    }

    public RestResponse add(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public RestResponse addAll(Map<? extends String, ?> m) {
        super.putAll(m);
        return this;
    }


    public RestResponse setDescription(String description) {
        this.put(DESC_KEY, description);
        return this;
    }

    public String getDescription() {
        Object desc = get(DESC_KEY);
        if(desc != null) return desc.toString();
        return null;
    }
}
