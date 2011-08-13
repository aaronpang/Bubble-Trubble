import java.applet.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.*;
import java.awt.Font;
import java.net.*;

public class BubbleTrubble extends JFrame implements ActionListener
{
    final int blockSize;
    private DrawingPanel Grid;
    // Number Of Rows and Columns
    final int noOfRows;
    final int noOfColumns;

    // Set the fonts for the game
    static final Font hpFont = new Font ("Verdana", Font.PLAIN, 12);
    static final Font selectFont = new Font ("Tahoma", Font.BOLD, 20);
    static final Font mapNameFont = new Font ("Verdana", Font.BOLD, 30);
    static final Font unitNameFont = new Font ("BroadWay", Font.BOLD, 40);
    static final Font moveCounterFont = new Font ("Verdana", Font.PLAIN, 40);

    // Checks the red turn, blue turn event
    int switchTurns;
    boolean redTurn = false;

    // Variables to keep track of the board, drawing it and positions of the tiles
    private int[] [] board;
    final int numberOfRows;
    final int numberOfColumns;
    private int length;
    private int width;

    // Keeps track on whether there is a critical hit, heal or attack
    boolean criticalHitCounter, healCounter, attackCounter;
    int defendingUnitXPos, defendingUnitYPos;

    // Title screen counter
    int titleScreenCounter;

    // Variable for the mountain tile, which gives +1 range to any
    // units that move on it. When they move off the mountain,
    // they lose the range.
    int[] unitsRangeCheck;
    // Range of each unit
    int[] unitRange;
    // 2D Array for the units
    private int[] [] unitGrid;

    // Keeps track of the movement of each unit and who can still move
    private int unitMoves;
    private int currentUnit;
    private int attackBlockDrawCount;
    private int noOfRedUnitsLeft;
    private int noOfBlueUnitsLeft;

    // Create the array for the Points of Attack, unit locations, unit HP,
    // unit Image Strings (will be printed out as images later), array of unit
    // numbers and unit selection array
    final Point[] locationsAttack;
    final Point[] unitLocations;
    final double[] unitsCountHP;
    final String[] unitImageStrings;
    final boolean[] unitsSelect;
    final int[] unitMoveCounters;
    final boolean[] unitsBubbled;
    final int noOfMaps;

    // Array of units, first 4 will be red units, next 4 will be blue units
    final Unit[] unitsCount;

    //Create the Squad
    Squad mySquad;

    // Create the Images being used (not including unit Images)
    private Image unitImage, attackBlock, leftBar, cursorIcon;
    private Image attackMenu, defendMenu, endMenu, cursorMenu;
    private Image blueWins, redWins, gameOverMenu;
    private Image blueSelectImage, redSelectImage, startScreenImage, unitSelectCursor, okButton, levelSelectImage, levelSelectCursorImage;
    private int unitSelectRow, unitSelectColumn, unitSelect;

    // Tile images
    private Image grass, desert, forest, water, mountain;
    private Image highwayHorizontal, highwayVertical, acid, black, blackEmpty, hwTurnRight, hwTurnLeft, hwTurnUp, hwTurnDown, hwCrossroadThree, hwCrossroadFour;
    private Image redTurnChange, blueTurnChange;
    private Image criticalHitBox, healAnimate, bubbleAnimate;

    // Set the integers for menu cursor and the move counter for each unit
    private int cursor, gameOverCursor, levelSelectCursor;
    private JMenuItem exitOption, resetOption, characterSelectionOption, levelSelectionOption, howToPlayOption, aboutOption;
    private boolean move, menu, attackChoice, attack, endTurn, gameOver;
    private boolean sound, soundPlaying;
    private boolean blueSelect, redSelect, levelSelect;
    private String map;
    private AudioClip bgMusic, characterSelectMusic, attackSound, swordAttack, splashAttack, bubbleAttack, gunAttack, victoryMusic, healSound, waterOne;

