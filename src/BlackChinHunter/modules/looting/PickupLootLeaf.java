package BlackChinHunter.modules.looting;

import BlackChinHunter.Framework.Branch;
import BlackChinHunter.utilities.LootUtils;
import org.dreambot.api.utilities.Sleep;

import static org.dreambot.api.utilities.Logger.log;

public class PickupLootLeaf extends Branch {

    @Override
    public boolean validate() {
        // Validate if there is valuable loot to pick up
        return LootUtils.findValuableLoot() != null;
    }

    @Override
    public int execute() {
        log("Attempting to pick up valuable loot...");

        // Try picking up the valuable loot
        if (LootUtils.pickUpValuableLoot()) {
            log("Loot picked up successfully.");
            Sleep.sleepUntil(() -> LootUtils.findValuableLoot() == null, 2000); // Wait until loot is cleared
        } else {
            log("Failed to pick up loot.");
        }

        return 300; // Short delay before the next validation
    }
}
