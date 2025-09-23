package reverseminesweeper;
	
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Text;

/**
 * The ReverseMinesweeperApp class contains the front end applications for the game.
 * Its sets up the UI, handles game settings like cheats, and controls for the background music.
 * 
 * @author Alex Porambo
 * @version 4/27/25
 */
public class ReverseMinesweeperApp extends Application 
{
	private UIManager uiManager; // Manages UI elements.
    private Stage primaryStage; // Main game window.
    private Timeline timer; // The game timer.
    private MediaPlayer backgroundMusic; // The background music for the game.
    private double timeElapsed; // Tracks the elapsed time during the game.
    private int currentGridSize; // Current grid size for the game.
	private int currentBombCount; // Number of bombs in the current game.
	
	/**
	 * Starts the game by initializing the main stage.
	 * Sets up full screen mode, and proceeds with launching the welcome screen.
	 * Also starts/configures the background music.
	 * 
	 * @param primaryStage The main stage for the game. 
	 */
	@Override
	public void start(Stage primaryStage) 
	{
		primaryStage.setTitle("Reverse Minesweeper");
		this.primaryStage = primaryStage;
	
	    Platform.runLater(() -> {
	        forceStageToFullScreen(primaryStage);
	        primaryStage.setResizable(true);
	        primaryStage.show();
	    });
	    
	    showWelcomeScreen(); // Set the first scene
		
		//Get BackGround Music
  		Media bgMusic = new Media(getClass().getResource("/Music/8-bit-loop-189494.mp3").toExternalForm());
  		backgroundMusic = new MediaPlayer(bgMusic);
  		backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
  		backgroundMusic.setVolume(0.5); // Optional volume setting
  		backgroundMusic.play();
  		
	}
	
	/**
	 * Initializes and displays the welcome/start screen for the game.
	 * Creates and sets up the game title, game description, difficulty selection, 
	 * start button, and options button.
	 * Also applies visual styling/textures and background music/sound effects.
	 */
	private void showWelcomeScreen() 
	{
		VBox welcomeLayout = new VBox(20);
	    welcomeLayout.getStyleClass().add("background-grey");
	    welcomeLayout.setPadding(new Insets(20));
	    welcomeLayout.setAlignment(Pos.TOP_CENTER);
	    welcomeLayout.setMaxSize(700, 800);
	    
	    
        // Game title image
        Image image = new Image(getClass().getResourceAsStream("/Images/ReverseMinesweeperTitle.png"));
        ImageView titleImage = new ImageView(image);
        titleImage.setPreserveRatio(true);
        titleImage.setFitHeight(150);
        
		Label description = new Label("Welcome to Reverse Minesweeper!\n\nYou Already know where the bombs are your job is to place the correct numbers on the tiles to reflect the number of bombs around each tile.");
		description.setWrapText(true);
		description.getStyleClass().add("subtitle-font");
		description.setAlignment(Pos.CENTER);
		
		Label modeLabel = new Label("Select Difficulty:");
		modeLabel.getStyleClass().add("label-bold");

		// Radio buttons
		ToggleGroup difficultyGroup = new ToggleGroup();
		
		RadioButton easy = new RadioButton("Easy");
		easy.setUserData(new int[]{5, 5});
		easy.setToggleGroup(difficultyGroup);
        easy.getStyleClass().add("button-style");
        addSoundEffectToRadioButton(easy);
        
		RadioButton normal = new RadioButton("Normal");
		normal.setUserData(new int[]{10, 15});
		normal.setToggleGroup(difficultyGroup);
		normal.setSelected(true); // default
		normal.getStyleClass().add("button-style");
		addSoundEffectToRadioButton(normal);

		RadioButton hard = new RadioButton("Hard");
		hard.setUserData(new int[]{15, 40});
		hard.setToggleGroup(difficultyGroup);
		hard.getStyleClass().add("button-style");
		addSoundEffectToRadioButton(hard);

		RadioButton ultraHard = new RadioButton("Ultra Hard");
		ultraHard.setUserData(new int[]{20, 99});
		ultraHard.setToggleGroup(difficultyGroup);
		ultraHard.getStyleClass().add("button-style");
		addSoundEffectToRadioButton(ultraHard);


		HBox modeBox = new HBox(20, easy, normal, hard, ultraHard);
		modeBox.setAlignment(Pos.CENTER);

		// Start Game Button
		Button startButton = new Button("Start Game");
		startButton.getStyleClass().add("button-style");
		startButton.setOnAction(e -> {
			playSound("retro-select-236670.mp3");
		    int[] config = (int[]) difficultyGroup.getSelectedToggle().getUserData();
		    currentGridSize = config[0];
		    currentBombCount = config[1];
		    showGameScreen(currentGridSize, currentBombCount);
		});
		
		// Options button
		Button options = new Button("Options");
		options.getStyleClass().add("button-style");
		options.setOnAction(e -> {
			playSound("retro-select-236670.mp3");
			showOptionsMenu();
		});
		VBox optionsButton = new VBox(10, options);
		optionsButton.setAlignment(Pos.CENTER);
		
		Button exitButton = new Button("Exit Game");
	    exitButton.getStyleClass().add("button-style");
	    exitButton.setOnAction(e -> {
	    playSound("retro-select-236670.mp3");
	    Platform.exit();
	    });
	    exitButton.setAlignment(Pos.CENTER);
	    
		welcomeLayout.getChildren().addAll(titleImage, description, modeLabel, modeBox, startButton, optionsButton);
		
		Scene scene = createCenteredScene(welcomeLayout, 700, 800);
		primaryStage.setScene(scene);
    	applyFadeTransition(welcomeLayout);
	}
	
