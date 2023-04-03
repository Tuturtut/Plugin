package fr.arthur.aelyre.commands;

import fr.arthur.aelyre.minesweeper.MinesWeeperManagement;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.List;

public class MinesweeperCommand implements CommandExecutor , TabExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String name, String[] args) {
        if (sender instanceof Player) {
            MinesWeeperManagement minesWeeperManagement = new MinesWeeperManagement();
            minesWeeperManagement.create((Player) sender);
        } else {
            sender.sendMessage("You must be a player to use this command");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
