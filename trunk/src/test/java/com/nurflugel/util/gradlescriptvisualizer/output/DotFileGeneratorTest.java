package com.nurflugel.util.gradlescriptvisualizer.output;

import com.nurflugel.util.gradlescriptvisualizer.domain.Task;
import com.nurflugel.util.gradlescriptvisualizer.parser.GradleFileParser;
import com.nurflugel.util.gradlescriptvisualizer.parser.GradleFileParserTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.io.IOException;
import java.util.List;
import static org.apache.commons.io.FilenameUtils.getBaseName;
import static org.apache.commons.io.FilenameUtils.getFullPath;

/** Created with IntelliJ IDEA. User: douglas_bullard Date: 6/2/12 Time: 19:18 To change this template use File | Settings | File Templates. */
public class DotFileGeneratorTest
{
  @Test
  public void testGenerateSimpleDotFile() throws IOException
  {
    // GradleFileParser parser = new
    // GradleFileParser("/Users/douglas_bullard/Documents/JavaStuff/Google_Code/AntScriptVisualizer_Google/gradleTrunk/master-gradle/master-build.gradle");
    String           gradleFileName = GradleFileParserTest.getFilePath("dasbuild.gradle");
    GradleFileParser parser         = new GradleFileParser();

    parser.parseFile(gradleFileName);

    List<Task>       tasks            = parser.getTasks();
    DotFileGenerator dotFileGenerator = new DotFileGenerator();
    List<String>     lines            = dotFileGenerator.createOutput(tasks);

    dotFileGenerator.writeOutput(lines, gradleFileName);

    for (String line : lines)
    {
      System.out.println(line);
    }

    Assert.fail("just failed");
  }
}
