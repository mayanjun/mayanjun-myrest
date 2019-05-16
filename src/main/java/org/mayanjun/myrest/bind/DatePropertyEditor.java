package org.mayanjun.myrest.bind;

import org.apache.commons.lang3.StringUtils;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Convert an date string to {@link Date} if possible.
 * @author mayanjun
 */
public class DatePropertyEditor extends PropertyEditorSupport {

	private static final String DATE_FORMATS[] = {
			"yyyy-MM-dd HH:mm:ss",
			"yyyy-MM-dd HH:mm",
			"yyyy-MM-dd",
			"yyyy/MM/dd HH:mm:ss",
			"yyyy/MM/dd HH:mm",
			"yyyy/MM/dd",
			"yyyyMMdd HH:mm:ss",
			"yyyyMMdd"
	};

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		Date date = null;
		if(StringUtils.isNotBlank(text)) {
			for(String s : DATE_FORMATS) {
				date = parseDate(s, text);
				if(date != null) break;
			}
		}
		setValue(date);
	}

	private Date parseDate(String format, String source) {
		try {
			return new SimpleDateFormat(format).parse(source);
		} catch (ParseException e) {
		}
		return null;
	}
}
