package dev.bhaskar.elytrafix.hooks;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class CombatHookManager {
    private final List<CombatStatusHook> hooks;

    public CombatHookManager(List<CombatStatusHook> hooks) {
        this.hooks = new ArrayList<>(hooks);
    }

    public boolean hasAnyAvailableHook() {
        return hooks.stream().anyMatch(CombatStatusHook::isAvailable);
    }

    public String getAvailableHookNames() {
        return hooks.stream()
                .filter(CombatStatusHook::isAvailable)
                .map(CombatStatusHook::getHookName)
                .collect(Collectors.joining(", "));
    }

    public boolean isInCombat(Player player) {
        for (CombatStatusHook hook : hooks) {
            if (hook.isAvailable() && hook.isInCombat(player)) {
                return true;
            }
        }
        return false;
    }
}
