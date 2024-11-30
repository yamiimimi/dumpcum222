package BlackChinHunter.modules.inventory;

import BlackChinHunter.Framework.Branch;
import BlackChinHunter.modules.banking.BankingBranch;

import static org.dreambot.api.utilities.Logger.log;

public class InventoryBranch extends Branch {

    private final InventoryFoodLeaf inventoryFoodLeaf = new InventoryFoodLeaf();
    private final CheckEquipmentLeaf checkEquipmentLeaf = new CheckEquipmentLeaf();

    @Override
    public boolean validate() {
        // Always validate the inventory branch for food and equipment checks
        return true;
    }

    @Override
    public int execute() {
        // Check for food
        if (inventoryFoodLeaf.validate()) {
            log("Executing InventoryFoodLeaf...");
            return inventoryFoodLeaf.execute();
        }

        // Check for Rune Darts
        if (checkEquipmentLeaf.validate()) {
            log("Executing CheckEquipmentLeaf...");
            return checkEquipmentLeaf.execute();
        }

        log("Inventory and equipment checks passed. Continuing gameplay...");
        return 300; // Short delay before next validation
    }
}
