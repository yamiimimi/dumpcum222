package BlackChinHunter.modules.banking;

import BlackChinHunter.Framework.Branch;
import BlackChinHunter.utilities.InventoryUtils;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.utilities.Sleep;

import static org.dreambot.api.utilities.Logger.log;

public class NavigateToBankLeaf extends Branch {

    @Override
    public boolean validate() {
        // Check if the bank is not open or if we're not near the closest bank
        BankLocation closestBank = Bank.getClosestBankLocation();
        return !Bank.isOpen() && (closestBank == null || !closestBank.getArea(0).contains(org.dreambot.api.methods.interactive.Players.getLocal()));
    }

    @Override
    public int execute() {
        log("Navigating to the nearest bank...");

        // Get the closest bank location
        BankLocation closestBank = Bank.getClosestBankLocation();

        if (closestBank == null) {
            log("No valid bank locations found!");
            return 300;
        }

        // Handle teleport options mapped to specific BankLocation
        if (tryTeleportToBank(closestBank)) {
            waitForArrival(closestBank);
            return 300;
        }

        // Fallback to walking to the nearest bank
        log("Walking to the nearest bank: " + closestBank.name());
        if (Bank.open(closestBank)) {
            Sleep.sleepUntil(Bank::isOpen, 5000);
        } else {
            log("Failed to walk to the bank.");
        }

        return 300;
    }

    /**
     * Attempts to use a teleport item to reach the specified bank location.
     *
     * @param bankLocation The target bank location.
     * @return True if the teleport was successful, false otherwise.
     */
    private boolean tryTeleportToBank(BankLocation bankLocation) {
        switch (bankLocation) {
            case EDGEVILLE:
                return InventoryUtils.useTeleport("Amulet of glory", "Rub", "Edgeville");
            case GRAND_EXCHANGE:
                return InventoryUtils.useTeleport("Ring of wealth", "Rub", "Grand Exchange");
            case CASTLE_WARS:
                return InventoryUtils.useTeleport("Ring of dueling", "Rub", "Castle Wars");
            default:
                log("No teleport option available for: " + bankLocation.name());
                return false;
        }
    }

    /**
     * Waits until the player has arrived at the specified bank location.
     *
     * @param bankLocation The target bank location.
     */
    private void waitForArrival(BankLocation bankLocation) {
        log("Waiting for arrival at " + bankLocation.name() + "...");
        Sleep.sleepUntil(() -> bankLocation.getArea(0).contains(org.dreambot.api.methods.interactive.Players.getLocal()), 5000);
        log("Arrived at " + bankLocation.name() + " bank.");
    }
}
