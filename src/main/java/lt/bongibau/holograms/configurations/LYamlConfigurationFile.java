package lt.bongibau.holograms.configurations;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LYamlConfigurationFile {

    private final String fileName;
    
    private final JavaPlugin plugin;

    private YamlConfiguration configuration;

    private File file;

    public LYamlConfigurationFile(String fileName, JavaPlugin plugin) {
        this.fileName = fileName;
        this.plugin = plugin;
        this.configuration = null;
        this.file = null;

        this.saveDefault();
    }

    public void load() {
        this.file = new File(this.plugin.getDataFolder(), this.fileName + ".yml");
        this.configuration = YamlConfiguration.loadConfiguration(this.file);

        InputStream defaultStream = this.plugin.getResource(this.fileName + ".yml");

        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            this.configuration.setDefaults(defaultConfig);
        }
    }

    public YamlConfiguration getConfiguration() {
        if (this.configuration == null) {
            this.load();
        }

        return this.configuration;
    }

    public List<String> getMessageList(String path) {
        return this.getMessageList(path, new HashMap<>());
    }

    public List<String> getMessageList(String path, HashMap<String, String> args) {
        List<String> messages = this.getConfiguration().getStringList(path);

        if (messages == null) return new ArrayList<>();

        for (Map.Entry<String, String> entry : args.entrySet()) {
            messages.replaceAll(s -> s.replace("%" + entry.getKey().toLowerCase() + "%", entry.getValue()));
        }

        messages.replaceAll(s -> s.replace("&", "§"));
        return messages;
    }

    public String getMessage(String path) {
        return this.getMessage(path, new HashMap<>());
    }

    public String getMessage(String path, HashMap<String, String> args) {
        String message = this.getConfiguration().getString(path);

        if (message == null) return path;

        for (Map.Entry<String, String> entry : args.entrySet()) {
            message = message.replace("%" + entry.getKey().toLowerCase() + "%", entry.getValue());
        }

        message = message.replace("&", "§");
        return message;
    }

    public void save() {
        if (this.configuration == null || this.file == null) {
            return;
        }

        try {
            this.getConfiguration().save(this.file);
        } catch (Exception e) {
            this.plugin.getLogger().severe("Could not save configuration file " + this.file.getName() + "!");
        }
    }

    public void saveDefault() {
        if (this.file == null) {
            this.file = new File(this.plugin.getDataFolder(), this.fileName + ".yml");
        }

        if (!this.file.exists()) {
            this.plugin.saveResource(this.fileName + ".yml", false);
        }
    }

    public double getDouble(String path) {
        return this.getConfiguration().getDouble(path);
    }
}
