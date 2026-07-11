package com.keygui.hook;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Weiche Integration von PlaceholderAPI.
 *
 * <p>Ist PlaceholderAPI nicht installiert, werden Platzhalter einfach
 * unveraendert zurueckgegeben. Der Zugriff auf die PlaceholderAPI-Klassen
 * erfolgt erst nach einer Verfuegbarkeitspruefung, sodass das Plugin ohne
 * die Abhaengigkeit nicht abstuerzt.</p>
 */
public class PlaceholderHook {

    private final boolean available;

    public PlaceholderHook() {
        this.available = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    /**
     * @return {@code true}, wenn PlaceholderAPI verfuegbar ist
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * Loest alle PlaceholderAPI-Platzhalter in einem Text auf.
     *
     * @param player der Spielerkontext (darf {@code null} sein)
     * @param text   der Eingabetext
     * @return der aufgeloeste Text (oder der Originaltext, falls PAPI fehlt)
     */
    public String apply(OfflinePlayer player, String text) {
        if (text == null || text.isEmpty() || !available) {
            return text;
        }
        // Direkter API-Aufruf; die Klasse ist nur geladen, wenn das Plugin
        // vorhanden ist (siehe Verfuegbarkeitspruefung im Konstruktor).
        return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, text);
    }

    /**
     * Loest PlaceholderAPI-Platzhalter fuer eine ganze Liste auf.
     *
     * @param player der Spielerkontext (darf {@code null} sein)
     * @param lines  die Eingabezeilen
     * @return eine neue Liste mit aufgeloesten Zeilen
     */
    public List<String> apply(OfflinePlayer player, List<String> lines) {
        List<String> result = new ArrayList<>();
        if (lines == null) {
            return result;
        }
        if (!available) {
            result.addAll(lines);
            return result;
        }
        for (String line : lines) {
            result.add(apply(player, line));
        }
        return result;
    }
}
