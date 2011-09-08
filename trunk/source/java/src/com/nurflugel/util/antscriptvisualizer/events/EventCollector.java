/*
 * Created by IntelliJ IDEA. User: douglasbullard Date: Dec 14, 2004 Time: 7:11:41 PM
 */
package com.nurflugel.util.antscriptvisualizer.events;

import java.util.ArrayList;
import java.util.List;

/**
 * Collector for events that we want to handle nicely rather than rippling exceptions up through the stack. In particular, by noting an exception, we
 * don't interrupt execution.
 */
public class EventCollector
{
  private List<Event> events = new ArrayList<Event>();
  // -------------------------- OTHER METHODS --------------------------

  /** .* */
  public void addEvent(Event event)
  {
    events.add(event);
  }

  public void clear()
  {
    events.clear();
  }
  // --------------------- GETTER / SETTER METHODS ---------------------

  /**  */
  public List<Event> getEvents()
  {
    return events;
  }
}