	/**
	 * Displays the main game screen by initializing the game board, 
	 * timer, and UI elements for the background.
	 * 
	 * @param gridSize The size of the game grid.
	 * @param bombCount The number of bombs to be placed on the grid.
	 */
	private void showGameScreen(int gridSize, int bombCount) 
	{
		
		VBox gameLayout = new VBox(5);
		gameLayout.setAlignment(Pos.TOP_CENTER);
		gameLayout.setPadding(new Insets(5));
		gameLayout.setMaxSize(700, 800);
		gameLayout.getStyleClass().add("background-grey");
		
		// Timer display
		Label timerLabel = new Label("Time: 0.0s");
		timerLabel.getStyleClass().add("timer-label");

		// UI elements
	    uiManager = new UIManager(gridSize, bombCount, this);
	    BorderPane gameRoot = uiManager.initializeGameUI();
	    gameRoot.getStyleClass().add("background-grey");
	    gameRoot.setMaxSize(700, 700);
	    
	    gameLayout.getChildren().addAll(timerLabel, gameRoot);
	    
	    // Timer logic
	    timeElapsed = 0;
	    timer = new Timeline();
	    timer.setCycleCount(Animation.INDEFINITE);
	    KeyFrame frame = new KeyFrame(Duration.millis(100), e -> {
	        timeElapsed += 100;
	        double seconds = timeElapsed / 1000.0;
	        timerLabel.setText(String.format("Time: %.1fs", seconds));
	    });
	    timer.getKeyFrames().add(frame);
	    timer.play();
        
	    Scene scene = createCenteredScene(gameLayout, 700, 800);
	    primaryStage.setScene(scene);
	    applyFadeTransition(gameLayout);
	}
		
	/**
	 * Displays the win screen when the player completes the game.
	 * Shows the final time, game configuration(gird size), and the option to play again.
	 * 
	 * @param finalTimeElapsedMs The final time elapsed at the end of the game in milliseconds.
	 */
	public void showWinScreen(double finalTimeElapsedMs) 
	{
		playSound("good-6081.mp3");
		
        VBox winLayout = new VBox(20);
        winLayout.setAlignment(Pos.CENTER);
        winLayout.setPadding(new Insets(40));
        winLayout.setMaxSize(700, 800);
        winLayout.getStyleClass().add("background-grey");
        
        // Congratulations message
        Text congrats = new Text("Congratulations!");
        congrats.getStyleClass().add("label-big");
        
        applyMovingRainbowGradient(congrats);
        
        // Display the final time
        double seconds = finalTimeElapsedMs / 1000.0;
        Label timeLabel = new Label(String.format("You finished in %.1f seconds.", seconds));
        timeLabel.getStyleClass().add("label-bold");
        
        // Display the game configuration(grid size)
        Label config = new Label("Board: " + currentGridSize + "x" + currentGridSize + ", Bombs: " + currentBombCount);
        config.getStyleClass().add("label-bold");

        // Play Again button
        Button restart = new Button("Play Again");
        restart.getStyleClass().add("button-style");
        restart.setOnAction(e -> {
        	playSound("retro-select-236670.mp3");
        	showWelcomeScreen();
        });
        
        winLayout.getChildren().addAll(congrats, timeLabel, config, restart);
        Scene scene = createCenteredScene(winLayout, 700, 800);
        primaryStage.setScene(scene);
    }
	
