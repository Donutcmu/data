import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HumanTurnHelper {
    private static Scanner sc = new Scanner(System.in);

    public static void handleHumanActions(KombatGame game, Player currentPlayer) {
        boolean done = false;
        while(!done) {
            System.out.println("\n=== " + currentPlayer.getName() + "'s turn ===");
            System.out.println("Budget: " + currentPlayer.getBudget() + " | [B]uy hex | [S]pawn minion | [C]ommand minions | [M]oney | [D]one");
            System.out.print("Choose action: ");
            String action = sc.nextLine().trim().toLowerCase();

            switch(action) {
                case "b":
                case "buy":
                    handleBuyHex(game, currentPlayer);
                    break;
                case "s":
                case "spawn":
                    handleSpawn(game, currentPlayer);
                    break;
                case "c":
                case "command":
                    commandAllMinions(game, currentPlayer);
                    break;
                case "m":
                case "money":
                    // แสดงงบ
                    System.out.println(currentPlayer.getName() + " has " + currentPlayer.getBudget() + " budget.");
                    break;
                case "d":
                case "done":
                    done = true;
                    break;
                default:
                    System.out.println("Unknown command.");
            }
            // เมื่อกด done ออกจากลูป => เทิร์นของ player นี้จบ
        }
    }

    private static void handleBuyHex(KombatGame game, Player player) {
        long cost = game.getConfig().getHexPurchaseCost();
        if (player.getBudget() < cost) {
            System.out.println("Not enough budget to buy a hex.");
            return;
        }
        // ยังไม่มี logic เลือกพิกัดเป็นจริงเป็นจัง
        // สมมติซื้อสำเร็จ ถ้าเงินพอ
        player.setBudget(player.getBudget() - cost);
        System.out.println(player.getName() + " bought a new spawnable hex (not fully implemented).");
    }

    private static void handleSpawn(KombatGame game, Player player) {
        // เปลี่ยนข้อความเชิญให้พิมพ์ row col type เช่น "2 1 MELEE"
        System.out.print("Enter row col type (e.g. '2 1 MELEE'): ");
        String line = sc.nextLine().trim();
        String[] tokens = line.split("\\s+");
        if (tokens.length < 3) {
            System.out.println("Invalid input. Usage: row col type");
            return;
        }
        try {
            int row = Integer.parseInt(tokens[0]);
            int col = Integer.parseInt(tokens[1]);
            // ชนิดมินเนียน เช่น MELEE, RANGED, TANK, ASSASSIN
            String typeStr = tokens[2].toUpperCase();
            MinionType chosenType = MinionType.valueOf(typeStr);
            // สมมติว่าเราเก็บ cost/hp/defenseFactor ไว้ใน MinionType แล้ว

            // สั่ง spawn
            game.spawnMinion(player, row, col, chosenType, new SampleRandomStrategy());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format for row/col.");
        } catch (IllegalArgumentException e) {
            // จะเกิดถ้าพิมพ์ typeStr ไม่ตรงกับใน enum
            System.out.println("Invalid minion type. Must be one of: MELEE, RANGED, TANK, ASSASSIN");
        }
    }

    /**
     * ให้ผู้เล่นเลือกมินเนียนทีละตัว แล้วสั่งได้สูงสุด 3 คำสั่ง
     * ถ้าสั่งครบ 3 หรือพิมพ์ done ในระหว่างนั้น => จบการบังคับมินเนียนตัวนั้น
     * เมื่อมินเนียนตัวนั้นถูกบังคับแล้ว จะไม่สามารถเลือกมินเนียนตัวนั้นได้อีกในเทิร์นนี้
     * ถ้าบังคับมินเนียนทุกตัวเสร็จ หรือผู้เล่นไม่ต้องการบังคับต่อ => จบ
     */
    public static boolean commandAllMinions(KombatGame game, Player player) {
        // ดึงมินเนียนทั้งหมดตามลำดับการ addMinion(...)
        List<Minion> allMinions = player.getMinions();

        // สร้างลิสต์ใหม่เฉพาะตัวที่ยังไม่ตาย
        List<Minion> aliveOrder = new ArrayList<>();
        for (Minion mm : allMinions) {
            if (mm.isAlive()) {
                aliveOrder.add(mm);
            }
        }
        if (aliveOrder.isEmpty()) {
            System.out.println("No minions to command.");
            return false;
        }

        // **วนตามลำดับใน aliveOrder** โดยไม่ให้ผู้เล่นเลือก index
        for (int i = 0; i < aliveOrder.size(); i++) {
            Minion chosen = aliveOrder.get(i);
            System.out.println("\nCommanding minion #" + (i+1) + " (created earlier) at ("
                    + (chosen.getRow()+1) + "," + (chosen.getCol()+1) + ") HP=" + chosen.getHp());
            // สั่งได้สูงสุด 3 คำสั่ง
            commandSingleMinion(game, player, chosen);
        }

        // มาถึงตรงนี้แปลว่า “สั่งครบทุกตัว”
        System.out.println("All minions have been commanded in order.");
        return true;
    }


    /**
     * บังคับมินเนียนตัวเดียวได้สูงสุด 3 คำสั่ง
     */
    private static void commandSingleMinion(KombatGame game, Player player, Minion m) {
        int count = 0;
        while (count < 3) {
            if (!m.isAlive()) {
                System.out.println("This minion is dead. Stop commanding.");
                break;
            }
            System.out.println("\nCommanding minion at (" + (m.getRow()+1) + "," + (m.getCol()+1) + "), HP=" + m.getHp()
                    + " | Budget: " + player.getBudget());
            System.out.println("Enter command (move <dir> / shoot <dir> <x> / done): ");
            String line = sc.nextLine().trim().toLowerCase();
            if (line.equals("done")) {
                // หยุดบังคับ
                break;
            } else if (line.startsWith("move")) {
                // ตัวอย่าง: move up
                String[] tokens = line.split("\\s+");
                if (tokens.length != 2) {
                    System.out.println("Usage: move <direction>");
                    continue;
                }
                String dir = tokens[1];
                // move มีค่าใช้จ่าย 1 (ตามตัวอย่าง)
                if (player.getBudget() >= 1) {
                    player.setBudget(player.getBudget() - 1);
                    game.doMove(m, dir);
                } else {
                    System.out.println("Not enough budget to move.");
                }
            } else if (line.startsWith("shoot")) {
                // ตัวอย่าง: shoot up 10
                String[] tokens = line.split("\\s+");
                if (tokens.length != 3) {
                    System.out.println("Usage: shoot <direction> <expenditure>");
                    continue;
                }
                String dir = tokens[1];
                long expend;
                try {
                    expend = Long.parseLong(tokens[2]);
                } catch(NumberFormatException e) {
                    System.out.println("Invalid expenditure.");
                    continue;
                }
                game.doShoot(m, dir, expend);
            } else {
                System.out.println("Unknown command. Use 'move' / 'shoot' / 'done'.");
                continue;
            }
            count++;
        }
    }
}
