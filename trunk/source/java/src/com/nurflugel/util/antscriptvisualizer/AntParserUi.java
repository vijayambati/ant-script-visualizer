/*
 * Created by IntelliJ IDEA. User: douglasbullard Date: Dec 2, 2004 Time: 8:21:25 PM
 */
package com.nurflugel.util.antscriptvisualizer;

import javax.swing.*;

public interface AntParserUi
{
  OutputFormat getOutputFormat();
  boolean shouldGroupByBuildfiles();
  boolean shouldIncludeImportedFiles();
  boolean shouldShowLeftToRight();
  boolean shouldShowAntcalls();
  boolean shouldShowMacrodefs();
  boolean shouldShowTargets();
  boolean shouldShowTaskdefs();
  boolean shouldConcentrate();
  boolean shouldDeleteDotFilesOnExit();
  boolean shouldUseAbsolutePaths();
  JFrame getFrame();

  /**
   * Get the dot executable path if it already exists in Preferences, or is intalled. If not easily findable, as the user where the hell he put it.
   */
  String getDotExecutablePath();
  boolean showLegend();
}
