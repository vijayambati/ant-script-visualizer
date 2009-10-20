package com.nurflugel.util.antscriptvisualizer.nodes.paths;

import com.nurflugel.util.antscriptvisualizer.Constants;
import org.jdom.Attribute;
import org.jdom.Element;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/** Parent class for file sets and pattern sets. Since they share so many mathods, it made sense. */
@SuppressWarnings({ "ProtectedField", "AbstractClassWithoutAbstractMethods" })
public abstract class PathSet
{
  protected Element      element;
  protected List<String> excludes = new ArrayList<String>();
  protected List<String> includes = new ArrayList<String>();
  protected String       id;

  protected PathSet(Element element)
  {
    this.element = element;
  }

  /** This can be either from an "includes" attribute, or from a list of "includes" children. */
  protected void getExcludes()
  {
    getIncludesExcludes("excludes", "exclude", excludes);
  }

  /** Helper function. */
  private void getIncludesExcludes(String cludes, String clude, List<String> theList)
  {
    Attribute attribute = element.getAttribute(cludes);

    if (attribute != null)
    {
      String   includesText = attribute.getValue();
      String[] strings      = includesText.split(",");

      for (String string : strings)
      {
        theList.add(string);
      }
    }

    // noinspection unchecked
    List<Element> list = element.getChildren(clude);

    for (Element include : list)
    {
      Attribute nameAttribute = include.getAttribute(Constants.NAME);
      String    includeName   = nameAttribute.getValue();

      theList.add(includeName);
    }
  }

  public String getId()
  {
    return id;
  }

  /** This can be either from an "includes" attribute, or from a list of "includes" children. */
  protected void getIncludes()
  {
    getIncludesExcludes("includes", "include", includes);
  }

  /** Does this file match any of the includes? If so, is it matched by any of the exludes? */
  public boolean isFileOk(File file)
  {
    String  fileName = file.getAbsolutePath();
    boolean matched  = false;

    for (String include : includes)
    {
      include = makeRegularExpression(include);

      if (fileName.matches(include))
      {
        matched = true;

        break;
      }
    }

    if (!matched)
    {
      return false;
    }

    for (String exclude : excludes)
    {
      exclude = makeRegularExpression(exclude);

      if (fileName.matches(exclude))
      {
        return false;
      }
    }

    return true;
  }

  // something like "**/*Dibble*.java" should end up as ".*Dibble.*\.java"
  protected String makeRegularExpression(String text)
  {
    String result = text;

    // first, convert all "." to "\."
    result = result.replaceAll("\\.", "\\\\.");

    // second, convert all "**" to "*"
    result = result.replaceAll("\\*\\*", "\\*");

    // third, convert all "*" to ".*"
    result = result.replaceAll("\\*", "\\.\\*");

    return result;
  }
}
