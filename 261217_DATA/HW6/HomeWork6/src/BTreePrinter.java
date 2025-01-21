public class BTreePrinter  {
    private PrintStream outStream = System.out;

    private int hspace = 2;

    public void setPrintStream(PrintStream outStream) {
        this.outStream = outStream;
    }


    public void printTree(Node root) {
        List<TreeLine> treeLines = buildTreeLines(root);
        printTreeLines(treeLines);
    }



    private void printTreeLines(List<TreeLine> treeLines) {
        if (treeLines.size() > 0) {
            int minLeftOffset = minLeftOffset(treeLines);
            int maxRightOffset = maxRightOffset(treeLines);
            for (TreeLine treeLine : treeLines) {
                int leftSpaces = -(minLeftOffset - treeLine.leftOffset);
                int rightSpaces = maxRightOffset - treeLine.rightOffset;
                outStream.println(spaces(leftSpaces) + treeLine.line + spaces(rightSpaces));
            }
        }
    }

    private List<TreeLine> buildTreeLines(Node root) {
        if (root == null) return Collections.emptyList();
        else {
            String rootLabel = ""+root.key;
            List<TreeLine> leftTreeLines = buildTreeLines(root.left);
            List<TreeLine> rightTreeLines = buildTreeLines(root.right);

            int leftCount = leftTreeLines.size();
            int rightCount = rightTreeLines.size();
            int minCount = Math.min(leftCount, rightCount);
            int maxCount = Math.max(leftCount, rightCount);

            // The left and right subtree print representations have jagged edges, and we essentially we have to
            // figure out how close together we can bring the left and right roots so that the edges just meet on
            // some line.  Then we add hspace, and round up to next odd number.
            int maxRootSpacing = 0;
            for (int i = 0; i < minCount; i++) {
                int spacing = leftTreeLines.get(i).rightOffset - rightTreeLines.get(i).leftOffset;
                if (spacing > maxRootSpacing) maxRootSpacing = spacing;
            }
            int rootSpacing = maxRootSpacing + hspace;
            if (rootSpacing % 2 == 0) rootSpacing++;
            // rootSpacing is now the number of spaces between the roots of the two subtrees

            List<TreeLine> allTreeLines = new ArrayList<>();

            // strip ANSI escape codes to get length of rendered string. Fixes wrong padding when labels use ANSI escapes for colored nodes.
            String renderedRootLabel = rootLabel.replaceAll("\\e\\[[\\d;]*[^\\d;]", "");

            // add the root and the two branches leading to the subtrees

            allTreeLines.add(new TreeLine(rootLabel, -(renderedRootLabel.length() - 1) / 2, renderedRootLabel.length() / 2));

            // also calculate offset adjustments for left and right subtrees
            int leftTreeAdjust = 0;
            int rightTreeAdjust = 0;

            if (leftTreeLines.isEmpty()) {
                // there's a right subtree only
                if (!rightTreeLines.isEmpty()) {
                    
                
                    allTreeLines.add(new TreeLine("\u2514\u2510", 0, 1));
                    rightTreeAdjust = 1;
                        
                    
                }
            } else if (rightTreeLines.isEmpty()) {
                // there's a left subtree only
                
                allTreeLines.add(new TreeLine("\u250C\u2518", -1, 0));
                leftTreeAdjust = -1;
                
            } else {
                // there's a left and right subtree
                
                int adjust = (rootSpacing / 2) + 1;
                String horizontal = String.join("", Collections.nCopies(rootSpacing / 2, "\u2500"));
                String branch = "\u250C" + horizontal + "\u2534" + horizontal + "\u2510";
                allTreeLines.add(new TreeLine(branch, -adjust, adjust));
                rightTreeAdjust = adjust;
                leftTreeAdjust = -adjust;
                
            }

            // now add joined lines of subtrees, with appropriate number of separating spaces, and adjusting offsets

            for (int i = 0; i < maxCount; i++) {
                TreeLine leftLine, rightLine;
                if (i >= leftTreeLines.size()) {
                    // nothing remaining on left subtree
                    rightLine = rightTreeLines.get(i);
                    rightLine.leftOffset += rightTreeAdjust;
                    rightLine.rightOffset += rightTreeAdjust;
                    allTreeLines.add(rightLine);
                } else if (i >= rightTreeLines.size()) {
                    // nothing remaining on right subtree
                    leftLine = leftTreeLines.get(i);
                    leftLine.leftOffset += leftTreeAdjust;
                    leftLine.rightOffset += leftTreeAdjust;
                    allTreeLines.add(leftLine);
                } else {
                    leftLine = leftTreeLines.get(i);
                    rightLine = rightTreeLines.get(i);
                    int adjustedRootSpacing = (rootSpacing == 1 ? 1 : rootSpacing);
                    TreeLine combined = new TreeLine(leftLine.line + spaces(adjustedRootSpacing - leftLine.rightOffset + rightLine.leftOffset) + rightLine.line,
                            leftLine.leftOffset + leftTreeAdjust, rightLine.rightOffset + rightTreeAdjust);
                    allTreeLines.add(combined);
                }
            }
            return allTreeLines;
        }
    }

    private static int minLeftOffset(List<TreeLine> treeLines) {
        return treeLines.stream().mapToInt(l -> l.leftOffset).min().orElse(0);
    }

    private static int maxRightOffset(List<TreeLine> treeLines) {
        return treeLines.stream().mapToInt(l -> l.rightOffset).max().orElse(0);
    }

    private static String spaces(int n) {
        return String.join("", Collections.nCopies(n, " "));
    }

    private static class TreeLine {
        String line;
        int leftOffset;
        int rightOffset;

        TreeLine(String line, int leftOffset, int rightOffset) {
            this.line = line;
            this.leftOffset = leftOffset;
            this.rightOffset = rightOffset;
        }
    }

}
