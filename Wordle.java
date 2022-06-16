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
  
  public Wordle(){
    try{
      BufferedReader reader = new BufferedReader(new FileReader("5LetterWords.txt"));
      for(int i = 0; i < (int)(Math.random()*5757); i++){
        reader.readLine();
      }
  
      answer = reader.readLine();
  
      reader.close();
    }
    catch(IOException e){
      System.out.println(e);

      answer = ""; 
    }

    numWordleGames++;
    GAME_ID = numWordleGames;
  }

  public Wordle(String ans){
    answer = ans;
    numWordleGames++;
    GAME_ID = numWordleGames;
  }

  public String getCurrentGuess(){
    if(numGuesses > 0){
      return guesses[numGuesses-1];
    }
    else{
      return "";
    }
  }

  public String[] getAllGuesses(){
    return guesses;
  }

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

  public String[] getCurrentTilesColoring(){
    return allTilesColoring[numGuesses-1];
  }

  public String[][] getAllTilesColoring(){
    return allTilesColoring;
  }

  public String getAnswer(){
    return answer;
  }

  public void setAnswer(String ans){
    answer = ans;
  }

  public boolean getIsSolved(){
    return isSolved;
  }

  public int getNumGuesses(){
    return numGuesses;
  }

  public static int getNumWordleGames(){
    return numWordleGames;
  }

  public int getGAME_ID(){
    return GAME_ID;
  }

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

  @Override
  public String toString(){
    return "Wordle Object: answer = " + answer + "; guesses = " + Arrays.toString(guesses) + "; allTilesColorings = " + Arrays.deepToString(allTilesColoring) + "; isSolved = " + isSolved;
  }

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
            System.out.println(i + "" + j);
            break;
          }
        }
      }
    }

    return tilesColoring;
  }
}
