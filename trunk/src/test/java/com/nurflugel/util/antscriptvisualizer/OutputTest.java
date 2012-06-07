package com.nurflugel.util.antscriptvisualizer;

import static com.nurflugel.util.Os.findOs;
import com.nurflugel.util.antscriptvisualizer.events.Event;
import static org.apache.commons.io.FileUtils.contentEquals;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.io.File;
import java.io.IOException;
import java.util.List;

/** Test class to exercise the app and compare it to known good results. */
public class OutputTest
{
  private AntScriptPreferences preferences;

  /** Here we set up preferences, overriding the defaults as needed. */
  @BeforeMethod(groups = { "dot", "master", "cRoy" })
  public void doSetup()
  {
    preferences = new AntScriptPreferences(true);
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
    AntFileParser fileParser = new AntFileParser(findOs(), preferences, null, new File(baseName + ".xml"));

    fileParser.processBuildFile(false);

    List<Event> events = fileParser.getEventCollector().getEvents();

    assertTrue(events.isEmpty(), "Should not have any events");

    File testFile     = new File(baseName + ".dot");
    File standardFile = new File(baseName + "_test.dot");

    System.out.println("file length=" + testFile.length());
    System.out.println("std file length=" + standardFile.length());

    boolean areEqual = contentEquals(testFile, standardFile);

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
    testDotFile("unversioned/config/SimpleBuild/fourbuild");
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

  @Test(groups = "cRoy")  // todo
  public void testCRoy() throws IOException
  {
    testDotFile("unversioned/config/C_Roy/build");
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
  public void testNoGrouping() throws IOException
  {
    preferences.setShouldGroupByBuildFiles(false);
    testDotFile("unversioned/config/Import Test/noGroup");
  }

  @Test(groups = "dot")
  public void testNoImports() throws IOException
  {
    preferences.setShouldIncludeImportedFiles(false);
    testDotFile("unversioned/config/Import Test/noImports");
  }

  @Test(groups = "master")  // todo
  public void testMasterBuild() throws IOException
  {
    testDotFile("unversioned/config/MasterBuildExamples/build");
  }

  @Test(groups = "master")  // todo
  public void testMasterBuildOverride() throws IOException
  {
    testDotFile("unversioned/config/MasterBuildExamples/build_overrideCompile");
  }

  @Test(groups = "master")
  public void testMasterBuildOverrideNoShowOverridden() throws IOException
  {
    testDotFile("unversioned/config/MasterBuildExamples/build_overrideCompile_NoShowOverriden");
  }

  @Test(groups = "master")
  public void testMasterBuildOverrideNoUnusedDefs() throws IOException
  {
    testDotFile("unversioned/config/MasterBuildExamples/build_overrideCompile_NoUnusedDefs");
  }

  @Test(groups = { "master", "failed" })
  public void testMasterBuildOverrideJustJavadoc() throws IOException
  {
    testDotFile("unversioned/config/MasterBuildExamples/build_overrideCompile_JustJavadoc");
  }
}
