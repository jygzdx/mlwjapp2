package com.mlxing.chatui.daoyou.utils;

/**
 * Created by Administrator on 2016/3/8.
 */

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串处理工具类
 *
 * @author xieat@chineseml.net
 */
public class StringUtil {

    /**
     * 按长度分割字符串
     *
     * @param content
     * @param len
     * @return
     */
    public static String[] split(String content, int len) {
        if (content == null || content.equals("")) {
            return null;
        }
        int len2 = content.length();
        if (len2 <= len) {
            return new String[]{content};
        } else {
            int i = len2 / len + 1;
            // System.out.println( "i:" + i );
            String[] strA = new String[i];
            int j = 0;
            int begin = 0;
            int end = 0;
            while (j < i) {
                begin = j * len;
                end = (j + 1) * len;
                if (end > len2)
                    end = len2;
                strA[j] = content.substring(begin, end);
                // System.out.println(strA[j]+"<br/>");
                j = j + 1;
            }
            return strA;
        }
    }

    public static boolean emailFormat(String email) {
        boolean tag = true;
        final String pattern1 = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+" +
                "(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        final Pattern pattern = Pattern.compile(pattern1);
        final Matcher mat = pattern.matcher(email);
        if (!mat.find()) {
            tag = false;
        }
        return tag;
    }

