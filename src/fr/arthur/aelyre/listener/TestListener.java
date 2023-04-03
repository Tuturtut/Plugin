package fr.arthur.aelyre.listener;

import fr.arthur.aelyre.test.Main;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;

import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;

public class TestListener implements Listener {

    public static JavaPlugin plugin = Main.getInstance();
    public HashMap<Player, BukkitTask> tasks = new HashMap<>();
    public HashMap<Player, Runnable> runnableTasks = new HashMap<>();
    public static HashMap<Player, Block> previusBlocks = new HashMap<>();
    public static final double PLAYER_RADIUS = 0.35;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Quand le joueur se connect donne un stuff de base
        Player player = event.getPlayer();
        player.sendMessage("Bienvenue " + player.getName() + " sur le serveur !");
        player.getInventory().clear();
        player.getInventory().addItem(new ItemStack(Material.IRON_AXE));
        player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 32));
        player.getInventory().setArmorContents(new ItemStack[]{
                new ItemStack(Material.IRON_BOOTS),
                new ItemStack(Material.IRON_LEGGINGS),
                new ItemStack(Material.IRON_CHESTPLATE),
                new ItemStack(Material.IRON_HELMET)
        });
        // Pioche en diamant enchantée EFFICIENCY 5 et fortune 3
        ItemStack pickaxe = new ItemStack(Material.DIAMOND_PICKAXE);
        pickaxe.addEnchantment(Enchantment.DIG_SPEED, 5);
        pickaxe.addEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 3);

        ItemMeta pickaxeMeta = pickaxe.getItemMeta();
        pickaxeMeta.setDisplayName("§dLA SUPER PIOCHE");

        pickaxe.setItemMeta(pickaxeMeta);

        player.getInventory().addItem(pickaxe);



    }

    @EventHandler
    public void onBlockBreaking(BlockBreakEvent event) {
        // Quand le joueur casse un bloc de sable avec une pelle en bois, lui donne un diamant
        Player player = event.getPlayer();
        ItemStack holding = player.getInventory().getItemInMainHand();
        if (holding.getType() == Material.WOODEN_SHOVEL) {
            if (event.getBlock().getType() == Material.SAND) {
                player.getInventory().addItem(new ItemStack(Material.DIAMOND));
            }

        }
        // Si le joueur casse un bloc de sable donne soit du fer, soit de l'or, soit du diamant, soit de l'émeraude avec un
        // taux de drop différent pour chaque matériaux
        if (event.getBlock().getType() == Material.SAND){
            ArrayList<Material> materials = new ArrayList<>();
            materials.add(Material.RAW_IRON);
            materials.add(Material.RAW_GOLD);
            materials.add(Material.DIAMOND);
            materials.add(Material.EMERALD);

            double ironDrop = 0.5;
            double goldDrop = 0.3;
            double diamondDrop = 0.15;
            double emeraldDrop = 0.05;

            ArrayList<Double> chances = new ArrayList<>();
            chances.add(ironDrop);
            chances.add(goldDrop);
            chances.add(diamondDrop);
            chances.add(emeraldDrop);

            Material material = null;
            double sum = 0;
            double random = Math.random();

            for(int i = 0; i < chances.size(); i++) {
                sum += chances.get(i);

                if (random < sum) {
                    material = materials.get(i);
                    break;
                }
            }

            event.getPlayer().sendMessage("Vous avez trouvé un " + material.toString() + "\n" + "chances: " + random);


            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(),
                    new ItemStack(material) );
        }

        // Si le joueur casse un bloc de diamand avec une pioche en diamant, améliore sa pioche avec un enchantement d'efficacité
        if (event.getBlock().getType() == Material.DIAMOND_ORE){
            if (holding.getType() == Material.DIAMOND_PICKAXE){
                if (holding.getItemMeta().hasEnchant(Enchantment.DIG_SPEED)){
                    holding.addEnchantment(Enchantment.DIG_SPEED, holding.getEnchantmentLevel(Enchantment.DIG_SPEED) + 1);
                } else {
                    holding.addEnchantment(Enchantment.DIG_SPEED, 1);
                }

            }
        }

    }

    @EventHandler
    public void onEntitGetDamagedByEntity(EntityDamageByEntityEvent event) {

        EntityType creeper = EntityType.CREEPER;
        EntityType skeleton = EntityType.SKELETON;

        // Duplique l'entité frappé par le joueur, sauf si l'entité est un joueur
        EntityType entityType = event.getEntity().getType();
        if (!(event.getDamager() instanceof Arrow)) {
            if (!(event.getEntity() instanceof Player)) {
                if (event.getEntity() instanceof Skeleton) {
                    Skeleton s = (Skeleton) event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), entityType);
                    s.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 3, 100));
                    s.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 3, 100));
                }
                event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), entityType);

            }
        }



        if (event.getDamager() instanceof Player){
            // Augmente le knockback des creepers
            if (event.getEntity().getType() == creeper){
                Player player = (Player) event.getDamager();
                event.getEntity().setVelocity(player.getLocation().getDirection().multiply(10));
            }

            // Fais en sorte que les skeletons explosent
            if (event.getEntity().getType() == skeleton){
                Skeleton actualSkeleton = (Skeleton) event.getEntity();
                actualSkeleton.setHealth(0);
                event.getEntity().getWorld().createExplosion(event.getEntity().getLocation(), 8);
            }
        }

        // Si une entité tire une flèche sur une autre entité, echange leurs places
        if (event.getDamager() instanceof Arrow){
            Entity damager = event.getDamager();
            Entity entity = event.getEntity();

            Arrow arrow = (Arrow) damager;
            event.setDamage(0);


            Location entityLocation = event.getEntity().getLocation();


            if (arrow.getShooter() instanceof Entity){
                Entity shooter = (Entity) arrow.getShooter();
                Location shooterLocation = shooter.getLocation();

                shooter.teleport(entityLocation);
                entity.teleport(shooterLocation);

            }
        }


    }

    @EventHandler
    public void onLookingUp(PlayerMoveEvent event){
        // Envoie un message au joueur si il regarde vers le haut
//        Player player = event.getPlayer();
//        Location blockLocation = player.getLocation();
//
//        if (player.getLocation().getPitch() <= -45){
//            player.sendMessage("BAISSE LES YEUX");
//        }
    }

    @EventHandler
    public void onMoving(PlayerMoveEvent event){
        Player player = event.getPlayer();
        Location blockLocation = player.getLocation();

        blockLocation.add(0, -1, 0);

        // Si le joueur tien un baton, pose un bloc de laine violet sous lui

        if (player.getInventory().getItemInMainHand().getType() == Material.STICK){
            player.getWorld().getBlockAt(blockLocation).setType(Material.PURPLE_WOOL);
        }
    }

    @EventHandler
    public void deACoudre(PlayerMoveEvent event){
//        Player player = event.getPlayer();
//        Location blockLocation = player.getLocation();
//
//        blockLocation.add(0, -1, 0);
//
//        // Si le joueur touche de l'eau et que sa vitesse est supérieur à 2
//        if (player.getWorld().getBlockAt(blockLocation).getType() == Material.WATER){
//            blockLocation.getBlock().setType(Material.RED_CONCRETE);
//            player.teleport(player.getWorld().getSpawnLocation());
//        }
    }

    @EventHandler
    public void onGetDamage(EntityDamageEvent event){
        // Si le joueur est équipé d'une armure en cuir, il prend 2x moins de dégats et 4x moins de dégats de chute
        if (event.getEntity().getType() == EntityType.PLAYER){
            Player player = (Player) event.getEntity();
            if (player.getInventory().getHelmet().getType() == Material.LEATHER_HELMET &&
                    player.getInventory().getChestplate().getType() == Material.LEATHER_CHESTPLATE &&
                    player.getInventory().getLeggings().getType() == Material.LEATHER_LEGGINGS &&
                    player.getInventory().getBoots().getType() == Material.LEATHER_BOOTS){
                if (event.getCause() == EntityDamageEvent.DamageCause.FALL)
                    event.setDamage(event.getDamage() / 4);
                else {
                    event.setDamage(event.getDamage() / 2);
                }
            }
        }
    }

    @EventHandler
    public void onItemInteraction(PlayerInteractEvent event){
        // Si le joueur clique droit sur un livre, le téléporte au bloc au dessus de lui
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.BOOK){
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
                // Teleporte le joueur aussi haut que possible, jusqu'à ce qu'il touche un bloc, s'il n'y a pas de bloc, ne fait rien
                Location location = player.getLocation();
                Location blockLocation = location.clone();
                blockLocation.add(0, -1, 0);
                Location startLocation = location.clone();



                int airBlocks = -1000;
                while (player.getWorld().getMaxHeight() > blockLocation.getY()){
                    blockLocation.add(0, 1, 0);



                    if (blockLocation.getBlock().getType() == Material.AIR){
                        airBlocks++;
                    } else {
                        airBlocks = 0;
                    }

                    if (airBlocks == 2){
                        blockLocation.setY(blockLocation.getY() - 0.75);
                        player.teleport(blockLocation);
                        break;
                    }

                }

            }
        }
    }

    @EventHandler
    public void miniGame(PlayerMoveEvent event){
        // Si le joueur marche sur une laine verte, la transforme en jaune, s'il marche sur une jaune, la transforme en rouge, s'il marche sur une rouge, le transforme en air
        Player player = event.getPlayer();
        Location blockLocation = event.getTo().clone();
        blockLocation.add(0, -1, 0);

        Location previusBlockLocation = event.getFrom();
        previusBlockLocation.add(0, -1, 0);


        Block previusBlock = null;
        if (previusBlocks.containsKey(player)){
            previusBlock = previusBlocks.get(player);
        }
        Block block = getBlocksAround(blockLocation, PLAYER_RADIUS);

        previusBlocks.put(player, block);

        if (previusBlock == null && block == null){
            return;
        }

        if (previusBlock != null && block != null){
            if (previusBlock.equals(block)){
                return;
            }
        }

        if (previusBlock != null){
            if (tasks.get(player) != null && !tasks.get(player).isCancelled()){
                Runnable playerTask = runnableTasks.get(player);
                if (!playerTask.wasCalled()){
                    changeBlock(getBlocksAround(previusBlockLocation, PLAYER_RADIUS));
                }
                tasks.get(player).cancel();
            }
        }

        if (block != null){
            Runnable runnable = new Runnable(blockLocation.getBlock(), player);
            runnableTasks.put(player, runnable);
            tasks.put(player, runnable.runTaskTimer(plugin, 20, 20));
        }

    }

    public static class Runnable extends BukkitRunnable {

        private Player player;
        Block block;
        int time = 0;

        public Runnable(Block block, Player player){
            this.block = block;
            this.player = player;
        }

        @Override
        public void run() {
            time++;
            if (changeBlock(block)){
                block = getBlocksAround(player.getLocation().clone().add(0, -1, 0), PLAYER_RADIUS);
                previusBlocks.put(player, block);
            }

        }

        public boolean wasCalled(){
            return time > 0;
        }
    }

    public static boolean changeBlock(Block block){

        if (block == null){
            return true;
        }
        HashMap<Material, Material> switchBlock = new HashMap<>();
        switchBlock.put(Material.LIME_WOOL, Material.YELLOW_WOOL);
        switchBlock.put(Material.YELLOW_WOOL, Material.ORANGE_WOOL);
        switchBlock.put(Material.ORANGE_WOOL, Material.RED_WOOL);
        switchBlock.put(Material.RED_WOOL, Material.AIR);
        if (switchBlock.containsKey(block.getType())){
            Material type = switchBlock.get(block.getType());
            block.setType(switchBlock.get(block.getType()));
            return type == Material.AIR;

        }
        return block.getType() == Material.AIR;
    }

    public static Block getBlocksAround(Location location, double radius){
        Block playerBlock = null;

        double surface = 0;

        Location playerLocation = location.clone();
        playerLocation.add(-radius, 0, -radius);
        Location playerLocation2 = location.clone();
        playerLocation2.add(radius, 0, radius);

        for (double x = location.getX() - radius; x <= location.getX() + radius; x+= radius *2){
            for (double z = location.getZ() - radius; z <= location.getZ() + radius; z+= radius *2){
                Location blockLocation = new Location(location.getWorld(), x, location.getY(), z).getBlock().getLocation();
                Location blockLocation2 = new Location(blockLocation.getWorld(), blockLocation.getX() + 1, blockLocation.getY(), blockLocation.getZ() + 1);
                double surfaceTemp;

                surfaceTemp = getOverlapSurface(playerLocation, playerLocation2, blockLocation, blockLocation2);
                Block block = blockLocation.getBlock();

                if (block.getType() == Material.AIR){
                    continue;
                }
                if (playerBlock == null || surfaceTemp > surface){
                    playerBlock = block;
                    surface = surfaceTemp;
                }
            }

        }
//
//        if (playerBlock != null) {
//            playerBlock.setType(Material.PURPLE_WOOL);
//        }

//            Location playerBlockLocation = playerBlock.getLocation();
//            playerBlockLocation.add(0.5, 1, 0.5);
//
//            playerBlockLocation.getWorld().spawnParticle(Particle.REDSTONE, playerBlockLocation.getX(), playerBlockLocation.getY(), playerBlockLocation.getZ(), 0, 0, 0, 0, new Particle.DustOptions(Color.RED, 1));
//        }
        return playerBlock;
    }

    public static double getOverlapSurface(Location pa1, Location pa2, Location pb1, Location pb2) {
        double xa1 = Math.min(pa1.getX(), pa2.getX());
        double xa2 = Math.max(pa1.getX(), pa2.getX());
        double za1 = Math.min(pa1.getZ(), pa2.getZ());
        double za2 = Math.max(pa1.getZ(), pa2.getZ());

        double xb1 = Math.min(pb1.getX(), pb2.getX());
        double xb2 = Math.max(pb1.getX(), pb2.getX());
        double zb1 = Math.min(pb1.getZ(), pb2.getZ());
        double zb2 = Math.max(pb1.getZ(), pb2.getZ());

        double xOverlap = Math.max(0, Math.min(xa2, xb2) - Math.max(xa1, xb1));
        double zOverlap = Math.max(0, Math.min(za2, zb2) - Math.max(za1, zb1));

        return xOverlap * zOverlap;
    }
}
