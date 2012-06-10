package com.nurflugel.util.gradlescriptvisualizer.domain;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import java.util.*;
import static com.nurflugel.util.gradlescriptvisualizer.domain.TaskUsage.EXECUTE;
import static com.nurflugel.util.gradlescriptvisualizer.domain.TaskUsage.GRADLE;
import static org.apache.commons.lang.StringUtils.*;
import static org.apache.commons.lang.StringUtils.split;

public class Task
{
  private static final String DEPENDS_ON     = "dependsOn:";
  private static final String EXECUTE        = ".execute";
  private String              name;
  private String              type;
  private List<Task>          dependsOnTasks = new ArrayList<Task>();
  private TaskUsage           usage          = GRADLE;
  private String[]            scopeLines;
  private boolean             showType       = true;

  public static Task findOrCreateTaskByLine(Map<String, Task> taskMap, Line line, List<Line> lines)
  {
    String name   = findTaskName(line.getText());
    Task   result;

    if (taskMap.containsKey(name))
    {
      result = taskMap.get(name);
    }
    else
    {
      result = new Task(taskMap, line);
      taskMap.put(name, result);
    }

    // find any dependencies in the task
    result.findTaskDependsOn(taskMap, line);
    result.setScopeLines(findTaskLinesInScope(line, lines));
    result.analyzeScopeLinesForExecuteDependencies(taskMap);

    return result;
  }

  /** Find the task name in the line. */
  private static String findTaskName(String line)
  {
    String taskName = substringAfter(line, "task ");

    taskName = substringBefore(taskName, " ");
    taskName = getTextBeforeIfExists(taskName, "(");

    return taskName;
  }

  // find the depends on from something like task signJars(dependsOn: 'installApp') << {
  private void findTaskDependsOn(Map<String, Task> taskMap, Line line)
  {
    findTaskDependsOn(taskMap, line, DEPENDS_ON);
  }

  /**
   * Go through the lines from the given line where the task is declared and get all the lines within the task scope for analysis.
   *
   * @param  line   the line where the task is declared
   * @param  lines  the lines of the script
   */
  private static String[] findTaskLinesInScope(Line line, List<Line> lines)
  {
    List<String> scopeLines = new ArrayList<String>();
    int          index      = lines.indexOf(line);

    // in case the opening { is on the next (or later) lines, scan ahead until we find it
    while (!line.getText().contains("{") && (index < lines.size()))
    {
      index++;
    }

    String text = line.getText();

    if (text.contains("{"))
    {
      scopeLines.add(text);

      int levelOfNesting = 0;

      levelOfNesting += countMatches(text, "{");
      levelOfNesting -= countMatches(text, "}");  // I'm assuming braces will be nicely formatted and not all on one line, bad assumption as that's
                                                  // legal

      while ((levelOfNesting > 0) && (index++ < lines.size()))
      {
        text = lines.get(index).getText();
        scopeLines.add(text);
        levelOfNesting += countMatches(text, "{");
        levelOfNesting -= countMatches(text, "}");
      }
    }

    return scopeLines.toArray(new String[scopeLines.size()]);
  }

  /** Go through the scope lines, look for any .executes - grab that and mark that task as a dependency. */
  private void analyzeScopeLinesForExecuteDependencies(Map<String, Task> taskMap)
  {
    String[] scopeLines1 = scopeLines;

    for (String line : scopeLines1)
    {
      String executeDependency = findExecuteDependency(line);

      if (executeDependency != null)
      {
        Task newTask = findOrCreateTaskByName(taskMap, executeDependency);

        newTask.setUsage(TaskUsage.EXECUTE);
        dependsOnTasks.add(newTask);
      }
    }
  }

  public static String findExecuteDependency(String text)
  {
    if (contains(text, EXECUTE))
    {
      String beforeText         = substringBefore(text, EXECUTE);
      String afterLastSpaceText = substringAfterLast(beforeText, " ");
      String trimmedText        = afterLastSpaceText.trim();

      return trimmedText;
    }
    else
    {
      return null;
    }
  }

  public static Task findOrCreateTaskByName(Map<String, Task> taskMap, String name)
  {
    Task result;

    if (taskMap.containsKey(name))
    {
      result = taskMap.get(name);
    }
    else
    {
      result = new Task(name);
      taskMap.put(name, result);
    }

    return result;
  }

  // check.dependsOn integrationTest
  public static List<Task> findOrCreateImplicitTasksByLine(Map<String, Task> taskMap, String trimmedLine)
  {
    List<Task> tasks       = new ArrayList<Task>();
    String     dependsText = ".dependsOn";
    String     text        = substringBefore(trimmedLine, dependsText);

    if (text.contains("["))  // it's a list
    {
      text = substringAfter(text, "[");
      text = substringBefore(text, "]");

      String[] tokens = split(text, ",");

      for (String token : tokens)
      {
        Task task = extractTaskByName(taskMap, trimmedLine, dependsText, token);

        tasks.add(task);
      }
    }
    else
    {
      Task task = extractTaskByName(taskMap, trimmedLine, dependsText, text);

      tasks.add(task);
    }

    return tasks;
  }

