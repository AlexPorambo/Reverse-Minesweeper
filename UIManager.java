package reverseminesweeper;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.paint.Color;

/**
 * The UIManager class manages the game's graphical user interfaces.
 * It handles rendering the game grid, process user interactions(clicks),
 * updating tile visual effects, and managing the game's progress for win conditions.
 * 
 * @author Alex Porambo
 * @version 4/27/25
 */
public class UIManager 
{
    private int gridSize; // The size of the game grid.
    private GridPane gameGrid; // Represents the visual game grid.
    private double timeElapsed; // Tracks the elapsed time in milliseconds.
    private GameController gameController; // Handles game logic.
    private ReverseMinesweeperApp app; // Reference to the main application instance.
    
    /**
     * Constructs a UIManager object, initializing the game grid and logic.
     * 
     * @param gridSize The size of the game grid.
     * @param bombCount The number of bombs to be placed.
     * @param app Reference to the main Reverse Minesweeper App.
     */
    public UIManager(int gridSize, int bombCount, ReverseMinesweeperApp app) 
    {
        this.gridSize = gridSize;
        this.app = app;

        // Initialize the game logic
        this.gameController = new GameController(gridSize, bombCount);
    }
    
    /**
     * Initializes the game UI, creating and displaying the game grid layout.
     * 
     * @return A BorderPane containing the game grid.
     */
    public BorderPane initializeGameUI() 
    {
        BorderPane layout = new BorderPane();

        gameGrid = new GridPane();
        gameGrid.setPadding(new Insets(10));
        gameGrid.setHgap(1);
        gameGrid.setVgap(1);
        gameGrid.setAlignment(Pos.CENTER);

        layout.setCenter(gameGrid);
        updateGrid();
        
        return layout;
    }

    /**
     * Updates the game grid and display.
     * Determines the right tile sizes based on the selected difficulty level.
     */
    private void updateGrid() 
    {
        gameGrid.getChildren().clear();
        double tileSize = 0;

        // Determine tile size based on difficulty picked
        if (gridSize == 5)
        {
        	tileSize += 85;
        }
        if (gridSize == 10)
        {
        	tileSize += 50;
        }
        if (gridSize == 15)
        {
        	tileSize += 33;
        }
        if (gridSize == 20)
        {
        	tileSize += 25;
        }
  
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                Button tile = new Button(" ");
                
                Image tileImage = new Image(getClass().getResourceAsStream("/Images/MINE_BLANK.png"));
                ImageView tileIcon = new ImageView(tileImage);
                tileIcon.fitWidthProperty().bind(tile.widthProperty());
                tileIcon.fitHeightProperty().bind(tile.heightProperty());
                tileIcon.setPreserveRatio(true);
                tileIcon.setSmooth(true);
                tile.setGraphic(tileIcon);
                tile.setText("");
                tile.setStyle("-fx-padding: 0; -fx-background-color: transparent");
                
                tile.setPrefSize(tileSize, tileSize);
                tile.setMaxSize(tileSize, tileSize);
                tile.setMinSize(tileSize, tileSize);

                int newRow = row;
                int newCol = col;
                tile.setOnAction(e -> {
                	playSound("retro-select-236670.mp3");
                handleTileClick(tile, newRow, newCol);});
                gameGrid.add(tile, col, row);

                // If the cell is a bomb, show it right away
                if (gameController.isBomb(row, col))
                {
                    Image bombImage = new Image(getClass().getResourceAsStream("/Images/MINE_BOMB.png"));
                    ImageView bombIcon = new ImageView(bombImage);
                    bombIcon.fitWidthProperty().bind(tile.widthProperty());
                    bombIcon.fitHeightProperty().bind(tile.heightProperty());
                    bombIcon.setPreserveRatio(true);
                    bombIcon.setSmooth(true);
                    tile.setGraphic(bombIcon);
                    tile.setText("");
                    tile.setStyle("-fx-padding: 0; -fx-background-color: transparent;");
                }
            }
        }
    }
    
    /**
     * Handles when the user clicks on tiles, updating the tile's appearance.
     * Also applies cheat mode effects and checks for win conditions.
     * 
     * @param tile The button representing the game tile.
     * @param row The row index of the tile.
     * @param col The column index of the tile.
     * 
     * @coauthor Michael Arculeo
     */
    private void handleTileClick(Button tile, int row, int col) 
    {
        if (!gameController.isBomb(row, col)) 
        {
            int currentTileValue = gameController.getBoard()[row][col];
            
            // Cycles threw each valid number(s)
            if (currentTileValue == 0) 
            {
                currentTileValue = 1;
            } 
            else if (currentTileValue >= 1 && currentTileValue < 8) 
            {
                currentTileValue++;
            } 
            else if (currentTileValue == 8) 
            {
                currentTileValue = 0;
            }

            gameController.setUserValue(row, col, currentTileValue);

            // Load correct number image
            String imagePath = (currentTileValue == 0)
                ? "/Images/MINE_BLANK.png"
                : "/Images/MINENUM" + currentTileValue + ".png";

            Image tileImage = new Image(getClass().getResourceAsStream(imagePath));
            ImageView tileIcon = new ImageView(tileImage);
            tileIcon.fitWidthProperty().bind(tile.widthProperty());
            tileIcon.fitHeightProperty().bind(tile.heightProperty());
            tileIcon.setPreserveRatio(true);
            tileIcon.setSmooth(true);

            // Apply cheat tint if cheats ON 
            boolean showCheats = gameController.isShowCheatsOn();
            if (showCheats) 
            {
                boolean isCorrect = gameController.checksCorrectChoice(row, col, gameController.getBoard()[row][col]);

                Color tintColor = isCorrect ? Color.LIGHTGREEN : Color.TOMATO;

                // Grayscale base
                ColorAdjust monochrome = new ColorAdjust();
                monochrome.setSaturation(0);

                // Color overlay bound to the button size
                ColorInput tint = new ColorInput(0, 0, tile.getWidth(), tile.getHeight(), tintColor.deriveColor(0, 1, 1, 0.5));

                // Listen for resizing
                tile.widthProperty().addListener((obs, oldVal, newVal) -> tint.setWidth(newVal.doubleValue()));
                tile.heightProperty().addListener((obs, oldVal, newVal) -> tint.setHeight(newVal.doubleValue()));

                Blend blend = new Blend(BlendMode.MULTIPLY, monochrome, tint);

                tileIcon.setEffect(blend);
            } 
            else 
            {
                tileIcon.setEffect(null); // No effect if cheats off
            }

            // Apply to tile
            tile.setGraphic(tileIcon);
            tile.setText("");
            tile.setStyle("-fx-padding: 0; -fx-background-color: transparent;");

            // Check for win condition 
            if (gameController.checkWin()) 
            {
                app.stopTimerAndShowWinScreen();
                app.showWinScreen(app.getElapsedTime());
            }
        }
    }
    
    /**
     * Retrieves the elapsed time in milliseconds.
     * 
     * @return The elapsed game time.
     */
    public double getElapsedTime() 
	{
	    return timeElapsed;
	}
    
    /**
     * Click sound effect.
     * PLays a sound effect from the audio file.
     * 
     * @param fileName The name of the sound file to play.
     */
    private void playSound(String fileName) 
	{
	    Media sound = new Media(getClass().getResource("/Music/" + fileName).toExternalForm());
	    MediaPlayer sfxPlayer = new MediaPlayer(sound);
	    sfxPlayer.setVolume(0.8);
	    sfxPlayer.play();
	}
}