    public BubbleTrubble ()
    {
	// Sets all of the variables to their proper values
	blockSize = 50;
	// Number Of Rows and Columns
	noOfRows = 15;
	noOfColumns = 25;

	// Checks the red turn, blue turn event
	redTurn = false;

	// Variables to keep track of the board, drawing it and positions of the tiles
	numberOfRows = 18;
	numberOfColumns = 24;

	// Title screen counter
	titleScreenCounter = 0;

	// Variable for the mountain tile, which gives +1 range to any
	// units that move on it. When they move off the mountain,
	// they lose the range.
	unitsRangeCheck = new int [8];
	// Range of each unit
	unitRange = new int [8];
	// 2D Array for the units
	unitGrid = new int [18] [24];

	// Create the array for the Points of Attack, unit locations, unit HP,
	// unit Image Strings (will be printed out as images later), array of unit
	// numbers and unit selection array
	locationsAttack = new Point [noOfRows * noOfColumns];
	unitLocations = new Point [8];
	unitsCountHP = new double [8];
	unitImageStrings = new String [8];
	unitsSelect = new boolean [8];
	unitMoveCounters = new int [8];
	unitsBubbled = new boolean [8];
	noOfMaps = 10;

	// Array of units, first 4 will be red units, next 4 will be blue units
	unitsCount = new Unit [8];

	//Create the Squad
	mySquad = new Squad ();




	// Used to hold the tile data of the map
	board = new int [numberOfRows] [numberOfColumns];
	map = (" ");
	// Reads the file for different maps
	readMapInfo ();

	setLocation (10, 100);
	setTitle ("Bubble Trubble");
	setResizable (false);
	Container contentPane = getContentPane ();
	Dimension size = new Dimension (noOfColumns * blockSize,
		(noOfRows + 1) * blockSize);
	Grid = new DrawingPanel (size);
	contentPane.add (Grid, BorderLayout.CENTER);
	// Resets the current game
	resetOption = new JMenuItem ("Reset Current Game");
	resetOption.setAccelerator (
		KeyStroke.getKeyStroke (KeyEvent.VK_R, InputEvent.CTRL_MASK));
	resetOption.addActionListener (this);

	// Goes back to the Character Selection screen
	characterSelectionOption = new JMenuItem ("Character Selection");
	characterSelectionOption.setAccelerator (
		KeyStroke.getKeyStroke (KeyEvent.VK_C, InputEvent.CTRL_MASK));
	characterSelectionOption.addActionListener (this);

	// Goes back to the Level Selection screen
	levelSelectionOption = new JMenuItem ("Level Selection");
	levelSelectionOption.setAccelerator (
		KeyStroke.getKeyStroke (KeyEvent.VK_L, InputEvent.CTRL_MASK));
	levelSelectionOption.addActionListener (this);

	// Exits the window
	exitOption = new JMenuItem ("Exit");
	exitOption.setAccelerator (
		KeyStroke.getKeyStroke (KeyEvent.VK_X, InputEvent.CTRL_MASK));
	exitOption.addActionListener (this);

	// Set up the Help Menu Items
	howToPlayOption = new JMenuItem ("How To Play");
	howToPlayOption.setAccelerator (
		KeyStroke.getKeyStroke (KeyEvent.VK_H, InputEvent.CTRL_MASK));
	// Tips / Hints
	howToPlayOption.addActionListener (new ActionListener ()
	{
	    /** Responds to the help option being selected
	      *@param eventThe event that selected this menu option
	      */
	    public void actionPerformed (ActionEvent event)
	    {
		JOptionPane.showMessageDialog (BubbleTrubble.this,

			"Tips/Hints\n" +
			"There are ideal spots in each map to place your units. Use mountains and forests to your advantage.\n" +
			"First Attack does not always guarantee a win. Move and attack carefully.\n" +
			"If you jump to level select without selecting your characters, they will all be soldiers.\n" +
			"This game has a similar control scheme to Visual Boy Advance :)\n" +
			"Pressing 1, 2, 3 or 4 will switch to that corresponding unit according to the number beside it on the left menu.\n" +
			"A unit becomes darker when it is isolated, you must end all units turns before players are able to switch turns \n" +
			"Massing tanks is not the best way to go ><\n", "Bubble Trubble",
			JOptionPane.INFORMATION_MESSAGE);
	    }
	}
	);
	// List of Units
	howToPlayOption.addActionListener (new ActionListener ()
	{
	    /** Responds to the help option being selected
	      *@param eventThe event that selected this menu option
	      */
	    public void actionPerformed (ActionEvent event)
	    {
		JOptionPane.showMessageDialog (BubbleTrubble.this,

			"List of Units\n" +
			"Bubble Soldier **                                 Bubble Elite **\n" +
			"+ Second highest HP                          + Good HP\n" +
			"+ High damage                                     Medium damage\n" +
			"- Attacks only in one direction          Diagonal attack\n" +
			"- Range is only 2                                  - Range is only 2\n" +
			"\n" +
			"\n" +
			"Bubble Ninja                                         Bubble Mage\n" +
			"++ Highest critical hit                        + Second best attack\n" +
			"++ Highest movement                       + Second highest movement\n" +
			"- Bad against a tank                           - Low HP\n" +
			"- Low HP                                               - Low critical hit ratio\n" +
			"- relies on critical hit to deal            - Range attack is limited, hard\n" +
			"  a lot of damage                                   to connect with multiple units\n" +
			"\n" +
			"\n" +
			"Bubble Boy                                            Mr. Bubble\n" +
			"Horizontal / vertical attack                Diagonal attack\n" +
			"Average unit                                         Average unit\n" +
			"\n" +
			"\n" +
			"Bubble Sniper                                       Bubble Tank\n" +
			"+ Highest range                                   ++ Highest damage\n" +
			"- No close range attack                     + High HP\n" +
			"- Bad against ninjas                           + Heals a lot\n" +
			"-- Lowest HP                                         Mountains boost its small range,\n" +
			"                                                                  increasing its potential\n" +
			"                                                                - Bad against snipers\n" +
			"                                                                - Can only move onto a mountain tile once\n" +
			"                                                                  before it has to stop\n" +
			"                                                                -- Lowest movement\n" +
			"                                                                -- Lowest critical hit ratio\n" +
			"\n" +
			"\n" +
			"Legend:   ++ Best   + Good   - Bad   -- Worst\n" +
			"\n" +
			"* Bubble Boy and Mr. Bubble are even. Only difference is attack type *\n" +
			"** The Elite and Soldier are almost on par. The Elite has higher critical hit ratio,         \n" +
			"              while the Soldier has higher HP and damage. **\n", "Bubble Trubble",
			JOptionPane.INFORMATION_MESSAGE);
	    }
	}
	);
	// Units
	howToPlayOption.addActionListener (new ActionListener ()
	{
	    /** Responds to the help option being selected
	      *@param eventThe event that selected this menu option
	      */
	    public void actionPerformed (ActionEvent event)
	    {
		JOptionPane.showMessageDialog (BubbleTrubble.this,

			"Units\n" +
			"There are 8 different types of units to choose from. \n" +
			"Each unit has a set amount of movement, total health points, attack power, attack range, attack type, \n" +
			"and critical hit ratio.\n" +
			"For units with greater range, they are able to attack overseas and across mountains, but suffer from the \n " +
			"lack of health points. On the other hand, certain units with close attack range have an increase \n" +
			"in health points and attack power, but are limited to their movement.\n" +
			"\n" +
			"The exceptions are:\n" +
			"Bubble Ninja: its increased movement speed, critical hit and close range attack comes with a price: low HP.\n" +
			"Bubble Mage: lower HP is the cost for scattered range, increased attack and increased movement.\n " +
			"\n" +
			"Choosing the right combination of ranged and melee-type units can greatly influence \n" +
			"the flow of battle.\n" +
			"\n" +
			"Unit Stats\n" +
			"Movement: Number of move counters the unit has\n" +
			"Range: The number of attack blocks the unit covers\n" +
			"HP: The amount of health points it can take\n" +
			"Dmg: The amount of HP it reduces on attacked units\n" +
			"Crit: The chance of hitting a critical attack on attacked unit (double damage)\n", "Bubble Trubble",
			JOptionPane.INFORMATION_MESSAGE);
	    }
	}
	);
	// Terrain
	howToPlayOption.addActionListener (new ActionListener ()
	{
	    /** Responds to the help option being selected
	      *@param eventThe event that selected this menu option
	      */
	    public void actionPerformed (ActionEvent event)
	    {
		JOptionPane.showMessageDialog (BubbleTrubble.this,
			"Terrain\n" +
			"The specific terrain on a map may affect the unit's health, range or movement.\n" +
			"Grass, Highways, Desert: Reduces 1 move counter, no special attack or range boosts.\n" +
			"Mountains: Reduces 3 move counters, units gain + 1 range.\n" +
			"Forests: Reduces 2 move counters, units gain 1/3.5 health rather than 1/4 when defending on this tile.\n" +
			"Water, Black Pit: Units cannot move onto these tiles.\n" +
			"Acid: Reduces the unit's HP by 2 if moved on.\n", "Bubble Trubble",
			JOptionPane.INFORMATION_MESSAGE);
	    }
	}
	);

	// In-Game Options
	howToPlayOption.addActionListener (new ActionListener ()
	{
	    /** Responds to the help option being selected
	      *@param eventThe event that selected this menu option
	      */
	    public void actionPerformed (ActionEvent event)
	    {
		JOptionPane.showMessageDialog (BubbleTrubble.this,
			"In-game Options\n" +
			"Attack: Shows the attack range of the unit, then if attack is executed, damages ALL enemies within range.\n" +
			"Defend: Heals the selected unit 1/3 of its health. You cannot select defend if your unit has used more than 4 move counters.\n" +
			"End Turn: Ends the turn of the current unit.\n", "Bubble Trubble",
			JOptionPane.INFORMATION_MESSAGE);
	    }

	}
	);

	// Objective and Playing
	howToPlayOption.addActionListener (new ActionListener ()
	{
	    /** Responds to the help option being selected
	      *@param eventThe event that selected this menu option
	      */
	    public void actionPerformed (ActionEvent event)
	    {
		JOptionPane.showMessageDialog (BubbleTrubble.this,

			"Playing the Game\n" +
			"After selecting your units and a level to play on, the battle begins! \n" +
			"The objective of the game is to bubble all of the opponents' characters by attacking them with your units.\n" +
			"You select one of your units with 1, 2, 3 and 4 and move it up, down, left or right with the arrow keys. You can switch \n" +
			"between your units at any time as long as they are not bubbled or their turn has not ended.\n" +
			"\n" +
			"Each unit has its own move counters (varies per type of unit), in which you lose 1 move counter for moving onto a tile, \n" +
			"an extra 1 onto forest and extra 2 onto mountains.\n" +
			"\n" +
			"Once you are out of move counters or you decide to stop moving the unit, press Z to open up the menu on the left of the screen.\n" +
			"This menu will allow you to select attack, defend, or end turn for the current unit you have selected.\n" +
			"Attacking, Defending or Ending Turn will isolate the unit and cannot be moved or selected for the duration of your turn.\n" +
			"If you have isolated all your units, all your unit move counters will reset back to their initial values and the turn switches.\n" +
			"Once one side's units are all bubbled, the game ends.\n", "Bubble Trubble",
			JOptionPane.INFORMATION_MESSAGE);
	    }
	}
	);
	// Basic Controls
	howToPlayOption.addActionListener (new ActionListener ()
	{
	    /** Responds to the help option being selected
	      *@param eventThe event that selected this menu option
	      */
	    public void actionPerformed (ActionEvent event)
	    {
		JOptionPane.showMessageDialog (BubbleTrubble.this,

			"Basic Controls\n" +
			"Choose Units / Level / Option or Move a Unit: Use the Arrow Keys \n" +
			"Select Unit / Level / Option or Attack: Press Z\n" +
			"Cancel Selection / Option: Press X\n" +
			"Switch between units in-game: Press 1, 2, 3 or 4 (Units cannot be selected if moved or bubbled).\n" +
			"Note: To select attack, defend or end turn ingame, press Z. If attack is selected, press Z again to execute attack.\n" +
			"Also note that you MUST end all of your units' turns before you can switch turns.\n", "Bubble Trubble",
			JOptionPane.INFORMATION_MESSAGE);
	    }
	}
	);

	// Battle System
	howToPlayOption.addActionListener (new ActionListener ()
	{
	    /** Responds to the help option being selected
	      *@param eventThe event that selected this menu option
	      */
	    public void actionPerformed (ActionEvent event)
	    {
		JOptionPane.showMessageDialog (BubbleTrubble.this,

			"Battle system\n" +
			"Bubble Trubble is turn-based, where two opposing squads, lead  by two different users, take turns \n" +
			"advancing and deploying their units in strategic manners to outsmart and prevail over their opponents.\n", "Bubble Trubble",
			JOptionPane.INFORMATION_MESSAGE);
	    }
	}
	);

	// Some background to the game - Story
	howToPlayOption.addActionListener (new ActionListener ()
	{
	    /** Responds to the help option being selected
	      *@param eventThe event that selected this menu option
	      */
	    public void actionPerformed (ActionEvent event)
	    {
		JOptionPane.showMessageDialog (BubbleTrubble.this,

			"Story\n" +
			"Oh No! Little Bubble World is under war again! You must out-bubble your opponent and win the war! \n" +
			"Using advanced bubble warfare and equipment, the bubble battle begins between 2 teams, Red vs. Blue.\n" +
			"You must pick a side and an opponent as you two fight for bubble dominance in this never ending bubble war!\n", "Bubble Trubble",
			JOptionPane.INFORMATION_MESSAGE);
	    }
	}
	);

	// Set up the About menu
	aboutOption = new JMenuItem ("About");
	aboutOption.addActionListener (new ActionListener ()
	{
	    /** Responds to the help option being selected
	      *@param eventThe event that selected this menu option
	      */
	    public void actionPerformed (ActionEvent event)
	    {
		JOptionPane.showMessageDialog (BubbleTrubble.this,
			"Bubble Trubble  Copyright © Ma and Pango Games\n" +
			"Brandon Ma, Aaron Pang\n", "Bubble Trubble",
			JOptionPane.INFORMATION_MESSAGE);
	    }
	}
	);


	// Set up the Game Menu
	JMenu gameMenu = new JMenu ("Game");
	JMenu soundMenu = new JMenu ("Sound");
	JMenu helpMenu = new JMenu ("Help");

	// Set up the radio buttons for sound
	ButtonGroup soundGroup = new ButtonGroup ();
	JRadioButtonMenuItem soundOnOption =
	    new JRadioButtonMenuItem ("On", true);
	soundOnOption.addActionListener (new SoundListener (true));
	sound = true;
	JRadioButtonMenuItem soundOffOption =
	    new JRadioButtonMenuItem ("Off", false);
	soundOffOption.addActionListener (new SoundListener (false));
	soundGroup.add (soundOnOption);
	soundGroup.add (soundOffOption);
	soundMenu.add (soundOnOption);
	soundMenu.add (soundOffOption);

	// Add each MenuItem to the Game Menu (with a separator)
	gameMenu.add (resetOption);
	gameMenu.add (characterSelectionOption);
	gameMenu.add (levelSelectionOption);
	gameMenu.addSeparator ();
	gameMenu.add (exitOption);
	soundMenu.add (soundOnOption);
	helpMenu.add (howToPlayOption);
	helpMenu.add (aboutOption);
	JMenuBar mainMenu = new JMenuBar ();
	mainMenu.add (gameMenu);
	mainMenu.add (soundMenu);
	mainMenu.add (helpMenu);
	setJMenuBar (mainMenu);

	// The menu images
	startScreenImage = new ImageIcon ("menus/titleScreen.png").getImage ();
	blueSelectImage = new ImageIcon ("menus/blueUnitSelect.png").getImage ();
	redSelectImage = new ImageIcon ("menus/redUnitSelect.png").getImage ();
	unitSelectCursor = new ImageIcon ("menus/unitSelectCursor.png").getImage ();
	okButton = new ImageIcon ("menus/okButton.png").getImage ();
	levelSelectImage = new ImageIcon ("menus/levelSelect.png").getImage ();
	levelSelectCursorImage = new ImageIcon ("menus/levelSelectCursor.png").getImage ();

	// The left bar ingame menu
	attackMenu = new ImageIcon ("overlay/attackBar.png").getImage ();
	defendMenu = new ImageIcon ("overlay/defendBar.png").getImage ();
	endMenu = new ImageIcon ("overlay/endBar.png").getImage ();
	cursorMenu = new ImageIcon ("overlay/unitIconCursor.png").getImage ();

	// Blue wins and Red wins overlay
	blueWins = new ImageIcon ("overlay/blueWins.png").getImage ();
	redWins = new ImageIcon ("overlay/redWins.png").getImage ();
	gameOverMenu = new ImageIcon ("overlay/menuGO0.png").getImage ();
	setIconImage (Toolkit.getDefaultToolkit ().getImage ("unitImages/bluUnit2.png"));

	// Sound Files
	bgMusic = Applet.newAudioClip (getCompleteURL ("sounds/ff7Bat.wav"));
	waterOne = Applet.newAudioClip (getCompleteURL ("sounds/Water1.wav"));
	characterSelectMusic = Applet.newAudioClip (getCompleteURL ("sounds/ssbm.wav"));
	victoryMusic = Applet.newAudioClip (getCompleteURL ("sounds/A Great Success.wav"));
	swordAttack = Applet.newAudioClip (getCompleteURL ("sounds/swordAttack.wav"));
	splashAttack = Applet.newAudioClip (getCompleteURL ("sounds/splashAttack.wav"));
	gunAttack = Applet.newAudioClip (getCompleteURL ("sounds/gunAttack.wav"));
	healSound = Applet.newAudioClip (getCompleteURL ("sounds/Heal.wav"));
	bubbleAttack = Applet.newAudioClip (getCompleteURL ("sounds/bubbleAttack.wav"));

	// Block Tile Images
	grass = new ImageIcon ("blockImages/Grass.png").getImage ();
	desert = new ImageIcon ("blockImages/Desert.png").getImage ();
	forest = new ImageIcon ("blockImages/Forest1.png").getImage ();
	water = new ImageIcon ("blockImages/water.gif").getImage ();
	mountain = new ImageIcon ("blockImages/Mountain1.png").getImage ();
	highwayHorizontal = new ImageIcon ("blockImages/highway1.png").getImage ();
	highwayVertical = new ImageIcon ("blockImages/highway2.png").getImage ();
	acid = new ImageIcon ("blockImages/Acid.png").getImage ();
	black = new ImageIcon ("blockImages/Black2.png").getImage ();
	blackEmpty = new ImageIcon ("blockImages/Black.png").getImage ();
	hwTurnRight = new ImageIcon ("blockImages/HighwayTurn1.png").getImage ();
	hwTurnLeft = new ImageIcon ("blockImages/HighwayTurn2.png").getImage ();
	hwTurnUp = new ImageIcon ("blockImages/HighwayTurn3.png").getImage ();
	hwTurnDown = new ImageIcon ("blockImages/HighwayTurn4.png").getImage ();
	hwCrossroadThree = new ImageIcon ("blockImages/HighwayCross3.png").getImage ();
	hwCrossroadFour = new ImageIcon ("blockImages/HighwayCross4.png").getImage ();

	// Animations overlay images
	redTurnChange = new ImageIcon ("Turns/Red Turn.gif").getImage ();
	blueTurnChange = new ImageIcon ("Turns/Blue Turn.gif").getImage ();
	criticalHitBox = new ImageIcon ("blockImages/Critical Hit.png").getImage ();
	healAnimate = new ImageIcon ("blockImages/healAnimation2.gif").getImage ();
	bubbleAnimate = new ImageIcon ("blockImages/bubble-pop.gif").getImage ();


	// Create the array of solider units when the game boots up, changes later in character selections
	for (int unit = 0 ; unit < unitsCount.length ; unit++)
	{
	    unitsCount [unit] = mySquad.getUnit (0);
	}
	// First screen is title screen, then goes straight to red character selection
	redSelect = true;
	pack ();
	setVisible (true);
	// If the game sound is on (which it is initially) then play the song according to the screen
	checkSoundOn ();
	resetGame ();
    }


