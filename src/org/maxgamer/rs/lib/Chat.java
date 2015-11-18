package org.maxgamer.rs.lib;

import java.util.ArrayList;

/**
 * @author netherfoam
 */
public class Chat {
	private Chat() {} // Private Constructor

	public static String withPrefix(String s) {
		char[] vowels = { 'a', 'e', 'i', 'o', 'u' };
		for (char c : vowels)
			if (s.trim().startsWith("" + Character.toUpperCase(c)))
				return "an " + s;
		return "a " + s;
	}

	/**
	 * Removes excessive caps in the given message Formally, If length > 10 && 1/3rd of the message is CAPITAL LETTERS, this method lowercases the
	 * message and returns it. Else the original message is returned.
	 * 
	 * @param msg
	 *            the message
	 * @return the new message
	 */
	public static String capsBlock(String msg) {
		int l = msg.length();
		int u = 0;

		if (l > 10) {
			for (int i = 0; i < msg.length(); i++) {
				if (isUpper(msg.charAt(i))) {
					u++;
				}
			}

			if (u * 3 > l) { // At least a third is uppercase
				// Fix it
				msg = msg.toLowerCase();
			}
		}

		return msg;
	}

	/**
	 * Fixes the grammar for the given message
	 * 
	 * @param s
	 *            The message to fix
	 * @return The fixed message
	 */
	public static String grammar(String s) {
		char c = 0;
		int i = 0;

		boolean cap = true;
		char[] t = s.toCharArray();

		String word;
		for (i = 0; i < t.length; i++) {
			int w = i;
			while (w < t.length && t[w] != ' ') {
				w++;
			}
			word = new String(t, i, w - i);
			if (word.startsWith("http://")) {
				i = w;
				continue;
			}

			c = t[i];
			if (cap) {
				if (isLower(c)) {
					t[i] = (char) (c - 'a' + 'A');
					cap = false;
				} else if (isUpper(c) || isNumeric(c)) {
					// It's already capitalized
					cap = false;
				}
			} else if (c == '.' || c == '!' || c == '?') {
				cap = true;
			}
		}
		s = new String(t);

		c = s.charAt(s.length() - 1);
		if (isAlphaNumeric(c)) {
			s += "."; // Full stop
		}

		return s;
	}

	public static String[] lines(String text, int lineWidth) {
		String[] pieces = text.split("\\n");
		ArrayList<String> list = new ArrayList<String>(pieces.length * 2);

		for (String s : pieces) {
			while (s.length() > lineWidth) {
				int pos = lineWidth - 1;

				while (isWhitespace(s.charAt(pos)) == false) {
					pos--;
				}

				if (pos == 0) {
					// We couldn't split the line at all!
					list.add(s.substring(0, lineWidth));
					s = s.substring(lineWidth, s.length());
				} else {
					// We managed to split at white space. Store the new string
					// And cut the piece off the front
					list.add(s.substring(0, pos));
					s = s.substring(pos, s.length());
				}
			}

			list.add(s);
		}

		return list.toArray(new String[list.size()]);
	}

	private static boolean isLower(char c) {
		return c >= 'a' && c <= 'z';
	}

	private static boolean isUpper(char c) {
		return c >= 'A' && c <= 'Z';
	}

	private static boolean isAlpha(char c) {
		return isLower(c) || isUpper(c);
	}

	private static boolean isNumeric(char c) {
		return c >= '0' && c <= '9';
	}

	private static boolean isAlphaNumeric(char c) {
		if (isAlpha(c) || isNumeric(c)) {
			return true;
		}
		return false;
	}

	private static boolean isWhitespace(char c) {
		if (c == ' ')
			return true;
		if (c == '\t')
			return true;
		if (c == '\n')
			return true;
		if (c == '\r')
			return true;
		return false;
	}
}