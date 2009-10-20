/*
 * Created by IntelliJ IDEA. User: douglasbullard Date: Nov 27, 2004 Time: 3:38:03 PM
 */
package com.nurflugel.util.antscriptvisualizer.nodes;

/**
 * This is a dependency - it extends Target, but has a "resolved" attribute. This is because although all depencencies might be detected during
 * initial parsing of a target, they might not be fully resolved, and might need to be later.
 *
 * <p>For instance, if you have an imported buildfile, any calls to a target in that buildfile won't get resolved immediately. So, you need to know
 * which targets are fully resolved. After you've done that, any unresolved dependencies are probably lurking in other buildfiles - but it's a simple
 * matter to get those and set the proper buildfile in the depencency.</p>
 */
public class Dependency extends Target
{
  private boolean resolved;

  public Dependency(String name, Antfile antfile)
  {
    super(name, antfile);
  }

  public Dependency(String name, String description, Antfile antfile)
  {
    super(name, description, antfile);
  }

  @Override
  @SuppressWarnings({ "RefusedBequest" })
  public boolean isDependency()
  {
    return true;
  }

  /** Has this dependency been resolved? */
  public boolean isResolved()
  {
    return resolved;
  }

  public void setResolved(boolean resolved)
  {
    this.resolved = resolved;
  }

  public void setBuildFile(Antfile antfile)
  {
    buildFile = antfile;
  }
}
