package edu.vt.cs5044.DotsAndBoxes;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class G_For_Beginner {

  final int UP = 1;
  final int DOWN = 3;
  final int LEFT = 4;
  final int RIGHT = 2;
  public Box[][] boxes;
  Field field;
  CounterLabel X, Y;
  final int oo = (1 << 28);
  boolean flag[][];
  int n, m;

  public G_For_Beginner() {
    boxes = new Box[n + 3][m + 3];
  }

  public G_For_Beginner(int n, int m) {
    this();
    this.n = n;
    this.m = m;
    boxes = new Box[n + 3][m + 3];
    int i, j;
    for (i = 0; i < n + 1; i++) {
      for (j = 0; j < m + 1; j++) {
        boxes[i][j] = new Box(i, j);
      }
    }

  }

  public G_For_Beginner(Field field) {
    this();
    this.field = field;
    this.n = field.rows;
    this.m = field.cols;
    boxes = field.boxses;
    CounterLabel X = field.X, Y = field.Y;
  }

  public Pair getAdjacentBox(int x, int y, int side) {

    Box box0 = new Box(), box1 = new Box();
    if (side == 1) {
      box0 = boxes[x][y];
      box1 = boxes[x - 1][y];
    } else if (side == 2) {
      box0 = boxes[x][y];
      box1 = boxes[x][y + 1];
    } else if (side == 3) {
      box0 = boxes[x][y];
      box1 = boxes[x + 1][y];
    } else if (side == 4) {
      box0 = boxes[x][y];
      box1 = boxes[x][y - 1];
    }

    return new Pair(box0, box1);
  }

  public Pair updateBox(Box box, int side) {
    Pair < Box, Box > pair;
    pair = getAdjacentBox(box.x, box.y, side);
    System.out.println("update");
    Box box0, box1;
    box0 = pair.getFirst();
    box1 = pair.getSecond();
    box0.setLine(side);
    box1.setLineReverse(side);
    if (box0.degree == 0) {
      box0.chainLength = oo;
      box0.isComputerCompleted = true;
      box0.name = "C";
      field.Y.increment();
    }
    if (box1.degree == 0) {
      box1.chainLength = oo;
      box1.isComputerCompleted = true;
      box1.name = "C";
      field.Y.increment();
    }
    box0.toString1();
    box1.toString1();
    System.out.println("updated");
    if (box0.x > 0 && box0.x <= n && box0.y > 0 && box0.y <= m) {
      boxes[box0.x][box0.y] = box0.clone();
    }
    if (box1.x > 0 && box1.x <= n && box1.y > 0 && box1.y <= m) {
      boxes[box1.x][box1.y] = box1.clone();
    }
    return new Pair(box0, box1);
  }

  public int eatAll(Box root) {
    System.out.println("In eatAll");
    root.toString1();
    int side, count = 0;
    Pair < Box, Box > pair;
    Box box0, box1;
    if (root.degree == 1) {
      root.isComputerCompleted = true;
      root.name = "C";
      side = root.getEmpty();
      System.out.println("side = " + side);
      pair = updateBox(root, side);
      box0 = pair.getFirst();
      box1 = pair.getSecond();
      if (box0.degree == 1) {
        count += eatAll(box0);
      }
      if (box1.degree == 1) {
        count += eatAll(box1);
      }
    }
    return count;
  }

  public boolean computerMove(Box box) {
    int x, y, side, i, j, k;
    x = box.x;
    y = box.y;
    side = box.hoveredSide;
    Box bx0, bx1, next;
    Pair pair; {
      {
        System.out.println("Human Move");
        System.out.println("Input x, y, and hoverside-> " + x + " " + y + " " + side);
        if (boxes[x][y].isSet(side)) {
          System.out.println("Invalid move");
          boxes[x][y].toString1();
          return true;
        }
        pair = getAdjacentBox(x, y, side);
        bx0 = (Box) pair.getFirst();
        bx1 = (Box) pair.getSecond();

        bx0.setLine(side);
        bx1.setLineReverse(side);
        int cnt = 0;
        if (bx0.degree == 0) {
          cnt++;
          bx0.chainLength = oo;
          bx0.isHumanCompleted = true;
          bx0.name = field.name;
          field.X.increment();
          boxes[bx0.x][bx0.y] = bx0.clone();
        }
        if (bx1.degree == 0) {
          cnt++;
          bx1.chainLength = oo;
          bx1.isHumanCompleted = true;
          bx1.name = field.name;
          field.X.increment();
          boxes[bx1.x][bx1.y] = bx1.clone();
        }
        if (cnt > 0) {
          System.out.println("User get +" + cnt + " point");
          printBox();
          return true;
        }
        if (bx0.degree == 1) {
          eatAll(bx0);
        }
        if (bx1.degree == 1) {
          eatAll(bx1);
        }
        boxes[bx0.x][bx0.y] = bx0.clone();
        boxes[bx1.x][bx1.y] = bx1.clone();
        Move();

        System.out.print("Computer");
        printBox();
      }
    }
    return false;
  }

  public int checkMove(Box box) {

    int a, b;
    a = box.x;
    b = box.y;

    if (box.up == 0) {
      if (a > 0 && boxes[a - 1][b].degree > 2) {
        System.out.println("chkMove up " + (a - 1) + " " + b + " " + boxes[a - 1][b].degree);
        return UP;
      }
    }
    if (box.down == 0) {
      if (a <= n && boxes[a + 1][b].degree > 2) {
        System.out.println("chkMove down " + (a + 1) + " " + b + " " + boxes[a + 1][b].degree);
        return DOWN;
      }
    }
    if (box.left == 0) {
      if (b > 0 && boxes[a][b - 1].degree > 2) {
        System.out.println("chkMove left " + a + " " + b + " " + boxes[a][b - 1].degree);
        return LEFT;
      }
    }
    if (box.right == 0) {
      if (b <= m && boxes[a][b + 1].degree > 2) {
        System.out.println("chkMove right " + (b + 1) + " " + boxes[a][b + 1].degree);
        return RIGHT;
      }
    }
    return -1;
  }

  public int connected(Box root) {
    if (root.degree == 0) {
      return oo;
    }
    if (root.degree > 2) {
      return oo;
    }
    root.parentx = root.x;
    root.parenty = root.y;
    Queue < Box > Q = new LinkedList < > ();
    int count = 0;
    Box bx = root;
    System.out.println("this is root " + root.x + " " + root.y);
    Q.add(root);
    int i, j;
    flag = new boolean[n + 4][m + 4];
    for (i = 0; i < n + 4; i++) {
      for (j = 0; j < m + 4; j++) {
        flag[i][j] = false;
      }
    }
    flag[root.x][root.y] = true;
    while (!Q.isEmpty()) {
      bx = Q.poll();

      System.out.println("child " + bx.x + " " + bx.y);
      bx.parentx = root.x;
      bx.parenty = root.y;
      int x = bx.x, y = bx.y;
      count++;
      if (bx.up == 0) {
        if (x - 1 > 0 && flag[x - 1][y] == false && boxes[x - 1][y].degree == 2) {
          Q.add(getBox(x - 1, y));
          flag[x - 1][y] = true;
        }
      }
      if (bx.down == 0) {
        if (x + 1 <= n && flag[x + 1][y] == false && boxes[x + 1][y].degree == 2) {
          Q.add(getBox(x + 1, y));
          flag[x + 1][y] = true;
        }
      }
      if (bx.left == 0) {
        if (y - 1 > 0 && flag[x][y - 1] == false && boxes[x][y - 1].degree == 2) {
          Q.add(getBox(x, y - 1));
          flag[x][y - 1] = true;
        }
      }
      if (bx.right == 0) {
        if (y + 1 <= m && flag[x][y + 1] == false && boxes[x][y + 1].degree == 2) {
          Q.add(getBox(x, y + 1));
          flag[x][y + 1] = true;
        }
      }
    }

    return count;
  }

  public Box getBox(int x, int y) {
    return boxes[x][y];
  }

  public Box moveChain() {
    int i, j, minLen = (1 << 20), len, x, y;
    Box minBox = null;

    for (i = 1; i < n + 1; i++) {
      for (j = 1; j < m + 1; j++) {
        len = connected(boxes[i][j]);
        if (len < minLen) {
          x = boxes[i][j].parentx;
          y = boxes[i][j].parenty;
          minBox = boxes[x][y];
          minLen = len;
        }
      }
    }

    if (minBox != null) {
      int side = minBox.getEmpty();
      System.out.println("minBox moveFromChain " + minLen + " " + side);
      minBox.toString1();
      updateBox(minBox, side);
      System.out.println("minBox moveFromChain " + minLen + " " + side);
      minBox.toString1();
    } else {
      return new Box(0, 0);
    }
    return minBox;
  }

  public Box Move() {
    int i, j, side, test;
    test = (1 << 16);
    Random ri = new Random();
    Random rj = new Random();
    while (test > 0) {
      test--;
      i = ri.nextInt(n) + 1;
      j = rj.nextInt(m) + 1;
      if (boxes[i][j].degree > 2) {
        side = checkMove(boxes[i][j]);
        System.out.println("pos " + i + " " + j + " " + boxes[i][j].degree + " side " + side);
        if (side > 0) {
          System.out.println("before update" + i + " " + j + " " + side);
          updateBox(boxes[i][j], side);
          return boxes[i][j];
        }
      }
    }
    for (i = 1; i <= n; i++) {
      for (j = 1; j <= m; j++) {
        if (boxes[i][j].degree > 2) {
          side = checkMove(boxes[i][j]);
          System.out.println("pos " + i + " " + j + " " + boxes[i][j].degree + " side " + side);
          if (side > 0) {
            System.out.println("before update" + i + " " + j + " " + side);
            updateBox(boxes[i][j], side);
            return boxes[i][j];
          }
        }
      }
    }

    return moveChain();
  }

  void printBox() {
    System.out.println("______________________________________________________________________");
    for (int i = 0; i <= n + 1; i++) {
      System.out.print(i + " ");
      for (int j = 0; j <= m + 1; j++) {
        if (boxes[i][j].up != 0) {
          System.out.print("---");
        } else {
          System.out.print("   ");
        }
      }
      System.out.println("");
      System.out.print(i + " ");
      for (int j = 0; j <= m + 1; j++) {
        if (boxes[i][j].left != 0) {
          System.out.print("|  ");
        } else {
          System.out.print("   ");
        }
      }
      System.out.println("");
    }
    System.out.println("______________________________________________________________________");
  }

}