    /** Reads the information of the map selected from a text file
      */
    public void readMapInfo ()
    {
	FileReader reader = null;
	try
	{
	    // Read the information from the file
	    reader = new FileReader (map);
	    BufferedReader inFile =
		new BufferedReader (new FileReader (map));

	    // Gets the length and width of the grid
	    int length = Integer.parseInt (inFile.readLine ());
	    int width = Integer.parseInt (inFile.readLine ());

	    // Saves the text data
	    for (int lengthCounter = 1 ; lengthCounter <= length ; lengthCounter++)
	    {
		// Index of each word, which is reset after each loop to start
		// at the beginning of the next line
		int letterIndex = 0;
		// Saves the next line in the text file to be read
		String nextLine = inFile.readLine ();
		for (int widthCounter = 1 ; widthCounter <= width ; widthCounter++)
		{
		    board [lengthCounter] [widthCounter] = (nextLine.charAt (letterIndex) - 48);
		    letterIndex++;
		}
	    }
	}
	catch (IOException ex)
	{
	}
    }


    /** Checks for the sound whether it is on or off
      */
    private class SoundListener implements ActionListener
    {
	private boolean setSound;
	public SoundListener (boolean sound)
	{
	    setSound = sound;
	}


	/** Responds to the sub menu being selected
	  * @param event The event that selected this menu option
	  */
	public void actionPerformed (ActionEvent event)
	{
	    sound = setSound;
	    if (soundPlaying)
		checkSoundOff ();
	    else if (!soundPlaying)
		checkSoundOn ();
	}
    }


    /** Responds to the sub menu being selected
      * @param event The event that selected this menu option
      */
    public void actionPerformed (ActionEvent event)
    {
	if (event.getSource () == resetOption)
	    resetGame ();
	else if (event.getSource () == characterSelectionOption)
	{
	    // Play the bgMusic sound if sound is turned on
	    if (sound)
	    {
		bgMusic.stop ();
		victoryMusic.stop ();
		characterSelectMusic.loop ();
	    }
	    // Set the
	    unitSelect = 0;
	    // Make sure the only menu that is open is redSelect
	    levelSelect = false;
	    blueSelect = false;
	    redSelect = true;
	    repaint ();
	}

	// Goes back to the level select screen
	else if (event.getSource () == levelSelectionOption)
	{
	    // Play the character Select music if sound is turned on
	    if (sound)
	    {
		bgMusic.stop ();
		victoryMusic.stop ();
		characterSelectMusic.loop ();
	    }
	    // Make sure the only menu that is open is levelSelect
	    redSelect = false;
	    blueSelect = false;
	    levelSelect = true;
	    repaint ();
	}

	// Exits the game
	else if (event.getSource () == exitOption)

	    {
		hide ();
		System.exit (0);
	    }
    }


    /** Retrieves the URL for the file name
      * @param fileName The file name and type
      * @return returns nothing to the user
      */
    public URL getCompleteURL (String fileName)
    {
	try
	{
	    return new URL ("file:" + System.getProperty ("user.dir") + "/" + fileName);
	}

	catch (MalformedURLException e)
	{
	    //            System.err.println (e.getMessage ());
	}
	return null;
    }


    /** Reset the game board, unit positions, HP, movement back to initial status
      */
    public void resetGame ()
    {
	// Unit can move, menu is not open, it is not the end of the turn,
	// it is not attacking and it has a move value of whatever the unit is
	move = true;
	menu = false;
	endTurn = false;
	attackChoice = false;
	attack = false;

	// Resets the unit grid
	for (int resetRow = 1 ; resetRow <= 16 ; resetRow++)
	    for (int resetColumn = 1 ; resetColumn <= 22 ; resetColumn++)
		unitGrid [resetRow] [resetColumn] = 0;
	for (int unit = 0 ; unit < unitsCount.length ; unit++)
	{
	    unitsBubbled [unit] = false;
	    unitsSelect [unit] = false;
	}
	storeUnitLocations ();
	storeUnitInfo ();

	// Set the cursor for the left bar menu
	cursor = 0;
	// Set the cursor for the game Over menu
	gameOverCursor = 0;
	// Set Player turn to random decision
	if ((int) (Math.random () * 2) == 1)
	{
	    redTurn = true;
	    currentUnit = 0;
	}
	else
	{
	    redTurn = false;
	    currentUnit = 4;
	}


	gameOver = false;
	unitMoves = 0;
	noOfRedUnitsLeft = 4;
	noOfBlueUnitsLeft = 4;
	if (sound && !redSelect && !blueSelect)
	{
	    victoryMusic.stop ();
	    bgMusic.loop ();
	    soundPlaying = true;
	}


	// Set the Switch turn so that the gif won't play again
	switchTurns = 1;

	if (titleScreenCounter == 0)
	{
	    for (int time = 0 ; time <= 300 ; time++)
	    {
		Grid.paintImmediately (0, 0, Grid.getWidth (),
			Grid.getHeight ());
		delay (10);
	    }
	    titleScreenCounter = 1;
	}


	repaint ();

    }


    /** Stores the information of the units in the unitsCount array
      */
    public void storeUnitInfo ()
    {
	// Store the units in the unitsCount array, fill in the
	// HP array with the respective unit's HP and store unit Images
	// and fills in the range of each unit into an array
	for (int unit = 0 ; unit < unitsCount.length ; unit++)
	{
	    unitsCountHP [unit] = unitsCount [unit].getHP ();
	    // Set all of the unitsRangeCheck to -1 as a default
	    for (int rangeOfUnit = 0 ; rangeOfUnit < unitsRangeCheck.length ; rangeOfUnit++)
	    {
		unitsRangeCheck [rangeOfUnit] = -1;
	    }
	    // Store the range of the units
	    unitRange [unit] = unitsCount [unit].getRange ();
	    if (unit < 4)
		unitImageStrings [unit] = new String ("redUnit" + unitsCount [unit].getImageNumber ());
	    else
		unitImageStrings [unit] = new String ("bluUnit" + unitsCount [unit].getImageNumber ());
	    unitMoveCounters [unit] = unitsCount [unit].getMovement ();
	}
    }


