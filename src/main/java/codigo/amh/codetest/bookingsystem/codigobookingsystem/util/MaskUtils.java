package codigo.amh.codetest.bookingsystem.codigobookingsystem.util;

public class MaskUtils {

    private static final char DEFAULT_MASK_CHAR = '*';
    private static final int DEFAULT_VISIBLE_START = 1;
    private static final int DEFAULT_VISIBLE_END = 1;

    private MaskUtils() {
        // Prevent instantiation
    }

    public static String mask(String input) {
        if (input == null) return null;
        if (input.isEmpty()) return "";

        int length = input.length();
        int visibleStart = Math.max(0, DEFAULT_VISIBLE_START);
        int visibleEnd = Math.max(0, DEFAULT_VISIBLE_END);

        if (length <= visibleStart + visibleEnd) {
            return String.valueOf(DEFAULT_MASK_CHAR).repeat(length);
        }

        return input.substring(0, visibleStart)
                + String.valueOf(DEFAULT_MASK_CHAR).repeat(length - visibleStart - visibleEnd)
                + input.substring(length - visibleEnd);
    }
}
