package com.keygui.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Verwaltet spielerbezogene Cooldowns fuer die Key-Vergabe.
 *
 * <p>Die Implementierung ist bewusst leichtgewichtig und haelt nur den
 * Zeitpunkt der letzten Aktion pro Spieler-UUID vor.</p>
 */
public class CooldownManager {

    /** Zeitpunkt (in Millisekunden), zu dem der Cooldown des Spielers endet. */
    private final Map<UUID, Long> expiryTimes = new HashMap<>();

    /**
     * Prueft, ob fuer den Spieler aktuell ein Cooldown aktiv ist.
     *
     * @param playerId die UUID des Spielers
     * @return {@code true}, wenn der Spieler noch warten muss
     */
    public boolean isOnCooldown(UUID playerId) {
        Long expiry = expiryTimes.get(playerId);
        if (expiry == null) {
            return false;
        }
        if (System.currentTimeMillis() >= expiry) {
            // Cooldown abgelaufen -> aufraeumen.
            expiryTimes.remove(playerId);
            return false;
        }
        return true;
    }

    /**
     * Liefert die verbleibende Cooldown-Zeit in ganzen Sekunden (aufgerundet).
     *
     * @param playerId die UUID des Spielers
     * @return verbleibende Sekunden, mindestens 0
     */
    public long getRemainingSeconds(UUID playerId) {
        Long expiry = expiryTimes.get(playerId);
        if (expiry == null) {
            return 0L;
        }
        long remainingMs = expiry - System.currentTimeMillis();
        if (remainingMs <= 0L) {
            return 0L;
        }
        return (remainingMs + 999L) / 1000L;
    }

    /**
     * Startet den Cooldown fuer einen Spieler.
     *
     * @param playerId       die UUID des Spielers
     * @param cooldownSeconds die Dauer in Sekunden
     */
    public void applyCooldown(UUID playerId, int cooldownSeconds) {
        if (cooldownSeconds <= 0) {
            return;
        }
        expiryTimes.put(playerId, System.currentTimeMillis() + cooldownSeconds * 1000L);
    }

    /**
     * Entfernt einen ggf. gespeicherten Cooldown (z. B. beim Logout).
     *
     * @param playerId die UUID des Spielers
     */
    public void clear(UUID playerId) {
        expiryTimes.remove(playerId);
    }
}
