package com.nurflugel.util.antscriptvisualizer;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static org.testng.Assert.assertTrue;

/** Test class to exercise the app and compare it to known good results. */
public class OutputTest
{
  private Preferences preferences;

  /** Here we set up preferences, overriding the defaults as needed. */
  @BeforeTest
  public void doSetup()
  {
    preferences = new Preferences();
    preferences.setShouldShowLegend(false);
  }

  @Test(groups = "unit")
  public void testOutputSimple() throws IOException
  {
      AntFileParser fileParser = new AntFileParser(Os.findOs(),preferences, null, new File("unversioned/config/SimpleBuild/onebuild.xml"));

    fileParser.processBuildFile(false);
    assertTrue(FileUtils.contentEquals(new File("unversioned/config/Simplebuild/onebuild.dot"),
                                       new File("unversioned/config/Simplebuild/onebuild_test.dot")),"File contents should be identical");
  }
}
