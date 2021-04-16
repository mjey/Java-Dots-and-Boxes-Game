package edu.vt.cs5044.DotsAndBoxes;

import edu.vt.cs5044.DABGameTest.DABGameTest;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.event.MouseInputListener;

public class Field extends JComponent implements MouseInputListener {

  int rows, cols;
  private int boxL;
  private Point origin = new Point();
  Font font;
  private int dotSize;
  private int lineThickness;
  public CounterLabel X;
  public CounterLabel Y;
  public Box[][] boxses;
  public Box hoverdBox;
  public boolean userMove;
  Calculation intermediate;
  MaintainChain hard;
  G_For_Beginner beginner;
  String name;
  public int level;
  private Image img;
  int xheight, yheight;
  DABGameTest test;

  public Field() {
    X = new CounterLabel("X position");
    Y = new CounterLabel("Y position");
    name = new String("H");
    this.level = 1;
  }

  public Field(int row, int col) {
    super();
    try {
      img = ImageIO.read(new File("background.jpg"));
    } catch (IOException ex) {
      JOptionPane.showConfirmDialog(this, "Image Not found");
    }
    X = new CounterLabel("X position");
    Y = new CounterLabel("Y position");
    this.rows = row;
    this.cols = col;
    font = new Font("Kalpurush", Font.PLAIN, 10);
    this.boxses = new Box[row + 3][col + 3];

    name = new String("H");
    for (int i = 0; i < row + 2; i++) {
      for (int j = 0; j < col + 2; j++) {
        boxses[i][j] = new Box(i, j);
      }
    }
    this.addMouseListener(this);
    this.addMouseMotionListener(this);
    userMove = true;
    intermediate = new Calculation(this);
    hard = new MaintainChain(this);
    beginner = new G_For_Beginner(this);
  }
  public Field(int row, int col, DABGameTest test) {
    this(row, col);
    this.test = test;
  }

  @Override
  public boolean isOpaque() {
    return true;
  }

  public void repaintLine(int orientation, int col, int row) {
    int halfThick = this.lineThickness / 2;
    int x = this.origin.x + col * boxL;
    int y = this.origin.y + row * boxL;
    if (orientation == 0) {
      this.repaint(x, y - halfThick,
        boxL, this.lineThickness);
    } else {
      this.repaint(x - halfThick, y, this.lineThickness, boxL);
    }
  }

  public void repaintBox(int col, int row) {
    int halfThick = this.lineThickness / 2;
    int x = this.origin.x + col * this.boxL;
    int y = this.origin.y + row * this.boxL;
    this.repaint(x - halfThick, y - halfThick, this.boxL +
      this.lineThickness, this.boxL + this.lineThickness);
  }

  private void newSize(int newWidth, int newHeight) {
    xheight = newWidth;
    yheight = newHeight;
    double sideX = (newWidth - 2.0) / (this.cols + 0.4);
    double sideY = (newHeight - 2.0) / (this.rows + 0.4);
    double side = sideX;
    if (sideY < sideX) {
      side = sideY;
    }
    int centerX = newWidth / 2;
    int centerY = newHeight / 2;
    this.boxL = (int) side;
    this.font = font = new Font("Kalpurush", Font.PLAIN, this.boxL);
    if (this.boxL < 1) {
      this.boxL = 1;
    }
    int fieldWidth = this.boxL * this.cols;
    int fieldHeight = this.boxL * this.rows;
    this.origin.x = (int) centerX - (fieldWidth / 2);
    this.origin.y = (int) centerY - (fieldHeight / 2);
    this.dotSize = (int)(side / 10);
    if (this.dotSize == 0) {
      this.dotSize = 1;
    }
    this.lineThickness = (int)(side / 20);
    if (this.lineThickness == 0) {
      this.lineThickness = 1;
    }
    this.repaint();
  }

  @Override
  public void setSize(Dimension d) {
    super.setSize(d);
    this.newSize(d.width, d.height);
  }

  @Override
  public void setSize(int width, int height) {
    super.setSize(width, height);
    this.newSize(width, height);
  }

  @Override
  public void setBounds(int x, int y, int width, int height) {
    super.setBounds(x, y, width, height);
    this.newSize(width, height);
  }

