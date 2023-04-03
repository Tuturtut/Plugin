package fr.arthur.aelyre.commands;

import org.bukkit.command.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class NearCommand implements CommandExecutor, TabExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String name, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            int size = 10;
            ArrayList<EntityType> entityChoice = new ArrayList<>();

            switch (args.length) {
                case 0 -> player.sendMessage("ZERO ARGUMENTS");
                case 1 -> {
                    player.sendMessage("1 ARGUMENT");
                    try {
                        size = Integer.parseInt(args[0]);
                    } catch (NumberFormatException e) {
                        player.sendMessage("§cErreur: §f" + args[0] + " n'est pas un nombre");
                        return true;
                    }
                }
            }
            if (args.length >= 2){
                for (int i = 1; i < args.length; i++) {
                    try {
                        size = Integer.parseInt(args[0]);
                    } catch (NumberFormatException e) {
                        player.sendMessage("§cErreur: §f" + args[0] + " n'est pas un nombre");
                        return true;
                    }
                    try {
                        entityChoice.add(EntityType.valueOf(args[i].toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        player.sendMessage("§cErreur: §f" + args[i] + " n'est pas une entité");
                        return true;
                    }
                }
            }


            List<Entity> entities = player.getNearbyEntities(size, size, size);
            HashMap<String, EntityData> entityCount = new HashMap<>();
            for (Entity entity : entities) {
                String entityName = entity.getType().name();
                if (entityCount.containsKey(entityName)) {
                    entityCount.get(entityName).addAmmount();
                } else {
                    entityCount.put(entityName, new EntityData(entityName, 1));
                }
            }
            ArrayList<EntityData> entityData = new ArrayList<>(entityCount.values());

            entityData.sort(EntityData::compareTo);
            if (entityData.size() == 0) {
                player.sendMessage("§fAucune entité trouvée");
                return true;
            } else {
                StringBuilder sb = new StringBuilder();
                boolean asFound = false;

                if (entityChoice.size() == 0) {

                    for (EntityData e : entityData) {
                        sb.append("§6").append(e.getName()).append(": §f").append(e.getCount()).append("\n");
                        asFound = true;
                    }

                } else {
                    for (EntityData entityDatum : entityData) {
                        for (EntityType entityType : entityChoice){
                            if (entityDatum.getName().equals(entityType.name())) {
                                sb.append("§6").append(entityDatum.getName()).append(": §f").append(entityDatum.getCount()).append("\n");
                                asFound = true;
                            }
                        }
                    }
                }
                if (!asFound){
                    player.sendMessage("§fAucune entité trouvée");
                    return true;
                } else {
                    player.sendMessage(sb.toString());
                }
            }

        }


        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String name, String[] args) {
        ArrayList<String> options = new ArrayList<>();

        if (args.length == 1) {
            options.add("10");
            options.add("25");
            options.add("50");
            options.add("100");
            options.add("250");
        } else if (args.length >= 2) {
            for (EntityType entityType : EntityType.values()) {
                options.add(entityType.name());
            }
        }

        options.removeIf(string -> !string.toLowerCase().startsWith(args[args.length - 1].toLowerCase()));
        return options;


    }


    public class EntityData implements Comparable<EntityData> {
        private String name;
        private int count;

        public EntityData(String name, int count){
            this.name = name;
            this.count = count;
        }

            public String getName () {
            return name;
        }

            public int getCount () {
            return count;
        }

            public void addAmmount ( int ammount){
            this.count += ammount;
        }

            public void addAmmount () {
            addAmmount(1);
        }

        @Override
        public int compareTo(EntityData o) {
            return Integer.compare(o.getCount(), this.getCount());
        }
    }

}