package svg.diagram;

//<editor-fold defaultstate="collapsed" desc="The imports.">
import createstatistics.data.ActionInfo;
import createstatistics.data.SetInfo;
import java.util.ArrayList;
import java.util.List;
import svg.CIRCLE;
import svg.LINE;
import svg.RECT;
import svg.SVG;
import svg.TEXT;
//</editor-fold>

/**
 * A diagram.
 *
 * @author volleybase
 */
public class DiagramPps extends DiagramBase {

  //<editor-fold defaultstate="collapsed" desc="The fields.">
  public String colorA = "red";
  public String colorB = "blue";

  // the additional height for the lineup
  private final static int OFFSET_LINEUP = 100;

  // the size of a data marker
  private final static int DATAPOINT_RADIUS = 5;
  // the thickness of a data line
  private final static int DATALINE_THICKNESS = 4;

  // the line up info of team A
  private List<String> namesA = null;
  // the line up info of team B
  private List<String> namesB = null;
  // max y position for diagram
  private int top_y = 0;
  // the data
  private List<Integer> dataS, dataR;
  // which team serves
  private boolean aIsSrv;
  // the set to display
  private final SetInfo si;
  //</editor-fold>

  public DiagramPps(SetInfo si, int maxY, List<String> namesA, List<String> namesB) {

    this.si = si;

    // axis
    setY(si.maxPt());
    setMaxY(maxY);
    setX(si.length(), namesA, namesB);
    setData(si.scoringsA, si.scoringsB);
    // dia.setActions(si);
  }

  //<editor-fold defaultstate="collapsed" desc="Sets the data.">
  /**
   * Initializes the points axis.
   *
   * @param max The maximum number of point for this set.
   * @return The diagram itself.
   */
  private DiagramPps setY(int max) {
    yaxis = new YAxis();
    List<String> labels = new ArrayList<>();
    for (int i = 0; i <= max; ++i) {
      labels.add("" + i);
    }
    yaxis.labels(labels);

    height = (max + 2) * factor;
    origin_y = height - factor;
    top_y = factor;

    return this;
  }

  /**
   * Increases the height to maximum points to draw multiple sets in a row.
   *
   * @param max The maximum number of points for all sets of a game.
   * @return The diagram itself.
   */
  private DiagramPps setMaxY(int max) {
    int h = (max + 2) * factor;
    if (h > height) {
      top_y += h - height;
      height = h;
    }
    origin_y = height - factor;

    return this;
  }

//  /**
//   * Initializes the x axis.
//   *
//   * @param size The size of the x axis.
//   * @return The diagram itself.
//   */
//  public Diagram setX(int size) {
//    return setX(size, null, null);
//  }
  private DiagramPps setX(int size, List<String> namesA, List<String> namesB) {
    xaxis = new XAxis();
    List<String> labels = new ArrayList<>();
    float start = .6f;
    for (int i = 0; i < size; ++i, start += 0.5f) {
      labels.add("" + (((int) start) % 6 + 1));
    }
    xaxis.labels(labels, true);

    width = (labels.size() + 2) * factor;
    origin_x = factor;

    if (height > 0 && namesA != null && namesB != null) {
      this.namesA = namesA;
      this.namesB = namesB;
      height += OFFSET_LINEUP;
    }

    return this;
  }

