package svg.pps;

import svg.G;
import svg.LINE;
import svg.TEXT;

/**
 * The y axis.
 *
 * @author volleybase
 */
public class YAxis extends Axis {

  @Override
  public G svg(int x, int y, int factor) throws Exception {
    G axis = new G();
    axis.attr("transform", "translate(" + x + " " + y + ")");

    int max2 = -max - (areaLabels ? 1 : 0);
    LINE line = new LINE(0, min * factor, 0, max2 * factor);
    styles(line);
    axis.add(line);

    for (int i = 0; i >= max2 + min; --i) {
      LINE tick = new LINE(0, i * factor, -5, i * factor);
      styles(tick);
      axis.add(tick);
      if (labels != null && labels.size() > -i) {
        String lab = labels.get(-i);
        if (lab != null && lab.length() > 0) {
          TEXT text = new TEXT(
            areaLabels ? -3 : -7,
            i * factor - (areaLabels ? factor / 2 : 0),
            "R", lab);
          styles(text);
          axis.add(text);
        }
      }
    }

    return axis;
  }
}
