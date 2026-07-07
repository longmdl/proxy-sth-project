package mdl.proxysthproject.util;

public class NameMaskingUtil {

    /**
     * Masks the middle words of a name.
     * - Single word: show fully
     * - Exactly two words: show first char of each
     * - More than two words: mask all middle words with ***
     */
    public static String maskName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return fullName;
        }

        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0];
        }

        if (parts.length == 2) {
            return parts[0].charAt(0) + "* " + parts[1].charAt(0) + "*";
        }

        StringBuilder masked = new StringBuilder(parts[0]);
        masked.append(" *** ");
        masked.append(parts[parts.length - 1]);
        return masked.toString();
    }
}
