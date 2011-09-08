/*
 * Created by IntelliJ IDEA. User: douglasbullard Date: Nov 27, 2004 Time: 3:38:03 PM
 */
package com.nurflugel.util.antscriptvisualizer.nodes;

import com.nurflugel.util.antscriptvisualizer.Preferences;

/**
 * This is a dependency - it extends Target, but has a "resolved" attribute. This is because although all dependencies might be detected during
 * initial parsing of a target, they might not be fully resolved, and might need to be later.
 *
 * <p>For instance, if you have an imported buildfile, any calls to a target in that buildfile won't get resolved immediately. So, you need to know
 * which targets are fully resolved. After you've done that, any unresolved dependencies are probably lurking in other buildfiles - but it's a simple
 * matter to get those and set the proper buildfile in the dependency.</p>
 */
public interface Dependency
{
  boolean isResolved();
  void setResolved(boolean resolved);
  String getName();
  void setBuildFile(Antfile antfile);
  String getColor();
  String getNiceName();
  String getDependencyExtraInfo();
  boolean shouldPrint(Preferences preferences);
}
