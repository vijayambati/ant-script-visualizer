package com.nurflugel.util.gradlescriptvisualizer.domain;

import org.testng.annotations.Test;
import java.util.List;
import static org.testng.Assert.assertEquals;

@Test(groups = "gradle")
public class GradleTaskTest
{
  @Test
  public void testFindTaskType()
  {
    Task task = new Task("task copyHelp(type: Copy) {");

    assertEquals(task.getTaskType(), "Copy");
  }

  @Test
  public void testFindTaskTypeNoTypeDeclared()
  {
    Task task = new Task("task copyHelp() {");

    assertEquals(task.getTaskType(), "noType");
  }

  @Test
  public void testFindDependsOn()
  {
    Task       task           = new Task("task signJars(dependsOn: 'installApp') << {");
    List<Task> dependsOnTasks = task.getDependsOn();

    assertEquals(dependsOnTasks.size(), 1);
    assertEquals(dependsOnTasks.get(0).getTaskName(), "installApp");
  }

  @Test
  public void testFindDependsOnDoubleQuotes()
  {
    Task       task           = new Task("task signJars(dependsOn: \"installApp\") << {");
    List<Task> dependsOnTasks = task.getDependsOn();

    assertEquals(dependsOnTasks.size(), 1);
    assertEquals(dependsOnTasks.get(0).getTaskName(), "installApp");
  }

  @Test
  public void testFindDependsOnNoQuotes()
  {
    Task       task           = new Task("task signJars(dependsOn: installApp) << {");
    List<Task> dependsOnTasks = task.getDependsOn();

    assertEquals(dependsOnTasks.size(), 1);
    assertEquals(dependsOnTasks.get(0).getTaskName(), "installApp");
  }

  @Test
  public void testFindDependsOnWithComma()
  {
    Task       task           = new Task("task jettyRunMock(dependsOn: war, description:");
    List<Task> dependsOnTasks = task.getDependsOn();

    assertEquals(dependsOnTasks.size(), 1);
    assertEquals(dependsOnTasks.get(0).getTaskName(), "war");
  }

  @Test
  public void testFindMultipleDependsOn()
  {
    Task       task           = new Task("task signJars(dependsOn: [installApp,dibble, dabble]) << {");
    List<Task> dependsOnTasks = task.getDependsOn();

    assertEquals(dependsOnTasks.size(), 3);
    assertEquals(dependsOnTasks.get(0).getTaskName(), "installApp");
    assertEquals(dependsOnTasks.get(1).getTaskName(), "dibble");
    assertEquals(dependsOnTasks.get(2).getTaskName(), "dabble");
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
