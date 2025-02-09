import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Config {
    long spawnCost;
    long hexPurchaseCost;
    long initBudget;
    long initHp;
    long turnBudget;
    long maxBudget;
    long interestPct;
    long maxTurns;
    long maxSpawns;

    public static Config fromFile(String filename) throws IOException {
        Config c = new Config();
        Properties props = new Properties();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            props.load(br);
        }
        c.spawnCost       = Long.parseLong(props.getProperty("spawn_cost", "100"));
        c.hexPurchaseCost = Long.parseLong(props.getProperty("hex_purchase_cost", "1000"));
        c.initBudget      = Long.parseLong(props.getProperty("init_budget", "10000"));
        c.initHp          = Long.parseLong(props.getProperty("init_hp", "100"));
        c.turnBudget      = Long.parseLong(props.getProperty("turn_budget", "90"));
        c.maxBudget       = Long.parseLong(props.getProperty("max_budget", "23456"));
        c.interestPct     = Long.parseLong(props.getProperty("interest_pct", "5"));
        c.maxTurns        = Long.parseLong(props.getProperty("max_turns", "69"));
        c.maxSpawns       = Long.parseLong(props.getProperty("max_spawns", "47"));
        return c;
    }
}
