/*
 * Created by IntelliJ IDEA. User: douglasbullard Date: Nov 26, 2004 Time: 5:11:30 PM
 */
package com.nurflugel.util.antscriptvisualizer;

import com.nurflugel.util.Os;
import com.nurflugel.util.OutputFormat;
import org.apache.log4j.Logger;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import static com.nurflugel.util.Os.OS_X;
import static com.nurflugel.util.Os.findOs;
import static com.nurflugel.util.OutputFormat.*;
import static com.nurflugel.util.Util.*;
import static java.awt.Cursor.*;
import static javax.swing.JFileChooser.APPROVE_OPTION;
import static javax.swing.JOptionPane.showMessageDialog;

/** The main UI class and entry point for the app. */
@SuppressWarnings({ "BooleanMethodNameMustStartWithQuestion", "CallToSystemExit" })
public class AntParserUiImpl implements AntParserUi
{
  public static final String  HELP_HS                       = "help.hs";
  public static final String  VERSION                       = "3.0.6";
  private static final Logger LOGGER                        = Logger.getLogger(AntParserUiImpl.class);
  private Cursor              normalCursor                  = getPredefinedCursor(DEFAULT_CURSOR);
  private Cursor              busyCursor                    = getPredefinedCursor(WAIT_CURSOR);
  private JButton             findDotButton;
  private JButton             helpButton;
  private JButton             quitButton;
  private JButton             selectAntFileButton;
  private JButton             selectDirButton;
  private JCheckBox           concentrateCheckbox;
  private JCheckBox           deleteDotFilesCheckbox;
  private JCheckBox           filterFromNodeCheckbox;
  private JCheckBox           filterThroughNodeCheckbox;
  private JCheckBox           groupNodesByBuildfileCheckbox;
  private JCheckBox           includeImportedFilesCheckbox;
  private JCheckBox           showAntcallsCheckbox;
  private JCheckBox           showFilePathsCheckBox;
  private JCheckBox           showLegendCheckBox;
  private JCheckBox           showMacrodefsCheckbox;
  private JCheckBox           showTargetsCheckbox;
  private JCheckBox           showTaskdefsCheckBox;
  private JFrame              frame;
  private JLabel              statusLabel;
  private JPanel              mainPanel;
  private JPanel              parseFileOptionsPanel;
  private JRadioButton        leftToRightRadioButton;
  private JRadioButton        pdfRadioButton;
  private JRadioButton        pngRadioButton;
  private JRadioButton        rightToLeftRadioButton;
  private JRadioButton        svgRadioButton;
  private Os                  os;
  private String              dotExecutablePath;
  private Preferences         preferences;

  /** Creates a new AntParserUi object. */
  public AntParserUiImpl()
  {
    LOGGER.error("Bad programmer, no donut!");
    os                = findOs();
    preferences       = new Preferences();
    dotExecutablePath = preferences.getDotExecutablePath();  // todo this is ugly, fix it somehow

    if ((dotExecutablePath == null) || (dotExecutablePath.length() == 0))
    {
      dotExecutablePath = os.getDefaultDotPath();
    }

    preferences.setDotExecutablePath(dotExecutablePath);
    frame = new JFrame();
    frame.setContentPane(mainPanel);
    initializeUi();
    setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel", frame);
    frame.pack();
    center(frame);
    frame.setTitle("Ant Script Visualizer v" + VERSION);
    frame.setVisible(true);
    showNewStuff();
    setDefaultDotLocation();
  }

  public static void main(String[] args)
  {
    AntParserUi ui = new AntParserUiImpl();
  }

  // ------------------------ OTHER METHODS ------------------------
  @Override
  public JFrame getFrame()
  {
    return frame;
  }

  @Override
  public OutputFormat getOutputFormat()
  {
    if (svgRadioButton.isSelected())
    {
      return SVG;
    }

    if (pdfRadioButton.isSelected())
    {
      return PDF;
    }

    return PNG;
  }