  public void printSide(Graphics g, int x, int y, int side) {
    Box box = boxses[x][y];
    if (side == Box.DOWN) {
      g.fillRect(box.y * boxL + origin.x - boxL, origin.y + (box.x + 1) * boxL - boxL, boxL, lineThickness);
    } else if (side == Box.UP) {
      g.fillRect(box.y * boxL + origin.x - boxL, origin.y + box.x * boxL - boxL, boxL, lineThickness);
    }
    if (side == Box.LEFT) {
      g.fillRect(origin.x + box.y * boxL - boxL, box.x * boxL + origin.y - boxL, lineThickness, boxL);
    }
    if (side == Box.RIGHT) {
      g.fillRect(origin.x + (box.y + 1) * boxL - boxL, (box.x) * boxL + origin.y - boxL, lineThickness, boxL);
    }
  }

  @Override
  public void paint(Graphics g) {

    int radius = dotSize;
    if (radius < 0) {
      radius = 1;
    }
    font = new Font("kalpurush", Font.PLAIN, boxL);
    g.setFont(font);
    Rectangle bounds = this.getBounds();
    g.drawImage(img, 0, 0, xheight, yheight, this);
    g.setColor(Color.LIGHT_GRAY);
    if (hoverdBox != null) {
      g.setColor(Color.GREEN);
      printSide(g, hoverdBox.x, hoverdBox.y, hoverdBox.hoveredSide);
    }
    g.setColor(Color.black);
    for (int i = 1; i <= rows + 1; i++) {
      for (int j = 1; j <= cols + 1; j++) {
        g.fillOval(origin.x + j * boxL - boxL, origin.y + i * boxL - boxL, radius, radius);
      }
    }
    for (int i = 1; i <= rows; i++) {
      for (int j = 1; j <= cols; j++) {
        g.setColor(Color.black);
        for (int k = 1; k < 5; k++) {
          if (boxses[i][j].isSet(k)) {
            printSide(g, i, j, k);
          }
        }
        if (boxses[i][j].degree == 0) {
          g.setColor(Color.cyan);
          if (boxses[i][j].isHumanCompleted) {
            g.setColor(Color.darkGray);
          }
          g.fillRect(origin.x + j * boxL - boxL + lineThickness, origin.y + i * boxL - boxL + lineThickness, boxL - lineThickness, boxL - lineThickness);
        }
      }
    }

  }

  @Override
  public void mouseClicked(MouseEvent e) {

  }

  @Override
  public void mousePressed(MouseEvent e) {}

  @Override
  public void mouseReleased(MouseEvent e) {
    int x, y;
    x = e.getX() + boxL;
    y = e.getY() + boxL;
    int[] n = getNearestLine(x, y);
    Box box = new Box(n[0], n[1]);
    box.hoveredSide = n[2];

    System.out.println("this.level=== " + level);

    if (this.level == 1) {
      System.out.println("Easy");
      beginner.computerMove(box);
    } else if (this.level == 2) {
      System.out.println("Inter");
      intermediate.computerMove(box);

    } else {
      System.out.println("-->Hard");
      hard.computerMove(box);
    }

    this.repaint();

    if (X.getPoint() + Y.getPoint() == rows * cols) {
      if (X.getPoint() > Y.getPoint()) {
        JOptionPane.showMessageDialog(this, "Human Win");
      } else if (Y.getPoint() > X.getPoint()) {
        JOptionPane.showMessageDialog(this, "Computer Win");
      } else {
        JOptionPane.showMessageDialog(this, "Game Draw");
      }
      test.newGame(rows, cols);
    }
  }

  @Override
  public void mouseEntered(MouseEvent e) {}

  @Override
  public void mouseExited(MouseEvent e) {}

  @Override
  public void mouseDragged(MouseEvent e) {}

  @Override
  public void mouseMoved(MouseEvent e) {
    int x, y;
    x = e.getX() + boxL;
    y = e.getY() + boxL;
    int[] n = getNearestLine(x, y);
    hoverdBox = new Box(n[0], n[1]);
    hoverdBox.hoveredSide = n[2];
    this.repaint();
  }

  private int[] getNearestLine(int x, int y) {
    x -= origin.x;
    y -= origin.y;
    int row = y / boxL;
    int col = x / boxL;

    if (col <= 1) {
      col = 1;
    }
    if (col >= this.cols) {
      col = this.cols;
    }
    if (row <= 1) {
      row = 1;
    }
    if (row >= this.rows) {
      row = this.rows;
    }

    Box nearestBox = this.boxses[row][col];

    x -= boxL * col;
    y -= boxL * row;
    int[] n = new int[3];

    n[0] = row;
    n[1] = col;
    n[2] = nearestBox.getNearestLine(x, y, boxL);
    return n;
  }

  public void showLineStatus(int x, int y, int side) {
    Box bx = this.boxses[x][y];
    if (bx.isSet(side)) {

    }
  }
}