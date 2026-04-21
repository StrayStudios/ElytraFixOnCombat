package dev.bhaskar.elytrafix.config;

import dev.bhaskar.elytrafix.ElytraFixOnCombat;
import org.bukkit.configuration.file.FileConfiguration;

public final class ConfigManager {
    private final ElytraFixOnCombat plugin;
    private FileConfiguration config;

    public ConfigManager(ElytraFixOnCombat plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    public void reload() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    public boolean isBlockGliding() {
        return this.config.getBoolean("elytra.block_gliding", false);
    }

    public boolean isBlockFireworks() {
        return this.config.getBoolean("elytra.block_fireworks", false);
    }

    public int getFireworkPowerLimit() {
        return this.config.getInt("elytra.firework_power_limit", -1);
    }

    public boolean isRemoveElytraOnTag() {
        return this.config.getBoolean("remove_elytra_on_tag.enabled", false);
    }

    public int getPickupProtectionSeconds() {
        return this.config.getInt("remove_elytra_on_tag.pickup_protection_seconds", 30);
    }

    public boolean isBlockEquippingElytra() {
        return this.config.getBoolean("block_equipping_elytra.enabled", true);
    }

    public String getPrefix() {
        return this.config.getString("messages.prefix", "");
    }

    public String getBlockedMessage() {
        return this.config.getString("messages.blocked", "&cYou cannot use an elytra while in combat!");
    }

    public String getElytraRemovedToInventoryMessage() {
        return this.config.getString("messages.elytra_removed_to_inventory", "&eYour elytra has been moved to your inventory.");
    }

    public String getElytraDroppedMessage() {
        return this.config.getString("messages.elytra_dropped", "&cYour inventory was full! Your elytra has been dropped.");
    }
}
