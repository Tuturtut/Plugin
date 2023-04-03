package fr.arthur.aelyre.test;

import fr.arthur.aelyre.commands.*;
import fr.arthur.aelyre.listener.TestListener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private static Main MAIN;

    @Override
    public void onLoad() {
        MAIN = this;
    }

    @Override
    public void onEnable() {
        System.out.println("Plugin enabled ARTHUR");
        getServer().getPluginManager().registerEvents(new TestListener(), this);
        getCommand("test").setExecutor(new TestCommand());
        getCommand("getPosition").setExecutor(new GetPositionCommand());
        getCommand("gamemode").setExecutor(new GamemodeCommand());
        getCommand("near").setExecutor(new NearCommand());
        getCommand("minesweeper").setExecutor(new MinesweeperCommand());



    }

    @Override
    public void onDisable() {
        System.out.println("Plugin disabled");
    }

    public static Main getInstance() {
        return MAIN;
    }


}
