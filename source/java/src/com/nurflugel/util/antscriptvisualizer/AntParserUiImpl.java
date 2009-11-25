/*
 * Created by IntelliJ IDEA. User: douglasbullard Date: Nov 26, 2004 Time: 5:11:30 PM
 */
package com.nurflugel.util.antscriptvisualizer;

import static com.nurflugel.util.antscriptvisualizer.OutputFormat.*;
import org.apache.log4j.Logger;
import java.awt.*;
import static java.awt.Cursor.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;
import java.util.prefs.Preferences;
import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.swing.*;
import static javax.swing.SwingUtilities.*;

/** The main UI class and entry point for the app. */
@SuppressWarnings({ "BooleanMethodNameMustStartWithQuestion", "CallToSystemExit" })
public class AntParserUiImpl implements AntParserUi
{
  public static final String    HELP_HS                       = "help.hs";
  public static final String    VERSION                       = "2.1.1";
  protected static final String LAST_DIR                      = "LAST_DIR";
  private static final Logger   LOGGER                        = Logger.getLogger(AntParserUiImpl.class);
  private static final String   CONCENTRATE_LINES             = "concentrateLines";
  private static final String   DELETE_DOT_FILES_ON_EXIT      = "deleteDotFilesOnExit";
  private static final String   DOT_EXECUTABLE                = "dotExecutable";
  private static final String   GROUP_NODES_BY_BUILDFILE      = "groupNodesByBuildfile";
  private static final String   INCLUDE_IMPORTED_ANT_FILES    = "includeImportedAntFiles";
  private static final String   OUTPUT_FORMAT                 = "outputFormat";
  private static final String   SHOW_ABSOLUTE_FILE_PATHS      = "show_absolute_file_paths";
  private static final String   SHOW_ANTCALLS                 = "showAntcalls";
  private static final String   SHOW_MACRODEFS                = "showMacrodefs";
  private static final String   SHOW_TARGETS                  = "showTargets";
  private static final String   SHOW_TASKDEFS                 = "showTaskdefs";
  private static final String   VERSION_FIELD                 = "version";
  private Cursor                normalCursor                  = getPredefinedCursor(DEFAULT_CURSOR);
  private Cursor                busyCursor                    = getPredefinedCursor(WAIT_CURSOR);
  private JCheckBox             concentrateCheckbox;
  private JCheckBox             deleteDotFilesCheckbox;
  private String                dotExecutablePath;
  private JCheckBox             filterFromNodeCheckbox;
  private JCheckBox             filterThroughNodeCheckbox;
  private JButton               findDotButton;
  private JFrame                frame;
  private JCheckBox             groupNodesByBuildfileCheckbox;
  private JButton               helpButton;
  private JCheckBox             includeImportedFilesCheckbox;
  private JRadioButton          leftToRightRadioButton;
  private JPanel                mainPanel;
  private String                os;
  private JPanel                parseFileOptionsPanel;
  private JRadioButton          pdfRadioButton;
  private JRadioButton          pngRadioButton;
  private Preferences           preferences;
  private JButton               quitButton;
  private JRadioButton          rightToLeftRadioButton;
  private JButton               selectAntFileButton;
  private JButton               selectDirButton;
  private JCheckBox             showAntcallsCheckbox;
  private JCheckBox             showFilePathsCheckBox;
  private JCheckBox             showMacrodefsCheckbox;
  private JCheckBox             showTargetsCheckbox;
  private JCheckBox             showTaskdefsCheckBox;
  private JLabel                statusLabel;
  private JRadioButton          svgRadioButton;
  private JCheckBox             showLegendCheckBox;

  /** Creates a new AntParserUi object. */
  public AntParserUiImpl()
  {
    LOGGER.error("Bad programmer, no donut!");
    os          = System.getProperty("os.name");
    preferences = Preferences.userNodeForPackage(AntParserUiImpl.class);
    frame       = new JFrame();
    frame.setContentPane(mainPanel);
    initializeUi();
    setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel", frame);
    frame.pack();
    center();
    showNewStuff();
    frame.setVisible(true);
    setDefaultDotLocation();
  }

  public static void main(String[] args)
  {
    AntParserUi ui = new AntParserUiImpl();
  }
  // ------------------------ OTHER METHODS ------------------------

  /**
   * Get the dot executable path if it already exists in Preferences, or is intalled. If not easily findable, as the user where the hell he put it.
   */
  @Override
  public String getDotExecutablePath()
  {
    return dotExecutablePath;
  }

  @Override
  public boolean showLegend()
  {
    return showLegendCheckBox.isSelected();
  }

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

  @Override
  public boolean shouldConcentrate()
  {
    return concentrateCheckbox.isSelected();
  }

  @Override
  public boolean shouldDeleteDotFilesOnExit()
  {
    return deleteDotFilesCheckbox.isSelected();
  }

