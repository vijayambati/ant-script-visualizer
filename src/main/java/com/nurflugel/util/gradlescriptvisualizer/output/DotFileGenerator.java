package com.nurflugel.util.gradlescriptvisualizer.output;

import com.nurflugel.util.gradlescriptvisualizer.domain.Task;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.apache.commons.io.FileUtils.writeLines;
import static org.apache.commons.io.FilenameUtils.getBaseName;
import static org.apache.commons.io.FilenameUtils.getFullPath;

public class DotFileGenerator
{
  public List<String> createOutput(List<Task> tasks)
  {
    List<String> output = new ArrayList<String>();

    output.add("digraph G {\n"
                 + "node [shape=box,fontname=\"Arial\",fontsize=\"10\"];\n"
                 + "edge [fontname=\"Arial\",fontsize=\"8\"];\n"
                 + "rankdir=BT;\n" + '\n' + "concentrate=true;");

    // declare tasks
    for (Task task : tasks)
    {
      output.add(task.getDotDeclaration());
    }

    output.add("\n\n");

    for (Task task : tasks)
    {
      output.addAll(task.getDotDependencies());
    }

    output.add("}");

    return output;
  }

  public File writeOutput(List<String> lines, String gradleFileName) throws IOException
  {
    String name       = getBaseName(gradleFileName);
    String path       = getFullPath(gradleFileName);
    String outputName = path + name + ".dot";
    File   file       = new File(outputName);

    System.out.println("writing output file = " + file.getAbsolutePath());
    writeLines(file, lines);

    return file;
  }
}
