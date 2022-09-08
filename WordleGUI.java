/*
*  Author: Philip Xu
*  Date: 2022/06/23
*  Description: Wrapper class
*/

import javax.swing.*;

public class WordleGUI extends JFrame{
  public WordleGUI(){
    super("Wordle");
    setBounds(0, 0, 500, 350);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLayout(null);
    
    Game game = new Game();
    this.add(game);
    
    setVisible(true);
  }
}