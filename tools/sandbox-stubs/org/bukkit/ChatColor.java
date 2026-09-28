package org.bukkit;
public enum ChatColor {
    BLACK, DARK_BLUE, DARK_GREEN, DARK_AQUA, DARK_RED, DARK_PURPLE, GOLD, GRAY,
    DARK_GRAY, BLUE, GREEN, AQUA, RED, LIGHT_PURPLE, YELLOW, WHITE, RESET, MAGIC, BOLD,
    STRIKETHROUGH, UNDERLINE, ITALIC;

    public static final char COLOR_CHAR = '\u00a7';

    public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
        throw new UnsupportedOperationException("stub");
    }
}
