package dev.bhaskar.elytrafix.listeners;

import com.github.sirblobman.combatlogx.api.event.PlayerTagEvent;
import dev.bhaskar.elytrafix.config.ConfigManager;
import dev.bhaskar.elytrafix.util.DropProtectionManager;
import dev.bhaskar.elytrafix.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public final class ListenerCombatTag implements Listener {
    private final ConfigManager config;
    private final DropProtectionManager dropProtectionManager;

    public ListenerCombatTag(ConfigManager config, DropProtectionManager dropProtectionManager) {
        this.config = config;
        this.dropProtectionManager = dropProtectionManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerTag(PlayerTagEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("elytrafix.bypass")) {
            return;
        }

        ItemStack chest = player.getInventory().getChestplate();
        if (chest == null || chest.getType() != Material.ELYTRA) {
            return;
        }

        player.getInventory().setChestplate(null);

        int slot = player.getInventory().firstEmpty();
        if (slot != -1) {
            player.getInventory().setItem(slot, chest);
            MessageUtil.sendConfigured(player, config, config.getElytraRemovedToInventoryMessage());
            return;
        }

        Item dropped = player.getWorld().dropItem(player.getLocation(), chest);
        dropProtectionManager.protect(dropped, player.getUniqueId());
        MessageUtil.sendConfigured(player, config, config.getElytraDroppedMessage());
    }
}
