package dev.bhaskar.elytrafix.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

public final class PvPManagerHook implements CombatStatusHook {
    @Override
    public boolean isAvailable() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("PvPManager");
        return plugin != null && plugin.isEnabled();
    }

    @Override
    public boolean isInCombat(Player player) {
        if (!isAvailable()) {
            return false;
        }
        try {
            Class<?> combatPlayerClass = Class.forName("me.chancesd.pvpmanager.player.CombatPlayer");
            Method getMethod = combatPlayerClass.getMethod("get", Player.class);
            Object combatPlayer = getMethod.invoke(null, player);
            if (combatPlayer == null) {
                return false;
            }
            Method inCombatMethod = combatPlayerClass.getMethod("isInCombat");
            Object result = inCombatMethod.invoke(combatPlayer);
            return result instanceof Boolean && (Boolean) result;
        } catch (ReflectiveOperationException ignored) {
            return false;
        }
    }

    @Override
    public String getHookName() {
        return "PvPManager";
    }
}