  /**
   * Sets the points.
   *
   * The first entry of the receiving is -1 to mark it.
   *
   * @param dataA The points of team A.
   * @param dataB The points of team B.
   * @return The diagram itself.
   */
  private DiagramPps setData(List<Integer> dataA, List<Integer> dataB) {

    aIsSrv = dataA.get(0) >= 0;

    if (aIsSrv) {
      dataS = dataA;
      dataR = dataB;
    } else {
      dataS = dataB;
      dataR = dataA;
    }

    return this;
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Svg output.">
  /**
   * Creates the svg code.
   *
   * @return The created svg code.
   * @throws Exception
   */
  public String svg() throws Exception {
//    SVG svg = new SVG(width, height);
//    RECT rect = new RECT(0, 0, width, height);
//    rect.style("fill", "gold");
//    svg.add(rect);
    SVG svg = createBackground();

    // axis, lineup
    svg.add(xaxis.svg(origin_x, origin_y, factor));
    List<String> la = namesA;
    List<String> lb = namesB;

    // lineup a
    if (la != null && lb != null) {
      for (int xx = origin_x + factor / 2, idx = 0; xx < width - factor; xx += factor * 2, ++idx) {
        String txt = la.get(idx);
        if (txt != null && txt.length() > 0) {
          int y = origin_y + 20;
          TEXT text = new TEXT(0, 0, "L", txt);
          text.attr("transform", "translate(" + xx + " " + y + ") rotate(90)");
          text.style("fill", aIsSrv ? colorA : colorB)
            .style("stroke", "none")
            .style("font", ((int) (factor * 0.7f)) + "px sans-serif");
          svg.add(text);

          RECT rectMarker = new RECT(xx - factor / 2, top_y, factor, -top_y + origin_y);
          rectMarker.style("fill", "#80808011");
          svg.add(rectMarker);
        }
      }
      // lineup b
      for (int xx = origin_x + factor / 2 + factor, idx = 0; xx < width - factor; xx += factor * 2, ++idx) {
        String txt = lb.get(idx);
        if (txt != null && txt.length() > 0) {
          int y = origin_y + 20;
          TEXT text = new TEXT(0, 0, "L", txt);
          text.attr("transform", "translate(" + xx + " " + y + ") rotate(90)");
          text.style("fill", aIsSrv ? colorB : colorA)
            .style("stroke", "none")
            .style("font", ((int) (factor * 0.7f)) + "px sans-serif");
          svg.add(text);
        }
      }
    }
    svg.add(yaxis.svg(origin_x, origin_y, factor));

    // optional actions
    if (si.actions != null) {
      int idx = 0, lastX = 0;
      List<String> keys = new ArrayList<>(si.actions.keySet());
      keys.sort(null);
      for (String key : keys) {
        List<ActionInfo> infos = si.actions.get(key);
        for (ActionInfo ai : infos) {
          ++idx;
          int x = (int) (origin_x + factor * ai.position);
          if (x > lastX + 105) {
            idx = 1;
          }
          lastX = x;

          LINE line = new LINE(x, origin_y, x, top_y);
          line.style("stroke", "#111")
            .style("stroke-width", "0.5")
            .style("stroke-dasharray", "1 2")
            .style("stroke-linecap", "round")
            .style("stroke-linejoin", "round");
          svg.add(line);
          CIRCLE circle = new CIRCLE(x, top_y + 1, 1);
          circle.style("fill", "#111").style("stroke", "none");
          svg.add(circle);
          float hh = factor / 3f * 2f;
          RECT rc = new RECT(x - 50, (int) (origin_y - hh * idx), 100, (int) hh - 1);
          rc.style("fill", ai.teamA ? colorA : colorB)
            .style("stroke", "none");
          svg.add(rc);
          TEXT text = new TEXT(x, (int) (origin_y - hh * idx), "T", ai.info);
          text.style("fill", "yellow") //ai.teamA ? "black" : "yellow")
            .style("stroke", "none")
            .style("font", ((int) (hh * 0.7f)) + "px sans-serif");
          svg.add(text);
        }
      }
    }

    // points of team a
    dataline(svg, dataS, dataR, aIsSrv);
    dataline(svg, dataR, dataS, !aIsSrv);

    return svg.out();
  }

  /**
   * Creates a data line.
   *
   * @param svg The svg file.
   * @param team The team to draw.
   * @param opponent The oppeonent.
   * @param isA Is the team to draw team A?
   * @throws Exception
   */
  private void dataline(SVG svg, List<Integer> team, List<Integer> opponent, boolean isA)
    throws Exception {

    int offset_x = 0;
    boolean receiving = false;
    if (team.get(0) == -1) {
      receiving = true;
      offset_x = -factor;
    }
    boolean isLoser = team.get(team.size() - 1) < opponent.get(opponent.size() - 1);

    int last = 0;
    boolean waitForTeam2 = true;
    for (int i = 0; i < team.size(); ++i) {
      if (i == 0 && receiving) {
        continue;
      }
      int x = origin_x + i * factor * 2 + offset_x;
      int y = origin_y - last * factor;

      // start with a marker
      if (i == 0 || (i == 1 && receiving)) {
        CIRCLE circle = new CIRCLE(x, y, DATAPOINT_RADIUS);
        stylesCircle(circle, isA);
        svg.add(circle);
      }

      // one point for getting the service
      if (i > 0) {
        LINE line = new LINE(x, y, x, y - factor);
        stylesLine(line, isA);
        svg.add(line);
        y -= factor;
      }

      // int cur = last + team.get(i);
      int cur = team.get(i);
      if (cur > last) {
        int y2 = origin_y - cur * factor;
        // points with serving
        LINE line = new LINE(x, y, x + factor, y2);
        stylesLine(line, isA);
        svg.add(line);

        // other team to serve
        if ((i < team.size() - 1 && i < opponent.size()) || isLoser) {
          waitForTeam2 = true;
          line = new LINE(x + factor, y2, x + 2 * factor, y2);
          stylesLine(line, isA);
          svg.add(line);
        } else {
          waitForTeam2 = false;
        }
        last = cur;
      } else {
        LINE line = new LINE(x, y, x + 2 * factor, y);
        stylesLine(line, isA);
        svg.add(line);
      }
      y = origin_y - last * factor;
      CIRCLE circle2 = new CIRCLE(x + factor + (waitForTeam2 ? factor : 0), y, DATAPOINT_RADIUS);
      stylesCircle(circle2, isA);
      svg.add(circle2);
    }
  }

  /**
   * Styles a point of a data line.
   *
   * @param circle The circle to style.
   * @param isSrv True for the serving team, false for the receiving one.
   */
  private void stylesCircle(CIRCLE circle, boolean isSrv) {
    circle.style("fill", isSrv ? colorA : colorB)
      .style("stroke", "none");
  }

  /**
   * Styles a line of a data line.
   *
   * @param line The line to style.
   * @param isSrv True for the serving team, false for the receiving one.
   */
  private void stylesLine(LINE line, boolean isSrv) {
    line.style("stroke", isSrv ? colorA : colorB)
      .style("stroke-width", "" + DATALINE_THICKNESS)
      .style("stroke-linecap", "round")
      .style("stroke-linejoin", "round");
  }
  //</editor-fold>
}
