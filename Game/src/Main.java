import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // 1) โหลด config
        Configuration config = new Configuration();
        config.loadFromFile("C:\\Users\\ASUS ROG\\IdeaProjects\\Game\\src\\config");

        // 2) สร้างเกม
        KombatGame game = new KombatGame(config);

        // 3) ให้ผู้ใช้เลือกโหมด
        System.out.println("Select game mode: 1) Duel  2) Solitaire  3) Auto");
        int mode = sc.nextInt();
        sc.nextLine(); // consume leftover newline
        if (mode == 1) {
            game.setGameMode(GameMode.DUEL);
        } else if (mode == 2) {
            game.setGameMode(GameMode.SOLITAIRE);
        } else {
            game.setGameMode(GameMode.AUTO);
        }

        // 4) ตั้งชื่อผู้เล่น
        if (game.getGameMode() == GameMode.AUTO) {
            // บอท vs บอท
            game.initPlayers("BotA", "BotB");
        } else if (game.getGameMode() == GameMode.SOLITAIRE) {
            // คนคนเดียว + บอท
            System.out.print("Enter your name: ");
            String name = sc.nextLine().trim();
            game.initPlayers(name, "Bot");
        } else {
            // DUEL: คน vs คน
            System.out.print("Enter Player1 name: ");
            String name1 = sc.nextLine().trim();
            System.out.print("Enter Player2 name: ");
            String name2 = sc.nextLine().trim();
            game.initPlayers(name1, name2);
        }

        // 5) ตัวอย่าง spawn มินเนียนเริ่มต้นให้แต่ละฝั่ง (ไม่บังคับ)
        // สมมติวางที่มุม
        game.spawnMinion(game.getPlayer1(), 0, 0, MinionType.MELEE, new SampleRandomStrategy());
        game.spawnMinion(game.getPlayer2(), 7,  7, MinionType.ASSASSIN, new SampleRandomStrategy());

        // 6) วนเทิร์นสลับกันจนจบ
        while(!game.isGameOver()) {
            // เทิร์นผู้เล่น 1
            if (!game.isGameOver()) {
                game.doTurn(game.getPlayer1());
            }
            // เทิร์นผู้เล่น 2
            if (!game.isGameOver()) {
                game.doTurn(game.getPlayer2());
            }
        }

        // 7) ประกาศผล
        String winner = game.getWinner();
        System.out.println("Game Over! Winner = " + winner);
    }
}
