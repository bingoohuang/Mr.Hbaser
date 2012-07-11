package org.phw.hbaser.util;

public class JsonEscaper {
    public static String removeQuotation(String value) {
        if (value.charAt(0) == '\'' && value.charAt(value.length() - 1) == '\''
                || value.charAt(0) == '\"' && value.charAt(value.length() - 1) == '\"') {
            return value.substring(1, value.length() - 1);
        }

        return value;
    }

    public static String quotString(String str) {
        if (JsonEscaper.isAsciiPrintable(str)) {
            return str;
        }
        return '\"' + str + '\"';
    }

    public static String quotKey(String str) {
        if (JsonEscaper.isSymbolUnQuotable(str)) {
            return str;
        }
        return '"' + str + '\"';
    }

    /**
     * Escape quotes, \, /, \r, \n, \b, \f, \t and other control characters (U+0000 through U+001F).
     * @param s
     * @return
     */
    public static String escape(String s) {
        if (s == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        escape(s, sb);
        return sb.toString();
    }

    /**
     * @param s - Must not be null.
     * @param sb
     */
    static void escape(String s, StringBuffer sb) {
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            switch (ch) {
            case '"':
                sb.append("\\\"");
                break;
            case '\\':
                sb.append("\\\\");
                break;
            case '\b':
                sb.append("\\b");
                break;
            case '\f':
                sb.append("\\f");
                break;
            case '\n':
                sb.append("\\n");
                break;
            case '\r':
                sb.append("\\r");
                break;
            case '\t':
                sb.append("\\t");
                break;
            case '/':
                sb.append("\\/");
                break;
            default:
                //Reference: http://www.unicode.org/versions/Unicode5.1.0/
                if (ch >= '\u0000' && ch <= '\u001F' || ch >= '\u007F' && ch <= '\u009F'
                        || ch >= '\u2000' && ch <= '\u20FF') {
                    String ss = Integer.toHexString(ch);
                    sb.append("\\u");
                    for (int k = 0; k < 4 - ss.length(); k++) {
                        sb.append('0');
                    }
                    sb.append(ss.toUpperCase());
                }
                else {
                    sb.append(ch);
                }
            }
        }//for
    }

    public static void main(String[] args) {
        String s = "abc\r\n\\aaa\u0000";
        String escape = escape(s);
        System.out.println(escape);
        String unescape = unescape(escape);
        System.out.println(s.equals(unescape));
    }

    public static String unescape(String s) {
        if (s == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        unescape(s, sb);
        return sb.toString();
    }

    /**
     * @param s - Must not be null.
     * @param sb
     */
    static void unescape(String s, StringBuffer sb) {
        for (int i = 0; i < s.length(); ++i) {
            char ch = s.charAt(i);
            if (ch != '\\' || i + 1 >= s.length()) {
                sb.append(ch);
                continue;

            }

            ch = s.charAt(++i);
            switch (ch) {
            case '"':
                sb.append('\"');
                break;
            case '\\':
                sb.append('\\');
                break;
            case 'b':
                sb.append('\b');
                break;
            case 'f':
                sb.append('\f');
                break;
            case 'n':
                sb.append('\n');
                break;
            case 'r':
                sb.append('\r');
                break;
            case 't':
                sb.append('\t');
                break;
            case '/':
                sb.append('/');
                break;
            default:
                if (ch != 'u' || i + 4 >= s.length()) {
                    sb.append(ch);
                    break;
                }
                if (s.charAt(i + 1) >= '0' && s.charAt(i + 1) <= '9'
                        && s.charAt(i + 2) >= '0' && s.charAt(i + 2) <= '9'
                        && s.charAt(i + 3) >= '0' && s.charAt(i + 3) <= '9'
                        && s.charAt(i + 4) >= '0' && s.charAt(i + 4) <= '9') {
                    sb.append((char) fromHexString(s.substring(i + 1, i + 5)));
                    i += 4;
                }
                else {
                    sb.append(ch);
                }
            }
        }//for
    }

    // converts integer n into a base b string
    public static String toString(int n1, int base) {
        // special case
        if (n1 == 0) {
            return "0";
        }

        String digits = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int n = n1;
        String s = "";
        while (n > 0) {
            int d = n % base;
            s = digits.charAt(d) + s;
            n = n / base;
        }

        return s;
    }

    public static String toBinaryString(int n) {
        return toString(n, 2);
    }

    public static String toHexString(int n) {
        return toString(n, 16);
    }

    public static void inputError(String s) {
        throw new RuntimeException("Input error with" + s);
    }

    // convert a String representing a base b integer into an int
    public static int fromString(String s, int b) {
        int result = 0;
        int digit = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= '0' && c <= '9') {
                digit = c - '0';
            }
            else if (c >= 'A' && c <= 'Z') {
                digit = 10 + c - 'A';
            }
            else {
                inputError(s);
            }

            if (digit < b) {
                result = b * result + digit;
            }
            else {
                inputError(s);
            }
        }
        return result;
    }

    public static int fromBinaryString(String s) {
        return fromString(s, 2);
    }

    public static int fromHexString(String s) {
        return fromString(s, 16);
    }

    /**
     * Checks if the string contains only ASCII printable characters.
     * 
     * <code>null</code> will return <code>false</code>.
     * An empty String ("") will return <code>true</code>.
     * 
     * <pre>
     * StringUtils.isAsciiPrintable(null)     = false
     * StringUtils.isAsciiPrintable("")       = true
     * StringUtils.isAsciiPrintable(" ")      = true
     * StringUtils.isAsciiPrintable("Ceki")   = true
     * StringUtils.isAsciiPrintable("ab2c")   = true
     * StringUtils.isAsciiPrintable("!ab-c~") = true
     * StringUtils.isAsciiPrintable("\u0020") = true
     * StringUtils.isAsciiPrintable("\u0021") = true
     * StringUtils.isAsciiPrintable("\u007e") = true
     * StringUtils.isAsciiPrintable("\u007f") = false
     * StringUtils.isAsciiPrintable("Ceki G\u00fclc\u00fc") = false
     * </pre>
     *
     * @param str the string to check, may be null
     * @return <code>true</code> if every character is in the range
     *  32 thru 126
     */
    public static boolean isSymbolUnQuotable(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        if (sz == 0) {
            return false;
        }
        char c = str.charAt(0);
        if (!(c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c == '_')) {
            return false;
        }

        for (int i = 1; i < sz; i++) {
            c = str.charAt(i);
            if (!(c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c == '_' || c >= '0' && c <= '9')) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAsciiPrintable(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (!isAsciiPrintable(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether the character is ASCII 7 bit printable.
     *
     * <pre>
     *   CharUtils.isAsciiPrintable('a')  = true
     *   CharUtils.isAsciiPrintable('A')  = true
     *   CharUtils.isAsciiPrintable('3')  = true
     *   CharUtils.isAsciiPrintable('-')  = true
     *   CharUtils.isAsciiPrintable('\n') = false
     *   CharUtils.isAsciiPrintable('&copy;') = false
     * </pre>
     * 
     * @param ch  the character to check
     * @return true if between 32 and 126 inclusive
     */
    public static boolean isAsciiPrintable(char ch) {
        return ch >= 32 && ch < 127;
    }

}
