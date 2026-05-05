package dev.bhaskar.elytrafix.listeners;

import dev.bhaskar.elytrafix.config.ConfigManager;
import dev.bhaskar.elytrafix.util.DropProtectionManager;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

import java.util.UUID;

public final class ListenerPickup implements Listener {
    private final ConfigManager config;
    private final DropProtectionManager dropProtectionManager;

    public ListenerPickup(ConfigManager config, DropProtectionManager dropProtectionManager) {
        this.config = config;
        this.dropProtectionManager = dropProtectionManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        Item item = event.getItem();
        UUID owner = dropProtectionManager.getOwner(item);
        if (owner == null) {
            return;
        }

        if (dropProtectionManager.isProtected(item, config.getPickupProtectionSeconds())) {
            if (!owner.equals(player.getUniqueId())) {
                event.setCancelled(true);
            }
            return;
        }

        dropProtectionManager.unprotect(item);
    }
}
