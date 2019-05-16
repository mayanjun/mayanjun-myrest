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
