package dev.bhaskar.elytrafix.hooks;

import org.bukkit.entity.Player;

public interface CombatStatusHook {
    boolean isAvailable();

    boolean isInCombat(Player player);

    String getHookName();
}
