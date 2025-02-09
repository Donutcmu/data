public class AssassinMinion implements MinionKind{
    @Override
    public String getName() { return "Assassin"; }
    @Override
    public int getDefenseFactor() { return 1; }
    @Override
    public String getStrategy() {
        // x = 10;
        // while(x) {
        //    x = x - 1;
        //    move up;
        // }
        // shoot down 5;
        // done;
        return ""
                + "x = 4;\n"
                + "while(x) {\n"
                + "    x = x - 1;\n"
                + "    move downleft;\n"
                + "    move upleft;\n"
                + "shoot up 1;\n"
                + "}\n"
                + "shoot up 1;\n"
                + "done;";
    }
}
