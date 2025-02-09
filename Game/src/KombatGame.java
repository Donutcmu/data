import java.util.Iterator;
import java.util.List;

public class KombatGame {
    private HexBoard board;
    private Configuration config;
    private Player player1;
    private Player player2;
    private long turnCount;
    private GameMode gameMode;
    public GameMode getGameMode() {
        return this.gameMode;
    }
    private int[] getHexDelta(int row, int col, String direction) {
        switch (direction.toLowerCase()) {
            case "up":         return new int[]{-1,  0};
            case "down":       return new int[]{1,  0};
            case "upleft":     return new int[]{-1, -1};
            case "upright":    return new int[]{-1, 1};
            case "downleft":   return new int[]{1, -1};
            case "downright":  return new int[]{1, 1};
            default:
                return new int[]{0,0};
        }
    }

    public KombatGame(Configuration config) {
        this.config = config;
        this.board = new HexBoard();
        this.turnCount = 0;
        this.gameMode = GameMode.DUEL; // default
    }

    public void setGameMode(GameMode mode) {
        this.gameMode = mode;
    }

    public void initPlayers(String name1, String name2) {
        long initBudget = config.getInitBudget();
        long maxBudget = config.getMaxBudget();
        long maxSpawns = config.getMaxSpawns();

        this.player1 = new Player(name1, initBudget, maxBudget, maxSpawns);
        this.player2 = new Player(name2, initBudget, maxBudget, maxSpawns);

        // กำหนดช่องเริ่มต้นของ Player1
        // ตัวอย่างตามรูป: (row=1,col=1), (1,2), (2,1), (1,3), (2,2)
        // หมายเหตุ: อาจต้องปรับ index ให้ตรงกับ 0-based หรือ 1-based
        claimHexCell(player1, 0, 0);
        claimHexCell(player1, 0, 1);
        claimHexCell(player1, 1, 0);
        claimHexCell(player1, 0, 2);
        claimHexCell(player1, 1, 1);

        // กำหนดช่องเริ่มต้นของ Player2
        // เช่น (row=7,col=7), (7,6), (6,7), (8,7), (7,8) แล้วแต่การดีไซน์
        // หรือถ้า array เป็น 0-7 => (7,7), (7,6), (6,7), (7,5), (6,6) เป็นต้น
        claimHexCell(player2, 7, 7);
        claimHexCell(player2, 7, 6);
        claimHexCell(player2, 6, 7);
        claimHexCell(player2, 7, 5);
        claimHexCell(player2, 6, 6);
    }
    private void claimHexCell(Player p, int r, int c) {
        HexCell cell = board.getCell(r, c);
        if (cell != null) {
            cell.setOwner(p);
        }
    }

    public boolean canSpawnAt(int row, int col) {
        HexCell cell = board.getCell(row, col);
        return (cell != null && !cell.isOccupied());
    }

    public void spawnMinion(Player player, int row, int col, MinionType type, MinionStrategy strategy) {
        HexCell cell = board.getCell(row, col);
        if (cell == null) {
            System.out.println("Invalid cell (" + row + "," + col + ")");
            return;
        }
        // ตรวจสอบ ownership
        if (cell.getOwner() != player) {
            System.out.println("You can only spawn on a cell you own.");
            return;
        }
        // ตรวจสอบว่าไม่มี occupant อยู่แล้ว
        if (cell.isOccupied()) {
            System.out.println("This cell is already occupied by another minion!");
            return;
        }
        // ตรวจสอบงบ
        long cost = type.getCost();
        if (player.getBudget() < cost) {
            System.out.println("Not enough budget to spawn " + type);
            return;
        }
        // หักงบ + spawn
        player.setBudget(player.getBudget() - cost);
        player.incrementSpawnsUsed();

        Minion m = new Minion(player, row, col, type.getBaseHp(), type.getDefenseFactor(), strategy);
        player.addMinion(m);
        cell.setOccupant(m); // ผูก occupant กับ minion
        System.out.println(player.getName() + " spawned a " + type + " at (" + (row+1) + "," + (col+1) + ")");
    }


    public boolean canMoveTo(int row, int col) {
        if (!board.isInsideBoard(row, col)) return false;
        return !board.getCell(row, col).isOccupied();
    }

