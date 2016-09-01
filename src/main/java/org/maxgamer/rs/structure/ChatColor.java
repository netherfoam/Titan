package org.maxgamer.rs.structure;

/**
 * @author netherfoam
 */
public enum ChatColor implements CharSequence {

    BLACK("000000"),

    RED("FF0000"), GREEN("00FF00"), BLUE("0000FF"),

    PURPLE("FF00FF"), CYAN("00FFFF"), YELLOW("FFFF00"),

    // Specials
    LIGHT_BLUE("00B6ED"), DARK_GREEN("00AD20"), DARK_PURPLE("8900D9"), DARK_RED("A80000"), ORANGE("FFAE00"), BROWN("805C0E"), PINK("FF69B4"),

    GRAY("#7F7F7F"), WHITE("FFFFFF");

    private String text;

    private ChatColor(String text) {
        this.text = "<col=" + text + ">";
    }
	
    public static String get(int r, int g, int b) {
        return String.format("<col=%X%X%X>", r, g, b);
    }

    @Override
    public char charAt(int index) {
        return text.charAt(index);
    }

    @Override
    public int length() {
        return text.length();
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return text.subSequence(start, end);
    }

    @Override
    public String toString() {
        return text;
    }
}