	/**
	 * Adjusts all game stages to be in full screen mode by default,
	 * setting its screen dimensions to match the inputed screens visual bounds(dimensions).
	 * 
	 * @param stage The stage to be resized to full screen.
	 */
	private void forceStageToFullScreen(Stage stage) 
	{
	    Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
	    stage.setX(screenBounds.getMinX());
	    stage.setY(screenBounds.getMinY());
	    stage.setWidth(screenBounds.getWidth());
	    stage.setHeight(screenBounds.getHeight());
	}
	
	/**
	 * Creates a centered scene with a specified width and height.
	 * Wraps the game window in a styled black background container.
	 * Makes the negative outside space a consistent UI appearance.
	 * 
	 * @param content The main pane to be displayed within the scene.
	 * @param width The width of the scene.
	 * @param height The height of the scene.
	 * @return A new Scene containing the centered content with the applied black background styles.
	 */
	private Scene createCenteredScene(Pane content, int width, int height) 
	{
	    content.setMaxSize(width, height);

	    StackPane wrapper = new StackPane(content);
	    wrapper.setStyle("-fx-background-color: black;");
	    wrapper.setAlignment(Pos.CENTER);

	    Scene scene = new Scene(wrapper, width, height);
	    scene.getStylesheets().add(getClass().getResource("/reverseminesweeper.css").toExternalForm());

	    return scene;
	}
	
	/**
	 * Displays the options menu, allowing the player to toggle cheats, background music,
	 * and the ability to exit the game or return to the welcome/home screen.
	 * Applies visual styles and sound effects.
	 * 
	 * @coauthor Michael Arculeo
	 */
	private void showOptionsMenu()
	{	
		VBox layout = new VBox(25);
	    layout.setAlignment(Pos.CENTER);
	    layout.setPadding(new Insets(40));
	    layout.setMaxSize(700, 800);
	    layout.getStyleClass().add("background-grey");

	    // Options title
	    Label cheatLabel = new Label("OPTIONS");
	    cheatLabel.getStyleClass().add("label-big");
		
		// Cheat toggle button
		ToggleButton cheatToggle = new ToggleButton("Cheats: " + (GameSettings.getMode().isShowCheatsOn() ? "ON" : "OFF"));
	    cheatToggle.getStyleClass().add("button-style");
	    cheatToggle.setSelected(GameSettings.getMode().isShowCheatsOn());
		cheatToggle.getStyleClass().add("toggle-button");
		
		cheatToggle.setOnAction(e -> {
			playSound("retro-select-236670.mp3");
			boolean isOn = cheatToggle.isSelected();
			GameSettings.getMode().setShowCheats(isOn);
			cheatToggle.setText("Cheats: " + (isOn ? "ON" : "OFF"));
			
			uiManager = new UIManager(currentGridSize, currentBombCount, this);
			
			Platform.runLater(() -> 
				primaryStage.getScene().getRoot().requestLayout());
		});
		
		// Music toggle button
		ToggleButton musicToggle = new ToggleButton("Music: ON");
	    musicToggle.getStyleClass().add("button-style");
	    musicToggle.getStyleClass().add("toggle-button");
	    
	    boolean isMusicOn = backgroundMusic.getStatus() == MediaPlayer.Status.PLAYING;
	    musicToggle.setText("Music: " + (isMusicOn ? "ON" : "OFF"));
	    musicToggle.setSelected(isMusicOn);
	    
	    musicToggle.setOnAction(e -> {
	        boolean isOn = musicToggle.isSelected();
	        if (isOn) 
	        {
	            backgroundMusic.play();
	        } 
	        else 
	        {
	            backgroundMusic.pause();
	            
	        }
	        musicToggle.setText("Music: " + (isOn ? "ON" : "OFF"));
	        playSound("retro-select-236670.mp3");
	    });
	    
	    // Exit Game button
	    Button exitButton = new Button("Exit Game");
	    exitButton.getStyleClass().add("button-style");
	    exitButton.getStyleClass().add("toggle-button");
	    exitButton.setOnAction(e -> {
	        playSound("retro-select-236670.mp3");
	        Platform.exit();
	    });
	    
		// Back button
		Button backButton = new Button("Back");
		backButton.getStyleClass().add("button-style");
		backButton.getStyleClass().add("toggle-button");
		backButton.setOnAction(e -> { 
			playSound("retro-select-236670.mp3");
			showWelcomeScreen();
		});
		
		// Add elements to the layout
		layout.getChildren().addAll(cheatLabel, cheatToggle, musicToggle, exitButton, backButton);
	    Scene scene = createCenteredScene(layout, 700, 800);
	    primaryStage.setScene(scene);
	    primaryStage.setMaximized(true);
	    applyFadeTransition(layout);
	    
	}
	
