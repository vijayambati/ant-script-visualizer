package com.nurflugel.util.antscriptvisualizer;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.io.File;
import java.io.IOException;
import static org.testng.Assert.assertTrue;

/** Test class to exercise the app and compare it to known good results. */
public class OutputTest
{
  private Preferences preferences;

  /** Here we set up preferences, overriding the defaults as needed. */
  @BeforeMethod(groups = "dot")
  public void doSetup()
  {
    preferences = new Preferences(true);
    preferences.setShouldShowLegend(false);
    preferences.setShouldDeleteDotFilesOnExit(false);
  }

  @Test(groups = "dot")
  public void testSimpleOne() throws IOException
  {
    testDotFile("unversioned/config/SimpleBuild/onebuild");
  }

  private void testDotFile(String baseName) throws IOException
  {
    AntFileParser fileParser = new AntFileParser(Os.findOs(), preferences, null, new File(baseName + ".xml"));

    fileParser.processBuildFile(false);
    assertTrue(fileParser.getEventCollector().getEvents().isEmpty(), "Should not have any events");

    File testFile     = new File(baseName + ".dot");
    File standardFile = new File(baseName + "_test.dot");

    System.out.println("file length=" + testFile.length());
    System.out.println("std file length=" + standardFile.length());

    boolean areEqual = FileUtils.contentEquals(testFile, standardFile);

    assertTrue(areEqual, "File contents should be identical");
  }

  @Test(groups = "dot")
  public void testSimpleTwoTaskdefsMacrodefs() throws IOException
  {
    testDotFile("unversioned/config/SimpleBuild/twobuild_with_taskdef_and_macrodef");
  }

  @Test(groups = "dot")
  public void testSimpleTwo() throws IOException
  {
    testDotFile("unversioned/config/SimpleBuild/twobuild_with_taskdefs");
  }

  @Test(groups = "dot")
  public void testSimpleThree() throws IOException
  {
    testDotFile("unversioned/config/SimpleBuild/threebuild");
  }

  @Test(groups = "dot")
  public void testSimpleFour() throws IOException
  {
    testDotFile("unversioned/config/Simplebuild/fourbuild");
  }

  @Test(groups = "dot")
  public void testDependency() throws IOException
  {
    testDotFile("unversioned/config/Dependency Test/build-batch");
  }

  @Test(groups = "dot")
  public void testEntity() throws IOException
  {
    testDotFile("unversioned/config/ENTITY property Test/threebuild");
  }

  @Test(groups = "dot")
  public void testImport() throws IOException
  {
    testDotFile("unversioned/config/Import Test/threebuild");
  }

  @Test(groups = "dot")
  public void testLegend() throws IOException
  {
    preferences.setShouldShowLegend(true);
    testDotFile("unversioned/config/SimpleBuild/legend");
  }

  @Test(groups = "dot")
  public void testNoMacrodefs() throws IOException
  {
    preferences.setShouldShowMacrodefs(false);
    testDotFile("unversioned/config/SimpleBuild/noMacrodefs");
  }

  @Test(groups = "dot")
  public void testNoTaskdefs() throws IOException
  {
    preferences.setShouldShowTaskdefs(false);
    testDotFile("unversioned/config/SimpleBuild/noTaskdefs");
  }

  @Test(groups = "dot")
  public void testNoAntCalls() throws IOException
  {
    preferences.setShouldShowAntCalls(false);
    testDotFile("unversioned/config/SimpleBuild/noAntCalls");
  }

  @Test(groups = "dot")
  public void testNoAnts() throws IOException
  {
    preferences.setShouldShowAnts(false);
    testDotFile("unversioned/config/SimpleBuild/noAnts");
  }

  @Test(groups = "dot")
  public void testNoTargets() throws IOException
  {
    preferences.setShouldShowTargets(false);
    testDotFile("unversioned/config/SimpleBuild/noTargets");
  }

  @Test(groups = "dot")
  public void testNoGrouping() throws IOException
  {
    preferences.setShouldGroupByBuildfiles(false);
    testDotFile("unversioned/config/Import Test/noGroup");
  }

  @Test(groups = "dot")
  public void testNoImports() throws IOException
  {
    preferences.setShouldIncludeImportedFiles(false);
    testDotFile("unversioned/config/Import Test/noImports");
  }
}
