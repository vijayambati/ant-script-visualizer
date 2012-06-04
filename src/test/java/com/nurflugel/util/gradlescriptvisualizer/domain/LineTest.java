package com.nurflugel.util.gradlescriptvisualizer.domain;

import org.testng.Assert;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class LineTest
{
  @Test
  public void testToString() throws Exception
  {
    Line line = new Line("dibble");

    assertEquals(line.toString(), "Line{text='dibble'}");
  }
}
