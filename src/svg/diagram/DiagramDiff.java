package svg.diagram;

import java.util.List;
import svg.RECT;
import svg.SVG;
import svg.XmlNode;

/**
 * The differences.
 *
 * @author volleybase
 */
public class DiagramDiff extends DiagramBase {

  private List<Integer> scoreS = null;
  private List<Integer> scoreR = null;
  private boolean isAServing = false;
  private int posZero = 0;
  private final String keyGradient;

  public DiagramDiff(int width, List<String> labelsY, int posZero, String keyGradient) {
    int h = labelsY.size() - 1;
    this.posZero = posZero;
    this.keyGradient = keyGradient;

    height = (h + 2) * factor;
    origin_y = height - factor;
    yaxis = new YAxis();
    yaxis.labels(labelsY);
    yaxis.showRaster(true, width);
    if (posZero > 0) {
      yaxis.showZero(posZero, width);
    }

    this.width = (width + 2) * factor;
    origin_x = factor;
    xaxis = new XAxis();
    xaxis.setRange(width);
    xaxis.showRaster(true, h);
    if (posZero > 0) {
      xaxis.showAxis(false);
    }
  }

  public void setData(List<Integer> scoreS, List<Integer> scoreR, boolean isAServing) {
    this.scoreS = scoreS;
    this.scoreR = scoreR;
    this.isAServing = isAServing;
  }

  /**
   * Creates the svg code.
   *
   * @return The created svg code.
   * @throws Exception
   */
  @Override
  public String svg() throws Exception {
    SVG svg = createBackground();
    svg.add(new Gradient(keyGradient));
    svg.add(xaxis.svg(origin_x, origin_y, factor));
    svg.add(yaxis.svg(origin_x, origin_y, factor));

    // the data
    // <rect x="0" y="0" height="99" width="24.2" rx="5" ry="5" style="fill: url(#lg_3); stroke: none;"></rect>
    int cntS = scoreS.size(),
      cntR = scoreR.size(),
      cnt = Math.max(cntS, cntR),
      curR = 0, curS = 0, diff, x = 0;

    for (int idx = 0; idx < cnt; ++idx) {
      if (idx > 0 && idx < cntR) {
        curR = scoreR.get(idx);
        diff = (curS - curR) * (isAServing ? -1 : 1);
        x += factor;
        addDiff(svg, x, diff);
      }

      if (idx < cntS) {
        curS = scoreS.get(idx);
        diff = (curS - curR) * (isAServing ? -1 : 1);
        x += factor;
        addDiff(svg, x, diff);
      }
    }

    return svg.out();
  }

  private void addDiff(SVG svg, int x, int diff) throws Exception {
    int h = diff * factor * (diff < 0 ? -1 : 1),
      y = origin_y - posZero * factor;
    if (diff < 0) {
      y += diff * factor;
    }

    if (h > 1) {
      RECT rect = new RECT(x, y, factor, h);
      rect.attr("rx", "5");
      rect.attr("ry", "5");
      rect.style("fill", "url(#lg_" + keyGradient + ")").style("stroke", "none");

      svg.add(rect);
    }
  }
}

class Gradient extends XmlNode {

  private final String content;

  public Gradient(String key) {
    super("");

    content = "  <linearGradient id=\"lg_" + key + "\" x1=\"0%\" y1=\"0%\" x2=\"100%\" y2=\"0%\">"
      + "<stop offset=\"2%\" stop-color=\"#777\" stop-opacity=\"1\"></stop>"
      + "<stop offset=\"27%\" stop-color=\"#BBB\" stop-opacity=\"1\"></stop>"
      + "<stop offset=\"42%\" stop-color=\"#BBB\" stop-opacity=\"1\"></stop>"
      + "<stop offset=\"95%\" stop-color=\"#666\" stop-opacity=\"1\"></stop>"
      + "</linearGradient>";
  }

  @Override
  public String out() {
    return out(0);
  }

  @Override
  public String out(int indent) {
    return content;
  }
}
