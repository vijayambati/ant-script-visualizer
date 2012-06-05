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

  {
    // GUI initializer generated by IntelliJ IDEA GUI Designer
    // >>> IMPORTANT!! <<<
    // DO NOT EDIT OR ADD ANY CODE HERE!
    $$$setupUI$$$();
  }

  /**
   * Method generated by IntelliJ IDEA GUI Designer >>> IMPORTANT!! <<< DO NOT edit this method OR call it in your code!
   *
   * @noinspection  ALL
   */
  private void $$$setupUI$$$()
  {
    mainPanel = new JPanel();
    mainPanel.setLayout(new GridBagLayout());

    final JPanel panel1 = new JPanel();

    panel1.setLayout(new GridBagLayout());

    GridBagConstraints gbc;

    gbc         = new GridBagConstraints();
    gbc.gridx   = 0;
    gbc.gridy   = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill    = GridBagConstraints.BOTH;
    mainPanel.add(panel1, gbc);
    parseFileOptionsPanel = new JPanel();
    parseFileOptionsPanel.setLayout(new GridBagLayout());
    gbc         = new GridBagConstraints();
    gbc.gridx   = 0;
    gbc.gridy   = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill    = GridBagConstraints.BOTH;
    panel1.add(parseFileOptionsPanel, gbc);
    parseFileOptionsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Rendering options"));
    includeImportedFilesCheckbox = new JCheckBox();
    includeImportedFilesCheckbox.setSelected(true);
    includeImportedFilesCheckbox.setText("Include imported Ant files");
    includeImportedFilesCheckbox.setToolTipText("Include any Ant files referenced in <import> tasks");
    gbc         = new GridBagConstraints();
    gbc.gridx   = 0;
    gbc.gridy   = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor  = GridBagConstraints.WEST;
    parseFileOptionsPanel.add(includeImportedFilesCheckbox, gbc);
    groupNodesByBuildfileCheckbox = new JCheckBox();
    groupNodesByBuildfileCheckbox.setSelected(true);
    groupNodesByBuildfileCheckbox.setText("Group nodes by buildfiles");
    gbc         = new GridBagConstraints();
    gbc.gridx   = 0;
    gbc.gridy   = 1;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor  = GridBagConstraints.WEST;
    parseFileOptionsPanel.add(groupNodesByBuildfileCheckbox, gbc);
    concentrateCheckbox = new JCheckBox();
    concentrateCheckbox.setSelected(true);
    concentrateCheckbox.setText("Concentrate lines");
    gbc         = new GridBagConstraints();
    gbc.gridx   = 0;
    gbc.gridy   = 2;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor  = GridBagConstraints.WEST;
    parseFileOptionsPanel.add(concentrateCheckbox, gbc);
    showFilePathsCheckBox = new JCheckBox();
    showFilePathsCheckBox.setText("Show file paths");
    showFilePathsCheckBox.setToolTipText("Show file paths for all files");
    gbc         = new GridBagConstraints();
    gbc.gridx   = 0;
    gbc.gridy   = 3;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor  = GridBagConstraints.WEST;
    parseFileOptionsPanel.add(showFilePathsCheckBox, gbc);
    showLegendCheckBox = new JCheckBox();
    showLegendCheckBox.setSelected(true);
    showLegendCheckBox.setText("Include a legend");
    gbc         = new GridBagConstraints();
    gbc.gridx   = 0;
    gbc.gridy   = 4;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor  = GridBagConstraints.WEST;
    parseFileOptionsPanel.add(showLegendCheckBox, gbc);

    final JPanel panel2 = new JPanel();

    panel2.setLayout(new GridBagLayout());
    gbc         = new GridBagConstraints();
    gbc.gridx   = 1;
    gbc.gridy   = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill    = GridBagConstraints.BOTH;
    panel1.add(panel2, gbc);
    panel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Include options"));
    showMacrodefsCheckbox = new JCheckBox();
    showMacrodefsCheckbox.setSelected(true);
    showMacrodefsCheckbox.setText("Show macrodefs");
    showMacrodefsCheckbox.setToolTipText("Include macrodefs in graph");
    gbc         = new GridBagConstraints();
    gbc.gridx   = 0;
    gbc.gridy   = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor  = GridBagConstraints.WEST;
    panel2.add(showMacrodefsCheckbox, gbc);
    showTaskdefsCheckBox = new JCheckBox();
    showTaskdefsCheckBox.setSelected(true);
    showTaskdefsCheckBox.setText("Show taskdefs");
    showTaskdefsCheckBox.setToolTipText("Include taskdefs in graph");
    gbc         = new GridBagConstraints();
    gbc.gridx   = 0;
    gbc.gridy   = 1;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor  = GridBagConstraints.WEST;
    panel2.add(showTaskdefsCheckBox, gbc);
    showAntcallsCheckbox = new JCheckBox();
    showAntcallsCheckbox.setSelected(true);
    showAntcallsCheckbox.setText("Show Ant and Antcalls");
    gbc         = new GridBagConstraints();
    gbc.gridx   = 0;
    gbc.gridy   = 2;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor  = GridBagConstraints.WEST;
    panel2.add(showAntcallsCheckbox, gbc);
    showTargetsCheckbox = new JCheckBox();
    showTargetsCheckbox.setSelected(true);
    showTargetsCheckbox.setText("Show targets");
    showTargetsCheckbox.setToolTipText("Show dependencies on other targets in graph");
    gbc         = new GridBagConstraints();
    gbc.gridx   = 0;
    gbc.gridy   = 3;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor  = GridBagConstraints.WEST;
    panel2.add(showTargetsCheckbox, gbc);

    final JPanel panel3 = new JPanel();

    panel3.setLayout(new GridBagLayout());
    gbc         = new GridBagConstraints();
    gbc.gridx   = 0;
    gbc.gridy   = 1;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill    = GridBagConstraints.BOTH;
    mainPanel.add(panel3, gbc);

    final JPanel panel4 = new JPanel();

    panel4.setLayout(new GridBagLayout());
    gbc         = new GridBagConstraints();
    gbc.gridx   = 0;
    gbc.gridy   = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill    = GridBagConstraints.BOTH;
    panel3.add(panel4, gbc);
    panel4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Misc options"));
    deleteDotFilesCheckbox = new JCheckBox();
    deleteDotFilesCheckbox.setSelected(true);
    deleteDotFilesCheckbox.setText("Delete .dot files on exit");
    gbc         = new GridBagConstraints();
    gbc.gridx   = 0;
    gbc.gridy   = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor  = GridBagConstraints.WEST;
    panel4.add(deleteDotFilesCheckbox, gbc);
    filterFromNodeCheckbox = new JCheckBox();
    filterFromNodeCheckbox.setEnabled(false);
    filterFromNodeCheckbox.setText("Filter from node...");
    gbc         = new GridBagConstraints();
    gbc.gridx   = 0;
    gbc.gridy   = 1;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor  = GridBagConstraints.WEST;
    panel4.add(filterFromNodeCheckbox, gbc);
    filterThroughNodeCheckbox = new JCheckBox();
    filterThroughNodeCheckbox.setEnabled(false);
    filterThroughNodeCheckbox.setText("Filter through node...");
    gbc         = new GridBagConstraints();
    gbc.gridx   = 0;
    gbc.gridy   = 2;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor  = GridBagConstraints.WEST;
    panel4.add(filterThroughNodeCheckbox, gbc);

    final JPanel panel5 = new JPanel();

    panel5.setLayout(new GridBagLayout());
    gbc         = new GridBagConstraints();
    gbc.gridx   = 1;
    gbc.gridy   = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill    = GridBagConstraints.BOTH;
    panel3.add(panel5, gbc);
    panel5.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Output format"));
    pngRadioButton = new JRadioButton();
    pngRadioButton.setSelected(true);
    pngRadioButton.setText("PNG");
    gbc         = new GridBagConstraints();
    gbc.gridx   = 0;
    gbc.gridy   = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor  = GridBagConstraints.WEST;
    panel5.add(pngRadioButton, gbc);
    pdfRadioButton = new JRadioButton();
    pdfRadioButton.setText("PDF (OS X only)");
    gbc         = new GridBagConstraints();
    gbc.gridx   = 0;
    gbc.gridy   = 1;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor  = GridBagConstraints.WEST;
    panel5.add(pdfRadioButton, gbc);
    svgRadioButton = new JRadioButton();
    svgRadioButton.setText("SVG");
    gbc         = new GridBagConstraints();
    gbc.gridx   = 0;
    gbc.gridy   = 2;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor  = GridBagConstraints.WEST;
    panel5.add(svgRadioButton, gbc);

    final JPanel panel6 = new JPanel();

    panel6.setLayout(new GridBagLayout());
    gbc         = new GridBagConstraints();
    gbc.gridx   = 2;
    gbc.gridy   = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill    = GridBagConstraints.BOTH;
    panel3.add(panel6, gbc);

    final JPanel panel7 = new JPanel();

    panel7.setLayout(new GridBagLayout());
    panel7.setEnabled(true);
    gbc         = new GridBagConstraints();
    gbc.gridx   = 0;
    gbc.gridy   = 0;
    gbc.weightx = 1.0;
    gbc.fill    = GridBagConstraints.BOTH;
    panel6.add(panel7, gbc);
    panel7.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Node Ordering"));
    rightToLeftRadioButton = new JRadioButton();
    rightToLeftRadioButton.setEnabled(false);
    rightToLeftRadioButton.setText("Right-to-Left");
    gbc         = new GridBagConstraints();
    gbc.gridx   = 0;
    gbc.gridy   = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor  = GridBagConstraints.WEST;
    panel7.add(rightToLeftRadioButton, gbc);
    leftToRightRadioButton = new JRadioButton();
    leftToRightRadioButton.setEnabled(false);
    leftToRightRadioButton.setSelected(true);
    leftToRightRadioButton.setText("Left-to-Right");
    gbc         = new GridBagConstraints();
    gbc.gridx   = 0;
    gbc.gridy   = 1;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.anchor  = GridBagConstraints.WEST;
    panel7.add(leftToRightRadioButton, gbc);

    final JPanel panel8 = new JPanel();

    panel8.setLayout(new GridBagLayout());
    gbc         = new GridBagConstraints();
    gbc.gridx   = 0;
    gbc.gridy   = 2;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill    = GridBagConstraints.BOTH;
    mainPanel.add(panel8, gbc);
    helpButton = new JButton();
    helpButton.setText("Help");
    gbc         = new GridBagConstraints();
    gbc.gridx   = 0;
    gbc.gridy   = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill    = GridBagConstraints.HORIZONTAL;
    panel8.add(helpButton, gbc);
    selectAntFileButton = new JButton();
    selectAntFileButton.setText("Select Ant file to visualize");
    gbc         = new GridBagConstraints();
    gbc.gridx   = 1;
    gbc.gridy   = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill    = GridBagConstraints.HORIZONTAL;
    panel8.add(selectAntFileButton, gbc);
    findDotButton = new JButton();
    findDotButton.setText("Find Dot");
    gbc         = new GridBagConstraints();
    gbc.gridx   = 2;
    gbc.gridy   = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill    = GridBagConstraints.HORIZONTAL;
    panel8.add(findDotButton, gbc);
    quitButton = new JButton();
    quitButton.setText("Quit");
    gbc         = new GridBagConstraints();
    gbc.gridx   = 3;
    gbc.gridy   = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill    = GridBagConstraints.HORIZONTAL;
    panel8.add(quitButton, gbc);
    statusLabel = new JLabel();
    statusLabel.setHorizontalAlignment(0);
    statusLabel.setHorizontalTextPosition(0);
    statusLabel.setText(" ");
    gbc         = new GridBagConstraints();
    gbc.gridx   = 0;
    gbc.gridy   = 3;
    gbc.weightx = 1.0;
    gbc.fill    = GridBagConstraints.HORIZONTAL;
    mainPanel.add(statusLabel, gbc);

    ButtonGroup buttonGroup;

    buttonGroup = new ButtonGroup();
    buttonGroup.add(pngRadioButton);
    buttonGroup.add(pdfRadioButton);
    buttonGroup.add(svgRadioButton);
    buttonGroup = new ButtonGroup();
    buttonGroup.add(rightToLeftRadioButton);
    buttonGroup.add(leftToRightRadioButton);
  }

  /** @noinspection  ALL */
  public JComponent $$$getRootComponent$$$()
  {
    return mainPanel;
  }
}
