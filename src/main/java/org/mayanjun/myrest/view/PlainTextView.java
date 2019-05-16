package org.mayanjun.myrest.view;

import org.mayanjun.core.Assert;
import org.mayanjun.core.Status;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * Used to render plain text data
 *
 * @author mayanjun
 * @since 0.0.2(Jan 15, 2016)
 */
public class PlainTextView  extends AbstractView {
	
	private static final String MIME = "text/plain";
	public static final String DEFAULT_CHARSET = "UTF-8";
	
	private String text;
	private String charset;

	/**
	 * Construct a PlainTextView with UTF-8 charset
	 * @param text text to render
     */
	public PlainTextView(String text) {
		this(text, DEFAULT_CHARSET);
	}
	
	public PlainTextView(String text, String charset) {
		this(text, charset, MIME);
	}
	
	public PlainTextView(String text, String charset, String mime) {
		this.setText(text);
		this.charset = charset;
		this.setContentType(mime + ";charset=" + this.charset);
	}

	public String getCharset() {
		return charset;
	}

	public PlainTextView setCharset(String charset) {
		this.charset = charset;
		return this;
	}

	public String getText() {
		return text;
	}

	public PlainTextView setText(String text) {
		Assert.notNull(text, Status.PARAM_ERROR);
		this.text = text;
		return this;
	}

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(this.text.getBytes(this.charset));
		response.setCharacterEncoding(this.charset);
		this.writeToResponse(response, baos);
	}
}
