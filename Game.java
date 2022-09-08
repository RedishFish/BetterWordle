/*
*  Author:  Philip Xu
*  Date:  2022/06/23
*  Description:  Main game GUI for this assignment
*/

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import java.io.*;

public class Game extends JPanel implements KeyListener, ActionListener{ 
  
  //**
  //CONSTANTS
  //**
  
  private final int[][] XP_TABLE = {{1, 100}, {5, 150}, {10, 200}, {20, 300}, {50, 500}, {100, 1000}}; //for each (level, xp) pair, it represents the starting level when the xp rule applies, and the total xp needed to level up
  private final Color TRANSLUCENT_GREY = new Color(204, 204, 204, 200); //custom color
  private final Color DARK_GREEN = new Color(0, 200, 0); //custom color
  private final Color LIGHT_RED = new Color(255, 69, 0);
  private final Border BLACK_LINE = BorderFactory.createLineBorder(Color.BLACK); //black border line
  private final int NUMBER_OF_WORDS = 5757; //number of word in the word bank 5LetterWords.txt
  private final String[] VOWELS = new String[] {"a", "e", "i", "o", "u", "y"}; //all possible vowels
  private final String[] CONSONANTS = new String[] {"b", "c", "d", "f", "g", "h", "j", "k", "l", "m", "n", "p", "q", "r", "s", "t", "v", "w", "x", "y", "z"}; //all possible consonants

  //**
  //VARIABLES (non-components)
  //**
  
  private int x = 0; //text cursor's x-value with respect to the letter tiles
  private int y = 0; //text cursor's y-value with respect to the letter tiles
  private String guess = ""; //stores the current guess
  private String greenLetters = ""; //stores all letters guessed, which are green in color on the letter pad
  private int currentKeyCode; //key code
  private boolean isValidWord; //a word's validity according to 5LetterWords.txt
  private boolean onMenu = false; //disables(false) or enables(true) the menu
  private String initialPersonalBest; //personalBest stored before starting the program

  Wordle wordle = new Wordle(); //New Wordle game
  String[] tileColors = new String[5]; //tileColors for 1 row

  //**
  //GUI COMPONENTS
  //**
  
  //Main wordle game components
  private JTextField invisibleTextField;
  private JLabel[][] tiles = new JLabel[6][5];
  
  //End game menu components
  private JLabel gameMessage, expBox, oldLevelBar, addedLevelBar, levelUpMessage, currentLevel, winningStreak, personalBest, personalBestMenuDisplay, expMessage;
  private JButton playAgainButton;
  JPanel menu = new JPanel();

