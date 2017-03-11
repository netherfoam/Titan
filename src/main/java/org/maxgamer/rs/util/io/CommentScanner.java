package org.maxgamer.rs.util.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A scanner which ignores all lines that contain standard java comments.
 * This class skips over any text on a single line preceded by a //.
 * It also skips any text contained in a /* block. Any blank lines are
 * also stripped from the input, so that only lines with uncommented
 * text are returned.
 */
public class CommentScanner {
    private static final String[] COMMENTS_LINE = {"#", "//"};
    private static final String[] COMMENTS_BLOCK_START = {"/*"};
    private static final String[] COMMENTS_BLOCK_END = {"*/"};
    //The source to read from
    private char[] buffer;
    private int mark = 0;
    private int index = 0;

    /**
     * Creates a new CommentScanner object.
     *
     * @param f The file to read from
     * @throws IOException
     */
    public CommentScanner(File f) throws IOException {
        this(new FileInputStream(f));
    }

    public CommentScanner(InputStream in) throws IOException {
        if (in == null) {
            throw new NullPointerException("InputStream may not be null");
        }
        this.buffer = new char[in.available()];

        int b;
        while ((b = in.read()) >= 0) {
            this.buffer[index++] = (char) b;
        }
        index = 0;
        in.close();
    }

    /**
     * Returns true if we have available input, false otherwise.
     *
     * @return true if we have available input, false otherwise.
     */
    public boolean hasNextLine() {
        mark = index;

        try {
            readLine();
            index = mark;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Reads the next line of uncommented input.
     *
     * @return the next line of uncommented input
     * @throws IndexOutOfBoundsException if we have no more input available
     */
    public String readLine() {
        StringBuilder sb = new StringBuilder();
        try {
            while (true) {
                sb.append(readRawLine());
                //We've hit the new line.
                //Now we strip comments.

                int index;
                for (String t : COMMENTS_LINE) {
                    index = sb.indexOf(t);
                    if (index >= 0) {
                        return sb.substring(0, index) + System.getProperty("line.separator");
                    }
                }

                for (int i = 0; i < COMMENTS_BLOCK_START.length; i++) {
                    index = sb.indexOf(COMMENTS_BLOCK_START[i]);
                    if (index >= 0) {
                        int end;
                        while ((end = sb.indexOf(COMMENTS_BLOCK_END[i])) < 0) {
                            sb.append(readRawLine());
                        }

                        sb.replace(index, end + COMMENTS_BLOCK_END[i].length(), "");
                    }
                }

                return sb.toString();
            }
        } catch (Exception e) {
            throw new IndexOutOfBoundsException(e.getMessage());
        }
    }

    /**
     * Reads a raw line, excluding the \n character
     */
    private StringBuilder readRawLine() throws IndexOutOfBoundsException {
        StringBuilder sb = new StringBuilder();
        char c;
        while (true) {
            c = buffer[index++];

            if (c == '\n') {
                // Exclude newline
                return sb;
            }

            sb.append(c);

            if (index >= buffer.length) {
                // Include last char
                return sb;
            }
        }
    }
}