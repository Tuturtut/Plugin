package fr.arthur.aelyre.minesweeper;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class MinesWeeperManagement {

    private static MinesWeeperManagement INSTANCE;

    private int height;
    private int width;

    private Material nonFindTile = Material.GRAY_CONCRETE;
    public static HashMap<Player, MinesWeeperManagement> games = new HashMap<>();

    public MinesWeeperManagement() {;
    }

    public static MinesWeeperManagement getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MinesWeeperManagement();
        }
        return INSTANCE;
    }

    public void create(Player player) {
        games.put(player, this);
        initGame(player);
    }



    public MinesWeeperManagement getByPlayer(Player player){
        return games.get(player);
    }

    public void removeGame(Player player){
        games.remove(player);
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Material getNonFindTile() {
        return nonFindTile;
    }

    public void initGame(Player player){

        Minesweeper minesweeper = new Minesweeper();

        player.sendMessage("§aVous avez lancé une partie de Minesweeper");

        for (int i = 0; i < getHeight(); i++) {
            for (int j = 0; j < getWidth(); j++) {
                Location location = new Location(player.getWorld(), i,100, j);
                player.getWorld().getBlockAt(location).setType(getNonFindTile());
                player.sendMessage("i : " + i + " j : " + j);
            }
        }

    }
}
