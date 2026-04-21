package dev.bhaskar.elytrafix.listeners;

import dev.bhaskar.elytrafix.config.ConfigManager;
import dev.bhaskar.elytrafix.hooks.CombatLogXHook;
import dev.bhaskar.elytrafix.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ListenerGlide implements Listener {
    private static final long WINDOW_MS = 2000L;
    private static final int THRESHOLD = 3;

    private final ConfigManager config;
    private final CombatLogXHook hook;
    private final Map<UUID, Integer> attempts = new HashMap<>();
    private final Map<UUID, Long> lastAttempt = new HashMap<>();

    public ListenerGlide(ConfigManager config, CombatLogXHook hook) {
        this.config = config;
        this.hook = hook;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onToggleGlide(EntityToggleGlideEvent event) {
        if (!(event.getEntity() instanceof Player player) || !event.isGliding()) {
            return;
        }
        if (player.hasPermission("elytrafix.bypass") || !hook.isInCombat(player)) {
            return;
        }
        event.setCancelled(true);
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        long last = lastAttempt.getOrDefault(uuid, 0L);
        int count = (now - last <= WINDOW_MS) ? attempts.getOrDefault(uuid, 0) + 1 : 1;
        attempts.put(uuid, count);
        lastAttempt.put(uuid, now);

        if (count >= THRESHOLD) {
            ItemStack chest = player.getInventory().getChestplate();
            if (chest != null && chest.getType() == Material.ELYTRA) {
                player.getInventory().setChestplate(null);
                Map<Integer, ItemStack> leftovers = player.getInventory().addItem(chest);
                leftovers.values().forEach(item -> player.getWorld().dropItem(player.getLocation(), item));
            }
            attempts.remove(uuid);
            lastAttempt.remove(uuid);
        }

        MessageUtil.sendConfigured(player, config, config.getBlockedMessage());
    }
}
