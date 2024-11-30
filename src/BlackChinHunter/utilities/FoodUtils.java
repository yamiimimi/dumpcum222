package BlackChinHunter.utilities;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.wrappers.items.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.dreambot.api.utilities.Logger.log;

public class FoodUtils {

    // Define food and their healing values
    private static final Map<String, Integer> FOOD_ITEMS = new HashMap<>();

    static {
        FOOD_ITEMS.put("Shark", 20);
        FOOD_ITEMS.put("Lobster", 12);
        FOOD_ITEMS.put("Swordfish", 14);
        FOOD_ITEMS.put("Monkfish", 16);
        FOOD_ITEMS.put("Tuna", 10);
        FOOD_ITEMS.put("Trout", 7);
        FOOD_ITEMS.put("Salmon", 9);
        FOOD_ITEMS.put("Anglerfish", 22);
        FOOD_ITEMS.put("Karambwan", 18);

        // PVP Blighted Foods
        FOOD_ITEMS.put("Blighted manta ray", 22);
        FOOD_ITEMS.put("Blighted karambwan", 18);
        FOOD_ITEMS.put("Blighted anglerfish", 22);

        // Saradomin Brews (Partial doses)
        FOOD_ITEMS.put("Saradomin brew (4)", 15);
        FOOD_ITEMS.put("Saradomin brew (3)", 11);
        FOOD_ITEMS.put("Saradomin brew (2)", 7);
        FOOD_ITEMS.put("Saradomin brew (1)", 3);
    }

    /**
     * Checks if the given item is a type of food.
     * @param itemName The name of the item to check.
     * @return True if the item is food, false otherwise.
     */
    public static boolean isFood(String itemName) {
        return itemName != null && FOOD_ITEMS.containsKey(itemName);
    }

    /**
     * Counts the total number of food items in the inventory.
     * @return The total count of food in the inventory.
     */
    public static long countFoodInInventory() {
        log("Counting food items in inventory...");
        long foodCount = Inventory.all().stream()
                .filter(Objects::nonNull) // Ensure null items are excluded
                .filter(FoodUtils::isFood) // Check if the item is a recognized food
                .count();
        log("Food count in inventory: " + foodCount);
        return foodCount;
    }

    private static boolean isFood(Item item) {
        if (item == null) {
            return false;
        }
        String itemName = item.getName().toLowerCase();
        return itemName.contains("shark") || itemName.contains("lobster") || itemName.contains("monkfish");
    }
}
