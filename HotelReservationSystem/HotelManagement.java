import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

// ENUM
enum RoomType { SINGLE, DOUBLE, DELUXE, SUITE }
enum BedType { SINGLE, DOUBLE, KING }

//Represents a class Hotel Room with serializable
class Room implements Serializable {
    private static final long serialVersionUID = 1L;
    private String roomId;
    private RoomType type;
    private BedType bed;
    private int price;

    public Room(String roomId, RoomType type, BedType bed, int price) {
        this.roomId = roomId;
        this.type = type;
        this.bed = bed;
        this.price = price;
    }

    public String getRoomId() { return roomId; }
    public RoomType getType() { return type; }
    public BedType getBed() { return bed; }
    public int getPrice() { return price; }

    @Override
    public String toString() { return roomId; }
}

//class customers
class Customer implements Serializable {
    private static final long serialVersionUID = 1L;
    private String customerId, name, phone, email;

    public Customer(String id, String name, String phone, String email) {
        this.customerId = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public String getCustomerId() { return customerId; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }

    @Override
    public String toString() { return customerId + " - " + name; }
}

//class Reservation with serializable 
class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;
    private String resId, roomId, custId;
    private Date checkIn, checkOut;
    private int total;

    public Reservation(String resId, String roomId, String custId, Date in, Date out, int total) {
        this.resId = resId;
        this.roomId = roomId;
        this.custId = custId;
        this.checkIn = in;
        this.checkOut = out;
        this.total = total;
    }

    public String getResId() { return resId; }
    public String getRoomId() { return roomId; }
    public String getCustId() { return custId; }
    public Date getCheckIn() { return checkIn; }
    public Date getCheckOut() { return checkOut; }
    public int getTotal() { return total; }
}

// DATA PERSISTENCE with serialization
class DataManager {
    private static final String ROOM_FILE = "rooms.dat";
    private static final String CUST_FILE = "cust.dat";
    private static final String RES_FILE = "res.dat";

    // Generic save method for serializable objects 
    public static void save(Object obj, String fileName) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(obj);
        } catch (Exception e) {
            System.err.println("Persistence Error: " + e.getMessage());
        }
    }

    //Generic load method 
    public static Object load(String fileName) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            return ois.readObject();
        } catch (Exception e) {
            return null; 
        }
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<Room> getRooms() {
        ArrayList<Room> data = (ArrayList<Room>) load(ROOM_FILE);
        return data != null ? data : new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<Customer> getCust() {
        ArrayList<Customer> data = (ArrayList<Customer>) load(CUST_FILE);
        return data != null ? data : new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<Reservation> getRes() {
        ArrayList<Reservation> data = (ArrayList<Reservation>) load(RES_FILE);
        return data != null ? data : new ArrayList<>();
    }
}

// CUSTOM UI COMPONENTS

// JPanel that supports a background image 
class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel(String imagePath) {
        this.backgroundImage = new ImageIcon(imagePath).getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}

// Modern, rounded buttons
class ModernButton extends JButton {
    private Color startColor = new Color(255, 255, 255, 200);
    private Color endColor = new Color(230, 230, 230, 180);

    public ModernButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setFont(new Font("Segoe UI", Font.BOLD, 16));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (getModel().isRollover()) {
            g2.setPaint(new GradientPaint(0, 0, Color.WHITE, 0, getHeight(), new Color(200, 200, 255)));
        } else {
            g2.setPaint(new GradientPaint(0, 0, startColor, 0, getHeight(), endColor));
        }

        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 40, 40));

        g2.setColor(new Color(255, 255, 255, 100));
        g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 40, 40));

        g2.dispose();
        super.paintComponent(g);
    }
}

// MAIN ENTRY POINT
public class HotelManagement {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Initialize Splash Screen
            JFrame splash = new JFrame();
            splash.setUndecorated(true);

            BackgroundPanel bg = new BackgroundPanel("homeSplash.jpg");
            bg.setLayout(new BorderLayout());

            JLabel titleLabel = new JLabel("HOTEL MANAGEMENT SYSTEM", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Serif", Font.BOLD, 45));
            titleLabel.setForeground(Color.WHITE);
            bg.add(titleLabel, BorderLayout.CENTER);

            JProgressBar progressBar = new JProgressBar(0, 100);
            progressBar.setForeground(new Color(46, 204, 113));
            progressBar.setStringPainted(true);
            bg.add(progressBar, BorderLayout.SOUTH);

            splash.add(bg);
            splash.setSize(1000, 700);
            splash.setLocationRelativeTo(null);
            splash.setVisible(true);

            // Fake loading thread
            new Thread(() -> {
                for(int i = 0; i <= 100; i++) {
                    try { 
                        Thread.sleep(20); 
                        progressBar.setValue(i); 
                    } catch(Exception ignored){}
                }
                splash.dispose();
                new HomeFrame();
            }).start();
        });
    }
}