    /**
     * 验证是不是EMAIL
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        boolean retval = false;
        if (StringUtil.empty(email))
            return retval;
        String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)" +
                "+[a-zA-Z]{2,}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(email);
        retval = matcher.matches();
        return retval;
    }

    /**
     * 验证汉字为true
     *
     * @param s
     * @return
     */
    public static boolean isLetterorDigit(String s) {
        if (s.equals("") || s == null) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isLetterOrDigit(s.charAt(i))) {
                // if (!Character.isLetter(s.charAt(i))){
                return false;
            }
        }
        // Character.isJavaLetter()
        return true;
    }

    /**
     * 判断某字符串是否为null，如果长度大于256，则返回256长度的子字符串，反之返回原串
     *
     * @param str
     * @return
     */
    public static String checkStr(String str) {
        if (str == null) {
            return "";
        } else if (str.length() > 256) {
            return str.substring(256);
        } else {
            return str;
        }
    }

    /**
     * 验证是不是Int validate a int string
     *
     * @param str the Integer string.
     * @return true if the str is invalid otherwise false.
     */
    public static boolean validateInt(String str) {
        if (str == null || str.trim().equals(""))
            return false;

        char c;
        for (int i = 0, l = str.length(); i < l; i++) {
            c = str.charAt(i);
            if (!((c >= '0') && (c <= '9')))
                return false;
        }

        return true;
    }

    /**
     * 字节码转换成16进制字符串 内部调试使用
     *
     * @param b
     * @return
     */
    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1)
                hs = hs + "0" + stmp;
            else
                hs = hs + stmp;
            if (n < b.length - 1)
                hs = hs + ":";
        }
        return hs.toUpperCase();
    }


    /**
     * 验证字符串是否为空
     *
     * @param param
     * @return
     */
    public static boolean empty(String param) {
        return param == null || param.trim().length() < 1;
    }

    /**
     * 验证字符串是否非空
     *
     * @param param
     * @return
     */
    public static boolean isNotEmpty(String param) {
        return !empty(param);
    }

    // 65~90 97~122

    /**
     * 判断字符是否为字母
     *
     * @param c
     * @return
     */
    public static boolean isLetter(char c) {
        if (c >= 65 && c <= 90) {
            return true;
        }
        if (c >= 97 && c <= 122) {
            return true;
        }
        return false;
    }

    // 验证英文字母或数据
    public static boolean isLetterOrDigit(String str) {
        java.util.regex.Pattern p = null; // 正则表达式
        java.util.regex.Matcher m = null; // 操作的字符串
        boolean value = true;
        try {
            p = java.util.regex.Pattern.compile("[^0-9A-Za-z]");
            m = p.matcher(str);
            if (m.find()) {

                value = false;
            }
        } catch (Exception e) {
        }
        return value;
    }

    /**
     * 验证是否是小写字符串
     */
    @SuppressWarnings("unused")
    private static boolean isLowerLetter(String str) {
        java.util.regex.Pattern p = null; // 正则表达式
        java.util.regex.Matcher m = null; // 操作的字符串
        boolean value = true;
        try {
            p = java.util.regex.Pattern.compile("[^a-z]");
            m = p.matcher(str);
            if (m.find()) {
                value = false;
            }
        } catch (Exception e) {
        }
        return value;
    }

    /**
     * 判断一个字符串是否都为数字
     *
     * @param strNum
     * @return
     */
    public static boolean isDigit(String strNum) {
        return strNum.matches("[0-9]{1,}");
    }

    /**
     * 判断一个字符串是否都为数字
     *
     * @param strNum
     * @return
     */
    public static boolean isDigit2(String strNum) {
        Pattern pattern = Pattern.compile("[0-9]{1,}");
        Matcher matcher = pattern.matcher(strNum);
        return matcher.matches();
    }

    /**
     * 判断一个字符串是否都为数字(包含小数点)
     *
     * @param strNum
     * @return
     */
    public static boolean isDigit3(String strNum) {
        Pattern pattern = Pattern.compile("^([1-9][0-9]*(\\.[0-9]{1,2})?)|(0\\.[0-9]{1,2})?");
        Matcher matcher = pattern.matcher(strNum);
        return matcher.matches();
    }

    /**
     * 截取数字
     *
     * @param content
     * @return
     */
    public static String getNumbers(String content) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }

    /**
     * 截取非数字
     *
     * @param content
     * @return
     */
    public static String splitNotNumber(String content) {
        Pattern pattern = Pattern.compile("\\D+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }

    public static String encode(String str, String code) {
        try {
            return URLEncoder.encode(str, code);
        } catch (Exception ex) {
            ex.fillInStackTrace();
            return "";
        }
    }

    public static String decode(String str, String code) {
        try {
            return URLDecoder.decode(str, code);
        } catch (Exception ex) {
            ex.fillInStackTrace();
            return "";
        }
    }

    /**
     * 去除字符串两边空格 为空返回类空格
     *
     * @param param
     * @return
     */
    public static String nvl(String param) {
        return param != null ? param.trim() : "";
    }

    /**
     * 将String类型的数字转换成int类型 为空或非字符串数字时返回所传默认值d
     *
     * @param param
     * @param d
     * @return
     */
    public static int parseInt(String param, int d) {
        int i = d;
        try {
            i = Integer.parseInt(param);
        } catch (Exception e) {
        }
        return i;
    }

    /**
     * 字符串数字类型转换 为空或非数字 返回默认值0
     *
     * @param param
     * @return
     */
    public static int parseInt(String param) {
        return parseInt(param, 0);
    }

    /**
     * 字符串long类型数字转换 条件不符合时返回0L
     *
     * @param param
     * @return
     */
    public static long parseLong(String param) {
        long l = 0L;
        try {
            l = Long.parseLong(param);
        } catch (Exception e) {
        }
        return l;
    }

    /**
     * 字符串double类型数字转换 条件不符合时返回1.0
     *
     * @param param
     * @return
     */
    public static double parseDouble(String param) {
        return parseDouble(param, 1.0);
    }

    /**
     * 字符串double类型数字转换 条件不符合时返回所传默认值
     *
     * @param param
     * @param t
     * @return
     */
    public static double parseDouble(String param, double t) {
        double tmp = 0.0;
        try {
            tmp = Double.parseDouble(param.trim());
        } catch (Exception e) {
            tmp = t;
        }
        return tmp;
    }

    /**
     * @param param
     * @return
     */
    public static boolean parseBoolean(String param) {
        if (empty(param))
            return false;
        switch (param.charAt(0)) {
            case 49: // '1'
            case 84: // 'T'
            case 89: // 'Y'
            case 116: // 't'
            case 121: // 'y'
                return true;

        }
        return false;
    }

    /**
     * public static String replace(String mainString, String oldString, String
     * newString) { if(mainString == null) return null; int i =
     * mainString.lastIndexOf(oldString); if(i < 0) return mainString;
     * StringBuffer mainSb = new StringBuffer(mainString); for(; i >= 0; i =
     * mainString.lastIndexOf(oldString, i - 1)) mainSb.replace(i, i +
     * oldString.length(), newString);
     * <p/>
     * return mainSb.toString(); }
     */

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static final String[] split(String str, String delims) {
        StringTokenizer st = new StringTokenizer(str, delims);
        ArrayList list = new ArrayList();
        for (; st.hasMoreTokens(); list.add(st.nextToken()))
            ;
        return (String[]) list.toArray(new String[list.size()]);
    }

    public static String substring(String str, int length) {
        if (str == null)
            return null;

        if (str.length() > length)
            return (str.substring(0, length - 2) + "...");
        else
            return str;
    }

    public static boolean validateDouble(String str) throws RuntimeException {
        if (str == null)
            // throw new RuntimeException("null input");
            return false;
        char c;
        int k = 0;
        for (int i = 0, l = str.length(); i < l; i++) {
            c = str.charAt(i);
            if (!((c >= '0') && (c <= '9')))
                if (!(i == 0 && (c == '-' || c == '+')))
                    if (!(c == '.' && i < l - 1 && k < 1))
                        // throw new RuntimeException("invalid number");
                        return false;
                    else
                        k++;
        }
        return true;
    }

    public static boolean validateMobile(String str, boolean includeUnicom) {
        if (str == null || str.trim().equals(""))
            return false;
        str = str.trim();

        if (str.length() != 11 || !validateInt(str))
            return false;

        if (includeUnicom && (str.startsWith("130") || str.startsWith("133") || str.startsWith
                ("131")))
            return true;

        if (!(str.startsWith("139") || str.startsWith("138") || str.startsWith("137") || str
                .startsWith("136") || str
                .startsWith("135")))
            return false;
        return true;
    }

    public static boolean validateMobile(String str) {
        return validateMobile(str, false);
    }

    /**
     * 判断是否为手机号码
     *
     * @param mdn 手机号码
     * @return
     */
    public static boolean isMobileNumber(String mdn) {
        if (StringUtil.empty(mdn))
            return false;
        Pattern p = Pattern
                .compile("^((13[0,1,2,3,4,5,6,7,8,9])|(145,147)|(15[0,1,2,3,4,5,6,7,8,9])|(18[0," +
                        "1,2,3,4,5,6,7,8,9]))\\d{8}$");
        Matcher m = p.matcher(mdn);
        return m.matches();
    }

    /**
     * delete file
     *
     * @param fileName
     * @return -1 exception,1 success,0 false,2 there is no one directory of the
     * same name in system
     */
    public static int deleteFile(String fileName) {
        File file = null;
        int returnValue = -1;
        try {
            file = new File(fileName);
            if (file.exists())
                if (file.delete())
                    returnValue = 1;
                else
                    returnValue = 0;
            else
                returnValue = 2;

        } catch (Exception e) {
            e.printStackTrace();
            // System.out.println( "Exception:e=" + e.getMessage() );
        } finally {
            file = null;
            // return returnValue;
        }
        return returnValue;
    }

    public static String gbToIso(String s) throws UnsupportedEncodingException {
        return covertCode(s, "GB2312", "ISO8859-1");
    }

    public static String covertCode(String s, String code1, String code2) throws
            UnsupportedEncodingException {
        if (s == null)
            return null;
        else if (s.trim().equals(""))
            return "";
        else
            return new String(s.getBytes(code1), code2);
    }

    public static String transCode(String s0) throws UnsupportedEncodingException {
        if (s0 == null || s0.trim().equals(""))
            return null;
        else {
            s0 = s0.trim();
            return new String(s0.getBytes("GBK"), "ISO8859-1");
        }
    }

    /**
     * ISO8859-1编码转成UTF-8编码字符串
     *
     * @param value
     * @return
     */
    public static String isoToUTF8(String value) {
        try {
            return covertCode(value, "ISO8859-1", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 编码转换
     *
     * @param s
     * @return
     */
    public static String GBToUTF8(String s) {
        try {
            StringBuffer out = new StringBuffer("");
            byte[] bytes = s.getBytes("unicode");
            for (int i = 2; i < bytes.length - 1; i += 2) {
                out.append("\\u");
                String str = Integer.toHexString(bytes[i + 1] & 0xff);
                for (int j = str.length(); j < 2; j++) {
                    out.append("0");
                }
                out.append(str);
                String str1 = Integer.toHexString(bytes[i] & 0xff);
                for (int j = str1.length(); j < 2; j++) {
                    out.append("0");
                }

                out.append(str1);
            }
            return out.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 字符串组整组字符串替换
     *
     * @param obj
     * @param oldString
     * @param newString
     * @return
     */
    @SuppressWarnings("unused")
    public static final String[] replaceAll(String[] obj, String oldString, String newString) {
        if (obj == null) {
            return null;
        }
        int length = obj.length;
        String[] returnStr = new String[length];
        String str = null;
        for (int i = 0; i < length; i++) {
            returnStr[i] = replaceAll(obj[i], oldString, newString);
        }
        return returnStr;
    }

    /**
     * 字符串全文替换
     *
     * @param s0
     * @param oldstr
     * @param newstr
     * @return
     */
    public static String replaceAll(String s0, String oldstr, String newstr) {
        if (s0 == null || s0.trim().equals(""))
            return null;
        StringBuffer sb = new StringBuffer(s0);
        int begin = 0;
        // int from = 0;
        begin = s0.indexOf(oldstr);
        while (begin > -1) {
            sb = sb.replace(begin, begin + oldstr.length(), newstr);
            s0 = sb.toString();
            begin = s0.indexOf(oldstr, begin + newstr.length());
        }
        return s0;
    }

    /**
     * 字符换转换成html代码
     *
     * @param str
     * @return
     */
    public static String toHtml(String str) {
        String html = str;
        if (str == null || str.length() == 0) {
            return str;
        }
        html = replaceAll(html, "&", "&amp;");
        html = replaceAll(html, "<", "&lt;");
        html = replaceAll(html, ">", "&gt;");
        html = replaceAll(html, "\r\n", "\n");
        html = replaceAll(html, "\n", "<br>\n");
        html = replaceAll(html, "\t", "         ");
        html = replaceAll(html, " ", "&nbsp;");
        return html;
    }

    /**
     * 字符串替换
     *
     * @param line
     * @param oldString
     * @param newString
     * @return
     */
    public static final String replace(String line, String oldString, String newString) {
        if (line == null) {
            return null;
        }
        int i = 0;
        if ((i = line.indexOf(oldString, i)) >= 0) {
            char[] line2 = line.toCharArray();
            char[] newString2 = newString.toCharArray();
            int oLength = oldString.length();
            StringBuffer buf = new StringBuffer(line2.length);
            buf.append(line2, 0, i).append(newString2);
            i += oLength;
            int j = i;
            while ((i = line.indexOf(oldString, i)) > 0) {
                buf.append(line2, j, i - j).append(newString2);
                i += oLength;
                j = i;
            }
            buf.append(line2, j, line2.length - j);
            return buf.toString();
        }
        return line;
    }

    /**
     * 标签转义
     *
     * @param input
     * @return
     */
    public static final String escapeHTMLTags(String input) {
        // Check if the string is null or zero length -- if so, return
        // what was sent in.
        if (input == null || input.length() == 0) {
            return input;
        }
        // Use a StringBuffer in lieu of String concatenation -- it is
        // much more efficient this way.
        StringBuffer buf = new StringBuffer(input.length());
        char ch = ' ';
        for (int i = 0; i < input.length(); i++) {
            ch = input.charAt(i);
            if (ch == '<') {
                buf.append("&lt;");
            } else if (ch == '>') {
                buf.append("&gt;");
            } else {
                buf.append(ch);
            }
        }
        return buf.toString();
    }

    /**
     * Returns a random String of numbers and letters of the specified length.
     * The method uses the Random class that is built-in to Java which is
     * suitable for low to medium grade security uses. This means that the
     * output is only pseudo random, i.e., each number is mathematically
     * generated so is not truly random.
     * <p/>
     * <p/>
     * For every character in the returned String, there is an equal chance that
     * it will be a letter or number. If a letter, there is an equal chance that
     * it will be lower or upper case.
     * <p/>
     * <p/>
     * The specified length must be at least one. If not, the method will return
     * null.
     *
     * @param length the desired length of the random String to return.
     * @return a random String of numbers and letters of the specified length.
     */

    private static Random randGen = null;

    private static Object initLock = new Object();

    private static char[] numbersAndLetters = null;

    public static final String randomString(int length) {
        if (length < 1) {
            return null;
        }
        // Init of pseudo random number generator.
        if (randGen == null) {
            synchronized (initLock) {
                if (randGen == null) {
                    randGen = new Random();
                    // Also initialize the numbersAndLetters array
                    numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz" +
                            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ")
                            .toCharArray();
                }
            }
        }
        // Create a char buffer to put random letters and numbers in.
        char[] randBuffer = new char[length];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
        }
        return new String(randBuffer);
    }

    /**
     * 固定长度字符串截取
     *
     * @param content
     * @param i
     * @return
     */
    public static String getSubstring(String content, int i) {
        int varsize = 10;
        String strContent = content;
        if (strContent.length() < varsize + 1) {
            return strContent;
        } else {
            int max = (int) Math.ceil((double) strContent.length() / varsize);
            if (i < max - 1) {
                return strContent.substring(i * varsize, (i + 1) * varsize);
            } else {
                return strContent.substring(i * varsize);
            }
        }
    }

    /**
     * 日期转String
     *
     * @param pattern
     * @return
     */
    public static String now(String pattern) {
        return dateToString(new Date(), pattern);
    }

    public static String dateToString(Date date, String pattern) {
        if (date == null) {
            return "";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.format(date);
        }
    }

    public static synchronized String getNow() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return sdf.format(new Date());
    }

    public static synchronized String getShortNow() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(new Date());
    }

    /**
     * 根据字符串转换成日期
     *
     * @param strDate
     * @param pattern
     * @return
     */
    public static Date getDate(String strDate, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = simpleDateFormat.parse(strDate);
        } catch (ParseException e) {
        }
        return date;
    }

    /**
     * String转Date
     *
     * @param strDate
     * @param pattern
     * @return
     * @throws ParseException
     */
    public static java.sql.Date stringToDate(String strDate, String pattern) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Date date = simpleDateFormat.parse(strDate);
        long lngTime = date.getTime();
        return new java.sql.Date(lngTime);
    }

    /**
     * Util类型Date
     *
     * @param strDate
     * @param pattern
     * @return
     * @throws ParseException
     */
    public static java.util.Date stringToUtilDate(String strDate, String pattern) throws
            ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.parse(strDate);
    }

    /**
     * 严格意义上的2个日期相差天数
     *
     * @param fDate
     * @param oDate
     * @return
     */
    public static int getIntervalDays(Date fDate, Date oDate) {

        if (null == fDate || null == oDate) {

            return -1;

        }

        long intervalMilli = oDate.getTime() - fDate.getTime();

        return (int) (intervalMilli / (24 * 60 * 60 * 1000));

    }

    /**
     * 2个日期相差天数
     *
     * @param fDate
     * @param oDate
     * @return
     */
    public static int daysOfTwo(Date fDate, Date oDate) {

        Calendar aCalendar = Calendar.getInstance();

        aCalendar.setTime(fDate);

        int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);

        aCalendar.setTime(oDate);

        int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);

        return day2 - day1;

    }

    /**
     * html代码转换String类型字符串
     *
     * @param s
     * @return
     */
    public static String formatHTMLOutput(String s) {
        if (s == null)
            return null;

        if (s.trim().equals(""))
            return "";

        String formatStr;
        formatStr = replaceAll(s, " ", "&nbsp;");
        formatStr = replaceAll(formatStr, "\n", "<br />");

        return formatStr;
    }

    /*
     * 4舍5入 @param num 要调整的数 @param x 要保留的小数位
     */
    public static double myround(double num, int x) {
        BigDecimal b = new BigDecimal(num);
        return b.setScale(x, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

	/*
     * public static String getSubstring(String content,int i){ int varsize=10;
	 * String strContent=content; if(strContent.length()<varsize+1){ return
	 * strContent; }else{ int
	 * max=(int)Math.ceil((double)strContent.length()/varsize); if(i<max-1){
	 * return strContent.substring(i*varsize,(i+1)*varsize); }else{ return
	 * strContent.substring(i*varsize); } } }
	 */

    /**
     * liuqs
     *
     * @param param
     * @param d
     * @return
     */
    public static int parseLongInt(Long param, int d) {
        int i = d;
        try {
            i = Integer.parseInt(String.valueOf(param));
        } catch (Exception e) {
        }
        return i;
    }

    public static int parseLongInt(Long param) {
        return parseLongInt(param, 0);
    }

    public static String returnString(Object obj) {
        if (obj == null) {
            return "";
        } else {
            return obj.toString();
        }
    }

    /**
     * 修改敏感字符编码
     *
     * @param value
     * @return
     */
    public static String htmlEncode(String value) {
        if (value == null || value.isEmpty())
            return value;
        String re[][] = {{"<", "&lt;"}, {">", "&gt;"}, {"\"", "&quot;"}, {"\\′", "&acute;"},
                {"&", "&amp;"}};

        for (int i = 0; i < 4; i++) {
            value = value.replaceAll(re[i][0], re[i][1]);
        }
        return value;
    }

    /**
     * 防SQL注入
     *
     * @param str
     * @return
     */
    public static boolean sql_inj(String str) {
        String inj_str = "'|and|exec|insert|select|delete|update|count|*|%|chr|mid|master" +
                "|truncate|char|declare|;|or|-|+|,";
        String inj_stra[] = inj_str.split("|");
        for (int i = 0; i < inj_stra.length; i++) {
            if (str.indexOf(inj_stra[i]) >= 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取字符串值，判断null并且去除左右空格
     *
     * @param v
     * @param def
     * @return
     */
    public static String stringValue(String v, String def) {
        if (v == null || v.length() == 0)
            return def;
        return v.trim();
    }

    /***
     * 字符串替换
     *
     * @param str    要替换的字符串 例如: "这是一个{0}案例,告诉你是{1}使用的."
     * @param values 替换的值 例如: ["测试","怎么"]
     * @return 返回替换后的字符串 例如: "这是一个测试案例,告诉你是怎么使用的."
     */
    public static String format(String str, String... values) {
        if (values == null || values.length <= 0)
            return str;
        for (int i = 0; i < values.length; i++) {
            str = str.replace("{" + i + "}", values[i]);
        }
        return str;
    }

    /**
     * 生成流水号(订单)
     *
     * @return
     */
    public static String generateSerialNumber(String prefix) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String str = sdf.format(cal.getTime());
        long value = Long.parseLong(str) - 20140101000000000L; // 特定值
        return prefix + String.valueOf(value);
    }

    /**
     * 将字符串转为List 集合
     *
     * @param ids 例如:"1,2,3,4"
     * @return
     */
    public static List<Long> generateListInteger(String ids) {
        if (isNotEmpty(ids)) {
            String[] IDS = ids.split(",");
            List<Long> list = new ArrayList<Long>();
            for (String id : IDS) {
                if (isNotEmpty(id))
                    list.add(Long.parseLong(id));
            }
            return list;
        }
        return null;
    }

    /**
     * 分割字符串转换成List
     *
     * @param strings
     * @return
     */
    public static List<String> splitToStringList(String strings) {
        return splitToStringList(strings, ",");
    }

    /**
     * 分割字符串转换成List
     *
     * @param strings
     * @param regex   分隔符
     * @return
     */
    public static List<String> splitToStringList(String strings, String regex) {
        if (isNotEmpty(strings)) {
            String[] strArr = strings.split(regex);
            List<String> list = new ArrayList<String>();
            for (String str : strArr) {
                if (isNotEmpty(str))
                    list.add(str);
            }
            return list;
        }
        return null;
    }

    /**
     * 移除一个字符串最开始和结束的指定符号
     *
     * @param str
     * @param prefix
     * @return
     */
    public static String trim(String str, String prefix) {
        if (empty(str))
            return str;
        String newStr = str;
        if (str.startsWith(prefix)) {
            newStr = newStr.replaceFirst(prefix, "");
        }
        return newStr;
    }

    /**
     * 移除一个字符串最开始的指定符号
     *
     * @param str
     * @param prefix
     * @return
     */
    public static String trimEnd(String str, String prefix) {
        if (empty(str))
            return str;
        int preLen = prefix.length();
        String newStr = str;
        if (str.endsWith(prefix)) {
            newStr = newStr.substring(0, newStr.length() - preLen);
        }
        return newStr;
    }

    /**
     * 移除一个字符串结束的指定符号
     *
     * @param str
     * @param prefix
     * @return
     */
    public static String trimStart(String str, String prefix) {
        if (empty(str))
            return str;
        int preLen = prefix.length();
        String newStr = str;
        if (str.startsWith(prefix)) {
            newStr = newStr.replaceFirst(prefix, "");
        }
        if (str.endsWith(prefix)) {
            newStr = newStr.substring(0, newStr.length() - preLen);
        }
        return newStr;
    }

    /**
     * 检查申请退款的理由中有无包含 支付宝禁用的特殊字符
     *
     * @param str
     * @return
     * @author wenhua_Lee
     */
    public static boolean checkRefundReason(String str) {
        // 字符串为空 则不包特殊字符
        if (empty(str))
            return false;
        // 正则
        final Pattern pattern = Pattern.compile("[\\^\\|\\$\\#]");
        final Matcher mat = pattern.matcher(str);
        // 包含特殊字符返回true
        if (mat.find())
            return true;
        else
            return false;
    }

    /**
     * 随机生成6位数字 结算单号有调用
     *
     * @return
     */
    public static String generatorNo() {
        Random rd = new Random();
        int n = 0;
        while (n < 100000) {
            n = rd.nextInt(999999);
        }
        return String.valueOf(n);
    }

    /**
     * 字符串转换unicode
     */
    public static String string2Unicode(String str) {

        StringBuffer unicode = new StringBuffer();

        for (int i = 0; i < str.length(); i++) {

            // 取出每一个字符
            char c = str.charAt(i);

            // 转换为unicode
            unicode.append("\\u" + Integer.toHexString(c));
        }

        return unicode.toString();
    }

    /**
     * unicode 转字符串
     */
    public static String unicode2String(String unicode) {

        StringBuffer string = new StringBuffer();

        String[] hex = unicode.split("\\\\u");

        for (int i = 1; i < hex.length; i++) {

            // 转换出每一个代码点
            int data = Integer.parseInt(hex[i], 16);

            // 追加成string
            string.append((char) data);
        }

        return string.toString();
    }

    /**
     * 数组转字符串
     *
     * @param ary    [1,2,3]
     * @param :","
     * @return "1,2,3"
     */
    public static String arrayToString(String[] ary) {
        return arrayToString(ary, ",");
    }

    /**
     * 数组转字符串
     *
     * @param ary    [1,2,3]
     * @param prefix 例如:","
     * @return "1,2,3"
     */
    public static String arrayToString(String[] ary, String prefix) {
        if (ary == null || ary.length <= 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (String string : ary) {
            sb.append(prefix).append(string);
        }
        return sb.toString().replaceFirst(prefix, "");
    }

    /**
     * 获取中文星期几
     *
     * @param paramCalendar
     * @return
     */
    public static String getChineseWeekday(Calendar paramCalendar) {
        switch (paramCalendar.get(Calendar.DAY_OF_WEEK)) {
            case 2:
                return "星期一";
            case 3:
                return "星期二";
            case 4:
                return "星期三";
            case 5:
                return "星期四";
            case 6:
                return "星期五";
            case 7:
                return "星期六";
            case 1:
                return "星期日";
        }
        return "未知";
    }

    /**
     * 获取中文周几
     *
     * @param paramCalendar
     * @return
     */
    public static String getChineseWeekdayTwo(Calendar paramCalendar) {
        switch (paramCalendar.get(Calendar.DAY_OF_WEEK)) {
            case 2:
                return "周一";
            case 3:
                return "周二";
            case 4:
                return "周三";
            case 5:
                return "周四";
            case 6:
                return "周五";
            case 7:
                return "周六";
            case 1:
                return "周日";
        }
        return "未知";
    }

    /**
     * 指定长度截取并加...
     *
     * @param str
     * @param max
     * @return
     */
    public static String getSubStrDot(String str, int max) {
        if (empty(str)) {
            return str;
        }
        return str.length() <= max ? str : str.substring(0, max - 1) + "...";
    }

    /**
     * 获取全路径图片地址
     *
     * @param domain 域名
     * @param imgUrl 无域名的图片路径
     * @return
     */
    public static String getFullImageUrl(String domain, String imgUrl) {
        if (StringUtil.empty(imgUrl)) {
            return "";
        }
        if (imgUrl.contains("http")) {
            return imgUrl;
        }
        return domain.concat(imgUrl);
    }

    /**
     * 获取完整的带参数的url
     *
     * @param url      例如:http://www.baidu.com/
     * @param paramMap
     * @return 返回值 例如: http://www.baidu.com/?name=hello&pwd=XMSDX322ZSD
     */
    public static String getParamUrl(String url, Map<String, String> paramMap) {
        StringBuffer sb = new StringBuffer();
        if (paramMap == null || paramMap.isEmpty()) {
            return "";
        } else {
            for (String key : paramMap.keySet()) {
                String value = paramMap.get(key);
                if (sb.length() < 1) {
                    sb.append(key).append("=").append(value);
                } else {
                    sb.append("&").append(key).append("=").append(value);
                }
            }
            return url += "?" + sb.toString();
        }
    }

}
