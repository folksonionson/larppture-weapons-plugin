package dev.larppture.weapons;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tiny per-player, per-ability cooldown bookkeeping (millisecond timestamps).
 */
public class Cooldowns {

    private final Map<String, Map<UUID, Long>> stamps = new HashMap<String, Map<UUID, Long>>();

    private Map<UUID, Long> bucket(String ability) {
        Map<UUID, Long> map = this.stamps.get(ability);
        if (map == null) {
            map = new HashMap<UUID, Long>();
            this.stamps.put(ability, map);
        }
        return map;
    }

    /** @return true when the ability is ready to fire (and stamps it). */
    public boolean tryUse(UUID player, String ability, double cooldownSeconds) {
        long now = System.currentTimeMillis();
        Map<UUID, Long> map = bucket(ability);
        Long last = map.get(player);
        if (cooldownSeconds <= 0D) {
            return true;
        }
        if (last != null && now - last.longValue() < (long) (cooldownSeconds * 1000D)) {
            return false;
        }
        map.put(player, Long.valueOf(now));
        return true;
    }

    /** @return remaining cooldown in seconds, 0 when ready. */
    public double remainingSeconds(UUID player, String ability, double cooldownSeconds) {
        Map<UUID, Long> map = this.stamps.get(ability);
        if (map == null || cooldownSeconds <= 0D) {
            return 0D;
        }
        Long last = map.get(player);
        if (last == null) {
            return 0D;
        }
        double left = cooldownSeconds - (System.currentTimeMillis() - last.longValue()) / 1000D;
        return left < 0D ? 0D : left;
    }
}
