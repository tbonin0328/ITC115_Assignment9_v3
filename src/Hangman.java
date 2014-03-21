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
    private JButton btn;
    private JPanel alphaPanel;
    private String alreadyUsed = "";
    private String goAgainMsg = "";
    
    private List<String> words = new ArrayList<String>();
    
    Font font;
    Color mainColor = Color.BLACK;
    Color altColor = Color.BLUE;
    
    private int missCounter = 0;
   
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
   
    public Font getFont(int size)
    {
    	return font = new Font(ONLY_FONT, Font.BOLD, size);
    }
   
    public void makeMenu()
    {
        //create a menu bar which can hold lots of menus
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
       
        //create a new menu to add to the menu bar
        JMenu menu = new JMenu("Options");
        menuBar.add(menu);
        
        //use addMenuItem method to add menus
        addMenuItem("New Game", menu);
        addMenuItem("Red Hangman", menu);
        addMenuItem("Blue Hangman", menu);
        addMenuItem("Magenta Hangman", menu);
        addMenuItem("Original Hangman", menu);
        addMenuItem("Quit", menu);
    }
   
    private void addMenuItem(String label, JMenu menu)
    {
        JMenuItem menuItem = new JMenuItem(label);
        menuItem.addActionListener(this);
        menu.add(menuItem);
    }
   
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
        	mainColor = Color.RED;
        	repaint();
        }
        else if (menuText.equals("blue hangman"))
        {
        	mainColor = Color.BLUE;
        	repaint();
        }
        else if (menuText.equals("magenta hangman"))
        {
        	mainColor = Color.MAGENTA;
        	repaint();
        }
        else if (menuText.equals("original hangman"))
        {
        	mainColor = Color.BLACK;
        	repaint();
        }
        else if (menuText.equals("quit"))
        {
        	System.exit(0);
        }
    }
   
    private JPanel addAlphaPanel(int rows, int cols, int hgap, int vgap)
    {
        alphaPanel = new JPanel(new GridLayout(rows, cols, hgap, vgap));
        add(alphaPanel, BorderLayout.SOUTH);
        
        String[] alpha = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n",
            "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

        for(int i = 0;i < alpha.length; i++)
        {
			btn = new JButton(alpha[i]);
			alphaPanel.add(btn);
			btn.addActionListener(new ActionListener(){
            
				@Override
	            public void actionPerformed(ActionEvent e)
				{
					JButton choiceBtn = (JButton)e.getSource();
					       
					String cue = choiceBtn.getText();
					char c = cue.charAt(0);
					hideThis = replace(letters, c);
					
					testIfUsed(cue, c);

					displayThis = getShowVersion(hideThis);
					System.out.println(String.valueOf(hideThis)); //TEST CODE
					System.out.println(String.valueOf(letters)); //TEST CODE
					repaint();
				}
				
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
          		g.drawString(String.valueOf(letters), 100, 500); //TEST CODE
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
    
    public List<String> buildWordList()
    {
        
        Scanner fileIn = getFileReader(GUESS_FILE);
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
   
    private String getShowVersion(char[] hideThis2) 
    {
	   	String readable = "";
	   	
        for (int i = 0; i < hideThis2.length; i++)
        {
                readable += hideThis[i] + " ";
        }
        return readable;
	}

	public char[] replace(char[] word, char replace)
    {
        //the letter comes in
        //check to see if the letter matches the first letter in the word
        //if so, replace the char at that index point in the hideThis
        //if not, leave it alone (no else statement needed)
        //return the new hideThis array, not the word array.
       
        for (int i = 0; i < word.length; i++)
        {
            if (replace == word[i])
            {
                hideThis[i] = replace;
            }
        }
       
        return hideThis;
    }
   
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
   
    public static void main (String[] args)
    {
        Hangman myFrame = new Hangman();
        myFrame.setVisible(true);
    }
}
