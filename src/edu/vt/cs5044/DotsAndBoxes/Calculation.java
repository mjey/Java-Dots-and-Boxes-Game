package edu.vt.cs5044.DotsAndBoxes;

import java.util.LinkedList;
import java.util.Queue;
import javax.swing.JOptionPane;

public class Calculation {

  final int UP = 1, DOWN = 3, LEFT = 4, RIGHT = 2;
  static int calc;
  Field field;
  CounterLabel X, Y;
  public Box[][] boxes;
  public Pair < Integer, Integer > pair;
  public int n, m;

  final int oo = (1 << 28);
  boolean flag[][];
  public Calculation() {
    calc = 0;
  }
  public Calculation(int n, int m) {
    this();
    this.n = n;
    this.m = m;
    boxes = new Box[n + 3][m + 3];

    for (int i = 0; i < n + 1; i++) {
      for (int j = 0; j < m + 1; j++) {
        boxes[i][j] = new Box(i, j);
      }
    }
  }

  public Calculation(Field field) {
    this();
    this.field = field;
    this.n = field.rows;
    this.m = field.cols;
    boxes = field.boxses;
    this.X = field.X;
    this.Y = field.Y;
  }

  public Box moveFromChain() {
    int i, j, minLen = (1 << 20), len, x, y;
    Box minBox = new Box();

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
    int side = minBox.getEmpty();
    System.out.println("minBox updated len = " + minLen);
    updateBox(minBox, side);
    return minBox;
  }

  public void cutString(Box box, int side) {
    Pair < Box, Box > pairs;
    pairs = getAdjacentBox(box.x, box.y, side);
    Box box0, box1;
    box0 = pairs.getFirst();
    box1 = pairs.getSecond();
    box0.setLine(side);
    box1.setLineReverse(side);
    boxes[box0.x][box0.y] = box0.clone();
    boxes[box1.x][box1.y] = box1.clone();
    return;
  }

  public void setString(Box box, int side) {
    Pair < Box, Box > pairs;
    pairs = getAdjacentBox(box.x, box.y, side);
    Box box0, box1;
    box0 = pairs.getFirst();
    box1 = pairs.getSecond();
    box0.resetLine(side);
    box1.resetLineReverse(side);
    boxes[box0.x][box0.y] = box0.clone();
    boxes[box1.x][box1.y] = box1.clone();
    return;
  }

  public void eatAll(Box root) {
    System.out.println("In eatAll");
    root.toString1();
    int side;
    Pair < Box, Box > pair;
    Box box0, box1;
    if (root.degree == 1) {
      root.isComputerCompleted = true;
      root.name = "C";
      side = root.getEmpty();
      System.out.println("side = " + side);
      updateBox(root, side);
      pair = getAdjacentBox(root.x, root.y, side);
      box0 = pair.getFirst();
      box1 = pair.getSecond();
      if (box1.degree == 1)
        eatAll(box1);
    }
  }

  public Box eatAll(Box root, int level, int len) {

    int side, count = 0;
    Pair < Box, Box > pair;
    Box box0, box1, box = boxes[0][0];
    if (level == 0 && len >= 2) {
      side = root.getEmpty();
      System.out.println("tricks " + side + " " + root.x + " " + root.y);
      cutString(root, side);
      pair = getAdjacentBox(root.x, root.y, side);
      box0 = pair.getSecond();
      box = eatAll(box0, level - 1, len);
      System.out.println("root human or comp " + root.isComputerCompleted);

      boxes[box0.x][box0.y].isComputerCompleted = false;
      field.Y.decrease();

      System.out.println("child " + boxes[box0.x][box0.y].isComputerCompleted);
      setString(root, side);
      boxes[box0.x][box0.y].toString1();
      System.out.println("root info ");
      root.toString1();

      return boxes[box0.x][box0.y];
    }
    if (level == 0) return root;
    if (root.degree == 1) {
      root.isComputerCompleted = true;
      root.name = "C";
      side = root.getEmpty();
      System.out.println("side = " + side);
      pair = updateBox(root, side);
      box0 = pair.getFirst();
      box1 = pair.getSecond();

      if (box1.degree == 1) {
        box = eatAll(box1, level - 1, len);
      }
    }
    return box;
  }

  public int eatCount(Box root) {
    System.out.println("In eatCount");
    root.toString1();
    int count;
    int side = root.getEmpty();
    cutString(root, side);
    Pair pair;
    pair = getAdjacentBox(root.x, root.y, side);
    Box box = (Box) pair.getSecond();
    System.out.println("eatConnected child");
    box.toString1();
    count = eatConnected(box);
    setString(root, side);
    return count + 1;
  }