  private static Task extractTaskByName(Map<String, Task> taskMap, String trimmedLine, String dependsText, String taskName)
  {
    String name = taskName.trim();
    Task   task = findOrCreateTaskByName(taskMap, name);

    task.findTaskDependsOn(taskMap, new Line(trimmedLine), dependsText);

    return task;
  }

  // todo do I still need Line in this???
  private void findTaskDependsOn(Map<String, Task> taskMap, Line line, String dependsText)
  {
    String text = substringAfter(line.getText(), dependsText);

    text = substringBefore(text, ")");

    // test for multiple dependsOn
    if (text.contains("["))
    {
      text = substringAfter(text, "[");
      text = substringBefore(text, "]");

      String[] tokens = split(text, ",");

      for (String token : tokens)
      {
        addSingleDependsOnTask(taskMap, token);
      }
    }
    else
    {
      if (isNotBlank(text))
      {
        addSingleDependsOnTask(taskMap, text);
      }
    }
  }

  private void addSingleDependsOnTask(Map<String, Task> taskMap, String oldText)
  {
    // remove any quotes
    String text = substringBefore(oldText, ",");

    text = remove(text, '\"');
    text = remove(text, '\'');
    text = trim(text);

    Task task = findOrCreateTaskByName(taskMap, text);

    if (!dependsOnTasks.contains(task))
    {
      dependsOnTasks.add(task);
    }
  }

  public static Task findOrCreateImplicitTasksByExecute(Map<String, Task> taskmap, String line)
  {
    String trim = line.trim();

    if (trim.contains(EXECUTE))
    {
      String taskName = substringBefore(trim, EXECUTE);
      Task   task     = findOrCreateTaskByName(taskmap, taskName);

      task.setUsage(TaskUsage.EXECUTE);

      return task;
    }

    return null;
  }

  public Task(String name)
  {
    this.name = name;
  }

  // todo maybe a better way of doing this would be to have a subclass which takes the other constructor??
  Task(Map<String, Task> taskMap, Line line)
  {
    name = findTaskName(line.getText());
    type = findTaskType(line.getText());
    findTaskDependsOn(taskMap, line);
  }

  private static String findTaskType(String line)
  {
    if (line.contains("type:"))
    {
      String taskType = substringAfter(line, "type:");

      taskType = trim(taskType);
      taskType = getTextBeforeIfExists(taskType, ")");
      taskType = getTextBeforeIfExists(taskType, ",");
      taskType = getTextBeforeIfExists(taskType, " ");
      taskType = trim(taskType);

      return taskType;
    }
    else
    {
      return "noType";
    }
  }

  private static String getTextBeforeIfExists(String taskType, String matchingText)
  {
    if (taskType.contains(matchingText))
    {
      taskType = substringBefore(taskType, matchingText);
    }

    return taskType;
  }

  // -------------------------- OTHER METHODS --------------------------
  public List<Task> getDependsOn()
  {
    return dependsOnTasks;
  }

  public String getDotDeclaration()
  {
    return name + " [label=\"" + getDeclarationLabel() + "\" shape=" + usage.getShape() + " color=" + usage.getColor() + " ];";
  }

  private String getDeclarationLabel()
  {
    boolean shouldShowType = showType;
    boolean noType         = StringUtils.equals(type, "noType");

    shouldShowType &= !noType;
    shouldShowType &= isNotEmpty(type);

    return shouldShowType ? (name + "\\n" + "Type: " + type)
                          : name;
  }

  public List<String> getDotDependencies()
  {
    List<String> lines = new ArrayList<String>();

    for (Task dependsOnTask : dependsOnTasks)
    {
      lines.add(name + " -> " + dependsOnTask.name + ';');
    }

    return lines;
  }

  public void printTask(int nestingLevel)
  {
    System.out.println(leftPad("", nestingLevel * 4) + "task = " + this);

    for (Task dependancy : dependsOnTasks)
    {
      dependancy.printTask(nestingLevel + 1);
    }
  }

  // ------------------------ CANONICAL METHODS ------------------------
  @Override
  public boolean equals(Object obj)
  {
    if (obj == null)
    {
      return false;
    }

    if (getClass() != obj.getClass())
    {
      return false;
    }

    Task other = (Task) obj;

    return new EqualsBuilder().append(name, other.name).isEquals();
  }

  @Override
  public int hashCode()
  {
    return new HashCodeBuilder().append(name).toHashCode();
  }

  public String toString()
  {
    return name;
  }

  // --------------------- GETTER / SETTER METHODS ---------------------
  public String getName()
  {
    return name;
  }

  public String[] getScopeLines()
  {
    return scopeLines;
  }

  public void setScopeLines(String[] scopeLines)
  {
    this.scopeLines = scopeLines;
  }

  public String getType()
  {
    return type;
  }

  public TaskUsage getUsage()
  {
    return usage;
  }

  public void setUsage(TaskUsage usage)
  {
    this.usage = usage;
  }
}
