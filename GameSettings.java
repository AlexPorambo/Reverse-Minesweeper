package reverseminesweeper;

/**
 * The GameSettings class contains the configurable game settings
 * for enabling or disabling cheat mode.
 * 
 * @author Michael Arculeo
 * @version 4/27/25
 */
public class GameSettings
{
	private static GameSettings mode; // Singleton(mode) instance of GameSettings.
	private boolean showCheats = false; // Tracks whether or not cheat mode is enabled.
	
	/**
	 * Gets the singleton(mode) instance of the GameSettings class.
	 * Ensures only one instance of GameSettings exists.
	 * 
	 * @return The singleton(mode) GameSettings instance.
	 */
	public static GameSettings getMode()
	{
		if (mode == null)
		{
			mode = new GameSettings();
		}
		return mode;
	}
	
	/**
	 * Enables or disables the cheat/debug mode.
	 * 
	 * @param cheater True to enable the cheats, false to disable the cheats.
	 */
	public void setShowCheats(boolean cheater)
	{
		showCheats = cheater;
	}
	
	/**
	 * Checks whether cheat mode is actively enabled.
	 * 
	 * @return True if cheat mode is enabled, false if cheat mode is disabled.
	 */
	public boolean isShowCheatsOn()
	{
		return showCheats;
	}
}