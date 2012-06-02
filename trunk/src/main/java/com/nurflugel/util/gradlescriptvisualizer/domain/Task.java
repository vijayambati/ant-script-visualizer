package com.nurflugel.util.gradlescriptvisualizer.domain;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import java.util.ArrayList;
import java.util.List;
import static org.apache.commons.lang.StringUtils.*;

public class Task
{
  private String     taskName;
  private String     taskType;
  private List<Task> dependsOnTasks = new ArrayList<Task>();

  public Task(String line)
  {
    findTaskName(line);
    findTaskType(line);
    findTaskDependsOn(line);
  }

  public Task() {}

  // find the depends on from something like task signJars(dependsOn: 'installApp') << {
  private void findTaskDependsOn(String line)
  {
    String text = substringAfter(line, "dependsOn:");

    text = substringBefore(text, ")");

    // test for multiple dependsOn
    if (text.contains("["))
    {
      text = substringAfter(text, "[");
      text = substringBefore(text, "]");

      String[] tokens = StringUtils.split(text, ",");

      for (String token : tokens)
      {
        addSingleDependsOnTask(token);
      }
    }
    else
    {
      addSingleDependsOnTask(text);
    }
  }

  private void addSingleDependsOnTask(String text)
  {
    // remove any quotes
    text = substringBefore(text, ",");
    text = remove(text, '\"');
    text = remove(text, '\'');
    text = trim(text);

    Task task = new Task();

    task.setTaskName(text);
    dependsOnTasks.add(task);
  }

  private void findTaskType(String line)
  {
    if (line.contains("type:"))
    {
      String taskType = substringAfter(line, "type:");

      taskType      = trim(substringBefore(taskType, ")"));
      this.taskType = taskType;
    }
    else
    {
      this.taskType = "noType";
    }
  }

  private void findTaskName(String line)
  {
    String taskName = substringAfter(line, "task ");

    taskName = substringBefore(taskName, " ");

    if (taskName.contains("("))
    {
      taskName = substringBefore(taskName, "(");
    }

    this.taskName = taskName;
  }

  public String getTaskName()
  {
    return taskName;
  }

  public String toString()
  {
    return new ToStringBuilder(this).append("taskName", taskName).toString();
  }

  public String getTaskType()
  {
    return taskType;
  }

  public List<Task> getDependsOn()
  {
    return dependsOnTasks;
  }

  public void setTaskName(String taskName)
  {
    this.taskName = taskName;
  }
}
