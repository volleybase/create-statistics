package svg.pps;

import svg.G;
import svg.LINE;
import svg.TEXT;

/**
 * The x axis.
 *
 * @author volleybase
 */
public class XAxis extends Axis {

  @Override
  public G svg(int x, int y, int factor) throws Exception {
    G axis = new G();
    axis.attr("transform", "translate(" + x + " " + y + ")");

    LINE line = new LINE(min * factor, 0, max * factor + (areaLabels ? factor : 0), 0);
    styles(line);
    axis.add(line);

    for (int i = 0; i <= max - min + (areaLabels ? 1 : 0); ++i) {
      //LINE tick = new LINE(i * factor, -1000, i * factor, 5);
      LINE tick = new LINE(i * factor, 0, i * factor, 5);
      styles(tick);
      axis.add(tick);
      if (labels != null && labels.size() > i) {
        String lab = labels.get(i);
        if (lab != null && lab.length() > 0) {
          TEXT text = new TEXT(
            i * factor + (areaLabels ? factor / 2 : 0),
            areaLabels ? 3 : 7,
            "T", lab);
          styles(text);
          axis.add(text);
        }
      }
    }

    return axis;
  }
}
