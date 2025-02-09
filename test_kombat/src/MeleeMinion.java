public class MeleeMinion implements MinionKind {
    @Override
    public String getName() { return "Melee"; }
    @Override
    public int getDefenseFactor() { return 2; }
    @Override
    public String getStrategy() {
        // แก้ไขเป็นคนละ statement ชัดเจน ปิดท้ายด้วย ; (optional semicolon หลัง done ก็ได้)
        return "move downleft";
    }
}
