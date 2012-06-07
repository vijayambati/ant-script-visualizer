package com.nurflugel.util.gradlescriptvisualizer.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import java.util.*;
import static com.nurflugel.util.gradlescriptvisualizer.domain.TaskUsage.EXECUTE;
import static com.nurflugel.util.gradlescriptvisualizer.domain.TaskUsage.GRADLE;
import static org.apache.commons.lang.StringUtils.*;
import static org.apache.commons.lang.StringUtils.split;

public class Task
{
  private String     taskName;
  private String     taskType;
  private List<Task> dependsOnTasks = new ArrayList<Task>();
  private TaskUsage  taskUsage      = GRADLE;

  public static Task findOrCreateTaskByLine(Map<String, Task> taskMap, Line line)
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

    return result;
  }

  private static String findTaskName(String line)
  {
    String taskName = substringAfter(line, "task ");

    taskName = substringBefore(taskName, " ");

    if (taskName.contains("("))
    {
      taskName = substringBefore(taskName, "(");
    }

    return taskName;
  }

  // find the depends on from something like task signJars(dependsOn: 'installApp') << {
  private void findTaskDependsOn(Map<String, Task> taskMap, Line line)
  {
    findTaskDependsOn(taskMap, line, "dependsOn:");
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

  private static Task extractTaskByName(Map<String, Task> taskMap, String trimmedLine, String dependsText, String name)
  {
    name = name.trim();

    Task task = findOrCreateTaskByName(taskMap, name);

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

  public static Task findOrCreateImplicitTasksByExecute(Map<String, Task> taskmap, String line)
  {
    String trim = line.trim();

    if (trim.contains(".execute"))
    {
      String taskName = substringBefore(trim, ".execute");
      Task   task     = findOrCreateTaskByName(taskmap, taskName);

      task.setTaskUsage(EXECUTE);

      return task;
    }

    return null;
  }

  public Task(String name)
  {
    taskName = name;
  }

  // todo maybe a better way of doing this would be to have a subclass which takes the other constructor??
  Task(Map<String, Task> taskMap, Line line)
  {
    taskName = findTaskName(line.getText());
    taskType = findTaskType(line.getText());
    findTaskDependsOn(taskMap, line);
  }

  private static String findTaskType(String line)
  {
    if (line.contains("type:"))
    {
      String taskType = substringAfter(line, "type:");

      taskType = trim(substringBefore(taskType, ")"));

      return taskType;
    }
    else
    {
      return "noType";
    }
  }

  // -------------------------- OTHER METHODS --------------------------
  public List<Task> getDependsOn()
  {
    return dependsOnTasks;
  }

  public String getDotDeclaration()
  {
    return taskName + " [label=\"" + taskName + "\" shape=" + taskUsage.getShape() + " color=" + taskUsage.getColor() + " ];";     // todo add task type, color, shape later
  }

  public List<String> getDotDependencies()
  {
    List<String> lines = new ArrayList<String>();

    for (Task dependsOnTask : dependsOnTasks)
    {
      lines.add(taskName + " -> " + dependsOnTask.taskName + ';');
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

    return new EqualsBuilder().append(taskName, other.taskName).isEquals();
  }

  @Override
  public int hashCode()
  {
    return new HashCodeBuilder().append(taskName).toHashCode();
  }

  public String toString()
  {
    return taskName;
  }

  // --------------------- GETTER / SETTER METHODS ---------------------
  public String getTaskName()
  {
    return taskName;
  }

  public String getTaskType()
  {
    return taskType;
  }

  public TaskUsage getTaskUsage()
  {
    return taskUsage;
  }

  public void setTaskUsage(TaskUsage taskUsage)
  {
    this.taskUsage = taskUsage;
  }
}
