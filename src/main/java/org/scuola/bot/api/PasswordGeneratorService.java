package org.scuola.bot.api;

import java.security.SecureRandom;

public class PasswordGeneratorService {

    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()-_=+[]{};:,.<>?";

    private static final String ALL = LOWER + UPPER + DIGITS + SYMBOLS;

    private static final SecureRandom random = new SecureRandom();

    public static String generate(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("La password deve avere almeno 8 caratteri");
        }

        StringBuilder sb = new StringBuilder();

        // Garantisce almeno un carattere per tipo
        sb.append(randomChar(LOWER));
        sb.append(randomChar(UPPER));
        sb.append(randomChar(DIGITS));
        sb.append(randomChar(SYMBOLS));

        // Riempie il resto
        for (int i = 4; i < length; i++) {
            sb.append(randomChar(ALL));
        }

        return shuffle(sb.toString());
    }

    private static char randomChar(String source) {
        return source.charAt(random.nextInt(source.length()));
    }

    // Mischia i caratteri
    private static String shuffle(String input) {
        char[] chars = input.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }
}
