package dev.bhaskar.elytrafix.util;

import dev.bhaskar.elytrafix.config.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

public final class MessageUtil {
    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.legacyAmpersand();

    private MessageUtil() {
    }

    public static Component color(String message) {
        return SERIALIZER.deserialize(message == null ? "" : message);
    }

    public static void sendConfigured(Player player, ConfigManager config, String message) {
        player.sendMessage(color(config.getPrefix() + message));
    }
}
