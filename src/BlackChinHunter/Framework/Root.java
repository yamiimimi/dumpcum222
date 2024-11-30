package BlackChinHunter.Framework;

import BlackChinHunter.modules.banking.BankingBranch;
import BlackChinHunter.modules.combat.CombatBranch;
import BlackChinHunter.modules.looting.LootingBranch;
import BlackChinHunter.modules.inventory.InventoryBranch;

import static org.dreambot.api.utilities.Logger.log;

public class Root {

    private final BankingBranch bankingBranch = new BankingBranch();
    private final CombatBranch combatBranch = new CombatBranch();
    private final LootingBranch lootingBranch = new LootingBranch();
    private final InventoryBranch inventoryBranch = new InventoryBranch();

    public void run() {
        // Check each branch dynamically
        if (bankingBranch.validate()) {
            log("Switching to BankingBranch...");
            bankingBranch.execute();
        } else if (combatBranch.validate()) {
            log("Switching to CombatBranch...");
            combatBranch.execute();
        } else if (lootingBranch.validate()) {
            log("Switching to LootingBranch...");
            lootingBranch.execute();
        } else if (inventoryBranch.validate()) {
            log("Switching to InventoryBranch...");
            inventoryBranch.execute();
        } else {
            log("No valid branch found. Idling...");
        }
    }
}
