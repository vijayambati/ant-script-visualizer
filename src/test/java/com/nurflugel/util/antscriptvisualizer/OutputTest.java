package com.nurflugel.util.antscriptvisualizer;

import com.nurflugel.util.Os;
import com.nurflugel.util.antscriptvisualizer.events.Event;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.io.File;
import java.io.IOException;
import java.util.List;
import static com.nurflugel.util.Os.findOs;
import static com.nurflugel.util.test.TestResources.getFilePath;
import static org.apache.commons.io.FileUtils.contentEquals;
import static org.testng.Assert.assertTrue;

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

  @Test(groups = { "dot", "failed" })
  public void testSimpleOne() throws IOException
  {
    testDotFile(getFilePath("SimpleBuild/onebuild"));
  }

  private void testDotFile(String baseName) throws IOException
  {
    File          file       = new File(baseName + ".xml");
    Os            os         = findOs();
    AntFileParser fileParser = new AntFileParser(os, preferences, null, file);

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

  @Test(groups = { "dot", "failed" })
  public void testSimpleTwoTaskdefsMacrodefs() throws IOException
  {
    testDotFile(getFilePath("SimpleBuild/twobuild_with_taskdef_and_macrodef"));
  }

  @Test(groups = { "dot", "failed" })
  public void testSimpleTwo() throws IOException
  {
    testDotFile(getFilePath("SimpleBuild/twobuild_with_taskdefs"));
  }

  @Test(groups = { "dot", "failed" })
  public void testSimpleThree() throws IOException
  {
    testDotFile(getFilePath("SimpleBuild/threebuild"));
  }

  @Test(groups = { "dot", "failed" })
  public void testSimpleFour() throws IOException
  {
    testDotFile(getFilePath("SimpleBuild/fourbuild"));
  }

  @Test(groups = { "dot", "failed" })
  public void testLegend() throws IOException
  {
    preferences.setShouldShowLegend(true);
    testDotFile(getFilePath("SimpleBuild/legend"));
  }

  @Test(groups = { "dot", "failed" })
  public void testNoMacrodefs() throws IOException
  {
    preferences.setShouldShowMacrodefs(false);
    testDotFile(getFilePath("SimpleBuild/noMacrodefs"));
  }

  @Test(groups = { "dot", "failed" })
  public void testNoTaskdefs() throws IOException
  {
    preferences.setShouldShowTaskdefs(false);
    testDotFile(getFilePath("SimpleBuild/noTaskdefs"));
  }

  @Test(groups = { "dot", "failed" })
  public void testNoAntCalls() throws IOException
  {
    preferences.setShouldShowAntCalls(false);
    testDotFile(getFilePath("SimpleBuild/noAntCalls"));
  }

  @Test(groups = { "dot", "failed" })
  public void testNoAnts() throws IOException
  {
    preferences.setShouldShowAnts(false);
    testDotFile(getFilePath("SimpleBuild/noAnts"));
  }

  @Test(groups = { "dot", "failed" })
  public void testNoTargets() throws IOException
  {
    preferences.setShouldShowTargets(false);
    testDotFile(getFilePath("SimpleBuild/noTargets"));
  }

  @Test(groups = { "cRoy", "failed" })
  public void testCRoy() throws IOException
  {
    testDotFile(getFilePath("C_Roy/build"));
  }

  @Test(groups = { "dot", "failed" })
  public void testDependency() throws IOException
  {
    testDotFile(getFilePath("Dependency Test/build-batch"));
  }

  @Test(groups = { "dot", "failed" })
  public void testEntity() throws IOException
  {
    testDotFile(getFilePath("ENTITY property Test/threebuild"));
  }

  @Test(groups = { "dot", "failed" })
  public void testImport() throws IOException
  {
    testDotFile(getFilePath("Import Test/threebuild"));
  }

  @Test(groups = { "dot", "failed" })
  public void testNoGrouping() throws IOException
  {
    preferences.setShouldGroupByBuildFiles(false);
    testDotFile(getFilePath("Import Test/noGroup"));
  }

  @Test(groups = { "dot", "failed" })
  public void testNoImports() throws IOException
  {
    preferences.setShouldIncludeImportedFiles(false);
    testDotFile(getFilePath("Import Test/noImports"));
  }

  @Test(groups = { "master", "failed" })
  public void testMasterBuild() throws IOException
  {
    testDotFile(getFilePath("MasterBuildExamples/build"));
  }

  @Test(groups = { "master", "failed" })
  public void testMasterBuildOverride() throws IOException
  {
    testDotFile(getFilePath("MasterBuildExamples/build_overrideCompile"));
  }

  @Test(groups = { "master", "failed" })
  public void testMasterBuildOverrideNoShowOverridden() throws IOException
  {
    testDotFile(getFilePath("MasterBuildExamples/build_overrideCompile_NoShowOverriden"));
  }

  @Test(groups = { "master", "failed" })
  public void testMasterBuildOverrideNoUnusedDefs() throws IOException
  {
    testDotFile(getFilePath("MasterBuildExamples/build_overrideCompile_NoUnusedDefs"));
  }

  @Test(groups = { "master", "failed" })
  public void testMasterBuildOverrideJustJavadoc() throws IOException
  {
    testDotFile(getFilePath("MasterBuildExamples/build_overrideCompile_JustJavadoc"));
  }
}
