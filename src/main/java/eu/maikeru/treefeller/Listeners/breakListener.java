package eu.maikeru.treefeller.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class breakListener implements Listener {
    @EventHandler
    public void onBlockBreak (BlockBreakEvent e) {
        Block block = e.getBlock(); // block
        Location initialLocation = block.getLocation();

        if (!block.getType().name().contains("_LOG")) return;

        // We broke a block that might belong to a tree!

        // Definition of a tree: A bunch of logs stacked on a dirt block, surrounded by persistent=false leaves.

        // Find out if the surrounding area contains logs surrounded by persistent=false leaves or air.
        // Definition of surrounding area: 3x3x1 * n tree height. (x,y,z | Note: y is up/down).

        if (!GetLocationAbove(initialLocation, -1).getBlock().getType().equals(Material.DIRT)) {
            if (!GetLocationAbove(initialLocation, -1).getBlock().getType().equals(block.getType())) {
                return; // Possibly player made. Do not touch.
            }
            // We figured out that the block here is actually a log, but we don't know if the block below it is a dirt block.
            Bukkit.getLogger().info("Hey you broke a log in the middle, weirdo!");
            short depth = -2;
            boolean belowUnequal = !GetLocationAbove(initialLocation, depth).getBlock().getType().equals(block.getType());
            while (!belowUnequal) {
                depth--;
                belowUnequal = !GetLocationAbove(initialLocation, depth).getBlock().getType().equals(block.getType());
            }
            if (!GetLocationAbove(initialLocation, depth).getBlock().getType().equals(Material.DIRT)) {
                return;
            }
            Bukkit.getLogger().info("Found dirt block at Depth: " + depth);
        }

        short logCount = 0;
        short leavesCount = 0;
        boolean aboveEmpty = false;
        ArrayList<Location> logLocations = new ArrayList<>();

        logLocations.add(initialLocation);
        aboveEmpty = logLocations.get(0).getBlock().getType() != block.getType();

        // Acacia trees are broken, they do not work because of the type of generation they use,
        // we will be required to check two blocks up per cycle!

        while (!aboveEmpty || logCount < logLocations.size()) {
            Bukkit.getLogger().info(logCount+1 + " : " + logLocations.size());
            for (byte x = -1; x <= 1; x++) {
                for (byte z = -1; z <= 1; z++){
                    if (x == z & x == 0) continue;

                    Block newBlock = GetBlockAtNew(logLocations.get(logCount) , x, z);
                    if (newBlock.getType().name().contains("_LEAVES")) {
                        if (!((Leaves) newBlock.getBlockData()).isPersistent()) {
                            leavesCount++;
                        }
                    }else if (newBlock.getType().equals(block.getType())) {
                        boolean alreadyExists = false;
                        for (Location preExisting : logLocations) {
                            if (newBlock.getLocation().equals(preExisting)) {
                                alreadyExists = true;
                                break;
                            }
                        }
                        if (!alreadyExists) logLocations.add(newBlock.getLocation());
                    }

                }

            }
            //Bukkit.getLogger().info("Persistent=false leaves: " + leavesCount);
            Location aboveLocation = GetLocationAbove(logLocations.get(logCount));
            aboveEmpty = aboveLocation.getBlock().getType() != block.getType();
            if (!aboveEmpty) {
                logLocations.add(aboveLocation);
            }
            logCount++;
        }

        Bukkit.getLogger().info("Total log count: " + logLocations.size());


        if (leavesCount > 8) {
            block.getLocation().getWorld()
                    .dropItem(block.getLocation(), new ItemStack(block.getType(), logCount));
            for (Location log : logLocations) {
                log.getBlock().setType(Material.AIR);
                Bukkit.getLogger().info(log.getBlockX() + " " + log.getBlockY() + " " + log.getBlockZ());
            }
        }
    }
    private @NotNull Block GetBlockAtNew(Location location, byte x, byte z) {
        return new Location(location.getWorld(), location.getBlockX() + x, location.getBlockY(), location.getBlockZ() + z).getBlock();
    }
    @Contract("_ -> new")
    private @NotNull Location GetLocationAbove(Location location) {
        return new Location(location.getWorld(), location.getBlockX(), location.getBlockY() + 1, location.getBlockZ());
    }
    @Contract("_, _ -> new")
    private @NotNull Location GetLocationAbove(Location location, int amount) {
        return new Location(location.getWorld(), location.getBlockX(), location.getBlockY() + amount, location.getBlockZ());
    }

}
