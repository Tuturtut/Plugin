package fr.arthur.aelyre.commands;

import org.bukkit.GameMode;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GamemodeCommand implements CommandExecutor, TabExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String name, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            if (args.length == 0){
                player.sendMessage(player.getGameMode().name());
            }
            if (args.length == 1){
                switch (args[0]) {
                    case "0", "s", "survival" -> player.setGameMode(GameMode.SURVIVAL);
                    case "1", "c", "creative" -> player.setGameMode(GameMode.CREATIVE);
                    case "2", "a", "adventure" -> player.setGameMode(GameMode.ADVENTURE);
                    case "3", "sp", "spectator" -> player.setGameMode(GameMode.SPECTATOR);
                    default -> player.sendMessage("§cErreur: §fVeuillez entrer un mode de jeu valide.");
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String msg, String[] args) {
        ArrayList<String> options = new ArrayList<>();



        if (!(sender instanceof Player)){
            return options;
        }

        if (args.length == 1) {
            options.add("survival");
            options.add("creative");
            options.add("adventure");
            options.add("spectator");
        }

        options.removeIf(string -> !string.toLowerCase().startsWith(args[args.length - 1].toLowerCase()));

        return options;

    }
}
