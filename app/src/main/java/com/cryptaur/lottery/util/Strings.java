package com.cryptaur.lottery.util;

import android.content.Context;

import com.cryptaur.lottery.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Vector;

/**
 * String utilities.
 */
public final class Strings {
    private static String LINE_SEPARATOR;

    static {
        try {
            LINE_SEPARATOR = AccessController.doPrivileged(new PrivilegedAction<String>() {
                public String run() {
                    // the easy way
                    return System.getProperty("line.separator");
                }
            });

        } catch (Exception e) {
            try {
                // the harder way
                LINE_SEPARATOR = String.format("%n");
            } catch (Exception ef) {
                LINE_SEPARATOR = "\n";   // we're desperate use this...
            }
        }
    }

    public static String fromUTF8ByteArray(byte[] bytes) {
        int i = 0;
        int length = 0;

        while (i < bytes.length) {
            length++;
            if ((bytes[i] & 0xf0) == 0xf0) {
                // surrogate pair
                length++;
                i += 4;
            } else if ((bytes[i] & 0xe0) == 0xe0) {
                i += 3;
            } else if ((bytes[i] & 0xc0) == 0xc0) {
                i += 2;
            } else {
                i += 1;
            }
        }

        char[] cs = new char[length];

        i = 0;
        length = 0;

        while (i < bytes.length) {
            char ch;

            if ((bytes[i] & 0xf0) == 0xf0) {
                int codePoint = ((bytes[i] & 0x03) << 18) | ((bytes[i + 1] & 0x3F) << 12) | ((bytes[i + 2] & 0x3F) << 6) | (bytes[i + 3] & 0x3F);
                int U = codePoint - 0x10000;
                char W1 = (char) (0xD800 | (U >> 10));
                char W2 = (char) (0xDC00 | (U & 0x3FF));
                cs[length++] = W1;
                ch = W2;
                i += 4;
            } else if ((bytes[i] & 0xe0) == 0xe0) {
                ch = (char) (((bytes[i] & 0x0f) << 12)
                        | ((bytes[i + 1] & 0x3f) << 6) | (bytes[i + 2] & 0x3f));
                i += 3;
            } else if ((bytes[i] & 0xd0) == 0xd0) {
                ch = (char) (((bytes[i] & 0x1f) << 6) | (bytes[i + 1] & 0x3f));
                i += 2;
            } else if ((bytes[i] & 0xc0) == 0xc0) {
                ch = (char) (((bytes[i] & 0x1f) << 6) | (bytes[i + 1] & 0x3f));
                i += 2;
            } else {
                ch = (char) (bytes[i] & 0xff);
                i += 1;
            }

            cs[length++] = ch;
        }

        return new String(cs);
    }

    public static byte[] toUTF8ByteArray(String string) {
        return toUTF8ByteArray(string.toCharArray());
    }

    public static byte[] toUTF8ByteArray(char[] string) {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();

        try {
            toUTF8ByteArray(string, bOut);
        } catch (IOException e) {
            throw new IllegalStateException("cannot encode string to byte array!");
        }

        return bOut.toByteArray();
    }

    public static void toUTF8ByteArray(char[] string, OutputStream sOut)
            throws IOException {
        char[] c = string;
        int i = 0;

        while (i < c.length) {
            char ch = c[i];

            if (ch < 0x0080) {
                sOut.write(ch);
            } else if (ch < 0x0800) {
                sOut.write(0xc0 | (ch >> 6));
                sOut.write(0x80 | (ch & 0x3f));
            }
            // surrogate pair
            else if (ch >= 0xD800 && ch <= 0xDFFF) {
                // in error - can only happen, if the Java String class has a
                // bug.
                if (i + 1 >= c.length) {
                    throw new IllegalStateException("invalid UTF-16 codepoint");
                }
                char W1 = ch;
                ch = c[++i];
                char W2 = ch;
                // in error - can only happen, if the Java String class has a
                // bug.
                if (W1 > 0xDBFF) {
                    throw new IllegalStateException("invalid UTF-16 codepoint");
                }
                int codePoint = (((W1 & 0x03FF) << 10) | (W2 & 0x03FF)) + 0x10000;
                sOut.write(0xf0 | (codePoint >> 18));
                sOut.write(0x80 | ((codePoint >> 12) & 0x3F));
                sOut.write(0x80 | ((codePoint >> 6) & 0x3F));
                sOut.write(0x80 | (codePoint & 0x3F));
            } else {
                sOut.write(0xe0 | (ch >> 12));
                sOut.write(0x80 | ((ch >> 6) & 0x3F));
                sOut.write(0x80 | (ch & 0x3F));
            }

            i++;
        }
    }

    /**
     * A locale independent version of toUpperCase.
     *
     * @param string input to be converted
     * @return a US Ascii uppercase version
     */
    public static String toUpperCase(String string) {
        boolean changed = false;
        char[] chars = string.toCharArray();

        for (int i = 0; i != chars.length; i++) {
            char ch = chars[i];
            if ('a' <= ch && 'z' >= ch) {
                changed = true;
                chars[i] = (char) (ch - 'a' + 'A');
            }
        }

        if (changed) {
            return new String(chars);
        }

        return string;
    }

