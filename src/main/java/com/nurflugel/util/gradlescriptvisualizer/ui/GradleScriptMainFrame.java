package com.nurflugel.util.gradlescriptvisualizer.ui;

import com.nurflugel.util.Os;
import com.nurflugel.util.gradlescriptvisualizer.Preferences;
import com.nurflugel.util.gradlescriptvisualizer.domain.Task;
import com.nurflugel.util.gradlescriptvisualizer.output.DotFileGenerator;
import com.nurflugel.util.gradlescriptvisualizer.parser.GradleFileParser;
import org.apache.commons.lang.StringUtils;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import static com.nurflugel.util.Os.findOs;
import static com.nurflugel.util.Util.center;
import static com.nurflugel.util.Util.setLookAndFeel;
import static javax.swing.JFileChooser.APPROVE_OPTION;
import static org.apache.commons.lang.StringUtils.isBlank;

/** Created with IntelliJ IDEA. User: douglas_bullard Date: 6/3/12 Time: 14:32 To change this template use File | Settings | File Templates. */
public class GradleScriptMainFrame
{
  private JButton      selectGradleScriptButton;
  private JCheckBox    watchFileForChangesCheckBox;
  private JRadioButton generateJustDOTFilesRadioButton;
  private JRadioButton generatePNGPDFFilesRadioButton;
  private JPanel       mainPanel;
  private JButton      quitButton;
  private JFrame       frame;
  private Preferences  preferences;
  private String       dotExecutablePath;
  private Os           os;

  public GradleScriptMainFrame()
  {
    os    = findOs();
    frame = new JFrame();
    frame.setContentPane(mainPanel);
    initializeUi();
    selectGradleScriptButton.addActionListener(new ActionListener()
      {
        @Override
        public void actionPerformed(ActionEvent actionEvent)
        {
          try
          {
            selectGradleScript();
          }
          catch (IOException e)
          {
            e.printStackTrace();
          }
        }
      });
    quitButton.addActionListener(new ActionListener()
      {
        @Override
        public void actionPerformed(ActionEvent actionEvent)
        {
          doQuitAction();
        }
      });
    preferences       = new Preferences();
    dotExecutablePath = preferences.getDotExecutablePath();  // todo this is ugly, fix it somehow

    if (isBlank(dotExecutablePath))
    {
      dotExecutablePath = os.getDefaultDotPath();
    }

    preferences.setDotExecutablePath(dotExecutablePath);
  }

  private void initializeUi()
  {
    setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel", frame);
    frame.pack();
    center(frame);

    // frame.setTitle("Gradle Script Visualizer v" + VERSION);
    frame.setTitle("Gradle Script Visualizer ");
    frame.setVisible(true);
  }

  private void selectGradleScript() throws IOException
  {
    JFileChooser chooser = new JFileChooser();
    String       lastDir = preferences.getLastDir();

    if (lastDir != null)
    {
      chooser.setCurrentDirectory(new File(lastDir));
    }

    chooser.setFileFilter(new FileNameExtensionFilter("Gradle scripts", "gradle", "groovy"));
    chooser.setMultiSelectionEnabled(true);

    int returnVal = chooser.showOpenDialog(frame);

    if (returnVal == APPROVE_OPTION)
    {
      File[] selectedFiles = chooser.getSelectedFiles();

      if (selectedFiles.length > 0)
      {
        preferences.setLastDir(selectedFiles[0].getParent());
      }

      GradleFileParser parser = new GradleFileParser();

      for (File selectedFile : selectedFiles)
      {
        parser.parseFile(selectedFile);
        System.out.println("selectedFile = " + selectedFile);

        List<Task>       tasks            = parser.getTasks();
        DotFileGenerator dotFileGenerator = new DotFileGenerator();
        List<String>     lines            = dotFileGenerator.createOutput(tasks);
        File             dotFile          = dotFileGenerator.writeOutput(lines, selectedFile.getAbsolutePath());

        try
        {
          os.openFile(dotFile.getAbsolutePath());
        }
        catch (InvocationTargetException e)
        {
          e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
          e.printStackTrace();
        }
        catch (NoSuchMethodException e)
        {
          e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
          e.printStackTrace();
        }
      }

      // preferences.setLastDir(selectedFiles[0].getParent());
      // AntFileParser fileParser = new AntFileParser(os, preferences, this, selectedFiles);
      try
      {
        // fileParser.processBuildFile(true);
      }
      catch (Exception e)
      {
        e.printStackTrace();  // todo message dialog here
      }
    }
  }

  private void doQuitAction()
  {
    getOutputPreferencesFromUi();
    preferences.save();
    System.exit(0);
  }

  private void getOutputPreferencesFromUi() {}

  // --------------------------- main() method ---------------------------
  public static void main(String[] args)
  {
    GradleScriptMainFrame ui = new GradleScriptMainFrame();
  }
}
