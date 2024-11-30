package BlackChinHunter.modules.looting;

import BlackChinHunter.Framework.Branch;
import BlackChinHunter.modules.banking.BankingBranch;
import BlackChinHunter.modules.combat.ResetLeaf;

import static org.dreambot.api.utilities.Logger.log;

public class LootingBranch extends Branch {

    private final PickupLootLeaf pickupLootLeaf = new PickupLootLeaf(); // Handles loot pickup
    private final EvaluateLootLeaf evaluateLootLeaf = new EvaluateLootLeaf(); // Evaluates loot value

    @Override
    public boolean validate() {
        // Always validate looting branch
        return true;
    }

    @Override
    public int execute() {
        // First, attempt to pick up valuable loot
        if (pickupLootLeaf.validate()) {
            log("Executing PickupLootLeaf...");
            return pickupLootLeaf.execute();
        }

        // Then, evaluate the inventory's loot value
        if (evaluateLootLeaf.validate()) {
            log("Executing EvaluateLootLeaf...");
            evaluateLootLeaf.execute();
        }

        // Decide next action based on evaluated loot value
        int inventoryLootValue = evaluateLootLeaf.getInventoryLootValue();
        if (inventoryLootValue >= 320000) {
            log("Loot value exceeds threshold. Transitioning to BankingBranch...");
            return new BankingBranch().execute();
        } else {
            log("Loot value below threshold. Transitioning to ResetLeaf...");
            return new ResetLeaf().execute();
        }
    }
}
