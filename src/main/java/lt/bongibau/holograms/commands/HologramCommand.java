package lt.bongibau.holograms.commands;

import lt.bongibau.holograms.api.Hologram;
import lt.bongibau.holograms.managers.HologramManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class HologramCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cVous devez être un joueur pour exécuter cette commande.");
            return true;
        }

        if (args.length == 0) {
            this.sendHelp(player);
            return true;
        }

        if (args.length == 1) {
            this.sendHelp(player);
            return true;
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("create")) {
                String name = args[1];

                this.create(name, player, "&cAucune ligne n'a été spécifiée.");
                return true;
            }

            if (args[0].equalsIgnoreCase("remove")) {
                String name = args[1];

                this.remove(name, player);
                return true;
            }

            if (args[0].equalsIgnoreCase("tphere")) {
                String name = args[1];

                this.teleportHere(name, player);
                return true;
            }

            if (args[0].equalsIgnoreCase("tp")) {
                String name = args[1];

                this.teleport(name, player);
                return true;
            }
        }

        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("remove")) {
                String name = args[1];

                int number;
                try {
                    number = Integer.parseInt(args[2]);
                } catch (NumberFormatException exception) {
                    player.sendMessage("§cLe numéro de ligne doit être un nombre.");
                    return true;
                }

                this.removeLine(name, player, number);
                return true;
            }
        }

        if (args.length >= 3) {
            if (args[0].equalsIgnoreCase("create")) {
                String name = args[1];

                StringBuilder lineBuilder = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    lineBuilder.append(args[i]).append(" ");
                }

                this.create(name, player, lineBuilder.toString().trim());
                return true;
            }

            if (args[0].equalsIgnoreCase("add")) {
                String name = args[1];

                StringBuilder lineBuilder = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    lineBuilder.append(args[i]).append(" ");
                }

                this.addLine(name, player, lineBuilder.toString().trim());
                return true;
            }
        }

        if (args.length >= 4) {
            if(args[0].equalsIgnoreCase("set")) {
                String name = args[1];

                StringBuilder lineBuilder = new StringBuilder();
                for (int i = 3; i < args.length; i++) {
                    lineBuilder.append(args[i]).append(" ");
                }

                int number;
                try {
                    number = Integer.parseInt(args[2]);
                } catch (NumberFormatException exception) {
                    player.sendMessage("§cLe numéro de ligne doit être un nombre.");
                    return true;
                }

                this.setLine(name, player, number, lineBuilder.toString().trim());
                return true;
            }
        }

        return true;
    }

    private void remove(String name, Player player) {
        Hologram hologram = HologramManager.getInstance().get(name);

        if (hologram == null) {
            player.sendMessage("§cAucun hologramme avec ce nom n'existe.");
            return;
        }

        HologramManager.getInstance().unregister(hologram);
    }

    private void sendHelp(Player player) {
        player.sendMessage("§8-=[ §e+ §8]=- Holograms §8-=[ §e+ §8]=-");
        player.sendMessage(" ");
        player.sendMessage(" §e/hologram §f- §7Afficher l'aide.");
        player.sendMessage(" §e/hologram create <nom> [texte] §f- §7Créer un hologramme.");
        player.sendMessage(" §e/hologram remove <nom> [ligne] §f- §7Supprimer un hologramme ou une ligne spécifique.");
        player.sendMessage(" §e/hologram add <nom> [texte] §f- §7Ajouter une ligne à un hologramme.");
        player.sendMessage(" §e/hologram set <nom> <ligne> [texte] §f- §7Modifier une ligne d'un hologramme.");
        player.sendMessage(" §e/hologram tphere <nom> §f- §7Téléporter un hologramme à vous.");
        player.sendMessage(" §e/hologram tp <nom> §f- §7Téléporter vous à un hologramme.");
        player.sendMessage(" ");
        player.sendMessage("§8-=[ §e+ §8]=- Holograms §8-=[ §e+ §8]=-");
    }

    private void create(String name, Player player, String line) {
        Location location = player.getLocation().clone();

        if (HologramManager.getInstance().exists(name)) {
            player.sendMessage("§cUn hologramme avec ce nom existe déjà.");
            return;
        }

        Hologram hologram = new Hologram(name, location, line);
        HologramManager.getInstance().register(hologram);
    }

    private void setLine(String name, Player player, int lineNumber, String line) {
        Hologram hologram = HologramManager.getInstance().get(name);

        if (hologram == null) {
            player.sendMessage("§cAucun hologramme avec ce nom n'existe.");
            return;
        }

        if (lineNumber < 0) {
            player.sendMessage("§cLe numéro de ligne est trop petit.");
            return;
        }

        if (lineNumber >= hologram.getLines().length) {
            player.sendMessage("§cLe numéro de ligne est trop grand.");
            return;
        }

        hologram.setLine(lineNumber, line);
    }

    private void addLine(String name, Player player, String line) {
        Hologram hologram = HologramManager.getInstance().get(name);

        if (hologram == null) {
            player.sendMessage("§cAucun hologramme avec ce nom n'existe.");
            return;
        }

        hologram.addLine(line);
    }

    private void removeLine(String name, Player player, int lineNumber) {
        Hologram hologram = HologramManager.getInstance().get(name);

        if (hologram == null) {
            player.sendMessage("§cAucun hologramme avec ce nom n'existe.");
            return;
        }

        if (lineNumber < 0) {
            player.sendMessage("§cLe numéro de ligne est trop petit.");
            return;
        }

        if (lineNumber >= hologram.getLines().length) {
            player.sendMessage("§cLe numéro de ligne est trop grand.");
            return;
        }

        hologram.removeLine(lineNumber);
    }

    private void teleportHere(String name, Player player) {
        Hologram hologram = HologramManager.getInstance().get(name);

        if (hologram == null) {
            player.sendMessage("§cAucun hologramme avec ce nom n'existe.");
            return;
        }

        hologram.setLocation(player.getLocation());
    }

    private void teleport(String name, Player player) {
        Hologram hologram = HologramManager.getInstance().get(name);

        if (hologram == null) {
            player.sendMessage("§cAucun hologramme avec ce nom n'existe.");
            return;
        }

        player.teleport(hologram.getLocation());
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            return Stream.of("create", "remove", "add", "set", "tphere", "tp").filter(string -> string.startsWith(args[0])).collect(Collectors.toList());
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("create")) return List.of();

            return HologramManager.getInstance().getAll().stream().map(Hologram::getName).filter(string -> string.startsWith(args[1])).collect(Collectors.toList());
        }

        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("create")) return List.of();
            if (args[0].equalsIgnoreCase("add")) return List.of();
            if (args[0].equalsIgnoreCase("tphere")) return List.of();
            if (args[0].equalsIgnoreCase("tp")) return List.of();

            Hologram hologram = HologramManager.getInstance().get(args[1]);

            if (hologram == null) return List.of();

            return IntStream.range(0, hologram.getLines().length).mapToObj(String::valueOf).filter(string -> string.startsWith(args[2])).collect(Collectors.toList());
        }

        return List.of();
    }
}
