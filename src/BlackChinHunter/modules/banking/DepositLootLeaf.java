package BlackChinHunter.modules.banking;

import BlackChinHunter.Framework.Branch;
import BlackChinHunter.utilities.BankingUtils;
import BlackChinHunter.utilities.InventoryUtils;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.wrappers.items.Item;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.dreambot.api.utilities.Logger.log;

public class DepositLootLeaf extends Branch {

    // Define essential items
    private static final List<String> ESSENTIAL_ITEMS = Arrays.asList(
            "Amulet of glory",
            "Burning amulet",
            "Shark",
            "Ranging potion",
            "Rune dart",
            "Green d'hide chaps",
            "Green d'hide vambraces"
    );

    @Override
    public boolean validate() {
        // Validate only if there are non-essential items in the inventory
        boolean hasNonEssentialItems = Inventory.all().stream()
                .anyMatch(item -> item != null && !isEssentialItem(item)); // Ensure null-check for safety

        if (!hasNonEssentialItems) {
            log("Inventory contains only essential items. Deposit complete.");
        }

        return hasNonEssentialItems;
    }

    @Override
    public int execute() {
        log("Depositing loot...");
        if (!BankingUtils.openBank()) {
            log("Failed to open the bank. Retrying...");
            return 300;
        }

        // Deposit all non-essential items
        BankingUtils.depositAllExcept(InventoryUtils::isEssentialItem);

        // Check if inventory is clean
        if (Inventory.all().stream().filter(Objects::nonNull).allMatch(InventoryUtils::isEssentialItem)) {
            log("Inventory contains only essential items. Deposit complete.");
        }

        BankingUtils.closeBank();
        return 300;
    }
    /**
     * Determines if an item is essential and should not be deposited.
     * @param item The item to evaluate.
     * @return True if the item is essential, false otherwise.
     */
    private boolean isEssentialItem(Item item) {
        if (item == null) return false; // Prevent null items from causing errors
        String itemName = item.getName();

        // Check if the item is an Amulet of Glory or Burning Amulet variant
        if (itemName.startsWith("Amulet of glory") || itemName.startsWith("Burning amulet")) {
            return true;
        }

        // Check against the essential items list
        return ESSENTIAL_ITEMS.stream().anyMatch(itemName::startsWith);
    }


    /**
     * Helper method to check if an item matches an amulet variant.
     *
     * @param itemName     The item's name.
     * @param amuletBase   The base name of the amulet (e.g., "Amulet of glory").
     * @return True if the item matches the base name or its variant, false otherwise.
     */
    private boolean isAmuletVariant(String itemName, String amuletBase) {
        return itemName.startsWith(amuletBase + " (") || itemName.equals(amuletBase);
    }
}