  @Override
  public boolean shouldGroupByBuildfiles()
  {
    return groupNodesByBuildfileCheckbox.isSelected();
  }

  @Override
  public boolean shouldIncludeImportedFiles()
  {
    return includeImportedFilesCheckbox.isSelected();
  }

  @Override
  public boolean shouldShowAntcalls()
  {
    return showAntcallsCheckbox.isSelected();
  }

  @Override
  public boolean shouldShowLeftToRight()
  {
    return leftToRightRadioButton.isSelected();
  }

  @Override
  public boolean shouldShowMacrodefs()
  {
    return showMacrodefsCheckbox.isSelected();
  }

  @Override
  public boolean shouldShowTargets()
  {
    return showTargetsCheckbox.isSelected();
  }

  @Override
  public boolean shouldShowTaskdefs()
  {
    return showTaskdefsCheckBox.isSelected();
  }

  @Override
  public boolean shouldUseAbsolutePaths()
  {
    return showFilePathsCheckBox.isSelected();
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
    addHelpListener();
  }

  /** Add the help listener - link to the help files. */
  private void addHelpListener()
  {
    ClassLoader classLoader = AntParserUiImpl.class.getClassLoader();
    HelpSet     helpSet;

    try
    {
      URL hsURL = HelpSet.findHelpSet(classLoader, HELP_HS);

      helpSet = new HelpSet(null, hsURL);
    }
    catch (Exception ee)  // Say what the exception really is
    {
      LOGGER.error("HelpSet " + ee.getMessage());
      LOGGER.error("HelpSet " + HELP_HS + " not found");

      return;
    }

    // Create a HelpBroker object:
    HelpBroker                helpBroker            = helpSet.createHelpBroker();
    CSH.DisplayHelpFromSource displayHelpFromSource = new CSH.DisplayHelpFromSource(helpBroker);

    helpButton.addActionListener(displayHelpFromSource);
  }

  private void center()
  {
    Toolkit   defaultToolkit = Toolkit.getDefaultToolkit();
    Dimension screenSize     = defaultToolkit.getScreenSize();
    int       x              = (int) ((screenSize.getWidth() - frame.getWidth()) / 2);
    int       y              = (int) ((screenSize.getHeight() - frame.getHeight()) / 2);

    frame.setBounds(x, y, frame.getWidth(), frame.getHeight());
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
    preferences.putBoolean(GROUP_NODES_BY_BUILDFILE, shouldGroupByBuildfiles());
    preferences.putBoolean(INCLUDE_IMPORTED_ANT_FILES, shouldIncludeImportedFiles());
    preferences.putBoolean(CONCENTRATE_LINES, shouldConcentrate());
    preferences.putBoolean(SHOW_MACRODEFS, shouldShowMacrodefs());
    preferences.putBoolean(SHOW_TASKDEFS, shouldShowTaskdefs());
    preferences.putBoolean(SHOW_ANTCALLS, shouldShowAntcalls());
    preferences.putBoolean(SHOW_TARGETS, shouldShowTargets());
    preferences.putBoolean(DELETE_DOT_FILES_ON_EXIT, shouldDeleteDotFilesOnExit());
    preferences.put(OUTPUT_FORMAT, getOutputFormat().getDisplayLabel());
    preferences.putBoolean(SHOW_ABSOLUTE_FILE_PATHS, shouldUseAbsolutePaths());
    System.exit(0);
  }

