/*
 * Created by IntelliJ IDEA. User: douglasbullard Date: Dec 10, 2004 Time: 9:07:28 PM
 */
package com.nurflugel.util.antscriptvisualizer;

import java.util.ArrayList;


/** todo get rid of this and use Set or something real... */
public class UniqueList<T> extends ArrayList<T>
{

    /** Use serialVersionUID for interoperability. */
    private static final long serialVersionUID = 4904814593676631969L;

    /**  */
    @Override
    public boolean add(T o) { return !super.contains(o) && super.add(o); }
}