    /** Determines the attack direction and the range of each unit
      */
    public void storeAttackBlocks ()
    {
	int columnDraw;
	int rowDraw;
	// Set the array position to 0 every time the unit is in a new position
	int blockAttack = 0;
	// Set the array block Count to 0 every time the unit is in a new position
	attackBlockDrawCount = 0;

	// Find the points of all the attack blocks horizontal / vertical to the unit
	// Depending on the attack type of the unit, store different locations of the attack blocks
	if (unitsCount [currentUnit].getAttackType () != 1)
	{
	    if (unitsCount [currentUnit].getAttackType () != 2)
	    {
		// Finds the attack points below the unit
		rowDraw = unitLocations [currentUnit].x + blockSize;

		if (unitsCount [currentUnit].getAttackType () == 3)
		    rowDraw = unitLocations [currentUnit].x + blockSize * 3;
		while (rowDraw < (noOfRows + 1) * blockSize && rowDraw < unitRange [currentUnit] * blockSize + unitLocations [currentUnit].x)
		{
		    // Fill in the attack block locations array
		    locationsAttack [blockAttack] = new Point (rowDraw, unitLocations [currentUnit].y);
		    rowDraw += blockSize;
		    // Once that location is filled, move to the next array location
		    blockAttack++;
		    // Counts the number of squares to be drawn
		    attackBlockDrawCount++;
		}

		// Finds the attack points above the unit
		rowDraw = unitLocations [currentUnit].x - blockSize;

		if (unitsCount [currentUnit].getAttackType () == 3)
		    rowDraw = unitLocations [currentUnit].x - blockSize * 3;
		while (rowDraw > -1 && rowDraw > unitLocations [currentUnit].x - unitRange [currentUnit] * blockSize)
		{
		    locationsAttack [blockAttack] = new Point (rowDraw, unitLocations [currentUnit].y);
		    rowDraw -= blockSize;
		    blockAttack++;
		    attackBlockDrawCount++;
		}
	    }
	    if (unitsCount [currentUnit].getAttackType () != 2 || currentUnit < 4)
	    {
		// Finds the attack points to the right the unit
		columnDraw = unitLocations [currentUnit].y + blockSize;
		if (unitsCount [currentUnit].getAttackType () == 3)
		    columnDraw = unitLocations [currentUnit].y + blockSize * 3;
		while (columnDraw < noOfColumns * blockSize && columnDraw < unitRange [currentUnit] * blockSize + unitLocations [currentUnit].y)
		{
		    locationsAttack [blockAttack] = new Point (unitLocations [currentUnit].x, columnDraw);
		    columnDraw += blockSize;
		    blockAttack++;
		    attackBlockDrawCount++;
		}
	    }
	    if (unitsCount [currentUnit].getAttackType () != 2 || currentUnit > 3)
	    {
		// Finds the attack points left the unit
		columnDraw = unitLocations [currentUnit].y - blockSize;
		if (unitsCount [currentUnit].getAttackType () == 3)
		    columnDraw = unitLocations [currentUnit].y - blockSize * 3;
		while (columnDraw > 2 * blockSize && columnDraw > unitLocations [currentUnit].y - unitRange [currentUnit] * blockSize)
		{
		    locationsAttack [blockAttack] = new Point (unitLocations [currentUnit].x, columnDraw);
		    columnDraw -= blockSize;
		    blockAttack++;
		    attackBlockDrawCount++;
		}
	    }
	}


	// Diagonal Attack Blocks
	if (unitsCount [currentUnit].getAttackType () > 0)
	{
	    if (unitsCount [currentUnit].getAttackType () != 2 || currentUnit > 3)
	    {
		// Finds the attack points bottom left the unit
		rowDraw = unitLocations [currentUnit].x + blockSize;
		columnDraw = unitLocations [currentUnit].y - blockSize;
		if (unitsCount [currentUnit].getAttackType () == 3)
		{
		    rowDraw = unitLocations [currentUnit].x + blockSize * 2;
		    columnDraw = unitLocations [currentUnit].y - blockSize * 2;
		}
		while ((rowDraw < (noOfRows + 1) * blockSize && rowDraw < unitRange [currentUnit] * blockSize + unitLocations [currentUnit].x) &&
			columnDraw > 2 * blockSize && columnDraw > unitLocations [currentUnit].y - unitRange [currentUnit] * blockSize)
		{
		    // Fill in the attack block locations array
		    locationsAttack [blockAttack] = new Point (rowDraw, columnDraw);
		    rowDraw += blockSize;
		    columnDraw -= blockSize;
		    // Once that location is filled, move to the next array location
		    blockAttack++;
		    // Counts the number of squares to be drawn
		    attackBlockDrawCount++;
		}
		// Finds the attack points top left the unit
		rowDraw = unitLocations [currentUnit].x - blockSize;
		columnDraw = unitLocations [currentUnit].y - blockSize;
		if (unitsCount [currentUnit].getAttackType () == 3)
		{
		    rowDraw = unitLocations [currentUnit].x - blockSize * 2;
		    columnDraw = unitLocations [currentUnit].y - blockSize * 2;
		}
		while ((rowDraw > -1 && rowDraw > unitLocations [currentUnit].x - unitRange [currentUnit] * blockSize) &&
			(columnDraw > 2 * blockSize && columnDraw > unitLocations [currentUnit].y - unitRange [currentUnit] * blockSize))
		{
		    locationsAttack [blockAttack] = new Point (rowDraw, columnDraw);
		    rowDraw -= blockSize;
		    columnDraw -= blockSize;
		    blockAttack++;
		    attackBlockDrawCount++;
		}
	    }
	    if (unitsCount [currentUnit].getAttackType () != 2 || currentUnit < 4)
	    {
		// Finds the attack points bottom right the unit
		rowDraw = unitLocations [currentUnit].x + blockSize;
		columnDraw = unitLocations [currentUnit].y + blockSize;
		if (unitsCount [currentUnit].getAttackType () == 3)
		{
		    rowDraw = unitLocations [currentUnit].x + blockSize * 2;
		    columnDraw = unitLocations [currentUnit].y + blockSize * 2;
		}
		while ((columnDraw < noOfColumns * blockSize && columnDraw < unitRange [currentUnit] * blockSize + unitLocations [currentUnit].y) &&
			(rowDraw < (noOfRows + 1) * blockSize && rowDraw < unitRange [currentUnit] * blockSize + unitLocations [currentUnit].x))
		{
		    locationsAttack [blockAttack] = new Point (rowDraw, columnDraw);
		    columnDraw += blockSize;
		    rowDraw += blockSize;
		    blockAttack++;
		    attackBlockDrawCount++;
		}
		// Finds the attack points top right the unit
		rowDraw = unitLocations [currentUnit].x - blockSize;
		columnDraw = unitLocations [currentUnit].y + blockSize;
		if (unitsCount [currentUnit].getAttackType () == 3)
		{
		    rowDraw = unitLocations [currentUnit].x - blockSize * 2;
		    columnDraw = unitLocations [currentUnit].y + blockSize * 2;
		}
		while ((columnDraw > 2 * blockSize && columnDraw > unitLocations [currentUnit].y - unitRange [currentUnit] * blockSize) &&
			(rowDraw > -1 && rowDraw > unitLocations [currentUnit].x - unitRange [currentUnit] * blockSize))
		{
		    locationsAttack [blockAttack] = new Point (rowDraw, columnDraw);
		    columnDraw += blockSize;
		    rowDraw -= blockSize;
		    blockAttack++;
		    attackBlockDrawCount++;
		}
	    }
	}
    }


    /**     Store the initial unit locations
     */
    public void storeUnitLocations ()
    {
	int startRow = 0;
	int startColumn = 0;
	for (int unitLocationCheck = 0 ; unitLocationCheck < unitLocations.length ; unitLocationCheck++)
	{
	    int unitBlockChecker = 1;
	    // If there is a unit on that block then keep randomizing until it is on a block
	    // with no unit
	    do
	    {
		if (unitLocationCheck < 4)
		{
		    startRow = (int) (Math.random () * 3 + (noOfRows - 2)) * blockSize;
		    startColumn = (int) (Math.random () * 3 + 3) * blockSize;
		}
		else
		{
		    startRow = (int) (Math.random () * 3) * blockSize;
		    startColumn = (int) (Math.random () * 3 + (noOfColumns - 3)) * blockSize;
		}
		unitLocations [unitLocationCheck] = new Point (startRow, startColumn);
		int row = (startRow) / 50;
		int column = (startColumn - 150) / 50;

		// Checks if there is a unit placed on that block
		if (unitGrid [row + 1] [column + 1] > 0)
		{
		    unitBlockChecker = 1;
		}
		else
		{
		    unitGrid [row + 1] [column + 1] = unitLocationCheck + 1;
		    unitBlockChecker = 0;
		}
	    }
	    while (unitBlockChecker == 1);

	    // Store the unit location of the unit
	    unitLocations [unitLocationCheck] = new Point (startRow, startColumn);
	}


	/*
	Subtract 150 from startRow to get the necessary row number.
	This is because the max width of the game screen is 950, with
	a border of 150 on the left. Subtracting 150 from max width (950)
	to get 800 would equal the height (800).
	~~~~~
	Max row / column of the field is 800, so to get the 16 blocks,
	each 50 pixels, divide it by 50 and then use the result to save
	each unit in the second unit Grid array
	*/
    }


    /** Checks if there are units around the current moving unit,
      * collision detection
      * @return     The boolean array of which grid blocks are empty
      *             around the current unit
      */
    public boolean[] checkUnitPosition ()
    {
	// Create a boolean array and change it true if there is a unit to
	// the left, bottom, up or right of current unit
	boolean[] locationsAroundUnit = new boolean [4];
	for (int unit = 0 ; unit < unitLocations.length ; unit++)
	{
	    // Create new points around the unit and check if those points
	    // are the same with any other unit locations
	    if (unitLocations [currentUnit].equals (new Point (unitLocations [unit].x, unitLocations [unit].y + 50)))
		locationsAroundUnit [0] = true;
	    else if (unitLocations [currentUnit].equals (new Point (unitLocations [unit].x, unitLocations [unit].y - 50)))
		locationsAroundUnit [1] = true;
	    else if (unitLocations [currentUnit].equals (new Point (unitLocations [unit].x - 50, unitLocations [unit].y)))
		locationsAroundUnit [2] = true;
	    else if (unitLocations [currentUnit].equals (new Point (unitLocations [unit].x + 50, unitLocations [unit].y)))
		locationsAroundUnit [3] = true;
	}
	return locationsAroundUnit;
    }


    /** Checks the points of the attack blocks and see if any units are
      * equal to the attack blocks
      * @return    If there are any units found in the attack blocks
      */
    public boolean attackUnitCheck ()
    {
	boolean unitIsAttacked = false;
	for (int checkAttackBlock = 0 ; checkAttackBlock < attackBlockDrawCount ; checkAttackBlock++)
	    for (int checkUnitBlock = 0 ; checkUnitBlock < unitLocations.length ; checkUnitBlock++)
	    {
		// Check if one of the attack blocks is the same as one of
		// the units
		if (locationsAttack [checkAttackBlock].equals (unitLocations [checkUnitBlock]))
		    // This is to make sure damage is only dealt to opposing
		    // unitsCount (friendly fire may be turned on here)
		    if ((currentUnit < 4 && checkUnitBlock > 3) || (currentUnit > 3 && checkUnitBlock < 4))
		    {
			// The unitsCount being attacked will lose HP
			// according to the attacking unit's strength

			// However, if the current unit has a critical hit then
			// deal 2 times more damage
			if (unitsCount [currentUnit].getCriticalHit () >= (int) (Math.random () * 100))
			{
			    unitsCountHP [checkUnitBlock] = unitsCountHP [checkUnitBlock] - (unitsCount [currentUnit].getStrength () * 2);
			    //Draws the Critical Hit Box
			    criticalHitCounter = true;
			    // Makes sure that the user cannot move while its animating
			    for (int time = 0 ; time <= 10 ; time++)
			    {
				Grid.paintImmediately (0, 0, Grid.getWidth (),
					Grid.getHeight ());
				delay (2);
			    }
			    criticalHitCounter = false;
			}
			else
			{
			    unitsCountHP [checkUnitBlock] = unitsCountHP [checkUnitBlock] - unitsCount [currentUnit].getStrength ();
			}
			//Draws the bubble attack
			attackCounter = true;
			// Makes sure that the user cannot move while its animating
			defendingUnitXPos = unitLocations [checkUnitBlock].x;
			defendingUnitYPos = unitLocations [checkUnitBlock].y;
			for (int time = 0 ; time <= 10 ; time++)
			{
			    Grid.paintImmediately (0, 0, Grid.getWidth (),
				    Grid.getHeight ());
			    delay (5);
			}
			attackCounter = false;
			unitIsAttacked = true;
			// If the unit is killed
			if (unitsCountHP [checkUnitBlock] <= 0)
			{
			    unitsCountHP [checkUnitBlock] = 0;
			    // If the unit being killed is red and it has not
			    // been killed already
			    if (checkUnitBlock < 4 && !unitsBubbled [checkUnitBlock])
			    {
				delay (100);
				noOfRedUnitsLeft--;
				unitsBubbled [checkUnitBlock] = true;
				unitLocations [checkUnitBlock] = new Point (0, 0);
			    }
			    else if (checkUnitBlock > 3 && !unitsBubbled [checkUnitBlock])
			    {
				delay (100);
				noOfBlueUnitsLeft--;
				unitsBubbled [checkUnitBlock] = true;
				unitLocations [checkUnitBlock] = new Point (0, 0);
			    }
			}
		    }
	    }

	if (noOfRedUnitsLeft == 0 || noOfBlueUnitsLeft == 0)
	    gameOver = true;
	return unitIsAttacked;
    }