    /**
     * A locale independent version of toLowerCase.
     *
     * @param string input to be converted
     * @return a US ASCII lowercase version
     */
    public static String toLowerCase(String string) {
        boolean changed = false;
        char[] chars = string.toCharArray();

        for (int i = 0; i != chars.length; i++) {
            char ch = chars[i];
            if ('A' <= ch && 'Z' >= ch) {
                changed = true;
                chars[i] = (char) (ch - 'A' + 'a');
            }
        }

        if (changed) {
            return new String(chars);
        }

        return string;
    }

    public static byte[] toByteArray(char[] chars) {
        byte[] bytes = new byte[chars.length];

        for (int i = 0; i != bytes.length; i++) {
            bytes[i] = (byte) chars[i];
        }

        return bytes;
    }

    public static byte[] toByteArray(String string) {
        byte[] bytes = new byte[string.length()];

        for (int i = 0; i != bytes.length; i++) {
            char ch = string.charAt(i);

            bytes[i] = (byte) ch;
        }

        return bytes;
    }

    public static int toByteArray(String s, byte[] buf, int off) {
        int count = s.length();
        for (int i = 0; i < count; ++i) {
            char c = s.charAt(i);
            buf[off + i] = (byte) c;
        }
        return count;
    }

    /**
     * Convert an array of 8 bit characters into a string.
     *
     * @param bytes 8 bit characters.
     * @return resulting String.
     */
    public static String fromByteArray(byte[] bytes) {
        return new String(asCharArray(bytes));
    }

    /**
     * Do a simple conversion of an array of 8 bit characters into a string.
     *
     * @param bytes 8 bit characters.
     * @return resulting String.
     */
    public static char[] asCharArray(byte[] bytes) {
        char[] chars = new char[bytes.length];

        for (int i = 0; i != chars.length; i++) {
            chars[i] = (char) (bytes[i] & 0xff);
        }

        return chars;
    }

    public static String[] split(String input, char delimiter) {
        Vector v = new Vector();
        boolean moreTokens = true;
        String subString;

        while (moreTokens) {
            int tokenLocation = input.indexOf(delimiter);
            if (tokenLocation > 0) {
                subString = input.substring(0, tokenLocation);
                v.addElement(subString);
                input = input.substring(tokenLocation + 1);
            } else {
                moreTokens = false;
                v.addElement(input);
            }
        }

        String[] res = new String[v.size()];

        for (int i = 0; i != res.length; i++) {
            res[i] = (String) v.elementAt(i);
        }
        return res;
    }

    public static StringList newList() {
        return new StringListImpl();
    }

    public static String lineSeparator() {
        return LINE_SEPARATOR;
    }

    public static String toDecimalString(BigInteger value, int pointShift, int minDigitsAfterPoint, String point, String splitChar) {
        String valueStr = value.toString();
        if (valueStr.length() < pointShift + 1) {
            int prependZeroes = pointShift + 1 - valueStr.length();
            char[] charArray = new char[prependZeroes];
            Arrays.fill(charArray, '0');
            valueStr = new String(charArray) + valueStr;
        }
        String part1 = valueStr.substring(0, valueStr.length() - pointShift);
        StringBuilder result = new StringBuilder();
        if (splitChar == null) {
            result.append(part1);
        } else {
            int firstPart = part1.length() % 3;
            if (firstPart == 0)
                firstPart = 3;
            int parts = (part1.length() - 1) / 3 + 1;
            result.append(part1, 0, firstPart);
            for (int i = 1; i < parts; i++) {
                result.append(splitChar);
                result.append(part1, firstPart + 3 * (i - 1), firstPart + 3 * i);
            }
        }

        int minBackZeroPos = valueStr.length() - pointShift + minDigitsAfterPoint;
        int backZeroes = 0;
        for (int i = valueStr.length() - 1; i >= minBackZeroPos; i--) {
            if (valueStr.charAt(i) != '0') {
                break;
            }
            ++backZeroes;
        }

        if (backZeroes < pointShift) {
            result.append(point);
            result.append(valueStr, valueStr.length() - pointShift, valueStr.length() - backZeroes);
        }


        return result.toString();
    }

    public static String formatInterval(long seconds, Context context) {
        int secsToDraw = (int) (Math.abs(seconds) % 60);
        int minutesToDraw = (int) ((Math.abs(seconds) / 60) % 60);
        int hoursToDraw = (int) Math.abs(seconds / 60 / 60);

        int days = hoursToDraw / 24;
        hoursToDraw = hoursToDraw % 24;

        if (days == 0)
            return String.format(Locale.US, "%02d:%02d:%02d", hoursToDraw, minutesToDraw, secsToDraw);
        else {
            String str = context.getResources().getQuantityString(R.plurals.d_days, days, days);
            return String.format(Locale.US, "%s %02d:%02d:%02d", str, hoursToDraw, minutesToDraw, secsToDraw);
        }
    }

    private static class StringListImpl
            extends ArrayList<String>
            implements StringList {
        public boolean add(String s) {
            return super.add(s);
        }

        public String set(int index, String element) {
            return super.set(index, element);
        }

        public void add(int index, String element) {
            super.add(index, element);
        }

        public String[] toStringArray() {
            String[] strs = new String[this.size()];

            for (int i = 0; i != strs.length; i++) {
                strs[i] = this.get(i);
            }

            return strs;
        }

        public String[] toStringArray(int from, int to) {
            String[] strs = new String[to - from];

            for (int i = from; i != this.size() && i != to; i++) {
                strs[i - from] = this.get(i);
            }

            return strs;
        }
    }
}
