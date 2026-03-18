package com.example.lms;

import com.example.lms.model.Student;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LmsSwingApp extends JFrame {

    private JTextField urlField;
    private JButton loadButton;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JProgressBar progressBar;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public LmsSwingApp() {
        setTitle("LMS Active Students");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        // Top panel for URL input and load button
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel urlLabel = new JLabel("API Base URL:");
        urlField = new JTextField("http://localhost:8080/api", 30);
        loadButton = new JButton("Load Active Students");

        JPanel urlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        urlPanel.add(urlLabel);
        urlPanel.add(urlField);
        urlPanel.add(loadButton);
        topPanel.add(urlPanel, BorderLayout.CENTER);

        // Center panel with table
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Email"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // non-editable
            }
        };
        studentTable = new JTable(tableModel);
        studentTable.setAutoCreateRowSorter(true); // enable sorting
        studentTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        studentTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        studentTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        JScrollPane scrollPane = new JScrollPane(studentTable);

        // Bottom panel for status and progress
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        statusLabel = new JLabel("Ready");
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        bottomPanel.add(statusLabel, BorderLayout.WEST);
        bottomPanel.add(progressBar, BorderLayout.EAST);

        // Add all to frame
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Action listener for load button
        loadButton.addActionListener(this::loadData);
    }

    private void loadData(ActionEvent e) {
        String baseUrl = urlField.getText().trim();
        if (baseUrl.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the API base URL.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Disable button and show progress
        loadButton.setEnabled(false);
        progressBar.setVisible(true);
        statusLabel.setText("Loading...");

        // Execute background task
        new LoadWorker(baseUrl).execute();
    }

    /**
     * SwingWorker to fetch data in background.
     */
    private class LoadWorker extends SwingWorker<List<Student>, Void> {

        private final String baseUrl;

        LoadWorker(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        @Override
        protected List<Student> doInBackground() throws Exception {
            String endpoint = baseUrl + "/students/active";
            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("HTTP error code: " + responseCode);
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            // Parse JSON into List<Student>
            return objectMapper.readValue(response.toString(), new TypeReference<List<Student>>() {});
        }

        @Override
        protected void done() {
            try {
                List<Student> students = get();
                // Update table model
                tableModel.setRowCount(0); // clear existing
                for (Student s : students) {
                    tableModel.addRow(new Object[]{s.getId(), s.getName(), s.getEmail()});
                }
                statusLabel.setText("Last updated: " + java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));
            } catch (InterruptedException | ExecutionException e) {
                Throwable cause = e.getCause();
                String message = (cause != null) ? cause.getMessage() : e.getMessage();
                // User-friendly error
                JOptionPane.showMessageDialog(LmsSwingApp.this,
                        "Failed to load data. Please check the URL and ensure the server is running.\nDetails: " + message,
                        "Connection Error",
                        JOptionPane.ERROR_MESSAGE);
                statusLabel.setText("Error: " + (cause != null ? cause.getMessage() : "unknown error"));
            } finally {
                loadButton.setEnabled(true);
                progressBar.setVisible(false);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LmsSwingApp().setVisible(true));
    }
}