public class TankMinion implements MinionKind{
    @Override
    public String getName() { return "Tank"; }
    @Override
    public int getDefenseFactor() { return 5; }
    @Override
    public String getStrategy() {
        // while(3) { move up; }
        // done;
        return "x = 3;"
                + "while(x) {\n"
                + "    shoot down 1;\n"
                + "     x = x - 1\n"
                + "}\n"
                + "done;";
    }

}