// DASHBOARD FRAME
class HomeFrame extends JFrame {
    public HomeFrame() {
        setTitle("Dashboard - Olied's Hotel");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        BackgroundPanel bg = new BackgroundPanel("green-natural-background-vector-illustration-59110.jpg");
        bg.setLayout(new GridBagLayout());
        setContentPane(bg);

        JPanel menuGrid = new JPanel(new GridLayout(2, 2, 50, 50));
        menuGrid.setOpaque(false);

        menuGrid.add(createMenuButton("Manage Rooms", "checked.png", e -> { new RoomForm(); dispose(); }));
        menuGrid.add(createMenuButton("Customers", "Customer Registration & Check IN.png", e -> { new CustomerForm(); dispose(); }));
        menuGrid.add(createMenuButton("Reservations", "Customer Details Bill.png", e -> { new ReservationForm(); dispose(); }));
        menuGrid.add(createMenuButton("Exit System", "logout.png", e -> System.exit(0)));

        bg.add(menuGrid);
        setSize(1200, 900);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JButton createMenuButton(String text, String iconPath, ActionListener action) {
        ModernButton btn = new ModernButton(text);
        try {
            Image iconImg = new ImageIcon(iconPath).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(iconImg));
        } catch(Exception e) { /* Icon fail silent */ }

        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setPreferredSize(new Dimension(280, 250));
        btn.addActionListener(action);
        return btn;
    }
}

// ROOM MANAGEMENT FORM
class RoomForm extends JFrame {
    private JTextField idField, priceField;
    private JComboBox<RoomType> typeCombo;
    private JComboBox<BedType> bedCombo;
    private JTable roomTable;
    private DefaultTableModel tableModel;
    private ArrayList<Room> roomList;

    public RoomForm() {
        roomList = DataManager.getRooms();
        setupFrame();
    }

    private void setupFrame() {
        setTitle("Room Management");
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        BackgroundPanel bg = new BackgroundPanel("loasd.jpg");
        bg.setLayout(new BorderLayout());
        setContentPane(bg);

        // Top Navigation
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        ModernButton backBtn = new ModernButton("Dashboard");
        backBtn.setPreferredSize(new Dimension(150, 50));
        backBtn.addActionListener(e -> { new HomeFrame(); dispose(); });
        topPanel.add(backBtn);

        // Input Form Panel
        JPanel inputPanel = new JPanel(new GridLayout(12, 1, 10, 10));
        inputPanel.setOpaque(false);
        inputPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        idField = new JTextField(); 
        priceField = new JTextField();
        typeCombo = new JComboBox<>(RoomType.values());
        bedCombo = new JComboBox<>(BedType.values());

        addLabel(inputPanel, "Room ID:"); inputPanel.add(idField);
        addLabel(inputPanel, "Room Type:"); inputPanel.add(typeCombo);
        addLabel(inputPanel, "Bed Type:"); inputPanel.add(bedCombo);
        addLabel(inputPanel, "Price per Night:"); inputPanel.add(priceField);

        ModernButton addBtn = new ModernButton("Add Room");
        addBtn.addActionListener(e -> {
            try {
                roomList.add(new Room(
                        idField.getText(), 
                        (RoomType)typeCombo.getSelectedItem(), 
                        (BedType)bedCombo.getSelectedItem(), 
                        Integer.parseInt(priceField.getText())
                ));
                DataManager.save(roomList, "rooms.dat"); 
                refreshTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid Price format");
            }
        });

        ModernButton delBtn = new ModernButton("Delete Selection");
        delBtn.addActionListener(e -> {
            int viewRow = roomTable.getSelectedRow();
            if(viewRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a row to delete.");
                return;
            }
            int modelRow = roomTable.convertRowIndexToModel(viewRow);
            roomList.remove(modelRow);
            DataManager.save(roomList, "rooms.dat"); 
            refreshTable();
        });

        // NEW: Load and Update buttons
        ModernButton loadBtn = new ModernButton("Load Selected");
        loadBtn.addActionListener(e -> {
            int viewRow = roomTable.getSelectedRow();
            if (viewRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a row to edit.");
                return;
            }
            int modelRow = roomTable.convertRowIndexToModel(viewRow);
            Room r = roomList.get(modelRow);

            idField.setText(r.getRoomId());
            typeCombo.setSelectedItem(r.getType());
            bedCombo.setSelectedItem(r.getBed());
            priceField.setText(String.valueOf(r.getPrice()));
        });

        ModernButton updateBtn = new ModernButton("Update Room");
        updateBtn.addActionListener(e -> {
            int viewRow = roomTable.getSelectedRow();
            if (viewRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a row to update.");
                return;
            }
            try {
                int modelRow = roomTable.convertRowIndexToModel(viewRow);
                Room updated = new Room(
                        idField.getText(),
                        (RoomType) typeCombo.getSelectedItem(),
                        (BedType) bedCombo.getSelectedItem(),
                        Integer.parseInt(priceField.getText())
                );
                roomList.set(modelRow, updated);
                DataManager.save(roomList, "rooms.dat");
                refreshTable();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid price format");
            }
        });

        inputPanel.add(addBtn); 
        inputPanel.add(delBtn);
        inputPanel.add(loadBtn);
        inputPanel.add(updateBtn);

        // Table Setup
        tableModel = new DefaultTableModel(new String[]{"ID", "Type", "Bed", "Price"}, 0);
        roomTable = new JTable(tableModel);
        roomTable.setRowHeight(30);

        bg.add(topPanel, BorderLayout.NORTH);
        bg.add(inputPanel, BorderLayout.WEST);
        bg.add(new JScrollPane(roomTable), BorderLayout.CENTER);

        refreshTable();
        setVisible(true);
    }

