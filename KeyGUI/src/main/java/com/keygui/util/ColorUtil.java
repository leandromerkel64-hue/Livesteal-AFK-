package com.keygui.util;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Hilfsklasse fuer die Farbverarbeitung.
 *
 * <p>Unterstuetzt sowohl die klassischen {@code &}-Farbcodes als auch
 * Hex-Farben im Format {@code &#RRGGBB}. Hex-Farben werden in die von
 * Minecraft (ab 1.16) verstandene {@code §x§R§R§G§G§B§B}-Sequenz umgewandelt,
 * sodass keine externe Abhaengigkeit benoetigt wird.</p>
 */
public final class ColorUtil {

    /** Erkennt Hex-Farben im Format &#RRGGBB (case-insensitive). */
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([0-9a-fA-F]{6})");

    private ColorUtil() {
        // Utility-Klasse: keine Instanzen.
    }

    /**
     * Faerbt einen einzelnen Text ein.
     *
     * @param input der Eingabetext (darf {@code null} sein)
     * @return der eingefaerbte Text, niemals {@code null}
     */
    public static String colorize(String input) {
        if (input == null || input.isEmpty()) {
            return input == null ? "" : input;
        }

        // 1) Hex-Farben (&#RRGGBB) in die §x-Sequenz umwandeln.
        Matcher matcher = HEX_PATTERN.matcher(input);
        StringBuilder builder = new StringBuilder(input.length());
        while (matcher.find()) {
            matcher.appendReplacement(builder, Matcher.quoteReplacement(toLegacyHex(matcher.group(1))));
        }
        matcher.appendTail(builder);

        // 2) Klassische &-Codes uebersetzen.
        return ChatColor.translateAlternateColorCodes('&', builder.toString());
    }

    /**
     * Faerbt eine Liste von Texten ein.
     *
     * @param input die Eingabeliste (darf {@code null} sein)
     * @return eine neue Liste mit eingefaerbten Texten, niemals {@code null}
     */
    public static List<String> colorize(List<String> input) {
        List<String> result = new ArrayList<>();
        if (input == null) {
            return result;
        }
        for (String line : input) {
            result.add(colorize(line));
        }
        return result;
    }

    /**
     * Wandelt einen 6-stelligen Hex-Wert in die {@code §x§R§R§G§G§B§B}-Sequenz um.
     *
     * @param hex sechs Hex-Ziffern (ohne fuehrendes '#')
     * @return die entsprechende Legacy-Sequenz
     */
    private static String toLegacyHex(String hex) {
        StringBuilder legacy = new StringBuilder("\u00a7x");
        for (char c : hex.toCharArray()) {
            legacy.append('\u00a7').append(c);
        }
        return legacy.toString();
    }
}
