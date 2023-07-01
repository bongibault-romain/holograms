package lt.bongibau.holograms;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class PlaceholderAPReplacements {
    public static String replace(Player player, String text) {
        return PlaceholderAPI.setPlaceholders(player, text).replace("%player_name%", player.getName());
    }
}
