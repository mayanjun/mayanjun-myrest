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
