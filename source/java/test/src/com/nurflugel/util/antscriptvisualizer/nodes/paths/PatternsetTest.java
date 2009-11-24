package com.nurflugel.util.antscriptvisualizer.nodes.paths;

import static com.nurflugel.util.antscriptvisualizer.Constants.*;
import org.jdom.Element;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;
import org.testng.annotations.Test;
import java.io.File;

/** PatternSett Tester. */
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

    PatternSett patternSett = new PatternSett(element);

    assertTrue(patternSett.isFileOk(new File("Fibble.java")));
    assertFalse(patternSett.isFileOk(new File("Fibble.xml")));
    assertFalse(patternSett.isFileOk(new File("FibbleTest.java")));
  }

  // <patternset id="non.test.sources" includes ="**/*Dibble*.java,**/*Dabble*.*" excludes="**/*Test*"/>
  @Test(groups = "unit")
  public void testInlinedIncludesExcludes()
  {
    Element element = new Element(PATTERNSET);

    element.setAttribute(ID, "non.test.sources");
    element.setAttribute(INCLUDES, "**/*Dibble*.java,**/*Dabble*.java");
    element.setAttribute(EXCLUDES, "**/*Test*");

    PatternSett patternSett = new PatternSett(element);

    assertTrue(patternSett.isFileOk(new File("Dibble.java")));
    assertTrue(patternSett.isFileOk(new File("DibbleBibble.java")));
    assertFalse(patternSett.isFileOk(new File("Dabble.xml")));
    assertFalse(patternSett.isFileOk(new File("DibbleTest.java")));
  }
}
