package com.nurflugel.util.gradlescriptvisualizer.parser;

import com.nurflugel.util.gradlescriptvisualizer.domain.Line;
import com.nurflugel.util.gradlescriptvisualizer.domain.Task;
import org.apache.commons.io.FilenameUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.nurflugel.util.gradlescriptvisualizer.domain.Task.findOrCreateTaskByLine;
import static org.apache.commons.io.FileUtils.readLines;
import static org.apache.commons.lang.StringUtils.remove;
import static org.apache.commons.lang.StringUtils.substringAfter;
import static org.apache.commons.lang.StringUtils.trim;

/** Created with IntelliJ IDEA. User: douglas_bullard Date: 5/30/12 Time: 22:07 To change this template use File | Settings | File Templates. */
public class GradleFileParser
{
  private Map<String, Task> taskMap = new HashMap<String, Task>();

  public GradleFileParser() {}

  // -------------------------- OTHER METHODS --------------------------
  public List<Task> getTasks() throws IOException
  {
    return new ArrayList<Task>(taskMap.values());
  }

  public Map<String, Task> getTasksMap()
  {
    return taskMap;
  }

  public void parseFile(File file) throws IOException
  {
    System.out.println("file = " + file.getAbsolutePath());

    if (file.exists())
    {
      List<Line> lines = readLinesInFile(file);

      findTasksInLines(lines);
      findImports(lines, file);
    }
    else
    {
      throw new FileNotFoundException("Expected file not found: " + file.getAbsolutePath());
    }
  }

  public void parseFile(String fileName) throws IOException
  {
    File file = new File(fileName);

    parseFile(file);
  }

  private void findImports(List<Line> lines, File file) throws IOException
  {
    for (Line line : lines)
    {
      String text = line.getText().trim();

      if (text.startsWith("apply from: "))
      {
        text = substringAfter(text, "apply from: ");
        text = remove(text, '\'');
        text = remove(text, '\"');

        if (text.startsWith("http:"))
        {
          findUrlImport(text);
        }
        else
        {
          String fileName = trim(text);

          // non-absolute path must be resolved relative to the current file
          if (!fileName.startsWith("/"))
          {
            String parent = FilenameUtils.getFullPath(file.getAbsolutePath());

            fileName = parent + fileName;
          }

          parseFile(fileName);
        }
      }
    }
  }

  private void findUrlImport(String fileName) throws IOException
  {
    parseFile(fileName);
  }

  /**
   * We wrap the text lines into object lines so we can determine parsing strings or lines better. Later on, we may modify the line class to be more
   * broad than a single line of text.
   */
  List<Line> readLinesInFile(File file) throws IOException
  {
    List<Line>   lines     = new ArrayList<Line>();
    List<String> textLines = readLines(file);

    for (String textLine : textLines)
    {
      lines.add(new Line(textLine));
    }

    return lines;
  }

  private List<Task> findTasksInLines(List<Line> lines)
  {
    List<Task> tasks = new ArrayList<Task>();

    for (Line line : lines)
    {
      String trimmedLine = line.getText().trim();

      if (trimmedLine.startsWith("task "))
      {
        Task task = findOrCreateTaskByLine(taskMap, line);

        taskMap.put(task.getTaskName(), task);
      }
    }

    return tasks;
  }
}
