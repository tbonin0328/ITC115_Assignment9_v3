import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * @author thomas bonin
 * ITC115 Assignment 9, Hangman Game
 *
 * This class contains all the methods for running the game.
 * At the top, constants are set that allow easy adjustment of the following at the coding level:
 * 		1) Application height and width.
 * 		2) The positions of the title and word to be guessed.
 * 		3) The position of screen messages that let the user know win/lose status, etc.
 * 		4) The font for text for the game.
 * 		5) The position of the hangman on the screen; can be adjusted by changing one or both
 * 		ROPE constants.
 * 		6) The input file for the words that the app choose the guess word from.
 * 
 * Additional class variables are set that allow the arrays for the hidden version of the 
 * guess word and the guess word, the alpha keyboard, the messages indicating letters used, and the 
 * count of wrong guesses (missCounter) used to be accessible throughout the class.
 * 
 * The font is set up at this level so the getFont method can be used.
 * 
 * The colors for the screen are initiated at the class level so they can be changed within
 * the program easily.
 */
public class Hangman extends JFrame implements ActionListener
{
    private static final int APP_WIDTH = 800;
    private static final int APP_HEIGHT = 800;
    private static final int FINAL_MSG_X = 100;
    private static final int FINAL_MSG_Y = 200;
    private static final int MENU_MSG_X = FINAL_MSG_X - 80;
    private static final int MENU_MSG_Y = FINAL_MSG_Y +25;
    private static final int LEFT_MARGIN = 20;
    private static final int ROPE_X = 500;
    private static final int ROPE_Y = 150;
    private static final int USED_MSG_Y = 600;
    private static final int TITLE_Y = 90;
    private static final String ONLY_FONT = "Helvetica";
    private static final String GUESS_FILE = "input/dictionary.txt";
   
    private char[] hideThis = null;
    private char[] letters;
    private String displayThis;
    private JPanel alphaPanel;
    private String alreadyUsed = "";
    private String goAgainMsg = "";
    private int missCounter = 0;
    
    Font font;
    Color mainColor = Color.BLACK;
    Color altColor = Color.BLUE;
   
    /**
     * The method for setting up the game. It leverages JFrame with "super", sets the size,
     * sets the layout, makes the menu, gets the guess word, and adds the alpha keyboard.
     */
    public Hangman()
    {
        super("Hangman Game");
        setSize(APP_WIDTH, APP_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       
        setLayout(new BorderLayout());
       
        makeMenu();
        
        getWord(buildWordList());
        
        addAlphaPanel(4, 7, 2, 2);
    }
   
    /**
     * @param size: the font size needed.
     * @return: returns a "Helvetica" bold version of the font size needed.
     */
    public Font getFont(int size)
    {
    	return font = new Font(ONLY_FONT, Font.BOLD, size);
    }
   
    /**
     * This method:
     * 		1) Creates a menu bar which can hold lots of menus.
     * 		2) Creates a new menu to add to the menu bar.
     * 		3) Use addMenuItem method to add items to the menu.
     */
    public void makeMenu()
    {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
     
        JMenu menu = new JMenu("Options");
        menuBar.add(menu);
        
        addMenuItem("New Game", menu);
        addMenuItem("Red Hangman", menu);
        addMenuItem("Blue Hangman", menu);
        addMenuItem("Magenta Hangman", menu);
        addMenuItem("Original Hangman", menu);
        addMenuItem("Quit", menu);
    }
   
    /**
     * @param label: the label to appear on the menu as an option.
     * @param menu: the menu to which the label is to be added.
     * 
     * This method adds a menu item to the specified menu.
     */
    private void addMenuItem(String label, JMenu menu)
    {
        JMenuItem menuItem = new JMenuItem(label);
        menuItem.addActionListener(this);
        menu.add(menuItem);
    }
   
    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     * 
     * This method contains the code needed to implement certain actions depending on 
     * which menutime is chosen in the menu (i.e., starts a new game, quits the game,
     * or changes the color of the game).
     */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        String menuText = e.getActionCommand().toLowerCase();
       
        if(menuText.equals("new game"))
        {
            getWord(buildWordList());
            alreadyUsed = "";
            add(alphaPanel, BorderLayout.SOUTH);
            missCounter = 0;
            repaint();
    	}
        else if (menuText.equals("red hangman"))
        {
        	newScreen(Color.RED, Color.MAGENTA);
        }
        else if (menuText.equals("blue hangman"))
        {
        	newScreen(Color.BLUE, Color.BLACK);
        }
        else if (menuText.equals("magenta hangman"))
        {
        	newScreen(Color.MAGENTA, Color.BLUE);
        }
        else if (menuText.equals("original hangman"))
        {
        	newScreen(Color.BLACK, Color.BLUE);
        }
        else if (menuText.equals("quit"))
        {
        	System.exit(0);
        }
    }
    
