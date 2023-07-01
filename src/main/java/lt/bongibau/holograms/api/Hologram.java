package lt.bongibau.holograms.api;

import lt.bongibau.holograms.configurations.DataHologramsConfigurationFile;
import lt.bongibau.holograms.managers.HologramManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.ArmorStand;
import org.checkerframework.checker.units.qual.A;

import java.util.*;

public class Hologram implements ConfigurationSerializable {


    private String name;

    private Location location;

    private String[] lines;

    private List<ArmorStand> armorStands = new ArrayList<>();

    private final double marginMultiplier = 0.25;

    public Hologram(String name, Location location, String... lines) {
        this.name = name;
        this.location = location;
        this.lines = lines;
    }

    public void respawn() {
        this.kill();
        this.spawn();
    }

    public void spawn() {
        for (int i = 0; i < this.lines.length; i++) {
            this.spawnLine(i);
        }
    }

    private void spawnLine(int index) {
        double marginTop = index * marginMultiplier;

        ArmorStand armorStand = Objects.requireNonNull(this.location.getWorld()).spawn(this.location.clone().subtract(0, marginTop, 0), ArmorStand.class);
        armorStand.setGravity(false);
        armorStand.setCanPickupItems(false);
        armorStand.setCustomNameVisible(true);
        armorStand.setCustomName(this.lines[index].replace("&", "§"));
        armorStand.setVisible(false);
        armorStand.setMarker(true);
        armorStand.setPersistent(true);

        this.armorStands.add(armorStand);
    }

    public void kill() {
        for (ArmorStand armorStand : this.armorStands) {
            armorStand.remove();
        }

        this.armorStands.clear();
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
        this.respawn();
        this.save();
    }

    public String[] getLines() {
        return lines;
    }

    public void setLines(String... lines) {
        this.lines = lines;
        this.respawn();
        this.save();
    }

    public void addLine(String line) {
        String[] newLines = new String[this.lines.length + 1];
        System.arraycopy(this.lines, 0, newLines, 0, this.lines.length);
        newLines[this.lines.length] = line;
        this.lines = newLines;

        this.respawn();
        this.save();
    }

    public void removeLine(int index) {
        String[] newLines = new String[this.lines.length - 1];
        System.arraycopy(this.lines, 0, newLines, 0, index);
        System.arraycopy(this.lines, index + 1, newLines, index, this.lines.length - index - 1);
        this.lines = newLines;

        this.respawn();
        this.save();
    }

    public void save() {
        HologramManager.getInstance().save();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();

        map.put("name", this.name);
        map.put("location", this.location);
        map.put("lines", List.of(this.lines));

        return map;
    }

    @SuppressWarnings("unchecked")
    public static Hologram deserialize(Map<String, Object> map) {
        String name = (String) map.get("name");
        Location location = (Location) map.get("location");
        String[] lines = ((List<String>) map.get("lines")).toArray(new String[0]);

        return new Hologram(name, location, lines);
    }

    public void setLine(int lineNumber, String line) {
        this.lines[lineNumber] = line;
        this.respawn();
        this.save();
    }

    public boolean isHologram(int entityId) {
        return this.armorStands.stream().anyMatch(armorStand -> armorStand.getEntityId() == entityId);
    }

    public String getText(int entityId) {
        ArmorStand armorStand = this.armorStands.stream().filter(am -> am.getEntityId() == entityId).findFirst().orElse(null);

        if (armorStand == null) return "§cError";

        return armorStand.getCustomName();
    }

    public List<ArmorStand> getArmorStands() {
        return Collections.unmodifiableList(this.armorStands);
    }
}
