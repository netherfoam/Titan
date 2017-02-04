package org.maxgamer.rs.structure;

import org.maxgamer.rs.util.io.ByteReader;
import org.maxgamer.rs.util.io.ByteWriter;

import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * @author netherfoam
 */
public class Util {
    private static Pattern IP_PATTERN = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    private static HashSet<String> yes = new HashSet<>();
    private static HashSet<String> no = new HashSet<>();

    static {
        yes.add("yes");
        yes.add("true");
        yes.add("on");
        yes.add("enable");
        yes.add("1");
        no.add("no");
        no.add("false");
        no.add("off");
        no.add("disable");
        no.add("0");
    }

    /**
     * Returns true if the given string matches *.*.*.*
     *
     * @param s The string which may or may not be an ip
     * @return true if the given string matches *.*.*.*
     */
    public static boolean isIP(String s) {
        return IP_PATTERN.matcher(s).matches();
    }

    public static String readCString(ByteReader src) throws IOException {
        StringBuilder sb = new StringBuilder();
        byte b;
        while ((b = src.readByte()) != 0) {
            sb.append((char) b);
        }
        return sb.toString();
    }

    /**
     * Converts the given user input string into a boolean. Such as if a player
     * enters "yes", "enable", "disable", it converts them to true, true, false
     * respectively.
     *
     * @param response The string the user has sent
     * @return The boolean equivilant
     * @throws ParseException if the given string is not a valid answer...
     *                        Example: "bananas" is not a boolean!
     */
    public static boolean parseBoolean(String response) throws ParseException {
        response = response.toLowerCase();
        if (yes.contains(response)) return true;
        if (no.contains(response)) return false;
        throw new ParseException("Invalid boolean: " + response, 0);
    }

    public static String escape(String s) {
        if (s == null) return s;
        return s.replaceAll("\"", "\\\"");
    }

