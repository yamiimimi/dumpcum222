package BlackChinHunter.utilities;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.Item;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static org.dreambot.api.utilities.Logger.log;

public class InventoryUtils {

    // *** ITEM VALIDATION ***

    /**
     * Checks if the inventory contains a specific item by name.
     * @param itemName The name of the item to check.
     * @return True if the item exists in the inventory, false otherwise.
     */
    public static boolean contains(String itemName) {
        return Inventory.contains(itemName);
    }
    public static boolean isEssentialItem(Item item) {
        if (item == null) {
            log("Encountered a null item while checking essential items.");
            return false;
        }

        String itemName = item.getName().toLowerCase();

        // Check for specific variants
        if (itemName.startsWith("amulet of glory") || itemName.startsWith("burning amulet") || itemName.startsWith("ranging potion")) {
            return true;
        }

        // Check against specific item names
        boolean essential = Arrays.asList(
                "shark",
                "rune dart",
                "green d'hide chaps",
                "green d'hide vambraces"
        ).contains(itemName);

        log("Item: " + itemName + ", Essential: " + essential);
        return essential;
    }
    public static List<Item> getInventoryItems() {
        return Inventory.all();
    }

    public static boolean useTeleport(String itemName, String interaction, String destination) {
        if (Inventory.interact(itemName, interaction)) {
            Sleep.sleepUntil(() -> Players.getLocal().getTile().distance(new Tile(0, 0)) > 0, 5000);
            log("Teleporting to " + destination + " using " + itemName);
            return true;
        }
        return false;
    }
    public static long countFoodInInventory() {
        log("Counting food items in inventory...");
        long foodCount = Inventory.all().stream()
                .filter(Objects::nonNull) // Ensure null items are excluded
                .filter(item -> isFood(item)) // Check if the item is a recognized food
                .count();
        log("Food count in inventory: " + foodCount);
        return foodCount;
    }

    private static boolean isFood(Item item) {
        if (item == null) {
            return false;
        }
        // Add logic for identifying food items, for example:
        String itemName = item.getName().toLowerCase();
        return itemName.contains("shark") || itemName.contains("lobster") || itemName.contains("monkfish");
    }
    /**
     * Counts the total number of a specific item in the inventory.
     * @param itemName The name of the item to count.
     * @return The total count of the item in the inventory.
     */
    public static int count(String itemName) {
        return Inventory.count(itemName);
    }

    /**
     * Checks if the player has a usable teleport amulet.
     * @param amuletNames List of amulet names to check (e.g., "Burning amulet", "Amulet of glory").
     * @return True if a teleport amulet with charges is equipped or in the inventory.
     */
    public static boolean hasUsableTeleport(List<String> amuletNames) {
        return amuletNames.stream().anyMatch(amulet ->
                Equipment.contains(item -> isMatchingVariant(item, amulet)) ||
                        Inventory.contains(item -> isMatchingVariant(item, amulet))
        );
    }


    public static boolean hasNonEssentialItems() {
        boolean hasNonEssentials = Inventory.all().stream()
                .filter(Objects::nonNull) // Exclude null items
                .anyMatch(item -> {
                    boolean isEssential = isEssentialItem(item);
                    log("Item: " + item.getName() + ", Essential: " + isEssential);
                    return !isEssential; // Match non-essential items
                });

        log("Has non-essential items: " + hasNonEssentials);
        return hasNonEssentials;
    }
    public static boolean hasBurningAmuletVariant() {
        // Check for any Burning Amulet variant (e.g., Burning amulet(1) to Burning amulet(5))
        return Inventory.contains(item -> item != null && item.getName().startsWith("Burning amulet"));
    }
    public static boolean hasRangingPotionVariant() {
        // Check for any Ranging Potion variant in the inventory
        return Inventory.contains(item -> item != null && item.getName().startsWith("Ranging potion"));
    }
    public static boolean needsRestocking() {
        boolean missingTeleport = !hasUsableTeleport(Collections.singletonList("Amulet of glory")) &&
                !Equipment.contains(item -> item != null && item.getName().startsWith("Amulet of glory"));

        boolean missingBurningAmulet = !Inventory.contains(item -> item != null && item.getName().startsWith("Burning amulet"));

        boolean missingPotion = !Inventory.contains(item -> item != null && item.getName().startsWith("Ranging potion"));

        boolean insufficientFood = Inventory.count("Shark") < 12;

        boolean insufficientDarts = Equipment.count("Rune dart") < 120;

        boolean missingChaps = !Equipment.contains("Green d'hide chaps");

        boolean missingVambraces = !Equipment.contains("Green d'hide vambraces");

        log("Restocking Check:");
        log("- Missing teleport: " + missingTeleport);
        log("- Missing burning amulet: " + missingBurningAmulet);
        log("- Insufficient food: " + insufficientFood);
        log("- Missing potion: " + missingPotion);
        log("- Insufficient darts: " + insufficientDarts);
        log("- Missing chaps: " + missingChaps);
        log("- Missing vambraces: " + missingVambraces);

        return missingTeleport || missingBurningAmulet || insufficientFood || missingPotion || insufficientDarts || missingChaps || missingVambraces;
    }



    private static boolean isMatchingVariant(Item item, String baseName) {
        if (item == null) return false;
        String itemName = item.getName().toLowerCase();
        return itemName.startsWith(baseName.toLowerCase());
    }
    public static boolean withdrawAnyAmuletVariant(String baseName, int quantity) {
        for (int i = 1; i <= 6; i++) { // Check for all potential variants (1-6)
            String variantName = baseName + "(" + i + ")";
            if (Bank.contains(variantName)) {
                log("Withdrawing: " + variantName);
                return Bank.withdraw(variantName, quantity);
            }
        }
        log("No variants of " + baseName + " found in bank.");
        return false;
    }
    /**
     * Checks if a teleport amulet has charges.
     * @param amuletName The name of the amulet to check.
     * @return True if the amulet has at least one charge remaining.
     */
    public static boolean hasCharges(String amuletName) {
        return Inventory.get(item -> item.getName().startsWith(amuletName) && getCharges(item) > 0) != null;
    }

    /**
     * Extracts the number of charges left on an amulet based on its name.
     * @param item The amulet item.
     * @return The number of charges, or 0 if parsing fails.
     */
    private static int getCharges(Item item) {
        try {
            String name = item.getName();
            return Integer.parseInt(name.replaceAll("\\D", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    // *** INVENTORY MANAGEMENT ***

    /**
     * Cleans the inventory by dropping all items except the ones specified.
     * @param keepCondition A predicate defining which items to keep.
     */
    public static void cleanInventory(Predicate<Item> keepCondition) {
        Inventory.all().stream() // Use Inventory.all() to get a stream of items
                .filter(item -> !keepCondition.test(item)).filter(Objects::nonNull) // Drop items not matching the condition
                .forEach(item -> Inventory.drop(item.getName()));
    }


    /**
     * Ensures the inventory has the specified amount of a required item.
     * @param itemName The name of the item.
     * @param requiredAmount The required amount.
     * @return True if the inventory meets the requirement, false otherwise.
     */
    public static boolean hasRequiredAmount(String itemName, int requiredAmount) {
        return Inventory.count(itemName) >= requiredAmount;
    }


    /**
     * Equips a teleport amulet if not already equipped.
     * @param amuletName The name of the amulet to equip.
     */
    public static void equipTeleportAmulet(String amuletName) {
        if (Inventory.contains(amuletName) && !Equipment.contains(amuletName)) {
            Inventory.interact(amuletName, "Wear");
            Sleep.sleepUntil(() -> Equipment.contains(amuletName), 2000);
        }
    }
}
