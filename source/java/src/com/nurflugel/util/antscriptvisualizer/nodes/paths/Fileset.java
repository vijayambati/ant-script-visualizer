package com.nurflugel.util.antscriptvisualizer.nodes.paths;

import org.jdom.Attribute;
import org.jdom.Element;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/** Representation of an Ant file set. */

// case 1
// <fileset dir="lib">
// //<include name="**/*.jar"/>
// //<include name="**/xerces*.jar"/>
// </fileset>
//
// or
//
// case 2
// <fileset dir="${server.src}" casesensitive="yes">
////  <patternSett id="non.test.sources">
//////    <include name="**/*.java"/>
//////    <exclude name="**/*Test*"/>
// // </patternSett>
// </fileset>
//
// also
//
// case 3
// <fileset dir="src" includes="main/" />
// also
//
// case
// <fileset dir="src" includes="main/" />

public class Fileset extends PathSet
{
  private File       dir;
  private File       file;
  private PatternSett patternSett;

    public Fileset(Element element)
  {
    super(element);

    Attribute attribute = element.getAttribute("id");

    id   = attribute.getValue();
    dir  = getFileOrDir("dir");
    file = getFileOrDir("file");

    getIncludes();
    getExcludes();
  }

  /** Gets either hte file or dir for the attribute name. */
  private File getFileOrDir(String attributeName)
  {
    File      theFile   = null;
    Attribute attribute = element.getAttribute(attributeName);

    if (attribute != null)
    {
      theFile = new File(attribute.getValue());
    }

    return theFile;
  }

  /** Get the file or list of files contained by this file set. */
  public List<File> getFiles()
  {
    List<File> files = new ArrayList<File>();

    if (file != null)
    {
      files.add(file);
    }
    else
    {
      File[] children = dir.listFiles();

      for (File child : children)
      {
        if (isFileOk(child))
        {
          files.add(child);
        }
      }
    }

    return files;
  }
}
