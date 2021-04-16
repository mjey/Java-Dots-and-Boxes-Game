package edu.vt.cs5044.DABGame;

import edu.vt.cs5044.DotsAndBoxes.NewGameDialogue;

public class DABGame {
  public static void main(String[] args) {
    try {
      for (javax.swing.UIManager.LookAndFeelInfo info: javax.swing.UIManager.getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
          javax.swing.UIManager.setLookAndFeel(info.getClassName());
          break;
        }
      }
    } catch (ClassNotFoundException ex) {
      java.util.logging.Logger.getLogger(NewGameDialogue.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (InstantiationException ex) {
      java.util.logging.Logger.getLogger(NewGameDialogue.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      java.util.logging.Logger.getLogger(NewGameDialogue.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (javax.swing.UnsupportedLookAndFeelException ex) {
      java.util.logging.Logger.getLogger(NewGameDialogue.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }

    NewGameDialogue instance = new NewGameDialogue();
    instance.showDialog();

  }

}