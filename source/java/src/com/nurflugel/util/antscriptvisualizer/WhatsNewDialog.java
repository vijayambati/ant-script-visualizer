package com.nurflugel.util.antscriptvisualizer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/**
 * This dialog appears when a new version arrives and you haven't seen the new features dialog yet.
 *
 * <p>todo add button to "show all changes", add button to main UI to never show</p>
 */
public class WhatsNewDialog extends JDialog
{
  /** Use serialVersionUID for interoperability. */
  private static final long serialVersionUID = -7886919499358184005L;
  private Container         contentPane      = getContentPane();
  private JButton           closeButton;
  private JPanel            mainPanel;
  private JTextPane         textPane;
  private List<Version>     versionHistory   = new ArrayList<Version>();
  private Version           latestVersion;
  private Version           previousVersion;

  public WhatsNewDialog(Frame frame, Version previousVersion)
  {
    super(frame, true);  // todo - make a subroutine to parse string line and automatically break it up with \n's and padding spaces
    this.previousVersion = previousVersion;
    latestVersion        = new Version("0.5");
    latestVersion.setFeatures(new String[] { "First whack at this" });
    versionHistory.add(latestVersion);
    latestVersion = new Version("1.0");
    latestVersion.setFeatures(new String[] { "Added Ant and AntCalls", "Added optional delete dot files on exit", });
    versionHistory.add(latestVersion);

    //
    latestVersion = new Version("1.1");
    latestVersion.setFeatures(new String[]
                              {
                                "Added ability to find 'dot' if in non-standard location",
                                "Now displays the graphic automatically using system's default application", "Fixed bug for antcalls calls",
                                "Cleans up temp files"
                              });
    versionHistory.add(latestVersion);

    //
    latestVersion = new Version("1.2");
    latestVersion.setFeatures(new String[]
                              {
                                "Added this \"What's new\" feature", "Fixed bug for ant calls ",
                                "Fixed bug for ant and antcall calls - \n" + "     dotted line wasn't dotted (make a different color?)"
                              });
    versionHistory.add(latestVersion);

    //
    latestVersion = new Version("1.3.0");
    latestVersion.setFeatures(new String[]
                              {
                                "Fixed still yet another bug for ant and antcall calls - \n     "
                                  + "dotted line is now solid (didn't show right on png)"
                              });
    versionHistory.add(latestVersion);

    //
    latestVersion = new Version("1.3.10");
    latestVersion.setFeatures(new String[] { "Improved help documentation ", "Dotting of the lines for Ant and Antcalls in additoin to coloring" });
    versionHistory.add(latestVersion);

    //
    latestVersion = new Version("1.3.11");
    latestVersion.setFeatures(new String[] { "Resolved dependencies in straightforward imports." });
    versionHistory.add(latestVersion);

    //
    latestVersion = new Version("1.3.17");
    latestVersion.setFeatures(new String[]
                              {
                                "Fixed Runtime.exec to work with filenames that contain spaces.",
                                "Eliminated this dialog popping up of a version increment has \n" + "      occurred but no new text is contained."
                              });
    versionHistory.add(latestVersion);

    //
    latestVersion = new Version("1.4");
    latestVersion.setFeatures(new String[] { "Fixed nasty bug in which imported files wouldn't show Ant call dependencies." });
    versionHistory.add(latestVersion);

    //
    latestVersion = new Version("1.4.5");
    latestVersion.setFeatures(new String[]
                              {
                                "Fixed bug which killed all generation if an error occurred parsing \n"
                                  + "      file name.  Now, just skips the file and shows a warning.",
                              });
    versionHistory.add(latestVersion);

    //
    latestVersion = new Version("1.5");
    latestVersion.setFeatures(new String[]
                              {
                                "AntScriptVisualizer now reads properties!  From ant (explicit) declarations,\n"
                                  + "      to properties files, it'll pick it up.  If your target/ant \n"
                                  + "      call has a property for a name or attribute, this should pick it up \n"
                                  + "      unless it's determined at Ant runtime.",
                              });
    versionHistory.add(latestVersion);
    latestVersion = new Version("2.0");
    latestVersion.setFeatures(new String[]
                              {
                                "Refactored heavily, this will allow future improvements.",
                                "Macrodefs now can have dependencies on taskdefs and other macrodefs"
                              });
    versionHistory.add(latestVersion);
    latestVersion = new Version("2.0.3");
    latestVersion.setFeatures(new String[] { "Fixed bug on ant calls where the scripts are in dir other than main script." });
    versionHistory.add(latestVersion);
    latestVersion = new Version("2.1.1");
    latestVersion.setFeatures(new String[] { "Moved to Nimbus look and feel, now that it's available on OS X" });
    versionHistory.add(latestVersion);
    latestVersion = new Version("2.5");
    latestVersion.setFeatures(new String[] { "Fixed UI bugs in WebStart and GUI design" });
    versionHistory.add(latestVersion);

    //
    // Version futureVersion = new Version("9.9.9");
    // "    Parse ant call filenames for properties",
    // "    Parse taskdefs better - go into property files, jars",
    // "    That's enough for now!"});
    // versionHistory.add(futureVersion);
    contentPane.add(mainPanel);
    setSize(550, 400);
    center();

    ////
    closeButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent actionEvent)
        {
          hide();
        }
      });
  }

  /** Only show if it's needed. */
  public void showIfNeeded()
  {
    if (latestVersion.compareTo(previousVersion) != 0)
    {
      showNewFeatures();
    }
  }

  /**  */
  private void center()
  {
    Toolkit   defaultToolkit = Toolkit.getDefaultToolkit();
    Dimension screenSize     = defaultToolkit.getScreenSize();
    int       x              = (int) ((screenSize.getWidth() - getWidth()) / 2);
    int       y              = (int) ((screenSize.getHeight() - getHeight()) / 2);

    setBounds(x, y, getWidth(), getHeight());
  }

  /** Show any new features since the last usage. */
  private void showNewFeatures()
  {
    StringBuilder newVersions = new StringBuilder();

    for (Version version : versionHistory)
    {
      if (version.compareTo(previousVersion) > 0)
      {
        if ((version.getMajor() == 9) && (version.getMinor() == 9) && (version.getPoint() == 9))
        {
          newVersions.append("Todos:\n");
        }
        else
        {
          newVersions.append("New in version ").append(version.toString()).append(":\n");
        }

        String[] features = version.getFeatures();

        for (String feature : features)
        {
          newVersions.append("    * ").append(feature).append("\n");
        }
      }
    }

    String newInformation = newVersions.toString();

    if ((newInformation != null) && (newInformation.length() > 0))
    {
      textPane.setText(newInformation);
      pack();
      show();
    }
  }
}
