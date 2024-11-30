package BlackChinHunter.modules.looting;

import BlackChinHunter.Framework.Branch;
import BlackChinHunter.utilities.InventoryUtils;
import BlackChinHunter.utilities.LootUtils;

import static org.dreambot.api.utilities.Logger.log;

public class EvaluateLootLeaf extends Branch {

    private int inventoryLootValue = 0; // Store the calculated value of loot in the inventory

    @Override
    public boolean validate() {
        // Always validate to continuously calculate the inventory's loot value
        return true;
    }

    @Override
    public int execute() {
        // Calculate the total loot value in the inventory
        inventoryLootValue = calculateInventoryLootValue();
        log("Current inventory loot value: " + inventoryLootValue);
        return 300; // Short delay before recalculating
    }

    /**
     * Calculates the total value of loot in the player's inventory.
     *
     * @return The total value of loot in the inventory.
     */
    private int calculateInventoryLootValue() {
        return InventoryUtils.getInventoryItems().stream()
                .filter(item -> !InventoryUtils.isEssentialItem(item)) // Exclude essential items
                .mapToInt(item -> LootUtils.getItemPrice(item.getName()) * item.getAmount())
                .sum();
    }

    /**
     * Gets the most recently calculated inventory loot value.
     *
     * @return The inventory loot value.
     */
    public int getInventoryLootValue() {
        return inventoryLootValue;
    }
}