  private void doSelectAntFileAction()
  {
    if (!showMacrodefsCheckbox.isSelected() && !showTaskdefsCheckBox.isSelected() && !showTargetsCheckbox.isSelected()
          && !showAntcallsCheckbox.isSelected())
    {
      JOptionPane.showMessageDialog(frame,
                                    "Sorry, you must have one of \"Show targets\", \"Show macrodefs\", \n\""
                                    + "Show taskdefs\", or \"Show Antcalls\" checked to do this");
    }
    else
    {
      frame.setCursor(busyCursor);
      statusLabel.setText("Parsing Ant files... ");

      JFileChooser      fileChooser = new JFileChooser();
      ExampleFileFilter filter      = new ExampleFileFilter();

      filter.addExtension("xml");
      filter.addExtension("build");
      filter.setDescription("Ant build files");
      fileChooser.setFileFilter(filter);

      String lastDir = preferences.get(LAST_DIR, "");

      if (lastDir != null)
      {
        fileChooser.setCurrentDirectory(new File(lastDir));
      }

      fileChooser.setMultiSelectionEnabled(true);

      int returnVal = fileChooser.showOpenDialog(frame);

      if (returnVal == JFileChooser.APPROVE_OPTION)
      {
        File[] selectedFiles = fileChooser.getSelectedFiles();

        preferences.put(LAST_DIR, selectedFiles[0].getParent());

        AntFileParser fileParser = new AntFileParser(this, selectedFiles);

        try
        {
          fileParser.processBuildFile();
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

  private void doSelectDirAction()
  {
    // todo
  }

  private void doSelectShowImportsAction()
  {
    parseFileOptionsPanel.setVisible(false);
    selectDirButton.setVisible(true);
  }

  private void doSelectShowMacrodefDependenciesAction()
  {
    parseFileOptionsPanel.setVisible(false);
    selectDirButton.setVisible(true);
  }

  private void findDotExecutablePath()
  {
    dotExecutablePath = preferences.get(DOT_EXECUTABLE, "");

    if ((dotExecutablePath == null) || (dotExecutablePath.length() == 0))
    {
      if (os.startsWith("Mac OS"))
      {
        dotExecutablePath = "/Applications/Graphviz.app/Contents/MacOS/dot";
      }
      else  // if (os.toLowerCase().startsWith("windows"))
      {
        dotExecutablePath = "\"C:\\Program Files\\ATT\\Graphviz\\bin\\dot.exe\"";
      }
    }

    // Create a file chooser
    NoDotDialog dialog            = new NoDotDialog(dotExecutablePath);
    File        dotExecutableFile = dialog.getFile();

    if (dotExecutableFile != null)
    {
      dotExecutablePath = dotExecutableFile.getAbsolutePath();
      preferences.put(DOT_EXECUTABLE, dotExecutablePath);
    }
    else
    {
      JOptionPane.showMessageDialog(frame,
                                    "Sorry, this program can't run without the GraphViz installation.\n" + "  Please install that and try again");
      doQuitAction();
    }
  }

  /** Sets the look and feel. */
  public static void setLookAndFeel(String feelName, Component component)
  {
    try
    {
      UIManager.setLookAndFeel(feelName);
      updateComponentTreeUI(component);
    }
    catch (Exception e)
    {
      System.out.println("Error setting native LAF: " + feelName + e.getMessage());
    }
  }

  private void initializeUi()
  {
    // $$$setupUI$$$();

    addActionListeners();
    doParseAntFileRadiobuttonAction();
    groupNodesByBuildfileCheckbox.setSelected(preferences.getBoolean(GROUP_NODES_BY_BUILDFILE, true));
    includeImportedFilesCheckbox.setSelected(preferences.getBoolean(INCLUDE_IMPORTED_ANT_FILES, true));
    concentrateCheckbox.setSelected(preferences.getBoolean(CONCENTRATE_LINES, true));
    showMacrodefsCheckbox.setSelected(preferences.getBoolean(SHOW_MACRODEFS, true));
    showTaskdefsCheckBox.setSelected(preferences.getBoolean(SHOW_TASKDEFS, true));
    showAntcallsCheckbox.setSelected(preferences.getBoolean(SHOW_ANTCALLS, true));
    showTargetsCheckbox.setSelected(preferences.getBoolean(SHOW_TARGETS, true));
    deleteDotFilesCheckbox.setSelected(preferences.getBoolean(DELETE_DOT_FILES_ON_EXIT, true));
    showFilePathsCheckBox.setSelected(preferences.getBoolean(SHOW_ABSOLUTE_FILE_PATHS, false));

    String       outputString = preferences.get(OUTPUT_FORMAT, PNG.getDisplayLabel());
    OutputFormat outputFormat;

    try
    {
      outputFormat = valueOf(outputString);
    }
    catch (IllegalArgumentException e)
    {
      outputFormat = PNG;
    }

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

    preferences.put(OUTPUT_FORMAT, getOutputFormat().toString());

    if (os.startsWith("Mac OS"))
    {
      pdfRadioButton.setSelected(true);
    }
  }

  private void setDefaultDotLocation()
  {
    dotExecutablePath = preferences.get(DOT_EXECUTABLE, "");

    if ((dotExecutablePath == null) || (dotExecutablePath.length() == 0))
    {
      if (os.startsWith("Mac OS"))
      {
        dotExecutablePath = "/Applications/Graphviz.app/Contents/MacOS/dot";
      }
      else  // if (os.toLowerCase().startsWith("windows"))
      {
        dotExecutablePath = "\"C:\\Program Files\\ATT\\Graphviz\\bin\\dot.exe\"";
      }
    }
  }

  /** If they're running a new version, show a dialog showing the new stuff. */
  private void showNewStuff()
  {
    Version previousVersion = new Version(preferences.get(VERSION_FIELD, ""));

    // Version previousVersion = new Version("0.0.0");
    String currentVersionString = VERSION;

    preferences.put(VERSION_FIELD, currentVersionString);

    // preferences.put(VERSION,"0.0.0");
    Version currentVersion = new Version(currentVersionString);

    if (currentVersion.compareTo(previousVersion) != 0)
    {
      WhatsNewDialog whatsNewDialog = new WhatsNewDialog(frame, previousVersion);

      whatsNewDialog.showIfNeeded();
    }
  }
}
