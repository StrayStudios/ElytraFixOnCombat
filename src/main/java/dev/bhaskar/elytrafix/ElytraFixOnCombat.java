package dev.bhaskar.elytrafix;

import dev.bhaskar.elytrafix.config.ConfigManager;
import dev.bhaskar.elytrafix.hooks.CombatLogXHook;
import dev.bhaskar.elytrafix.listeners.ListenerCombatTag;
import dev.bhaskar.elytrafix.listeners.ListenerEquip;
import dev.bhaskar.elytrafix.listeners.ListenerFirework;
import dev.bhaskar.elytrafix.listeners.ListenerGlide;
import dev.bhaskar.elytrafix.listeners.ListenerPickup;
import dev.bhaskar.elytrafix.util.DropProtectionManager;
import dev.bhaskar.elytrafix.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ElytraFixOnCombat extends JavaPlugin implements TabExecutor {
    private final List<Listener> activeListeners = new ArrayList<>();

    private ConfigManager configManager;
    private CombatLogXHook combatLogXHook;
    private DropProtectionManager dropProtectionManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.configManager = new ConfigManager(this);
        this.combatLogXHook = new CombatLogXHook();
        this.dropProtectionManager = new DropProtectionManager(this);

        PluginCommand command = getCommand("elytrafix");
        if (command != null) {
            command.setExecutor(this);
            command.setTabCompleter(this);
        }

        if (combatLogXHook.isAvailable()) {
            getLogger().info("[ElytraFixOnCombat] Hooked into CombatLogX successfully.");
            registerConfiguredListeners();
        } else {
            getLogger().warning("[ElytraFixOnCombat] CombatLogX not found — all features disabled.");
        }

        dropProtectionManager.startCleanupTask();
    }

    @Override
    public void onDisable() {
        unregisterConfiguredListeners();
        if (dropProtectionManager != null) {
            dropProtectionManager.stopCleanupTask();
            dropProtectionManager.clear();
        }
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    private void registerConfiguredListeners() {
        unregisterConfiguredListeners();
        PluginManager pluginManager = Bukkit.getPluginManager();

        if (configManager.isBlockGliding()) {
            Listener listener = new ListenerGlide(configManager, combatLogXHook);
            pluginManager.registerEvents(listener, this);
            activeListeners.add(listener);
        }

        if (configManager.isBlockFireworks() || configManager.getFireworkPowerLimit() >= 1) {
            Listener listener = new ListenerFirework(configManager, combatLogXHook);
            pluginManager.registerEvents(listener, this);
            activeListeners.add(listener);
        }

        if (configManager.isRemoveElytraOnTag()) {
            Listener tagListener = new ListenerCombatTag(configManager, dropProtectionManager);
            Listener pickupListener = new ListenerPickup(configManager, dropProtectionManager);
            pluginManager.registerEvents(tagListener, this);
            pluginManager.registerEvents(pickupListener, this);
            activeListeners.add(tagListener);
            activeListeners.add(pickupListener);
        }

        if (configManager.isBlockEquippingElytra()) {
            Listener listener = new ListenerEquip(configManager, combatLogXHook);
            pluginManager.registerEvents(listener, this);
            activeListeners.add(listener);
        }
    }

    private void unregisterConfiguredListeners() {
        for (Listener listener : activeListeners) {
            HandlerList.unregisterAll(listener);
        }
        activeListeners.clear();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("elytrafix.admin")) {
                sender.sendMessage(MessageUtil.color("&cYou do not have permission to use this command."));
                return true;
            }

            configManager.reload();
            if (combatLogXHook.isAvailable()) {
                registerConfiguredListeners();
            } else {
                unregisterConfiguredListeners();
            }
            sender.sendMessage(MessageUtil.color(configManager.getPrefix() + "&aElytraFixOnCombat configuration reloaded."));
            return true;
        }

        sender.sendMessage(MessageUtil.color(configManager.getPrefix() + "&eUsage: /" + label + " reload"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return "reload".startsWith(args[0].toLowerCase()) ? List.of("reload") : Collections.emptyList();
        }
        return Collections.emptyList();
    }
}
