package top.cokernut.customwebview.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlUtil {
    public static List<String> getMetas(String str) {
        List<String> metas = new ArrayList<>();
       // Pattern p = Pattern.compile("<meta.*name=\"okwei:app:menu\".*?>");
        Pattern p = Pattern.compile("<meta.*?>");
        Matcher matcher = p.matcher(str);
        while (matcher.find()) {
            metas.add(str.substring(matcher.start() ,matcher.end()));
            if (matcher.end() + 1 < str.length()) {
                str = str.substring(matcher.end() + 1, str.length());
                matcher = p.matcher(str);
            }
        }
        return metas;
    }

    public static String getTitle(String str) {
        Pattern p = Pattern.compile("title=\".*?\"");
        Matcher matcher = p.matcher(str);
        if (matcher.find()) {
            str = str.substring(matcher.start() + 7 ,matcher.end() - 1);
            return str;
        } else {
            return null;
        }
    }

    public static String getIcon(String str) {
        String icon = getAndroidIcon(str);
        if (icon==null) {
            Pattern p = Pattern.compile("icon=\".*?\"");
            Matcher matcher = p.matcher(str);
            if (matcher.find()) {
                str = str.substring(matcher.start() + 6, matcher.end() - 1);
                return str;
            } else {
                return null;
            }
        } else {
            return icon;
        }
    }

    public static String getAndroidIcon(String str) {
        Pattern p = Pattern.compile("icon-android=\".*?\"");
        Matcher matcher = p.matcher(str);
        if (matcher.find()) {
            str = str.substring(matcher.start() + 14 ,matcher.end() - 1);
            return str;
        } else {
            return null;
        }
    }

    public static String getContent(String str) {
        Pattern p = Pattern.compile("content=\".*?\"");
        Matcher matcher = p.matcher(str);
        if (matcher.find()) {
            str = str.substring(matcher.start() + 9 ,matcher.end() - 1);
            return str;
        } else {
            return null;
        }
    }

    public static String getWithText(String str) {
        Pattern p = Pattern.compile("with-text=\".*?\"");
        Matcher matcher = p.matcher(str);
        if (matcher.find()) {
            str = str.substring(matcher.start() + 11 ,matcher.end() - 1);
            return str;
        } else {
            return "1";
        }
    }

    public static List<String> getTitleProperty(String str) {
        List<String> propertys = new ArrayList<>();
        Pattern p = Pattern.compile("<meta.*?>");
        Matcher matcher = p.matcher(str);
        while (matcher.find()) {
            propertys.add(str.substring(matcher.start() ,matcher.end()));
            if (matcher.end() + 1 < str.length()) {
                str = str.substring(matcher.end() + 1, str.length());
                matcher = p.matcher(str);
            }
        }
        return propertys;
    }

    public static String getProperty(String str) {
        Pattern p = Pattern.compile("property=\".*?\"");
        Matcher matcher = p.matcher(str);
        if (matcher.find()) {
            str = str.substring(matcher.start() + 10 ,matcher.end() - 1);
            return str;
        } else {
            return null;
        }
    }
}
