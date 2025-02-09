public class RangedMinion implements MinionKind{
    @Override
    public String getName() { return "Ranged"; }
    @Override
    public int getDefenseFactor() { return 0; }
    @Override
    public String getStrategy() {
        // ตัวอย่าง if/then/else + block
        return ""
                + "if (1) then {\n"
                + "    move up;\n"
                + "} else {\n"
                + "    move down;\n"
                + "}\n"
                + "done;";
    }
}
