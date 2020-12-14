import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ConnectFour {

  private static final char[] PLAYERS = new char[2];

  private final int width, height;

  private final char[][] grid;
  
  public boolean turnFailed = false, gameWon = false;

  private int lastCol = -1, lastTop = -1;

  public ConnectFour(int w, int h, char j, char k) {
    this.width = w;
    this.height = h;
    this.grid = new char[h][];

    for (int i = 0; i < h; i++) {
      Arrays.fill(this.grid[i] = new char[w], '.');
    }

    PLAYERS[0] = j;
    PLAYERS[1] = k;
  }

  public String toString() {
    return "-------\r\n" + IntStream.range(0, this.width).
           mapToObj(Integer::toString).
           collect(Collectors.joining("")) + 
           "\r\n" +
           Arrays.stream(this.grid).
           map(String::new).
           collect(Collectors.joining("\r\n")) + "\r\n-------\r\n";
  }

  public String horizontal() {
    return new String(this.grid[this.lastTop]);
  }

  public String vertical() {
    StringBuilder sb = new StringBuilder(this.height);

    for (int h = 0; h < this.height; h++) {
      sb.append(this.grid[h][this.lastCol]);
    }

    return sb.toString();
  }

  public String slashDiagonal() {
    StringBuilder sb = new StringBuilder(this.height);

    for (int h = 0; h < this.height; h++) {
      int w = this.lastCol + this.lastTop - h;

      if (0 <= w && w < this.width) {
        sb.append(this.grid[h][w]);
      }
    }

    return sb.toString();
  }

  public String backslashDiagonal() {
    StringBuilder sb = new StringBuilder(this.height);

    for (int h = 0; h < this.height; h++) {
      int w = this.lastCol - this.lastTop + h;

      if (0 <= w && w < this.width) {
        sb.append(this.grid[h][w]);
      }
    }

    return sb.toString();
  }

  public static boolean contains(String str, String substring) {
    return str.indexOf(substring) >= 0;
  }

  public boolean isWinningPlay() {
    if (this.lastCol == -1) {
      System.err.println("No move has been made yet");
      return false;
    }

    char sym = this.grid[this.lastTop][this.lastCol];
 
    String streak = String.format("%c%c%c%c", sym, sym, sym, sym);

    return contains(horizontal(), streak) || 
           contains(vertical(), streak) || 
           contains(slashDiagonal(), streak) || 
           contains(backslashDiagonal(), streak);
  }

  public String chooseAndDrop(ConnectFour board, char symbol, int col) {
    System.out.println("\nPlayer " + symbol + " turn: ");

    if (!(0 <= col && col < this.width)) {
      System.out.println("Column must be between 0 and " + (this.width - 1));
      turnFailed = true;
      return "Column must be between 0 and " + (this.width - 1) + "\r\n";
    }

    for (int h = this.height - 1; h >= 0; h--) {
      if (this.grid[h][col] == '.') {
      this.grid[this.lastTop = h][this.lastCol = col] = symbol;
        return "";
      }
    }

    System.out.println("Column " + col + " is full.");
    turnFailed = true;
    return "Column " + col + " is full." + "\r\n";
  }

  public String playerTurn(ConnectFour board, int player, int col) {
    char symbol = PLAYERS[player];

    String output = board.chooseAndDrop(board, symbol, col);

    if (board.isWinningPlay()) {
      gameWon = true;
      System.out.println("\nPlayer " + symbol + " wins!");
      return board + "\r\nPlayer " + symbol + " wins!\r\n";
    }
    return output;
  }
}