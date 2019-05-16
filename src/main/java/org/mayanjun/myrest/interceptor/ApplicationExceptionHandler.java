package org.mayanjun.myrest.interceptor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.mayanjun.core.ServiceException;
import org.mayanjun.core.Status;
import org.mayanjun.myrest.RestResponse;
import org.mayanjun.myrest.WebMVC;
import org.mayanjun.myrest.util.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.text.SimpleDateFormat;

/**
 * Handle some exception
 * @author mayanjun
 * @since 1.0.1
 */
public class ApplicationExceptionHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(ApplicationExceptionHandler.class);

	private static final String SERVICE_EXCEPTION_CLASSNAME = ServiceException.class.getName();

	private static volatile UnknownExceptionHandler UNKNOWN_EXCEPTION_HANDLER;

    public static RestResponse handleAllException(Throwable t) {
    	RestResponse response = null;
    	boolean setE = true;
    	if(t instanceof ServiceException) {
    		setE = false;
    		response = handleInternalServiceException(t);
		} else if(t instanceof MethodArgumentTypeMismatchException) {
			String name = ((MethodArgumentTypeMismatchException) t).getName();
			response = new RestResponse(new CustomStatus(Status.PARAM_MISS.getCode(), "参数错误:" + name));
		} else if(t instanceof BindException) {
			FieldError error = ((BindException) t).getFieldError();
			String fieldName = error.getField();
			response = new RestResponse(new CustomStatus(Status.PARAM_MISS.getCode(), "参数错误:" + fieldName));
		} else if(t instanceof MissingServletRequestParameterException) {
			String paramName = ((MissingServletRequestParameterException) t).getParameterName();
			response = new RestResponse(new CustomStatus(Status.PARAM_MISS.getCode(), "缺少参数" + paramName));
		} else if(t instanceof RuntimeException) {
			response = handleSCFServiceException(t);
		} else {
			response = handleUnknownException(t);
		}
		if(response == null) return RestResponse.error().setDescription(t.getMessage());
    	if(WebMVC.DEBUG && setE) response.setDescription(t.getMessage());
    	return response;
    }

	private static RestResponse handleSCFServiceException(Throwable t) {
		String message = t.getMessage();
		StringBuffer sb = new StringBuffer();
		if(!StringUtils.isBlank(message)) {
			for(int i = 0; i < message.length(); i++) {
				char c = message.charAt(i);
				if(c == '\r' || c == '\n') break;
				sb.append(c);
			}
			String head = sb.toString();
			if(!StringUtils.isBlank(head) && head.startsWith(SERVICE_EXCEPTION_CLASSNAME)) {
				String msg = head.substring(head.indexOf(":") + 1);
				if(!StringUtils.isBlank(msg)) msg = msg.trim();
				else msg = "操作失败";
				return handleInternalServiceException(new ServiceException(Status.INTERNAL_ERROR, msg));
			}
		}
		return handleUnknownException(t);
	}

	private static RestResponse handleInternalServiceException(Throwable t) {
		ServiceException st = (ServiceException)t;
		LOG.info("Service Exception Detected: code={}, message={}, log={}, data={}", st.getStatus().getCode(), st.getMessage(), st.getLog(), JSON.se(st.getHolder()));
		RestResponse re = new RestResponse(((ServiceException) t).getStatus());

		if(((ServiceException) t).getHolder() != null) re.putAll(((ServiceException) t).getHolder());
		return re;
	}

	private static RestResponse handleUnknownException(Throwable t) {
		LOG.error("Error Detected: {} > {}", t.getClass().getCanonicalName(), t.getMessage(), t);
		try {
			if(UNKNOWN_EXCEPTION_HANDLER != null) return UNKNOWN_EXCEPTION_HANDLER.handleException(t);
		} catch (Throwable e) {
			return new RestResponse(Status.INTERNAL_ERROR);
		}
		return new RestResponse(Status.INTERNAL_ERROR);
	}

    private static class CustomStatus extends Status {
    	public CustomStatus(int code, String message) {
    		super(code,message);
		}
    }

	/**
	 * 安装未知异常处理器
	 * @param handler
	 */
	public static void installUnknownExceptionHandler(UnknownExceptionHandler handler) {
    	UNKNOWN_EXCEPTION_HANDLER = handler;
	}

	/**
	 * 用户自定义的未知异常处理器
	 */
	public static interface UnknownExceptionHandler {
		RestResponse handleException(Throwable t);
	}
}