    // เมธอดสำหรับสั่ง move ตาม string direction แบบง่าย ๆ
    public void doMove(Minion minion, String direction) {
        Player owner = minion.getOwner();
        long costOfMove = 1;
        if (owner.getBudget() < costOfMove) {
            System.out.println("Not enough budget.");
            return;
        }

        int row = minion.getRow();   // 0..7
        int col = minion.getCol();   // 0..7

        // ตัวอย่าง offset แบบ 6 ทิศ (ไม่สน parity)
        int dr=0, dc=0;
        switch(direction) {
            case "up":         dr=-1; dc=0;  break;
            case "down":       dr=1;  dc=0;  break;
            case "upleft":     dr=-1; dc=-1; break;
            case "upright":    dr=-1; dc=1;  break;
            case "downleft":   dr=1;  dc=-1; break;
            case "downright":  dr=1;  dc=1;  break;
            default:
                System.out.println("Invalid direction.");
                return;
        }

        int newRow = row + dr;
        int newCol = col + dc;

        // เช็กขอบ 0..7
        if (newRow < 0 || newRow > 7 || newCol < 0 || newCol > 7) {
            System.out.println("Cannot move " + direction + ": out of board range (0..7).");
            return;
        }

        HexCell targetCell = board.getCell(newRow, newCol); // array [newRow][newCol]
        if (targetCell.isOccupied()) {
            System.out.println("Cell(" + newRow + "," + newCol + ") is occupied.");
            return;
        }

        // หักงบ
        owner.setBudget(owner.getBudget() - costOfMove);

        // ย้าย occupant
        board.getCell(row, col).setOccupant(null);
        targetCell.setOccupant(minion);
        minion.setPosition(newRow, newCol);

        // แสดงผลเป็น 1..8 ตอนพิมพ์
        System.out.println(owner.getName() + "'s minion moves from ("
                + (row+1) + "," + (col+1) + ") " + direction + " to ("
                + (newRow+1) + "," + (newCol+1) + ")");
    }

    public void buyHex(Player player, int row, int col) {
        HexCell cell = board.getCell(row, col);
        if (cell == null) {
            System.out.println("Invalid cell.");
            return;
        }
        if (cell.getOwner() != null) {
            System.out.println("This cell is already owned by someone.");
            return;
        }

        // เช็คว่า adjacent มีของ player อยู่หรือเปล่า
        boolean adjacentOwned = false;
        for (HexCell adj : board.getAdjacentCells(row, col)) {
            if (adj.getOwner() == player) {
                adjacentOwned = true;
                break;
            }
        }
        if (!adjacentOwned) {
            System.out.println("You can only buy a cell adjacent to your owned cell.");
            return;
        }

        long cost = config.getHexPurchaseCost();
        if (player.getBudget() < cost) {
            System.out.println("Not enough budget to buy this cell.");
            return;
        }

        // ซื้อสำเร็จ
        player.setBudget(player.getBudget() - cost);
        cell.setOwner(player);
        System.out.println(player.getName() + " bought cell (" + row + "," + col + ") for " + cost
                + ". Remaining budget: " + player.getBudget());
    }
    public void doShoot(Minion attacker, String direction, long expenditure) {
        Player owner = attacker.getOwner();
        long costOfAttack = expenditure + 1;

        if (owner.getBudget() < costOfAttack) {
            System.out.println("Not enough budget to shoot => need " + costOfAttack);
            return;
        }
        owner.setBudget(owner.getBudget() - costOfAttack);

        int r = attacker.getRow();
        int c = attacker.getCol();
        int[] delta = getHexDelta(r, c, direction);
        int nr = r + delta[0];
        int nc = c + delta[1];

        // ถ้านอกกระดาน => ยิงลอย
        if (!board.isInsideBoard(nr, nc)) {
            System.out.println("Shot off-board => wasted. Remaining: " + owner.getBudget());
            return;
        }

        HexCell targetCell = board.getCell(nr, nc);
        if (!targetCell.isOccupied()) {
            System.out.println("No minion at (" + nr + "," + nc + ") => wasted shot. Remaining: " + owner.getBudget());
            return;
        }

        // มีมินเนียน
        Minion target = targetCell.getOccupant();
        long oldHP = target.getHp();
        long df = target.getDefenseFactor();
        long damage = Math.max(1, expenditure - df);
        long newHP = Math.max(0, oldHP - damage);
        target.setHp(newHP);

        System.out.println(owner.getName() + " shoots at (" + nr + "," + nc + ") => cost "
                + costOfAttack + ", damage=" + damage + ", HP: " + oldHP + " -> " + newHP);

        if (newHP <= 0) {
            target.getOwner().removeMinion(target);
            targetCell.setOccupant(null);
            System.out.println("The minion is destroyed!");
        }
    }

