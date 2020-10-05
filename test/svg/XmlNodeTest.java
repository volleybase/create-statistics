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
public class XmlNodeTest {

  public XmlNodeTest() {
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

  /**
   * Test of attr method, of class XmlNode.
   */
  @Test
  public void testAttr() {
    System.out.println("attr");
    XmlNode node = new XmlNode("node");
    node.attr("key", "value");
    node.attr("key2", "value 2");

    String test = "<node key=\"value\" key2=\"value 2\" />";
    String check = node.out();
    Assert.assertEquals(test, check);
  }

  /**
   * Test of add method, of class XmlNode.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testAdd() throws Exception {
    System.out.println("add");
    XmlNode node = new XmlNode("outer");
    XmlNode node2 = new XmlNode("inner");
    node.add(node2);

    String test = "<outer>\n  <inner />\n</outer>";
    String check = node.out();
    Assert.assertEquals(test, check);
  }

  /**
   * Test of text method, of class XmlNode.
   */
  @Test
  public void testText() throws Exception {
    System.out.println("text");
    String text = "text";
    XmlNode node = new XmlNode("node");
    node.text(text);

    String test = "<node>text</node>";
    String check = node.out();
    Assert.assertEquals(test, check);
  }

//  /**
//   * Test of out method, of class XmlNode.
//   */
//  @Test
//  public void testOut_0args() {
//    System.out.println("out");
//    XmlNode instance = null;
//    String expResult = "";
//    String result = instance.out();
//    assertEquals(expResult, result);
//    fail("The test case is a prototype.");
//  }
//
//  /**
//   * Test of out method, of class XmlNode.
//   */
//  @Test
//  public void testOut_int() {
//    System.out.println("out");
//    int indent = 0;
//    XmlNode instance = null;
//    String expResult = "";
//    String result = instance.out(indent);
//    assertEquals(expResult, result);
//    fail("The test case is a prototype.");
//  }
}
