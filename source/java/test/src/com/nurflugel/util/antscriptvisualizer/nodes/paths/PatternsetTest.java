package com.nurflugel.util.antscriptvisualizer.nodes.paths;

import static com.nurflugel.util.antscriptvisualizer.Constants.*;
import org.jdom.Element;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;
import org.testng.annotations.Test;
import java.io.File;

/** PatternSet Tester. */
public class PatternsetTest
{
  // <patternset id="non.test.sources">
  ///// <include name="**/*.java"/>
  ///// <exclude name="**/*Test*"/>
  // </patternset>
  @Test(groups = "unit")
  public void testSimpleIncludesExcludes()
  {
    Element element = new Element(PATTERNSET);

    element.setAttribute(ID, "non.test.sources");

    Element includeElement = new Element(INCLUDE);

    includeElement.setAttribute(NAME, "**/*.java");
    element.addContent(includeElement);

    Element excludeElement = new Element(EXCLUDE);

    excludeElement.setAttribute(NAME, "**/*Test*");
    element.addContent(excludeElement);

    PatternSet patternSet = new PatternSet(element);

    assertTrue(patternSet.isFileOk(new File("Fibble.java")));
    assertFalse(patternSet.isFileOk(new File("Fibble.xml")));
    assertFalse(patternSet.isFileOk(new File("FibbleTest.java")));
  }

  // <patternset id="non.test.sources" includes ="**/*Dibble*.java,**/*Dabble*.*" excludes="**/*Test*"/>
  @Test(groups = "unit")
  public void testInlinedIncludesExcludes()
  {
    Element element = new Element(PATTERNSET);

    element.setAttribute(ID, "non.test.sources");
    element.setAttribute(INCLUDES, "**/*Dibble*.java,**/*Dabble*.java");
    element.setAttribute(EXCLUDES, "**/*Test*");

    PatternSet patternSet = new PatternSet(element);

    assertTrue(patternSet.isFileOk(new File("Dibble.java")));
    assertTrue(patternSet.isFileOk(new File("DibbleBibble.java")));
    assertFalse(patternSet.isFileOk(new File("Dabble.xml")));
    assertFalse(patternSet.isFileOk(new File("DibbleTest.java")));
  }
}
