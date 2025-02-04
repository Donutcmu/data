package main.game;

public class GameState {
    public Board board;
    public Player player1, player2;
    public int turnCount, maxTurns;

    public GameState(Player p1, Player p2, int maxTurns) {
        this.board = new Board();
        this.player1 = p1;
        this.player2 = p2;
        this.turnCount = 0;
        this.maxTurns = maxTurns;
    }

    public void startGameLoop() {
        while (!isGameOver()) {
            processTurn(player1);
            if (isGameOver()) break;
            processTurn(player2);
        }
        declareWinner();
    }

    public void processTurn(Player player) {
        player.updateBudget();
        player.takeTurn(board);
        turnCount++;
    }

    public boolean isGameOver() {
        return player1.getMinions().isEmpty() || player2.getMinions().isEmpty() || turnCount >= maxTurns;
    }

    private void declareWinner() {
        Player winner = determineWinner();
        System.out.println(winner != null ? "Winner: " + winner.getName() : "It's a tie!");
    }

    private Player determineWinner() {
        return player1.getMinions().size() > player2.getMinions().size() ? player1 :
               player2.getMinions().size() > player1.getMinions().size() ? player2 : null;
    }

}
