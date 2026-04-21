package dev.bhaskar.elytrafix.hooks;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class CombatLogXHook {
    public boolean isAvailable() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("CombatLogX");
        return plugin instanceof ICombatLogX && plugin.isEnabled();
    }

    public boolean isInCombat(Player player) {
        Plugin clx = Bukkit.getPluginManager().getPlugin("CombatLogX");
        if (!(clx instanceof ICombatLogX combatLogX) || !clx.isEnabled()) {
            return false;
        }
        ICombatManager manager = combatLogX.getCombatManager();
        return manager != null && manager.isInCombat(player);
    }
}
