package org.mayanjun.myrest.session;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * UserAgentUtils
 *
 * @author mayanjun(10/12/15)
 */
public final class UserAgentUtils {

    private UserAgentUtils() {}

    private static final Pattern PATTERN_APPLE = Pattern.compile("(Macintosh|iPhone|iPad|iPod|iOS)", Pattern.CASE_INSENSITIVE);

    private static final Pattern PATTERN_IOS = Pattern.compile("(iPhone|iPad|iPod|iOS)", Pattern.CASE_INSENSITIVE);

    private static final Pattern PATTERN_ANDROID = Pattern.compile("(Android)", Pattern.CASE_INSENSITIVE);

    private static final Pattern PATTERN_WINDOWS_PHONE = Pattern.compile("(Windows Phone)", Pattern.CASE_INSENSITIVE);

    public static final String USER_AGENT_HEADER = "user-agent";


    public static boolean isIOSPlatform(String userAgent) {
        if(StringUtils.isEmpty(userAgent)) return false;
        Matcher m = PATTERN_IOS.matcher(userAgent);
        return m.find();
    }

    public static boolean isIOSPlatform(HttpServletRequest request) {
        String userAgent = request.getHeader(USER_AGENT_HEADER);
        return isIOSPlatform(userAgent);
    }

    public static boolean isApplePlatform(String userAgent) {
        if(StringUtils.isEmpty(userAgent)) return false;
        Matcher m = PATTERN_APPLE.matcher(userAgent);
        return m.find();
    }

    public static boolean isApplePlatform(HttpServletRequest request) {
        String userAgent = request.getHeader(USER_AGENT_HEADER);
        return isApplePlatform(userAgent);
    }

    public static boolean isAndroidPlatform(String userAgent) {
        if(StringUtils.isEmpty(userAgent)) return false;
        Matcher m = PATTERN_ANDROID.matcher(userAgent);
        return m.find();
    }

    public static boolean isAndroidPlatform(HttpServletRequest request) {
        String userAgent = request.getHeader(USER_AGENT_HEADER);
        return isAndroidPlatform(userAgent);
    }

    public static boolean isMobilePlatform(String userAgent) {
        return (isAndroidPlatform(userAgent) || isIOSPlatform(userAgent) || isWindowsPhonePlatform(userAgent));
    }

    public static boolean isMobilePlatform(HttpServletRequest request) {
        String userAgent = request.getHeader(USER_AGENT_HEADER);
        return isMobilePlatform(userAgent);
    }

    public static boolean isNotMobilePlatform(HttpServletRequest request) {
        return !isMobilePlatform(request);
    }

    public static boolean isWindowsPhonePlatform(String userAgent) {
        if(StringUtils.isEmpty(userAgent)) return false;
        Matcher m = PATTERN_WINDOWS_PHONE.matcher(userAgent);
        return m.find();
    }

    public static boolean isWindowsPhonePlatform(HttpServletRequest request) {
        String userAgent = request.getHeader(USER_AGENT_HEADER);
        return isWindowsPhonePlatform(userAgent);
    }
}
