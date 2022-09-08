/*
*  Author: Philip Xu
*  Date: 2022/06/23
*  Description: Wordle object that allows a wordle game to be played
*/

import java.io.*;
import java.util.Arrays;

public class Wordle implements Comparable{
  private String[] guesses = new String[6];
  private String answer;
  private String[][] allTilesColoring = new String[6][5];
  private boolean isSolved = false;
  private int numGuesses = 0;
  private static int numWordleGames = 0;
  private final int GAME_ID;
  private final int NUMBER_OF_WORDS = 5757;
  
  public Wordle(){
    //Fetching a random word in the word bank
    try{
      BufferedReader reader = new BufferedReader(new FileReader("5LetterWords.txt"));
      for(int i = 0; i < (int)(Math.random()*NUMBER_OF_WORDS); i++){
        reader.readLine();
      }
  
      answer = reader.readLine();
  
      reader.close();
    }
    catch(IOException e){
      System.out.println(e);

      answer = ""; 
    }

    //Setups
    numWordleGames++;
    GAME_ID = numWordleGames;
    //System.out.println(answer);
  }

  public Wordle(String ans){
    //Setups
    answer = ans;
    numWordleGames++;
    GAME_ID = numWordleGames;
  }

  //Returns the last guess submitted
  public String getCurrentGuess(){
    if(numGuesses > 0){
      return guesses[numGuesses-1];
    }
    else{
      return "";
    }
  }

  //Returns all the guesses submitted
  public String[] getAllGuesses(){
    return guesses;
  }

  //Submits a guess
  public void addGuess(String g){
    guesses[numGuesses] = g;
    allTilesColoring[numGuesses] = findTilesColoring(g);

    isSolved = true;
    for(int i = 0; i < 5; i++){
      if(allTilesColoring[numGuesses][i] != "green"){
        isSolved = false;
        break;
      }
    }
    numGuesses++;
  }

  //Returns the colors of each letter of the last guess
  public String[] getCurrentTilesColoring(){
    return allTilesColoring[numGuesses-1];
  }

  //Returns the colors of each letter of all of the guesses
  public String[][] getAllTilesColoring(){
    return allTilesColoring;
  }

  //Returns the answer
  public String getAnswer(){
    return answer;
  }

  //Sets the answer
  public void setAnswer(String ans){
    answer = ans;
  }

  //Returns the state of the game, solved or not solved
  public boolean getIsSolved(){
    return isSolved;
  }

  //Returns the number of guesses
  public int getNumGuesses(){
    return numGuesses;
  }

  //Returns the number of Wordle objects
  public static int getNumWordleGames(){
    return numWordleGames;
  }

  //Returns the game ID of the object
  public int getGAME_ID(){
    return GAME_ID;
  }

  //Compares an object to this Wordle object, returns boolean
  @Override
  public boolean equals(Object o){
    if(o instanceof Wordle){
        Wordle w = (Wordle)o;
        if(answer.equalsIgnoreCase(w.getAnswer())){
          return true;
        }
    }
    return false;
  }

  //Compares based on the answer lexiography, returns int
  @Override
  public int compareTo(Object o){
    if(o instanceof Wordle){
      Wordle w = (Wordle)o;
      return answer.compareToIgnoreCase(w.getAnswer());
    }
    else{
      throw new ClassCastException("Cannot compare this object to Wordle object!");
    }
  }

  //String representation of a Wordle object
  @Override
  public String toString(){
    return "Wordle Object: answer = " + answer + "; guesses = " + Arrays.toString(guesses) + "; allTilesColorings = " + Arrays.deepToString(allTilesColoring) + "; isSolved = " + isSolved;
  }

  //Gives the colors of the letters of a guess without submitting a guess
  public String[] findTilesColoring(String g){
    String[] tilesColoring = new String[] {"grey", "grey", "grey", "grey", "grey"};
    boolean[] letterFound = new boolean[] {false, false, false, false, false};

    for(int i = 0; i < 5; i++){
      if(g.toLowerCase().charAt(i) == answer.toLowerCase().charAt(i)){
        tilesColoring[i] = "green";
        letterFound[i] = true;
      }
    }

    for(int i = 0; i < 5; i++){
      if(!tilesColoring[i].equals("green")){
        for(int j = 0; j < 5; j++){
          if(i != j && g.toLowerCase().charAt(i) == answer.toLowerCase().charAt(j) && letterFound[j] == false){
            tilesColoring[i] = "yellow";
            letterFound[j] = true;
            break;
          }
        }
      }
    }

    return tilesColoring;
  }
}
