package eu.maikeru.treefeller.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CollectTreeInformation {
    ArrayList<Location> logLocations = new ArrayList<>();
    Location aboveLocation;
    Material initialBlockType;
    private short logCount = 0;
    private short leavesCount = 0;

    private boolean branching;

    public CollectTreeInformation(Location initialLocation) {
        if (isTreeOnDirt(initialLocation)) {
            logLocations.add(initialLocation);
            aboveLocation = GetLocationAbove(initialLocation);
            initialBlockType = initialLocation.getBlock().getType();
            processInformation();
        }
    }
    private boolean isTreeOnDirt(Location initialLocation) {
        if (!GetLocationAbove(initialLocation, -1).getBlock().getType().equals(Material.DIRT)) {
            // We figured out that the block here is actually a log, but we don't know if the block below it is a dirt block.
            Bukkit.getLogger().info("Hey you broke a log in the middle, weirdo!");
            short depth = -2;
            boolean belowUnequal = !GetLocationAbove(initialLocation, depth).getBlock().getType().equals(initialBlockType);
            while (!belowUnequal) {
                depth--;
                belowUnequal = !GetLocationAbove(initialLocation, depth).getBlock().getType().equals(initialBlockType);
            }
            if (!GetLocationAbove(initialLocation, depth).getBlock().getType().equals(Material.DIRT)) {
                return false;
            }
            Bukkit.getLogger().info("Found dirt block at Depth: " + depth);
        }
        return true;
    }
    public boolean isValid() {
        return leavesCount > 8;
    }
    public int getLogCount() {
        return logCount;
    }
    public void removeTree() {
        for (Location log : logLocations) {
            log.getBlock().setType(Material.AIR);
        }
    }
    private void processInformation() {
        if (aboveLocation.getBlock().getType() != initialBlockType
            && logCount >= logLocations.size()) return;

        for (byte x = -1; x <= 1; x++) {
            for (byte z = -1; z <= 1; z++){

                if (x == z & x == 0) continue;

                for (byte y = 0; y <= 1; y++) {

                    collectAt(x,y,z);

                }
            }
        }

        aboveLocation = GetLocationAbove(logLocations.get(logCount));
        if (aboveLocation.getBlock().getType() == initialBlockType) {
            logLocations.add(aboveLocation);
        }
        logCount++;
        processInformation();
    }
    private void collectAt(byte x, byte y, byte z) {
        Block newBlock = GetBlockAtNew(logLocations.get(logCount) , x, y, z);
        if (newBlock.getType().name().contains("_LEAVES")) {
            if (!((Leaves) newBlock.getBlockData()).isPersistent()) {
                leavesCount++;
            }
        }else if (newBlock.getType().equals(initialBlockType)) {
            if (!logLocations.contains(newBlock.getLocation())) {
                branching = true;
                logLocations.add(newBlock.getLocation());
            }

        }

    }
    private @NotNull Block GetBlockAtNew(Location location, byte x, byte y, byte z) {
        return new Location(location.getWorld(), location.getBlockX() + x, location.getBlockY() + y, location.getBlockZ() + z).getBlock();
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
