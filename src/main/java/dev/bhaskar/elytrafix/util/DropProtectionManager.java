package dev.bhaskar.elytrafix.util;

import dev.bhaskar.elytrafix.ElytraFixOnCombat;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class DropProtectionManager {
    private static final class ProtectedDrop {
        private final UUID owner;
        private final long droppedAt;

        private ProtectedDrop(UUID owner, long droppedAt) {
            this.owner = owner;
            this.droppedAt = droppedAt;
        }
    }

    private final ElytraFixOnCombat plugin;
    private final NamespacedKey ownerKey;
    private final Map<UUID, ProtectedDrop> protectedDrops = new ConcurrentHashMap<>();
    private BukkitRunnable cleanupTask;

    public DropProtectionManager(ElytraFixOnCombat plugin) {
        this.plugin = plugin;
        this.ownerKey = new NamespacedKey(plugin, "owner");
    }

    public NamespacedKey getOwnerKey() {
        return ownerKey;
    }

    public void protect(Item item, UUID owner) {
        item.getPersistentDataContainer().set(ownerKey, PersistentDataType.STRING, owner.toString());
        protectedDrops.put(item.getUniqueId(), new ProtectedDrop(owner, System.currentTimeMillis()));
    }

    public UUID getOwner(Item item) {
        String value = item.getPersistentDataContainer().get(ownerKey, PersistentDataType.STRING);
        if (value == null) {
            return null;
        }
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    public boolean isProtected(Item item, int protectionSeconds) {
        ProtectedDrop protectedDrop = protectedDrops.get(item.getUniqueId());
        if (protectedDrop == null) {
            return false;
        }
        long elapsed = System.currentTimeMillis() - protectedDrop.droppedAt;
        return elapsed < (protectionSeconds * 1000L);
    }

    public void unprotect(Item item) {
        PersistentDataContainer container = item.getPersistentDataContainer();
        container.remove(ownerKey);
        protectedDrops.remove(item.getUniqueId());
    }

    public void startCleanupTask() {
        stopCleanupTask();
        cleanupTask = new BukkitRunnable() {
            @Override
            public void run() {
                int seconds = plugin.getConfigManager().getPickupProtectionSeconds();
                long now = System.currentTimeMillis();
                long ttl = seconds * 1000L;
                protectedDrops.entrySet().removeIf(entry -> (now - entry.getValue().droppedAt) >= ttl);
            }
        };
        cleanupTask.runTaskTimer(plugin, 1200L, 1200L);
    }

    public void stopCleanupTask() {
        if (cleanupTask != null) {
            cleanupTask.cancel();
            cleanupTask = null;
        }
    }

    public void clear() {
        protectedDrops.clear();
    }
}
