package edu.vt.cs5044.DotsAndBoxes;

import java.util.Random;

public class Box {

  public static final int UP = 1, DOWN = 3, LEFT = 4, RIGHT = 2;
  public int up, down, left, right, degree, x, y, nextside, parentx, parenty, chainLength, hoveredSide;
  public boolean isHumanCompleted = false, isComputerCompleted = false;
  public String name = "0";
  public Box() {
    this(0, 0);
  }

  public int getNearestLine(int x, int y, int boxL) {
    int side;

    if (y < x) {
      if (y < boxL - x) {
        side = UP;
      } else {
        side = RIGHT;
      }
    } else {
      if (y < boxL - x) {
        side = LEFT;
      } else {
        side = DOWN;
      }
    }
    return side;
  }

  public Box(int x, int y) {
    this.x = x;
    this.y = y;
    up = down = left = right = 0;
    degree = 4;
    chainLength = 0;
    parentx = -1;
    parenty = -1;
  }

  public Box clone() {
    return this;
  }

  public void toString1() {
    if (x > 0 && y > 0) {
      System.out.println("(x, y)=(" + x + " , " + y + ")(px, py)=(" + parentx + " , " + parenty + " ) Length = " + chainLength + " Up = " + up + " down= " + down + " left= " + left + " right= " + right + " degree= " + degree);
    }
  }

  public int getEmpty() {
    int side = 0;

    if (degree <= 0) {
      return -1;
    }
    Random rnd = new Random();
    while (true) {
      if (UP == x && up == side) {
        return 1;
      }
      if (DOWN == x && down == side) {
        return 3;
      }
      if (LEFT == x && left == side) {
        return 4;
      }
      if (RIGHT == x && right == side) {
        return 2;
      }
    }
  }

  public void setLine(int side) {
    if (side == 1) {
      up = side;
    } else if (side == 3) {
      down = side;
    } else if (side == 4) {
      left = side;
    } else if (side == 2) {
      right = side;
    }
    degree--;
  }

  public void resetLine(int side) {
    if (side == 1) {
      up = 0;
    } else if (side == 3) {
      down = 0;
    } else if (side == 4) {
      left = 0;
    } else if (side == 2) {
      right = 0;
    }
    degree++;
  }

  public void setLineReverse(int side) {
    if (side == 1) {
      down = 3;
    } else if (side == 3) {
      up = 1;
    } else if (side == 4) {
      right = 2;
    } else if (side == 2) {
      left = 4;
    }
    degree--;
  }

  public void resetLineReverse(int side) {
    if (side == 1) {
      down = 0;
    } else if (side == 3) {
      up = 0;
    } else if (side == 4) {
      right = 0;
    } else if (side == 2) {
      left = 0;
    }
    degree++;
  }

  public int getReverse(int side) {
    if (side == 1) {
      return 3;
    } else if (side == 3) {
      return 1;
    } else if (side == 4) {
      return 2;
    } else if (side == 2) {
      return 4;
    }
    return -1;
  }

  public boolean isSet(int side) {
    if (up == side) {
      return true;
    }
    if (down == side) {
      return true;
    }
    if (left == side) {
      return true;
    }
    if (right == side) {
      return true;
    }
    return false;
  }

  public void reset() {
    up = down = left = right = 0;
    degree = 4;
  }
}