package createstatistics;

import java.util.ArrayList;
import java.util.List;

/**
 * The points of a set.
 *
 * @author gortonhu
 */
class SetPoints {

  final List<Integer> pointsA = new ArrayList<>();
  final List<Integer> pointsB = new ArrayList<>();
  boolean startA;

  SetPoints(boolean startA) {
    this.startA = startA;
  }

  SetPoints addA(int points) {
    pointsA.add(points);
    return this;
  }

  SetPoints addB(int points) {
    pointsB.add(points);
    return this;
  }
}