    /** Freezes the entire program for a set amount of time
      * @param  timeMS  The amount of time im milliseconds to freeze the program
      */
    public void delay (int timeMs)
    {
	try
	{
	    Thread.sleep (timeMs);
	}

	catch (InterruptedException e)
	{
	}
    }


    /** Checks for availble options once unit has ended its turn
      */

    public void unitMoveFinish ()
    {
	// Change the unit image to a greyed out unit image
	unitImageStrings [currentUnit] += "Done";
	// The unit has ended its turn so it cannot be selected again
	unitsSelect [currentUnit] = true;
	unitMoves++;
	// Move the current unit to the next available non dead/greyed out unit
	if (redTurn)
	{
	    for (int unit = 0 ; unit < 4 ; unit++)
		if (!unitsSelect [unit] && !unitsBubbled [unit])
		    currentUnit = unit;
	}

	else if (!redTurn)
	{
	    for (int unit = 4 ; unit < 8 ; unit++)
		if (!unitsSelect [unit] && !unitsBubbled [unit])
		    currentUnit = unit;
	}


	// When all the units one team has moved
	if ((currentUnit < 4 && unitMoves == noOfRedUnitsLeft) || (currentUnit > 3 && unitMoves == noOfBlueUnitsLeft))
	{
	    // Make all the units moveable and reset all the move Counters
	    for (int unit = 0 ; unit < unitsSelect.length ; unit++)
	    {
		unitsSelect [unit] = false;
		unitMoveCounters [unit] = unitsCount [unit].getMovement ();
	    }
	    // Switch player turns, change the moveable units and set current unit to an undead unit
	    if (redTurn)
	    {
		redTurn = false;
		for (int unit = 4 ; unit < 8 ; unit++)
		    if (!unitsBubbled [unit])
			currentUnit = unit;

		//Draws the Turn switch of the game, either red turn or blue turn
		switchTurns = 0;
		attackChoice = false;
		for (int time = 0 ; time <= 50 ; time++)
		{
		    Grid.paintImmediately (0, 0, Grid.getWidth (),
			    Grid.getHeight ());
		    delay (10);
		}
		switchTurns = 1;


	    }
	    else
	    {
		redTurn = true;
		for (int unit = 0 ; unit < 4 ; unit++)
		    if (!unitsBubbled [unit])
			currentUnit = unit;

		//Draws the Turn switch of the game, either red turn or blue turn
		switchTurns = 0;
		attackChoice = false;
		for (int time = 0 ; time <= 50 ; time++)
		{
		    Grid.paintImmediately (0, 0, Grid.getWidth (),
			    Grid.getHeight ());
		    delay (10);
		}
		switchTurns = 1;

	    }
	    unitMoves = 0;
	}
	repaint ();
    }


    /** Check if the unit selected is bubbled without being attacked (aka moving onto acid)
      */
    public void checkBubbled ()
    {
	// Check if the unit has been bubbled or not
	if (unitsCountHP [currentUnit] <= 0)
	{
	    delay (200);
	    unitsCountHP [currentUnit] = 0;
	    // The unit has moved and has been bubbled
	    unitsBubbled [currentUnit] = true;
	    unitsSelect [currentUnit] = true;
	    unitLocations [currentUnit] = new Point (0, 0);
	    if (currentUnit < 4)
		noOfRedUnitsLeft--;
	    else
		noOfBlueUnitsLeft--;
	    // Move the current unit to the next available non dead/greyed out unit
	    if (redTurn)
	    {
		for (int unit = 0 ; unit < 4 ; unit++)
		    if (!unitsSelect [unit] && !unitsBubbled [unit])
			currentUnit = unit;
	    }
	    else if (!redTurn)
	    {
		for (int unit = 4 ; unit < 8 ; unit++)

		    if (!unitsSelect [unit] && !unitsBubbled [unit])
			currentUnit = unit;
	    }
	    // When all the units one team has moved
	    if ((currentUnit < 4 && unitMoves == noOfRedUnitsLeft) || (currentUnit > 3 && unitMoves == noOfBlueUnitsLeft))
	    {
		// Make all the units moveable and reset all the move Counters
		for (int unit = 0 ; unit < unitsSelect.length ; unit++)
		{
		    unitsSelect [unit] = false;
		    unitMoveCounters [unit] = unitsCount [unit].getMovement ();
		}
		// Switch player turns, change the moveable units and set current unit to an undead unit
		if (redTurn)
		{
		    redTurn = false;
		    for (int unit = 4 ; unit < 8 ; unit++)
			if (!unitsBubbled [unit])
			    currentUnit = unit;
		    if (noOfRedUnitsLeft != 0 || noOfBlueUnitsLeft == 0)
		    {
			switchTurns = 0;
			for (int time = 0 ; time <= 50 ; time++)
			{
			    Grid.paintImmediately (0, 0, Grid.getWidth (),
				    Grid.getHeight ());
			    delay (10);
			}
			switchTurns = 1;
		    }
		}
		else if (!redTurn)
		{
		    redTurn = true;
		    for (int unit = 0 ; unit < 4 ; unit++)
			if (!unitsBubbled [unit])
			    currentUnit = unit;
		    if (noOfRedUnitsLeft == 0 || noOfBlueUnitsLeft != 0)
		    {
			switchTurns = 0;
			for (int time = 0 ; time <= 50 ; time++)
			{
			    Grid.paintImmediately (0, 0, Grid.getWidth (),
				    Grid.getHeight ());
			    delay (10);
			}
			switchTurns = 1;
		    }
		}
		unitMoves = 0;
	    }
	    if (noOfRedUnitsLeft == 0 || noOfBlueUnitsLeft == 0)
		gameOver = true;
	}
    }


    /** If the user selects sound off when a sound is playing, make sure that sound is turned off
      */
    public void checkSoundOff ()
    {
	// Stop all sounds instantly if user does not want to listen to any of them
	if (!sound)
	{
	    soundPlaying = false;
	    bgMusic.stop ();
	    characterSelectMusic.stop ();
	    victoryMusic.stop ();
	    swordAttack.stop ();
	    splashAttack.stop ();
	    waterOne.stop ();
	    gunAttack.stop ();
	    swordAttack.stop ();
	    healSound.stop ();
	}
    }


    /** If the user selects sound on make sure the correct sound is played
      */
    public void checkSoundOn ()
    {
	if (sound && !soundPlaying)
	{
	    // Make sure the corresponding bg songs are played if the user selects on
	    if (redSelect || blueSelect)
	    {
		characterSelectMusic.loop ();
		soundPlaying = true;
	    }
	    else if (!gameOver)
	    {
		bgMusic.loop ();
		soundPlaying = true;
	    }
	    else if (gameOver)
	    {
		victoryMusic.loop ();
		soundPlaying = true;
	    }
	}
    }


    /** Returns the name of the unit depending on its image number
      * @param unitImageNumber The image number of the unit
      */
    public String getUnitName (int unitImageNumber)
    {
	if (unitImageNumber == 1)
	    return ("Bubble Soldier");
	else if (unitImageNumber == 2)
	    return ("Bubble Elite");
	else if (unitImageNumber == 3)
	    return ("Bubble Ninja");
	else if (unitImageNumber == 4)
	    return ("Bubble Mage");
	else if (unitImageNumber == 5)
	    return ("Bubble Sniper");
	else if (unitImageNumber == 6)
	    return ("Bubble Boy");
	else if (unitImageNumber == 7)
	    return ("Mr.Bubble");
	else if (unitImageNumber == 8)
	    return ("Bubble Tank");
	else
	    return ("");
    }


    /** Returns the name of the map depending on the cursor selection
      * @param mapNumber The map number assigned for the cursor
      */
    public String getMapName (int mapNumber)
    {
	if (mapNumber == 0)
	    return ("Acid");
	else if (mapNumber == 1)
	    return ("Continents");
	else if (mapNumber == 2)
	    return ("Corridor");
	else if (mapNumber == 3)
	    return ("Desert");
	else if (mapNumber == 4)
	    return ("FourFang");
	else if (mapNumber == 5)
	    return ("Highways");
	else if (mapNumber == 6)
	    return ("CrossRoads");
	else if (mapNumber == 7)
	    return ("Pillar");
	else if (mapNumber == 8)
	    return ("Shadow Island");
	else if (mapNumber == 9)
	    return ("Valhalla");
	else
	    return ("");

    }


    /** Returns the unit number depending on what the user selected
      * @param row The select row of the units
      * @param column The select column of the units
      */
    public int getSelectUnit (int row, int column)
    {
	// Check all the units in the top row
	if (row == 0)
	    return column;
	else if (row == 1)
	    return column + 4;
	else
	    return -1;
    }


    /** Draws the different tiles on the board
      * @param g            the window to draw the image in
      * @param xPos         the x position of the tile
      * @param yPos         the y position of the tile
      * @param value        the number to determine the block
      */
    private void draw (Graphics g, int xPos, int yPos, int value)
    {
	// Draws the different tiles
	if (value == 0)
	    g.drawImage (grass, xPos, yPos, this);

	else if (value == 1)
	    g.drawImage (forest, xPos, yPos, this);

	else if (value == 2)
	    g.drawImage (desert, xPos, yPos, this);

	else if (value == 3)
	    g.drawImage (water, xPos, yPos, this);

	else if (value == 4)
	    g.drawImage (mountain, xPos, yPos, this);

	else if (value == 5)
	    g.drawImage (highwayHorizontal, xPos, yPos, this);

	else if (value == 6)
	    g.drawImage (highwayVertical, xPos, yPos, this);

	else if (value == 7)
	    g.drawImage (acid, xPos, yPos, this);

	else if (value == 8)
	    g.drawImage (black, xPos, yPos, this);

	else if (value == 9)
	    g.drawImage (blackEmpty, xPos, yPos, this);

	else if (value == 17)
	    g.drawImage (hwTurnRight, xPos, yPos, this);

	else if (value == 18)
	    g.drawImage (hwTurnLeft, xPos, yPos, this);

	else if (value == 19)
	    g.drawImage (hwTurnUp, xPos, yPos, this);

	else if (value == 20)
	    g.drawImage (hwTurnDown, xPos, yPos, this);

	else if (value == 21)
	    g.drawImage (hwCrossroadThree, xPos, yPos, this);

	else if (value == 22)
	    g.drawImage (hwCrossroadFour, xPos, yPos, this);
    }


