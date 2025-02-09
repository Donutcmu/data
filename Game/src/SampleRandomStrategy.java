import java.util.Random;

public class SampleRandomStrategy implements MinionStrategy {
    private Random rand = new Random();

    @Override
    public void executeTurn(KombatGame game, Minion minion) {
        // ตัวอย่างบอทสุ่ม: ลอง move 1 ที ถ้ามีงบ
        Player owner = minion.getOwner();
        if (owner.getBudget() <= 0) {
            return; // งบหมด ทำอะไรไม่ได้
        }

        // สุ่มทิศ move
        int r = minion.getRow();
        int c = minion.getCol();
        String[] directions = {"up", "down", "left", "right", "upleft", "downright"};
        String dir = directions[rand.nextInt(directions.length)];

        // move มีค่าใช้จ่าย 1
        if (owner.getBudget() >= 1) {
            owner.setBudget(owner.getBudget() - 1);
            game.doMove(minion, dir);
        }
        // สุ่มอีก ถ้าจะ shoot อะไรสักอย่างก็เพิ่มได้
    }
}
