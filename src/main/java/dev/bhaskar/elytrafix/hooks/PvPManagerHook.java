package dev.bhaskar.elytrafix.hooks;

import me.NoChance.PvPManager.PvPManager;
import me.NoChance.PvPManager.Managers.PlayerHandler;
import me.NoChance.PvPManager.Player;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public final class PvPManagerHook implements CombatStatusHook {
    @Override
    public boolean isAvailable() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("PvPManager");
        return plugin != null && plugin.isEnabled();
    }

    @Override
    public boolean isInCombat(org.bukkit.entity.Player player) {
        if (!isAvailable()) {
            return false;
        }
        PlayerHandler handler = PvPManager.getPlayerHandler();
        if (handler == null) {
            return false;
        }
        Player pvpPlayer = handler.get(player);
        return pvpPlayer != null && pvpPlayer.isInCombat();
    }

    @Override
    public String getHookName() {
        return "PvPManager";
    }
}
