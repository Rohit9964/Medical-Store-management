import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class MedicalStoreApp extends JFrame {
    JComboBox<String> tableSelector, collectionSelector;
    JTextArea outputArea;
    JButton loadButton, insertButton, updateButton, deleteButton, searchButton, alertButton;

    public MedicalStoreApp() {
        setTitle("🏥 Medical Store Inventory System");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 255, 250));

        // Top Panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        topPanel.setBackground(new Color(70, 130, 180));

        JLabel tableLabel = new JLabel("Table:");
        tableLabel.setForeground(Color.WHITE);
        tableLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel collectionLabel = new JLabel("Collection:");
        collectionLabel.setForeground(Color.WHITE);
        collectionLabel.setFont(new Font("Arial", Font.BOLD, 14));

        tableSelector = new JComboBox<>(new String[]{"Medicines", "Suppliers"});
        collectionSelector = new JComboBox<>(new String[]{"ArrayList", "TreeSet"});

        loadButton = new JButton("Load Data");
        loadButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        loadButton.setBackground(new Color(255, 215, 0));

        insertButton = new JButton("Insert");
        insertButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        insertButton.setBackground(new Color(60, 179, 113));
        insertButton.setForeground(Color.WHITE);

        updateButton = new JButton("Update");
        updateButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        updateButton.setBackground(new Color(255, 140, 0));
        updateButton.setForeground(Color.WHITE);

        deleteButton = new JButton("Delete");
        deleteButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        deleteButton.setBackground(new Color(220, 20, 60));
        deleteButton.setForeground(Color.WHITE);

        searchButton = new JButton("Search");
        searchButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        searchButton.setBackground(new Color(138, 43, 226));
        searchButton.setForeground(Color.WHITE);

        alertButton = new JButton("Low Stock");
        alertButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        alertButton.setBackground(new Color(255, 69, 0));
        alertButton.setForeground(Color.WHITE);

        topPanel.add(tableLabel);
        topPanel.add(tableSelector);
        topPanel.add(collectionLabel);
        topPanel.add(collectionSelector);
        topPanel.add(loadButton);
        topPanel.add(insertButton);
        topPanel.add(updateButton);
        topPanel.add(deleteButton);
        topPanel.add(searchButton);
        topPanel.add(alertButton);

        // Output Area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        outputArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        loadButton.addActionListener(e -> loadData());
        insertButton.addActionListener(e -> insertRecord());
        updateButton.addActionListener(e -> updateRecord());
        deleteButton.addActionListener(e -> deleteRecord());
        searchButton.addActionListener(e -> searchRecord());
        alertButton.addActionListener(e -> showLowStockAlert());

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/MedicalStore", "root", "");
    }

    private void loadData() {
        String table = (String) tableSelector.getSelectedItem();
        String collection = (String) collectionSelector.getSelectedItem();
        outputArea.setText("");

        try (Connection conn = getConnection()) {
            if ("Medicines".equals(table)) {
                List<Medicine> medicineList = new ArrayList<>();
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT * FROM Medicines")) {
                    while (rs.next()) {
                        medicineList.add(new Medicine(
                                rs.getInt("medicine_id"),
                                rs.getString("name"),
                                rs.getString("batch_no"),
                                rs.getFloat("price"),
                                rs.getInt("quantity"),
                                rs.getInt("threshold"),
                                rs.getString("expiry_date"),
                                rs.getInt("supplier_id")
                        ));
                    }
                }
                
                outputArea.append(String.format("%-4s | %-20s | %-10s | %-7s | %-8s | %-8s | %-12s | %-10s\n", 
                        "ID", "Name", "Batch No", "Price", "Quantity", "Threshold", "Expiry Date", "Supplier ID"));
                outputArea.append("--------------------------------------------------------------------------------------------------------\n");

                if ("TreeSet".equals(collection)) {
                    TreeSet<Medicine> sortedSet = new TreeSet<>(Comparator.comparing(Medicine::getName));
                    sortedSet.addAll(medicineList);
                    for (Medicine med : sortedSet) {
                        outputArea.append(med + "\n");
                    }
                } else {
                    for (Medicine med : medicineList) {
                        outputArea.append(med + "\n");
                    }
                }

            } else if ("Suppliers".equals(table)) {
                List<Supplier> supplierList = new ArrayList<>();
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT * FROM Suppliers")) {
                    while (rs.next()) {
                        supplierList.add(new Supplier(
                                rs.getInt("supplier_id"),
                                rs.getString("name"),
                                rs.getString("contact"),
                                rs.getString("email"),
                                rs.getString("address")
                        ));
                    }
                }

                outputArea.append(String.format("%-4s | %-20s | %-15s | %-25s | %-30s\n", 
                        "ID", "Name", "Contact", "Email", "Address"));
                outputArea.append("--------------------------------------------------------------------------------------------------------\n");
                
                for (Supplier s : supplierList) {
                    outputArea.append(s + "\n");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            outputArea.setText("❌ Error loading data: " + ex.getMessage());
        }
    }

    private void insertRecord() {
        String table = (String) tableSelector.getSelectedItem();

        if ("Medicines".equals(table)) {
            try {
                String idStr = JOptionPane.showInputDialog(this, "Enter Medicine ID (int):");
                if (idStr == null) return;
                int id = Integer.parseInt(idStr.trim());

                String name = JOptionPane.showInputDialog(this, "Enter Medicine Name:");
                if (name == null) return;

                String batchNo = JOptionPane.showInputDialog(this, "Enter Batch Number:");
                if (batchNo == null) return;

                String priceStr = JOptionPane.showInputDialog(this, "Enter Price:");
                if (priceStr == null) return;
                float price = Float.parseFloat(priceStr.trim());

                String quantityStr = JOptionPane.showInputDialog(this, "Enter Quantity:");
                if (quantityStr == null) return;
                int quantity = Integer.parseInt(quantityStr.trim());

                String thresholdStr = JOptionPane.showInputDialog(this, "Enter Threshold (low stock alert):");
                if (thresholdStr == null) return;
                int threshold = Integer.parseInt(thresholdStr.trim());

                String expiryDate = JOptionPane.showInputDialog(this, "Enter Expiry Date (YYYY-MM-DD):");
                if (expiryDate == null) return;
                
                // Validate date format
                try {
                    LocalDate.parse(expiryDate);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD");
                    return;
                }

                String supplierIdStr = JOptionPane.showInputDialog(this, "Enter Supplier ID (0 if none):");
                if (supplierIdStr == null) return;
                int supplierId = Integer.parseInt(supplierIdStr.trim());

                try (Connection conn = getConnection();
                     PreparedStatement ps = conn.prepareStatement(
                             "INSERT INTO Medicines(medicine_id, name, batch_no, price, quantity, threshold, expiry_date, supplier_id) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                    ps.setInt(1, id);
                    ps.setString(2, name);
                    ps.setString(3, batchNo);
                    ps.setFloat(4, price);
                    ps.setInt(5, quantity);
                    ps.setInt(6, threshold);
                    ps.setString(7, expiryDate);
                    ps.setInt(8, supplierId);
                    ps.executeUpdate();
                }

                JOptionPane.showMessageDialog(this, "Medicine added successfully.");
                loadData();

            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Invalid number format. Try again.");
            } catch (SQLException sqle) {
                JOptionPane.showMessageDialog(this, "SQL Error: " + sqle.getMessage());
            }
        } else if ("Suppliers".equals(table)) {
            try {
                String idStr = JOptionPane.showInputDialog(this, "Enter Supplier ID (int):");
                if (idStr == null) return;
                int id = Integer.parseInt(idStr.trim());

                String name = JOptionPane.showInputDialog(this, "Enter Supplier Name:");
                if (name == null) return;

                String contact = JOptionPane.showInputDialog(this, "Enter Contact Number:");
                if (contact == null) return;

                String email = JOptionPane.showInputDialog(this, "Enter Email:");
                if (email == null) return;

                String address = JOptionPane.showInputDialog(this, "Enter Address:");
                if (address == null) return;

                try (Connection conn = getConnection();
                     PreparedStatement ps = conn.prepareStatement(
                             "INSERT INTO Suppliers(supplier_id, name, contact, email, address) " +
                             "VALUES (?, ?, ?, ?, ?)")) {
                    ps.setInt(1, id);
                    ps.setString(2, name);
                    ps.setString(3, contact);
                    ps.setString(4, email);
                    ps.setString(5, address);
                    ps.executeUpdate();
                }

                JOptionPane.showMessageDialog(this, "Supplier added successfully.");
                loadData();

            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Invalid number format. Try again.");
            } catch (SQLException sqle) {
                JOptionPane.showMessageDialog(this, "SQL Error: " + sqle.getMessage());
            }
        }
    }

    private void updateRecord() {
        String table = (String) tableSelector.getSelectedItem();

        try (Connection conn = getConnection()) {
            if ("Medicines".equals(table)) {
                String idStr = JOptionPane.showInputDialog(this, "Enter the Medicine ID to update:");
                if (idStr == null) return;
                int id = Integer.parseInt(idStr.trim());

                // Fetch current record
                Medicine current = null;
                try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM Medicines WHERE medicine_id = ?")) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            current = new Medicine(
                                    rs.getInt("medicine_id"),
                                    rs.getString("name"),
                                    rs.getString("batch_no"),
                                    rs.getFloat("price"),
                                    rs.getInt("quantity"),
                                    rs.getInt("threshold"),
                                    rs.getString("expiry_date"),
                                    rs.getInt("supplier_id")
                            );
                        } else {
                            JOptionPane.showMessageDialog(this, "No Medicine found with that ID.");
                            return;
                        }
                    }
                }

                // Show current values and ask for new values (can leave blank to keep current)
                String name = JOptionPane.showInputDialog(this,
                        "Enter new Medicine Name (leave blank to keep '" + current.getName() + "'):");
                if (name == null) return;
                if (name.trim().isEmpty()) name = current.getName();

                String batchNo = JOptionPane.showInputDialog(this,
                        "Enter new Batch No (leave blank to keep '" + current.getBatchNo() + "'):");
                if (batchNo == null) return;
                if (batchNo.trim().isEmpty()) batchNo = current.getBatchNo();

                String priceStr = JOptionPane.showInputDialog(this,
                        "Enter new Price (leave blank to keep '" + current.getPrice() + "'):");
                if (priceStr == null) return;
                float price = priceStr.trim().isEmpty() ? current.getPrice() : Float.parseFloat(priceStr.trim());

                String quantityStr = JOptionPane.showInputDialog(this,
                        "Enter new Quantity (leave blank to keep '" + current.getQuantity() + "'):");
                if (quantityStr == null) return;
                int quantity = quantityStr.trim().isEmpty() ? current.getQuantity() : Integer.parseInt(quantityStr.trim());

                String thresholdStr = JOptionPane.showInputDialog(this,
                        "Enter new Threshold (leave blank to keep '" + current.getThreshold() + "'):");
                if (thresholdStr == null) return;
                int threshold = thresholdStr.trim().isEmpty() ? current.getThreshold() : Integer.parseInt(thresholdStr.trim());

                String expiryDate = JOptionPane.showInputDialog(this,
                        "Enter new Expiry Date (YYYY-MM-DD) (leave blank to keep '" + current.getExpiryDate() + "'):");
                if (expiryDate == null) return;
                if (expiryDate.trim().isEmpty()) expiryDate = current.getExpiryDate();
                else {
                    try {
                        LocalDate.parse(expiryDate);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD");
                        return;
                    }
                }

                String supplierIdStr = JOptionPane.showInputDialog(this,
                        "Enter new Supplier ID (leave blank to keep '" + current.getSupplierId() + "'):");
                if (supplierIdStr == null) return;
                int supplierId = supplierIdStr.trim().isEmpty() ? current.getSupplierId() : Integer.parseInt(supplierIdStr.trim());

                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE Medicines SET name=?, batch_no=?, price=?, quantity=?, threshold=?, expiry_date=?, supplier_id=? " +
                        "WHERE medicine_id=?")) {
                    ps.setString(1, name);
                    ps.setString(2, batchNo);
                    ps.setFloat(3, price);
                    ps.setInt(4, quantity);
                    ps.setInt(5, threshold);
                    ps.setString(6, expiryDate);
                    ps.setInt(7, supplierId);
                    ps.setInt(8, id);
                    ps.executeUpdate();
                }

                JOptionPane.showMessageDialog(this, "Medicine updated successfully.");
                loadData();

            } else if ("Suppliers".equals(table)) {
                String idStr = JOptionPane.showInputDialog(this, "Enter the Supplier ID to update:");
                if (idStr == null) return;
                int id = Integer.parseInt(idStr.trim());

                // Fetch current record
                Supplier current = null;
                try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM Suppliers WHERE supplier_id = ?")) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            current = new Supplier(
                                    rs.getInt("supplier_id"),
                                    rs.getString("name"),
                                    rs.getString("contact"),
                                    rs.getString("email"),
                                    rs.getString("address")
                            );
                        } else {
                            JOptionPane.showMessageDialog(this, "No Supplier found with that ID.");
                            return;
                        }
                    }
                }

                String name = JOptionPane.showInputDialog(this,
                        "Enter new Supplier Name (leave blank to keep '" + current.getName() + "'):");
                if (name == null) return;
                if (name.trim().isEmpty()) name = current.getName();

                String contact = JOptionPane.showInputDialog(this,
                        "Enter new Contact (leave blank to keep '" + current.getContact() + "'):");
                if (contact == null) return;
                if (contact.trim().isEmpty()) contact = current.getContact();

                String email = JOptionPane.showInputDialog(this,
                        "Enter new Email (leave blank to keep '" + current.getEmail() + "'):");
                if (email == null) return;
                if (email.trim().isEmpty()) email = current.getEmail();

                String address = JOptionPane.showInputDialog(this,
                        "Enter new Address (leave blank to keep '" + current.getAddress() + "'):");
                if (address == null) return;
                if (address.trim().isEmpty()) address = current.getAddress();

                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE Suppliers SET name=?, contact=?, email=?, address=? WHERE supplier_id=?")) {
                    ps.setString(1, name);
                    ps.setString(2, contact);
                    ps.setString(3, email);
                    ps.setString(4, address);
                    ps.setInt(5, id);
                    ps.executeUpdate();
                }

                JOptionPane.showMessageDialog(this, "Supplier updated successfully.");
                loadData();
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Invalid number format. Try again.");
        } catch (SQLException sqle) {
            JOptionPane.showMessageDialog(this, "SQL Error: " + sqle.getMessage());
        }
    }

    private void deleteRecord() {
        String table = (String) tableSelector.getSelectedItem();

        try (Connection conn = getConnection()) {
            if ("Medicines".equals(table)) {
                String idStr = JOptionPane.showInputDialog(this, "Enter the Medicine ID to delete:");
                if (idStr == null) return;
                int id = Integer.parseInt(idStr.trim());

                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete Medicine with ID " + id + "?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Medicines WHERE medicine_id = ?")) {
                        ps.setInt(1, id);
                        int affected = ps.executeUpdate();
                        if (affected == 0) {
                            JOptionPane.showMessageDialog(this, "No Medicine found with that ID.");
                        } else {
                            JOptionPane.showMessageDialog(this, "Medicine deleted.");
                        }
                    }
                    loadData();
                }
            } else if ("Suppliers".equals(table)) {
                String idStr = JOptionPane.showInputDialog(this, "Enter the Supplier ID to delete:");
                if (idStr == null) return;
                int id = Integer.parseInt(idStr.trim());

                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete Supplier with ID " + id + "?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    // First check if any medicines reference this supplier
                    try (PreparedStatement checkPs = conn.prepareStatement(
                            "SELECT COUNT(*) FROM Medicines WHERE supplier_id = ?")) {
                        checkPs.setInt(1, id);
                        try (ResultSet rs = checkPs.executeQuery()) {
                            if (rs.next() && rs.getInt(1) > 0) {
                                JOptionPane.showMessageDialog(this, 
                                        "Cannot delete supplier - there are medicines associated with this supplier.");
                                return;
                            }
                        }
                    }

                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Suppliers WHERE supplier_id = ?")) {
                        ps.setInt(1, id);
                        int affected = ps.executeUpdate();
                        if (affected == 0) {
                            JOptionPane.showMessageDialog(this, "No Supplier found with that ID.");
                        } else {
                            JOptionPane.showMessageDialog(this, "Supplier deleted.");
                        }
                    }
                    loadData();
                }
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Invalid number format. Try again.");
        } catch (SQLException sqle) {
            JOptionPane.showMessageDialog(this, "SQL Error: " + sqle.getMessage());
        }
    }

    private void searchRecord() {
        String table = (String) tableSelector.getSelectedItem();
        outputArea.setText("");

        if ("Medicines".equals(table)) {
            String[] options = {"By Name", "By Batch No"};
            int choice = JOptionPane.showOptionDialog(this, 
                    "Search Medicine By:", 
                    "Search Option", 
                    JOptionPane.DEFAULT_OPTION, 
                    JOptionPane.QUESTION_MESSAGE, 
                    null, 
                    options, 
                    options[0]);

            if (choice == -1) return; // User cancelled

            String searchTerm = JOptionPane.showInputDialog(this, "Enter search term:");
            if (searchTerm == null || searchTerm.trim().isEmpty()) return;

            try (Connection conn = getConnection()) {
                List<Medicine> results = new ArrayList<>();
                if (choice == 0) { // By Name
                    try (PreparedStatement ps = conn.prepareStatement(
                            "SELECT * FROM Medicines WHERE name LIKE ?")) {
                        ps.setString(1, "%" + searchTerm + "%");
                        try (ResultSet rs = ps.executeQuery()) {
                            while (rs.next()) {
                                results.add(new Medicine(
                                        rs.getInt("medicine_id"),
                                        rs.getString("name"),
                                        rs.getString("batch_no"),
                                        rs.getFloat("price"),
                                        rs.getInt("quantity"),
                                        rs.getInt("threshold"),
                                        rs.getString("expiry_date"),
                                        rs.getInt("supplier_id")
                                ));
                            }
                        }
                    }
                } else { // By Batch No
                    try (PreparedStatement ps = conn.prepareStatement(
                            "SELECT * FROM Medicines WHERE batch_no LIKE ?")) {
                        ps.setString(1, "%" + searchTerm + "%");
                        try (ResultSet rs = ps.executeQuery()) {
                            while (rs.next()) {
                                results.add(new Medicine(
                                        rs.getInt("medicine_id"),
                                        rs.getString("name"),
                                        rs.getString("batch_no"),
                                        rs.getFloat("price"),
                                        rs.getInt("quantity"),
                                        rs.getInt("threshold"),
                                        rs.getString("expiry_date"),
                                        rs.getInt("supplier_id")
                                ));
                            }
                        }
                    }
                }

                outputArea.append(String.format("%-4s | %-20s | %-10s | %-7s | %-8s | %-8s | %-12s | %-10s\n", 
                        "ID", "Name", "Batch No", "Price", "Quantity", "Threshold", "Expiry Date", "Supplier ID"));
                outputArea.append("--------------------------------------------------------------------------------------------------------\n");

                for (Medicine med : results) {
                    outputArea.append(med + "\n");
                }

                if (results.isEmpty()) {
                    outputArea.append("No matching medicines found.");
                }

            } catch (SQLException sqle) {
                JOptionPane.showMessageDialog(this, "SQL Error: " + sqle.getMessage());
            }
        } else if ("Suppliers".equals(table)) {
            String searchTerm = JOptionPane.showInputDialog(this, "Enter supplier name to search:");
            if (searchTerm == null || searchTerm.trim().isEmpty()) return;

            try (Connection conn = getConnection()) {
                List<Supplier> results = new ArrayList<>();
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT * FROM Suppliers WHERE name LIKE ?")) {
                    ps.setString(1, "%" + searchTerm + "%");
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            results.add(new Supplier(
                                    rs.getInt("supplier_id"),
                                    rs.getString("name"),
                                    rs.getString("contact"),
                                    rs.getString("email"),
                                    rs.getString("address")
                            ));
                        }
                    }
                }

                outputArea.append(String.format("%-4s | %-20s | %-15s | %-25s | %-30s\n", 
                        "ID", "Name", "Contact", "Email", "Address"));
                outputArea.append("--------------------------------------------------------------------------------------------------------\n");
                
                for (Supplier s : results) {
                    outputArea.append(s + "\n");
                }

                if (results.isEmpty()) {
                    outputArea.append("No matching suppliers found.");
                }

            } catch (SQLException sqle) {
                JOptionPane.showMessageDialog(this, "SQL Error: " + sqle.getMessage());
            }
        }
    }

    private void showLowStockAlert() {
        outputArea.setText("");
        
        try (Connection conn = getConnection()) {
            List<Medicine> lowStockMedicines = new ArrayList<>();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(
                         "SELECT * FROM Medicines WHERE quantity <= threshold")) {
                while (rs.next()) {
                    lowStockMedicines.add(new Medicine(
                            rs.getInt("medicine_id"),
                            rs.getString("name"),
                            rs.getString("batch_no"),
                            rs.getFloat("price"),
                            rs.getInt("quantity"),
                            rs.getInt("threshold"),
                            rs.getString("expiry_date"),
                            rs.getInt("supplier_id")
                    ));
                }
            }
            
            outputArea.append("⚠️ LOW STOCK ALERT ⚠️\n");
            outputArea.append("The following medicines are below or at their threshold levels:\n\n");
            outputArea.append(String.format("%-4s | %-20s | %-10s | %-8s | %-8s\n", 
                    "ID", "Name", "Batch No", "Quantity", "Threshold"));
            outputArea.append("------------------------------------------------------------\n");
            
            if (lowStockMedicines.isEmpty()) {
                outputArea.append("No medicines are currently low in stock.\n");
            } else {
                for (Medicine med : lowStockMedicines) {
                    outputArea.append(String.format("%-4d | %-20s | %-10s | %-8d | %-8d\n",
                            med.getMedicineId(), med.getName(), med.getBatchNo(), 
                            med.getQuantity(), med.getThreshold()));
                }
            }
            
            // Check for expired medicines
            outputArea.append("\n\n⚠️ EXPIRY ALERT ⚠️\n");
            outputArea.append("The following medicines are expired or will expire soon:\n\n");
            
            LocalDate today = LocalDate.now();
            LocalDate soon = today.plusDays(30); // Next 30 days
            
            List<Medicine> expiredMedicines = new ArrayList<>();
            List<Medicine> expiringSoonMedicines = new ArrayList<>();
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM Medicines")) {
                while (rs.next()) {
                    Medicine med = new Medicine(
                            rs.getInt("medicine_id"),
                            rs.getString("name"),
                            rs.getString("batch_no"),
                            rs.getFloat("price"),
                            rs.getInt("quantity"),
                            rs.getInt("threshold"),
                            rs.getString("expiry_date"),
                            rs.getInt("supplier_id")
                    );
                    
                    LocalDate expiryDate = LocalDate.parse(med.getExpiryDate());
                    if (expiryDate.isBefore(today)) {
                        expiredMedicines.add(med);
                    } else if (expiryDate.isBefore(soon)) {
                        expiringSoonMedicines.add(med);
                    }
                }
            }
            
            if (!expiredMedicines.isEmpty()) {
                outputArea.append("EXPIRED MEDICINES:\n");
                outputArea.append(String.format("%-4s | %-20s | %-10s | %-12s\n", 
                        "ID", "Name", "Batch No", "Expiry Date"));
                outputArea.append("------------------------------------------------\n");
                for (Medicine med : expiredMedicines) {
                    outputArea.append(String.format("%-4d | %-20s | %-10s | %-12s\n",
                            med.getMedicineId(), med.getName(), 
                            med.getBatchNo(), med.getExpiryDate()));
                }
            }
            
            if (!expiringSoonMedicines.isEmpty()) {
                outputArea.append("\nMEDICINES EXPIRING SOON (within 30 days):\n");
                outputArea.append(String.format("%-4s | %-20s | %-10s | %-12s\n", 
                        "ID", "Name", "Batch No", "Expiry Date"));
                outputArea.append("------------------------------------------------\n");
                for (Medicine med : expiringSoonMedicines) {
                    outputArea.append(String.format("%-4d | %-20s | %-10s | %-12s\n",
                            med.getMedicineId(), med.getName(), 
                            med.getBatchNo(), med.getExpiryDate()));
                }
            }
            
            if (expiredMedicines.isEmpty() && expiringSoonMedicines.isEmpty()) {
                outputArea.append("No expired or soon-to-expire medicines found.\n");
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            outputArea.setText("❌ Error checking stock alerts: " + ex.getMessage());
        }
    }

    // Medicine class
    static class Medicine {
        private int medicine_id;
        private String name;
        private String batch_no;
        private float price;
        private int quantity;
        private int threshold;
        private String expiry_date;
        private int supplier_id;

        public Medicine(int medicine_id, String name, String batch_no, float price, 
                        int quantity, int threshold, String expiry_date, int supplier_id) {
            this.medicine_id = medicine_id;
            this.name = name;
            this.batch_no = batch_no;
            this.price = price;
            this.quantity = quantity;
            this.threshold = threshold;
            this.expiry_date = expiry_date;
            this.supplier_id = supplier_id;
        }

        public int getMedicineId() { return medicine_id; }
        public String getName() { return name; }
        public String getBatchNo() { return batch_no; }
        public float getPrice() { return price; }
        public int getQuantity() { return quantity; }
        public int getThreshold() { return threshold; }
        public String getExpiryDate() { return expiry_date; }
        public int getSupplierId() { return supplier_id; }

        @Override
        public String toString() {
            return String.format("%-4d | %-20s | %-10s | $%-6.2f | %-8d | %-8d | %-12s | %-10d",
                    medicine_id, name, batch_no, price, quantity, threshold, expiry_date, supplier_id);
        }
    }

    // Supplier class
    static class Supplier {
        private int supplier_id;
        private String name;
        private String contact;
        private String email;
        private String address;

        public Supplier(int supplier_id, String name, String contact, String email, String address) {
            this.supplier_id = supplier_id;
            this.name = name;
            this.contact = contact;
            this.email = email;
            this.address = address;
        }

        public int getSupplierId() { return supplier_id; }
        public String getName() { return name; }
        public String getContact() { return contact; }
        public String getEmail() { return email; }
        public String getAddress() { return address; }

        @Override
        public String toString() {
            return String.format("%-4d | %-20s | %-15s | %-25s | %-30s",
                    supplier_id, name, contact, email, address);
        }
    }

    public static void main(String[] args) {
        // Create database tables if they don't exist
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "");
             Statement stmt = conn.createStatement()) {
            
            // Create database if not exists
            stmt.execute("CREATE DATABASE IF NOT EXISTS MedicalStore");
            stmt.execute("USE MedicalStore");
            
            // Create Medicines table
            stmt.execute("CREATE TABLE IF NOT EXISTS Medicines (" +
                    "medicine_id INT PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "batch_no VARCHAR(50) NOT NULL, " +
                    "price DECIMAL(10,2) NOT NULL, " +
                    "quantity INT NOT NULL, " +
                    "threshold INT NOT NULL, " +
                    "expiry_date DATE NOT NULL, " +
                    "supplier_id INT)");
            
            // Create Suppliers table
            stmt.execute("CREATE TABLE IF NOT EXISTS Suppliers (" +
                    "supplier_id INT PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "contact VARCHAR(20), " +
                    "email VARCHAR(100), " +
                    "address VARCHAR(200))");
            
            // Add some sample data if tables are empty
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Medicines");
            rs.next();
            if (rs.getInt(1) == 0) {
                stmt.execute("INSERT INTO Medicines VALUES " +
                        "(1, 'Paracetamol', 'BATCH001', 5.50, 50, 10, '2024-12-31', 1), " +
                        "(2, 'Ibuprofen', 'BATCH002', 7.25, 15, 5, '2025-06-30', 1), " +
                        "(3, 'Amoxicillin', 'BATCH003', 12.75, 8, 5, '2024-09-15', 2), " +
                        "(4, 'Cetirizine', 'BATCH004', 4.20, 30, 10, '2025-03-31', null)");
            }
            
            rs = stmt.executeQuery("SELECT COUNT(*) FROM Suppliers");
            rs.next();
            if (rs.getInt(1) == 0) {
                stmt.execute("INSERT INTO Suppliers VALUES " +
                        "(1, 'Pharma Distributors', '9876543210', 'pharma@example.com', '123 Pharma St, Mumbai'), " +
                        "(2, 'MediCorp', '8765432109', 'medicorp@example.com', '456 Health Ave, Delhi')");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error initializing database: " + e.getMessage(), 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        
        SwingUtilities.invokeLater(MedicalStoreApp::new);
    }
}