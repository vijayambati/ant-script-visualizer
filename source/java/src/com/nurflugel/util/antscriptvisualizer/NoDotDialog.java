package com.nurflugel.util.antscriptvisualizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;


/** Dialog to find where GraphViz is located. */
public class NoDotDialog extends JDialog
{

    /** Use serialVersionUID for interoperability. */
    private static final long serialVersionUID = 6408989705672527137L;
    private File       file;
    private JButton    buttonCancel;
    private JButton    buttonOK;
    private JButton    useTextBoxButton;
    private JPanel     contentPane;
    private JTextField pathTextField;


    public NoDotDialog(String dotExecutablePath)
    {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        pathTextField.setText(dotExecutablePath);

        buttonOK.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e)
                {
                    onOK();
                }
            });

        useTextBoxButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e)
                {
                    file = new File(pathTextField.getText());
                    dispose();
                }
            });

        buttonCancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e)
                {
                    onCancel();
                }
            });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e)
                {
                    onCancel();
                }
            });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
                public void actionPerformed(ActionEvent e)
                {
                    onCancel();
                }
            }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        pack();
        center();
        show();
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

    /**  */
    private void onCancel()
    {

        // add your code here if necessary
        dispose();
    }

    /**  */
    private void onOK()
    {

        JFileChooser fileChooser = new JFileChooser();
        int          result      = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
        }

        dispose();
    }

    /**  */
    public static void main(String[] args)
    {
        NoDotDialog dialog = new NoDotDialog("Test message for not finding path");

        dialog.pack();
        dialog.show();
        System.exit(0);
    }

    public File getFile() { return file; }
}