    /** Draws the data of each specific unit
    *@param g             Console to draw it in
    *@param squadUnit     the specific unit to get the data from
    */
    public void drawUnitData (Graphics g, Unit squadUnit)
    {
	g.drawString ("HP: " + mySquad.getUnit (getSelectUnit (unitSelectRow, unitSelectColumn)).getHP (), 100, 80);
	g.drawString ("Move: " + mySquad.getUnit (getSelectUnit (unitSelectRow, unitSelectColumn)).getMovement (), 100, 130);
	g.drawString ("Range: " + (mySquad.getUnit (getSelectUnit (unitSelectRow, unitSelectColumn)).getRange () - 1), 100, 180);
	g.drawString ("Dmg: " + (mySquad.getUnit (getSelectUnit (unitSelectRow, unitSelectColumn)).getStrength ()), 100, 230);
	g.drawString ("Crit: " + (mySquad.getUnit (getSelectUnit (unitSelectRow, unitSelectColumn)).getCriticalHit ()) + "%", 100, 280);
	g.drawImage (unitSelectCursor, 660 + unitSelectColumn * 113, 35 + unitSelectRow * 113, this);
	g.setFont (unitNameFont);
	g.drawString (getUnitName (mySquad.getUnit (getSelectUnit (unitSelectRow, unitSelectColumn)).getImageNumber ()), 100, 400);
    }



    /** The main drawing panel that declares the window to draw
      */
    private class DrawingPanel extends JPanel
    {
	public DrawingPanel (Dimension size)
	{
	    setPreferredSize (size);
	    setBackground (new Color (255, 255, 255));
	    this.setFocusable (true);
	    this.addKeyListener (new KeyHandler ());
	    this.requestFocusInWindow ();
	}

	public void paintComponent (Graphics g)
	{
	    super.paintComponent (g);

	    // Draws the title screen
	    if (titleScreenCounter == 0)
	    {
		g.drawImage (startScreenImage, 0, 0, this);
		return;
	    }

	    // Variables to count the X and Y axis' of the board
	    // To check which tile to draw
	    int xAxis = 1;
	    for (int row = 0 ; row <= (numberOfRows - 3) ; row++)
	    {
		int yAxis = 1;
		for (int column = 3 ; column <= numberOfColumns ; column++)
		{
		    // Find the x and y positions for each row and column
		    int xPos = column * blockSize;
		    int yPos = row * blockSize;

		    draw (g, xPos, yPos, board [xAxis] [yAxis]);
		    yAxis++;
		}
		xAxis++;
	    }

	    // Change the color of the menu and attack boxes depending on the
	    // player's turn
	    if (redTurn)
	    {
		for (int blueUnitImage = 4 ; blueUnitImage < 8 ; blueUnitImage++)
		    unitImageStrings [blueUnitImage] = unitImageStrings [blueUnitImage].substring (0, 8);
		attackBlock = new ImageIcon ("overlay/redBlock.png").getImage ();
		leftBar = new ImageIcon ("overlay/leftBarRed.png").getImage ();
	    }
	    else if (!redTurn)
	    {
		for (int redUnitImage = 0 ; redUnitImage < 4 ; redUnitImage++)
		    unitImageStrings [redUnitImage] = unitImageStrings [redUnitImage].substring (0, 8);
		attackBlock = new ImageIcon ("overlay/blueBlock.png").getImage ();
		leftBar = new ImageIcon ("overlay/leftBarBlue.png").getImage ();
	    }
	    // Draw the left bar
	    g.drawImage (leftBar, 0, 0, this);

	    int unitNumber = 1;
	    // Draw the health bar
	    for (int unitHPCount = unitsCountHP.length - 1 ; unitHPCount >= 0 ; unitHPCount--)
	    {
		// Draw the mini icons next to the health bars
		Image unitImage = new ImageIcon ("unitImages/" + unitImageStrings [unitHPCount].substring (0, 8) + "icon.png").getImage ();
		// Find the percentage of health of the unit according to its preset health
		double healthPercentage = unitsCountHP [unitHPCount] / unitsCount [unitHPCount].getHP ();
		g.drawImage (unitImage, blockSize / 5, (unitHPCount + 6) * blockSize - 23, this);
		g.drawString ("" + unitNumber, blockSize / 5, (int) (noOfRows - unitHPCount - 1.5) * blockSize - 23);
		// Draw the health bar rectangle according to the unit's remaining health
		g.setColor (Color.black);
		g.drawRect (blockSize - 5, (unitHPCount + 6) * blockSize - 17, (blockSize * 2 - 13), 17);
		g.setColor (Color.green);
		g.fillRect (blockSize - 4, (unitHPCount + 6) * blockSize - 15, (int) ((blockSize * 2 - 15) * healthPercentage), 14);
		g.setColor (Color.black);
		g.setFont (hpFont);
		g.drawString ("" + (int) (unitsCountHP [unitHPCount]) + " / " + unitsCount [unitHPCount].getHP ()
			, blockSize + 27,
			(unitHPCount + 6) * blockSize - 3);
		if (unitNumber == 4)
		    unitNumber = 1;
		else
		    unitNumber++;
	    }
	    // Draw the cursor on the left side to show which unit the user
	    // is moving
	    g.drawImage (cursorMenu, 0, (currentUnit + 5) * blockSize + 18, this);
	    // Draw the move counter
	    g.setFont (moveCounterFont);
	    g.drawString ("" + unitMoveCounters [currentUnit], blockSize + 18, (noOfRows) * blockSize);
	    // Draw the images & store its locations
	    for (int drawUnit = 0 ; drawUnit < unitImageStrings.length ; drawUnit++)
	    {
		Image unitImage = new ImageIcon ("unitImages/" + unitImageStrings [drawUnit] + ".png").getImage ();
		if (!unitsBubbled [drawUnit])
		    g.drawImage (unitImage, unitLocations [drawUnit].y, unitLocations [drawUnit].x, this);
	    }


	    // Draw the cursor only if it is not game over
	    cursorIcon = new ImageIcon ("overlay/cursor.png").getImage ();
	    if (!gameOver)
		g.drawImage (cursorIcon, unitLocations [currentUnit].y, unitLocations [currentUnit].x, this);

	    // If attack is chosen, then draw the grid that shows which blocks are able to attack
	    if (attackChoice)
	    {
		// Store the variables for all the attack blocks
		storeAttackBlocks ();
		// Draw the attack blocks
		for (int drawBlock = 0 ; drawBlock < attackBlockDrawCount ; drawBlock++)
		    g.drawImage (attackBlock, locationsAttack [drawBlock].y, locationsAttack [drawBlock].x, this);
	    }
	    // If the unit is attacking then draw the attacking unit and deal the damage
	    if (menu)
	    {
		if (cursor == 0)
		    g.drawImage (attackMenu, 0, 0, this);
		else if (cursor == 1)
		    g.drawImage (defendMenu, 0, 0, this);
		else if (cursor == 2)
		    g.drawImage (endMenu, 0, 0, this);
	    }
	    // Character / level selection screens
	    if (redSelect)
	    {
		g.drawImage (redSelectImage, 0, 0, this);

		if (unitSelect == 4)
		    g.drawImage (okButton, 1070, 613, this);
		if (unitSelect < 4)
		{
		    g.setFont (selectFont);

		    // Draws the data by calling the method
		    drawUnitData (g, mySquad.getUnit (getSelectUnit (unitSelectRow, unitSelectColumn)));
		}
		for (int drawImage = 0 ; drawImage < unitSelect ; drawImage++)
		    g.drawImage (new ImageIcon ("unitImages/" + unitImageStrings [drawImage] + ".png").getImage (), 650 + drawImage * 60, 650, this);
	    }
	    else if (blueSelect)
	    {
		g.drawImage (blueSelectImage, 0, 0, this);

		if (unitSelect == 4)
		    g.drawImage (okButton, 1070, 613, this);
		if (unitSelect < 4)
		{
		    g.setFont (selectFont);

		    // Draws the data by calling the method
		    drawUnitData (g, mySquad.getUnit (getSelectUnit (unitSelectRow, unitSelectColumn)));
		}
		for (int drawImage = 0 ; drawImage < unitSelect ; drawImage++)
		    g.drawImage (new ImageIcon ("unitImages/" + unitImageStrings [drawImage + 4] + ".png").getImage (), 650 + (drawImage) * 60, 650, this);
	    }
	    if (levelSelect)
	    {
		g.drawImage (levelSelectImage, 0, 0, this);
		g.setFont (mapNameFont);
		for (int map = 0 ; map < noOfMaps ; map++)
		    g.drawString (getMapName (map), 100, 100 + 30 * map);
		g.drawImage (levelSelectCursorImage, 50, 60 + levelSelectCursor * 30, this);
	    }

	    // Draws the critical hit box
	    if (criticalHitCounter)
	    {
		// Checks whether to draw it above or below the unit,
		// depending on the position on the board
		if (unitLocations [currentUnit].x >= 100)
		    g.drawImage (criticalHitBox, unitLocations [currentUnit].y - 40, (unitLocations [currentUnit].x) - 70, this);
		else
		    g.drawImage (criticalHitBox, unitLocations [currentUnit].y - 40, (unitLocations [currentUnit].x) + 50, this);
	    }
	    // Draws a healing aura around the unit
	    if (healCounter)
		g.drawImage (healAnimate, unitLocations [currentUnit].y, unitLocations [currentUnit].x, this);

	    // Draws the bubble attack
	    if (attackCounter)
		g.drawImage (bubbleAnimate, defendingUnitYPos, defendingUnitXPos, this);

	    //Draws the Turn switch of the game, either red turn or blue turn
	    if (redTurn && switchTurns == 0 && (noOfRedUnitsLeft != 0 && noOfBlueUnitsLeft != 0))
	    {
		g.drawImage (redTurnChange, 150, 264, this);
	    }
	    else if (!redTurn && switchTurns == 0 && (noOfRedUnitsLeft != 0 && noOfBlueUnitsLeft != 0))
	    {
		g.drawImage (blueTurnChange, 150, 264, this);
	    }
	    // Draws the winning team's Victory image
	    if (gameOver)
	    {
		if (noOfRedUnitsLeft == 0)
		    g.drawImage (blueWins, (noOfColumns / 3) * blockSize + 55, (noOfRows / 2) * blockSize - 40, this);

		else if (noOfBlueUnitsLeft == 0)
		    g.drawImage (redWins, (noOfColumns / 3) * blockSize + 55, (noOfRows / 2) * blockSize - 40, this);

		gameOverMenu = new ImageIcon ("overlay/menuGO" + gameOverCursor + ".png").getImage ();
		g.drawImage (gameOverMenu, (noOfColumns / 3) * blockSize + 55, (noOfRows / 2) * blockSize - 40, this);
	    }
	}
    }


