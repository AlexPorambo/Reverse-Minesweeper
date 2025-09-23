package reverseminesweeper;

import java.util.Random;

/**
 * The GameController class contains the game back end game logic for Reverse Minesweeper.  
 * Which includes random bomb placement, updating the board, 
 * and the final win condition check.
 * 
 * @author Michael Arculeo
 * @version 4/27/25
 */
public class GameController
{
	private int[][] board; // 2D Array, represents the game board.
	private int[][] correctAnswerNums; // 2D Array, stores the correct values for each tile.
    private boolean[][] isTileLocked; // Tracks whether a tile has been "locked in" by the user.
    private int gridSize; // Size of the game grid.
    private int bombCount; // Number of bombs on the game grid.
    
    /**
     * This constructs a game controller with the specified grid size and number of bombs.
     * Creates the game board and places the bombs and numbers. 
     * 
     * @param gridSize The size of the grid.
     * @param bombCount The number of bombs to be placed on the grid. 
     */
    public GameController(int gridSize, int bombCount)
    {
        this.gridSize = gridSize;
        this.bombCount = bombCount;
        board = new int[gridSize][gridSize];
        isTileLocked = new boolean[gridSize][gridSize];

        bombLocations();
        placeNumbers();
    }

    /**
     * Randomly assigns bombs to the board.
     * Checks that a given position is empty before placing bombs.
     * Ensures bombs are placed only in the empty tiles.
     */
    private void bombLocations() 
    {
        Random random = new Random();
        int bombsPlaced = 0;

        while (bombsPlaced < bombCount) 
        {
            int row = random.nextInt(gridSize);
            int col = random.nextInt(gridSize);

            // Checks for bomb
            if (board[row][col] != -1) 
            {
                board[row][col] = -1; // represent bomb with -1
                bombsPlaced++;
            }
        }
    }

    /**
     * Iterates through the board, checking empty tiles.
     * Assigns numbers to tiles on the board based on adjacent bomb counts.
     * Skips bomb tiles.
     */
    private void placeNumbers() 
    {
    	correctAnswerNums = new int[gridSize][gridSize];
    	
        for (int row = 0; row < gridSize; row++) 
        {
            for (int col = 0; col < gridSize; col++) 
            {	
                if (board[row][col] == -1) 
                {
                	correctAnswerNums[row][col] = -1;
                    continue; // skip bombs
                }

                int count = 0;
                for (int i = -1; i <= 1; i++)
                {
                    for (int j = -1; j <= 1; j++)
                    {
                        int newRow = row + i;
                        int newCol = col + j;

                        if (isValid(newRow, newCol) && board[newRow][newCol] == -1)
                        {
                            count++;
                        }
                    }
                }
                correctAnswerNums[row][col] = count;
            }
        }
    }
    
    /**
     * Keeps track of whether the user chosen number matches the correct answer for that tile.
     * 
     * @param row The row number of the tile.
     * @param col The column number of the tile. 
     * @param userChosenNumber The number chosen by the user. 
     * @return True if the user choice is correct, false otherwise. 
     */
    public boolean checksCorrectChoice(int row, int col, int userChosenNumber)
    {
    	System.out.println("Checking correct chocie for (" + row + ", " + col + ")");
    	System.out.println("Correct answer: " + correctAnswerNums[row][col]);
    	System.out.println("User entered: " + userChosenNumber);
    	boolean result = correctAnswerNums[row][col] == userChosenNumber;
    	System.out.println("Match? " + result);
    	return result;
    }

    /**
     * Confirms that a given tile position is within the bounds of the grid.
     * Makes sure the grid is not out of bounds.
     * 
     * @param row The row number to check.
     * @param col The column number to check.
     * @return True if the tile position is valid, false otherwise.
     */
    private boolean isValid(int row, int col) 
    {
        return row >= 0 && row < gridSize && col >= 0 && col < gridSize;
    }
    
    /**
     * Makes sure the cheat mode is enabled in the game settings.
     * 
     * @return True if cheat mode is enabled, false otherwise.
     */
    public boolean isShowCheatsOn()
    {
    	return GameSettings.getMode().isShowCheatsOn();
    }
    
    /**
     * Getter, gets the current state of the game board.
     * 
     * @return A 2D Array representing the board.
     */
    public int[][] getBoard()
    {
        return board;
    }
    
    /**
     * Lets the user set a value at a specified grid position.
     * Allows the user to set a value if the tile is not locked and is not bomb.
     * 
     * @param row The row number of the tile.
     * @param col The column number of the tile.
     * @param userValue The value to be set in the tile by the user.
     */
    public void setUserValue(int row, int col, int userValue) 
    {
        if (!isTileLocked[row][col] && board[row][col] != -1)
        {
            board[row][col] = userValue;
        }
    }

    /**
     * Checks if any given tile contains a bomb.
     * 
     * @param row The row number of the tile.
     * @param col The column number of the tile.
     * @return True if the tile is a bomb, false otherwise.
     */
    public boolean isBomb(int row, int col) 
    {
        return board[row][col] == -1;
    }
    
    /**
     * Checks if the player has won the game.
     * The player can win the game when all non bomb tiles contain the correct numbers.
     * 
     * @return True if the player has won, false otherwise.
     */
    public boolean checkWin() 
    {
        for (int row = 0; row < gridSize; row++) 
        {
            for (int col = 0; col < gridSize; col++) 
            {
                if (board[row][col] != -1 && board[row][col] != correctAnswerNums[row][col])
                {
                    return false; // mismatch in a non-bomb tile
                }
            }
        }
        return true;
    }
}