package com.nurflugel.util.gradlescriptvisualizer.parser;

import com.nurflugel.util.gradlescriptvisualizer.domain.Task;
import org.apache.commons.lang.ArrayUtils;
import org.testng.annotations.Test;
import java.io.IOException;
import java.util.List;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@Test(groups = "gradle")
public class GradleFileParserTest
{
  // private final String parseFileName = "src/test/resources/gradle/parsetest.gradle";
  private final String parseFileName = "gradle/parsetest.gradle";

  @Test
  public void testCanary()
  {
    assertTrue(true);
  }

  @Test(expectedExceptions = IOException.class)
  public void testReadBadFile() throws IOException
  {
    GradleFileParser parser = new GradleFileParser("dibble.gradle");

    parser.readLinesInFile();
  }

  @Test
  public void testReadLinesFromFile() throws IOException
  {
    GradleFileParser parser = new GradleFileParser(parseFileName);
    List<String>     lines  = parser.readLinesInFile();

    assertFalse(lines.isEmpty());
  }

  @Test
  public void testFindTaskLines() throws IOException
  {
    GradleFileParser parser = new GradleFileParser(parseFileName);
    List<Task>       tasks  = parser.getTasks();

    assertEquals(tasks.size(), 9);
  }

  @Test
  public void testFindTaskNames() throws IOException
  {
    GradleFileParser parser = new GradleFileParser(parseFileName);
    List<Task>       tasks  = parser.getTasks();
    String[]         names  =
    { "formatTestResults", "transform", "publishWebstart", "copyLibs", "copyResources", "copyHelp", "signJars", "listRuntimeJars", "wrapper" };

    for (Task task : tasks)
    {
      assertTrue(ArrayUtils.contains(names, task.getTaskName()));
    }
  }

  // -test read in file
  // -test find tasks
  // -test find task type if it exists
  // -test find dependsOn in task declaration
  // -test find dependsOn in task declaration with multiple dependsOn
  // ==>test find task dependsOn if task exists elsewhere in build script
  // test find dependsOn in task modification
  // test find dependsOn in iterative task modification
  // determine type of task
}