	/**
	 * Applies fade in transition effects.
	 * The transition gradually increases the opacity over 1.5 seconds.
	 * 
	 * @param node The UI element to apply the fade transition to.
	 */
	private void applyFadeTransition(javafx.scene.Node node) 
	{
	    FadeTransition fade = new FadeTransition(Duration.seconds(1.5), node);
	    fade.setFromValue(0.0);
	    fade.setToValue(1.5);
	    fade.play();
	}
	
	/**
	 * Plays a sound effect from a file.
	 * 
	 * @param fileName The name of the sound file located in the Music directory.
	 */
	private void playSound(String fileName) 
	{
	    Media sound = new Media(getClass().getResource("/Music/" + fileName).toExternalForm());
	    MediaPlayer sfxPlayer = new MediaPlayer(sound);
	    sfxPlayer.setVolume(0.8);
	    sfxPlayer.play();
	}
	
	/**
	 * Adds a sound effect to a radio button when clicked.
	 * 
	 * @param radioButton The radio button to which the sound effect will be added.
	 */
	private void addSoundEffectToRadioButton(RadioButton radioButton) 
	{
	    radioButton.setOnAction(e -> {
	        playSound("retro-select-236670.mp3");
	    });
	}
	
	/**
	 * Applies a moving rainbow gradient effect to text(win screen congratulations).
	 * Continuously cycles through colors over a 5 second looping animation.
	 * 
	 * @param text The text element to apply the rainbow gradient effect to.
	 */
	private void applyMovingRainbowGradient(Text text)
	{   
	    // Time line for Rainbow Gradient
	    Timeline rainbowTimeline = new Timeline(new KeyFrame(Duration.millis(50), e -> {
	        double time = (System.currentTimeMillis() % 5000) / 5000.0; // looping 5 seconds
	        Stop[] stops = new Stop[] {
	            new Stop((time + 0.0) % 1.0, Color.RED),
	            new Stop((time + 0.15) % 1.0, Color.ORANGE),
	            new Stop((time + 0.30) % 1.0, Color.YELLOW),
	            new Stop((time + 0.45) % 1.0, Color.GREEN),
	            new Stop((time + 0.60) % 1.0, Color.BLUE),
	            new Stop((time + 0.75) % 1.0, Color.INDIGO),
	            new Stop((time + 0.90) % 1.0, Color.VIOLET)
	        };
	
	        LinearGradient gradient = new LinearGradient(
	            0, 0, 1, 0, true, CycleMethod.REPEAT, stops
	        );
	        text.setFill(gradient);
	        
	    }));
	    rainbowTimeline.setCycleCount(Animation.INDEFINITE);
	    rainbowTimeline.play();
	
	}
	
	/**
	 * Gets the elapsed time for the game.
	 * 
	 * @return The elapsed time in milliseconds.
	 */
	public double getElapsedTime() 
	{
		return timeElapsed;
	}

	/**
	 * Stops timer from UIManager.
	 * Triggers the win screen display.
	 * Ensures the timer does not continue running after the game has been won.
	 */
	public void stopTimerAndShowWinScreen() 
	{
	    if (timer != null) {
	        timer.stop();
	    }
	    showWinScreen(timeElapsed);
	}
	
	/**
	 * Main entry point for launching the Reverse Minesweeper game.
	 * 
	 * @param args Command line arguments (not used)
	 */
	public static void main(String[] args) 
	{
		launch(args);
	}
}