  //Letterpads components
  JLabel[] vowelsPad = new JLabel[6];
  JLabel[] consonantsPad = new JLabel[21];

  
  //**
  //CONSTRUCTOR METHOD - SETTING UP COMPONENTS
  //**
  public Game(){
    
    //**Main Setup**//
    setBounds(0, 0, 500, 400);
    setLayout(null);
    this.add(menu);

    //Invisible textfield to be able to take key input
    invisibleTextField = new JTextField();
    invisibleTextField.setBounds(0, 0, 0, 0);
    invisibleTextField.addKeyListener(this);
    this.add(invisibleTextField);

    //Add the 30 letter tiles
    for(int i = 0; i < 6; i++){
      for(int j = 0; j < 5; j++){
        tiles[i][j] = new JLabel();
        tiles[i][j].setBounds(20+50*j, 20+50*i, 40, 40);
        tiles[i][j].setBorder(BLACK_LINE);
        tiles[i][j].setOpaque(true);
        this.add(tiles[i][j]);
      }
    }
    
    //**Letterpad setup**//

    //Setting up the vowel pad
    for(int i = 0; i < 6; i++){
      vowelsPad[i] = new JLabel(VOWELS[i], SwingConstants.CENTER);
      vowelsPad[i].setBounds(280+30*i, 150, 20, 30);
      vowelsPad[i].setBorder(BLACK_LINE);
      vowelsPad[i].setOpaque(true);
      this.add(vowelsPad[i]);
    }

    //Setting up the consonant pad
    for(int i = 0; i < 21; i++){
      consonantsPad[i] = new JLabel(CONSONANTS[i], SwingConstants.CENTER);
      consonantsPad[i].setBounds(280+30*(i%7), 200+40*(i/7), 20, 30);
      consonantsPad[i].setBorder(BLACK_LINE);
      consonantsPad[i].setOpaque(true);
      this.add(consonantsPad[i]);
    }

    
    //**Winning streak and personal best message setup**//

    //winningStreak setup
    winningStreak = new JLabel();
    winningStreak.setBounds(280, 30, 200, 15);

    //Fetches personal best and display winningStreak from Stats.txt
    try{
      BufferedReader reader = new BufferedReader(new FileReader("Stats.txt"));
      for(int i = 0; i < 2; i++){
        reader.readLine();
      }
      initialPersonalBest = reader.readLine().substring(14);
      winningStreak.setText("Winning Streak: " + reader.readLine().substring(14));

      reader.close();
    }
    catch(IOException e){
      System.out.println(e);
    }

    //Displays personal best on game
    if(initialPersonalBest.equals("7")){
      personalBest = new JLabel("Personal Best: None");
    }
    else{
      personalBest = new JLabel("Personal Best: " + initialPersonalBest + " guess(es)");
    }
    personalBest.setBounds(280, 55, 200, 15);

    this.add(winningStreak);
    this.add(personalBest);

    
    //**End game menu setup**//

    //Hides the menu initially
    menu.setVisible(false);

    //Menu frame setup
    menu.setBounds(10, 10, 260, 310);
    menu.setLayout(null);
    menu.setBackground(TRANSLUCENT_GREY);
    menu.setBorder(BLACK_LINE);

    //Displays either "You won" or "You lost"
    gameMessage = new JLabel("", SwingConstants.CENTER);
    gameMessage.setBounds(0, 40, 270, 40);
    gameMessage.setFont(new Font("Arial", Font.PLAIN, 32));
    menu.add(gameMessage);

    //Experience bar
    expBox = new JLabel("", SwingConstants.CENTER);
    expBox.setBounds(50, 100, 160, 20);
    expBox.setOpaque(true);
    expBox.setBackground(Color.WHITE);
    expBox.setBorder(BLACK_LINE);
    menu.add(expBox);

    //Displays "current experience"/"experience to next level" in the experience bar
    expMessage = new JLabel("", SwingConstants.CENTER);
    expMessage.setBounds(0, 0, 160, 20);
    expBox.add(expMessage);

    //Level up notification
    levelUpMessage = new JLabel("Level up");
    levelUpMessage.setFont(new Font("Arial", Font.PLAIN, 10));
    levelUpMessage.setForeground(DARK_GREEN);
    levelUpMessage.setBounds(5, 120, 45, 10);
    menu.add(levelUpMessage);
    levelUpMessage.setVisible(false);

    //Displays level
    currentLevel = new JLabel();
    currentLevel.setFont(new Font("Arial", Font.PLAIN, 10));
    currentLevel.setBounds(10, 105, 40, 10);
    menu.add(currentLevel);

    //Displays past level bar in the experience box
    oldLevelBar = new JLabel();
    oldLevelBar.setOpaque(true);
    oldLevelBar.setBackground(DARK_GREEN);
    oldLevelBar.setBorder(BLACK_LINE);
    expBox.add(oldLevelBar);

    //Displays the added level bar in the experience box
    addedLevelBar = new JLabel();
    addedLevelBar.setOpaque(true);
    addedLevelBar.setBackground(Color.GREEN);
    addedLevelBar.setBorder(BLACK_LINE);
    expBox.add(addedLevelBar);

    //Play again button
    playAgainButton = new JButton("Play again");
    playAgainButton.setBounds(60, 180, 140, 60);
    playAgainButton.addActionListener(this);
    menu.add(playAgainButton);

    //If personal best is achieved, the end game menu will notify of a personal best
    personalBestMenuDisplay = new JLabel("", SwingConstants.CENTER);
    personalBestMenuDisplay.setBounds(0, 130, 270, 20);
    personalBestMenuDisplay.setVisible(false);
    menu.add(personalBestMenuDisplay);
  }

