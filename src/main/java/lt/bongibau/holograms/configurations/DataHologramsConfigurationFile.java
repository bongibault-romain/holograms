package lt.bongibau.holograms.configurations;

import lt.bongibau.holograms.HologramsPlugin;
import lt.bongibau.holograms.api.Hologram;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class DataHologramsConfigurationFile extends LYamlConfigurationFile {

    private static final DataHologramsConfigurationFile instance = new DataHologramsConfigurationFile(HologramsPlugin.getInstance());

    public DataHologramsConfigurationFile(JavaPlugin plugin) {
        super("data/holograms", plugin);
    }

    @SuppressWarnings("unchecked")
    public List<Hologram> getHolograms() {
        return (List<Hologram>) this.getConfiguration().get("holograms");
    }

    public void setHolograms(List<Hologram> holograms) {
        this.getConfiguration().set("holograms", holograms);
    }

    public static DataHologramsConfigurationFile getInstance() {
        return instance;
    }
}
