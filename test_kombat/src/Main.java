//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_CYAN= "\u001B[36m";
    public static final String ANSI_GREEN= "\u001B[32m";
    public static void main(String[] args) {
        try {
            // 1) โหลด config (แก้ path ตามต้องการ)
            Config config = Config.fromFile("/Users/donut/IdeaProjects/test_kombat/src/config.txt");

            // 2) สร้าง Player
            Player p1 = new Player("Player1", config);
            Player p2 = new Player("Player2", config);

            // 3) Board 8x8
            Board board = new Board(8, 8);

            // 4) สร้าง MinionKind
            MinionKind mkMelee = new MeleeMinion();
            MinionKind mkRanged = new RangedMinion();
            MinionKind mkTank = new TankMinion();
            MinionKind mkAssassin = new AssassinMinion();

            // 5) spawn มินเนียน
            spawnMinion(p1, board, mkMelee, config, 2, 0);
            spawnMinion(p1, board, mkTank, config, 3, 0);
            spawnMinion(p2, board, mkRanged, config, 2, 7);
            spawnMinion(p2, board, mkAssassin, config, 3, 7);

            // 6) วนเทิร์น
            long turnCount = 0;
            while (turnCount < config.maxTurns) {
                turnCount++;
                System.out.println("\n=== TURN " + turnCount + " ===");
                // เทิร์น P1
                takeTurn(p1, p2, board, config);
                if (checkGameOver(p1, p2, turnCount, config)) break;

                // เทิร์น P2
                takeTurn(p2, p1, board, config);
                if (checkGameOver(p1, p2, turnCount, config)) break;
            }

            // สรุปผล
            System.out.println("\nGame ended at turn " + turnCount);
            int p1Alive = p1.countAliveMinions();
            int p2Alive = p2.countAliveMinions();
            System.out.println("Player1 alive minions: " + p1Alive);
            System.out.println("Player2 alive minions: " + p2Alive);
            if (p1Alive > p2Alive) {
                System.out.println("Player1 wins!");
            } else if (p2Alive > p1Alive) {
                System.out.println("Player2 wins!");
            } else {
                System.out.println("Draw or tie!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void spawnMinion(Player player, Board board, MinionKind kind, Config config, int row, int col) {
        if (player.getSpawnsLeft() > 0 && player.getBudget() >= config.spawnCost) {
            player.setBudget(player.getBudget() - config.spawnCost);
            player.decrementSpawn();
            Minion m = new Minion(kind, (int) config.initHp, row, col, player.getName());
            board.placeMinion(m, row, col);
            player.addMinion(m);
            System.out.println(player.getName() + " spawned a [" + kind.getName() + "] at (" + row + "," + col + ").");
        }
    }

    public static void takeTurn(Player current, Player other, Board board, Config config) {
        current.incrementTurns();
        long oldBudget = current.getBudget();
        long interest = (long) (oldBudget * (config.interestPct / 100.0));
        long newBudget = oldBudget + config.turnBudget + interest;
        if (newBudget > config.maxBudget) newBudget = config.maxBudget;
        current.setBudget(newBudget);

        // สั่งมินเนียน
        for (Minion m : current.getMinions()) {
            if (!m.isAlive()) continue;
            System.out.println("Executing minion: " + m + " Alive: " + m.isAlive());
            System.out.println("Old Budget: " + oldBudget + " -> New Budget: " + newBudget);
            m.executeStrategy(board, current, other);
        }
        board.printBoard();
    }

    private static boolean checkGameOver(Player p1, Player p2, long turnCount, Config config) {
        if (p1.countAliveMinions() == 0 || p2.countAliveMinions() == 0) {
            return true;
        }
        if (turnCount >= config.maxTurns) {
            return true;
        }
        return false;
    }
}