  //**
  //FOR WHEN A KEY IS PRESSED
  //**
  @Override
  public void keyPressed(KeyEvent e){
    
    if(!onMenu){ //Makes sure that the menu is called to be used
      currentKeyCode = e.getKeyCode(); 

      //**Typing an alphabet character**//
      
      if(currentKeyCode >= KeyEvent.VK_A && currentKeyCode <= KeyEvent.VK_Z){

        //If the first empty field is one of the first 4 letters
        if(y < 4){
          guess += (char)currentKeyCode;
          tiles[x][y].setText(String.valueOf((char)currentKeyCode));
          y++;  
        }

        //If the first empty field is the last letter (This makes sure that if all fields are filled, nothing will happen)
        else if(y == 4 && guess.length() == 4){
          guess += (char)currentKeyCode;
          tiles[x][y].setText(String.valueOf((char)currentKeyCode));
        }
      }

      //**Submitting a guess if return is pressed**//
        
      else if(currentKeyCode == KeyEvent.VK_ENTER && y == 4 && guess.length() == 5){

        //Determines if the submission is one of the words in the word bank
        isValidWord = false;

        try{
          BufferedReader reader = new BufferedReader(new FileReader("5LetterWords.txt"));
  
          for(int i = 0; i < NUMBER_OF_WORDS; i++){
            if(reader.readLine().equalsIgnoreCase(guess)){
              isValidWord = true;
              break;
            }
          }
          reader.close();
        }
        catch(IOException ex){
          System.out.println(ex);
        }

        //If the word is determined as valid...
        if(isValidWord){

          //Add the guess
          wordle.addGuess(guess);
          

          tileColors = wordle.getCurrentTilesColoring();

          //Display letter colors both on the game and the letterpad
          for(int i = 0; i < 5; i++){
            if(tileColors[i].equals("grey")){
              tiles[x][i].setBackground(Color.GRAY);
              updateLetterpadLetter(String.valueOf(guess.charAt(i)), Color.GRAY);
            }
              
            else if(tileColors[i].equals("yellow")){
              tiles[x][i].setBackground(Color.YELLOW);

              //Makes sure that tiles that are already green will not be made yellow 
              boolean isAlreadyGuessed = false;
              for(int j = 0; j < greenLetters.length(); j++){
                if(greenLetters.charAt(j) == guess.charAt(i)){
                  isAlreadyGuessed = true;
                  break;
                }
              } 
              if(!isAlreadyGuessed){
                updateLetterpadLetter(String.valueOf(guess.charAt(i)), Color.YELLOW);
              }    
            }
            
            else if(tileColors[i].equals("green")){ 
              tiles[x][i].setBackground(Color.GREEN);
              greenLetters += guess.charAt(i); updateLetterpadLetter(String.valueOf(guess.charAt(i)), Color.GREEN);
            }
          }

          //Display end game menu if the game had ended
          if(wordle.getIsSolved()){
            updateStats((7-wordle.getNumGuesses())*25, true);
            onMenu = true;
            gameMessage.setText("You won!");
            menu.setVisible(true);
          }
          else if(x == 5){
            updateStats(10, false);
            onMenu = true;
            gameMessage.setText("You lost!");
            menu.setVisible(true);
          }
          //Go to a new tile line otherwise
          else{
            x++;
            y = 0;
            guess = "";
          }
        }

        //If the guess is invalid, we make all tiles red until the guess letters are being deleted
        else{
          for(int i = 0; i < 5; i++){
            tiles[x][i].setBackground(LIGHT_RED);
          }
        }
      }

      //**Deletes last letter if the backspace is pressed**//
        
      else if(currentKeyCode == KeyEvent.VK_BACK_SPACE && y > 0){
        //Makes sure that the red background color (as a warning) is deleted if it is present
        if(guess.length() == 5){
          for(int i = 0; i < 5; i++){
            tiles[x][i].setBackground(null);
          }
        }

        //Deleting the last character
        y = guess.length()-1;
        tiles[x][y].setText("");
        guess = guess.substring(0, guess.length()-1);
      }
    }
  }

  @Override
  public void keyReleased(KeyEvent e){
  }

  @Override
  public void keyTyped(KeyEvent e){
  }

  //**
  //FOR WHEN PLAY AGAIN IS PRESSED
  //**
  @Override
  public void actionPerformed(ActionEvent e){
    
    onMenu = false; //Disables menu
    menu.setVisible(false); //Hides menu

    //Clears the letter tiles
    for(int i = 0; i < 6; i++){
      for(int j = 0; j < 5; j++){
        tiles[i][j].setText(null);
        tiles[i][j].setBackground(null);
      }
    }

    //Clears the letter pads of colors
    for(int i = 0; i < 6; i++){
      vowelsPad[i].setBackground(null);
    }
    for(int i = 0; i < 21; i++){
      consonantsPad[i].setBackground(null);
    }

    //Resetting variables
    x = 0;
    y = 0;
    guess = "";
    greenLetters= "";
    wordle = new Wordle();
    
  }

  
  //***********************
  //        Methods
  //***********************

