package lt.bongibau.holograms.managers;

import lt.bongibau.holograms.api.Hologram;
import lt.bongibau.holograms.configurations.DataHologramsConfigurationFile;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HologramManager {

    private static HologramManager instance = new HologramManager();

    private final List<Hologram> holograms = new ArrayList<>();

    public void load() {
        this.holograms.addAll(DataHologramsConfigurationFile.getInstance().getHolograms());

        for (Hologram hologram : this.holograms) {
            hologram.spawn();
        }
    }

    public void unload() {
        for (Hologram hologram : this.holograms) {
            hologram.kill();
        }
    }

    public static HologramManager getInstance() {
        return instance;
    }

    public void register(Hologram hologram) {
        this.holograms.add(hologram);
        hologram.spawn();
        this.save();
    }

    public void unregister(Hologram hologram) {
        this.holograms.remove(hologram);
        hologram.kill();
        this.save();
    }

    @Nullable
    public Hologram get(String name) {
        for (Hologram hologram : this.holograms) {
            if (hologram.getName().equalsIgnoreCase(name)) {
                return hologram;
            }
        }

        return null;
    }

    public boolean exists(String name) {
        return this.get(name) != null;
    }

    public boolean isHologram(int entityId) {
        for (Hologram hologram : this.holograms) {
            if (hologram.isHologram(entityId)) {
                return true;
            }
        }

        return false;
    }

    public void save() {
        DataHologramsConfigurationFile.getInstance().setHolograms(this.holograms);
        DataHologramsConfigurationFile.getInstance().save();
    }

    @Nullable
    public Hologram getHologram(int entityId) {
        for (Hologram hologram : this.holograms) {
            if (hologram.isHologram(entityId)) {
                return hologram;
            }
        }

        return null;
    }

    public List<Hologram> getAll() {
        return Collections.unmodifiableList(this.holograms);
    }
}
