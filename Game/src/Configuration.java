import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Configuration {
    private Map<String, Long> configValues;

    public Configuration() {
        configValues = new HashMap<>();
    }

    public void loadFromFile(String filePath) {
        File file = new File(filePath);
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] tokens = line.split("=");
                if (tokens.length == 2) {
                    String name = tokens[0].trim();
                    long value = Long.parseLong(tokens[1].trim());
                    configValues.put(name, value);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Configuration file not found: " + filePath);
        } catch (NumberFormatException e) {
            System.err.println("Invalid number in config file.");
        }
    }

    public long getValue(String key, long defaultVal) {
        return configValues.getOrDefault(key, defaultVal);
    }

    public long getSpawnCost() {
        return getValue("spawn_cost", 100L);
    }

    public long getHexPurchaseCost() {
        return getValue("hex_purchase_cost", 1000L);
    }

    public long getInitBudget() {
        return getValue("init_budget", 10000L);
    }

    public long getInitHP() {
        return getValue("init_hp", 100L);
    }

    public long getTurnBudget() {
        return getValue("turn_budget", 90L);
    }

    public long getMaxBudget() {
        return getValue("max_budget", 23456L);
    }

    public long getInterestPct() {
        return getValue("interest_pct", 5L);
    }

    public long getMaxTurns() {
        return getValue("max_turns", 69L);
    }

    public long getMaxSpawns() {
        return getValue("max_spawns", 47L);
    }
}
