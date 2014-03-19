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
import javax.swing.JLabel;
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
    private JLabel choiceLabel;
    private JLabel labelTop1;
    private JLabel labelTop2;
    private JLabel labelTop3;
   
    private char[] guessThis = null;
    private char[] hideThis = null;
    private char[] letters;
    private char[] wordUpdate;
    private JButton startButton;
    private JButton btn;
    private JPanel alphaPanel, mainPanel;
   
    JPanel leftPanel;
    JPanel rightPanel;

   
    private boolean match = false;
    private boolean start = false;
    private boolean update = false;
   
    public Hangman()
    {
        super("Hangman Game");
        setSize(APP_WIDTH, APP_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       
        setLayout(new BorderLayout());
             
        choiceLabel = new JLabel();
        //mainPanel.add(choiceLabel, BorderLayout.WEST);
        choiceLabel.setVisible(false);
        //leftPanel.add(choiceLabel, BorderLayout.SOUTH);
       
        makeMenu();
        getWord();
        
        addAlphaPanel(4, 7, 2, 2);
    }
   
    public JPanel createPanel(Color color)
    {
        JPanel myPanel = new JPanel();
        myPanel.setBackground(color);
        add(myPanel);
        return myPanel;
    }
   
    public void makeMenu()
    {
        //create a menu bar which can hold lots of menus
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
       
        //create a new menu to add to the menu bar
        JMenu menu = new JMenu("Options");
        menuBar.add(menu);
       
        addMenuItem("New Game", menu);
        addMenuItem("Quit", menu);
    }
   
    public void addMenuItem(String label, JMenu menu)
    {
        JMenuItem menuItem = new JMenuItem(label);
        menuItem.addActionListener(this);
        menu.add(menuItem);
    }
   
    @Override
    public void actionPerformed(ActionEvent e)
    {
        String menuText = e.getActionCommand().toLowerCase();
       
        if(menuText.equals("New Game"))
        {
            addAlphaPanel(4, 7, 2, 2);
            repaint();
    	}
        else if (menuText.equals("Quit"))
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
          btn=new JButton(alpha[i]);
          alphaPanel.add(btn);
          btn.addActionListener(new ActionListener(){
                  @Override
                public void actionPerformed(ActionEvent e)
                    {
                       //choiceLabel.setVisible(true);
                       JButton choiceBtn = (JButton)e.getSource();
                       choiceLabel.setText(choiceBtn.getText());
                           
                       String cue = choiceBtn.getText();
                       char c = cue.charAt(0);
                       hideThis = replace(letters, c);
                       System.out.println(hideThis);
                       repaint();
                     }       

                 });
        }
        return alphaPanel;
    }
   
    public void paint(Graphics g)
    {
        super.paint(g);
           
        	Font font = new Font("Serif", Font.BOLD, 36);
        	g.setFont(font);
        	g.drawString(String.valueOf(hideThis), 400, 200);
           
            font = new Font("Serif", Font.BOLD|Font.ITALIC, 24);
            g.setFont(font);
            g.setColor(Color.ORANGE);
            g.drawString("Welcome to the Hangman Game!", 50, 100);
           
            g.drawOval(100, 100, 200, 200);    //face
           

            //draw eyes
            g.setColor(Color.BLUE);
           
            if(!update)
            {
               
            }
            else
            {
               
            }
           
            if(!match)
            {
                g.fillOval(155, 160, 20, 20);    //left eye
            }
            else
            {
                for(int i=0; i< letters.length; i++)
                {
                     g.drawString(String.valueOf(wordUpdate), 30, 90);
                }
            }
           
            g.fillOval(230, 160, 20, 20);    //right eye
           
            //draw mouth
            g.setColor(Color.RED);
            g.drawLine(150, 250, 250, 250);
       // }
    }
   
    public char[] getWord()
    {
        Random rand = new Random(System.currentTimeMillis());
        String randWord = "";
        List<String> words = new ArrayList<String>();
        Scanner fileIn = getFileReader("input/dictionary.txt");
        while(fileIn.hasNext())
        {
            String line = fileIn.nextLine();
            String[] wordsLine = line.split(", ");
            for(String word : wordsLine)
            {
                words.add(word);
            }
        }
        randWord = words.get(rand.nextInt(words.size()));
        letters = randWord.toCharArray();
        hideThis = getHidden(letters);
       
//        System.out.println(randWord);
//        System.out.println(hideThis);
//        System.out.println(letters);
        //startButton.setVisible(false);
       
        return hideThis;
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