    private void addLabel(JPanel p, String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.WHITE);
        p.add(lbl);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for(Room r : roomList) {
            tableModel.addRow(new Object[]{r.getRoomId(), r.getType(), r.getBed(), r.getPrice()});
        }
    }
}

// CUSTOMER MANAGEMENT FORM
class CustomerForm extends JFrame {
    private JTextField idField, nameField, phoneField, emailField, searchField;
    private JTable customerTable;
    private DefaultTableModel tableModel;
    private ArrayList<Customer> customerList;

    public CustomerForm() {
        customerList = DataManager.getCust();
        setupUI();
    }

    private void setupUI() {
        setTitle("Customer Management");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        BackgroundPanel bg = new BackgroundPanel("loasd.jpg");
        bg.setLayout(new BorderLayout());
        setContentPane(bg);

        // Header Navigation & Search
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setOpaque(false);

        ModernButton backBtn = new ModernButton("BACK");
        backBtn.setPreferredSize(new Dimension(100, 50));
        backBtn.addActionListener(e -> { new HomeFrame(); dispose(); });

        searchField = new JTextField(15);
        JPanel searchBox = new JPanel(); 
        searchBox.setOpaque(false);
        JLabel searchLbl = new JLabel("Search Guest Name: "); 
        searchLbl.setForeground(Color.WHITE);
        searchBox.add(searchLbl); 
        searchBox.add(searchField);

        navPanel.add(backBtn, BorderLayout.WEST);
        navPanel.add(searchBox, BorderLayout.EAST);

        // Left Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(13, 1, 10, 10));
        inputPanel.setOpaque(false);
        inputPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        idField = new JTextField(); 
        nameField = new JTextField(); 
        phoneField = new JTextField(); 
        emailField = new JTextField();

        inputPanel.add(createWhiteLabel("Customer ID:")); inputPanel.add(idField);
        inputPanel.add(createWhiteLabel("Full Name:")); inputPanel.add(nameField);
        inputPanel.add(createWhiteLabel("Phone Number:")); inputPanel.add(phoneField);
        inputPanel.add(createWhiteLabel("Email Address:")); inputPanel.add(emailField);

        ModernButton registerBtn = new ModernButton("Register Guest");
        registerBtn.addActionListener(e -> {
            customerList.add(new Customer(
                    idField.getText(), 
                    nameField.getText(), 
                    phoneField.getText(), 
                    emailField.getText()
            ));
            DataManager.save(customerList, "cust.dat"); 
            refreshTable();
        });
        inputPanel.add(registerBtn);

        // NEW: Load, Update, Delete buttons
        ModernButton loadBtn = new ModernButton("Load Selected");
        loadBtn.addActionListener(e -> {
            int viewRow = customerTable.getSelectedRow();
            if (viewRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a row to edit.");
                return;
            }
            int modelRow = customerTable.convertRowIndexToModel(viewRow);
            Customer c = customerList.get(modelRow);

            idField.setText(c.getCustomerId());
            nameField.setText(c.getName());
            phoneField.setText(c.getPhone());
            emailField.setText(c.getEmail());
        });

        ModernButton updateBtn = new ModernButton("Update Guest");
        updateBtn.addActionListener(e -> {
            int viewRow = customerTable.getSelectedRow();
            if (viewRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a row to update.");
                return;
            }
            int modelRow = customerTable.convertRowIndexToModel(viewRow);

            Customer updated = new Customer(
                    idField.getText(),
                    nameField.getText(),
                    phoneField.getText(),
                    emailField.getText()
            );
            customerList.set(modelRow, updated);
            DataManager.save(customerList, "cust.dat");
            refreshTable();
        });

        ModernButton deleteBtn = new ModernButton("Delete Guest");
        deleteBtn.addActionListener(e -> {
            int viewRow = customerTable.getSelectedRow();
            if (viewRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a row to delete.");
                return;
            }
            int modelRow = customerTable.convertRowIndexToModel(viewRow);
            customerList.remove(modelRow);
            DataManager.save(customerList, "cust.dat");
            refreshTable();
        });

        inputPanel.add(loadBtn);
        inputPanel.add(updateBtn);
        inputPanel.add(deleteBtn);

        // Table with Filtering
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Phone", "Email"}, 0);
        customerTable = new JTable(tableModel);
        customerTable.setRowHeight(30);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        customerTable.setRowSorter(sorter);

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchField.getText()));
            }
        });

        bg.add(navPanel, BorderLayout.NORTH);
        bg.add(inputPanel, BorderLayout.WEST);
        bg.add(new JScrollPane(customerTable), BorderLayout.CENTER);

        refreshTable();
        setVisible(true);
    }

    private JLabel createWhiteLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(Color.WHITE);
        return l;
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for(Customer c : customerList) {
            tableModel.addRow(new Object[]{c.getCustomerId(), c.getName(), c.getPhone(), c.getEmail()});
        }
    }
}