    public void doTurn(Player currentPlayer) {
        turnCount++;
        // 1) เพิ่มงบ + ดอกเบี้ย
        currentPlayer.setBudget(currentPlayer.getBudget() + config.getTurnBudget());
        currentPlayer.applyInterest(turnCount, config.getInterestPct());

        // 2) ถ้าเป็นบอท (Auto) หรือผู้เล่นในโหมด Solitaire ที่เป็นบอท
        boolean isBot = isBotPlayer(currentPlayer);

        if (isBot) {
            // ให้มินเนียนแต่ละตัวใช้ strategy ของตัวเอง (ตัวอย่าง)
            for (Iterator<Minion> it = currentPlayer.getMinions().iterator(); it.hasNext();) {
                Minion m = it.next();
                if (!m.isAlive()) {
                    it.remove();
                    continue;
                }
                m.getStrategy().executeTurn(this, m);
            }
        } else {
            // มนุษย์: เลือกว่าจะซื้อ Hex, spawn, หรือออกคำสั่งมินเนียน
            // (ตัวอย่างสั้น ๆ ในที่นี้จะให้ใช้ console input)
            HumanTurnHelper.handleHumanActions(this, currentPlayer);
        }
    }

    // เช็คว่า player นี้เป็นบอทหรือไม่ ขึ้นกับโหมดและว่าคนไหน
    private boolean isBotPlayer(Player p) {
        // ในที่นี้สมมติว่า Player1 กับ Player2
        // ถ้าโหมด DUEL => ทั้งคู่เป็นมนุษย์
        // ถ้าโหมด SOLITAIRE => player2 เป็นบอท, player1 เป็นมนุษย์
        // ถ้าโหมด AUTO => ทั้งคู่เป็นบอท
        if (gameMode == GameMode.DUEL) {
            return false;
        } else if (gameMode == GameMode.SOLITAIRE) {
            if (p == player2) return true;
            return false;
        } else if (gameMode == GameMode.AUTO) {
            return true;
        }
        return false;
    }

    public boolean isGameOver() {
        // ถ้าฝ่ายใดไม่มีมินเนียน => จบ
        if (player1.getMinions().isEmpty() || player2.getMinions().isEmpty()) {
            return true;
        }
        // หรือถึง maxTurns
        if (turnCount >= config.getMaxTurns()) {
            return true;
        }
        return false;
    }

    public String getWinner() {
        // แบบเดียวกับตัวอย่างเดิม
        if (player1.getMinions().isEmpty() && !player2.getMinions().isEmpty()) {
            return player2.getName();
        } else if (player2.getMinions().isEmpty() && !player1.getMinions().isEmpty()) {
            return player1.getName();
        } else if (player1.getMinions().isEmpty() && player2.getMinions().isEmpty()) {
            return "Tie";
        }

        // เทียบจำนวน
        int p1Count = player1.getMinions().size();
        int p2Count = player2.getMinions().size();
        if (p1Count > p2Count) {
            return player1.getName();
        } else if (p2Count > p1Count) {
            return player2.getName();
        }
        // ถ้าเท่ากันดู sumHP
        long p1HP = player1.getMinions().stream().mapToLong(Minion::getHp).sum();
        long p2HP = player2.getMinions().stream().mapToLong(Minion::getHp).sum();
        if (p1HP > p2HP) {
            return player1.getName();
        } else if (p2HP > p1HP) {
            return player2.getName();
        }
        // ถ้ายังเท่ากันอีกดู budget
        long p1Budget = player1.getBudget();
        long p2Budget = player2.getBudget();
        if (p1Budget > p2Budget) {
            return player1.getName();
        } else if (p2Budget > p1Budget) {
            return player2.getName();
        }
        return "Tie";
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public long getTurnCount() {
        return turnCount;
    }

    public HexBoard getBoard() {
        return board;
    }

    public Configuration getConfig() {
        return config;
    }
}
