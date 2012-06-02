package com.nurflugel.util.gradlescriptvisualizer.parser;

import com.nurflugel.util.gradlescriptvisualizer.domain.Task;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Created with IntelliJ IDEA. User: douglas_bullard Date: 5/30/12 Time: 22:07 To change this template use File | Settings | File Templates. */
public class GradleFileParser
{
  private String fileName;
  private Map    taskMap = new HashMap();

  public GradleFileParser(String fileName)
  {
    this.fileName = fileName;
  }

  List<String> readLinesInFile() throws IOException
  {
    File         file  = new File(fileName);
    List<String> lines = FileUtils.readLines(file);

    return lines;
  }

  public List<Task> getTasks() throws IOException
  {
    List<String> lines = readLinesInFile();
    List<Task>   tasks = new ArrayList<Task>();

    for (String line : lines)
    {
      if (line.trim().startsWith("task "))
      {
        Task task = new Task(line);

        tasks.add(task);
        taskMap.put(task.getTaskName(), task);
      }
    }

    return tasks;
  }
}
