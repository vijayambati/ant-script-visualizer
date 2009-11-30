package com.nurflugel.util.antscriptvisualizer;

import static com.nurflugel.util.antscriptvisualizer.OutputFormat.*;
import static com.nurflugel.util.antscriptvisualizer.OutputFormat.PNG;

/**
 * Created by IntelliJ IDEA. User: douglasbullard Date: Nov 24, 2009 Time: 5:04:47 PM To change this template use File | Settings | File Templates.
 */
public class Preferences
{
  private static final String         CONCENTRATE_LINES          = "concentrateLines";
  private static final String         DELETE_DOT_FILES_ON_EXIT   = "deleteDotFilesOnExit";
  private static final String         DOT_EXECUTABLE             = "dotExecutablePath";
  private static final String         GROUP_NODES_BY_BUILDFILE   = "groupNodesByBuildfile";
  private static final String         INCLUDE_IMPORTED_ANT_FILES = "includeImportedAntFiles";
  private static final String         OUTPUT_FORMAT              = "outputFormat";
  private static final String         SHOW_ABSOLUTE_FILE_PATHS   = "show_absolute_file_paths";
  private static final String         SHOW_ANTCALLS              = "showAntcalls";
  private static final String         SHOW_ANTS                  = "showAnts";
  private static final String         SHOW_MACRODEFS             = "showMacrodefs";
  private static final String         SHOW_TARGETS               = "showTargets";
  private static final String         SHOW_TASKDEFS              = "showTaskdefs";
  private static final String         LAST_DIR                   = "LAST_DIR";
  private static final String         PREVIOUS_VERSION           = "version";
  private static final String         SHOW_LEGEND                = "showLegend";
  private java.util.prefs.Preferences preferencesStore;
  private boolean                     shouldGroupByBuildfiles    = true;
  private boolean                     shouldIncludeImportedFiles = true;
  private boolean                     shouldConcentrate          = true;
  private boolean                     shouldShowMacrodefs        = true;
  private boolean                     shouldShowAntcalls         = true;
  private boolean                     shouldShowTargets          = true;
  private boolean                     shouldDeleteDotFilesOnExit = true;
  private OutputFormat                outputFormat               = PDF;
  private boolean                     shouldUseAbsolutePaths     = false;
  private boolean                     shouldShowTaskdefs         = true;
  private String                      lastDir;
  private String                      dotExecutablePath;
  private String                      previousVersion;
  private boolean                     shouldShowAnts             = true;
  private boolean                     shouldShowLegend           = true;

  public Preferences()
  {
    preferencesStore           = java.util.prefs.Preferences.userNodeForPackage(AntParserUiImpl.class);
    dotExecutablePath          = preferencesStore.get(DOT_EXECUTABLE, "");
    shouldGroupByBuildfiles    = preferencesStore.getBoolean(GROUP_NODES_BY_BUILDFILE, true);
    shouldIncludeImportedFiles = preferencesStore.getBoolean(INCLUDE_IMPORTED_ANT_FILES, true);
    shouldConcentrate          = preferencesStore.getBoolean(CONCENTRATE_LINES, true);
    shouldShowMacrodefs        = preferencesStore.getBoolean(SHOW_MACRODEFS, true);
    shouldShowTaskdefs         = preferencesStore.getBoolean(SHOW_TASKDEFS, true);
    shouldShowAntcalls         = preferencesStore.getBoolean(SHOW_ANTCALLS, true);
    shouldShowAnts             = preferencesStore.getBoolean(SHOW_ANTS, true);
    shouldShowTargets          = preferencesStore.getBoolean(SHOW_TARGETS, true);
    shouldDeleteDotFilesOnExit = preferencesStore.getBoolean(DELETE_DOT_FILES_ON_EXIT, true);
    shouldDeleteDotFilesOnExit = preferencesStore.getBoolean(SHOW_LEGEND, true);
    shouldUseAbsolutePaths     = preferencesStore.getBoolean(SHOW_ABSOLUTE_FILE_PATHS, false);
    lastDir                    = preferencesStore.get(LAST_DIR, "");
    outputFormat               = OutputFormat.valueOf(preferencesStore.get(OUTPUT_FORMAT, PNG.getDisplayLabel()));
    previousVersion            = preferencesStore.get(PREVIOUS_VERSION, "");
  }

  public Preferences(boolean dummyFlag)
  {
    preferencesStore           = java.util.prefs.Preferences.userNodeForPackage(AntParserUiImpl.class);
    dotExecutablePath          = preferencesStore.get(DOT_EXECUTABLE, "");
  }

