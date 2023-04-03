package fr.arthur.aelyre.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TestCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String name, String[] args) {
        sender.sendMessage("Test command");
        for (String arg : args) {
            sender.sendMessage(arg);
        }

        return true;
    }
}
