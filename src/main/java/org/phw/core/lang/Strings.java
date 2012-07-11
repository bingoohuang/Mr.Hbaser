package org.phw.core.lang;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.phw.core.exception.BaseException;

/**
 * 字符串操作的帮助函数.
 * 
 */
public abstract class Strings {
    /**
     * 数字匹配模式。
     * Explaination: 
     * [-+]?: Can have an optional - or + sign at the beginning. 
     *  [0-9]*: Can have any numbers of digits between 0 and 9 
     *  \\.? : the digits may have an optional decimal point. 
     * [0-9]+$: The string must have a digit at the end. 
     * If you want to consider x. as a valid number change 
     *     the expression as follows. (but I treat this as an invalid number.). 
     *   String expression = "[-+]?[0-9]*\\.?[0-9\\.]+$"; 
     */
    public static final Pattern NUMERIC_PATTERN = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+$");
    /**
     * 整数匹配模式.
     */
    public static final Pattern INTEGER_PATTERN = Pattern.compile("[-+]?([0-9]+)$");
    /**
     * The empty String <code>""</code>.
     */
    public static final String EMPTY = "";
    /**
     * Represents a failed index search.
     */
    public static final int INDEX_NOT_FOUND = -1;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)"
                    + "|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
    private static final String SEPERATE_COMMA = ",";

    /**
     * 复制字符串.
     * 
     * @param cs
     *            字符串
     * @param num
     *            数量
     * @return 新字符串
     */
    public static String dup(CharSequence cs, int num) {
        if (isEmpty(cs) || num <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(cs.length() * num);
        for (int i = 0; i < num; i++) {
            sb.append(cs);
        }
        return sb.toString();
    }

    /**
     * 复制字符.
     * 
     * @param c
     *            字符
     * @param num
     *            数量
     * @return 新字符串
     */
    public static String dup(char c, int num) {
        if (c == 0 || num < 1) {
            return "";
        }
        StringBuilder sb = new StringBuilder(num);
        for (int i = 0; i < num; i++) {
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * 将字符串首字母大写.
     * 
     * @param s
     *            字符串
     * @return 首字母大写后的新字符串
     */
    public static String capitalize(CharSequence s) {
        if (null == s) {
            return null;
        }
        int len = s.length();
        if (len == 0) {
            return "";
        }
        char char0 = s.charAt(0);
        if (Character.isUpperCase(char0)) {
            return s.toString();
        }
        return new StringBuilder(len).append(Character.toUpperCase(char0))
                                        .append(s.subSequence(1, len))
                                        .toString();
    }

    /**
     * 将字符串首字母小写.
     * 
     * @param s
     *            字符串
     * @return 首字母小写后的新字符串
     */
    public static String lowerFirst(CharSequence s) {
        if (null == s) {
            return null;
        }
        int len = s.length();
        if (len == 0) {
            return "";
        }
        char c = s.charAt(0);
        if (Character.isLowerCase(c)) {
            return s.toString();
        }
        return new StringBuilder(len).append(Character.toLowerCase(c))
                                        .append(s.subSequence(1, len))
                                        .toString();
    }

    /**
     * 检查两个字符串的忽略大小写后是否相等.
     * 
     * @param s1
     *            字符串A
     * @param s2
     *            字符串B
     * @return true 如果两个字符串忽略大小写后相等,且两个字符串均不为null
     */
    public static boolean equalsIgnoreCase(String s1, String s2) {
        return s1 == null ? s2 == null : s1.equalsIgnoreCase(s2);
    }

    /**
     * 检查两个字符串是否相等.
     * 
     * @param s1
     *            字符串A
     * @param s2
     *            字符串B
     * @return true 如果两个字符串相等,且两个字符串均不为null
     */
    public static boolean equals(String s1, String s2) {
        return s1 == null ? s2 == null : s1.equals(s2);
    }

    /**
     * 判断字符串是否以特殊字符开头.
     * 
     * @param s
     *            字符串
     * @param c
     *            特殊字符
     * @return 是否以特殊字符开头
     */
    public static boolean startsWithChar(String s, char c) {
        return null != s ? (s.length() == 0 ? false : s.charAt(0) == c) : false;
    }

    /**
     * 判断字符串是否以特殊字符结尾.
     * 
     * @param s
     *            字符串
     * @param c
     *            特殊字符
     * @return 是否以特殊字符结尾
     */
    public static boolean endsWithChar(String s, char c) {
        return null != s ? (s.length() == 0 ? false : s.charAt(s.length() - 1) == c) : false;
    }

    /**
     * 判断字符串是否空串.
     * @param cs
     *            字符串
     * @return 是不是为空字符串
     */
    public static boolean isEmpty(CharSequence cs) {
        return null == cs || cs.length() == 0;
    }

    /**
     *  判断字符串是否空白串.
     * @param cs
     *            字符串
     * @return 是不是为空白字符串
     */
    public static boolean isBlank(CharSequence cs) {
        if (null == cs) {
            return true;
        }
        int length = cs.length();
        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 去掉字符串前后空白.
     * 
     * @param cs    字符串
     * @return 新字符串
     */
    public static String trim(CharSequence cs) {
        if (null == cs) {
            return null;
        }

        if (cs instanceof String) {
            return ((String) cs).trim();
        }

        int length = cs.length();
        if (length == 0) {
            return cs.toString();
        }

        int l = 0;
        int last = length - 1;
        int r = last;
        for (; l < length; l++) {
            if (!Character.isWhitespace(cs.charAt(l))) {
                break;
            }
        }
        for (; r > l; r--) {
            if (!Character.isWhitespace(cs.charAt(r))) {
                break;
            }
        }
        if (l > r) {
            return "";
        }
        else if (l == 0 && r == last) {
            return cs.toString();
        }
        return cs.subSequence(l, r + 1).toString();
    }

    // Substring
    //-----------------------------------------------------------------------
    /**
     * <p>Gets a substring from the specified String avoiding exceptions.</p>
     *
     * <p>A negative start position can be used to start <code>n</code>
     * characters from the end of the String.</p>
     *
     * <p>A <code>null</code> String will return <code>null</code>.
     * An empty ("") String will return "".</p>
     *
     * <pre>
     * StringUtils.substring(null, *)   = null
     * StringUtils.substring("", *)     = ""
     * StringUtils.substring("abc", 0)  = "abc"
     * StringUtils.substring("abc", 2)  = "c"
     * StringUtils.substring("abc", 4)  = ""
     * StringUtils.substring("abc", -2) = "bc"
     * StringUtils.substring("abc", -4) = "abc"
     * </pre>
     *
     * @param str  the String to get the substring from, may be null
     * @param start  the position to start from, negative means
     *  count back from the end of the String by this many characters
     * @return substring from start position, <code>null</code> if null String input
     */
    public static String substring(String str, int start) {
        if (str == null) {
            return null;
        }

        // handle negatives, which means last n characters
        if (start < 0) {
            start = str.length() + start; // remember start is negative
        }

        if (start < 0) {
            start = 0;
        }
        if (start > str.length()) {
            return EMPTY;
        }

        return str.substring(start);
    }

    /**
     * <p>Gets a substring from the specified String avoiding exceptions.</p>
     *
     * <p>A negative start position can be used to start/end <code>n</code>
     * characters from the end of the String.</p>
     *
     * <p>The returned substring starts with the character in the <code>start</code>
     * position and ends before the <code>end</code> position. All position counting is
     * zero-based -- i.e., to start at the beginning of the string use
     * <code>start = 0</code>. Negative start and end positions can be used to
     * specify offsets relative to the end of the String.</p>
     *
     * <p>If <code>start</code> is not strictly to the left of <code>end</code>, ""
     * is returned.</p>
     *
     * <pre>
     * StringUtils.substring(null, *, *)    = null
     * StringUtils.substring("", * ,  *)    = "";
     * StringUtils.substring("abc", 0, 2)   = "ab"
     * StringUtils.substring("abc", 2, 0)   = ""
     * StringUtils.substring("abc", 2, 4)   = "c"
     * StringUtils.substring("abc", 4, 6)   = ""
     * StringUtils.substring("abc", 2, 2)   = ""
     * StringUtils.substring("abc", -2, -1) = "b"
     * StringUtils.substring("abc", -4, 2)  = "ab"
     * </pre>
     *
     * @param str  the String to get the substring from, may be null
     * @param start  the position to start from, negative means
     *  count back from the end of the String by this many characters
     * @param end  the position to end at (exclusive), negative means
     *  count back from the end of the String by this many characters
     * @return substring from start position to end positon,
     *  <code>null</code> if null String input
     */
    public static String substring(String str, int start, int end) {
        if (str == null) {
            return null;
        }

        // handle negatives
        if (end < 0) {
            end = str.length() + end; // remember end is negative
        }
        if (start < 0) {
            start = str.length() + start; // remember start is negative
        }

        // check length next
        if (end > str.length()) {
            end = str.length();
        }

        // if start is greater than end, return ""
        if (start > end) {
            return EMPTY;
        }

        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }

        return str.substring(start, end);
    }

    // join
    /**
     * 将String集合合并为String。
     * @param list 列表
     * @param seperate 合并String之间的分隔符
     * @return 字符串
     */
    public static String join(Collection<String> list, String seperate) {
        return join(list, seperate, null, null);
    }

    /**
     * 将String集合合并为String。
     * @param list 列表
     * @param seperate 合并String之间的分隔符
     * @param brace 合并的前后缀
     * @return 字符串
     */
    public static String join(Collection<String> list, String seperate, String brace) {
        return join(list, seperate, brace, brace);
    }

    /**
     * 将String集合合并为String。
     * @param list 列表
     * @param seperate 集合元素之间合并的分隔符
     * @param prefix 合并后的前缀
     * @param postfix 合并后的后缀
     * @return 字符串
     */
    public static String join(Collection<String> list, String seperate, String prefix, String postfix) {
        if (list == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(256);

        for (String str : list) {
            if (builder.length() > 0) {
                builder.append(seperate);
            }

            if (prefix != null) {
                builder.append(prefix);
            }
            builder.append(str);
            if (postfix != null) {
                builder.append(postfix);
            }
        }

        return builder.toString();
    }

    /**
     * 将String集合合并为String。
     * @param list 列表
     * @param seperate 合并String之间的分隔符
     * @return 字符串
     */
    public static String join(String[] list, String seperate) {
        return join(list, seperate, null, null);
    }

    /**
     * 将String集合合并为String。
     * @param list 列表
     * @param seperate 合并String之间的分隔符
     * @param brace 合并的前后缀
     * @return 字符串
     */
    public static String join(String[] list, String seperate, String brace) {
        return join(list, seperate, brace, brace);
    }

    /**
     * 将String集合合并为String。
     * @param list 列表
     * @param seperate 集合元素之间合并的分隔符
     * @param prefix 合并后的前缀
     * @param postfix 合并后的后缀
     * @return 字符串
     */
    public static String join(String[] list, String seperate, String prefix, String postfix) {
        if (list == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(256);

        for (String str : list) {
            if (builder.length() > 0) {
                builder.append(seperate);
            }

            if (prefix != null) {
                builder.append(prefix);
            }
            builder.append(str);
            if (postfix != null) {
                builder.append(postfix);
            }
        }

        return builder.toString();
    }

    // split
    /**
     * 分割字符串为String数组。
     * @param str 字符串
     * @return 字符串数组
     */
    public static String[] split(String str) {
        return split(str, SEPERATE_COMMA, true);
    }

    /**
     * 分割字符串为String数组。
     * @param str 字符串
     * @param seperate 分割符
     * @param bypassEmpty 是否忽略空的分割段
     * @return 字符串数组
     */
    public static String[] split(String str, String seperate, boolean bypassEmpty) {
        ArrayList<String> arr = new ArrayList<String>();
        int startPos = 0;
        int indexOf = indexOf(str, seperate, startPos);

        while (indexOf >= 0) {
            String subStr = substring(str, startPos, indexOf);
            if (!bypassEmpty || !isBlank(subStr)) {
                arr.add(subStr.trim());
            }

            startPos = indexOf + 1;
            indexOf = indexOf(str, seperate, startPos);
        }

        if (startPos <= (str == null ? -1 : str.length())) {
            String subStr = substring(str, startPos);
            if (!bypassEmpty || !isBlank(subStr)) {
                arr.add(subStr.trim());
            }
        }

        return arr.toArray(new String[arr.size()]);
    }

    /**
     * 重复str字符串count次数，以seperate分隔。
     * @param str 字符串
     * @param seperate 分隔符
     * @param count 次数
     * @return 字符串
     */
    public static String repeat(String str, String seperate, int count) {
        StringBuilder sb = new StringBuilder();
        if (count > 0) {
            sb.append(str);
        }

        for (int i = 1; i < count; ++i) {
            sb.append(seperate).append(str);
        }

        return sb.toString();
    }

    /**
     * 将字符串按半角逗号，拆分成数组，空元素将被忽略.
     * 
     * @param s
     *            字符串
     * @return 字符串数组
     */
    public static String[] splitIgnoreBlank(String s) {
        return Strings.splitIgnoreBlank(s, SEPERATE_COMMA);
    }

    /**
     * 根据一个正则式，将字符串拆分成数组，空元素将被忽略.
     * 
     * @param s
     *            字符串
     * @param regex
     *            正则式
     * @return 字符串数组
     */
    public static String[] splitIgnoreBlank(String s, String regex) {
        if (null == s) {
            return null;
        }
        String[] ss = s.split(regex);
        List<String> list = new LinkedList<String>();
        for (String st : ss) {
            if (isBlank(st)) {
                continue;
            }
            list.add(trim(st));
        }
        return list.toArray(new String[list.size()]);
    }

    /**
     * 将一个整数转换成最小长度为某一固定数值的十进制形式字符串.
     * 
     * @param d
     *            整数
     * @param width
     *            宽度
     * @return 新字符串
     */
    public static String fillDigit(int d, int width) {
        return Strings.alignRight(String.valueOf(d), width, '0');
    }

    /**
     * 将一个整数转换成最小长度为某一固定数值的十六进制形式字符串.
     * 
     * @param d
     *            整数
     * @param width
     *            宽度
     * @return 新字符串
     */
    public static String fillHex(int d, int width) {
        return Strings.alignRight(Integer.toHexString(d), width, '0');
    }

    /**
     * 将一个整数转换成最小长度为某一固定数值的二进制形式字符串.
     * 
     * @param d
     *            整数
     * @param width
     *            宽度
     * @return 新字符串
     */
    public static String fillBinary(int d, int width) {
        return Strings.alignRight(Integer.toBinaryString(d), width, '0');
    }

    /**
     * 将一个整数转换成固定长度的十进制形式字符串.
     * 
     * @param d
     *            整数
     * @param width
     *            宽度
     * @return 新字符串
     */
    public static String toDigit(int d, int width) {
        return Strings.cutRight(String.valueOf(d), width, '0');
    }

    /**
     * 将一个整数转换成固定长度的十六进制形式字符串.
     * 
     * @param d
     *            整数
     * @param width
     *            宽度
     * @return 新字符串
     */
    public static String toHex(int d, int width) {
        return Strings.cutRight(Integer.toHexString(d), width, '0');
    }

    /**
     * 将一个整数转换成固定长度的二进制形式字符串.
     * 
     * @param d
     *            整数
     * @param width
     *            宽度
     * @return 新字符串
     */
    public static String toBinary(int d, int width) {
        return Strings.cutRight(Integer.toBinaryString(d), width, '0');
    }

    /**
     * 保证字符串为一固定长度。超过长度，切除，否则补字符。
     * 
     * @param s
     *            字符串
     * @param width
     *            长度
     * @param c
     *            补字符
     * @return 修饰后的字符串
     */
    public static String cutRight(String s, int width, char c) {
        if (null == s) {
            return null;
        }
        int len = s.length();
        if (len == width) {
            return s;
        }
        if (len < width) {
            return Strings.dup(c, width - len) + s;
        }
        return s.substring(len - width, len);
    }

    /**
     * 在字符串左侧填充一定数量的特殊字符.
     * 
     * @param cs
     *            字符串
     * @param width
     *            字符数量
     * @param c
     *            字符
     * @return 新字符串
     */
    public static String alignRight(CharSequence cs, int width, char c) {
        if (null == cs) {
            return null;
        }
        int len = cs.length();
        if (len >= width) {
            return cs.toString();
        }
        return new StringBuilder().append(dup(c, width - len)).append(cs).toString();
    }

    /**
     * 在字符串左侧侧填充一定数量的特殊字符.
     * 
     * @param cs
     *            字符串
     * @param width
     *            字符数量
     * @param c
     *            字符
     * @return 新字符串
     */
    public static String alignLeft(CharSequence cs, int width, char c) {
        if (null == cs) {
            return null;
        }
        int length = cs.length();
        if (length >= width) {
            return cs.toString();
        }
        return new StringBuilder().append(cs).append(dup(c, width - length)).toString();
    }

    /**
     * 字符串是被左字符和右字符包裹 -- 忽略空白.
     * 
     * @param cs
     *            字符串
     * @param lc
     *            左字符
     * @param rc
     *            右字符
     * @return 字符串是被左字符和右字符包裹 -- 忽略空白
     */
    public static boolean isQuoteByIgnoreBlank(CharSequence cs, char lc, char rc) {
        if (null == cs) {
            return false;
        }
        int len = cs.length();
        if (len < 2) {
            return false;
        }
        int l = 0;
        int last = len - 1;
        int r = last;
        for (; l < len; l++) {
            if (!Character.isWhitespace(cs.charAt(l))) {
                break;
            }
        }
        if (cs.charAt(l) != lc) {
            return false;
        }
        for (; r > l; r--) {
            if (!Character.isWhitespace(cs.charAt(r))) {
                break;
            }
        }
        return l < r && cs.charAt(r) == rc;
    }

    /**
     * 字符串是被左字符和右字符包裹.
     * @param cs
     *            字符串
     * @param lc
     *            左字符
     * @param rc
     *            右字符
     * @return 字符串是被左字符和右字符包裹
     */
    public static boolean isQuoteBy(CharSequence cs, char lc, char rc) {
        if (null == cs) {
            return false;
        }

        int length = cs.length();
        return length > 1 && cs.charAt(0) == lc && cs.charAt(length - 1) == rc;
    }

    /**
     * 获得一个字符串集合中，最长串的长度.
     * 
     * @param coll
     *            字符串集合
     * @return 最大长度
     */
    public static int maxLength(Collection<? extends CharSequence> coll) {
        int re = 0;
        if (null != coll) {
            for (CharSequence s : coll) {
                if (null != s) {
                    re = Math.max(re, s.length());
                }
            }
        }
        return re;
    }

    /**
     * 获得一个字符串数组中，最长串的长度.
     * @param <T> 字符串
     * @param array
     *            字符串数组
     * @return 最大长度
     */
    public static <T extends CharSequence> int maxLength(T[] array) {
        int re = 0;
        if (null != array) {
            for (CharSequence s : array) {
                if (null != s) {
                    re = Math.max(re, s.length());
                }
            }
        }
        return re;
    }

    /**
     * 对obj进行toString()操作,如果为null返回"".
     * 
     * @param obj Object
     * @return obj.toString()
     */
    public static String sNull(Object obj) {
        return sNull(obj, "");
    }

    /**
     * 对obj进行toString()操作,如果为null返回def中定义的值.
     * 
     * @param obj Object
     * @param def 
     *            如果obj==null返回的内容
     * @return obj的toString()操作
     */
    public static String sNull(Object obj, String def) {
        return obj != null ? obj.toString() : def;
    }

    /**
     * 对obj进行toString()操作,如果为空串返回"".
     * 
     * @param obj Object
     * @return obj.toString()
     */
    public static String sBlank(Object obj) {
        return sBlank(obj, "");
    }

    /**
     * 对obj进行toString()操作,如果为空串返回def中定义的值.
     * 
     * @param obj Object
     * @param def
     *            如果obj==null返回的内容
     * @return obj的toString()操作
     */
    public static String sBlank(Object obj, String def) {
        if (null == obj) {
            return def;
        }
        String s = obj.toString();
        return Strings.isBlank(s) ? def : s;
    }

    /**
     * 截去第一个字符.
     * <p>
     * 比如:
     * <ul>
     * <li>removeFirst("12345") => 2345
     * <li>removeFirst("A") => ""
     * </ul>
     * 
     * @param str
     *            字符串
     * @return 新字符串
     */
    public static String removeFirst(CharSequence str) {
        if (str == null) {
            return null;
        }
        if (str.length() > 1) {
            return str.subSequence(1, str.length()).toString();
        }
        return "";
    }

    /**
     * 如果str中第一个字符和 c一致,则删除,否则返回 str.
     * <p>
     * 比如:
     * <ul>
     * <li>removeFirst("12345",1) => "2345"
     * <li>removeFirst("ABC",'B') => "ABC"
     * <li>removeFirst("A",'B') => "A"
     * <li>removeFirst("A",'A') => ""
     * </ul>
     * 
     * @param str
     *            字符串
     * @param c
     *            第一个个要被截取的字符
     * @return 新字符串
     */
    public static String removeFirst(String str, char c) {
        return Strings.isEmpty(str) || c != str.charAt(0) ? str : str.substring(1);
    }

    /**
     * 判断一个字符串数组是否包括某一字符串.
     * 
     * @param ss
     *            字符串数组
     * @param s
     *            字符串
     * @return 是否包含
     */
    public static boolean isin(String[] ss, String s) {
        if (null == ss || ss.length == 0 || Strings.isBlank(s)) {
            return false;
        }
        for (String w : ss) {
            if (s.equals(w)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查一个字符串是否为合法的电子邮件地址.
     * 
     * @param input
     *            需要检查的字符串
     * @return true 如果是有效的邮箱地址
     */
    public static synchronized boolean isEmail(CharSequence input) {
        return EMAIL_PATTERN.matcher(input).matches();
    }

    /**
     * 将一个字符串某一个字符后面的字母变成大写，比如.
     * 
     * <pre>
     *  upperWord("hello-world", '-') => "helloWorld"
     * </pre>
     * 
     * @param s
     *            字符串
     * @param c
     *            字符
     * 
     * @return 转换后字符串
     */
    public static String upperWord(CharSequence s, char c) {
        StringBuilder sb = new StringBuilder();
        int len = s.length();
        for (int i = 0; i < len; i++) {
            char ch = s.charAt(i);
            if (ch == c) {
                do {
                    i++;
                    if (i >= len) {
                        return sb.toString();
                    }
                    ch = s.charAt(i);
                }
                while (ch == c);
                sb.append(Character.toUpperCase(ch));
            }
            else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    // SubStringAfter/SubStringBefore
    //-----------------------------------------------------------------------
    /**
     * <p>Gets the substring before the first occurrence of a separator.
     * The separator is not returned.</p>
     *
     * <p>A <code>null</code> string input will return <code>null</code>.
     * An empty ("") string input will return the empty string.
     * A <code>null</code> separator will return the input string.</p>
     *
     * <pre>
     * StringUtils.substringBefore(null, *)      = null
     * StringUtils.substringBefore("", *)        = ""
     * StringUtils.substringBefore("abc", "a")   = ""
     * StringUtils.substringBefore("abcba", "b") = "a"
     * StringUtils.substringBefore("abc", "c")   = "ab"
     * StringUtils.substringBefore("abc", "d")   = "abc"
     * StringUtils.substringBefore("abc", "")    = ""
     * StringUtils.substringBefore("abc", null)  = "abc"
     * </pre>
     *
     * @param str  the String to get a substring from, may be null
     * @param separator  the String to search for, may be null
     * @return the substring before the first occurrence of the separator,
     *  <code>null</code> if null String input
     * @since 2.0
     */
    public static String substringBefore(String str, String separator) {
        if (isEmpty(str) || separator == null) {
            return str;
        }
        if (separator.length() == 0) {
            return EMPTY;
        }
        int pos = str.indexOf(separator);
        if (pos == -1) {
            return str;
        }
        return str.substring(0, pos);
    }

    /**
     * <p>Gets the substring after the first occurrence of a separator.
     * The separator is not returned.</p>
     *
     * <p>A <code>null</code> string input will return <code>null</code>.
     * An empty ("") string input will return the empty string.
     * A <code>null</code> separator will return the empty string if the
     * input string is not <code>null</code>.</p>
     *
     * <pre>
     * StringUtils.substringAfter(null, *)      = null
     * StringUtils.substringAfter("", *)        = ""
     * StringUtils.substringAfter(*, null)      = ""
     * StringUtils.substringAfter("abc", "a")   = "bc"
     * StringUtils.substringAfter("abcba", "b") = "cba"
     * StringUtils.substringAfter("abc", "c")   = ""
     * StringUtils.substringAfter("abc", "d")   = ""
     * StringUtils.substringAfter("abc", "")    = "abc"
     * </pre>
     *
     * @param str  the String to get a substring from, may be null
     * @param separator  the String to search for, may be null
     * @return the substring after the first occurrence of the separator,
     *  <code>null</code> if null String input
     * @since 2.0
     */
    public static String substringAfter(String str, String separator) {
        if (isEmpty(str)) {
            return str;
        }
        if (separator == null) {
            return EMPTY;
        }
        int pos = str.indexOf(separator);
        if (pos == -1) {
            return EMPTY;
        }

        return str.substring(pos + separator.length());
    }

    // startsWith
    //-----------------------------------------------------------------------

    /**
     * <p>Check if a String starts with a specified prefix.</p>
     *
     * <p><code>null</code>s are handled without exceptions. Two <code>null</code>
     * references are considered to be equal. The comparison is case sensitive.</p>
     *
     * <pre>
     * StringUtils.startsWith(null, null)      = true
     * StringUtils.startsWith(null, "abc")     = false
     * StringUtils.startsWith("abcdef", null)  = false
     * StringUtils.startsWith("abcdef", "abc") = true
     * StringUtils.startsWith("ABCDEF", "abc") = false
     * </pre>
     *
     * @see java.lang.String#startsWith(String)
     * @param str  the String to check, may be null
     * @param prefix the prefix to find, may be null
     * @return <code>true</code> if the String starts with the prefix, case sensitive, or
     *  both <code>null</code>
     * @since 2.4
     */
    public static boolean startsWith(String str, String prefix) {
        return startsWith(str, prefix, false);
    }

    /**
     * <p>Case insensitive check if a String starts with a specified prefix.</p>
     *
     * <p><code>null</code>s are handled without exceptions. Two <code>null</code>
     * references are considered to be equal. The comparison is case insensitive.</p>
     *
     * <pre>
     * StringUtils.startsWithIgnoreCase(null, null)      = true
     * StringUtils.startsWithIgnoreCase(null, "abc")     = false
     * StringUtils.startsWithIgnoreCase("abcdef", null)  = false
     * StringUtils.startsWithIgnoreCase("abcdef", "abc") = true
     * StringUtils.startsWithIgnoreCase("ABCDEF", "abc") = true
     * </pre>
     *
     * @see java.lang.String#startsWith(String)
     * @param str  the String to check, may be null
     * @param prefix the prefix to find, may be null
     * @return <code>true</code> if the String starts with the prefix, case insensitive, or
     *  both <code>null</code>
     * @since 2.4
     */
    public static boolean startsWithIgnoreCase(String str, String prefix) {
        return startsWith(str, prefix, true);
    }

    /**
     * <p>Check if a String starts with a specified prefix (optionally case insensitive).</p>
     *
     * @see java.lang.String#startsWith(String)
     * @param str  the String to check, may be null
     * @param prefix the prefix to find, may be null
     * @param ignoreCase inidicates whether the compare should ignore case
     *  (case insensitive) or not.
     * @return <code>true</code> if the String starts with the prefix or
     *  both <code>null</code>
     */
    private static boolean startsWith(String str, String prefix, boolean ignoreCase) {
        if (str == null || prefix == null) {
            return str == null && prefix == null;
        }
        if (prefix.length() > str.length()) {
            return false;
        }
        return str.regionMatches(ignoreCase, 0, prefix, 0, prefix.length());
    }

    /**
     * <p>Check if a String starts with any of an array of specified strings.</p>
     * 
     * <pre>
     * StringUtils.startsWithAny(null, null)      = false
     * StringUtils.startsWithAny(null, new String[] {"abc"})  = false
     * StringUtils.startsWithAny("abcxyz", null)     = false
     * StringUtils.startsWithAny("abcxyz", new String[] {""}) = false
     * StringUtils.startsWithAny("abcxyz", new String[] {"abc"}) = true
     * StringUtils.startsWithAny("abcxyz", new String[] {null, "xyz", "abc"}) = true
     * </pre>
     *
     * @see #startsWith(String, String)
     * @param string  the String to check, may be null
     * @param searchStrings the Strings to find, may be null or empty
     * @return <code>true</code> if the String starts with any of the the prefixes, case insensitive, or
     *  both <code>null</code>
     * @since 2.5
     */
    public static boolean startsWithAny(String string, String[] searchStrings) {
        if (isEmpty(string) || Arrs.isEmpty(searchStrings)) {
            return false;
        }
        for (String searchString : searchStrings) {
            if (startsWith(string, searchString)) {
                return true;
            }
        }
        return false;
    }

    // endsWith
    //-----------------------------------------------------------------------

    /**
     * <p>Check if a String ends with a specified suffix.</p>
     *
     * <p><code>null</code>s are handled without exceptions. Two <code>null</code>
     * references are considered to be equal. The comparison is case sensitive.</p>
     *
     * <pre>
     * StringUtils.endsWith(null, null)      = true
     * StringUtils.endsWith(null, "def")     = false
     * StringUtils.endsWith("abcdef", null)  = false
     * StringUtils.endsWith("abcdef", "def") = true
     * StringUtils.endsWith("ABCDEF", "def") = false
     * StringUtils.endsWith("ABCDEF", "cde") = false
     * </pre>
     *
     * @see java.lang.String#endsWith(String)
     * @param str  the String to check, may be null
     * @param suffix the suffix to find, may be null
     * @return <code>true</code> if the String ends with the suffix, case sensitive, or
     *  both <code>null</code>
     * @since 2.4
     */
    public static boolean endsWith(String str, String suffix) {
        return endsWith(str, suffix, false);
    }

    /**
     * <p>Case insensitive check if a String ends with a specified suffix.</p>
     *
     * <p><code>null</code>s are handled without exceptions. Two <code>null</code>
     * references are considered to be equal. The comparison is case insensitive.</p>
     *
     * <pre>
     * StringUtils.endsWithIgnoreCase(null, null)      = true
     * StringUtils.endsWithIgnoreCase(null, "def")     = false
     * StringUtils.endsWithIgnoreCase("abcdef", null)  = false
     * StringUtils.endsWithIgnoreCase("abcdef", "def") = true
     * StringUtils.endsWithIgnoreCase("ABCDEF", "def") = true
     * StringUtils.endsWithIgnoreCase("ABCDEF", "cde") = false
     * </pre>
     *
     * @see java.lang.String#endsWith(String)
     * @param str  the String to check, may be null
     * @param suffix the suffix to find, may be null
     * @return <code>true</code> if the String ends with the suffix, case insensitive, or
     *  both <code>null</code>
     * @since 2.4
     */
    public static boolean endsWithIgnoreCase(String str, String suffix) {
        return endsWith(str, suffix, true);
    }

    /**
     * <p>Check if a String ends with a specified suffix (optionally case insensitive).</p>
     *
     * @see java.lang.String#endsWith(String)
     * @param str  the String to check, may be null
     * @param suffix the suffix to find, may be null
     * @param ignoreCase inidicates whether the compare should ignore case
     *  (case insensitive) or not.
     * @return <code>true</code> if the String starts with the prefix or
     *  both <code>null</code>
     */
    private static boolean endsWith(String str, String suffix, boolean ignoreCase) {
        if (str == null || suffix == null) {
            return str == null && suffix == null;
        }
        if (suffix.length() > str.length()) {
            return false;
        }
        int strOffset = str.length() - suffix.length();
        return str.regionMatches(ignoreCase, strOffset, suffix, 0, suffix.length());
    }

    // IndexOf
    //-----------------------------------------------------------------------
    /**
     * <p>Finds the first index within a String, handling <code>null</code>.
     * This method uses {@link String#indexOf(int)}.</p>
     *
     * <p>A <code>null</code> or empty ("") String will return <code>-1</code>.</p>
     *
     * <pre>
     * StringUtils.indexOf(null, *)         = -1
     * StringUtils.indexOf("", *)           = -1
     * StringUtils.indexOf("aabaabaa", 'a') = 0
     * StringUtils.indexOf("aabaabaa", 'b') = 2
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchChar  the character to find
     * @return the first index of the search character,
     *  -1 if no match or <code>null</code> string input
     * @since 2.0
     */
    public static int indexOf(String str, char searchChar) {
        if (isEmpty(str)) {
            return -1;
        }
        return str.indexOf(searchChar);
    }

    /**
     * <p>Finds the first index within a String from a start position,
     * handling <code>null</code>.
     * This method uses {@link String#indexOf(int, int)}.</p>
     *
     * <p>A <code>null</code> or empty ("") String will return <code>-1</code>.
     * A negative start position is treated as zero.
     * A start position greater than the string length returns <code>-1</code>.</p>
     *
     * <pre>
     * StringUtils.indexOf(null, *, *)          = -1
     * StringUtils.indexOf("", *, *)            = -1
     * StringUtils.indexOf("aabaabaa", 'b', 0)  = 2
     * StringUtils.indexOf("aabaabaa", 'b', 3)  = 5
     * StringUtils.indexOf("aabaabaa", 'b', 9)  = -1
     * StringUtils.indexOf("aabaabaa", 'b', -1) = 2
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchChar  the character to find
     * @param startPos  the start position, negative treated as zero
     * @return the first index of the search character,
     *  -1 if no match or <code>null</code> string input
     * @since 2.0
     */
    public static int indexOf(String str, char searchChar, int startPos) {
        if (isEmpty(str)) {
            return -1;
        }
        return str.indexOf(searchChar, startPos);
    }

    /**
     * <p>Finds the first index within a String, handling <code>null</code>.
     * This method uses {@link String#indexOf(String)}.</p>
     *
     * <p>A <code>null</code> String will return <code>-1</code>.</p>
     *
     * <pre>
     * StringUtils.indexOf(null, *)          = -1
     * StringUtils.indexOf(*, null)          = -1
     * StringUtils.indexOf("", "")           = 0
     * StringUtils.indexOf("aabaabaa", "a")  = 0
     * StringUtils.indexOf("aabaabaa", "b")  = 2
     * StringUtils.indexOf("aabaabaa", "ab") = 1
     * StringUtils.indexOf("aabaabaa", "")   = 0
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchStr  the String to find, may be null
     * @return the first index of the search String,
     *  -1 if no match or <code>null</code> string input
     * @since 2.0
     */
    public static int indexOf(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return -1;
        }
        return str.indexOf(searchStr);
    }

    /**
     * <p>Finds the n-th index within a String, handling <code>null</code>.
     * This method uses {@link String#indexOf(String)}.</p>
     *
     * <p>A <code>null</code> String will return <code>-1</code>.</p>
     *
     * <pre>
     * StringUtils.ordinalIndexOf(null, *, *)          = -1
     * StringUtils.ordinalIndexOf(*, null, *)          = -1
     * StringUtils.ordinalIndexOf("", "", *)           = 0
     * StringUtils.ordinalIndexOf("aabaabaa", "a", 1)  = 0
     * StringUtils.ordinalIndexOf("aabaabaa", "a", 2)  = 1
     * StringUtils.ordinalIndexOf("aabaabaa", "b", 1)  = 2
     * StringUtils.ordinalIndexOf("aabaabaa", "b", 2)  = 5
     * StringUtils.ordinalIndexOf("aabaabaa", "ab", 1) = 1
     * StringUtils.ordinalIndexOf("aabaabaa", "ab", 2) = 4
     * StringUtils.ordinalIndexOf("aabaabaa", "", 1)   = 0
     * StringUtils.ordinalIndexOf("aabaabaa", "", 2)   = 0
     * </pre>
     *
     * <p>Note that 'head(String str, int n)' may be implemented as: </p>
     *
     * <pre>
     *   str.substring(0, lastOrdinalIndexOf(str, "\n", n))
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchStr  the String to find, may be null
     * @param ordinal  the n-th <code>searchStr</code> to find
     * @return the n-th index of the search String,
     *  <code>-1</code> (<code>INDEX_NOT_FOUND</code>) if no match or <code>null</code> string input
     * @since 2.1
     */
    public static int ordinalIndexOf(String str, String searchStr, int ordinal) {
        return ordinalIndexOf(str, searchStr, ordinal, false);
    }

    /**
     * <p>Finds the n-th index within a String, handling <code>null</code>.
     * This method uses {@link String#indexOf(String)}.</p>
     *
     * <p>A <code>null</code> String will return <code>-1</code>.</p>
     *
     * @param str  the String to check, may be null
     * @param searchStr  the String to find, may be null
     * @param ordinal  the n-th <code>searchStr</code> to find
     * @param lastIndex true if lastOrdinalIndexOf() otherwise false if ordinalIndexOf()
     * @return the n-th index of the search String,
     *  <code>-1</code> (<code>INDEX_NOT_FOUND</code>) if no match or <code>null</code> string input
     */
    // Shared code between ordinalIndexOf(String,String,int) and lastOrdinalIndexOf(String,String,int)
    private static int ordinalIndexOf(String str, String searchStr, int ordinal, boolean lastIndex) {
        if (str == null || searchStr == null || ordinal <= 0) {
            return INDEX_NOT_FOUND;
        }
        if (searchStr.length() == 0) {
            return lastIndex ? str.length() : 0;
        }
        int found = 0;
        int index = lastIndex ? str.length() : INDEX_NOT_FOUND;
        do {
            if (lastIndex) {
                index = str.lastIndexOf(searchStr, index - 1);
            }
            else {
                index = str.indexOf(searchStr, index + 1);
            }
            if (index < 0) {
                return index;
            }
            found++;
        }
        while (found < ordinal);
        return index;
    }

    /**
     * <p>Finds the first index within a String, handling <code>null</code>.
     * This method uses {@link String#indexOf(String, int)}.</p>
     *
     * <p>A <code>null</code> String will return <code>-1</code>.
     * A negative start position is treated as zero.
     * An empty ("") search String always matches.
     * A start position greater than the string length only matches
     * an empty search String.</p>
     *
     * <pre>
     * StringUtils.indexOf(null, *, *)          = -1
     * StringUtils.indexOf(*, null, *)          = -1
     * StringUtils.indexOf("", "", 0)           = 0
     * StringUtils.indexOf("aabaabaa", "a", 0)  = 0
     * StringUtils.indexOf("aabaabaa", "b", 0)  = 2
     * StringUtils.indexOf("aabaabaa", "ab", 0) = 1
     * StringUtils.indexOf("aabaabaa", "b", 3)  = 5
     * StringUtils.indexOf("aabaabaa", "b", 9)  = -1
     * StringUtils.indexOf("aabaabaa", "b", -1) = 2
     * StringUtils.indexOf("aabaabaa", "", 2)   = 2
     * StringUtils.indexOf("abc", "", 9)        = 3
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchStr  the String to find, may be null
     * @param startPos  the start position, negative treated as zero
     * @return the first index of the search String,
     *  -1 if no match or <code>null</code> string input
     * @since 2.0
     */
    public static int indexOf(String str, String searchStr, int startPos) {
        if (str == null || searchStr == null) {
            return -1;
        }
        // JDK1.2/JDK1.3 have a bug, when startPos > str.length for "", hence
        if (searchStr.length() == 0 && startPos >= str.length()) {
            return str.length();
        }
        return str.indexOf(searchStr, startPos);
    }

    /**
     * <p>Case in-sensitive find of the first index within a String.</p>
     *
     * <p>A <code>null</code> String will return <code>-1</code>.
     * A negative start position is treated as zero.
     * An empty ("") search String always matches.
     * A start position greater than the string length only matches
     * an empty search String.</p>
     *
     * <pre>
     * StringUtils.indexOfIgnoreCase(null, *)          = -1
     * StringUtils.indexOfIgnoreCase(*, null)          = -1
     * StringUtils.indexOfIgnoreCase("", "")           = 0
     * StringUtils.indexOfIgnoreCase("aabaabaa", "a")  = 0
     * StringUtils.indexOfIgnoreCase("aabaabaa", "b")  = 2
     * StringUtils.indexOfIgnoreCase("aabaabaa", "ab") = 1
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchStr  the String to find, may be null
     * @return the first index of the search String,
     *  -1 if no match or <code>null</code> string input
     * @since 2.5
     */
    public static int indexOfIgnoreCase(String str, String searchStr) {
        return indexOfIgnoreCase(str, searchStr, 0);
    }

    /**
     * <p>Case in-sensitive find of the first index within a String
     * from the specified position.</p>
     *
     * <p>A <code>null</code> String will return <code>-1</code>.
     * A negative start position is treated as zero.
     * An empty ("") search String always matches.
     * A start position greater than the string length only matches
     * an empty search String.</p>
     *
     * <pre>
     * StringUtils.indexOfIgnoreCase(null, *, *)          = -1
     * StringUtils.indexOfIgnoreCase(*, null, *)          = -1
     * StringUtils.indexOfIgnoreCase("", "", 0)           = 0
     * StringUtils.indexOfIgnoreCase("aabaabaa", "A", 0)  = 0
     * StringUtils.indexOfIgnoreCase("aabaabaa", "B", 0)  = 2
     * StringUtils.indexOfIgnoreCase("aabaabaa", "AB", 0) = 1
     * StringUtils.indexOfIgnoreCase("aabaabaa", "B", 3)  = 5
     * StringUtils.indexOfIgnoreCase("aabaabaa", "B", 9)  = -1
     * StringUtils.indexOfIgnoreCase("aabaabaa", "B", -1) = 2
     * StringUtils.indexOfIgnoreCase("aabaabaa", "", 2)   = 2
     * StringUtils.indexOfIgnoreCase("abc", "", 9)        = 3
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchStr  the String to find, may be null
     * @param startPos  the start position, negative treated as zero
     * @return the first index of the search String,
     *  -1 if no match or <code>null</code> string input
     * @since 2.5
     */
    public static int indexOfIgnoreCase(String str, String searchStr, int startPos) {
        if (str == null || searchStr == null) {
            return -1;
        }
        if (startPos < 0) {
            startPos = 0;
        }
        int endLimit = str.length() - searchStr.length() + 1;
        if (startPos > endLimit) {
            return -1;
        }
        if (searchStr.length() == 0) {
            return startPos;
        }
        for (int i = startPos; i < endLimit; i++) {
            if (str.regionMatches(true, i, searchStr, 0, searchStr.length())) {
                return i;
            }
        }
        return -1;
    }

    // LastIndexOf
    //-----------------------------------------------------------------------
    /**
     * <p>Finds the last index within a String, handling <code>null</code>.
     * This method uses {@link String#lastIndexOf(int)}.</p>
     *
     * <p>A <code>null</code> or empty ("") String will return <code>-1</code>.</p>
     *
     * <pre>
     * StringUtils.lastIndexOf(null, *)         = -1
     * StringUtils.lastIndexOf("", *)           = -1
     * StringUtils.lastIndexOf("aabaabaa", 'a') = 7
     * StringUtils.lastIndexOf("aabaabaa", 'b') = 5
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchChar  the character to find
     * @return the last index of the search character,
     *  -1 if no match or <code>null</code> string input
     * @since 2.0
     */
    public static int lastIndexOf(String str, char searchChar) {
        if (isEmpty(str)) {
            return -1;
        }
        return str.lastIndexOf(searchChar);
    }

    /**
     * <p>Finds the last index within a String from a start position,
    * handling <code>null</code>.
    * This method uses {@link String#lastIndexOf(int, int)}.</p>
    *
    * <p>A <code>null</code> or empty ("") String will return <code>-1</code>.
    * A negative start position returns <code>-1</code>.
    * A start position greater than the string length searches the whole string.</p>
    *
    * <pre>
    * StringUtils.lastIndexOf(null, *, *)          = -1
    * StringUtils.lastIndexOf("", *,  *)           = -1
    * StringUtils.lastIndexOf("aabaabaa", 'b', 8)  = 5
    * StringUtils.lastIndexOf("aabaabaa", 'b', 4)  = 2
    * StringUtils.lastIndexOf("aabaabaa", 'b', 0)  = -1
    * StringUtils.lastIndexOf("aabaabaa", 'b', 9)  = 5
    * StringUtils.lastIndexOf("aabaabaa", 'b', -1) = -1
    * StringUtils.lastIndexOf("aabaabaa", 'a', 0)  = 0
    * </pre>
    *
    * @param str  the String to check, may be null
    * @param searchChar  the character to find
    * @param startPos  the start position
    * @return the last index of the search character,
    *  -1 if no match or <code>null</code> string input
    * @since 2.0
    */
    public static int lastIndexOf(String str, char searchChar, int startPos) {
        if (isEmpty(str)) {
            return -1;
        }
        return str.lastIndexOf(searchChar, startPos);
    }

    /**
     * <p>Finds the last index within a String, handling <code>null</code>.
     * This method uses {@link String#lastIndexOf(String)}.</p>
     *
     * <p>A <code>null</code> String will return <code>-1</code>.</p>
     *
     * <pre>
     * StringUtils.lastIndexOf(null, *)          = -1
     * StringUtils.lastIndexOf(*, null)          = -1
     * StringUtils.lastIndexOf("", "")           = 0
     * StringUtils.lastIndexOf("aabaabaa", "a")  = 0
     * StringUtils.lastIndexOf("aabaabaa", "b")  = 2
     * StringUtils.lastIndexOf("aabaabaa", "ab") = 1
     * StringUtils.lastIndexOf("aabaabaa", "")   = 8
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchStr  the String to find, may be null
     * @return the last index of the search String,
     *  -1 if no match or <code>null</code> string input
     * @since 2.0
     */
    public static int lastIndexOf(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return -1;
        }
        return str.lastIndexOf(searchStr);
    }

    /**
     * <p>Finds the first index within a String, handling <code>null</code>.
     * This method uses {@link String#lastIndexOf(String, int)}.</p>
     *
     * <p>A <code>null</code> String will return <code>-1</code>.
     * A negative start position returns <code>-1</code>.
     * An empty ("") search String always matches unless the start position is negative.
     * A start position greater than the string length searches the whole string.</p>
     *
     * <pre>
     * StringUtils.lastIndexOf(null, *, *)          = -1
     * StringUtils.lastIndexOf(*, null, *)          = -1
     * StringUtils.lastIndexOf("aabaabaa", "a", 8)  = 7
     * StringUtils.lastIndexOf("aabaabaa", "b", 8)  = 5
     * StringUtils.lastIndexOf("aabaabaa", "ab", 8) = 4
     * StringUtils.lastIndexOf("aabaabaa", "b", 9)  = 5
     * StringUtils.lastIndexOf("aabaabaa", "b", -1) = -1
     * StringUtils.lastIndexOf("aabaabaa", "a", 0)  = 0
     * StringUtils.lastIndexOf("aabaabaa", "b", 0)  = -1
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchStr  the String to find, may be null
     * @param startPos  the start position, negative treated as zero
     * @return the first index of the search String,
     *  -1 if no match or <code>null</code> string input
     * @since 2.0
     */
    public static int lastIndexOf(String str, String searchStr, int startPos) {
        if (str == null || searchStr == null) {
            return -1;
        }
        return str.lastIndexOf(searchStr, startPos);
    }

    /**
     * <p>Case in-sensitive find of the last index within a String.</p>
     *
     * <p>A <code>null</code> String will return <code>-1</code>.
     * A negative start position returns <code>-1</code>.
     * An empty ("") search String always matches unless the start position is negative.
     * A start position greater than the string length searches the whole string.</p>
     *
     * <pre>
     * StringUtils.lastIndexOfIgnoreCase(null, *)          = -1
     * StringUtils.lastIndexOfIgnoreCase(*, null)          = -1
     * StringUtils.lastIndexOfIgnoreCase("aabaabaa", "A")  = 7
     * StringUtils.lastIndexOfIgnoreCase("aabaabaa", "B")  = 5
     * StringUtils.lastIndexOfIgnoreCase("aabaabaa", "AB") = 4
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchStr  the String to find, may be null
     * @return the first index of the search String,
     *  -1 if no match or <code>null</code> string input
     * @since 2.5
     */
    public static int lastIndexOfIgnoreCase(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return -1;
        }
        return lastIndexOfIgnoreCase(str, searchStr, str.length());
    }

    /**
     * <p>Case in-sensitive find of the last index within a String
     * from the specified position.</p>
     *
     * <p>A <code>null</code> String will return <code>-1</code>.
     * A negative start position returns <code>-1</code>.
     * An empty ("") search String always matches unless the start position is negative.
     * A start position greater than the string length searches the whole string.</p>
     *
     * <pre>
     * StringUtils.lastIndexOfIgnoreCase(null, *, *)          = -1
     * StringUtils.lastIndexOfIgnoreCase(*, null, *)          = -1
     * StringUtils.lastIndexOfIgnoreCase("aabaabaa", "A", 8)  = 7
     * StringUtils.lastIndexOfIgnoreCase("aabaabaa", "B", 8)  = 5
     * StringUtils.lastIndexOfIgnoreCase("aabaabaa", "AB", 8) = 4
     * StringUtils.lastIndexOfIgnoreCase("aabaabaa", "B", 9)  = 5
     * StringUtils.lastIndexOfIgnoreCase("aabaabaa", "B", -1) = -1
     * StringUtils.lastIndexOfIgnoreCase("aabaabaa", "A", 0)  = 0
     * StringUtils.lastIndexOfIgnoreCase("aabaabaa", "B", 0)  = -1
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchStr  the String to find, may be null
     * @param startPos  the start position
     * @return the first index of the search String,
     *  -1 if no match or <code>null</code> string input
     * @since 2.5
     */
    public static int lastIndexOfIgnoreCase(String str, String searchStr, int startPos) {
        if (str == null || searchStr == null) {
            return -1;
        }
        if (startPos > str.length() - searchStr.length()) {
            startPos = str.length() - searchStr.length();
        }
        if (startPos < 0) {
            return -1;
        }
        if (searchStr.length() == 0) {
            return startPos;
        }

        for (int i = startPos; i >= 0; i--) {
            if (str.regionMatches(true, i, searchStr, 0, searchStr.length())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Test whether the given string matches the given substring at the given index.
     * @param str the original string (or StringBuffer)
     * @param index the index in the original string to start matching against
     * @param substring the substring to match at the given index
     * @return true/false
     */
    public static boolean substringMatch(CharSequence str, int index, CharSequence substring) {
        for (int j = 0; j < substring.length(); j++) {
            int i = index + j;
            if (i >= str.length() || str.charAt(i) != substring.charAt(j)) {
                return false;
            }
        }
        return true;
    }

    // Substring between
    //-----------------------------------------------------------------------
    /**
     * <p>Gets the String that is nested in between two instances of the
     * same String.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.
     * A <code>null</code> tag returns <code>null</code>.</p>
     *
     * <pre>
     * StringUtils.substringBetween(null, *)            = null
     * StringUtils.substringBetween("", "")             = ""
     * StringUtils.substringBetween("", "tag")          = null
     * StringUtils.substringBetween("tagabctag", null)  = null
     * StringUtils.substringBetween("tagabctag", "")    = ""
     * StringUtils.substringBetween("tagabctag", "tag") = "abc"
     * </pre>
     *
     * @param str  the String containing the substring, may be null
     * @param tag  the String before and after the substring, may be null
     * @return the substring, <code>null</code> if no match
     * @since 2.0
     */
    public static String substringBetween(String str, String tag) {
        return substringBetween(str, tag, tag);
    }

    /**
     * <p>Gets the String that is nested in between two Strings.
     * Only the first match is returned.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.
     * A <code>null</code> open/close returns <code>null</code> (no match).
     * An empty ("") open and close returns an empty string.</p>
     *
     * <pre>
     * StringUtils.substringBetween("wx[b]yz", "[", "]") = "b"
     * StringUtils.substringBetween(null, *, *)          = null
     * StringUtils.substringBetween(*, null, *)          = null
     * StringUtils.substringBetween(*, *, null)          = null
     * StringUtils.substringBetween("", "", "")          = ""
     * StringUtils.substringBetween("", "", "]")         = null
     * StringUtils.substringBetween("", "[", "]")        = null
     * StringUtils.substringBetween("yabcz", "", "")     = ""
     * StringUtils.substringBetween("yabcz", "y", "z")   = "abc"
     * StringUtils.substringBetween("yabczyabcz", "y", "z")   = "abc"
     * </pre>
     *
     * @param str  the String containing the substring, may be null
     * @param open  the String before the substring, may be null
     * @param close  the String after the substring, may be null
     * @return the substring, <code>null</code> if no match
     * @since 2.0
     */
    public static String substringBetween(String str, String open, String close) {
        if (str == null || open == null || close == null) {
            return null;
        }
        int start = str.indexOf(open);
        if (start != -1) {
            int end = str.indexOf(close, start + open.length());
            if (end != -1) {
                return str.substring(start + open.length(), end);
            }
        }
        return null;
    }

    /**
     * <p>Searches a String for substrings delimited by a start and end tag,
     * returning all matching substrings in an array.</p>
     *
     * <p>A <code>null</code> input String returns <code>null</code>.
     * A <code>null</code> open/close returns <code>null</code> (no match).
     * An empty ("") open/close returns <code>null</code> (no match).</p>
     *
     * <pre>
     * StringUtils.substringsBetween("[a][b][c]", "[", "]") = ["a","b","c"]
     * StringUtils.substringsBetween(null, *, *)            = null
     * StringUtils.substringsBetween(*, null, *)            = null
     * StringUtils.substringsBetween(*, *, null)            = null
     * StringUtils.substringsBetween("", "[", "]")          = []
     * </pre>
     *
     * @param str  the String containing the substrings, null returns null, empty returns empty
     * @param open  the String identifying the start of the substring, empty returns null
     * @param close  the String identifying the end of the substring, empty returns null
     * @return a String Array of substrings, or <code>null</code> if no match
     * @since 2.3
     */
    public static String[] substringsBetween(String str, String open, String close) {
        if (str == null || isEmpty(open) || isEmpty(close)) {
            return null;
        }
        int strLen = str.length();
        if (strLen == 0) {
            return Arrs.EMPTY_STRING_ARRAY;
        }
        int closeLen = close.length();
        int openLen = open.length();
        List list = new ArrayList();
        int pos = 0;
        while (pos < strLen - closeLen) {
            int start = str.indexOf(open, pos);
            if (start < 0) {
                break;
            }
            start += openLen;
            int end = str.indexOf(close, start);
            if (end < 0) {
                break;
            }
            list.add(str.substring(start, end));
            pos = end + closeLen;
        }
        if (list.isEmpty()) {
            return null;
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    /**
     * <p>Uncapitalizes a String changing the first letter to title case as
     * per {@link Character#toLowerCase(char)}. No other letters are changed.</p>
     *
     * <p>For a word based algorithm, see {@link org.apache.commons.lang3.text.WordUtils#uncapitalize(String)}.
     * A {@code null} input String returns {@code null}.</p>
     *
     * <pre>
     * StringUtils.uncapitalize(null)  = null
     * StringUtils.uncapitalize("")    = ""
     * StringUtils.uncapitalize("Cat") = "cat"
     * StringUtils.uncapitalize("CAT") = "cAT"
     * </pre>
     *
     * @param str the String to uncapitalize, may be null
     * @return the uncapitalized String, {@code null} if null String input
     * @see org.apache.commons.lang3.text.WordUtils#uncapitalize(String)
     * @see #capitalize(String)
     * @since 2.0
     */
    public static String uncapitalize(String str) {
        if (str == null) {
            return str;
        }
        int strLen = str.length();
        if (strLen == 0) {
            return str;
        }

        return new StringBuilder(strLen)
                .append(Character.toLowerCase(str.charAt(0)))
                .append(str.substring(1))
                .toString();
    }

    /**
     * 判断字符串是否数字。
     * @param string 字符串。
     * @return true 是数字。
     */
    public static boolean isNumeric(String string) {
        return NUMERIC_PATTERN.matcher(string).matches();
    }

    /**
     * 判断字符串是否整数。
     * @param string 字符串。
     * @return true 是整数。
     */
    public static boolean isInteger(String string) {
        Matcher matcher = INTEGER_PATTERN.matcher(string);
        if (!matcher.matches()) {
            return false;
        }

        String number = matcher.group(1);
        String maxValue = "" + Integer.MAX_VALUE;
        if (number.length() > maxValue.length()) {
            return false;
        }

        return alignRight(number, maxValue.length(), '0').compareTo(maxValue) <= 0;
    }

    /**
     * 判断字符串是否整数。
     * @param string 字符串。
     * @return true 是整数。
     */
    public static boolean isLong(String string) {
        Matcher matcher = INTEGER_PATTERN.matcher(string);
        if (!matcher.matches()) {
            return false;
        }

        String number = matcher.group(1);
        String maxValue = "" + Long.MAX_VALUE;
        if (number.length() > maxValue.length()) {
            return false;
        }

        return alignRight(number, maxValue.length(), '0').compareTo(maxValue) <= 0;
    }

    /**
     * <p>Gets the substring before the last occurrence of a separator.
     * The separator is not returned.</p>
     *
     * <p>A {@code null} string input will return {@code null}.
     * An empty ("") string input will return the empty string.
     * An empty or {@code null} separator will return the input string.</p>
     *
     * <p>If nothing is found, the string input is returned.</p>
     *
     * <pre>
     * StringUtils.substringBeforeLast(null, *)      = null
     * StringUtils.substringBeforeLast("", *)        = ""
     * StringUtils.substringBeforeLast("abcba", "b") = "abc"
     * StringUtils.substringBeforeLast("abc", "c")   = "ab"
     * StringUtils.substringBeforeLast("a", "a")     = ""
     * StringUtils.substringBeforeLast("a", "z")     = "a"
     * StringUtils.substringBeforeLast("a", null)    = "a"
     * StringUtils.substringBeforeLast("a", "")      = "a"
     * </pre>
     *
     * @param str  the String to get a substring from, may be null
     * @param separator  the String to search for, may be null
     * @return the substring before the last occurrence of the separator,
     *  {@code null} if null String input
     * @since 2.0
     */
    public static String substringBeforeLast(String str, String separator) {
        if (isEmpty(str) || isEmpty(separator)) {
            return str;
        }
        int pos = str.lastIndexOf(separator);
        if (pos == INDEX_NOT_FOUND) {
            return str;
        }
        return str.substring(0, pos);
    }

    /**
     * <p>Gets the substring after the last occurrence of a separator.
     * The separator is not returned.</p>
     *
     * <p>A {@code null} string input will return {@code null}.
     * An empty ("") string input will return the empty string.
     * An empty or {@code null} separator will return the empty string if
     * the input string is not {@code null}.</p>
     *
     * <p>If nothing is found, the empty string is returned.</p>
     *
     * <pre>
     * StringUtils.substringAfterLast(null, *)      = null
     * StringUtils.substringAfterLast("", *)        = ""
     * StringUtils.substringAfterLast(*, "")        = ""
     * StringUtils.substringAfterLast(*, null)      = ""
     * StringUtils.substringAfterLast("abc", "a")   = "bc"
     * StringUtils.substringAfterLast("abcba", "b") = "a"
     * StringUtils.substringAfterLast("abc", "c")   = ""
     * StringUtils.substringAfterLast("a", "a")     = ""
     * StringUtils.substringAfterLast("a", "z")     = ""
     * </pre>
     *
     * @param str  the String to get a substring from, may be null
     * @param separator  the String to search for, may be null
     * @return the substring after the last occurrence of the separator,
     *  {@code null} if null String input
     * @since 2.0
     */
    public static String substringAfterLast(String str, String separator) {
        if (isEmpty(str)) {
            return str;
        }
        if (isEmpty(separator)) {
            return EMPTY;
        }
        int pos = str.lastIndexOf(separator);
        if (pos == INDEX_NOT_FOUND || pos == str.length() - separator.length()) {
            return EMPTY;
        }
        return str.substring(pos + separator.length());
    }

    // Replacing
    //-----------------------------------------------------------------------
    /**
     * <p>Replaces a String with another String inside a larger String, once.</p>
     *
     * <p>A {@code null} reference passed to this method is a no-op.</p>
     *
     * <pre>
     * StringUtils.replaceOnce(null, *, *)        = null
     * StringUtils.replaceOnce("", *, *)          = ""
     * StringUtils.replaceOnce("any", null, *)    = "any"
     * StringUtils.replaceOnce("any", *, null)    = "any"
     * StringUtils.replaceOnce("any", "", *)      = "any"
     * StringUtils.replaceOnce("aba", "a", null)  = "aba"
     * StringUtils.replaceOnce("aba", "a", "")    = "ba"
     * StringUtils.replaceOnce("aba", "a", "z")   = "zba"
     * </pre>
     *
     * @see #replace(String text, String searchString, String replacement, int max)
     * @param text  text to search and replace in, may be null
     * @param searchString  the String to search for, may be null
     * @param replacement  the String to replace with, may be null
     * @return the text with any replacements processed,
     *  {@code null} if null String input
     */
    public static String replaceOnce(String text, String searchString, String replacement) {
        return replace(text, searchString, replacement, 1);
    }

    /**
     * <p>Replaces all occurrences of a String within another String.</p>
     *
     * <p>A {@code null} reference passed to this method is a no-op.</p>
     *
     * <pre>
     * StringUtils.replace(null, *, *)        = null
     * StringUtils.replace("", *, *)          = ""
     * StringUtils.replace("any", null, *)    = "any"
     * StringUtils.replace("any", *, null)    = "any"
     * StringUtils.replace("any", "", *)      = "any"
     * StringUtils.replace("aba", "a", null)  = "aba"
     * StringUtils.replace("aba", "a", "")    = "b"
     * StringUtils.replace("aba", "a", "z")   = "zbz"
     * </pre>
     *
     * @see #replace(String text, String searchString, String replacement, int max)
     * @param text  text to search and replace in, may be null
     * @param searchString  the String to search for, may be null
     * @param replacement  the String to replace it with, may be null
     * @return the text with any replacements processed,
     *  {@code null} if null String input
     */
    public static String replace(String text, String searchString, String replacement) {
        return replace(text, searchString, replacement, -1);
    }

    /**
     * <p>Replaces a String with another String inside a larger String,
     * for the first {@code max} values of the search String.</p>
     *
     * <p>A {@code null} reference passed to this method is a no-op.</p>
     *
     * <pre>
     * StringUtils.replace(null, *, *, *)         = null
     * StringUtils.replace("", *, *, *)           = ""
     * StringUtils.replace("any", null, *, *)     = "any"
     * StringUtils.replace("any", *, null, *)     = "any"
     * StringUtils.replace("any", "", *, *)       = "any"
     * StringUtils.replace("any", *, *, 0)        = "any"
     * StringUtils.replace("abaa", "a", null, -1) = "abaa"
     * StringUtils.replace("abaa", "a", "", -1)   = "b"
     * StringUtils.replace("abaa", "a", "z", 0)   = "abaa"
     * StringUtils.replace("abaa", "a", "z", 1)   = "zbaa"
     * StringUtils.replace("abaa", "a", "z", 2)   = "zbza"
     * StringUtils.replace("abaa", "a", "z", -1)  = "zbzz"
     * </pre>
     *
     * @param text  text to search and replace in, may be null
     * @param searchString  the String to search for, may be null
     * @param replacement  the String to replace it with, may be null
     * @param max  maximum number of values to replace, or {@code -1} if no maximum
     * @return the text with any replacements processed,
     *  {@code null} if null String input
     */
    public static String replace(String text, String searchString, String replacement, int max) {
        if (isEmpty(text) || isEmpty(searchString) || replacement == null || max == 0) {
            return text;
        }
        int start = 0;
        int end = text.indexOf(searchString, start);
        if (end == INDEX_NOT_FOUND) {
            return text;
        }
        int replLength = searchString.length();
        int increase = replacement.length() - replLength;
        increase = increase < 0 ? 0 : increase;
        increase *= max < 0 ? 16 : max > 64 ? 64 : max;
        StringBuilder buf = new StringBuilder(text.length() + increase);
        while (end != INDEX_NOT_FOUND) {
            buf.append(text.substring(start, end)).append(replacement);
            start = end + replLength;
            if (--max == 0) {
                break;
            }
            end = text.indexOf(searchString, start);
        }
        buf.append(text.substring(start));
        return buf.toString();
    }

    /**
     * <p>
     * Replaces all occurrences of Strings within another String.
     * </p>
     *
     * <p>
     * A {@code null} reference passed to this method is a no-op, or if
     * any "search string" or "string to replace" is null, that replace will be
     * ignored. This will not repeat. For repeating replaces, call the
     * overloaded method.
     * </p>
     *
     * <pre>
     *  StringUtils.replaceEach(null, *, *)        = null
     *  StringUtils.replaceEach("", *, *)          = ""
     *  StringUtils.replaceEach("aba", null, null) = "aba"
     *  StringUtils.replaceEach("aba", new String[0], null) = "aba"
     *  StringUtils.replaceEach("aba", null, new String[0]) = "aba"
     *  StringUtils.replaceEach("aba", new String[]{"a"}, null)  = "aba"
     *  StringUtils.replaceEach("aba", new String[]{"a"}, new String[]{""})  = "b"
     *  StringUtils.replaceEach("aba", new String[]{null}, new String[]{"a"})  = "aba"
     *  StringUtils.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"w", "t"})  = "wcte"
     *  (example of how it does not repeat)
     *  StringUtils.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"d", "t"})  = "dcte"
     * </pre>
     *
     * @param text
     *            text to search and replace in, no-op if null
     * @param searchList
     *            the Strings to search for, no-op if null
     * @param replacementList
     *            the Strings to replace them with, no-op if null
     * @return the text with any replacements processed, {@code null} if
     *         null String input
     * @since 2.4
     */
    public static String replaceEach(String text, String[] searchList, String[] replacementList) {
        return replaceEach(text, searchList, replacementList, false, 0);
    }

    /**
     * <p>
     * Replaces all occurrences of Strings within another String.
     * </p>
     *
     * <p>
     * A {@code null} reference passed to this method is a no-op, or if
     * any "search string" or "string to replace" is null, that replace will be
     * ignored. 
     * </p>
     *
     * <pre>
     *  StringUtils.replaceEach(null, *, *, *) = null
     *  StringUtils.replaceEach("", *, *, *) = ""
     *  StringUtils.replaceEach("aba", null, null, *) = "aba"
     *  StringUtils.replaceEach("aba", new String[0], null, *) = "aba"
     *  StringUtils.replaceEach("aba", null, new String[0], *) = "aba"
     *  StringUtils.replaceEach("aba", new String[]{"a"}, null, *) = "aba"
     *  StringUtils.replaceEach("aba", new String[]{"a"}, new String[]{""}, *) = "b"
     *  StringUtils.replaceEach("aba", new String[]{null}, new String[]{"a"}, *) = "aba"
     *  StringUtils.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"w", "t"}, *) = "wcte"
     *  (example of how it repeats)
     *  StringUtils.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"d", "t"}, false) = "dcte"
     *  StringUtils.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"d", "t"}, true) = "tcte"
     *  StringUtils.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"d", "ab"}, true) = IllegalStateException
     *  StringUtils.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"d", "ab"}, false) = "dcabe"
     * </pre>
     *
     * @param text
     *            text to search and replace in, no-op if null
     * @param searchList
     *            the Strings to search for, no-op if null
     * @param replacementList
     *            the Strings to replace them with, no-op if null
     * @return the text with any replacements processed, {@code null} if
     *         null String input
     */
    public static String replaceEachRepeatedly(String text, String[] searchList, String[] replacementList) {
        // timeToLive should be 0 if not used or nothing to replace, else it's
        // the length of the replace array
        int timeToLive = searchList == null ? 0 : searchList.length;
        return replaceEach(text, searchList, replacementList, true, timeToLive);
    }

    /**
     * <p>
     * Replaces all occurrences of Strings within another String.
     * </p>
     *
     * <p>
     * A {@code null} reference passed to this method is a no-op, or if
     * any "search string" or "string to replace" is null, that replace will be
     * ignored.
     * </p>
     *
     * <pre>
     *  StringUtils.replaceEach(null, *, *, *) = null
     *  StringUtils.replaceEach("", *, *, *) = ""
     *  StringUtils.replaceEach("aba", null, null, *) = "aba"
     *  StringUtils.replaceEach("aba", new String[0], null, *) = "aba"
     *  StringUtils.replaceEach("aba", null, new String[0], *) = "aba"
     *  StringUtils.replaceEach("aba", new String[]{"a"}, null, *) = "aba"
     *  StringUtils.replaceEach("aba", new String[]{"a"}, new String[]{""}, *) = "b"
     *  StringUtils.replaceEach("aba", new String[]{null}, new String[]{"a"}, *) = "aba"
     *  StringUtils.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"w", "t"}, *) = "wcte"
     *  (example of how it repeats)
     *  StringUtils.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"d", "t"}, false) = "dcte"
     *  StringUtils.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"d", "t"}, true) = "tcte"
     *  StringUtils.replaceEach("abcde", new String[]{"ab", "d"}, new String[]{"d", "ab"}, *) = IllegalStateException
     * </pre>
     *
     * @param text
     *            text to search and replace in, no-op if null
     * @param searchList
     *            the Strings to search for, no-op if null
     * @param replacementList
     *            the Strings to replace them with, no-op if null
     * @param repeat if true, then replace repeatedly
     *       until there are no more possible replacements or timeToLive < 0
     * @param timeToLive
     *            if less than 0 then there is a circular reference and endless
     *            loop
     * @return the text with any replacements processed, {@code null} if
     *         null String input
     * @throws IllegalStateException
     *             if the search is repeating and there is an endless loop due
     *             to outputs of one being inputs to another
     * @throws IllegalArgumentException
     *             if the lengths of the arrays are not the same (null is ok,
     *             and/or size 0)
     * @since 2.4
     */
    private static String replaceEach(
            String text, String[] searchList, String[] replacementList, boolean repeat, int timeToLive) {

        // mchyzer Performance note: This creates very few new objects (one major goal)
        // let me know if there are performance requests, we can create a harness to measure

        if (text == null || text.length() == 0 || searchList == null
                || searchList.length == 0 || replacementList == null || replacementList.length == 0) {
            return text;
        }

        // if recursing, this shouldn't be less than 0
        if (timeToLive < 0) {
            throw new IllegalStateException("Aborting to protect against StackOverflowError - "
                                         + "output of one loop is the input of another");
        }

        int searchLength = searchList.length;
        int replacementLength = replacementList.length;

        // make sure lengths are ok, these need to be equal
        if (searchLength != replacementLength) {
            throw new IllegalArgumentException("Search and Replace array lengths don't match: "
                    + searchLength
                    + " vs "
                    + replacementLength);
        }

        // keep track of which still have matches
        boolean[] noMoreMatchesForReplIndex = new boolean[searchLength];

        // index on index that the match was found
        int textIndex = -1;
        int replaceIndex = -1;
        int tempIndex = -1;

        // index of replace array that will replace the search string found
        // NOTE: logic duplicated below START
        for (int i = 0; i < searchLength; i++) {
            if (noMoreMatchesForReplIndex[i] || searchList[i] == null
                    || searchList[i].length() == 0 || replacementList[i] == null) {
                continue;
            }
            tempIndex = text.indexOf(searchList[i]);

            // see if we need to keep searching for this
            if (tempIndex == -1) {
                noMoreMatchesForReplIndex[i] = true;
            }
            else {
                if (textIndex == -1 || tempIndex < textIndex) {
                    textIndex = tempIndex;
                    replaceIndex = i;
                }
            }
        }
        // NOTE: logic mostly below END

        // no search strings found, we are done
        if (textIndex == -1) {
            return text;
        }

        int start = 0;

        // get a good guess on the size of the result buffer so it doesn't have to double if it goes over a bit
        int increase = 0;

        // count the replacement text elements that are larger than their corresponding text being replaced
        for (int i = 0; i < searchList.length; i++) {
            if (searchList[i] == null || replacementList[i] == null) {
                continue;
            }
            int greater = replacementList[i].length() - searchList[i].length();
            if (greater > 0) {
                increase += 3 * greater; // assume 3 matches
            }
        }
        // have upper-bound at 20% increase, then let Java take over
        increase = Math.min(increase, text.length() / 5);

        StringBuilder buf = new StringBuilder(text.length() + increase);

        while (textIndex != -1) {

            for (int i = start; i < textIndex; i++) {
                buf.append(text.charAt(i));
            }
            buf.append(replacementList[replaceIndex]);

            start = textIndex + searchList[replaceIndex].length();

            textIndex = -1;
            replaceIndex = -1;
            tempIndex = -1;
            // find the next earliest match
            // NOTE: logic mostly duplicated above START
            for (int i = 0; i < searchLength; i++) {
                if (noMoreMatchesForReplIndex[i] || searchList[i] == null
                        || searchList[i].length() == 0 || replacementList[i] == null) {
                    continue;
                }
                tempIndex = text.indexOf(searchList[i], start);

                // see if we need to keep searching for this
                if (tempIndex == -1) {
                    noMoreMatchesForReplIndex[i] = true;
                }
                else {
                    if (textIndex == -1 || tempIndex < textIndex) {
                        textIndex = tempIndex;
                        replaceIndex = i;
                    }
                }
            }
            // NOTE: logic duplicated above END

        }
        int textLength = text.length();
        for (int i = start; i < textLength; i++) {
            buf.append(text.charAt(i));
        }
        String result = buf.toString();
        if (!repeat) {
            return result;
        }

        return replaceEach(result, searchList, replacementList, repeat, timeToLive - 1);
    }

    // Replace, character based
    //-----------------------------------------------------------------------
    /**
     * <p>Replaces all occurrences of a character in a String with another.
     * This is a null-safe version of {@link String#replace(char, char)}.</p>
     *
     * <p>A {@code null} string input returns {@code null}.
     * An empty ("") string input returns an empty string.</p>
     *
     * <pre>
     * StringUtils.replaceChars(null, *, *)        = null
     * StringUtils.replaceChars("", *, *)          = ""
     * StringUtils.replaceChars("abcba", 'b', 'y') = "aycya"
     * StringUtils.replaceChars("abcba", 'z', 'y') = "abcba"
     * </pre>
     *
     * @param str  String to replace characters in, may be null
     * @param searchChar  the character to search for, may be null
     * @param replaceChar  the character to replace, may be null
     * @return modified String, {@code null} if null string input
     * @since 2.0
     */
    public static String replaceChars(String str, char searchChar, char replaceChar) {
        if (str == null) {
            return null;
        }
        return str.replace(searchChar, replaceChar);
    }

    /**
     * <p>Replaces multiple characters in a String in one go.
     * This method can also be used to delete characters.</p>
     *
     * <p>For example:<br />
     * <code>replaceChars(&quot;hello&quot;, &quot;ho&quot;, &quot;jy&quot;) = jelly</code>.</p>
     *
     * <p>A {@code null} string input returns {@code null}.
     * An empty ("") string input returns an empty string.
     * A null or empty set of search characters returns the input string.</p>
     *
     * <p>The length of the search characters should normally equal the length
     * of the replace characters.
     * If the search characters is longer, then the extra search characters
     * are deleted.
     * If the search characters is shorter, then the extra replace characters
     * are ignored.</p>
     *
     * <pre>
     * StringUtils.replaceChars(null, *, *)           = null
     * StringUtils.replaceChars("", *, *)             = ""
     * StringUtils.replaceChars("abc", null, *)       = "abc"
     * StringUtils.replaceChars("abc", "", *)         = "abc"
     * StringUtils.replaceChars("abc", "b", null)     = "ac"
     * StringUtils.replaceChars("abc", "b", "")       = "ac"
     * StringUtils.replaceChars("abcba", "bc", "yz")  = "ayzya"
     * StringUtils.replaceChars("abcba", "bc", "y")   = "ayya"
     * StringUtils.replaceChars("abcba", "bc", "yzx") = "ayzya"
     * </pre>
     *
     * @param str  String to replace characters in, may be null
     * @param searchChars  a set of characters to search for, may be null
     * @param replaceChars  a set of characters to replace, may be null
     * @return modified String, {@code null} if null string input
     * @since 2.0
     */
    public static String replaceChars(String str, String searchChars, String replaceChars) {
        if (isEmpty(str) || isEmpty(searchChars)) {
            return str;
        }
        if (replaceChars == null) {
            replaceChars = EMPTY;
        }
        boolean modified = false;
        int replaceCharsLength = replaceChars.length();
        int strLength = str.length();
        StringBuilder buf = new StringBuilder(strLength);
        for (int i = 0; i < strLength; i++) {
            char ch = str.charAt(i);
            int index = searchChars.indexOf(ch);
            if (index >= 0) {
                modified = true;
                if (index < replaceCharsLength) {
                    buf.append(replaceChars.charAt(index));
                }
            }
            else {
                buf.append(ch);
            }
        }
        if (modified) {
            return buf.toString();
        }
        return str;
    }

    // Defaults
    //-----------------------------------------------------------------------
    /**
     * <p>Returns either the passed in String,
     * or if the String is {@code null}, an empty String ("").</p>
     *
     * <pre>
     * StringUtils.defaultString(null)  = ""
     * StringUtils.defaultString("")    = ""
     * StringUtils.defaultString("bat") = "bat"
     * </pre>
     *
     * @see String#valueOf(Object)
     * @param str  the String to check, may be null
     * @return the passed in String, or the empty String if it
     *  was {@code null}
     */
    public static String defaultString(String str) {
        return str == null ? EMPTY : str;
    }

    /**
     * <p>Returns either the passed in String, or if the String is
     * {@code null}, the value of {@code defaultStr}.</p>
     *
     * <pre>
     * StringUtils.defaultString(null, "NULL")  = "NULL"
     * StringUtils.defaultString("", "NULL")    = ""
     * StringUtils.defaultString("bat", "NULL") = "bat"
     * </pre>
     *
     * @see String#valueOf(Object)
     * @param str  the String to check, may be null
     * @param defaultStr  the default String to return
     *  if the input is {@code null}, may be null
     * @return the passed in String, or the default if it was {@code null}
     */
    public static String defaultString(String str, String defaultStr) {
        return str == null ? defaultStr : str;
    }

    /**
     * <p>Returns either the passed in CharSequence, or if the CharSequence is
     * whitespace, empty ("") or {@code null}, the value of {@code defaultStr}.</p>
     *
     * <pre>
     * StringUtils.defaultIfBlank(null, "NULL")  = "NULL"
     * StringUtils.defaultIfBlank("", "NULL")    = "NULL"
     * StringUtils.defaultIfBlank(" ", "NULL")   = "NULL"
     * StringUtils.defaultIfBlank("bat", "NULL") = "bat"
     * StringUtils.defaultIfBlank("", null)      = null
     * </pre>
     * @param <T> the specific kind of CharSequence
     * @param str the CharSequence to check, may be null
     * @param defaultStr  the default CharSequence to return
     *  if the input is whitespace, empty ("") or {@code null}, may be null
     * @return the passed in CharSequence, or the default
     */
    public static <T extends CharSequence> T defaultIfBlank(T str, T defaultStr) {
        return isBlank(str) ? defaultStr : str;
    }

    /**
     * <p>Returns either the passed in CharSequence, or if the CharSequence is
     * empty or {@code null}, the value of {@code defaultStr}.</p>
     *
     * <pre>
     * StringUtils.defaultIfEmpty(null, "NULL")  = "NULL"
     * StringUtils.defaultIfEmpty("", "NULL")    = "NULL"
     * StringUtils.defaultIfEmpty("bat", "NULL") = "bat"
     * StringUtils.defaultIfEmpty("", null)      = null
     * </pre>
     * @param <T> the specific kind of CharSequence
     * @param str  the CharSequence to check, may be null
     * @param defaultStr  the default CharSequence to return
     *  if the input is empty ("") or {@code null}, may be null
     * @return the passed in CharSequence, or the default
     */
    public static <T extends CharSequence> T defaultIfEmpty(T str, T defaultStr) {
        return isEmpty(str) ? defaultStr : str;
    }

    /**
     * 根据expr解码字符串 , 返回expr中匹配str的后一项。
     * expr以逗号分割，奇数分割单元为键，对应的后一项为值
     * @param str 键
     * @param expr 逗号分割字符串
     * @return 键对应的值，如果找不到，返回"null"
     */
    public static String decode(String str, String expr) {
        String[] arr = expr.split(SEPERATE_COMMA);
        String temp = str;
        if (temp == null) {
            temp = "null";
        }

        int i = 0;
        for (; i < arr.length - 1; i = i + 2) {
            if (arr[i].equals(str)) {
                return arr[i + 1];
            }
        }

        if (arr.length - i == 1) {
            return arr[i];
        }

        return temp;
    }

    /**
     * 获取字符串的GBK编码byte数组。
     * @param str 字符串
     * @return 字节数组
     */
    public static byte[] getGBKBytes(String str) {
        try {
            return str.getBytes("GBK");
        }
        catch (UnsupportedEncodingException e) {
            throw new BaseException(e);
        }
    }

    /**
     * 获取字符串的GBK编码byte数组。
     * @param str 字符串
     * @return 字节数组
     */
    public static byte[] getUTF8Bytes(String str) {
        try {
            return str.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new BaseException(e);
        }
    }

    /**
     * 根据字节长度截取字串。
     * 
     * @param input 字符串
     * @param n 长度
     * @return 字符串子串
     */
    public static String substrbGBK(String input, int n) {
        return substrb(input, n, "GBK");
    }

    /**
     * 获取字符串的子串, 子串为从字符串开始位置起的n个字节的字符串, 字符串内容为双字节字符是, n向下取整。
     * @param input 字符串
     * @param n 子串长度
     * @param charsetName 字符集名称
     * @return 字符串子串
     */
    public static String substrb(String input, int n, String charsetName) {
        if (n <= 0) {
            return "";
        }

        byte[] bytes;
        try {
            bytes = input.getBytes(charsetName);
        }
        catch (UnsupportedEncodingException e) {
            throw new BaseException(e);
        }

        if (n >= bytes.length) {
            return input;
        }

        byte[] data = new byte[n]; // 结果

        int x = 0;
        int y = 0;
        while (x < n) {
            int length = Character.toString(input.charAt(y)).getBytes().length;
            if (length > 1) { // 双字节
                if (x >= n - 1) { // 如果是最后一个字节
                    break;
                }

                data[x] = bytes[x];
                data[x + 1] = bytes[x + 1];
                x += 2;
                y++;
            }
            else {
                data[x] = bytes[x];
                x++;
                y++;
            }
        }
        try {
            return new String(data, 0, x, charsetName);
        }
        catch (UnsupportedEncodingException e) { // 冗余
            throw new BaseException(e);
        }
    }

    /**
     * 拆解像{01:15位身份证 , 02:18位身份证}这样的字符串。
     * @param mapString 字符串
     * @return Map实例
     */
    public static Map<String, String> parseMapStr(String mapString) {
        return parseMapStr(mapString, SEPERATE_COMMA, ":");
    }

    /**
     * 拆解制定格式的字符串。
     * 制定格式为：<br/>
     *  <li>1. 字符串以{开始，以}结束</li>
     *  <li>2. 字符串有两级形式，两级形式有不同的分隔符</li>
     * 例如：{01:15位身份证 , 02:18位身份证}
     * @param mapString 字符串
     * @param firstSeperator  第一级分隔符
     * @param secondSeperator 第二级分隔符
     * @return Map实例
     */
    public static Map<String, String> parseMapStr(String mapString, String firstSeperator, String secondSeperator) {

        String mappedDesc = substringBetween(mapString, "{", "}");
        if (isEmpty(mappedDesc)) {
            return null;
        }
        String[] someMappedDescs = split(mappedDesc, firstSeperator, true);

        Map<String, String> descMap = new HashMap<String, String>(someMappedDescs.length);
        for (String oneMappedDesc : someMappedDescs) {
            int indexOf = oneMappedDesc.indexOf(secondSeperator);
            if (indexOf < 0) {
                descMap.put(oneMappedDesc, "");
            }
            else if (indexOf > 0) {
                String key = oneMappedDesc.substring(0, indexOf);
                String val = substring(oneMappedDesc, indexOf + secondSeperator.length());

                if (!isBlank(key)) {
                    descMap.put(key.trim(), trim(val));
                }
            }
        }

        return descMap;
    }
}
