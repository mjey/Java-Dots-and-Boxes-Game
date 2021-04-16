package edu.vt.cs5044.DotsAndBoxes;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class MaintainChain {

  public Box[][] boxes;
  public int degrees[];
  public int chains, calc;
  int n, m;
  final int UP = 1;
  final int DOWN = 3;
  final int LEFT = 4;
  final int RIGHT = 2;
  Field field;
  CounterLabel X, Y;
  final int oo = (1 << 28);
  boolean flag[][];
  Box lastBox;
  int lastSide, which;

  public MaintainChain() {
    chains = 0;
  }

  public MaintainChain(int n, int m) {
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

    chains = calc = 0;

  }

  public MaintainChain(Field field) {
    this();
    this.field = field;
    this.n = field.rows;
    this.m = field.cols;
    boxes = field.boxses;
    this.X = field.X;
    this.Y = field.Y;
    chains = 0;
    degrees = new int[5];
    int i;
    for (i = 0; i < 5; i++)
      degrees[i] = 0;
    degrees[4] = n * m;
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

  public int eatConnected(Box root) {
    if (root.x > n || root.y > m || root.x <= 0 || root.y <= 0) return 0;
    int count = 0;
    Box bx;
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

  public int eatCount(Box root) {
    System.out.println("In eatCount");
    root.toString1();
    int count;
    int side = root.getEmpty();
    cutString(root, side);
    Pair pair;
    pair = getAdjacentBox(root.x, root.y, side);
    Box box = (Box) pair.getSecond();
    count = eatConnected(box);
    setString(root, side);
    return count + 1;
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

  public int chainNum() {
    int i, j, count = 0, chain = 0;
    Box bx;
    Queue < Box > Q = new LinkedList < > ();
    boolean flag[][] = new boolean[n + 5][m + 5];
    for (i = 0; i <= n; i++)
      for (j = 0; j <= m; j++) flag[i][j] = false;

    for (i = 1; i <= n; i++)
      for (j = 1; j <= m; j++) {
        if (flag[i][j] == true) continue;
        flag[i][j] = true;
        if (boxes[i][j].degree > 2) continue;
        Q.add(boxes[i][j]);
        chain++;
        while (!Q.isEmpty()) {
          bx = Q.poll();
          count++;
          flag[bx.x][bx.y] = true;
          int x = bx.x, y = bx.y;
          if (bx.up == 0)
            if (x - 1 > 0 && flag[x - 1][y] == false && boxes[i - 1][j].degree == 2)
              Q.add(getBox(x - 1, y));

          if (bx.down == 0)
            if (x + 1 <= n && flag[x + 1][y] == false && boxes[i + 1][j].degree == 2)
              Q.add(getBox(x + 1, y));

          if (bx.left == 0)
            if (y - 1 > 0 && flag[x][y - 1] == false && boxes[i][j - 1].degree == 2)
              Q.add(getBox(x, y - 1));

          if (bx.right == 0)
            if (y + 1 <= m && flag[x][y + 1] == false && boxes[i][j + 1].degree == 2)
              Q.add(getBox(x, y + 1));

        }
      }
    this.chains = chain;
    return chain;
  }

  public void cutString(Box box, int side) {
    Pair < Box, Box > pair;
    pair = getAdjacentBox(box.x, box.y, side);
    Box box0, box1;
    box0 = pair.getFirst();
    box1 = pair.getSecond();
    box0.setLine(side);
    box1.setLineReverse(side);
    boxes[box0.x][box0.y] = box0.clone();
    boxes[box1.x][box1.y] = box1.clone();
    return;
  }

  public void setString(Box box, int side) {
    Pair < Box, Box > pair;
    pair = getAdjacentBox(box.x, box.y, side);
    Box box0, box1;
    box0 = pair.getFirst();
    box1 = pair.getSecond();
    box0.resetLine(side);
    box1.resetLineReverse(side);
    boxes[box0.x][box0.y] = box0.clone();
    boxes[box1.x][box1.y] = box1.clone();
    return;
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
    System.out.println("minBox moveFromChain " + minLen);
    updateBox(minBox, side);
    return minBox;
  }

  public void Move() {
    int made = 0;
    chains = chainNum();
    if (chains % 2 == 1) {
      made = makeChain(1);
      if (made == 0) made = joinChain();
      if (made == 1) return;

    } else {
      made = makeChain(2);
      if (made == 1) return;
    }
    Box box = possibleMove();
    System.out.println("returned possible Move");
    box.toString1();
    return;
  }

  public Box getBox(int x, int y) {
    return boxes[x][y];
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

  public int checkMove(Box box) {

    int a, b;
    a = box.x;
    b = box.y;

    if (box.up == 0)
      if (a > 0 && boxes[a - 1][b].degree > 2) {
        System.out.println("chkMove up " + (a - 1) + " " + b + " " + boxes[a - 1][b].degree);
        return UP;
      }
    if (box.down == 0)
      if (a <= n && boxes[a + 1][b].degree > 2) {
        System.out.println("chkMove down " + (a + 1) + " " + b + " " + boxes[a + 1][b].degree);
        return DOWN;
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

  public boolean computerMove(Box box) {
    int x, y, side, i, j, k;
    x = box.x;
    y = box.y;
    side = box.hoveredSide;
    Box bx0, bx1, next = boxes[0][0];
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
        int cnt = 0;
        if (bx0.x > 0 && bx0.y > 0 && bx0.x <= n && bx0.y <= m) {
          bx0.setLine(side);
          if (bx0.degree == 0) {
            cnt++;
            bx0.chainLength = oo;
            bx0.isHumanCompleted = true;
            bx0.name = field.name;
            field.X.increment();
            boxes[bx0.x][bx0.y] = bx0.clone();
          }
        }
        if (bx1.x > 0 && bx1.y > 0 && bx1.x <= n && bx1.y <= m) {
          bx1.setLineReverse(side);
          if (bx1.degree == 0) {
            cnt++;
            bx1.chainLength = oo;
            bx1.isHumanCompleted = true;
            bx1.name = field.name;
            field.X.increment();
            boxes[bx1.x][bx1.y] = bx1.clone();
          }
        }

        if (cnt > 0) {
          System.out.println("User get +" + cnt + " point");
          printBox();
          return true;
        }
        System.out.println("degrees " + bx0.degree + " " + bx1.degree);
        int cnt1 = 0, cnt2 = 0;
        if (bx0.degree == 1) cnt1 = eatCount(bx0);
        if (bx1.degree == 1) cnt2 = eatCount(bx1);
        k = checkConnection(pair);
        System.out.println("counts " + cnt1 + " " + cnt2 + " " + field.X.getPoint() + " " + field.Y.getPoint());
        if (field.X.getPoint() + cnt1 + cnt2 + field.Y.getPoint() >= m * n) {

          eatAll(bx0, oo, cnt1);
          eatAll(bx1, oo, cnt2);
          System.out.println("All boxes filled");
          return false;
        }
        if (k == 1 && bx0.degree == 1) {
          System.out.println("connected");
          eatAll(bx0, cnt1, cnt1);
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
          eatAll(bx0, cnt1, cnt1);
          eatAll(bx1, cnt2, cnt2);
        }
        boxes[bx0.x][bx0.y] = bx0.clone();
        boxes[bx1.x][bx1.y] = bx1.clone();
        single(next);
        Move();

        System.out.print("Computer");
        printBox();
      }
    }
    return false;
  }

  public int availMove() {
    int count = 0, i, j;
    for (i = 1; i <= n; i++)
      for (j = 1; j <= m; j++)
        if (boxes[i][j].degree >= 3) count += boxMove(boxes[i][j]);
    return count;
  }

  public void single(Box box) {
    int i, j, cnt;
    Pair pair = getAdjacentBox(box.x, box.y, box.getEmpty());
    Box box1, box2;
    box1 = (Box) pair.getFirst();
    box2 = (Box) pair.getSecond();
    for (i = 1; i <= n; i++)
      for (j = 1; j <= m; j++)
        if (boxes[i][j].degree == 1 && ((box1.x != i && box1.y != j) || (box2.x != i && box2.y != j))) {
          cnt = eatCount(boxes[i][j]);
          eatAll(boxes[i][j], cnt, cnt);
        }
    return;
  }

  public Box possibleMove() {
    System.out.println("In possible Moves");
    int i, j, side;
    int test = 20000;
    Random ri = new Random();
    Random rj = new Random();
    while (test > 0) {
      test--;
      i = ri.nextInt(n) + 1;
      j = rj.nextInt(m) + 1;
      if (boxes[i][j].degree <= 2) continue;
      side = checkMove(boxes[i][j]);
      if (side > 0) {
        System.out.println("random " + i + " " + j + " " + side);
        updateBox(boxes[i][j], side);
        return boxes[i][j];
      }
    }

    for (i = 1; i <= n; i++)
      for (j = 1; j <= m; j++)
        if (boxes[i][j].degree >= 3) {
          side = checkMove(boxes[i][j]);
          System.out.println("pos " + i + " " + j + " " + boxes[i][j].degree + " side " + side);
          if (side > 0) {
            System.out.println("before update" + i + " " + j + " " + side);
            updateBox(boxes[i][j], side);
            return boxes[i][j];
          }
        }

    System.out.println("moveFromChain");
    return moveFromChain();
  }

  public int inChain(Box box) {

    if (box.up == 0 && box.x - 1 > 0 && boxes[box.x - 1][box.y].degree == 2) {
      return 1;
    }
    if (box.down == 0 && (box.x + 1) <= n && boxes[box.x + 1][box.y].degree == 2) {
      return 1;
    }
    if (box.left == 0 && box.y - 1 > 0 && boxes[box.x][box.y - 1].degree == 2) {
      return 1;
    }
    if (box.right == 0 && box.y + 1 <= m && boxes[box.x][box.y + 1].degree == 2) {
      return 1;
    }
    return 0;
  }

  public int diffChain(Pair < Box, Box > pair, int num) {

    int in1, in2, deg;
    deg = pair.getSecond().degree;
    in1 = inChain(pair.getFirst());
    in2 = inChain(pair.getSecond());
    if (num == 1) {
      if (in1 == 1 && in2 == 0 && deg == 2) return 1;
      if (in1 == 0 && in2 == 1) return 1;
    } else {
      if (in1 == 0 && in2 == 0 && deg > 2) return 1;
      if (in1 == 1 && in2 == 1) return 1;
    }
    return 0;
  }

  public int makeChain(int num) {
    int i, j, k, tem;
    lastBox = null;
    lastSide = 0;
    Pair < Box, Box > pair;
    System.out.println("makeChain " + num);
    for (i = 1; i <= n; i++)
      for (j = 1; j <= m; j++) {
        if (boxes[i][j].degree == 3) {
          System.out.println("making chain " + i + " " + j);
          if (boxes[i][j].up == 0 && boxes[i - 1][j].degree > 2) {
            System.out.println("makeChain UP " + boxes[i - 1][j].degree);
            cutString(boxes[i][j], UP);
            pair = getAdjacentBox(i, j, UP);
            k = checkConnection(pair);
            if (k == 0) {
              tem = diffChain(pair, num);
              if (tem == 1) {
                if (availMove() % 2 == 0) {
                  setString(boxes[i][j], UP);
                  chains += num;
                  updateBox(boxes[i][j], UP);
                  System.out.println("returning from makeChain ");
                  return 1;
                } else {
                  lastBox = boxes[i][j];
                  lastSide = UP;
                  which = num;
                }
              }
            }
            setString(boxes[i][j], UP);
          }
          if (boxes[i][j].down == 0 && boxes[i + 1][j].degree > 2) {
            System.out.println("makeChain Down " + boxes[i + 1][j].degree);
            cutString(boxes[i][j], DOWN);
            pair = getAdjacentBox(i, j, DOWN);
            k = checkConnection(pair);
            if (k == 0) {
              tem = diffChain(pair, num);
              if (tem == 1) {
                if (availMove() % 2 == 0) {
                  setString(boxes[i][j], DOWN);
                  chains += num;
                  updateBox(boxes[i][j], DOWN);
                  System.out.println("returning from makeChain ");
                  return 1;
                } else {
                  lastBox = boxes[i][j];
                  lastSide = DOWN;
                  which = num;
                }
              }
            }
            setString(boxes[i][j], DOWN);
          }
          if (boxes[i][j].left == 0 && boxes[i][j - 1].degree > 2) {
            System.out.println("makeChain Left " + boxes[i][j - 1].degree);
            cutString(boxes[i][j], LEFT);
            pair = getAdjacentBox(i, j, LEFT);
            k = checkConnection(pair);
            if (k == 0) {
              tem = diffChain(pair, num);
              if (tem == 1) {
                if (availMove() % 2 == 0) {
                  setString(boxes[i][j], LEFT);
                  chains += num;
                  updateBox(boxes[i][j], LEFT);
                  System.out.println("returning from makeChain ");

                  return 1;
                } else {
                  lastBox = boxes[i][j];
                  lastSide = LEFT;
                  which = num;
                }
              }
            }
            setString(boxes[i][j], LEFT);
          }
          if (boxes[i][j].right == 0 && boxes[i][j + 1].degree > 2) {
            System.out.println("makeChain Right " + boxes[i][j + 1].degree);
            cutString(boxes[i][j], RIGHT);
            pair = getAdjacentBox(i, j, RIGHT);
            k = checkConnection(pair);
            if (k == 0) {
              tem = diffChain(pair, num);
              if (tem == 1) {
                if (availMove() % 2 == 0) {
                  setString(boxes[i][j], RIGHT);
                  chains += num;
                  updateBox(boxes[i][j], RIGHT);
                  System.out.println("returning from makeChain ");
                  return 1;
                } else {
                  lastBox = boxes[i][j];
                  lastSide = RIGHT;
                  which = num;
                }
              }
            }
            setString(boxes[i][j], RIGHT);
          }
        }
      }
    System.out.println("returning from makeChain making no chains");
    return 0;
  }

  public int joinChain() {
    int i, j, k;
    System.out.println("joinChain");
    for (i = 1; i <= n; i++)
      for (j = 1; j <= m; j++) {
        if (boxes[i][j].degree == 3) {
          if (boxes[i][j].up == 0) {
            cutString(boxes[i][j], UP);
            k = checkConnection(getAdjacentBox(i, j, UP));
            setString(boxes[i][j], UP);
            if (k == 1 && boxes[i - 1][j].degree > 2 && boxes[i][j].degree > 2) {
              if (availMove() % 2 == 0) {
                chains--;
                updateBox(boxes[i][j], UP);
                return 1;
              } else {
                lastBox = boxes[i][j];
                lastSide = UP;
                which = 1;
              }
            }

          }
          if (boxes[i][j].down == 0) {
            cutString(boxes[i][j], DOWN);
            k = checkConnection(getAdjacentBox(i, j, DOWN));
            setString(boxes[i][j], DOWN);
            if (k == 1 && boxes[i + 1][j].degree > 2 && boxes[i][j].degree > 2) {
              if (availMove() % 2 == 0) {
                chains--;
                updateBox(boxes[i][j], DOWN);
                return 1;
              } else {
                lastBox = boxes[i][j];
                lastSide = DOWN;
                which = 1;
              }
            }

          }
          if (boxes[i][j].left == 0) {
            cutString(boxes[i][j], LEFT);
            k = checkConnection(getAdjacentBox(i, j, LEFT));
            setString(boxes[i][j], LEFT);
            if (k == 1 && boxes[i][j - 1].degree > 2 && boxes[i][j].degree > 2) {
              if (availMove() % 2 == 0) {
                chains--;
                updateBox(boxes[i][j], LEFT);
                return 1;
              } else {
                lastBox = boxes[i][j];
                lastSide = LEFT;
                which = 1;
              }
            }

          }
          if (boxes[i][j].right == 0) {
            cutString(boxes[i][j], RIGHT);
            k = checkConnection(getAdjacentBox(i, j, RIGHT));
            setString(boxes[i][j], RIGHT);
            if (k == 1 && boxes[i][j + 1].degree > 2 && boxes[i][j].degree > 2) {
              if (availMove() % 2 == 0) {
                chains--;
                updateBox(boxes[i][j], RIGHT);
                return 1;
              } else {
                lastBox = boxes[i][j];
                lastSide = RIGHT;
                which = 1;
              }
            }

          }
        }
      }
    if (lastBox != null) {
      chains -= which;
      updateBox(lastBox, lastSide);
      return 1;
    }

    return 0;
  }

}