// RESERVATION FORM
class ReservationForm extends JFrame {
    private JComboBox<Room> roomCombo;
    private JComboBox<Customer> guestCombo;
    private JTextField resIdField, totalPriceField, searchField;
    private JSpinner checkInSpinner, checkOutSpinner;
    private JTable resTable;
    private DefaultTableModel tableModel;
    private ArrayList<Reservation> reservationList;

    public ReservationForm() {
        reservationList = DataManager.getRes();
        setupUI();
    }

    private void setupUI() {
        setTitle("Reservation Management");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        BackgroundPanel bg = new BackgroundPanel("loasd.jpg");
        bg.setLayout(new BorderLayout());
        setContentPane(bg);

        // Header Panel
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setOpaque(false);
        ModernButton backBtn = new ModernButton("BACK");
        backBtn.setPreferredSize(new Dimension(100, 50));
        backBtn.addActionListener(e -> { new HomeFrame(); dispose(); });

        searchField = new JTextField(15);
        JPanel searchPanel = new JPanel(); 
        searchPanel.setOpaque(false);
        JLabel searchLbl = new JLabel("Search Res ID: "); 
        searchLbl.setForeground(Color.WHITE);
        searchPanel.add(searchLbl); 
        searchPanel.add(searchField);

        navPanel.add(backBtn, BorderLayout.WEST);
        navPanel.add(searchPanel, BorderLayout.EAST);

        // Input Layout
        JPanel inputGrid = new JPanel(new GridLayout(17, 1, 5, 5));
        inputGrid.setOpaque(false);
        inputGrid.setBorder(new EmptyBorder(10, 40, 10, 40));

        resIdField = new JTextField();
        roomCombo = new JComboBox<>(DataManager.getRooms().toArray(new Room[0]));
        guestCombo = new JComboBox<>(DataManager.getCust().toArray(new Customer[0]));

        checkInSpinner = new JSpinner(new SpinnerDateModel());
        checkOutSpinner = new JSpinner(new SpinnerDateModel());
        checkInSpinner.setEditor(new JSpinner.DateEditor(checkInSpinner, "yyyy-MM-dd"));
        checkOutSpinner.setEditor(new JSpinner.DateEditor(checkOutSpinner, "yyyy-MM-dd"));

        totalPriceField = new JTextField();

        inputGrid.add(createLabel("Reservation ID:")); inputGrid.add(resIdField);
        inputGrid.add(createLabel("Select Room:")); inputGrid.add(roomCombo);
        inputGrid.add(createLabel("Select Guest:")); inputGrid.add(guestCombo);
        inputGrid.add(createLabel("Check-In Date:")); inputGrid.add(checkInSpinner);
        inputGrid.add(createLabel("Check-Out Date:")); inputGrid.add(checkOutSpinner);
        inputGrid.add(createLabel("Total Amount ($):")); inputGrid.add(totalPriceField);

        ModernButton confirmBtn = new ModernButton("Confirm Booking");
        confirmBtn.addActionListener(e -> {
            try {
                reservationList.add(new Reservation(
                    resIdField.getText(), 
                    roomCombo.getSelectedItem().toString(), 
                    guestCombo.getSelectedItem().toString(), 
                    (Date)checkInSpinner.getValue(), 
                    (Date)checkOutSpinner.getValue(), 
                    Integer.parseInt(totalPriceField.getText())
                ));
                DataManager.save(reservationList, "res.dat"); 
                refreshTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Please check numeric fields.");
            }
        });
        inputGrid.add(confirmBtn);

        // NEW: Load, Update, Delete buttons
        ModernButton loadBtn = new ModernButton("Load Selected");
        loadBtn.addActionListener(e -> {
            int viewRow = resTable.getSelectedRow();
            if (viewRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a row to edit.");
                return;
            }
            int modelRow = resTable.convertRowIndexToModel(viewRow);
            Reservation r = reservationList.get(modelRow);

            resIdField.setText(r.getResId());
            selectComboItem(roomCombo, r.getRoomId());
            selectComboItem(guestCombo, r.getCustId());
            checkInSpinner.setValue(r.getCheckIn());
            checkOutSpinner.setValue(r.getCheckOut());
            totalPriceField.setText(String.valueOf(r.getTotal()));
        });

        ModernButton updateBtn = new ModernButton("Update Booking");
        updateBtn.addActionListener(e -> {
            int viewRow = resTable.getSelectedRow();
            if (viewRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a row to update.");
                return;
            }
            try {
                int modelRow = resTable.convertRowIndexToModel(viewRow);

                Reservation updated = new Reservation(
                        resIdField.getText(),
                        roomCombo.getSelectedItem().toString(),
                        guestCombo.getSelectedItem().toString(),
                        (Date) checkInSpinner.getValue(),
                        (Date) checkOutSpinner.getValue(),
                        Integer.parseInt(totalPriceField.getText())
                );
                reservationList.set(modelRow, updated);
                DataManager.save(reservationList, "res.dat");
                refreshTable();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please check numeric fields.");
            }
        });

        ModernButton deleteBtn = new ModernButton("Delete Booking");
        deleteBtn.addActionListener(e -> {
            int viewRow = resTable.getSelectedRow();
            if (viewRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a row to delete.");
                return;
            }
            int modelRow = resTable.convertRowIndexToModel(viewRow);
            reservationList.remove(modelRow);
            DataManager.save(reservationList, "res.dat");
            refreshTable();
        });

        inputGrid.add(loadBtn);
        inputGrid.add(updateBtn);
        inputGrid.add(deleteBtn);

        // Table UI
        tableModel = new DefaultTableModel(new String[]{"RID", "Room", "Guest", "In", "Out", "Total"}, 0);
        resTable = new JTable(tableModel);
        resTable.setRowHeight(30);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        resTable.setRowSorter(sorter);
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchField.getText()));
            }
        });

        bg.add(navPanel, BorderLayout.NORTH);
        bg.add(inputGrid, BorderLayout.WEST);
        bg.add(new JScrollPane(resTable), BorderLayout.CENTER);

        refreshTable();
        setVisible(true);
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(Color.WHITE);
        return l;
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for(Reservation r : reservationList) {
            tableModel.addRow(new Object[]{
                r.getResId(), r.getRoomId(), r.getCustId(), 
                r.getCheckIn(), r.getCheckOut(), r.getTotal()
            });
        }
    }

    // helper to select combo item by toString ID
    private void selectComboItem(JComboBox<?> combo, String idText) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            Object item = combo.getItemAt(i);
            if (item != null && item.toString().equals(idText)) {
                combo.setSelectedIndex(i);
                break;
            }
        }
    }
}

// thank you