    public static void printCSV(PrintStream ps, Object... objects) {
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] instanceof String) {
                objects[i] = "\"" + escape((String) objects[i]) + "\"";
            } else if (objects[i] == null) {
                objects[i] = "\"" + escape(String.valueOf(objects[i])) + "\"";
            }
        }

        for (int i = 0; i < objects.length - 1; i++) {
            if (objects[i] instanceof Boolean) {
                objects[i] = ((Boolean) objects[i]) ? 1 : 0;
            }
            ps.print(String.valueOf(objects[i]) + ", ");
        }
        ps.print(String.valueOf(objects[objects.length - 1]));
    }

    /**
     * Returns the given String, but wrapped with the given string joiner.
     *
     * @param str           The string to wrap
     * @param wrapLength    The number of characters in each column to wrap
     * @param join          The string we want to join them with, eg. \n or a HTML br
     *                      tag.
     * @param wrapLongWords True if we should force long words to wrap, false if
     *                      you want them to ignore the limit
     * @return The string, wrapped!
     */
    public static String wrap(final String str, int wrapLength, String join, final boolean wrapLongWords) {
        if (str == null) {
            return null;
        }

        if (wrapLength < 1) {
            wrapLength = 1;
        }
        final int inputLineLength = str.length();
        int offset = 0;
        final StringBuilder wrappedLine = new StringBuilder(inputLineLength + 32);

        while (inputLineLength - offset > wrapLength) {
            if (str.charAt(offset) == ' ') {
                offset++;
                continue;
            }
            int spaceToWrapAt = str.lastIndexOf(' ', wrapLength + offset);

            if (spaceToWrapAt >= offset) {
                // normal case
                wrappedLine.append(str.substring(offset, spaceToWrapAt));
                wrappedLine.append(join);
                offset = spaceToWrapAt + 1;

            } else {
                // really long word or URL
                if (wrapLongWords) {
                    // wrap really long word one line at a time
                    wrappedLine.append(str.substring(offset, wrapLength + offset));
                    wrappedLine.append(join);
                    offset += wrapLength;
                } else {
                    // do not wrap really long word, just extend beyond limit
                    spaceToWrapAt = str.indexOf(' ', wrapLength + offset);
                    if (spaceToWrapAt >= 0) {
                        wrappedLine.append(str.substring(offset, spaceToWrapAt));
                        wrappedLine.append(join);
                        offset = spaceToWrapAt + 1;
                    } else {
                        wrappedLine.append(str.substring(offset));
                        offset = inputLineLength;
                    }
                }
            }
        }

        // Whatever is left in line is short enough to just pass through
        wrappedLine.append(str.substring(offset));

        return wrappedLine.toString();
    }

    /**
     * Converts the given IP address into a number. This works because IP
     * addresses are 4 bytes, and an int is 4 bytes.
     *
     * @param ipAddress the IP string eg 192.168.2.1
     * @return the int representation
     */
    public static int IPAddressToNumber(String ipAddress) {
        StringTokenizer st = new StringTokenizer(ipAddress, ".");
        int[] ip = new int[4];
        int i = 0;
        while (st.hasMoreTokens()) {
            ip[i++] = Integer.parseInt(st.nextToken());
        }
        return ((ip[0] << 24) | (ip[1] << 16) | (ip[2] << 8) | (ip[3]));
    }

    public static int packGJString2(int position, byte[] buffer, String string) {
        int length = string.length();
        int offset = position;
        for (int i = 0; length > i; i++) {
            int character = string.charAt(i);
            if (character > 127) {
                if (character > 2047) {
                    buffer[offset++] = (byte) ((character | 919275) >> 12);
                    buffer[offset++] = (byte) (128 | ((character >> 6) & 63));
                    buffer[offset++] = (byte) (128 | (character & 63));
                } else {
                    buffer[offset++] = (byte) ((character | 12309) >> 6);
                    buffer[offset++] = (byte) (128 | (character & 63));
                }
            } else buffer[offset++] = (byte) character;
        }
        return offset - position;
    }

    public static void putGJString2(ByteWriter b, String string) throws IOException {
        byte[] packed = new byte[256];
        int length = Util.packGJString2(0, packed, string);
        b.writeByte((byte) 0);
        b.write(packed, 0, length);
        b.writeByte((byte) 0);
    }

    public static String toString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        sb.append("Len: ").append(data.length);
        sb.append("[").append(String.format("%X", data[0]));
        for (int i = 1; i < data.length; i++) {
            sb.append(String.format(", %X", data[i]));
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Prepares this time as a human readable duration.
     *
     * @return The time since epoch start, in human readable format (x years, y
     * months, n hours, etc)
     */
    public static String toDuration(long time) {
        if (time <= 0) return "Never";

        time = (long) Math.ceil(time / 1000.0); //Work in seconds.
        StringBuilder sb = new StringBuilder(40);

        if (time / 31449600 > 0) {
            //Years
            long years = time / 31449600;
            if (years > 100) return "Never";

            sb.append(years).append(years == 1 ? " year " : " years ");
            time -= years * 31449600;
        }
        if (time / 2620800 > 0) {
            //Months
            long months = time / 2620800;
            sb.append(months).append(months == 1 ? " month " : " months ");
            time -= months * 2620800;
        }
        if (time / 604800 > 0) {
            //Weeks
            long weeks = time / 604800;
            sb.append(weeks).append(weeks == 1 ? " week " : " weeks ");
            time -= weeks * 604800;
        }
        if (time / 86400 > 0) {
            //Days
            long days = time / 86400;
            sb.append(days).append(days == 1 ? " day " : " days ");
            time -= days * 86400;
        }

        if (time / 3600 > 0) {
            //Hours
            long hours = time / 3600;
            sb.append(hours).append(hours == 1 ? " hour " : " hours ");
            time -= hours * 3600;
        }

        if (time / 60 > 0) {
            //Minutes
            long minutes = time / 60;
            sb.append(minutes).append(minutes == 1 ? " minute " : " minutes ");
            time -= minutes * 60;
        }

        if (time > 0) {
            //Seconds
            sb.append(time).append(time == 1 ? " second " : " seconds ");
        }

        if (sb.length() > 1) {
            sb.replace(sb.length() - 1, sb.length(), "");
        } else {
            sb = new StringBuilder("N/A");
        }
        return sb.toString();
    }

    public static String formatNumber(double number) {
        return String.format("%,.0f", number);
    }
}