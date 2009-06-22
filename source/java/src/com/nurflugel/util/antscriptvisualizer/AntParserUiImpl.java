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
        os          = System.getProperty("os.name");
        preferences = Preferences.userNodeForPackage(AntParserUiImpl.class);
        frame       = new JFrame();
        setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel", frame);
        initializeUi();
        frame.setContentPane(mainPanel);
        frame.pack();
        center();
        showNewStuff();
        frame.show();
        setDefaultDotLocation();
    }

    public static void main(String[] args)
    {
        AntParserUi ui = new AntParserUiImpl();
    }
    // ------------------------ OTHER METHODS ------------------------

    /** Get the dot executable path if it already exists in Preferences, or is intalled. If not easily findable, as the user where the hell he put it. */
    public String getDotExecutablePath()
    {
        return dotExecutablePath;
    }

    public boolean showLegend()
    {
        return showLegendCheckBox.isSelected();
    }

    public JFrame getFrame()
    {
        return frame;
    }

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

    public boolean shouldConcentrate()
    {
        return concentrateCheckbox.isSelected();
    }

    public boolean shouldDeleteDotFilesOnExit()
    {
        return deleteDotFilesCheckbox.isSelected();
    }

    public boolean shouldGroupByBuildfiles()
    {
        return groupNodesByBuildfileCheckbox.isSelected();
    }

    public boolean shouldIncludeImportedFiles()
    {
        return includeImportedFilesCheckbox.isSelected();
    }

    public boolean shouldShowAntcalls()
    {
        return showAntcallsCheckbox.isSelected();
    }

    public boolean shouldShowLeftToRight()
    {
        return leftToRightRadioButton.isSelected();
    }

    public boolean shouldShowMacrodefs()
    {
        return showMacrodefsCheckbox.isSelected();
    }

    public boolean shouldShowTargets()
    {
        return showTargetsCheckbox.isSelected();
    }

    public boolean shouldShowTaskdefs()
    {
        return showTaskdefsCheckBox.isSelected();
    }

    public boolean shouldUseAbsolutePaths()
    {
        return showFilePathsCheckBox.isSelected();
    }

    private void addActionListeners()
    {
        frame.addWindowListener(new WindowAdapter()
            {
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
        catch (Exception ee)
        {  // Say what the exception really is
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
        if (!showMacrodefsCheckbox.isSelected() && !showTaskdefsCheckBox.isSelected() && !showTargetsCheckbox.isSelected() && !showAntcallsCheckbox.isSelected())
        {
            JOptionPane.showMessageDialog(frame, "Sorry, you must have one of \"Show targets\", \"Show macrodefs\", \n\"" + "Show taskdefs\", or \"Show Antcalls\" checked to do this");
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
        }  // end if-else
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
            JOptionPane.showMessageDialog(frame, "Sorry, this program can't run without the GraphViz installation.\n" + "  Please install that and try again");
            doQuitAction();
        }
    }

    /** Sets the look and feel. */
    public static void setLookAndFeel(String feelName, Component component)
    {
        try
        {
            UIManager.setLookAndFeel(feelName);
            SwingUtilities.updateComponentTreeUI(component);
        }
        catch (Exception e)
        {
            System.out.println("Error setting native LAF: " + feelName + e.getMessage());
        }
    }

    private void initializeUi()
    {
        $$$setupUI$$$();

        ButtonGroup outputOptionsGroup = new ButtonGroup();

        outputOptionsGroup.add(svgRadioButton);
        outputOptionsGroup.add(pngRadioButton);
        outputOptionsGroup.add(pdfRadioButton);

        ButtonGroup orderingGroup = new ButtonGroup();

        orderingGroup.add(leftToRightRadioButton);
        orderingGroup.add(rightToLeftRadioButton);
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

    {
        // GUI initializer generated by IntelliJ IDEA GUI Designer
        // >>> IMPORTANT!! <<<
        // DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer >>> IMPORTANT!! <<< DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
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
    }

    /** @noinspection ALL */
    public JComponent $$$getRootComponent$$$()
    {
        return mainPanel;
    }
}
