package com.nurflugel.util.antscriptvisualizer.nodes;

import com.nurflugel.util.antscriptvisualizer.nodes.paths.Path;

/** Representation of a Path element. */

// <pathelement path="${classpath}"/>
// <pathelement location="classes"/>
// are path and location the same thing???
public class PathElement<T>
{
  Path   path;
  String location;

  public PathElement(Path path)
  {
    this.path = path;
  }

  public PathElement(String location)
  {
    this.location = location;
  }
}
