/*
 * Created by IntelliJ IDEA. User: douglasbullard Date: Dec 14, 2004 Time: 7:11:41 PM
 */
package com.nurflugel.util.antscriptvisualizer.events;

import java.util.ArrayList;
import java.util.List;


/**  */
public class EventCollector
{

    private List<Event> events = new ArrayList<Event>();

    /**  */
    public List<Event> getEvents() { return events; }

    /** .* */
    public void addEvent(Event event)
    {
        events.add(event);
    }
}
