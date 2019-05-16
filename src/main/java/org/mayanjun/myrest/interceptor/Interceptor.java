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

package org.mayanjun.myrest.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.annotation.*;

/**
 * This annotation used to annotate an annotation serve as an interceptor configuration.
 * An interceptor handler that specified in an interceptor annotation that annotated on a controller or controller method will be executed.
 * <div>
 *     Example:
 *     <pre>
 *     &#64;PrintAccessLog
 *     public class TestController{
 *   	&#64;RequestMapping("test")
 *     	public Object test(){...}
 *     }
 *     </pre>
 * </div>
 *
 * @author mayanjun
 * @since 0.0.2(Jan 15, 2016)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
@Documented
public @interface Interceptor {

	/**
	 * Class instance of {@linkplain HandlerInterceptor}
	 * @return interceptor class
     */
	Class<? extends HandlerInterceptor> value();

	/**
	 * Set if load an interceptor instance from IOC container instead of new one
	 * @return return true if you want to load an instance from IOC
     */
	boolean loadFromContainer() default false;

	/**
	 *  Processor will load an interceptor instance from IOC container by name if this value is specified, otherwise by class.
	 *  It still try loading an instance by class if an interceptor not found by name.
	 * @return interceptor beanId defined in IOC container
     */
	String beanId() default "";
}
