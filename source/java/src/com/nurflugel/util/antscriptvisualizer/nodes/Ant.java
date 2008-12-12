/*
 * Created by IntelliJ IDEA. User: douglasbullard Date: Nov 27, 2004 Time: 5:13:53 PM
 */
package com.nurflugel.util.antscriptvisualizer.nodes;

/** The representation of an Ant call. */
public class Ant extends Dependency
{
    public Ant(String  targetName,
               Antfile antFile)
    {
        super(targetName, antFile);
    }


    @Override
    @SuppressWarnings({ "RefusedBequest" })
    public String getDependencyExtraInfo()
    {

        // return "[style=dotted,label=\"Ant\"]";
        // return "[style=dotted,color=red]";
        return "[color=red,style=dotted]";
    }

    @Override
    @SuppressWarnings({ "RefusedBequest" })
    protected void setNodeType()
    {
        nodeType = NodeType.ANT;
    }

    @Override
    public String toString() { return super.toString() + " ::: Ant{" + "antFile='" + buildFile + "'" + "}"; }
}
