import BlackChinHunter.Framework.Root;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;

@ScriptManifest(
        author = "YourName",
        category = Category.COMBAT,
        name = "BlackChinHunter",
        version = 1.0,
        description = "Hunts Black Chinchompas, engages in PvP, loots, and banks dynamically."
)
public class BlackChinHunter extends AbstractScript {

    private Root root; // The main Root that orchestrates script execution

    @Override
    public void onStart() {
        log("BlackChinHunter script starting...");
        try {
            root = new Root(); // Initialize the root
            if (root == null) {
                throw new IllegalStateException("Failed to initialize Root.");
            }
            log("Root initialized successfully.");
        } catch (Exception e) {
            log("Error during script startup: " + e.getMessage());
            e.printStackTrace();
            stop(); // Stop the script if initialization fails
        }
    }

    @Override
    public int onLoop() {
        try {
            if (root != null) {
                log("Running root...");
                root.run(); // Run the root to execute branches dynamically
            } else {
                throw new IllegalStateException("Root is null.");
            }
        } catch (Exception e) {
            log("An error occurred in the main loop: " + e.getMessage());
            e.printStackTrace();
            stop(); // Stop the script if an unexpected exception occurs
        }
        return 300; // A short delay between loops
    }

    @Override
    public void onExit() {
        log("BlackChinHunter script exiting. Cleaning up...");
        // Perform any necessary cleanup logic here, if applicable
        log("Cleanup complete.");
    }
}