  public int boxMove(Box box) {

    int a, b, count = 0;
    a = box.x;
    b = box.y;

    if (box.up == 0)
      if (a > 0 && boxes[a - 1][b].degree > 2) {
        System.out.println("chkMove up " + (a - 1) + " " + b + " " + boxes[a - 1][b].degree);
        count++;
      }
    if (box.down == 0)
      if (a <= n && boxes[a + 1][b].degree > 2) {
        System.out.println("chkMove down " + (a + 1) + " " + b + " " + boxes[a + 1][b].degree);
        count++;
      }
    if (box.left == 0) {
      if (b > 0 && boxes[a][b - 1].degree > 2) {
        System.out.println("chkMove left " + a + " " + b + " " + boxes[a][b - 1].degree);
        count++;
      }
    }
    if (box.right == 0) {
      if (b <= m && boxes[a][b + 1].degree > 2) {
        System.out.println("chkMove right " + (b + 1) + " " + boxes[a][b + 1].degree);
        count++;
      }
    }
    return count;
  }

  public int availMove() {
    int count = 0, i, j;
    for (i = 1; i <= n; i++)
      for (j = 1; j <= m; j++)
        if (boxes[i][j].degree >= 3) count += boxMove(boxes[i][j]);
    return count;
  }

  public int eatConnected(Box root) {
    if (root.x > n || root.y > m || root.x <= 0 || root.y <= 0) return 0;
    int count = 0;
    Box bx;
    System.out.println("in eatConnected " + root.x + " " + root.y);
    Queue < Box > Q = new LinkedList < > ();
    if (root.degree == 1)
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
      int x = bx.x, y = bx.y;
      count++;
      if (bx.up == 0) {
        if (x - 1 > 0 && flag[x - 1][y] == false && boxes[x - 1][y].degree <= 2 && boxes[x - 1][y].degree > 0) {
          Q.add(getBox(x - 1, y));
          flag[x - 1][y] = true;
        }
      }
      if (bx.down == 0) {
        if (x + 1 <= n && flag[x + 1][y] == false && boxes[x + 1][y].degree <= 2 && boxes[x + 1][y].degree > 0) {
          Q.add(getBox(x + 1, y));
          flag[x + 1][y] = true;
        }
      }
      if (bx.left == 0) {
        if (y - 1 > 0 && flag[x][y - 1] == false && boxes[x][y - 1].degree <= 2 && boxes[x][y - 1].degree > 0) {
          Q.add(getBox(x, y - 1));
          flag[x][y - 1] = true;
        }
      }
      if (bx.right == 0) {
        if (y + 1 <= m && flag[x][y + 1] == false && boxes[x][y + 1].degree <= 2 && boxes[x][y + 1].degree > 0) {
          Q.add(getBox(x, y + 1));
          flag[x][y + 1] = true;
        }
      }
    }
    return count;
  }

  public void single(Box box) {
    int i, j, cnt;
    Pair pairs = getAdjacentBox(box.x, box.y, box.getEmpty());
    Box box1, box2;
    box1 = (Box) pairs.getFirst();
    box2 = (Box) pairs.getSecond();
    for (i = 1; i <= n; i++)
      for (j = 1; j <= m; j++)
        if (boxes[i][j].degree == 1 && ((box1.x != i && box1.y != j) || (box2.x != i && box2.y != j))) {
          cnt = eatCount(boxes[i][j]);
          eatAll(boxes[i][j], cnt, cnt);
        }
    return;
  }

  public Box getBox(int x, int y) {
    return boxes[x][y];
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

    box0.toString1();
    box1.toString1();
    System.out.println("updated");
    if (box0.x > 0 && box0.x <= n && box0.y > 0 && box0.y <= m) {
      boxes[box0.x][box0.y] = box0.clone();
      if (box0.degree == 0) {
        box0.chainLength = oo;
        box0.isComputerCompleted = true;
        box0.name = "C";
        field.Y.increment();
      }
    }

    if (box1.x > 0 && box1.x <= n && box1.y > 0 && box1.y <= m) {
      boxes[box1.x][box1.y] = box1.clone();
      if (box1.degree == 0) {
        box1.chainLength = oo;
        box1.isComputerCompleted = true;
        box1.name = "C";
        field.Y.increment();
      }
    }
    return new Pair(box0, box1);
  }

  public int compMove(Box box) {
    box.toString1();
    int a, b;
    System.out.println("In Comp move");
    if (box.x == -1 && box.y == -1) {
      System.out.println("compMove to cmpChain");
      computeChain();
    }
    int ll = 100;
    while (ll--> 0) {
      int tem = box.getEmpty();
      System.out.println("side " + tem + " asssigned to ->");

      if (tem == 1) {
        a = box.x;
        b = box.y;
        if (a > 0 && boxes[a - 1][b].degree > 2 && boxes[a - 1][b].down == 0) {
          updateBox(box, UP);

          return 1;
        }
      }
      if (tem == 3) {
        a = box.x;
        b = box.y;
        if (a <= n && boxes[a + 1][b].degree > 2 && boxes[a + 1][b].up == 0) {
          updateBox(box, DOWN);
          return 1;
        }
      }
      if (tem == 4) {
        a = box.x;
        b = box.y;
        if (b > 0 && boxes[a][b - 1].degree > 2 && boxes[a][b - 1].right == 0) {
          updateBox(box, LEFT);
          return 1;
        }
      }
      if (tem == 2) {
        a = box.x;
        b = box.y;
        if (b <= m && boxes[a][b + 1].degree > 2 && boxes[a][b + 1].left == 0) {
          updateBox(box, RIGHT);
          return 1;
        }
      }
    }
    System.out.println("not assigned");
    return 0;
  }

  public Box NextBest(Box root) {
    Queue < Box > Q = new LinkedList < > ();
    Q.add(root);
    int move;
    flag = new boolean[n + 4][m + 4];
    for (int i = 0; i < n + 4; i++) {
      for (int j = 0; j < m + 4; j++) {
        flag[i][j] = false;
      }
    }
    while (!Q.isEmpty()) {
      Box bx = Q.poll();
      if (flag[bx.x][bx.y]) continue;
      if (bx.degree > 2) {
        move = compMove(bx);
        System.out.println("Return form queue Next Best");
        if (move == 1)
          return bx;
      } else {
        int x = bx.x, y = bx.y;
        flag[x][y] = true;
        if (bx.up == 0) {
          if (x - 1 > 0 && flag[x - 1][y] == false) {
            Q.add(getBox(x - 1, y));
          }
        }
        if (bx.down == 0) {
          if (x + 1 <= n && flag[x + 1][y] == false) {
            Q.add(getBox(x + 1, y));
          }
        }
        if (bx.left == 0) {
          if (y - 1 > 0 && flag[x][y - 1] == false) {
            Q.add(getBox(x, y - 1));
          }
        }
        if (bx.right == 0) {
          if (y + 1 <= m && flag[x][y + 1] == false) {
            Q.add(getBox(x, y + 1));
          }
        }
      }
    }
    for (int i = 1; i < n + 1; i++) {
      for (int j = 1; j < m + 1; j++) {
        if (boxes[i][j].degree > 2) {
          move = compMove(boxes[i][j]);
          System.out.println("NextBest  return from loop");
          boxes[i][j].toString1();
          if (move == 1)
            return boxes[i][j];
        }
      }
    }
    System.out.println("Call compute chain");

    Box bx = computeChain();
    if (bx == null) {
      JOptionPane.showMessageDialog(field, "opps nothing found");
      return null;
    }
    updateBox(bx, bx.getEmpty());
    return bx;
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

  public Pair getAdjacentBox(int x, int y, int side) {

    Box box0 = new Box(), box1 = boxes[0][0];
    if (side == 1) {
      box0 = boxes[x][y];
      if (x - 1 > 0)
        box1 = boxes[x - 1][y];
    } else if (side == 2) {
      box0 = boxes[x][y];
      if (y + 1 <= m)
        box1 = boxes[x][y + 1];
    } else if (side == 3) {
      box0 = boxes[x][y];
      if (x + 1 <= n)
        box1 = boxes[x + 1][y];
    } else if (side == 4) {
      box0 = boxes[x][y];
      if (y - 1 > 0)
        box1 = boxes[x][y - 1];
    }

    return new Pair(box0, box1);
  }

  public void status() {
    System.out.println("printing box status");
    int i, j;
    for (i = 0; i <= n; i++)
      for (j = 0; j <= m; j++) boxes[i][j].toString1();
    return;
  }

  public int checkConnection(Pair < Box, Box > pair) {
    Queue < Box > Q = new LinkedList < > ();
    Box bx = null, box1, box2;
    box1 = pair.getFirst();
    box2 = pair.getSecond();

    boolean flag[][] = new boolean[n + 5][m + 5];
    int i, j;
    for (i = 0; i <= n; i++)
      for (j = 0; j <= m; j++)
        flag[i][j] = false;

    Q.add(box1);
    while (!Q.isEmpty()) {
      bx = Q.poll();
      int x = bx.x, y = bx.y;
      flag[x][y] = true;
      if (box2.x == x && box2.y == y) return 1;

      if (bx.up == 0) {
        if (x - 1 > 0 && boxes[x - 1][y].degree <= 2 && boxes[x - 1][y].degree > 0 && flag[x - 1][y] == false) {
          Q.add(getBox(x - 1, y));
        }
      }
      if (bx.down == 0) {
        if (x + 1 <= n && boxes[x + 1][y].degree <= 2 && boxes[x + 1][y].degree > 0 && flag[x + 1][y] == false) {
          Q.add(getBox(x + 1, y));
        }
      }
      if (bx.left == 0) {
        if (y - 1 > 0 && boxes[x][y - 1].degree <= 2 && boxes[x][y - 1].degree > 0 && flag[x][y - 1] == false) {
          Q.add(getBox(x, y - 1));
        }
      }
      if (bx.right == 0) {
        if (y + 1 <= m && boxes[x][y + 1].degree <= 2 && boxes[x][y + 1].degree > 0 && flag[x][y + 1] == false) {
          Q.add(getBox(x, y + 1));
        }
      }
    }
    return 0;
  }

  public boolean computerMove(Box box) {
    int x, y, side, i, j, k;
    x = box.x;
    y = box.y;
    side = box.hoveredSide;
    Box bx0, bx1, next;
    Pair pair;
    next = boxes[0][0]; {
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
        status();
        int cnt1 = 0, cnt2 = 0;
        if (bx0.degree == 1) cnt1 = eatCount(bx0);
        if (bx1.degree == 1) cnt2 = eatCount(bx1);
        k = checkConnection(pair);
        System.out.println("counts " + cnt1 + " " + cnt2 + " points " + field.X.getPoint() + " " + field.Y.getPoint() + " conn status " + k);
        if (field.X.getPoint() + cnt1 + cnt2 + field.Y.getPoint() >= m * n) {

          eatAll(bx0);
          eatAll(bx1);
          System.out.println("All boxes filled");
          return false;
        }
        if (k == 1 && bx0.degree == 1) {
          System.out.println("connected");
          eatAll(bx0);
        } else if ((cnt1 > 0 || cnt2 > 0) && availMove() == 0) {
          System.out.println("in else");
          if (bx0.degree == 1) {
            if (cnt1 >= 2) {
              next = eatAll(bx0, cnt1 - 2, cnt1);
              System.out.println("count 1");
            } else {
              eatAll(bx0, cnt1, cnt1);
              System.out.println("count 1 all");
            }
          }
          if (bx1.degree == 1) {
            if (cnt1 >= 2 || cnt2 < 2) {
              eatAll(bx1, cnt2, cnt2);
              System.out.println("count 2 all");
            } else {
              next = eatAll(bx1, cnt2 - 2, cnt2);
              System.out.println("count 2");
            }
          }
          boxes[bx0.x][bx0.y] = bx0.clone();
          boxes[bx1.x][bx1.y] = bx1.clone();
          if ((cnt1 >= 2 || cnt2 >= 2) && k != 1) {
            single(next);
            System.out.println("unfilled box");
            next.toString1();
            return false;
          }
        } else {
          eatAll(bx0);
          eatAll(bx1);
        }
        boxes[bx0.x][bx0.y] = bx0.clone();
        boxes[bx1.x][bx1.y] = bx1.clone();
        single(next);
        if (calc == 0) {
          System.out.println("call nextBest");
          next = NextBest(bx0);
        } else {
          computeChain();
          System.out.println("move from chain");
          next = moveFromChain();
        }

        System.out.print("Computer");
        next.toString1();
        printBox();
      }
    }
    return false;
  }

  public int connected(Box root) {
    if (root.degree == 0) return oo;
    if (root.degree > 2) return oo;
    root.parentx = root.x;
    root.parenty = root.y;
    Queue < Box > Q = new LinkedList < > ();
    int count = 0;
    Box bx = root;
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
    root.chainLength = count;
    boxes[root.x][root.y].chainLength = count;
    return count;
  }

  private Box computeChain() {
    int i, j, k, len = 999999, minLen = 99999999, x, y;
    calc = 1;
    Box minBox = null, box;
    Queue < Box > Q = new LinkedList < > ();
    for (i = 1; i < n + 1; i++) {
      for (j = 1; j < m + 1; j++) {

        len = connected(boxes[i][j]);
        if (len < minLen) {
          x = boxes[i][j].parentx;
          y = boxes[i][j].parenty;
          minBox = boxes[x][y];
          minLen = len;
        }
        if (boxes[i][j].degree == 1) {
          Q.add(boxes[i][j]);
        }

      }
    }

    while (!Q.isEmpty()) {
      box = Q.poll();
      eatAll(box);
    }
    System.out.println("while exit");
    return minBox;
  }

  public void assign(Box bx) {
    int side;
    side = bx.getEmpty();
    Box box1, box2;
    Pair < Box, Box > mn = getAdjacentBox(bx.x, bx.y, side);
    box1 = mn.getFirst(); //setLine(side);
    box2 = mn.getSecond(); //setLineReverse(side);
    boxes[box1.x][box1.y].setLine(side);
    boxes[box2.x][box2.y].setLineReverse(side);
    return;
  }

}