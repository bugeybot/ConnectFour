import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// we are going to create a simple 2-players Connect Four implementation in Java 8
/*
  Nathaniel Pedro Marco Pedro Was here :)
*/
public class ConnectFour {

  // we define characters for players (R for Red, Y for Yellow)
  private static final char[] PLAYERS = new char[2];
  // dimensions for our board
  private final int width, height;
  // grid for the board
  private final char[][] grid;
  // we store last move made by a player
  private int lastCol = -1, lastTop = -1;

  public ConnectFour(int w, int h, char player1, char player2) {
    this.width = w;
    this.height = h;
    this.grid = new char[h][];

    // init the grid will blank cell
    for (int i = 0; i < h; i++) {
      Arrays.fill(this.grid[i] = new char[w], '.');
    }

    PLAYERS[0] = player1;
    PLAYERS[1] = player2;
  }

  // we use Streams to make a more concise method 
  // for representing the board
  @Override
  public String toString() {
    return IntStream.range(0, this.width).
           mapToObj(Integer::toString).
           collect(Collectors.joining(" ")) + 
           "\n" +
           Arrays.stream(this.grid).
           map(String::new).
           collect(Collectors.joining("\n"));
  }

  // get string representation of the row containing 
  // the last play of the user
  public String horizontal() {
    return new String(this.grid[this.lastTop]);
  }

  // get string representation fo the col containing 
  // the last play of the user
  public String vertical() {
    StringBuilder sb = new StringBuilder(this.height);

    for (int h = 0; h < this.height; h++) {
      sb.append(this.grid[h][this.lastCol]);
    }

    return sb.toString();
  }

  // get string representation of the "/" diagonal 
  // containing the last play of the user
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

  // get string representation of the "\" 
  // diagonal containing the last play of the user
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

  // static method checking if a substring is in str
  public static boolean contains(String str, String substring) {
    return str.indexOf(substring) >= 0;
  }

  // now, we create a method checking if last play is a winning play
  public boolean isWinningPlay() {
    if (this.lastCol == -1) {
      System.err.println("No move has been made yet");
      return false;
    }

    char sym = this.grid[this.lastTop][this.lastCol];
    // winning streak with the last play symbol
    String streak = String.format("%c%c%c%c", sym, sym, sym, sym);

    // check if streak is in row, col, 
    // diagonal or backslash diagonal
    return contains(horizontal(), streak) || 
           contains(vertical(), streak) || 
           contains(slashDiagonal(), streak) || 
           contains(backslashDiagonal(), streak);
  }

  // prompts the user for a column, repeating until a valid choice is made
  public void chooseAndDrop(char symbol, int col) {
    do {
      System.out.println("\nPlayer " + symbol + " turn: ");
      
      // check if column is ok
      if (!(0 <= col && col < this.width)) {
        System.out.println("Column must be between 0 and " + (this.width - 1));
        continue;
      }

      // now we can place the symbol to the first 
      // available row in the asked column
      for (int h = this.height - 1; h >= 0; h--) {
        if (this.grid[h][col] == '.') {
        this.grid[this.lastTop = h][this.lastCol = col] = symbol;
          return;
        }
      }

      // if column is full ==> we need to ask for a new input
      System.out.println("Column " + col + " is full.");
    } while (true);
  }

  public ConnectFour showBoard() {
    return this;
  }

  public int getWidth() {
    return this.width;
  }

  public int getHeight() {
    return this.height;
  }

  public int getMoves() {
    return this.width * this.height;
  }
  
  public String determineWin(char symbol) {
      //we need to check if a player won. If not, 
      // we continue, otherwise, we display a message
      if (this.isWinningPlay()) {
          System.out.println("\nPlayer " + symbol + " wins!");
          return "\nPlayer " + symbol + " wins!";
          }
          return "";
  }

  public String playerTurn(char symbol, int col) {
    //symbol for current player already gathered
    
    // we ask user to choose a column
    this.chooseAndDrop(symbol, col);
    
    // we return the board
    return this.showBoard().toString();
    
  }

  public static void playGame(int height, int width, int moves) {
    //we assemble all the pieces of the puzzle for 
    // building our Con nect Four Game
    try (Scanner input = new Scanner(System.in)) {
      // we create the ConnectFour instance
      ConnectFour board = new ConnectFour(width, height, 'R', 'Y');
    
      // we explain users how to enter their choices
      System.out.println("Use 0-" + (width - 1) + " to choose a column");
      // we display initial board
      System.out.println(board.showBoard());
    
      // we iterate until max nb moves be reached
      // simple trick to change player turn at each iteration
      for (int player = 0; moves-- > 0; player = 1 - player) {
      int col = 1;
        board.playerTurn(PLAYERS[player], col);
      }
      System.out.println("Game over. No winner. Try again!");
    }
  }
   //Test function
   public static void main(String[] args) {
       //we define some variables for our game like 
       // dimensions and nb max of moves
       int height = 6; int width = 8; int moves = height * width;
       playGame(height, width, moves);
   }
}