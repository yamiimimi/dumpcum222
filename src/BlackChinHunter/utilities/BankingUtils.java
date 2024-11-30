package BlackChinHunter.utilities;


import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.Item;

import java.util.List;
import java.util.function.Predicate;

import static org.dreambot.api.utilities.Logger.log;

public class BankingUtils {

    /**
     * Opens the nearest bank and waits until it is open.
     * @return True if the bank is successfully opened, false otherwise.
     */
    public static boolean openBank() {
        if (Bank.open()) {
            Sleep.sleepUntil(Bank::isOpen, 3000);
            return true;
        }
        log("Failed to open the bank.");
        return false;
    }

    /**
     * Deposits all items in the inventory except for the specified items.
     * @param keepCondition A predicate defining which items to keep.
     */
    public static void depositAllExcept(Predicate<Item> keepCondition) {
        Bank.all().stream()
                .filter(item -> item != null && !keepCondition.test(item)) // Null-check added here
                .forEach(item -> Bank.depositAll(item.getName()));
    }
    /**
     * Withdraws a specific item from the bank.
     * @param itemName The name of the item to withdraw.
     * @param amount The amount to withdraw (use -1 for all).
     * @return True if the item was successfully withdrawn, false otherwise.
     */
    public static boolean withdrawItem(String itemName, int amount) {
        if (!Bank.isOpen() && !openBank()) {
            return false;
        }
        if (Bank.contains(itemName)) {
            if (amount == -1) {
                return Bank.withdrawAll(itemName);
            } else {
                return Bank.withdraw(itemName, amount);
            }
        } else {
            log("Item not found in bank: " + itemName);
        }
        return false;
    }

    /**
     * Checks if the bank contains a specific item and quantity.
     * @param itemName The name of the item to check.
     * @param quantity The required quantity.
     * @return True if the bank contains the required quantity, false otherwise.
     */
    public static boolean bankContains(String itemName, int quantity) {
        return Bank.contains(itemName) && Bank.count(itemName) >= quantity;
    }

    /**
     * Restocks items by withdrawing them from the bank if needed.
     * @param itemName The name of the item to restock.
     * @param requiredAmount The amount required in the inventory.
     */
    public static void restockItem(String itemName, int requiredAmount) {
        if (!Inventory.contains(itemName) || Inventory.count(itemName) < requiredAmount) {
            int toWithdraw = requiredAmount - Inventory.count(itemName);
            withdrawItem(itemName, toWithdraw);
        }
    }

    /**
     * Closes the bank if it is currently open.
     */
    public static void closeBank() {
        if (Bank.isOpen()) {
            Bank.close();
            Sleep.sleepUntil(() -> !Bank.isOpen(), 1000);
        }
    }
}
