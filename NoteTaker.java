import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("unused")
public class NoteTaker {

    private JFrame frame;
    private JTextArea textArea;
    private JFileChooser fileChooser;
    private JComboBox<String> fontComboBox;
    private JTextField fontSizeField;
    private List<String> availableFonts;

    public NoteTaker() {
        frame = new JFrame("NoteTaker Alpha 0.2.0");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Create menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        JMenu helpMenu = new JMenu("Help");

        // Create "Open" menu item
        JMenuItem openItem = new JMenuItem("Open");
        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });
        fileMenu.add(openItem);

        // Create "Save" menu item
        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveFile();
            }
        });
        fileMenu.add(saveItem);

        // Add file menu to menu bar
        menuBar.add(fileMenu);

        // Create "Theme" menu item in Edit
        JMenu themeMenu = new JMenu("Theme");
        ButtonGroup themeGroup = new ButtonGroup();
        JRadioButtonMenuItem aeroModeItem = new JRadioButtonMenuItem("Aero");
        JRadioButtonMenuItem defaultModeItem = new JRadioButtonMenuItem("Default/Modern");

        aeroModeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAeroMode();
            }
        });
        defaultModeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setDefaultMode();
            }
        });

        themeGroup.add(aeroModeItem);
        themeGroup.add(defaultModeItem);
        themeMenu.add(aeroModeItem);
        themeMenu.add(defaultModeItem);
        editMenu.add(themeMenu);

        // Add edit menu to menu bar
        menuBar.add(editMenu);

        // Create "About" menu item in Help
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAboutDialog();
            }
        });
        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);

        // Create font selection combo box
        fontComboBox = new JComboBox<>();
        loadSystemFonts();
        fontComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateFont();
            }
        });
        menuBar.add(fontComboBox);

        // Create font size text field
        JLabel fontSizeLabel = new JLabel("Font Size:");
        fontSizeField = new JTextField("14", 3); // Default font size
        fontSizeField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateFontSize();
            }
        });
        menuBar.add(fontSizeLabel);
        menuBar.add(fontSizeField);

        frame.setJMenuBar(menuBar);
        frame.setVisible(true);
    }

    private void loadSystemFonts() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontNames = ge.getAvailableFontFamilyNames();
        for (String fontName : fontNames) {
            fontComboBox.addItem(fontName);
        }
    }

    private void openFile() {
        if (fileChooser == null) {
            fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
        }
        int returnVal = fileChooser.showOpenDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                textArea.setText(content.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveFile() {
        if (fileChooser == null) {
            fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
        }
        int returnVal = fileChooser.showSaveDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(textArea.getText());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setAeroMode() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        SwingUtilities.updateComponentTreeUI(frame);
    }

    private void setDefaultMode() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        SwingUtilities.updateComponentTreeUI(frame);
    }

    private void updateFont() {
        String selectedFont = (String) fontComboBox.getSelectedItem();
        int fontSize = Integer.parseInt(fontSizeField.getText().trim());
        Font font = new Font(selectedFont, Font.PLAIN, fontSize);
        textArea.setFont(font);
    }

    private void updateFontSize() {
        String selectedSize = fontSizeField.getText().trim().toLowerCase();
        switch (selectedSize) {
            case "title":
                textArea.setFont(textArea.getFont().deriveFont(24f));
                break;
            case "subtitle":
                textArea.setFont(textArea.getFont().deriveFont(18f));
                break;
            case "normal":
                textArea.setFont(textArea.getFont().deriveFont(14f));
                break;
            case "small":
                textArea.setFont(textArea.getFont().deriveFont(12f));
                break;
            default:
                try {
                    float fontSize = Float.parseFloat(selectedSize);
                    textArea.setFont(textArea.getFont().deriveFont(fontSize));
                } catch (NumberFormatException e) {
                    // Handle invalid input
                    System.err.println("Invalid font size input: " + selectedSize);
                }
                break;
        }
    }

    private void showAboutDialog() {
        ImageIcon icon = new ImageIcon("NoteTakerIcon.png");
        JOptionPane.showMessageDialog(frame, "NoteTaker Alpha 0.2.0", "About", JOptionPane.INFORMATION_MESSAGE, icon);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new NoteTaker();
            }
        });
    }
}
