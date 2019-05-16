package org.mayanjun.myrest.interceptor;

import org.springframework.core.Ordered;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * A convenient implementation of {@link org.springframework.web.servlet.HandlerInterceptor}
 *
 * @author mayanjun
 * @since 0.0.2(Jan 15, 2016)
 */
public abstract class AnnotationBasedHandlerInterceptor extends HandlerInterceptorAdapter implements Ordered {

	private static final Map<Class<?>, Map<Class<? extends Annotation>, Annotation>> ANNO_CACHE =
			new HashMap<Class<?>, Map<Class<? extends Annotation>, Annotation>>();

	/**
	 * Find the annotation on the target method or class in which it contains
	 * @param annotationClass annotation class
	 * @param handler handler method
	 * @param <T> Annotation type to be found
     * @return annotation instance
     */
	public <T extends Annotation> T findAnnotation(Class<T> annotationClass, Object handler) {
		HandlerMethod hm = ((HandlerMethod) handler);
		
		Method method = hm.getMethod();
		T anno = method.getAnnotation(annotationClass);
		
		if(anno == null) {
			Class<?> c = hm.getBeanType();
			Map<Class<? extends Annotation>, Annotation> map = ANNO_CACHE.get(c);
			if(map == null) {
				Annotation as[] = c.getAnnotations();
				map = new HashMap<Class<? extends Annotation>, Annotation>();
				for(Annotation a : as) map.put(a.annotationType(), a);
				ANNO_CACHE.put(c, map);
			}
			//anno = c.getAnnotation(annotationClass);
			//c.getAnnotations()
			anno = (T) map.get(annotationClass);
		}
		return anno;
	}

	/**
	 * Cast type of the handler bean to specified interface type
	 * @param interfaceClass the class be cast to
	 * @param handler handler method
	 * @param <T> the type be cast to
     * @return instance of T type
     */
	public <T> T castBeanTo(Class<T> interfaceClass, Object handler) {
		HandlerMethod hm = ((HandlerMethod) handler);

		Class<?> c = hm.getBeanType();
		if(interfaceClass != null) {
			if(interfaceClass.isAssignableFrom(c)) {
				return (T)hm.getBean();
			}
		}
		return null;
	}
}
