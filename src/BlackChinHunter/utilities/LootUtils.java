package BlackChinHunter.utilities;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.wrappers.items.GroundItem;

import java.util.HashSet;
import java.util.Set;

public class LootUtils {

    private static final Set<String> sessionLoot = new HashSet<>();
    private static final int MIN_LOOT_VALUE = 500; // Default minimum value for loot to be considered valuable.

    /**
     * Tracks an item as looted during the session.
     *
     * @param itemName The name of the looted item.
     */
    public static void trackLoot(String itemName) {
        sessionLoot.add(itemName);
    }

    /**
     * Calculates the total value of items looted during the session.
     *
     * @return The total value of tracked loot.
     */
    public static int calculateLootValue() {
        return sessionLoot.stream()
                .mapToInt(LootUtils::getItemPrice)
                .sum();
    }

    /**
     * Gets the price of an item using LivePrices.
     *
     * @param itemName The name of the item to evaluate.
     * @return The item's price, or 0 if the price cannot be fetched.
     */
    public static int getItemPrice(String itemName) {
        try {
            return LivePrices.get(itemName);
        } catch (Exception e) {
            return 0; // If price fetching fails, assume 0 value.
        }
    }

    /**
     * Determines if a GroundItem is valuable based on its price and the preset coin value.
     *
     * @param groundItem The ground item to evaluate.
     * @return True if the item's price exceeds the preset minimum value.
     */
    public static boolean isValuable(GroundItem groundItem) {
        if (groundItem == null) return false; // Ensure the item is not null.
        String itemName = groundItem.getName();
        int itemAmount = groundItem.getAmount();

        // Validate name and amount
        if (itemName == null || itemAmount <= 0) return false;

        // Fetch price and calculate total value
        int price = getItemPrice(itemName);
        int totalValue = price * itemAmount;

        // Check if the item's total value meets the threshold
        return totalValue >= MIN_LOOT_VALUE;
    }

    /**
     * Finds the closest valuable ground item based on the preset coin value.
     *
     * @return The closest valuable ground item, or null if none are found.
     */
    public static GroundItem findValuableLoot() {
        return GroundItems.closest(item -> {
            if (item == null) return false; // Null check
            String itemName = item.getName();
            int itemAmount = item.getAmount();

            // Validate name and amount
            if (itemName == null || itemAmount <= 0) return false;

            // Fetch price and calculate total value
            int price = getItemPrice(itemName);
            int totalValue = price * itemAmount;

            // Check if the item's total value meets the threshold
            return totalValue >= MIN_LOOT_VALUE;
        });
    }


    /**
     * Picks up a valuable ground item if one exists nearby.
     *
     * @return True if a valuable item was picked up, false otherwise.
     */
    public static boolean pickUpValuableLoot() {
        GroundItem valuableLoot = findValuableLoot();
        if (valuableLoot != null && valuableLoot.interact("Take")) {
            trackLoot(valuableLoot.getName()); // Track the item as looted for session value calculation.
            return true;
        }
        return false;
    }
}
