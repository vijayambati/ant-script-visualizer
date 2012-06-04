package com.nurflugel.util.gradlescriptvisualizer.domain;

/** Wrapper class for a line of text. */
public class Line
{
  private String text;

  public Line(String text)
  {
    this.text = text;
  }

  public String getText()
  {
    return text;
  }

  @Override
  public String toString()
  {
    return "Line{"
             + "text='" + text + '\'' + '}';
  }
}
