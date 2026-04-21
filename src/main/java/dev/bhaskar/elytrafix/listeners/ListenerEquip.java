package dev.bhaskar.elytrafix.listeners;

import dev.bhaskar.elytrafix.config.ConfigManager;
import dev.bhaskar.elytrafix.hooks.CombatLogXHook;
import dev.bhaskar.elytrafix.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public final class ListenerEquip implements Listener {
    private static final int PLAYER_CHEST_SLOT = 38;

    private final ConfigManager config;
    private final CombatLogXHook hook;

    public ListenerEquip(ConfigManager config, CombatLogXHook hook) {
        this.config = config;
        this.hook = hook;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (player.hasPermission("elytrafix.bypass") || !hook.isInCombat(player)) {
            return;
        }

        ItemStack current = event.getCurrentItem();
        ItemStack cursor = event.getCursor();
        boolean placingToChest = event.getRawSlot() == PLAYER_CHEST_SLOT
                && isElytra(cursor);

        boolean shiftEquipping = event.isShiftClick()
                && isElytra(current)
                && event.getClickedInventory() != null
                && event.getClickedInventory().getType() != InventoryType.PLAYER
                && player.getInventory().getChestplate() == null;

        boolean hotbarSwap = event.getClick() == ClickType.NUMBER_KEY
                && event.getRawSlot() == PLAYER_CHEST_SLOT
                && isElytra(player.getInventory().getItem(event.getHotbarButton()));

        boolean moveToOther = event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY
                && isElytra(current)
                && player.getInventory().getChestplate() == null;

        if (placingToChest || shiftEquipping || hotbarSwap || moveToOther) {
            event.setCancelled(true);
            MessageUtil.sendConfigured(player, config, config.getBlockedMessage());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (player.hasPermission("elytrafix.bypass") || !hook.isInCombat(player)) {
            return;
        }
        if (!isElytra(event.getOldCursor()) && !isElytra(event.getCursor())) {
            return;
        }
        if (event.getRawSlots().contains(PLAYER_CHEST_SLOT)) {
            event.setCancelled(true);
            MessageUtil.sendConfigured(player, config, config.getBlockedMessage());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDispenseArmor(BlockDispenseArmorEvent event) {
        if (!(event.getTargetEntity() instanceof Player player)) {
            return;
        }
        if (!isElytra(event.getItem())) {
            return;
        }
        if (player.hasPermission("elytrafix.bypass") || !hook.isInCombat(player)) {
            return;
        }
        event.setCancelled(true);
    }

    private boolean isElytra(ItemStack item) {
        return item != null && item.getType() == Material.ELYTRA;
    }
}
