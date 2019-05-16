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