    /** Handles all of the controls for the game
      */
    private class KeyHandler extends KeyAdapter
    {
	public void keyPressed (KeyEvent event)
	{
	    // If the screen is game over
	    if (gameOver)
	    {
		// Allow the user to select the option using left and right keypads
		if (event.getKeyCode () == KeyEvent.VK_LEFT && gameOverCursor > 0)
		    gameOverCursor--;
		else if (event.getKeyCode () == KeyEvent.VK_RIGHT && gameOverCursor < 3)
		    gameOverCursor++;
		else if (event.getKeyCode () == KeyEvent.VK_Z)
		{
		    // If the user selects any of the options, it is not game over anymore
		    gameOver = false;
		    if (gameOverCursor == 0)
			resetGame ();
		    else if (gameOverCursor == 1)
		    {
			if (sound)
			{
			    bgMusic.stop ();
			    victoryMusic.stop ();
			    characterSelectMusic.loop ();
			}
			// Reset all the settings and make sure it is red Select
			unitSelect = 0;
			levelSelect = false;
			redSelect = true;
			blueSelect = false;
			repaint ();

		    }
		    else if (gameOverCursor == 2)
		    {
			// Make sure level select is true
			levelSelect = true;
			resetGame ();

		    }
		    else if (gameOverCursor == 3)
		    {
			hide ();
			System.exit (0);
		    }
		}
		repaint ();
	    }
	    // If it is blue select
	    else if (blueSelect)
	    {
		// Make sure the user can select his/her units with keypad
		if (unitSelect < 4)
		    if (event.getKeyCode () == KeyEvent.VK_LEFT && unitSelectColumn > 0)
		    {
			unitSelectColumn--;
		    }
		    else if (event.getKeyCode () == KeyEvent.VK_RIGHT && unitSelectColumn < 3)
		    {
			unitSelectColumn++;
		    }
		    else if (event.getKeyCode () == KeyEvent.VK_DOWN && unitSelectRow < 1)
		    {
			unitSelectRow++;
		    }
		    else if (event.getKeyCode () == KeyEvent.VK_UP && unitSelectRow > 0)
		    {
			unitSelectRow--;
		    }
		// Once the user selects 4 units, they are prompted with an OK button to advance to next screen
		if (unitSelect == 4 && event.getKeyCode () == KeyEvent.VK_Z)
		{
		    blueSelect = false;
		    levelSelect = true;
		}
		// If the user has not selected all their units yet, store the unit into the unitsCoutn array and restore the information
		if (event.getKeyCode () == KeyEvent.VK_Z)
		{
		    if (unitSelect < 4)
		    {
			unitsCount [unitSelect + 4] = mySquad.getUnit (getSelectUnit (unitSelectRow, unitSelectColumn));
			unitSelect++;
			storeUnitInfo ();
		    }
		}
		// Removes the selected unit
		else if (event.getKeyCode () == KeyEvent.VK_X)
		{
		    if (unitSelect > 0)
		    {
			unitSelect--;
		    }
		}
		repaint ();
	    }
	    else if (redSelect && titleScreenCounter != 0)
	    {
		// Make sure the user can select his/her units with keypad
		if (unitSelect < 4)
		    if (event.getKeyCode () == KeyEvent.VK_LEFT && unitSelectColumn > 0)
		    {
			unitSelectColumn--;
		    }
		    else if (event.getKeyCode () == KeyEvent.VK_RIGHT && unitSelectColumn < 3)
		    {
			unitSelectColumn++;
		    }
		    else if (event.getKeyCode () == KeyEvent.VK_DOWN && unitSelectRow < 1)
		    {
			unitSelectRow++;
		    }
		    else if (event.getKeyCode () == KeyEvent.VK_UP && unitSelectRow > 0)
		    {
			unitSelectRow--;
		    }
		// Once the user selects 4 units, they are prompted with an OK button to advance to next screen
		if (unitSelect == 4 && event.getKeyCode () == KeyEvent.VK_Z)
		{
		    redSelect = false;
		    blueSelect = true;
		    unitSelect = 0;
		    unitSelectColumn = 0;
		    unitSelectRow = 0;
		}
		// If the user has not selected all their units yet, store the unit into the unitsCount array and restore the information
		else if (event.getKeyCode () == KeyEvent.VK_Z)
		{
		    unitsCount [unitSelect] = mySquad.getUnit (getSelectUnit (unitSelectRow, unitSelectColumn));
		    if (unitSelect < 4 && redSelect)
			unitSelect++;
		    storeUnitInfo ();

		}
		// Removes the selected unit
		else if (event.getKeyCode () == KeyEvent.VK_X)
		{
		    if (unitSelect > 0)
			unitSelect--;
		}
		repaint ();
	    }
	    // If the screen is level select
	    else if (levelSelect)
	    {
		// The user can select their levels by scrolling up or down
		if (event.getKeyCode () == KeyEvent.VK_DOWN && levelSelectCursor < noOfMaps - 1)
		{
		    levelSelectCursor++;
		}
		else if (event.getKeyCode () == KeyEvent.VK_UP && levelSelectCursor > 0)
		{
		    levelSelectCursor--;
		}

		if (event.getKeyCode () == KeyEvent.VK_Z)
		{
		    // Set the map to the map selected by the user, depending on the map the cursor is pointing towards
		    map = ("maps/" + getMapName (levelSelectCursor) + ".txt");
		    readMapInfo ();
		    levelSelect = false;
		    resetGame ();
		    if (sound)
		    {
			characterSelectMusic.stop ();
			bgMusic.loop ();
		    }
		}
		repaint ();
	    }
	    else
	    {
		// The user is able to select his/her units with 1, 2, 3 or 4. However the unit cannot be selected if it is bubbled or ended its turn
		if (event.getKeyCode () == KeyEvent.VK_1 && redTurn && !unitsSelect [0] && !unitsBubbled [0])
		    currentUnit = 0;
		else if (event.getKeyCode () == KeyEvent.VK_2 && redTurn && !unitsSelect [1] && !unitsBubbled [1])
		    currentUnit = 1;
		else if (event.getKeyCode () == KeyEvent.VK_3 && redTurn && !unitsSelect [2] && !unitsBubbled [2])
		    currentUnit = 2;
		else if (event.getKeyCode () == KeyEvent.VK_4 && redTurn && !unitsSelect [3] && !unitsBubbled [3])
		    currentUnit = 3;
		else if (event.getKeyCode () == KeyEvent.VK_1 && !redTurn && !unitsSelect [4] && !unitsBubbled [4])
		    currentUnit = 4;
		else if (event.getKeyCode () == KeyEvent.VK_2 && !redTurn && !unitsSelect [5] && !unitsBubbled [5])
		    currentUnit = 5;
		else if (event.getKeyCode () == KeyEvent.VK_3 && !redTurn && !unitsSelect [6] && !unitsBubbled [6])
		    currentUnit = 6;
		else if (event.getKeyCode () == KeyEvent.VK_4 && !redTurn && !unitsSelect [7] && !unitsBubbled [7])
		    currentUnit = 7;

		// Check if the move counter for that unit is done or not,
		// if it is done that unit cannot move anymore
		if (unitMoveCounters [currentUnit] == 0)
		    move = false;
		else
		    move = true;

		// If the unit is killed from a tile effect
		for (int unitBlock = 0 ; unitBlock < unitsCount.length ; unitBlock++)
		    if (unitsCountHP [unitBlock] <= 0)
		    {
			unitsCountHP [unitBlock] = 0;
			// If the unit being bubbled is red and it has not
			// been bubbled already
			if (unitBlock < 4 && !unitsBubbled [unitBlock])
			{
			    noOfRedUnitsLeft--;
			    unitsBubbled [unitBlock] = true;
			}
			else if (unitBlock > 3 && !unitsBubbled [unitBlock])
			{
			    noOfBlueUnitsLeft--;
			    unitsBubbled [unitBlock] = true;
			}
		    }

		// If the unit is able to move and there is no menu
		if (move && !menu)
		{
		    int row = (unitLocations [currentUnit].x) / 50;
		    int column = (unitLocations [currentUnit].y - 150) / 50;
		    if (event.getKeyCode () == KeyEvent.VK_LEFT && unitLocations [currentUnit].y > 3 * blockSize &&
			    !checkUnitPosition () [0] && board [row + 1] [column] != 3 && board [row + 1] [column] != 8)
		    {
			// Checks the terrain first before moving the unit. This allows it so that
			// it can only move onto certain terrain or would lose movement
			if (board [row + 1] [column] == 1)
			    unitMoveCounters [currentUnit]--;
			// If the unit walks onto a mountain tile
			else if (board [row + 1] [column] == 4)
			{
			    unitMoveCounters [currentUnit] -= 2;
			    // Adds one range to the current unit
			    if (unitsRangeCheck [currentUnit] == -1)
			    {
				unitsRangeCheck [currentUnit] = 1;
				unitRange [currentUnit] += unitsRangeCheck [currentUnit];
			    }
			}
			// If the unit walks onto an acid tile
			else if (board [row + 1] [column] == 7)
			{
			    unitsCountHP [currentUnit] -= 2;
			}
			// Separate check to see if the next tile is a mountain
			// or not. If it is then it would not do this.
			// If it is not a mountain tile then the range bonus
			// from the mountain tile is removed.
			if (board [row + 1] [column] != 4 && unitsRangeCheck [currentUnit] == 1)
			{
			    unitsRangeCheck [currentUnit] = -1;
			    unitRange [currentUnit] += unitsRangeCheck [currentUnit];
			}
			// Y IS LEFT TO RIGHT...... X IS UP TO DOWN
			// Removes the old unit data from the grid when it moves
			unitGrid [row + 1] [column + 1] = 0;

			unitLocations [currentUnit].y -= blockSize;
			unitMoveCounters [currentUnit]--;

			// Moves the unit data to the new section on the grid
			row = (unitLocations [currentUnit].x) / 50;
			column = (unitLocations [currentUnit].y - 150) / 50;
			unitGrid [row + 1] [column + 1] = currentUnit + 1;
		    }
		    else if (event.getKeyCode () == KeyEvent.VK_RIGHT && unitLocations [currentUnit].y < (noOfColumns - 1) * blockSize &&
			    !checkUnitPosition () [1] && board [row + 1] [column + 2] != 3 && board [row + 1] [column + 2] != 8)
		    {
			// Checks the terrain first before moving the unit. This allows it so that
			// it can only move onto certain terrain or would lose movement
			if (board [row + 1] [column + 2] == 1)
			    unitMoveCounters [currentUnit]--;
			else if (board [row + 1] [column + 2] == 4)
			{
			    unitMoveCounters [currentUnit] -= 2;
			    // Adds one range to the current unit
			    if (unitsRangeCheck [currentUnit] == -1)
			    {
				unitsRangeCheck [currentUnit] = 1;
				unitRange [currentUnit] += unitsRangeCheck [currentUnit];
			    }
			}
			else if (board [row + 1] [column + 2] == 7)
			{
			    unitsCountHP [currentUnit] -= 2;
			}
			// Separate check to see if the next tile is a mountain
			// or not. If it is then it would not do this.
			// If it is not a mountain tile then the range bonus
			// from the mountain tile is removed.
			if (board [row + 1] [column + 2] != 4 && unitsRangeCheck [currentUnit] == 1)
			{
			    unitsRangeCheck [currentUnit] = -1;
			    unitRange [currentUnit] += unitsRangeCheck [currentUnit];
			}
			// Y IS LEFT TO RIGHT...... X IS UP TO DOWN
			// Removes the old unit data from the grid when it moves
			unitGrid [row + 1] [column + 1] = 0;

			unitLocations [currentUnit].y += blockSize;
			unitMoveCounters [currentUnit]--;

			// Moves the unit data to the new section on the grid
			row = (unitLocations [currentUnit].x) / 50;
			column = (unitLocations [currentUnit].y - 150) / 50;
			unitGrid [row + 1] [column + 1] = currentUnit + 1;
		    }
		    else if (event.getKeyCode () == KeyEvent.VK_DOWN && unitLocations [currentUnit].x < (noOfRows) * blockSize &&
			    !checkUnitPosition () [2] && board [row + 2] [column + 1] != 3 && board [row + 2] [column + 1] != 8)
		    {
			// Checks the terrain first before moving the unit. This allows it so that
			// it can only move onto certain terrain or would lose movement
			if (board [row + 2] [column + 1] == 1)
			    unitMoveCounters [currentUnit]--;
			else if (board [row + 2] [column + 1] == 4)
			{
			    unitMoveCounters [currentUnit] -= 2;
			    // Adds one range to the current unit
			    if (unitsRangeCheck [currentUnit] == -1)
			    {
				unitsRangeCheck [currentUnit] = 1;
				unitRange [currentUnit] += unitsRangeCheck [currentUnit];
			    }
			}
			else if (board [row + 2] [column + 1] == 7)
			{
			    unitsCountHP [currentUnit] -= 2;
			}
			// Separate check to see if the next tile is a mountain
			// or not. If it is then it would not do this.
			// If it is not a mountain tile then the range bonus
			// from the mountain tile is removed.
			if (board [row + 2] [column + 1] != 4 && unitsRangeCheck [currentUnit] == 1)
			{
			    unitsRangeCheck [currentUnit] = -1;
			    unitRange [currentUnit] += unitsRangeCheck [currentUnit];
			}
			// Y IS LEFT TO RIGHT...... X IS UP TO DOWN
			// Removes the old unit data from the grid when it moves
			unitGrid [row + 1] [column + 1] = 0;

			unitLocations [currentUnit].x += blockSize;
			unitMoveCounters [currentUnit]--;

			// Moves the unit data to the new section on the grid
			row = (unitLocations [currentUnit].x) / 50;
			column = (unitLocations [currentUnit].y - 150) / 50;
			unitGrid [row + 1] [column + 1] = currentUnit + 1;
		    }
		    else if (event.getKeyCode () == KeyEvent.VK_UP && unitLocations [currentUnit].x > 0 && !checkUnitPosition () [3]
			    && board [row] [column + 1] != 3 && board [row] [column + 1] != 8)
		    {
			// Checks the terrain first before moving the unit. This allows it so that
			// it can only move onto certain terrain or would lose movement
			if (board [row] [column + 1] == 1)
			    unitMoveCounters [currentUnit]--;
			else if (board [row] [column + 1] == 4)
			{
			    unitMoveCounters [currentUnit] -= 2;
			    // Adds one range to the current unit
			    if (unitsRangeCheck [currentUnit] == -1)
			    {
				unitsRangeCheck [currentUnit] = 1;
				unitRange [currentUnit] += unitsRangeCheck [currentUnit];
			    }
			}
			else if (board [row] [column + 1] == 7)
			{
			    unitsCountHP [currentUnit] -= 2;
			}
			// Separate check to see if the next tile is a mountain
			// or not. If it is then it would not do this.
			// If it is not a mountain tile then the range bonus
			// from the mountain tile is removed.
			if (board [row] [column + 1] != 4 && unitsRangeCheck [currentUnit] == 1)
			{
			    unitsRangeCheck [currentUnit] = -1;
			    unitRange [currentUnit] += unitsRangeCheck [currentUnit];
			}
			// Y IS LEFT TO RIGHT...... X IS UP TO DOWN
			// Removes the old unit data from the grid when it moves
			unitGrid [row + 1] [column + 1] = 0;

			unitLocations [currentUnit].x -= blockSize;
			unitMoveCounters [currentUnit]--;

			// Moves the unit data to the new section on the grid
			row = (unitLocations [currentUnit].x) / 50;
			column = (unitLocations [currentUnit].y - 150) / 50;
			unitGrid [row + 1] [column + 1] = currentUnit + 1;
		    }
		    // Make sure you cannot have negative move counters
		    if (unitMoveCounters [currentUnit] < 0)
			unitMoveCounters [currentUnit] = 0;
		}
		checkBubbled ();
		// If there is a menu, the up and down keys do different things
		if (menu)
		{
		    if (event.getKeyCode () == KeyEvent.VK_DOWN && cursor < 2)
		    {
			cursor++;
		    }
		    else if (event.getKeyCode () == KeyEvent.VK_UP && cursor > 0)
		    {
			cursor--;
		    }
		    if (event.getKeyCode () == KeyEvent.VK_Z && attackChoice == true && cursor == 0)
		    {
			if (attackUnitCheck ())
			{
			    if (sound)
			    {
				// Play a attack sound according to the type of unit
				// Ninja
				if (unitsCount [currentUnit].getImageNumber () == 3)
				{
				    swordAttack.play ();
				    delay (200);
				}
				// Bubble Soldier and Elite and Sniper
				else if (unitsCount [currentUnit].getImageNumber () == 1 || unitsCount [currentUnit].getImageNumber () == 2
					|| unitsCount [currentUnit].getImageNumber () == 5)
				{
				    gunAttack.play ();
				    delay (150);
				}
				// Bubble Mage
				else if (unitsCount [currentUnit].getImageNumber () == 4)
				{
				    waterOne.play ();
				    delay (150);
				} // Bubble Boy
				else if (unitsCount [currentUnit].getImageNumber () == 6)
				{
				    bubbleAttack.play ();
				    delay (150);
				}
				// Mr. Bubble and Tank
				else if (unitsCount [currentUnit].getImageNumber () == 7 || unitsCount [currentUnit].getImageNumber () == 8)
				{
				    splashAttack.play ();
				    delay (200);
				}
			    }
			    endTurn = true;
			    menu = false;
			    move = true;
			    unitMoveFinish ();
			}
			else
			{
			    JOptionPane.showMessageDialog (Grid,
				    "There is nothing to attack!",
				    "Bubble Trubble",
				    JOptionPane.WARNING_MESSAGE);
			    return;
			}
		    }
		    // Whenever the user presses the Z key
		    if (event.getKeyCode () == KeyEvent.VK_Z)
		    {
			if (cursor == 0 && !endTurn)
			    attackChoice = true;
			else
			    attackChoice = false;
			// If the user selects Defend and it has only moved
			// 2 slots
			if (cursor == 1 && unitMoveCounters [currentUnit] >= unitsCount [currentUnit].getMovement () - 4)
			{
			    endTurn = true;
			    menu = false;

			    //Draws the heal animation
			    healCounter = true;
			    // Makes sure that the user cannot move while its animating
			    for (int time = 0 ; time <= 20 ; time++)
			    {
				Grid.paintImmediately (0, 0, Grid.getWidth (),
					Grid.getHeight ());
				delay (1);
			    }
			    healCounter = false;

			    // Checks whether the unit is on a forest tile. If so, then heal more
			    if (board [unitLocations [currentUnit].x / 50 + 1] [(unitLocations [currentUnit].y - 150) / 50 + 1] == 1)
				unitsCountHP [currentUnit] += (int) (unitsCount [currentUnit].getHP () / 4);       // heals more on forest tiles
			    else
				unitsCountHP [currentUnit] += (int) (unitsCount [currentUnit].getHP () / 3.5);
			    // Makes sure you can't heal more than your total hp
			    if (unitsCountHP [currentUnit] > unitsCount [currentUnit].getHP ())
				unitsCountHP [currentUnit] = unitsCount [currentUnit].getHP ();
			    if (sound)
				healSound.play ();
			    unitMoveFinish ();
			}
			// Gives user a warning that it cannot go into defence stance if the unit has moved more than 4 blocks
			else if (cursor == 1 && unitMoveCounters [currentUnit] < unitsCount [currentUnit].getMovement ())
			{
			    JOptionPane.showMessageDialog (Grid,
				    "You cannot go into defence stance if you've moved 4 or more blocks already!",
				    "Bubble Trubble",
				    JOptionPane.WARNING_MESSAGE);
			    return;
			}
			else if (cursor == 2)
			{
			    endTurn = true;
			    menu = false;
			    move = true;
			    unitMoveFinish ();
			}
		    }
		}
		// If the user is not in the menu, then they can move around
		else if (event.getKeyCode () == KeyEvent.VK_Z && !menu && !endTurn)
		{
		    menu = true;
		    move = false;
		}
		// Cancel button, cancels the menu, allows the user to move
		if (event.getKeyCode () == KeyEvent.VK_X && menu && !endTurn)
		{
		    menu = false;
		    move = true;
		    attackChoice = false;
		}

		// If the game is over then play the victory music
		if (sound && gameOver)
		{
		    bgMusic.stop ();
		    victoryMusic.loop ();
		    soundPlaying = true;
		}

		if (endTurn == true)
		    cursor = 0;
		endTurn = false;

		repaint ();
	    }
	}
    }


    public static void main (String[] args)
    {
	BubbleTrubble frame = new BubbleTrubble ();
	frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
    }
}