    /**
     * @param color1: the primary color for text and images in the game.
     * @param color2: the alternate color used for the win/lose message.
     * 
     * This method simply uses the parameters to change the colors and repaint the screen.
     */
    private void newScreen(Color color1, Color color2)
    {
    	mainColor = color1;
    	altColor = color2;
    	repaint();
    }
   
    /**
     * @param rows: the number of rows desired for the alpha keyboard.
     * @param cols: the number of columns desired for the alpha keyboard.
     * @param hgap: the horizontal space desired between the keys (buttons).
     * @param vgap: the vertical space desired between the keys (buttons).
     * @return: the alpha keyboard the bottom of the screen.
     * 
     * This method creates the alpha keyboard and sets up the buttons to work
     * such that, once a letter button is pressed, the following occurs:
     * 		1) the guess word is updated with the letter filled in if it's valid.
     * 		2) the guessed letter is checked to see if it's in the array of letters used.
     * 		3) the display of the letters used on the screen is updated.
     * 		4) if the letter has been used already, a message appears stating such.
     * 		5) The missCounter (wrong guesses) is increased if the letter guessed
     * 		has not already been used and is incorrect.
     */
    private JPanel addAlphaPanel(int rows, int cols, int hgap, int vgap)
    {
        alphaPanel = new JPanel(new GridLayout(rows, cols, hgap, vgap));
        add(alphaPanel, BorderLayout.SOUTH);
        
        String[] alpha = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n",
            "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

        for(int i = 0;i < alpha.length; i++)
        {
			JButton btn = new JButton(alpha[i]);
			alphaPanel.add(btn);
			btn.addActionListener(new ActionListener(){
            
				/* (non-Javadoc)
				 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
				 * 
				 * This method gets the text from the button and runs it through a method that replaces
				 * the underscores with that letter in the displayed word (if the letter is a correct 
				 * guess). The method also runs the testIfUsed method to see if it's in the used letters
				 * array. And, it updates the displayed version of the word.
				 */
				@Override
	            public void actionPerformed(ActionEvent e)
				{
					JButton choiceBtn = (JButton)e.getSource();
					       
					String cue = choiceBtn.getText();
					char c = cue.charAt(0);
					hideThis = replace(letters, c);
					
					testIfUsed(cue, c);

					displayThis = getShowVersion(hideThis);
					repaint();
				}
				
				/**
				 * @param cue: the string version of the letter on the button pressed.
				 * @param c: the char version of the letter on the button pressed.
				 * 
				 * This method checks to see if the letter selected has already been used.
				 * If it has it changes the goAgainMsg to tell the user it's already been
				 * used. If not, it adds the letter, to the "already use" array, changes
				 * the goAgainMsg to blank, and updates the missCounter if the letter is not
				 * in the guess word.
				 */
				public void testIfUsed(String cue, char c)
				{
					if (alreadyUsed.indexOf(cue) != -1)
					{
						goAgainMsg = "You used that one already, silly!";
					}
					else
					{
						alreadyUsed += cue + " ~ ";	
						goAgainMsg = "";
				
						if(String.valueOf(letters).indexOf(c) != -1)
						{
							
						}
						else
						{
						   missCounter++;
						}
					}
				}
			});
		}
        return alphaPanel;
	}
   
