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

package org.mayanjun.myrest.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.StringUtils;
import org.mayanjun.myrest.RestResponse;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * An implementation of {@link org.springframework.web.servlet.View} used to
 * render an result data in JSONP protocol
 *
 * @author mayanjun
 * @since 0.0.2(Jan 15, 2016)
 * @see org.springframework.web.servlet.mvc.method.annotation.AbstractJsonpResponseBodyAdvice
 * @see org.springframework.web.bind.annotation.ControllerAdvice
 * @see org.springframework.web.bind.annotation.RestControllerAdvice
 */
public class JsonpView extends AbstractView {
	
	private static final String DEFAULT_CALLBACK = "callback";
	private static final String MIME = "application/javascript";
	private static final String DEFAULT_CHARSET = "utf-8";

	/**
	 * The basic rule of Jackson thread-safety is that factories follow "thread-safe after configuration" philosophy. This means that:
	 * <ul>
	 *     <li>Configuring an instance is not synchronized or thread-safe, i.e. do not change settings while using it (which makes sense for other reasons too -- not all settings take effect once mapper has been in use, due to caching of serializers and deserializers)</li>
	 *     <li>Once configuration is complete, operation is fully thread-safe and synchronized in few places where that is needed, for symbol table and buffer reuse.</li>
	 * </ul>
	 *
	 * So as long as you first configure such factories from a single thread, and only then use it (from any number of threads), usage will be thread-safe without additional synchronization.
	 *
	 * This rule specifically is used for:
	 * <ul>
	 *     <li>ObjectMapper (and sub-classes)</li>
	 *     <li>JsonFactory (and sub-classes, like SmileFactory)</li>
	 * </ul>
	 */
	private static final ObjectMapper OBJECT_MAPPER;
	private static final String identifierRegex = "^[a-zA-z_$][a-zA-z0-9_$]*$";

	private RestResponse result;
	private String charset;

	static {
		OBJECT_MAPPER = new ObjectMapper();
		OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}
	
	public JsonpView(RestResponse result) {
		this(result, DEFAULT_CHARSET);
	}

	public JsonpView(RestResponse result, String charset) {
		this.result = result;
		this.setContentType(MIME + ";charset=" + charset);
		this.charset = charset;
	}
	

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String callback = request.getParameter("callback");
		if(StringUtils.isNotBlank(callback)) {
			if(!callback.matches(identifierRegex)) callback = DEFAULT_CALLBACK;
		} else {
			callback = DEFAULT_CALLBACK;
		}
		
		String prefix = callback + "(";
		String suffix = ");";
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		stream.write(prefix.getBytes(JsonEncoding.UTF8.getJavaName()));
		JsonGenerator generator = OBJECT_MAPPER.getFactory().createGenerator(stream, JsonEncoding.UTF8);

		// A workaround for JsonGenerators not applying serialization features
		// https://github.com/FasterXML/jackson-databind/issues/12
		if (this.OBJECT_MAPPER.isEnabled(SerializationFeature.INDENT_OUTPUT)) {
			generator.useDefaultPrettyPrinter();
		}
		
		this.OBJECT_MAPPER.writeValue(generator, this.result);
		stream.write(suffix.getBytes(JsonEncoding.UTF8.getJavaName()));
		response.setCharacterEncoding(this.charset);
		this.writeToResponse(response, stream);
	}
}
