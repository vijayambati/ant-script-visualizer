/*
 * Created by IntelliJ IDEA. User: douglasbullard Date: Dec 14, 2004 Time: 7:13:14 PM
 */
package com.nurflugel.util.antscriptvisualizer.events;

/**
 * Represents an expected event (usually caused by an exception), that we want to handle nicely rather than rippling exceptions up through the stack.
 * In particular, by noting an exception, we don't interrupt execution.
 */
public class Event
{
  private Exception exception;
  private String    reason;

  /**  */
  public Event(String reason, Exception exception)
  {
    this.reason    = reason;
    this.exception = exception;
  }

  /**  */
  public Exception getException()
  {
    return exception;
  }

  /**  */
  public String getReason()
  {
    return reason;
  }
}
