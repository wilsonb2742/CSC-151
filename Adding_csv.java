import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;


public class Adding_csv extends JFrame {

    private DefaultListModel<String> listModel;
    private JList<String> itemList;
    private JButton playersButton;
    private JButton staffButton;
    private String itemCategory = "";

    public Adding_csv() {

        setTitle("Team Roster");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        itemList = new JList<>(listModel);

        playersButton = new JButton("Players");
        staffButton = new JButton("Coaches/Staff");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(playersButton);
        buttonPanel.add(staffButton);

        add(buttonPanel, BorderLayout.NORTH);
        add(new JScrollPane(itemList), BorderLayout.CENTER);

        playersButton.addActionListener(new ButtonClickListener());
        staffButton.addActionListener(new ButtonClickListener());

        itemList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (!itemList.isSelectionEmpty()) {
                    String selected = itemList.getSelectedValue();
                    showDetails(selected);
                    logSelection(selected);
                }
            }
        });

        setVisible(true);
    }

    // ===============================
    // 🔹 FILE READING METHOD (Players)
    // ===============================
    private void loadFromFile(String filename) {
        listModel.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;

            br.readLine(); // Skip header

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                if (values.length >= 2) {
                    String displayText = values[0] + " - " + values[1].trim(); // New!! ".trim();"
                    listModel.addElement(displayText);
                }
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading file: " + filename);
        }
    }

    // ===============================
    // 🔹 FILE READING METHOD (Staff)
    // Format: Name,Title,Description (Description can have commas)
    // ===============================
    private void loadStaffFromFile(String filename) {
        listModel.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            br.readLine(); // skip header
            String line;

            while ((line = br.readLine()) != null) {
                // split into 3 parts only
                String[] parts = line.split(",", 3);
                if (parts.length >= 3) {
                    String displayText = parts[0] + " - " + parts[1];
                    listModel.addElement(displayText);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading file: " + filename);
        }
    }

    // ===============================
    // 🔹 FILE WRITING METHOD
    // ===============================
    private void logSelection(String selection) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("selections_log.txt", true))) {
            bw.write(selection);
            bw.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error writing to log file.");
        }
    }

    // ===============================
    // 🔹 SHOW DETAILS (Players + Staff)
    // ===============================
    private void showDetails(String selectedItem) {

        String name = selectedItem.split(" - ")[0];

        // New!!
        
        JDialog dialog = new JDialog(this, "Details", true);
        dialog.setLayout(new BorderLayout());

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        Font font = new Font("Arial", Font.BOLD, 14);
        textArea.setFont(font);

        StringBuilder messageBuilder = new StringBuilder();

        // New!!
        
        if (itemCategory.equals("Players")) {
            // Players CSV
            try (BufferedReader br = new BufferedReader(new FileReader("players.csv"))) {
                String line;
                br.readLine(); // Skip header

                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");
                    if (values[0].trim().equals(name)) {   // New!!  ".trim()."

                        // New!!

                        messageBuilder.append("Name: ").append(values[0]).append("\n")
                                      .append("Position: ").append(values[1]).append("\n")
                                      .append("Number: ").append(values[2]).append("\n")
                                      .append("Height: ").append(values[3]).append("\n")
                                      .append("Weight: ").append(values[4]).append("\n")
                                      .append("Age: ").append(values[5]).append("\n")
                                      .append("Exp: ").append(values[6]).append("\n")
                                      .append("College: ").append(values[7]);
                        break;

                        // New!!
                    }
                }

            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error reading details.");
                return; // New!!
            }

        } else if (itemCategory.equals("Coaches/Staff")) {
            // Staff CSV
            try (BufferedReader br = new BufferedReader(new FileReader("staff.csv"))) {
                String line;
                br.readLine(); // Skip header

                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",", 3); // split into Name, Title, Description
                    if (parts.length >= 3 && parts[0].trim().equals(name)) {  // New!! ".trim()."

                        // New!!

                        messageBuilder.apprend("Name: ").append(parts[0].trim()).append("\n")
                                      .apprend("Title: ").append(parts[1].trim()).append("\n")
                                      .append(parts[2].trim());
                        break;

                        // New!!
                    }
                }

            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error reading details.");
            }
        }

        // New!!
        
        textArea.setText(messageBuilder.toString());
        textArea.setPreferredSize(new Dimension(400,200));
        dialog.add(new JScrollPane(textArea), BorderLayout.CENTER);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        
        // New!!
    }

    // ===============================
    // 🔹 BUTTON LISTENER
    // ===============================
    private class ButtonClickListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            if (e.getSource() == playersButton) {
                itemCategory = "Players";
                updateItemList();
            } else if (e.getSource() == staffButton) {
                itemCategory = "Coaches/Staff";
                updateItemList();
            }
        }

        private void updateItemList() {

            if (itemCategory.equals("Players")) {
                loadFromFile("players.csv");
            } else if (itemCategory.equals("Coaches/Staff")) {
                loadStaffFromFile("staff.csv"); 
            }
        }
    }

    public static void main(String[] args) {
        new Adding_csv();
    }

}

