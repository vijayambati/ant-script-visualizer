package com.nurflugel.util.antscriptvisualizer;

import java.awt.*;
import java.io.File;
import static com.nurflugel.util.antscriptvisualizer.OutputFormat.PDF;
import static com.nurflugel.util.antscriptvisualizer.OutputFormat.PNG;

/** Enum of operating systems, and methods to deal with differenes between them. */
@SuppressWarnings({ "EnumeratedClassNamingConvention", "EnumeratedConstantNamingConvention" })
public enum Os
{
  OS_X   ("Mac OS X", "build.sh", new String[] {}, "javax.swing.plaf.mac.MacLookAndFeel", "/Applications/Graphviz.app/Contents/MacOS/dot", PDF),
  WINDOWS("Windows", "build.cmd", new String[] { "cmd.exe", "/C" }, "com.sun.java.swing.plaf.windows.WindowsLookAndFeel",
          "\"C:\\Program Files\\Graphviz2.24\\bin\\dot.exe\"", PNG);

  private String       name;
  private String       buildCommand;
  private String[]     baseCommandArgs;
  private String       lookAndFeel;
  private String       defaultDotPath;
  private OutputFormat outputFormat;

  // -------------------------- STATIC METHODS --------------------------

  public static Os findOs()
  {
    String osName = System.getProperty("os.name");
    Os[]   oses   = values();

    for (Os ose : oses)
    {
      if (osName.toLowerCase().startsWith(ose.getName().toLowerCase()))
      {
        return ose;
      }
    }

    return WINDOWS;
  }

  // --------------------------- CONSTRUCTORS ---------------------------
  Os(String name, String buildCommand, String[] baseCommandArgs, String lookAndFeel, String defaultDotPath, OutputFormat outputFormat)
  {
    this.name            = name;
    this.buildCommand    = buildCommand;
    this.baseCommandArgs = baseCommandArgs;
    this.lookAndFeel     = lookAndFeel;
    this.defaultDotPath  = defaultDotPath;
    this.outputFormat    = outputFormat;
  }

  // -------------------------- OTHER METHODS --------------------------

  public String getBuildCommandPath(String basePath)
  {
    return basePath + File.separator + buildCommand;
  }

  // --------------------- GETTER / SETTER METHODS ---------------------

  public String getDefaultDotPath()
  {
    return defaultDotPath;
  }

  public String getName()
  {
    return name;
  }

  public OutputFormat getOutputFormat()
  {
    return outputFormat;
  }

  @SuppressWarnings({ "CallToPrintStackTrace", "OverlyBroadCatchBlock" })
  public void setLookAndFeel(Component component)
  {
    if (lookAndFeel.length() > 0)
    {
      Util.setLookAndFeel(lookAndFeel, component);
    }
  }
}
