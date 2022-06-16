import java.util.Scanner;

class Main {
  public static void main(String[] args) {
    Scanner s = new Scanner(System.in);
    Wordle w = new Wordle("Which");
    System.out.println(w + "\n Guess: ");
    for(int i = 0; i < 6; i++){
      w.addGuess(s.nextLine());
      System.out.println(w + "\n Guess: ");
    }
    
  }
}