  //**
  //Function that updates both the GUI and txt file with the correct oldStats
  //**
  public void updateStats(int exp, boolean gameStatus){
    
    try{
      
      //** Loading stats **//
      
      BufferedReader reader = new BufferedReader(new FileReader("Stats.txt"));
    
      int oldStats[] = new int[4]; //int oldStats[] has attributes {level, xp, personalBest, winningStreak}

      //Loading the old stats from Stats.txt
      for(int i = 0; i < 4; i++){
        oldStats[i] = Integer.parseInt(reader.readLine().substring(14));
      }

      //find the new stats
      int newLevel = oldStats[0] + (oldStats[1] + exp)/findXPForLevel(oldStats[0]);
      int newExp = (oldStats[1] + exp)%findXPForLevel(oldStats[0]);

      reader.close();

      
      //**Update Stats.txt**//
      
      PrintWriter writer = new PrintWriter(new FileWriter("Stats.txt"));

      //Update level
      writer.println("level:        " + newLevel);

      //Update XP
      writer.println("exp:          " + newExp);

      //Update personalBest
      if(gameStatus){
        if(oldStats[2] > wordle.getNumGuesses()){
          writer.println("personalBest: " + wordle.getNumGuesses());
        }
        else{
          writer.println("personalBest: " + oldStats[2]);
        }
      }
      else{
        writer.println("personalBest: " + oldStats[2]);
      }

      //Update winningStreak
      if(gameStatus){
        oldStats[3]++;
      }
      else{
        oldStats[3] = 0;
      }
      writer.println("winningStreak:" + oldStats[3]);

      
      //**Update GUI**//

      //Update all experience and level related components
      expMessage.setText("<html>" + newExp + " / " + findXPForLevel(newLevel) + "xp" + "<span style=\"color:green;\"> (+" + exp + ")</span>" + "</html>");
      currentLevel.setText("Lvl. " + newLevel);

      if(newLevel == oldStats[0]){
        oldLevelBar.setBounds(0, 0, (int)(160.0/findXPForLevel(newLevel)*oldStats[1]), 20);
        levelUpMessage.setVisible(false);
      }
      else{
        oldLevelBar.setBounds(0, 0, 0, 0);
        levelUpMessage.setVisible(true);
      }

      addedLevelBar.setBounds(0, 0, (int)(160.0/findXPForLevel(newLevel)*newExp), 20);

      //Update winningStreak and personalBest related messages
      winningStreak.setText("Winning Streak: " + oldStats[3]);
      if(gameStatus && oldStats[2] > wordle.getNumGuesses()){
        personalBestMenuDisplay.setVisible(true);
        personalBestMenuDisplay.setText("Personal Best! " + wordle.getNumGuesses() + " guess(es)");
        personalBest.setText("Personal Best: " + wordle.getNumGuesses() + " guess(es)");
      }
      else{
        personalBestMenuDisplay.setVisible(false);
      }

      writer.close();
    }
      
    catch(IOException ex){
      System.out.println(ex);
    }
    
  }

  //**
  //Updates the letter pad's colors with a color
  //**
  public void updateLetterpadLetter(String letter, Color color){

    //Set the vowel pad's colors
    for(int i = 0; i < 6; i++){
      if(VOWELS[i].equalsIgnoreCase(letter)){
        vowelsPad[i].setBackground(color);
        break;
      }
    }

    //Set the consonant pad's colors
    for(int i = 0; i < 21; i++){
      if(CONSONANTS[i].equalsIgnoreCase(letter)){
        consonantsPad[i].setBackground(color);
        break;
      }
    }
    
  }

  //**
  //Determines the total XP needed for a specific level
  //**
  public int findXPForLevel(int level){

    for(int i = 0; i < XP_TABLE.length-1; i++){
      if(level >= XP_TABLE[i][0] && level < XP_TABLE[i+1][0]){
        return XP_TABLE[i][1];
      }
    }

    return XP_TABLE[XP_TABLE.length-1][1];
    
  }
  
}