/*
 * Created by IntelliJ IDEA. User: douglasbullard Date: Dec 2, 2004 Time: 10:34:48 PM
 */
package com.nurflugel.util.antscriptvisualizer;

import javax.swing.*;

/** Copyright 2005, Nurflugel.com. */
public class MockAntParserUi implements AntParserUi
{
  public boolean shouldConcentrate()
  {
    return false;
  }

  public boolean shouldDeleteDotFilesOnExit()
  {
    return true;
  }

  public String getDotExecutablePath()
  {
    String os = System.getProperty("os.name");

    if (os.startsWith("Mac OS"))
    {
      return "/Applications/Graphviz.app/Contents/MacOS/dot ";
    }
    else  // if (os.toLowerCase().startsWith("windows"))
    {
      return "\"C:\\Program Files\\ATT\\Graphviz\\bin\\dot.exe\" ";
    }
  }

  public boolean showLegend()
  {
    return true;
  }

  public JFrame getFrame()
  {
    return new JFrame();
  }

  public OutputFormat getOutputFormat()
  {
    return OutputFormat.PDF;
  }

  public boolean shouldGroupByBuildfiles()
  {
    return true;
  }

  public boolean shouldIncludeImportedFiles()
  {
    return true;
  }

  public boolean shouldShowLeftToRight()
  {
    return false;
  }

  public boolean shouldShowAntcalls()
  {
    return true;
  }

  public boolean shouldShowMacrodefs()
  {
    return true;
  }

  public boolean shouldShowTargets()
  {
    return true;
  }

  public boolean shouldShowTaskdefs()
  {
    return true;
  }

  public boolean shouldUseAbsolutePaths()
  {
    return false;
  }
}
