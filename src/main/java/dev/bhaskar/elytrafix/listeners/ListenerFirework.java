package dev.bhaskar.elytrafix.listeners;

import dev.bhaskar.elytrafix.config.ConfigManager;
import dev.bhaskar.elytrafix.hooks.CombatLogXHook;
import dev.bhaskar.elytrafix.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

public final class ListenerFirework implements Listener {
    private final ConfigManager config;
    private final CombatLogXHook hook;

    public ListenerFirework(ConfigManager config, CombatLogXHook hook) {
        this.config = config;
        this.hook = hook;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        if (!player.isGliding() || player.hasPermission("elytrafix.bypass") || !hook.isInCombat(player)) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.FIREWORK_ROCKET) {
            return;
        }

        if (config.isBlockFireworks()) {
            event.setCancelled(true);
            MessageUtil.sendConfigured(player, config, config.getBlockedMessage());
            return;
        }

        int limit = config.getFireworkPowerLimit();
        if (limit < 1) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta instanceof FireworkMeta fireworkMeta && fireworkMeta.getPower() > limit) {
            event.setCancelled(true);
            MessageUtil.sendConfigured(player, config, config.getBlockedMessage());
        }
    }
}
