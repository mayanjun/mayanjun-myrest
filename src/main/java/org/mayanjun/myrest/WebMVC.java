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

/**
 * @author mayanjun
 * @since 21/08/2017
 */
public class WebMVC {

    /**
     * Request attribute key to access HandlerMethod object
     */
    public static final String REQUEST_ATTR_HANDLER_METHOD = WebMVC.class.getName() + ".REQUEST_ATTR_HANDLER_METHOD";

    public static final String SERVLET_APPLICATION_CONTEXT_NAME = "org.springframework.web.servlet.FrameworkServlet.CONTEXT.dispatcher";

    public static boolean DEBUG = false;
}
