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

import org.mayanjun.myrest.interceptor.ApplicationExceptionHandler;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * @author mayanjun
 * @since 21/08/2017
 */
public abstract class BaseController {

    @ResponseBody
    @ExceptionHandler(Throwable.class)
    private Object handleException(Throwable t, HttpServletRequest request) {
        /**
         * If this.class is annotated by @RestController
         */
        if(isRestController()) {
            return ApplicationExceptionHandler.handleAllException(t);
        } else {
            /**
             * If this.class is not annotated by @RestController but target method is annotated by @ResponseBody
             */
            Object hmo = request.getAttribute(WebMVC.REQUEST_ATTR_HANDLER_METHOD);
            if(hmo != null && hmo instanceof HandlerMethod) {
                HandlerMethod hm = (HandlerMethod) hmo;
                Method m = hm.getMethod();
                if(m != null && m.isAnnotationPresent(ResponseBody.class)) {
                    return ApplicationExceptionHandler.handleAllException(t);
                }
            }
        }

        /**
         * By default returns an SmartView
         */
        return new ModelAndView("error/500").addObject("exception", t);
    }

    private boolean isRestController() {
        Class<?> c = this.getClass();
        while (c != Object.class) {
            if(c.isAnnotationPresent(RestController.class)) return true;
            c = c.getSuperclass();
        }
        return false;
    }
}