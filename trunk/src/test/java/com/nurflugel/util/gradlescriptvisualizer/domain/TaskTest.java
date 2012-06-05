package com.nurflugel.util.gradlescriptvisualizer.domain;

import org.testng.annotations.Test;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.nurflugel.util.gradlescriptvisualizer.domain.Task.findOrCreateImplicitTaskByLine;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@Test(groups = "gradle")
public class TaskTest
{
  @Test
  public void testFindTaskType()
  {
    Task task = new Task(new HashMap<String, Task>(), new Line("task copyHelp(type: Copy) {"));

    assertEquals(task.getTaskType(), "Copy");
  }

  @Test
  public void testFindTaskTypeNoTypeDeclared()
  {
    Task task = new Task(new HashMap<String, Task>(), new Line("task copyHelp() {"));

    assertEquals(task.getTaskType(), "noType");
  }

  @Test
  public void testFindDependsOn()
  {
    Task       task           = new Task(new HashMap<String, Task>(), new Line("task signJars(dependsOn: 'installApp') << {"));
    List<Task> dependsOnTasks = task.getDependsOn();

    assertEquals(dependsOnTasks.size(), 1);
    assertEquals(dependsOnTasks.get(0).getTaskName(), "installApp");
  }

  @Test
  public void testFindDependsOnDoubleQuotes()
  {
    Task       task           = new Task(new HashMap<String, Task>(), new Line("task signJars(dependsOn: \"installApp\") << {"));
    List<Task> dependsOnTasks = task.getDependsOn();

    assertEquals(dependsOnTasks.size(), 1);
    assertEquals(dependsOnTasks.get(0).getTaskName(), "installApp");
  }

  @Test
  public void testFindDependsOnNoQuotes()
  {
    Task       task           = new Task(new HashMap<String, Task>(), new Line("task signJars(dependsOn: installApp) << {"));
    List<Task> dependsOnTasks = task.getDependsOn();

    assertEquals(dependsOnTasks.size(), 1);
    assertEquals(dependsOnTasks.get(0).getTaskName(), "installApp");
  }

  @Test
  public void testFindDependsOnWithComma()
  {
    Task       task           = new Task(new HashMap<String, Task>(), new Line("task jettyRunMock(dependsOn: war, description:"));
    List<Task> dependsOnTasks = task.getDependsOn();

    assertEquals(dependsOnTasks.size(), 1);
    assertEquals(dependsOnTasks.get(0).getTaskName(), "war");
  }

  @Test
  public void testFindMultipleDependsOn()
  {
    Task       task           = new Task(new HashMap<String, Task>(), new Line("task signJars(dependsOn: [installApp,dibble, dabble]) << {"));
    List<Task> dependsOnTasks = task.getDependsOn();

    assertEquals(dependsOnTasks.size(), 3);
    assertEquals(dependsOnTasks.get(0).getTaskName(), "installApp");
    assertEquals(dependsOnTasks.get(1).getTaskName(), "dibble");
    assertEquals(dependsOnTasks.get(2).getTaskName(), "dabble");
  }

  @Test
  public void testDotDeclaration()
  {
    Task task = new Task("simpleTask");

    assertEquals(task.getDotDeclaration(), "simpleTask [label=\"simpleTask\" shape=box color=black ];");
  }

  @Test
  public void testDotDependencies()
  {
    Task         task  = new Task(new HashMap<String, Task>(), new Line("task signJars(dependsOn: [installApp,dibble, dabble]) << {"));
    List<String> lines = task.getDotDependencies();

    assertEquals(lines.get(0), "signJars -> installApp;");
    assertEquals(lines.get(1), "signJars -> dibble;");
    assertEquals(lines.get(2), "signJars -> dabble;");
  }

  @Test
  public void testImplicitTask1()
  {
    // check.dependsOn integrationTest
    Task task = findOrCreateImplicitTaskByLine(new HashMap<String, Task>(), "check.dependsOn integrationTest");

    assertTrue(task.getTaskName().equals("check"));
  }

  @Test
  public void testImplicitTaskDepends()
  {
    // check.dependsOn integrationTest
    Task       task      = findOrCreateImplicitTaskByLine(new HashMap<String, Task>(), "check.dependsOn integrationTest");
    List<Task> dependsOn = task.getDependsOn();

    assertFalse(dependsOn.isEmpty());
    assertTrue(dependsOn.get(0).getTaskName().equals("integrationTest"));
  }

  @Test
  public void testImplicitTaskDepends2()
  {
    // check.dependsOn integrationTest
    Task       task      = findOrCreateImplicitTaskByLine(new HashMap<String, Task>(), "check.dependsOn [integrationTest,'dibble']");
    List<Task> dependsOn = task.getDependsOn();

    assertFalse(dependsOn.isEmpty());
    assertTrue(dependsOn.get(0).getTaskName().equals("integrationTest"));
    assertTrue(dependsOn.get(1).getTaskName().equals("dibble"));
  }

  @Test
  public void testImplicitTask2()
  {
    // check.dependsOn integrationTest
    Map<String, Task> taskMap = new HashMap<String, Task>();

    findOrCreateImplicitTaskByLine(taskMap, "check.dependsOn integrationTest");
    assertTrue(taskMap.containsKey("integrationTest"));
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
