package svg;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author volleybase
 */
public class SVGTest {

  public SVGTest() {
  }

  @BeforeClass
  public static void setUpClass() {
  }

  @AfterClass
  public static void tearDownClass() {
  }

  @Before
  public void setUp() {
  }

  @After
  public void tearDown() {
  }

  @Test
  public void testSVG() {
    System.out.println("svg");
    SVG svg = new SVG(820, 1160);

    String check = svg.out();
    String test = "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" width=\"820\" height=\"1160\" viewBox=\"0 0 820 1160\" />";
    Assert.assertEquals(test, check);
  }
}
