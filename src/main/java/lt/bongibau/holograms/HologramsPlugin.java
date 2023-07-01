package lt.bongibau.holograms;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import lt.bongibau.holograms.api.Hologram;
import lt.bongibau.holograms.commands.HologramCommand;
import lt.bongibau.holograms.managers.HologramManager;
import lt.bongibau.holograms.packets.EntityMetaDataPacketAdapter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class HologramsPlugin extends JavaPlugin {

    private static HologramsPlugin instance;

    private static boolean isPlaceholderAPIPresent = false;

    @Override
    public void onLoad() {
        ConfigurationSerialization.registerClass(Hologram.class);
    }

    @Override
    public void onEnable() {
        instance = this;
        // Plugin startup logic

        HologramManager.getInstance().load();

        Objects.requireNonNull(this.getCommand("holograms")).setExecutor(new HologramCommand());
        Objects.requireNonNull(this.getCommand("holograms")).setTabCompleter(new HologramCommand());

        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

        protocolManager.addPacketListener(new EntityMetaDataPacketAdapter());

        isPlaceholderAPIPresent = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                EntityMetaDataPacketAdapter.sendUpdatePackets(onlinePlayer);
            }
        }, 0L, 20L);
    }

    @Override
    public void onDisable() {
        HologramManager.getInstance().unload();

        instance = null;
        // Plugin shutdown logic
    }

    public static HologramsPlugin getInstance() {
        return instance;
    }

    public static boolean isIsPlaceholderAPIPresent() {
        return isPlaceholderAPIPresent;
    }
}