    /* (non-Javadoc)
     * @see java.awt.Window#paint(java.awt.Graphics)
     * 
     * This method draws the screen, and it only draws certain portion (or updates them) if 
     * certain conditions are met:
     * 		1) The "You Win!" message is only painted if the user's current word matches the guess 
     * 		word. Otherwise, the screen updates the "already used" messages only.
     * 		2) For each wrong guess the user makes, a new part for the hangman image is added to 
     * 		the screen.
     * 		3) Once nine wrong guesses have been made, a "You Lose!" message is displayed and the
     * 		alpha keyboard is removed.
     */
    public void paint(Graphics g)
    {
        super.paint(g);
           
        	g.setFont(getFont(28));
        	g.setColor(mainColor);
        	
            if (String.valueOf(hideThis).equals(String.valueOf(letters)))
            {
            	g.setFont(getFont(36));
            	g.setColor(altColor);
            	g.drawString("You Win!", FINAL_MSG_X, FINAL_MSG_Y);
            	g.setFont(getFont(18));
            	g.drawString("Use Options Menu to Quit or Play Again!", MENU_MSG_X, MENU_MSG_Y);
            } 
            else
            {
          		g.drawString("Already Used: ", LEFT_MARGIN, USED_MSG_Y);
          		g.drawString(goAgainMsg, LEFT_MARGIN, USED_MSG_Y - 50);
          		g.setFont(getFont(16));
          		g.drawString(alreadyUsed, LEFT_MARGIN, USED_MSG_Y + 50);
            }
            
            g.setFont(getFont(36));
            g.setColor(mainColor);
            g.drawString("Welcome to the Hangman Game!", LEFT_MARGIN, TITLE_Y);
            
            g.fillRect(ROPE_X + 150, ROPE_Y + 450, 100, 25); //base for post for hangman
            g.drawLine(ROPE_X + 200, ROPE_Y, ROPE_X + 200, ROPE_Y + 450); //vertical up and down; post for hangman
            g.drawLine(ROPE_X, ROPE_Y, ROPE_X + 200, ROPE_Y); //horizontal extending from post for hangman
            g.drawLine(ROPE_X, ROPE_Y, ROPE_X, ROPE_Y + 50); //rope
            
            if(missCounter > 0)
            {
            	g.fillRect(ROPE_X - 25, ROPE_Y + 50, 50, 25); //top of hat
            	g.fillRect(ROPE_X - 50, ROPE_Y + 75, 100, 25);//bottom of hat
            }
            if(missCounter > 1 )
            {
               	g.fillOval(ROPE_X - 35, ROPE_Y + 100, 70, 70);  //face
            }
            if(missCounter > 2)
            {
               	g.fillRect(ROPE_X - 20, ROPE_Y + 171, 40, 125); //body
            }
            if(missCounter > 3)
            {
            	g.fillRect(ROPE_X + 20, ROPE_Y + 171, 50, 20); //right arm
            }
            if(missCounter > 4)
            {
            	g.fillRect(ROPE_X - 70, ROPE_Y + 171, 50, 20); //left arm
            }
            if(missCounter > 5)
            {
            	g.fillRect(ROPE_X, ROPE_Y + 296, 85, 20); //right leg
            }
            if(missCounter > 6)
            {
            	g.fillRect(ROPE_X - 85, ROPE_Y + 296, 85, 20); //left leg
            }
            if(missCounter > 7)
            {
            g.fillRect(ROPE_X + 87, ROPE_Y + 276, 15, 40); //right foot
            }
            if(missCounter > 8)
            {
	            g.fillRect(ROPE_X - 102, ROPE_Y + 276, 15, 40); //left foot
	            g.setFont(getFont(36));
	        	g.setColor(altColor);
	        	g.drawString("You Lose!", FINAL_MSG_X, FINAL_MSG_Y);
	        	g.setFont(getFont(18));
            	g.drawString("Use Options Menu to Quit or Play Again!", MENU_MSG_X, MENU_MSG_Y);
	        	remove(alphaPanel);
	        	repaint();
            }
            g.setFont(getFont(36));
            g.setColor(mainColor);
            g.drawString(displayThis, LEFT_MARGIN + 20, TITLE_Y + 235);
    }
 
