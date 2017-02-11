//
// Copyright (c) 2017 by jbo - Josef Baro
//
// Project: jbo-soo-home-graphics
// File: Frame.java
// Created: 05.02.2017 - 10:41:12
//
package de.jbo.soo.home.graphics.converter.colors.ui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileFilter;

import de.ingmbh.sphinx.api2d.core.Core;
import de.jbo.soo.home.graphics.converter.colors.io.FileBackup;
import de.jbo.soo.home.graphics.converter.colors.io.GrxFileFilter;
import de.jbo.soo.home.graphics.converter.colors.io.TxtFileFilter;
import de.jbo.soo.home.graphics.converter.colors.logic.ConverterLogic;
import de.jbo.soo.home.graphics.converter.colors.settings.Settings;

/**
 * @author Josef Baro (jbo) <br>
 * @version 05.02.2017 jbo - created <br>
 */
public class Frame extends JFrame {

    private static final long serialVersionUID = 1L;

    private Settings settings = null;

    private JTextField textColorsFile = null;

    private JTextField textGraphicsFile = null;

    private JColorCombobox comboColorSource = null;

    private JColorCombobox comboColorTarget = null;

    private JLabel labelState = null;

    public Frame() {
        super("Color converter");
        try {
            initSettings();
            initUI();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     *
     */
    private void initSettings() {
        settings = new Settings();
    }

    /**
     * @throws UnsupportedLookAndFeelException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     */
    private void initUI() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        addWindowListener(new WindowAdapter() {
            /*
             * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.
             * WindowEvent)
             */
            @Override
            public void windowClosing(WindowEvent e) {
                settings.save();
            }
        });

        getContentPane().setLayout(new GridBagLayout());

        GridBagConstraints constraint = new GridBagConstraints();
        constraint.anchor = GridBagConstraints.NORTHWEST;
        constraint.gridheight = 1;
        constraint.gridwidth = 1;
        constraint.gridx = 0;
        constraint.gridy = 0;
        constraint.fill = GridBagConstraints.NONE;
        constraint.insets = new Insets(5, 5, 5, 5);

        initUIColorsFile(constraint);
        initUIGraphicsFile(constraint);
        initUIColorChoosers(constraint);
        initUIButtonStart(constraint);

        pack();
    }

    /**
     * @param constraint
     */
    private void initUIButtonStart(GridBagConstraints constraint) {
        constraint.gridx = 0;
        constraint.gridy++;
        constraint.gridwidth = 1;
        JButton button = new JButton("Convert!");
        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                convert();
            }

        });
        getContentPane().add(button, constraint);

        constraint.gridx += constraint.gridwidth;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.gridwidth = 4;
        labelState = new JLabel("");
        labelState.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        getContentPane().add(labelState, constraint);
    }

    private void convert() {
        String graphicsFile = settings.getLastGraphicsFile();
        FileBackup backup = new FileBackup();
        if (backup.backup(graphicsFile)) {
            ConverterLogic logic = new ConverterLogic();
            if (logic.convert(graphicsFile, comboColorSource.getSelectedColor(), comboColorTarget.getSelectedColor())) {
                labelState.setText("Graphics successfully converted!");
            } else {
                labelState.setText("Graphics was NOT converted!");
            }
        }
    }

    /**
     * @param constraint
     */
    private void initUIColorChoosers(GridBagConstraints constraint) {
        constraint.gridy = 2;
        constraint.gridx = 0;
        JLabel labelColorSource = new JLabel("Color source: ");
        getContentPane().add(labelColorSource, constraint);

        constraint.gridx = 1;
        constraint.gridwidth = 3;
        comboColorSource = new JColorCombobox();
        comboColorSource.setSelectedIndex(settings.getColorSource());
        getContentPane().add(comboColorSource, constraint);
        comboColorSource.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                settings.setColorSource(comboColorSource.getSelectedIndex());
            }
        });

        constraint.gridy = 3;
        constraint.gridx = 0;
        constraint.gridwidth = 1;
        JLabel labelColorTarget = new JLabel("Color target: ");
        getContentPane().add(labelColorTarget, constraint);

        constraint.gridx = 1;
        constraint.gridwidth = 3;
        comboColorTarget = new JColorCombobox();
        comboColorTarget.setSelectedIndex(settings.getColorTarget());
        getContentPane().add(comboColorTarget, constraint);
        comboColorTarget.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                settings.setColorTarget(comboColorTarget.getSelectedIndex());
            }
        });
    }

    /**
     * @param constraint
     */
    private void initUIGraphicsFile(GridBagConstraints constraint) {
        constraint.gridy = 1;
        constraint.gridx = 0;
        JLabel labelGraphicsFile = new JLabel("Graphics file: ");
        getContentPane().add(labelGraphicsFile, constraint);

        constraint.gridx = 1;
        constraint.gridwidth = 3;
        constraint.fill = GridBagConstraints.HORIZONTAL;
        constraint.weightx = 1.0;
        textGraphicsFile = new JTextField(64);
        textGraphicsFile.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textGraphicsFile.setEditable(false);
        textGraphicsFile.setText(settings.getLastGraphicsFile());
        getContentPane().add(textGraphicsFile, constraint);

        constraint.gridx += constraint.gridwidth;
        constraint.gridwidth = 1;
        constraint.weightx = 0;
        JButton buttonChooseGraphicsFile = new JButton("...");
        buttonChooseGraphicsFile.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                File chosen = chooseFile(settings.getLastGraphicsFile(), new GrxFileFilter());
                if (chosen != null) {
                    textGraphicsFile.setText(chosen.getName());
                    settings.setLastGraphicsFile(chosen.getAbsolutePath());
                    updateCursorPosition(textGraphicsFile);
                }
            }

        });
        getContentPane().add(buttonChooseGraphicsFile, constraint);

        updateCursorPosition(textGraphicsFile);
    }

    private void updateCursorPosition(JTextField text) {
        String content = text.getText();
        text.setAutoscrolls(true);
        text.setCaretPosition(content.length());
        if (labelState != null) {
            labelState.setText("");
        }
    }

    private File chooseFile(String toBeSelected, FileFilter filter) {
        File chosenFile = null;
        JFileChooser chooser = new JFileChooser();

        if (toBeSelected != null) {
            chooser.setSelectedFile(new File(toBeSelected));
        }
        chooser.setFileFilter(filter);
        chooser.setAcceptAllFileFilterUsed(false);
        int option = chooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            chosenFile = chooser.getSelectedFile();
        }

        return chosenFile;
    }

    /**
     * @param constraint
     */
    private void initUIColorsFile(GridBagConstraints constraint) {
        JLabel labelColorsFile = new JLabel("Colors file: ");
        getContentPane().add(labelColorsFile, constraint);

        constraint.gridx = 1;
        constraint.gridwidth = 3;
        constraint.fill = GridBagConstraints.HORIZONTAL;
        constraint.weightx = 1.0;
        textColorsFile = new JTextField(64);
        textColorsFile.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textColorsFile.setEditable(false);
        textColorsFile.setText(settings.getColorsFile());
        getContentPane().add(textColorsFile, constraint);

        if (settings.getColorsFile() != null) {
            Core.readColors(settings.getColorsFile(), null);
        }

        constraint.gridx += constraint.gridwidth;
        constraint.gridwidth = 1;
        constraint.weightx = 0;
        JButton buttonChooseColorFile = new JButton("...");
        buttonChooseColorFile.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                File chosen = chooseFile(settings.getColorsFile(), new TxtFileFilter());
                if (chosen != null) {
                    textColorsFile.setText(chosen.getName());
                    settings.setColorsFile(chosen.getAbsolutePath());
                    updateCursorPosition(textColorsFile);
                }
            }

        });
        getContentPane().add(buttonChooseColorFile, constraint);
        updateCursorPosition(textColorsFile);
    }
}
