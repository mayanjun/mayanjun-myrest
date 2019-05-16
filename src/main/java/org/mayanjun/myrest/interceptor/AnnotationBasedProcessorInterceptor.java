package org.mayanjun.myrest.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.mayanjun.myrest.WebMVC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.util.CollectionUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An processor interceptor used to manage and execute interceptors are bound to an interceptor annotation that annotated by @{@link Interceptor}
 * <p>
 *     All of the interceptors managed by this can implements the {@link Ordered}. The order gets smaller, the execute time gets earlier.
 * </p>
 * <p style="color:red">
 *     Note that an interceptor managed by AnnotationBasedProcessorInterceptor is treated as Singleton
 *     so it must be stateless, otherwise may be at risk of thread-safe.
 * </p>
 *
 * @author mayanjun
 * @since 0.0.2(Jan 15, 2016)
 */
public abstract class AnnotationBasedProcessorInterceptor extends HandlerInterceptorAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(AnnotationBasedProcessorInterceptor.class);

    private final Map<Method, List<HandlerInterceptor>> interceptorsCache;

    /**
     * Constructor
     */
    public AnnotationBasedProcessorInterceptor() {
        interceptorsCache = new ConcurrentHashMap<Method, List<HandlerInterceptor>>(
                new IdentityHashMap<Method, List<HandlerInterceptor>>()
        );
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        List<? extends HandlerInterceptor> interceptors = getInterceptors(request, handler);

        if (!CollectionUtils.isEmpty(interceptors)) {
            for (HandlerInterceptor interceptor : interceptors) {
                boolean ret = interceptor.preHandle(request, response, handler);
                if (!ret) return false;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        List<? extends HandlerInterceptor> interceptors = getInterceptors(null, handler);

        if (!CollectionUtils.isEmpty(interceptors)) {
            for (HandlerInterceptor interceptor : interceptors) {
                interceptor.postHandle(request, response, handler, modelAndView);
            }
        }
    }

    private List<? extends HandlerInterceptor> getInterceptors(HttpServletRequest request, Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod m = ((HandlerMethod) handler);
            if(request != null) request.setAttribute(WebMVC.REQUEST_ATTR_HANDLER_METHOD, m);

            Class<?> c = m.getBeanType();
            Method method = m.getMethod();

            List<? extends HandlerInterceptor> interceptors = this.interceptorsCache.get(method);
            if (interceptors == null) {
                interceptors = searchInterceptors(c, method, request);
            }
            return interceptors;
        }
        return null;
    }

    private List<HandlerInterceptor> searchInterceptors(Class<?> c, Method m, HttpServletRequest request) {
        // handle class Interceptor
        Annotation annos[] = c.getAnnotations();
        List<HandlerInterceptor> list = instantiateInterceptor(annos, request);

        // search method
        List<HandlerInterceptor> mlist = instantiateInterceptor(m.getAnnotations(), request);
        if (!mlist.isEmpty()) list.addAll(mlist);

        if (!CollectionUtils.isEmpty(list)) {
            // sort
            Collections.sort(list, new Comparator<HandlerInterceptor>() {
                @Override
                public int compare(HandlerInterceptor o1, HandlerInterceptor o2) {
                    int s1 = 0;
                    int s2 = 0;
                    if (o1 instanceof Ordered) s1 = ((Ordered) o1).getOrder();
                    if (o2 instanceof Ordered) s2 = ((Ordered) o2).getOrder();
                    return s1 < s2 ? -1 : (s1 > s2 ? 1 : 0);
                }
            });
        }
        LOG.info("sorted interceptor: " + m.getName() + ":" + list);

        // pirint log
        if (!list.isEmpty()) {
            String log = "handler interceptors: class=" + c.getSimpleName() + ", method=" + m + "[";
            for (HandlerInterceptor in : list) {
                log += in.getClass().getCanonicalName() + ",";
            }
            LOG.info(log + "]");
        }

        this.interceptorsCache.put(m, list);
        return list;
    }

    private List<HandlerInterceptor> instantiateInterceptor(Annotation annos[], HttpServletRequest request) {
        List<HandlerInterceptor> list = new ArrayList<HandlerInterceptor>();
        for (Annotation anno : annos) {
            if (anno.annotationType().isAnnotationPresent(Interceptor.class)) {
                Interceptor ince = anno.annotationType().getAnnotation(Interceptor.class);
                Class<? extends HandlerInterceptor> cls = ince.value();
                HandlerInterceptor inc = null;
                String from = "new";
                String by = "class";
                if (ince.loadFromContainer()) {
                    Object incObj = null;
                    String beanId = ince.beanId();
                    ApplicationContext applicationContext = getApplicationContext(request);
                    if(StringUtils.isNotBlank(beanId)) {
                        try {
                            incObj = applicationContext.getBean(beanId);
                            by = "name";
                        } catch (Exception e) {
                            LOG.warn("Load interceptor from IOC by name failed: beanId=" + beanId);
                        }
                    }
                    if(incObj == null) {
                        LOG.info("Attempt to load interceptor bean from IOC by class: {}", cls);
                        try {
                            incObj = applicationContext.getBean(cls);
                        } catch (Exception e) {
                            LOG.warn("Load interceptor from IOC by class failed: class=" + cls);
                        }
                    }
                    inc = (HandlerInterceptor) incObj;
                    from = "spring";
                } else {
                    try {
                        inc = cls.newInstance();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                if (inc != null) {
                    list.add(inc);
                    LOG.info("Interceptor instantiated: " + inc.getClass().getCanonicalName() + (", from="+ from + ", by=" + by));
                }
            }
        }
        return list;
    }

    protected ApplicationContext getApplicationContext(HttpServletRequest request) {
        return (ApplicationContext)request.getSession().getServletContext().getAttribute(WebMVC.SERVLET_APPLICATION_CONTEXT_NAME);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) throws Exception {
        List<? extends HandlerInterceptor> interceptors = getInterceptors(request, handler);

        if (!CollectionUtils.isEmpty(interceptors)) {
            for (HandlerInterceptor interceptor : interceptors) {
                interceptor.afterCompletion(request, response, handler, ex);
            }
        }
    }
}