  public void save()
  {
    preferencesStore.putBoolean(GROUP_NODES_BY_BUILDFILE, shouldGroupByBuildfiles);
    preferencesStore.putBoolean(INCLUDE_IMPORTED_ANT_FILES, shouldIncludeImportedFiles);
    preferencesStore.putBoolean(CONCENTRATE_LINES, shouldConcentrate);
    preferencesStore.putBoolean(SHOW_MACRODEFS, shouldShowMacrodefs);
    preferencesStore.putBoolean(SHOW_TASKDEFS, shouldShowTaskdefs);
    preferencesStore.putBoolean(SHOW_ANTCALLS, shouldShowAntcalls);
    preferencesStore.putBoolean(SHOW_ANTS, shouldShowAnts);
    preferencesStore.putBoolean(SHOW_TARGETS, shouldShowTargets);
    preferencesStore.putBoolean(DELETE_DOT_FILES_ON_EXIT, shouldDeleteDotFilesOnExit);
    preferencesStore.put(OUTPUT_FORMAT, outputFormat.getDisplayLabel());
    preferencesStore.putBoolean(SHOW_ABSOLUTE_FILE_PATHS, shouldUseAbsolutePaths);
    preferencesStore.put(DOT_EXECUTABLE, dotExecutablePath);
    preferencesStore.put(LAST_DIR, lastDir);
    preferencesStore.putBoolean(SHOW_LEGEND, shouldShowLegend);
  }

  public void setShouldGroupByBuildfiles(boolean shouldGroupByBuildfiles)
  {
    this.shouldGroupByBuildfiles = shouldGroupByBuildfiles;
  }

  public void setShouldIncludeImportedFiles(boolean shouldIncludeImportedFiles)
  {
    this.shouldIncludeImportedFiles = shouldIncludeImportedFiles;
  }

  public void setShouldConcentrate(boolean shouldConcentrate)
  {
    this.shouldConcentrate = shouldConcentrate;
  }

  public void setShouldShowMacrodefs(boolean shouldShowMacrodefs)
  {
    this.shouldShowMacrodefs = shouldShowMacrodefs;
  }

  public void setShouldShowAntcalls(boolean shouldShowAntcalls)
  {
    this.shouldShowAntcalls = shouldShowAntcalls;
  }

  public void setShouldShowTargets(boolean shouldShowTargets)
  {
    this.shouldShowTargets = shouldShowTargets;
  }

  public void setShouldDeleteDotFilesOnExit(boolean shouldDeleteDotFilesOnExit)
  {
    this.shouldDeleteDotFilesOnExit = shouldDeleteDotFilesOnExit;
  }

  public void setOutputFormat(OutputFormat outputFormat)
  {
    this.outputFormat = outputFormat;
  }

  public void setShouldUseAbsolutePaths(boolean shouldUseAbsolutePaths)
  {
    this.shouldUseAbsolutePaths = shouldUseAbsolutePaths;
  }

  public void setShouldShowTaskdefs(boolean shouldShowTaskdefs)
  {
    this.shouldShowTaskdefs = shouldShowTaskdefs;
  }

  public String getLastDir()
  {
    return lastDir;
  }

  public void setLastDir(String lastDir)
  {
    this.lastDir = lastDir;
      save();
  }

  public String getDotExecutablePath()
  {
    return dotExecutablePath;
  }

  public void setPreferencesStore(java.util.prefs.Preferences preferencesStore)
  {
    this.preferencesStore = preferencesStore;
  }

  public void setDotExecutablePath(String dotExecutablePath)
  {
    this.dotExecutablePath = dotExecutablePath;
  }

  public boolean shouldGroupByBuildfiles()
  {
    return shouldGroupByBuildfiles;
  }

  public boolean shouldIncludeImportedFiles()
  {
    return shouldIncludeImportedFiles;
  }

  public boolean shouldConcentrate()
  {
    return shouldConcentrate;
  }

  public boolean shouldShowMacrodefs()
  {
    return shouldShowMacrodefs;
  }

  public boolean shouldShowAntcalls()
  {
    return shouldShowAntcalls;
  }

  public boolean shouldShowTargets()
  {
    return shouldShowTargets;
  }

  public boolean shouldDeleteDotFilesOnExit()
  {
    return shouldDeleteDotFilesOnExit;
  }

  public OutputFormat getOutputFormat()
  {
    return outputFormat;
  }

  public boolean shouldUseAbsolutePaths()
  {
    return shouldUseAbsolutePaths;
  }

  public boolean shouldShowTaskdefs()
  {
    return shouldShowTaskdefs;
  }

  public String getPreviousVersion()
  {
    return previousVersion;
  }

  public void setPreviousVersion(String previousVersion)
  {
    this.previousVersion = previousVersion;
  }

  public boolean shouldShowAnts()
  {
    return shouldShowAnts;
  }

  public void setShouldShowAnts(boolean shouldShowAnts)
  {
    this.shouldShowAnts = shouldShowAnts;
  }

  public boolean shouldShowLegend()
  {
    return shouldShowLegend;
  }

  public void setShouldShowLegend(boolean shouldShowLegend)
  {
    this.shouldShowLegend = shouldShowLegend;
  }
}
