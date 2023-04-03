package fr.arthur.aelyre.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GetPositionCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String name, String[] strings) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Location location = player.getLocation();
            sender.sendMessage("X: " + location.getX() + " Y: " + location.getY() + " Z: " + location.getZ());
        } else {
            sender.sendMessage("You must be a player to use this command");
        }
        return true;
    }
}
