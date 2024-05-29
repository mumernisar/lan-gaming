package Utils;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class GUILeaderboard extends JFrame {
    private DefaultTableModel model;
    public GUILeaderboard() {
        model = new DefaultTableModel(null, new String[] {"Name", "Progress", "Speed"});
    }
    public void initializeGUI() {
        SwingUtilities.invokeLater(() -> {
            // win setup
            setTitle("Leaderboard");
            setSize(600, 200);
            setResizable(false);
            // set closing bhaviour
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            // Table setup
            JTable table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);
            table.setFillsViewportHeight(true);

            // Adjusting column widths
            table.getColumnModel().getColumn(0).setPreferredWidth(100); // Name
            table.getColumnModel().getColumn(1).setPreferredWidth(400); // Progress
            table.getColumnModel().getColumn(2).setPreferredWidth(100); // Speed

            // Adding table to JFrame
            getContentPane().add(scrollPane, BorderLayout.CENTER);

            // Display the JFrame
            setVisible(true);
        });
    }
    public void updateLeaderboardWithData(String[][] data) {
        SwingUtilities.invokeLater(() -> {
            // Clear the previous data
            model.setRowCount(0);

            // Iterate through the array of data and add rows to the model
            for (String[] row : data) {
                if (row[0] != null){
                    model.addRow(row);
                }
            }
            revalidate();
            repaint();

        });
    }

    
    public void closeGUI() {
        SwingUtilities.invokeLater(() -> {
            dispose();
        });
    }
}
