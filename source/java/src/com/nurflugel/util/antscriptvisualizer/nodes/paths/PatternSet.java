package com.nurflugel.util.antscriptvisualizer.nodes.paths;

import static com.nurflugel.util.antscriptvisualizer.Constants.INCLUDESFILE;
import org.jdom.Attribute;
import org.jdom.Element;
import java.io.File;

// A PatternSet can be like this:

// <patternset id="non.test.sources">
///// <include name="**/*.java"/>
///// <exclude name="**/*Test*"/>
// </patternset>
//
// or like this:
///
// <patternset id="non.test.sources" includes ="**/*Dibble*.java,**/*Dabble*.*" excludes="**/*Test*"/>
public class PatternSet extends PathSet
{
  private File includesFile;

  public PatternSet(Element element)
  {
    super(element);

    Attribute attribute = element.getAttribute("id");

    id = attribute.getValue();
    getIncludesFile();
    getExcludesFile();
    getIncludes();
    getExcludes();
  }

    private void getIncludesFile()
  {
    Attribute attribute = element.getAttribute(INCLUDESFILE);

    if (attribute != null)
    {
      String fileName = attribute.getValue();
      File   file     = new File(fileName);

      // todo read in from file
      System.out.println("This function not yet implemented");
    }
  }

  private void getExcludesFile()
  {
    Attribute attribute = element.getAttribute(INCLUDESFILE);

    if (attribute != null)
    {
      String fileName = attribute.getValue();
      File   file     = new File(fileName);

      // todo read in from file
      System.out.println("This function not yet implemented");
    }
  }
}
