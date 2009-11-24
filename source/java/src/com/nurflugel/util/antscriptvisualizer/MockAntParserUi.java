/*
 * Created by IntelliJ IDEA. User: douglasbullard Date: Dec 2, 2004 Time: 10:34:48 PM
 */
package com.nurflugel.util.antscriptvisualizer;

import javax.swing.*;

/** Copyright 2005, Nurflugel.com. */
public class MockAntParserUi implements AntParserUi
{
  @Override
  public boolean shouldConcentrate()
  {
    return false;
  }

  @Override
  public boolean shouldDeleteDotFilesOnExit()
  {
    return true;
  }

  @Override
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

  @Override
  public boolean showLegend()
  {
    return true;
  }

  @Override
  public JFrame getFrame()
  {
    return new JFrame();
  }

  @Override
  public OutputFormat getOutputFormat()
  {
    return OutputFormat.PDF;
  }

  @Override
  public boolean shouldGroupByBuildfiles()
  {
    return true;
  }

  @Override
  public boolean shouldIncludeImportedFiles()
  {
    return true;
  }

  @Override
  public boolean shouldShowLeftToRight()
  {
    return false;
  }

  @Override
  public boolean shouldShowAntcalls()
  {
    return true;
  }

  @Override
  public boolean shouldShowMacrodefs()
  {
    return true;
  }

  @Override
  public boolean shouldShowTargets()
  {
    return true;
  }

  @Override
  public boolean shouldShowTaskdefs()
  {
    return true;
  }

  @Override
  public boolean shouldUseAbsolutePaths()
  {
    return false;
  }
}