    /**
     * @param words: the word list built by the FileReader via the buildWordList method.
     * @return: the hidden version of the guess word (the one to be displayed on the screen).
     * 
     * This method generates a random word from the dictionary.txt file. It then creates
     * a letters array from the word, creates a "hidden" version of the words (one where each letter
     * is replaced by an underscore), and creates a version of the hidden word for display (where
     * there's a space between each underscore).
     */
    public char[] getWord(List<String> words)
    {
        Random rand = new Random(System.currentTimeMillis());
        String randWord = "";
        randWord = words.get(rand.nextInt(words.size()));
        letters = randWord.toCharArray();
        hideThis = getHidden(letters);
        displayThis = getShowVersion(hideThis);
       
        return hideThis;
    }
    
    /**
     * @return: List<String> called words that contains all the words in dictionary.txt.
     * 
     * This method used the FileReader to pull in the dictionary words and create a List
     * from which the getWord method above can "choose" a random word.
     */
    public List<String> buildWordList()
    {
        Scanner fileIn = getFileReader(GUESS_FILE);
        List<String> words = new ArrayList<String>();
        while(fileIn.hasNext())
        {
            String line = fileIn.nextLine();
            String[] wordsLine = line.split(", ");
            for(String word : wordsLine)
            {
                words.add(word);
            }
        }
		return words;
    }
   
    /**
     * @param hideThis2: the hidden version of the guess word.
     * @return: a "readable" version of the word for display on the screen.
     * 
     * This method takes in the hidden version of the guess word and inserts
     * spaces between each underscore so the user doesn't just see one long line
     * for the guess word but a line with spaces between each underscore.
     */
    private String getShowVersion(char[] hideThis2) 
    {
	   	String readable = "";
	   	
        for (int i = 0; i < hideThis2.length; i++)
        {
                readable += hideThis[i] + " ";
        }
        return readable;
	}

	/**
	 * @param word: the guess word for the current game session.
	 * @param replace: the character that the user selected with the alpha keyboard.
	 * @return: an array that contains a version of the hidden version of the guess word
	 * with each letter that's a match to the guess word replaced.
	 * 
	 * This method takes in the guess word selected by the program and the letter selected
	 * by the user and if the letter is in the word, replaces each instance of that letter 
	 * in the word.
	 */
	public char[] replace(char[] word, char replace)
    {
        for (int i = 0; i < word.length; i++)
        {
            if (replace == word[i])
            {
                hideThis[i] = replace;
            }
        }
        return hideThis;
    }
   
    /**
     * @param fileName: the file to be processed by the FileReader.
     * @return: the new file to be scanned for the program.
     * 
     * This method returns a scannable version of a file if it exists. If 
     * it doesn't exist, it returns an error message.
     */
    public Scanner getFileReader(String fileName)
    {
        File file = new File(fileName);

        try
        {
            return new Scanner(file);
        }
       
        catch (FileNotFoundException e)
        {
            System.out.println(e.getMessage());
            System.exit(-1);
            return null;
        }
    }
   
    /**
     * @param word: A word array (for this program, the guess word array).
     * @return: A version of the word with each letter replaced by an underscore.
     * 
     * This method takes in an array letters (from the guess word) and creates a 
     * new array that replaces each letter with an underscore.
     */
    public char[] getHidden(char[]word)
    {
        char[] hideVersion = new char[word.length];
        for (int i = 0; i < word.length; i++)
        {
            if (Character.isLetter(word[i]))
            {
                hideVersion[i] = '_';
            }
        }
        return hideVersion;
    }
   
    /**
     * @param args: the array of strings needed to run the program.
     * 
     * This method is the main method used to run the program.
     */
    public static void main (String[] args)
    {
        Hangman myFrame = new Hangman();
        myFrame.setVisible(true);
    }
}