  private void addActionListeners()
  {
    frame.addWindowListener(new WindowAdapter()
      {
        @Override
        public void windowClosing(WindowEvent e)
        {
          doQuitAction();
        }
      });

    // todo ad listeners to deal with selections ->> preferences
    selectAntFileButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent event)
        {
          doSelectAntFileAction();
        }
      });
    findDotButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent event)
        {
          findDotExecutablePath();
        }
      });
    quitButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent event)
        {
          doQuitAction();
        }
      });
    addHelpListener(HELP_HS, helpButton, frame);
  }

  private void doHelpAction()
  {
    // todo
  }

  private void doParseAntFileRadiobuttonAction()
  {
    parseFileOptionsPanel.setVisible(true);
  }

  private void doQuitAction()
  {
    getOutputPreferencesFromUi();
    preferences.save();
    System.exit(0);
  }

  private void getOutputPreferencesFromUi()
  {
    preferences.setShouldGroupByBuildfiles(groupNodesByBuildfileCheckbox.isSelected());
    preferences.setShouldIncludeImportedFiles(includeImportedFilesCheckbox.isSelected());
    preferences.setShouldConcentrate(concentrateCheckbox.isSelected());
    preferences.setShouldShowMacrodefs(showMacrodefsCheckbox.isSelected());
    preferences.setShouldShowAntCalls(showAntcallsCheckbox.isSelected());
    preferences.setShouldShowTargets(showTargetsCheckbox.isSelected());
    preferences.setShouldDeleteDotFilesOnExit(deleteDotFilesCheckbox.isSelected());
    preferences.setOutputFormat(getOutputFormat());
    preferences.setShouldUseAbsolutePaths(showFilePathsCheckBox.isSelected());
    preferences.setShouldShowTaskdefs(showTaskdefsCheckBox.isSelected());
    preferences.setShouldShowLegend(showLegendCheckBox.isSelected());
  }

  private void doSelectAntFileAction()
  {
    if (!showMacrodefsCheckbox.isSelected() && !showTaskdefsCheckBox.isSelected() && !showTargetsCheckbox.isSelected()
          && !showAntcallsCheckbox.isSelected())
    {
      showMessageDialog(frame,
                        "Sorry, you must have one of \"Show targets\", \"Show macrodefs\", \n\""
                          + "Show taskdefs\", or \"Show Antcalls\" checked to do this");
    }
    else
    {
      getOutputPreferencesFromUi();
      frame.setCursor(busyCursor);
      updateStatusLabel(null);

      JFileChooser      fileChooser = new JFileChooser();
      ExampleFileFilter filter      = new ExampleFileFilter();

      filter.addExtension("xml");
      filter.addExtension("build");
      filter.setDescription("Ant build files");
      fileChooser.setFileFilter(filter);

      String lastDir = preferences.getLastDir();

      if (lastDir != null)
      {
        fileChooser.setCurrentDirectory(new File(lastDir));
      }

      fileChooser.setMultiSelectionEnabled(true);

      int returnVal = fileChooser.showOpenDialog(frame);

      if (returnVal == APPROVE_OPTION)
      {
        File[] selectedFiles = fileChooser.getSelectedFiles();

        preferences.setLastDir(selectedFiles[0].getParent());

        AntFileParser fileParser = new AntFileParser(os, preferences, this, selectedFiles);

        try
        {
          fileParser.processBuildFile(true);
        }
        catch (Exception e)
        {
          e.printStackTrace();  // todo message dialog here
        }
      }

      frame.setCursor(normalCursor);
      statusLabel.setText("Operations completed.  Check your directory for graphic files.");
      frame.pack();
    }                           // end if-else
  }

  public void updateStatusLabel(Integer count)
  {
    statusLabel.setText("Parsing Ant files... " + count);
  }

  private void doSelectDirAction()
  {
    // todo
  }

  private void findDotExecutablePath()
  {
    dotExecutablePath = preferences.getDotExecutablePath();

    if ((dotExecutablePath == null) || (dotExecutablePath.length() == 0))
    {
      dotExecutablePath = os.getDefaultDotPath();
    }

    NoDotDialog dialog            = new NoDotDialog(dotExecutablePath);
    File        dotExecutableFile = dialog.getFile();

    if (dotExecutableFile != null)
    {
      dotExecutablePath = dotExecutableFile.getAbsolutePath();
      preferences.setDotExecutablePath(dotExecutablePath);
    }
    else
    {
      showMessageDialog(frame, "Sorry, this program can't run without the GraphViz installation.\n" + "  Please install that and try again");
      doQuitAction();
    }
  }

  private void initializeUi()
  {
    // $$$setupUI$$$();
    addActionListeners();
    doParseAntFileRadiobuttonAction();
    groupNodesByBuildfileCheckbox.setSelected(preferences.shouldGroupByBuildfiles());
    includeImportedFilesCheckbox.setSelected(preferences.shouldIncludeImportedFiles());
    concentrateCheckbox.setSelected(preferences.shouldConcentrate());
    showMacrodefsCheckbox.setSelected(preferences.shouldShowMacrodefs());
    showTaskdefsCheckBox.setSelected(preferences.shouldShowTaskdefs());
    showAntcallsCheckbox.setSelected(preferences.shouldShowAntcalls());
    showTargetsCheckbox.setSelected(preferences.shouldShowTargets());
    deleteDotFilesCheckbox.setSelected(preferences.shouldDeleteDotFilesOnExit());
    showFilePathsCheckBox.setSelected(preferences.shouldUseAbsolutePaths());

    OutputFormat outputFormat = preferences.getOutputFormat();

    switch (outputFormat)
    {
      case PDF:
        pdfRadioButton.setSelected(true);
        break;

      case PNG:
        pngRadioButton.setSelected(true);
        break;

      case SVG:
        svgRadioButton.setSelected(true);
    }

    // preferences.put(OUTPUT_FORMAT, getOutputFormat().toString());
    if (os == OS_X)
    {
      pdfRadioButton.setSelected(true);
    }
  }

  private void setDefaultDotLocation()
  {
    dotExecutablePath = preferences.getDotExecutablePath();

    if ((dotExecutablePath == null) || (dotExecutablePath.length() == 0))
    {
      dotExecutablePath = os.getDefaultDotPath();
    }
  }

  /** If they're running a new version, show a dialog showing the new stuff. */
  private void showNewStuff()
  {
    Version previousVersion = new Version(preferences.getPreviousVersion());

    // Version previousVersion = new Version("0.0.0");
    String currentVersionString = VERSION;

    preferences.setPreviousVersion(currentVersionString);

    // preferences.put(VERSION,"0.0.0");
    Version currentVersion = new Version(currentVersionString);

    if (currentVersion.compareTo(previousVersion) != 0)
    {
      WhatsNewDialog whatsNewDialog = new WhatsNewDialog(frame, previousVersion);

      whatsNewDialog.showIfNeeded();
    }
  }
}
