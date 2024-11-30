package BlackChinHunter.modules.banking;

import BlackChinHunter.Framework.Branch;
import BlackChinHunter.utilities.BankingUtils;
import BlackChinHunter.utilities.InventoryUtils;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.utilities.Sleep;

import java.util.Arrays;

import static org.dreambot.api.utilities.Logger.log;

public class WithdrawSuppliesLeaf extends Branch {

    private static final int REQUIRED_SHARKS = 12;
    private static final int REQUIRED_RUNE_DARTS = 120;
    private boolean suppliesWithdrawn = false;

    @Override
    public boolean validate() {
        // Check for missing or low supplies
        boolean needsGlory = !InventoryUtils.hasUsableTeleport(Arrays.asList("Amulet of glory")) &&
                !Equipment.contains(item -> item != null && item.getName().startsWith("Amulet of glory"));
        boolean needsBurningAmulet = !Inventory.contains(item -> item != null && item.getName().startsWith("Burning amulet"));
        boolean needsFood = Inventory.count("Shark") < REQUIRED_SHARKS;
        boolean needsRangingPotion = !Inventory.contains(item -> item != null && item.getName().startsWith("Ranging potion"));
        boolean needsRuneDarts = Equipment.count("Rune dart") < REQUIRED_RUNE_DARTS;
        boolean needsGreenDhideChaps = !Equipment.contains("Green d'hide chaps");
        boolean needsGreenDhideVambraces = !Equipment.contains("Green d'hide vambraces");


        return needsGlory || needsBurningAmulet || needsFood || needsRangingPotion || needsRuneDarts || needsGreenDhideChaps || needsGreenDhideVambraces;
    }

    @Override
    public int execute() {
        log("Restocking supplies...");

        if (!BankingUtils.openBank()) {
            log("Failed to open the bank. Retrying...");
            return 300;
        }

        // Restock Amulet of Glory
        if (!InventoryUtils.hasUsableTeleport(Arrays.asList("Amulet of glory")) &&
                !Equipment.contains(item -> item != null && item.getName().startsWith("Amulet of glory"))) {
            log("Restocking Amulet of Glory...");
            if (!InventoryUtils.withdrawAnyAmuletVariant("Amulet of glory", 1)) {
                log("No variants of Amulet of Glory found in bank.");
            } else {
                // After withdrawing, attempt to equip the amulet if available in inventory
                if (Inventory.contains(item -> item != null && item.getName().startsWith("Amulet of glory"))) {
                    log("Equipping Amulet of Glory...");
                    if (Inventory.interact(item -> item.getName().startsWith("Amulet of glory"), "Wear")) {
                        Sleep.sleepUntil(() -> Equipment.contains(item -> item != null && item.getName().startsWith("Amulet of glory")), 2000);
                        log("Successfully equipped Amulet of Glory.");
                    } else {
                        log("Failed to equip Amulet of Glory.");
                    }
                } else {
                    log("Amulet of Glory not found in inventory after withdrawal.");
                }
            }
        }

        // Restock Burning Amulet
        if (!Inventory.contains(item -> item != null && item.getName().startsWith("Burning amulet"))) {
            log("Restocking Burning Amulet...");
            if (!InventoryUtils.withdrawAnyAmuletVariant("Burning amulet", 1)) {
                log("No variants of Burning Amulet found in bank.");
            }
        }

        // Restock Sharks
        if (Inventory.count("Shark") < REQUIRED_SHARKS) {
            int toWithdraw = REQUIRED_SHARKS - Inventory.count("Shark");
            log("Restocking Sharks (" + toWithdraw + " needed)...");
            if (!BankingUtils.withdrawItem("Shark", toWithdraw)) {
                log("No Sharks found in bank!");
            }
        }

        // Restock Ranging Potion
        if (!Inventory.contains(item -> item != null && item.getName().startsWith("Ranging potion"))) {
            log("Restocking Ranging Potion...");
            if (!BankingUtils.withdrawItem("Ranging potion(1)", 1) &&
                    !BankingUtils.withdrawItem("Ranging potion(2)", 1) &&
                    !BankingUtils.withdrawItem("Ranging potion(3)", 1) &&
                    !BankingUtils.withdrawItem("Ranging potion(4)", 1)) {
                log("No Ranging Potion found in bank!");
            }
        }

        // Restock Rune Darts
        int equippedDarts = Equipment.count("Rune dart");
        if (equippedDarts < REQUIRED_RUNE_DARTS) {
            int dartsNeeded = REQUIRED_RUNE_DARTS - equippedDarts;
            log("Restocking Rune Darts (" + dartsNeeded + " needed)...");
            if (BankingUtils.withdrawItem("Rune dart", dartsNeeded)) {
                log("Equipping Rune Darts...");
                Inventory.interact("Rune dart", "Wield");
                Sleep.sleepUntil(() -> Equipment.count("Rune dart") >= REQUIRED_RUNE_DARTS, 2000);
            } else {
                log("No Rune Darts found in bank!");
            }
        }

        // Restock and Equip Green D'hide Chaps
        if (!Equipment.contains("Green d'hide chaps")) {
            log("Restocking Green D'hide Chaps...");
            if (BankingUtils.withdrawItem("Green d'hide chaps", 1)) {
                log("Equipping Green D'hide Chaps...");
                Inventory.interact("Green d'hide chaps", "Wear");
                Sleep.sleepUntil(() -> Equipment.contains("Green d'hide chaps"), 2000);
            } else {
                log("No Green D'hide Chaps found in bank!");
            }
        }

        // Restock and Equip Green D'hide Vambraces
        if (!Equipment.contains("Green d'hide vambraces")) {
            log("Restocking Green D'hide Vambraces...");
            if (BankingUtils.withdrawItem("Green d'hide vambraces", 1)) {
                log("Equipping Green D'hide Vambraces...");
                Inventory.interact("Green d'hide vambraces", "Wear");
                Sleep.sleepUntil(() -> Equipment.contains("Green d'hide vambraces"), 2000);
            } else {
                log("No Green D'hide Vambraces found in bank!");
            }

            suppliesWithdrawn = true; // Mark as completed
        }

        // Restock supplies (existing logic)

        // Close the bank after completing all withdrawals
        if (suppliesWithdrawn) {
            log("Supplies withdrawn successfully. Closing bank...");
            BankingUtils.closeBank();
        } else {
            log("Supplies not fully withdrawn. Attempting again...");
        }

        return 300;
    }
}