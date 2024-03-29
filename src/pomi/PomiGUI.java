/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package pomi;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
/**
 *
 * @author Roby
 */
import java.sql.*;
import com.formdev.flatlaf.FlatLightLaf; // import the desired FlatLaf theme
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.netbeans.lib.awtextra.AbsoluteConstraints;

public class PomiGUI extends javax.swing.JFrame {
    // SQLite database connection
    private Connection connection;
    private final String DATABASE_URL = "jdbc:sqlite:pomi.db";
    
    /**
     * Creates new form PomiGUI
     */
    public PomiGUI() {
        //initalizeMainScreen
        main_screen();
        
        //SplashScreen
        splashscreen();
                
    }
    
    //Default Parameters
    String[] workApps = new String[10];
    String[] funApps = new String[10];
    
    int userId;
    String name;
    int pet;
    
    int[] duration = {25, 5, 15};

    String[] taskManager = {"Task 1","Task 2","Task 3","Task 4","Task 5","Task 6","Task 6","Task 7"};

    private static final int MIN_USERNAME_LENGTH = 5; // Minimum username length
    private static final int MIN_PASSWORD_LENGTH = 8; // Minimum password length
    
    void main_screen(){
        // Set the frame to fullscreen
        setExtendedState(JFrame.MAXIMIZED_BOTH); 
        
        // Hide the title bar and borders before making it displayable
        setUndecorated(true);
        
        // Set the size of the frame to the screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        setSize(screenSize.width, screenSize.height);
        setPreferredSize(screenSize);

        initComponents();
        
        content_panel.setSize(screenSize.width, screenSize.height);
        content_panel.setPreferredSize(screenSize);

        //Center the Screen
        setLocationRelativeTo(null);
        
        // Connect to the database
        connectToDatabase();
        
        centerDialogs();

        //SET the icon 
        Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("logo.png"));
        setIconImage(icon);
        
        //Current Time
        dateTime(date, time);
        
        //Visibility
        playbutton.setVisible(false);
        setUITimerMode(false);
        successTimer.setVisible(false);
        
        //Notepad No Border
        jScrollPane1.setBorder(null);
        
    }

    private boolean signUp(String username, String password) {
        // Check if the username already exists
        if (usernameExists(username)) {
            System.err.println("Username already exists.");
            return false; // Sign up failed
        }

        // If the username doesn't exist, proceed with sign up
        String insertQuery = "INSERT INTO Account (username, password) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true; // Sign up successful
        } catch (SQLException e) {
            System.err.println("Error signing up: " + e.getMessage());
            return false; // Sign up failed
        }
    }

    private int signIn(String username, String password) {
        String selectQuery = "SELECT user_id FROM Account WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id"); // Return userId if sign-in successful
            } else {
                return -1; // Sign in failed
            }
        } catch (SQLException e) {
            System.err.println("Error signing in: " + e.getMessage());
            return -1; // Sign in failed
        }
    }
    
    // Helper method to check if a username already exists
    private boolean usernameExists(String username) {
        String query = "SELECT COUNT(*) FROM Account WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet resultSet = pstmt.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking username existence: " + e.getMessage());
        }
        return false;
    }
    private boolean validateInput(String username, String password) {
        // Check if username and password are not empty
        if (username.isEmpty() || password.isEmpty()) {
            showMessageDialog("Username and password cannot be empty.");
            return false;
        }

        // Check minimum character requirements for username and password
        if (username.length() < MIN_USERNAME_LENGTH || password.length() < MIN_PASSWORD_LENGTH) {
            showMessageDialog("Username and password must have at least " + MIN_USERNAME_LENGTH + " and " + MIN_PASSWORD_LENGTH + " characters respectively.");
            return false;
        }

        // Add any additional validation rules here, such as requiring special characters, etc.

        return true;
    }

    private JLabel backgroundLabel;

    void selectPet(int pet) {
        String[] petNames = {"pomi", "terry", "penny", "felix"};
        int index = Math.min(pet, petNames.length - 1);
        String petName = petNames[index];

        String basePath = Paths.get("resources", "pets", petName, petName).toString();

        // Load background image
//        ImageIcon backgroundImageIcon = loadImageIcon(basePath + "_background.jpg");
//        loadBackground(backgroundImageIcon);
        loadBackground(new ImageIcon(basePath + "_background.jpg"));
          
        // Load start button icon
//        Image startButtonImage = loadImage(basePath + "_start.png");
//      startButton.setIcon(new ImageIcon(startButtonImage));
        startButton.setIcon(new ImageIcon(basePath + "_start.png"));

        // Load success timer icon
//        Image timerImage = loadImage(basePath + "_timer.gif");
//        successTimer.setIcon(new ImageIcon(timerImage));
        successTimer.setIcon(new ImageIcon(basePath + "_timer.gif"));
        
        // Convert petName to title case for instruction text
        String petInstruction = petName.substring(0, 1).toUpperCase() + petName.substring(1);

        // Set instruction text based on selected pet
        instruct_pet.setText(String.format("Hi " + name + "!" + " Tap on %s to begin!", petInstruction));
    }

    void loadBackground(ImageIcon backgroundImageIcon) {
        // Remove previous background JLabel if exists
        if (backgroundLabel != null) {
            remove(backgroundLabel);
        }

        // Create a new JLabel with the new background image
        backgroundLabel = new JLabel(backgroundImageIcon);
        configureLabel(backgroundLabel);

        // Add the new background JLabel to the container with constraints
        add(backgroundLabel, new AbsoluteConstraints(0, 0, backgroundLabel.getPreferredSize().width, backgroundLabel.getPreferredSize().height));

        // Ensure the container revalidates and repaints to reflect the changes
        revalidate();
        repaint();
    }

    void mouseEnteredPet(int pet) {
        String[] petNames = {"pomi", "terry", "penny", "felix"};
        int index = Math.min(pet, petNames.length - 1);
        String petName = petNames[index];

        String basePath = Paths.get("resources", "pets", petName, petName).toString();

        // Load start button icon
//        Image startButtonImage = loadImage(basePath + "_hover.png");
//        startButton.setIcon(new ImageIcon(startButtonImage));
        startButton.setIcon(new ImageIcon(basePath + "_hover.png"));
    }

    void mouseExitedPet(int pet) {
        String[] petNames = {"pomi", "terry", "penny", "felix"};
        int index = Math.min(pet, petNames.length - 1);
        String petName = petNames[index];

        String basePath = Paths.get("resources", "pets", petName, petName).toString();

        // Load start button icon
//        Image startButtonImage = loadImage(basePath + "_start.png");
//        startButton.setIcon(new ImageIcon(startButtonImage));
        startButton.setIcon(new ImageIcon(basePath + "_start.png"));
    }

    private Image loadImage(String path) {
        InputStream inputStream = getClass().getResourceAsStream(path);
        if (inputStream != null) {
            try {
                return ImageIO.read(inputStream);
            } catch (IOException e) {
                System.out.println("Error loading image: " + e.getMessage());
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    System.out.println("Error closing input stream: " + e.getMessage());
                }
            }
        } else {
            System.out.println("Image not found: " + path);
        }
        return null;
    }

    private ImageIcon loadImageIcon(String path) {
        InputStream inputStream = getClass().getResourceAsStream(path);
        if (inputStream != null) {
            try {
                return new ImageIcon(ImageIO.read(inputStream));
            } catch (IOException e) {
                System.out.println("Error loading image icon: " + e.getMessage());
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    System.out.println("Error closing input stream: " + e.getMessage());
                }
            }
        } else {
            System.out.println("Image not found: " + path);
        }
        return null;
    }


    private void configureLabel(JLabel label) {
        label.setBounds(0, 0, label.getIcon().getIconWidth(), label.getIcon().getIconHeight());
        content_panel.setOpaque(false);
        content_panel.setBackground(new Color(0, 0, 0, 0));
        content_panel.setBorder(BorderFactory.createEmptyBorder());
        content_panel.add(label);
    }

    void connectToDatabase() {
        try {
            connection = DriverManager.getConnection(DATABASE_URL);
            createTablesIfNotExists();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    void createTablesIfNotExists() {
        String createAccountTable = "CREATE TABLE IF NOT EXISTS Account (\n"
                + " user_id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " username TEXT NOT NULL,\n"
                + " name TEXT,\n"
                + " password TEXT NOT NULL\n"
                + ");";

        String createPreferencesTable = "CREATE TABLE IF NOT EXISTS Preferences (\n"
                + " user_id INTEGER PRIMARY KEY,\n"
                + " pets INTEGER DEFAULT 0,\n"
                + " duration TEXT DEFAULT '[25,5,15]',\n"
                + " ringtone TEXT DEFAULT null,\n"
                + " work_apps JSON DEFAULT '[null, null, null, null, null, null, null, null, null, null]',\n"
                + " fun_apps JSON DEFAULT '[null, null, null, null, null, null, null, null, null, null]',\n"
                + " FOREIGN KEY(user_id) REFERENCES Account(user_id)\n"
                + ");";
        
        String createNotesTable = "CREATE TABLE IF NOT EXISTS Notes (\n"
                + " user_id INTEGER,\n"
                + " note TEXT,\n"
                + " last_updated TEXT,\n"
                + " date_created TEXT,\n"
                + " FOREIGN KEY(user_id) REFERENCES Account(user_id)\n"
                + ");";

        try {
            Statement statement = connection.createStatement();
            statement.execute(createAccountTable);
            statement.execute(createPreferencesTable);
            statement.execute(createNotesTable);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    void welcome(){
        namewelcome1.setText("Welcome " + name + "!");
        welcomepomodoro.setText(String.valueOf(duration[0]));
        welcomebreakpomodoro.setText(String.valueOf(duration[1]));
        welcomelongbreakpomodoro.setText(String.valueOf(duration[2]));
    }
    
    void centerDialogs(){
        //Dialogs Set Center
        splashscreen.setShape(new RoundRectangle2D.Double(0,0, 589,330, 20,20));
        splashscreen.setLocationRelativeTo(null);
        addname.setLocationRelativeTo(null);
        choosepet.setLocationRelativeTo(null);
        welcome.setLocationRelativeTo(null);
        settingsDialog.setLocationRelativeTo(null);
    
    }
 
    void splashscreen(){
        
        //Splashscreen
        splashscreen.setVisible(true);
        jLabel7.setText(
        "<html>"+ "The PomoPets app offers an adorable twist to the traditional Pomodoro experience. PomoPets stands out as one of the few project management tools integrating native time tracking, and quite possibly the sole one incorporating the delightful Pomodoro technique for time management." +"</html>"
        );
        jLabel8.setText(
        "<html>"+ "Its interface is refreshingly sleek compared to counterparts, presenting a default 25-minute timer with 5 and 15-minute breaks. Naturally, users have the flexibility to customize session durations and opt for audible alerts upon session or break completion, all from the Pomodoro settings." +"</html>"
        );
        jLabel6.setText(
        "<html>"+ "T-Mang proudly presents PomoPets, developed as a requisite for SEN02 - Software Engineering 2 at the Angeles University Foundation. All illustrations sourced from freepik.com are utilized in accordance with legal permissions." +"</html>"
        );

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
        @Override
                public void run() {
                int i = 0;
                try {
                    while (i <= 100) {
                        // fill the menu bar
                        progressbar.setValue(i + 10);

                        // delay the thread
                        Thread.sleep(1000);
                        i += 20;
                    }
                }
                catch (Exception e) {
                }
                splashscreen.setVisible(false);
                load_authentication();
            }
        }, 2000);
    }
    
    void load_authentication(){
        //SET the icon 
        Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("logo.png"));
        access.setIconImage(icon);
        access.setLocationRelativeTo(null);
        access.setVisible(true);
        
    }

    void saveProfile() {
        try {
            // Prepare SQL statement
            String updateQuery = "UPDATE Account SET name = ? WHERE user_id = ?";
            PreparedStatement pstmt = connection.prepareStatement(updateQuery);

            // Set parameters
            pstmt.setString(1, name);
            pstmt.setInt(2, userId); // Assuming userId is an int

            // Execute the update statement
            int rowsAffected = pstmt.executeUpdate();

            // Check if any rows were affected by the update
            if (rowsAffected > 0) {
                // Update successful
                System.out.println("Update successful");
            } else {
                // No rows were updated, handle this case appropriately
                System.out.println("No rows updated");
            }

            // Close PreparedStatement
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
        
        try {
            // Check if the user_id already exists in the Preferences table
            String selectQuery = "SELECT COUNT(*) AS count FROM Preferences WHERE user_id = ?";
            PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
            selectStmt.setInt(1, userId);
            ResultSet resultSet = selectStmt.executeQuery();
            
            // Format the duration array into a string
            StringBuilder durationBuilder = new StringBuilder();
            durationBuilder.append("[");
            for (int i = 0; i < duration.length; i++) {
                durationBuilder.append(duration[i]);
                if (i < duration.length - 1) {
                    durationBuilder.append(",");
                }
            }
            durationBuilder.append("]");
            String durationString = durationBuilder.toString();

            if(resultSet.next() && resultSet.getInt("count") > 0) {
                // User already exists in Preferences table, so update the record
                String updateQuery = "UPDATE Preferences SET pets = ?, duration = ? WHERE user_id = ?";
                PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
                updateStmt.setInt(1, pet);
                updateStmt.setString(2, durationString);
                updateStmt.setInt(3, userId);

                int rowsAffected = updateStmt.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Update successful");
                } else {
                    System.out.println("No rows updated");
                }

                updateStmt.close();
            } else {
                // User doesn't exist in Preferences table, so insert a new record
                String insertQuery = "INSERT INTO Preferences (user_id, pets, duration) VALUES (?, ?, ?)";
                PreparedStatement insertStmt = connection.prepareStatement(insertQuery);
                insertStmt.setInt(1, userId);
                insertStmt.setInt(2, pet);
                insertStmt.setString(3, durationString);

                int rowsAffected = insertStmt.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Insert successful");
                } else {
                    System.out.println("No rows inserted");
                }

                insertStmt.close();
            }

            // Close PreparedStatement for select statement
            selectStmt.close();
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
    }

    
    void loadProfile() {
        try {
            // Fetching user's name from Account table
            PreparedStatement statement = connection.prepareStatement("SELECT name FROM Account WHERE user_id = ?");
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            // Check if result set has any rows
            if (resultSet.next()) {
                name = resultSet.getString("name");

                // Check if name is null or empty
                if (name == null || name.isEmpty()) {
                    // Configure User Account
                    addname.setVisible(true);
                } else {
                    // Load user preferences
                    PreparedStatement preferencesStatement = connection.prepareStatement("SELECT pets, duration, work_apps, fun_apps FROM Preferences WHERE user_id = ?");
                    preferencesStatement.setInt(1, userId);
                    ResultSet preferencesResultSet = preferencesStatement.executeQuery();

                    // Check if preferences are found
                    if (preferencesResultSet.next()) {
                        // Update pet variable
                        pet = preferencesResultSet.getInt("pets");
                        selectPet(pet);

                        // Update duration variable
                        String durationPreference = preferencesResultSet.getString("duration");
                        int[] durationArray = parseDuration(durationPreference);
                        // Assuming duration variable is an array
                        duration = durationArray;
                        
                        try {
                            // Load work_apps JSON array
                            JSONArray workAppsArray = new JSONArray(preferencesResultSet.getString("work_apps"));
                            for (int i = 0; i < workAppsArray.length(); i++) {
                                workApps[i] = workAppsArray.getString(i);
                            }

                            // Load fun_apps JSON array
                            JSONArray funAppsArray = new JSONArray(preferencesResultSet.getString("fun_apps"));
                            for (int i = 0; i < funAppsArray.length(); i++) {
                                funApps[i] = funAppsArray.getString(i);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Handle JSONException appropriately
                        }

                        // Load Apps
                        reinstate();

                        // Task Bar
                        tasklists();

                        // Open Notes
                        openNotes();

                        // Set the main app visible
                        setVisible(true);
                        
                        // Load Settings
                        loadSettings();
                    }

                    // Close preferences result set and statement
                    preferencesResultSet.close();
                    preferencesStatement.close();
                }
            }

            // Close the result set and statement for name retrieval
            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    void loadSettings(){
        getDuration();
        selectPetRadioButton();
    }
    
    public void getDuration() {
        settingspomodoroduration.setText(String.valueOf(duration[0]));
        settingsbreakduration.setText(String.valueOf(duration[1]));
        settingslongbreakduration.setText(String.valueOf(duration[2]));
    }
    
    public void selectPetRadioButton() {
        switch (pet) {
            case 0:
                settingsChooseaPet.setSelected(choosepomi1.getModel(), true);
                break;
            case 1:
                settingsChooseaPet.setSelected(chooseterry1.getModel(), true);
                break;
            case 2:
                settingsChooseaPet.setSelected(choosepenny1.getModel(), true);
                break;
            case 3:
                settingsChooseaPet.setSelected(choosefelix1.getModel(), true);
                break;
            default:
                // Handle invalid pet value here, if needed
                break;
        }
    }
    
    
    // Method to parse the duration JSON string
    public int[] parseDuration(String durationJson) {
        try {
            // Parse JSON string into JSONArray
            JSONArray durationArray = new JSONArray(durationJson);

            // Convert JSONArray to int array
            int[] result = new int[durationArray.length()];
            for (int i = 0; i < durationArray.length(); i++) {
                result[i] = durationArray.getInt(i);
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            // Handle parsing exceptions appropriately, e.g., return a default value or throw an exception
            return new int[0]; // Return an empty array as a default
        }
    }
    
    
    // Insert or update a note
    void saveNotes() {
        try {
            String note = notes.getText();
            // Check if the note already exists
            String selectQuery = "SELECT * FROM Notes WHERE user_id = ?;";
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
            selectStatement.setInt(1, userId);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                // Note exists, update it
                String updateQuery = "UPDATE Notes SET note = ?, last_updated = CURRENT_TIMESTAMP WHERE user_id = ?;";
                PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                updateStatement.setString(1, note);
                updateStatement.setInt(2, userId);
                updateStatement.executeUpdate();
            } else {
                // Note doesn't exist, insert it
                String insertQuery = "INSERT INTO Notes (user_id, note, last_updated, date_created) VALUES (?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);";
                PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
                insertStatement.setInt(1, userId);
                insertStatement.setString(2, note);
                insertStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Retrieve notes and display in JTextArea
    void openNotes() {
        try {
            String selectQuery = "SELECT note FROM Notes WHERE user_id = ?;";
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
            selectStatement.setInt(1, userId);
            ResultSet resultSet = selectStatement.executeQuery();

            StringBuilder note = new StringBuilder();
            while (resultSet.next()) {
                note.append(resultSet.getString("note")).append("\n");
            }

            // Set the text in JTextArea
            notes.setText(note.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    void tasklists(){
        pomodorotasktextfield1.setText(taskManager[0]);
        pomodorotasktextfield2.setText(taskManager[1]);
        pomodorotasktextfield3.setText(taskManager[2]);
        pomodorotasktextfield4.setText(taskManager[3]);
        pomodorotasktextfield5.setText(taskManager[4]);
        pomodorotasktextfield6.setText(taskManager[5]);
        pomodorotasktextfield7.setText(taskManager[6]);
        pomodorotasktextfield8.setText(taskManager[7]);
        
        pomodorotasktime1.setText(addTime(0,set));
        pomodorotasktime2.setText(addTime(1,set));
        pomodorotasktime3.setText(addTime(2,set));
        pomodorotasktime4.setText(addTime(3,set));
        pomodorotasktime5.setText(addTime(4,set));
        pomodorotasktime6.setText(addTime(5,set));
        pomodorotasktime7.setText(addTime(6,set));
        pomodorotasktime8.setText(addTime(7,set));
    }

    String addTime(int index, int set) {
        // Calculate total time elapsed in minutes
        int totalMinutes = set * (duration[0] + duration[1]) + ((set / 4) * duration[2]);
        int taskMinutes = index * (duration[0] + duration[1]);
        totalMinutes += taskMinutes;

        // Calculate current time
        LocalTime currentTime = LocalTime.now().plusMinutes(totalMinutes);

        // Format the current time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
        return currentTime.format(formatter);
    }

    // Method to update date and time labels in a separate thread
    void dateTime(JLabel dateLabel, JLabel timeLabel) {
        Thread timeThread = new Thread(() -> {
            try {
                while (true) { // Infinite loop for continuous updating
                    // Update date label
                    Format dateFormat = new SimpleDateFormat("EEE, dd/MM/yyyy");
                    String currentDate = dateFormat.format(new Date());
                    dateLabel.setText(currentDate);

                    // Update time label
                    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
                    String currentTime = timeFormat.format(new Date(System.currentTimeMillis()));
                    timeLabel.setText(currentTime);

                    Thread.sleep(1000); // Pause for 1 second
                }
            } catch (InterruptedException e) {
                // Restore interrupted state if needed
                Thread.currentThread().interrupt();
            }
        });

        timeThread.start(); // Start the thread
    }
   
    // Method to update time label in a separate thread
    void TimeOnly(JLabel timeLabel) {
        Thread timeThread = new Thread(() -> {
            try {
                while (true) { // Infinite loop for continuous updating
                    // Update time label
                    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
                    String currentTime = timeFormat.format(new Date(System.currentTimeMillis()));
                    timeLabel.setText(currentTime);

                    Thread.sleep(1000); // Pause for 1 second
                }
            } catch (InterruptedException e) {
                // Restore interrupted state if needed
                Thread.currentThread().interrupt();
            }
        });

        timeThread.start(); // Start the thread
    }
    
    //OPEN Apps
    void OpenApp(JButton button, String filename){
        // Replacement using ProcessBuilder
        ProcessBuilder processBuilder = new ProcessBuilder(filename);
        try {
            Process process = processBuilder.start();
            // Optionally, you can wait for the process to finish
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Process executed successfully.");
            } else {
                System.err.println("Process failed with exit code: " + exitCode);
            }
        } catch (IOException e) {
            System.err.println("Error starting the process: " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Process execution interrupted: " + e.getMessage());
        }
    }
   
    void setIcon(String filename, JButton button){
        File myObj = new File(filename);
        ImageIcon icon = (ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(myObj);
        if (icon != null) {
            ImageIcon imageIcon = new ImageIcon(icon.getImage().getScaledInstance(35, 35, Image.SCALE_DEFAULT));
            button.setIcon(imageIcon);
            button.setText("");
        }
    }

    void addApp(int position, JButton button, boolean kindapp){
        if (button.getIcon() == null){
            FileDialog fileDialog = new FileDialog(this,"Open File", FileDialog.LOAD);
            fileDialog.setFile("*.exe");
            fileDialog.setVisible(true);
            if(fileDialog.getFile() != null)
            {
                if (kindapp){
                    funApps[position] = fileDialog.getDirectory()+ fileDialog.getFile();
                }else{
                    workApps[position] = fileDialog.getDirectory()+ fileDialog.getFile();
                }
                updateApp();
            }
            reinstate();
        }else{
            if (kindapp){
                OpenApp(button, funApps[position]);
            }else{
                OpenApp(button, workApps[position]);
            }
        }
        
    }
    
    void removeapp(int position, JButton button, boolean kindapp){
        if (kindapp){
            funApps[position] = null;
            button.setText("");
            button.setIcon(null);
            reinstate();        
        }else{
            workApps[position] = null;
            button.setText("");
            button.setIcon(null);
            reinstate();
        }
        updateApp();
    }
    
    void updateApp() {
        try {
            // Update work_apps in the Preferences table
            String updateWorkAppsQuery = "UPDATE Preferences SET work_apps = ? WHERE user_id = ?";
            PreparedStatement updateWorkAppsStatement = connection.prepareStatement(updateWorkAppsQuery);
            updateWorkAppsStatement.setString(1, convertArrayToJson(workApps));
            updateWorkAppsStatement.setInt(2, userId);
            updateWorkAppsStatement.executeUpdate();
            updateWorkAppsStatement.close();

            // Update fun_apps in the Preferences table
            String updateFunAppsQuery = "UPDATE Preferences SET fun_apps = ? WHERE user_id = ?";
            PreparedStatement updateFunAppsStatement = connection.prepareStatement(updateFunAppsQuery);
            updateFunAppsStatement.setString(1, convertArrayToJson(funApps));
            updateFunAppsStatement.setInt(2, userId);
            updateFunAppsStatement.executeUpdate();
            updateFunAppsStatement.close();

            // Optionally, you can commit the changes if you're working with transactions
            // connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to convert String array to JSON string
    public String convertArrayToJson(String[] array) {
        try {
            JSONArray jsonArray = new JSONArray(array);
            return jsonArray.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            // Handle JSONException appropriately, e.g., return an empty string or throw an exception
            return ""; // Return an empty string as a default
        }
    }
    
    //REFERESH Apps
    void reinstate() {
        int workIndex = 0;
        int funIndex = 0;
        for (Component c : appspanel.getComponents()) {
            if (c instanceof JPanel) {
                JPanel panel = (JPanel) c;
                for (Component comp : panel.getComponents()) {
                    if (comp instanceof JButton) {
                        JButton button = (JButton) comp;
                        String buttonName = button.getName();
                        if (buttonName != null) {
                            if (buttonName.startsWith("work_appbutton") && workApps[workIndex] != null) {
                                if (button.getIcon() == null) {
                                   setIcon(workApps[workIndex], button);
                                }
                                workIndex++;
                            } else if (buttonName.startsWith("fun_appbutton") && funApps[funIndex] != null) {
                                if (button.getIcon() == null) {
                                    setIcon(funApps[funIndex], button);
                                }
                                funIndex++;
                            }
                        }
                    }
                }
            }
        }
        workIndex = 0;
        funIndex = 0;
    }

    //DIASBLE BUTTONS in POMODORO
    void disableEnter(){
        int funIndex = 0;
        for (Component c : appspanel.getComponents()) {
            if (c instanceof JPanel) {
                JPanel panel = (JPanel) c;
                for (Component comp : panel.getComponents()) {
                    if (comp instanceof JButton) {
                        JButton button = (JButton) comp;
                        String buttonName = button.getName();
                        if (buttonName != null) {
                            if (buttonName.startsWith("fun_appbutton")) {
                                button.setEnabled(false);
                                funIndex++;
                            }
                            
                            if (buttonName.startsWith("fun_removeapp")) {
                                button.setVisible(false);
                            }
                            
                        }
                    }
                }
            }
        }
        funIndex = 0;
    }
    
    //ENABLE BUTTONS in POMODORO
    void enableEnter(){
        int funIndex = 0;
        for (Component c : appspanel.getComponents()) {
            if (c instanceof JPanel) {
                JPanel panel = (JPanel) c;
                for (Component comp : panel.getComponents()) {
                    if (comp instanceof JButton) {
                        JButton button = (JButton) comp;
                        String buttonName = button.getName();
                        if (buttonName != null) {
                            if (buttonName.startsWith("fun_appbutton")) {
                                button.setEnabled(true);
                                funIndex++;
                            }
                            if (buttonName.startsWith("fun_removeapp")) {
                                button.setVisible(true);
                            }
                        }
                    }
                }
            }
        }
        funIndex = 0;
    }
    
    String stringConvert(int millis) {
        return String.format("%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(millis),
            TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
            TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }

    int secondsToMillis(int seconds) {
        return seconds * 1000;
    }
    
    int set = 0 ;
    int iteration = 0;
    int seconds = 0;
    int lastSecond = 0;
    
    boolean state = false;
    
    void timerpomodoro(int start, int end) {
        state = true;
        seconds = start;
        Thread t = new Thread(() -> {
            for (int x = 0; x < 100; x--) {
                if (state) {
                    try {
                        Thread.sleep(1000);
                        if (seconds <= end) {
                            if (seconds == end - 60) { // Notify when 60 seconds remaining
                                notifyUser("Time is almost up! 1 minute remaining.");
                            }
                            timerpomodoro.setText(stringConvert(secondsToMillis(seconds)));
                            seconds++;
                        } else {
                            seconds = 0;
                            iteration++;
                            if (iteration >= 16) {
                                reset();
                            } else {
                                stopTimer();
                                happytime();
                                playMusic();
                            }
                            break;
                        }
                    } catch (InterruptedException ignored) {
                    }
                } else {
                    lastSecond = end;
                    break;
                }
            }
        });
        t.start();
    }

    // Method to display a notification to the user
    private void notifyUser(String message) {
        new Thread(() -> {
            Toolkit.getDefaultToolkit().beep(); // Make a beep sound
            JOptionPane.showMessageDialog(null, message, "Notification", JOptionPane.INFORMATION_MESSAGE);
        }).start();
    }
    
    void startTimer() {
        if (!state) {
            state = true;
            setUITimerMode(true);
            setUICasualMode(false);
            
            taskpomodoro.setText(taskManager[set]);
            
            tasklists();
            
            timerpomodoro(0, duration[0] * 60);
            maxtimepomodoro.setText(stringConvert(secondsToMillis(duration[0] * 60)));
            disableEnter();
            
        }
    }

    void pauseTimer() {
        state = false;
        setUITimerMode(false);
        playbutton.setVisible(true);
    }

    void stopTimer() {
        state = false;
        seconds = 0;
    }

    void happytime() {
        setUITimerMode(false);
        successTimer.setVisible(true);
    }

    void reset() {
        setUITimerMode(false);
        setUICasualMode(true);
        
        selectPet(pet);
        enableEnter();
        instruct_pet1.setText("Please place all your apps on the app launcher.");
        seconds = 0;
        set = 0;
        iteration = 0;
        
        resetPomodoroIcons();
    }
    
    void resetPomodoroIcons() {
        String notSetIcon = Paths.get("resources", "clocks","notset circle.png").toString();
        pomodoroset1.setIcon(new ImageIcon(notSetIcon));
        pomodoroset2.setIcon(new ImageIcon(notSetIcon));
        pomodoroset3.setIcon(new ImageIcon(notSetIcon));
        pomodoroset4.setIcon(new ImageIcon(notSetIcon));
        pomodoroset5.setIcon(new ImageIcon(notSetIcon));
        pomodoroset6.setIcon(new ImageIcon(notSetIcon));
        pomodoroset7.setIcon(new ImageIcon(notSetIcon));
        pomodoroset8.setIcon(new ImageIcon(notSetIcon));
    }

    void setUITimerMode(boolean visible) {
        timerpomodoro1.setVisible(visible);
        resetpomodoro.setVisible(visible);
        maxtimepomodoro.setVisible(visible);
        taskpomodoro.setVisible(visible);
        timerpomodoro.setVisible(visible);
        setspomodoro.setVisible(visible);
    }
    
    void setUICasualMode(boolean visible) {
        startButton.setVisible(visible);
        instruct_pet.setVisible(visible);
        instruct_pet1.setVisible(visible);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        splashscreen = new javax.swing.JDialog();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        preview = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        progressbar = new javax.swing.JProgressBar();
        addname = new javax.swing.JDialog();
        jPanel1 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jPanel19 = new javax.swing.JPanel();
        user = new javax.swing.JTextField();
        jPanel20 = new javax.swing.JPanel();
        okname = new javax.swing.JButton();
        choosepet = new javax.swing.JDialog();
        jPanel3 = new javax.swing.JPanel();
        jPanel21 = new javax.swing.JPanel();
        namewelcome4 = new javax.swing.JLabel();
        namewelcome = new javax.swing.JLabel();
        jPanel22 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        choosepomi = new javax.swing.JRadioButton();
        jPanel9 = new javax.swing.JPanel();
        chooseterry = new javax.swing.JRadioButton();
        jLabel28 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        choosepenny = new javax.swing.JRadioButton();
        jLabel29 = new javax.swing.JLabel();
        jPanel38 = new javax.swing.JPanel();
        jRadioButton4 = new javax.swing.JRadioButton();
        jLabel30 = new javax.swing.JLabel();
        jPanel23 = new javax.swing.JPanel();
        choosepet_button = new javax.swing.JButton();
        settingsDialog = new javax.swing.JDialog();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel18 = new javax.swing.JPanel();
        backsettings = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jPanel24 = new javax.swing.JPanel();
        jPanel47 = new javax.swing.JPanel();
        Jlabel19 = new javax.swing.JLabel();
        jPanel25 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jPanel40 = new javax.swing.JPanel();
        settingspomodoroduration = new javax.swing.JTextField();
        Jlabel11 = new javax.swing.JLabel();
        Jlabel9 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jPanel39 = new javax.swing.JPanel();
        settingsbreakduration = new javax.swing.JTextField();
        Jlabel14 = new javax.swing.JLabel();
        Jlabel12 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jPanel26 = new javax.swing.JPanel();
        settingslongbreakduration = new javax.swing.JTextField();
        Jlabel17 = new javax.swing.JLabel();
        Jlabel15 = new javax.swing.JLabel();
        jPanel48 = new javax.swing.JPanel();
        Jlabel20 = new javax.swing.JLabel();
        jPanel42 = new javax.swing.JPanel();
        jPanel43 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        choosepomi1 = new javax.swing.JRadioButton();
        jPanel44 = new javax.swing.JPanel();
        chooseterry1 = new javax.swing.JRadioButton();
        jLabel31 = new javax.swing.JLabel();
        jPanel45 = new javax.swing.JPanel();
        choosepenny1 = new javax.swing.JRadioButton();
        jLabel32 = new javax.swing.JLabel();
        canvas3 = new java.awt.Canvas();
        jPanel46 = new javax.swing.JPanel();
        choosefelix1 = new javax.swing.JRadioButton();
        jLabel33 = new javax.swing.JLabel();
        jPanel41 = new javax.swing.JPanel();
        accountResetButton = new javax.swing.JButton();
        access = new javax.swing.JFrame();
        signinOrsignup = new javax.swing.JTabbedPane();
        signin_panel = new java.awt.Panel();
        jPanel6 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        signin_username_field = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        signin_password_field = new javax.swing.JPasswordField();
        jPanel14 = new javax.swing.JPanel();
        signin_button = new javax.swing.JButton();
        canvas1 = new java.awt.Canvas();
        signup_panel = new java.awt.Panel();
        jPanel15 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        signup_username_field = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        signup_password_field = new javax.swing.JPasswordField();
        jPanel17 = new javax.swing.JPanel();
        signup_button = new javax.swing.JButton();
        canvas2 = new java.awt.Canvas();
        welcome = new javax.swing.JDialog();
        jPanel27 = new javax.swing.JPanel();
        jPanel28 = new javax.swing.JPanel();
        namewelcome1 = new javax.swing.JLabel();
        namewelcome3 = new javax.swing.JLabel();
        jPanel29 = new javax.swing.JPanel();
        jPanel35 = new javax.swing.JPanel();
        Jlabel1 = new javax.swing.JLabel();
        jPanel36 = new javax.swing.JPanel();
        welcomepomodoro = new javax.swing.JTextField();
        Jlabel25 = new javax.swing.JLabel();
        jPanel33 = new javax.swing.JPanel();
        Jlabel4 = new javax.swing.JLabel();
        jPanel34 = new javax.swing.JPanel();
        welcomebreakpomodoro = new javax.swing.JTextField();
        Jlabel24 = new javax.swing.JLabel();
        jPanel31 = new javax.swing.JPanel();
        Jlabel7 = new javax.swing.JLabel();
        jPanel32 = new javax.swing.JPanel();
        welcomelongbreakpomodoro = new javax.swing.JTextField();
        Jlabel21 = new javax.swing.JLabel();
        jPanel37 = new javax.swing.JPanel();
        okwelcome = new javax.swing.JButton();
        chooseapet = new javax.swing.ButtonGroup();
        settingsChooseaPet = new javax.swing.ButtonGroup();
        content_panel = new javax.swing.JPanel();
        appspanel = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        work_app1 = new javax.swing.JPanel();
        work_removeapp1 = new javax.swing.JButton();
        work_appbutton1 = new javax.swing.JButton();
        fun_app1 = new javax.swing.JPanel();
        fun_removeapp1 = new javax.swing.JButton();
        fun_appbutton1 = new javax.swing.JButton();
        work_app2 = new javax.swing.JPanel();
        work_removeapp2 = new javax.swing.JButton();
        work_appbutton2 = new javax.swing.JButton();
        fun_app2 = new javax.swing.JPanel();
        fun_removeapp2 = new javax.swing.JButton();
        fun_appbutton2 = new javax.swing.JButton();
        work_app3 = new javax.swing.JPanel();
        work_removeapp3 = new javax.swing.JButton();
        work_appbutton3 = new javax.swing.JButton();
        fun_app3 = new javax.swing.JPanel();
        fun_removeapp3 = new javax.swing.JButton();
        fun_appbutton3 = new javax.swing.JButton();
        work_app4 = new javax.swing.JPanel();
        work_removeapp4 = new javax.swing.JButton();
        work_appbutton4 = new javax.swing.JButton();
        fun_app4 = new javax.swing.JPanel();
        fun_removeapp4 = new javax.swing.JButton();
        fun_appbutton4 = new javax.swing.JButton();
        work_app5 = new javax.swing.JPanel();
        work_removeapp5 = new javax.swing.JButton();
        work_appbutton5 = new javax.swing.JButton();
        fun_app5 = new javax.swing.JPanel();
        fun_removeapp5 = new javax.swing.JButton();
        fun_appbutton5 = new javax.swing.JButton();
        work_app6 = new javax.swing.JPanel();
        work_removeapp6 = new javax.swing.JButton();
        work_appbutton6 = new javax.swing.JButton();
        fun_app6 = new javax.swing.JPanel();
        fun_removeapp6 = new javax.swing.JButton();
        fun_appbutton6 = new javax.swing.JButton();
        work_app7 = new javax.swing.JPanel();
        work_removeapp7 = new javax.swing.JButton();
        work_appbutton7 = new javax.swing.JButton();
        fun_app7 = new javax.swing.JPanel();
        fun_removeapp7 = new javax.swing.JButton();
        fun_appbutton7 = new javax.swing.JButton();
        work_app8 = new javax.swing.JPanel();
        work_removeapp8 = new javax.swing.JButton();
        work_appbutton8 = new javax.swing.JButton();
        fun_app8 = new javax.swing.JPanel();
        fun_removeapp8 = new javax.swing.JButton();
        fun_appbutton8 = new javax.swing.JButton();
        work_app9 = new javax.swing.JPanel();
        work_removeapp9 = new javax.swing.JButton();
        work_appbutton9 = new javax.swing.JButton();
        fun_app9 = new javax.swing.JPanel();
        fun_removeapp9 = new javax.swing.JButton();
        fun_appbutton9 = new javax.swing.JButton();
        work_app10 = new javax.swing.JPanel();
        work_removeapp10 = new javax.swing.JButton();
        work_appbutton10 = new javax.swing.JButton();
        fun_app10 = new javax.swing.JPanel();
        fun_removeapp10 = new javax.swing.JButton();
        fun_appbutton10 = new javax.swing.JButton();
        actions = new javax.swing.JPanel();
        jPanel49 = new javax.swing.JPanel();
        settings = new javax.swing.JButton();
        exit = new javax.swing.JButton();
        timer_panel = new javax.swing.JPanel();
        heading_panel = new javax.swing.JPanel();
        instruct_pet = new javax.swing.JLabel();
        instruct_pet1 = new javax.swing.JLabel();
        clock = new javax.swing.JPanel();
        successTimer = new javax.swing.JButton();
        startButton = new javax.swing.JButton();
        maxtimepomodoro = new javax.swing.JLabel();
        timerpomodoro = new javax.swing.JLabel();
        taskpomodoro = new javax.swing.JLabel();
        timerpomodoro1 = new javax.swing.JButton();
        resetpomodoro = new javax.swing.JButton();
        playbutton = new javax.swing.JButton();
        setspomodoro = new javax.swing.JPanel();
        pomodoroset1 = new javax.swing.JLabel();
        pomodoroset2 = new javax.swing.JLabel();
        pomodoroset3 = new javax.swing.JLabel();
        pomodoroset4 = new javax.swing.JLabel();
        blank = new javax.swing.JLabel();
        pomodoroset5 = new javax.swing.JLabel();
        pomodoroset6 = new javax.swing.JLabel();
        pomodoroset7 = new javax.swing.JLabel();
        pomodoroset8 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        workspace_panel = new javax.swing.JPanel();
        time_date_panel = new javax.swing.JPanel();
        timeicon = new javax.swing.JLabel();
        jPanel30 = new javax.swing.JPanel();
        time = new javax.swing.JLabel();
        date = new javax.swing.JLabel();
        taskslist = new javax.swing.JPanel();
        task1 = new javax.swing.JPanel();
        pomodorotasktime1 = new javax.swing.JLabel();
        pomodorotasktextfield1 = new javax.swing.JTextField();
        task2 = new javax.swing.JPanel();
        pomodorotasktime2 = new javax.swing.JLabel();
        pomodorotasktextfield2 = new javax.swing.JTextField();
        task3 = new javax.swing.JPanel();
        pomodorotasktime3 = new javax.swing.JLabel();
        pomodorotasktextfield3 = new javax.swing.JTextField();
        task4 = new javax.swing.JPanel();
        pomodorotasktime4 = new javax.swing.JLabel();
        pomodorotasktextfield4 = new javax.swing.JTextField();
        task5 = new javax.swing.JPanel();
        pomodorotasktime5 = new javax.swing.JLabel();
        pomodorotasktextfield5 = new javax.swing.JTextField();
        task6 = new javax.swing.JPanel();
        pomodorotasktime6 = new javax.swing.JLabel();
        pomodorotasktextfield6 = new javax.swing.JTextField();
        task7 = new javax.swing.JPanel();
        pomodorotasktime7 = new javax.swing.JLabel();
        pomodorotasktextfield7 = new javax.swing.JTextField();
        task8 = new javax.swing.JPanel();
        pomodorotasktime8 = new javax.swing.JLabel();
        pomodorotasktextfield8 = new javax.swing.JTextField();
        stickynotes = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        notes = new javax.swing.JTextArea();

        splashscreen.setUndecorated(true);
        splashscreen.setResizable(false);
        splashscreen.setSize(new java.awt.Dimension(589, 340));

        jPanel2.setBackground(new java.awt.Color(96, 0, 0));

        jLabel1.setFont(new java.awt.Font("SF Pro Display", 1, 28)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(233, 178, 178));
        jLabel1.setText("PomoPets");

        jLabel2.setFont(new java.awt.Font("SF Pro Display", 0, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(233, 178, 178));
        jLabel2.setText("Version 1.0");

        preview.setBackground(new java.awt.Color(96, 0, 0));
        preview.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pomi/resources/loadingscreen/splashscreenonce.gif"))); // NOI18N
        preview.setOpaque(true);

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pomi/resources/loadingscreen/icon.png"))); // NOI18N

        jLabel5.setFont(new java.awt.Font("SF Pro Display", 0, 8)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(233, 178, 178));
        jLabel5.setText("@ 2023-2024 T-MANG. All rights reserved.");

        jLabel6.setFont(new java.awt.Font("SF Pro Display", 0, 8)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(233, 178, 178));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel6.setText("Infos");
        jLabel6.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel7.setFont(new java.awt.Font("SF Pro Display", 0, 8)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(233, 178, 178));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel7.setText("Infos");
        jLabel7.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel8.setFont(new java.awt.Font("SF Pro Display", 0, 8)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(233, 178, 178));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel8.setText("Infos");
        jLabel8.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        progressbar.setBackground(new java.awt.Color(233, 178, 178));
        progressbar.setForeground(new java.awt.Color(96, 0, 0));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel5)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(progressbar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(preview, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(jLabel1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressbar, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(preview, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout splashscreenLayout = new javax.swing.GroupLayout(splashscreen.getContentPane());
        splashscreen.getContentPane().setLayout(splashscreenLayout);
        splashscreenLayout.setHorizontalGroup(
            splashscreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        splashscreenLayout.setVerticalGroup(
            splashscreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        addname.setBackground(new java.awt.Color(255, 255, 255));
        addname.setUndecorated(true);
        addname.setResizable(false);
        addname.setSize(new java.awt.Dimension(676, 298));
        addname.getContentPane().setLayout(new javax.swing.BoxLayout(addname.getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(50, 50, 50, 50));
        jPanel1.setPreferredSize(new java.awt.Dimension(676, 298));
        jPanel1.setLayout(new java.awt.GridLayout(3, 1));

        jLabel9.setFont(new java.awt.Font("Poppins", 0, 36)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(96, 0, 0));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Hello there! What's your name?");
        jPanel1.add(jLabel9);

        jPanel19.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 0)));
        jPanel19.setLayout(new java.awt.GridLayout(1, 0));

        user.setBackground(new java.awt.Color(255, 255, 255));
        user.setFont(new java.awt.Font("Poppins", 0, 24)); // NOI18N
        user.setForeground(new java.awt.Color(96, 0, 0));
        user.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        user.setBorder(null);
        user.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                userKeyReleased(evt);
            }
        });
        jPanel19.add(user);

        jPanel1.add(jPanel19);

        jPanel20.setBackground(new java.awt.Color(255, 255, 255));
        jPanel20.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 130, 1, 130));
        jPanel20.setLayout(new java.awt.GridLayout(1, 0));

        okname.setBackground(new java.awt.Color(96, 0, 0));
        okname.setFont(new java.awt.Font("SF Pro Display", 0, 18)); // NOI18N
        okname.setForeground(new java.awt.Color(255, 255, 255));
        okname.setText("Ok!");
        okname.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        okname.setBorderPainted(false);
        okname.setEnabled(false);
        okname.setFocusPainted(false);
        okname.setFocusable(false);
        okname.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                oknameActionPerformed(evt);
            }
        });
        jPanel20.add(okname);

        jPanel1.add(jPanel20);

        addname.getContentPane().add(jPanel1);

        choosepet.setUndecorated(true);
        choosepet.setResizable(false);
        choosepet.setSize(new java.awt.Dimension(624, 371));
        choosepet.getContentPane().setLayout(new java.awt.GridLayout(1, 0));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 0, 20));
        jPanel3.setPreferredSize(new java.awt.Dimension(624, 371));
        jPanel3.setLayout(new java.awt.GridLayout(3, 1));

        jPanel21.setBackground(new java.awt.Color(255, 255, 255));
        jPanel21.setLayout(new java.awt.GridLayout(2, 1));

        namewelcome4.setFont(new java.awt.Font("SF Pro Display", 0, 36)); // NOI18N
        namewelcome4.setForeground(new java.awt.Color(96, 0, 0));
        namewelcome4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        namewelcome4.setText("Choose Your Productivity Pal!");
        namewelcome4.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jPanel21.add(namewelcome4);

        namewelcome.setFont(new java.awt.Font("SF Pro Display", 0, 14)); // NOI18N
        namewelcome.setForeground(new java.awt.Color(96, 0, 0));
        namewelcome.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        namewelcome.setText("Meet Your Adorable Pomodoro Companions");
        namewelcome.setToolTipText("");
        namewelcome.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel21.add(namewelcome);

        jPanel3.add(jPanel21);

        jPanel22.setBackground(new java.awt.Color(255, 255, 255));
        jPanel22.setLayout(new java.awt.GridLayout(1, 3, 20, 0));

        jPanel10.setBackground(new java.awt.Color(227, 227, 227));
        jPanel10.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel10.setLayout(new java.awt.BorderLayout());

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pomi/resources/pets/pomi/pomi_icon.png"))); // NOI18N
        jLabel10.setToolTipText("");
        jLabel10.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel10.add(jLabel10, java.awt.BorderLayout.CENTER);

        chooseapet.add(choosepomi);
        choosepomi.setForeground(new java.awt.Color(96, 0, 0));
        choosepomi.setSelected(true);
        choosepomi.setText("Pomi");
        choosepomi.setToolTipText("");
        choosepomi.setName("pomi"); // NOI18N
        jPanel10.add(choosepomi, java.awt.BorderLayout.NORTH);

        jPanel22.add(jPanel10);

        jPanel9.setBackground(new java.awt.Color(227, 227, 227));
        jPanel9.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel9.setLayout(new java.awt.BorderLayout());

        chooseapet.add(chooseterry);
        chooseterry.setForeground(new java.awt.Color(96, 0, 0));
        chooseterry.setText("Terry");
        chooseterry.setName("terry"); // NOI18N
        jPanel9.add(chooseterry, java.awt.BorderLayout.NORTH);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pomi/resources/pets/terry/terry_icon.png"))); // NOI18N
        jLabel28.setToolTipText("");
        jLabel28.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel9.add(jLabel28, java.awt.BorderLayout.CENTER);

        jPanel22.add(jPanel9);

        jPanel8.setBackground(new java.awt.Color(227, 227, 227));
        jPanel8.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel8.setName(""); // NOI18N
        jPanel8.setLayout(new java.awt.BorderLayout());

        chooseapet.add(choosepenny);
        choosepenny.setForeground(new java.awt.Color(96, 0, 0));
        choosepenny.setText("Penny");
        choosepenny.setName("penny"); // NOI18N
        jPanel8.add(choosepenny, java.awt.BorderLayout.NORTH);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pomi/resources/pets/penny/penny_icon.png"))); // NOI18N
        jLabel29.setToolTipText("");
        jLabel29.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel8.add(jLabel29, java.awt.BorderLayout.CENTER);

        jPanel22.add(jPanel8);

        jPanel38.setBackground(new java.awt.Color(227, 227, 227));
        jPanel38.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel38.setName(""); // NOI18N
        jPanel38.setLayout(new java.awt.BorderLayout());

        chooseapet.add(jRadioButton4);
        jRadioButton4.setForeground(new java.awt.Color(96, 0, 0));
        jRadioButton4.setText("Felix");
        jRadioButton4.setName("felix"); // NOI18N
        jPanel38.add(jRadioButton4, java.awt.BorderLayout.NORTH);

        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pomi/resources/pets/felix/felix_icon.png"))); // NOI18N
        jLabel30.setToolTipText("");
        jLabel30.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel38.add(jLabel30, java.awt.BorderLayout.CENTER);

        jPanel22.add(jPanel38);

        jPanel3.add(jPanel22);

        jPanel23.setBackground(new java.awt.Color(255, 255, 255));
        jPanel23.setBorder(javax.swing.BorderFactory.createEmptyBorder(40, 220, 40, 220));
        jPanel23.setLayout(new java.awt.GridLayout(1, 0));

        choosepet_button.setBackground(new java.awt.Color(76, 90, 35));
        choosepet_button.setFont(new java.awt.Font("SF Pro Display", 0, 18)); // NOI18N
        choosepet_button.setForeground(new java.awt.Color(255, 255, 255));
        choosepet_button.setText("Choose");
        choosepet_button.setBorderPainted(false);
        choosepet_button.setFocusPainted(false);
        choosepet_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                choosepet_buttonActionPerformed(evt);
            }
        });
        jPanel23.add(choosepet_button);

        jPanel3.add(jPanel23);

        choosepet.getContentPane().add(jPanel3);

        settingsDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        settingsDialog.setUndecorated(true);
        settingsDialog.setResizable(false);
        settingsDialog.setSize(new java.awt.Dimension(524, 577));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setPreferredSize(new java.awt.Dimension(524, 577));
        jPanel4.setLayout(new java.awt.BorderLayout());

        jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 0, 10, 0));
        jPanel5.setOpaque(false);
        jPanel5.setLayout(new java.awt.BorderLayout());

        jPanel18.setOpaque(false);
        jPanel18.setLayout(new java.awt.GridLayout(1, 0));

        backsettings.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pomi/resources/settings/backarrow.png"))); // NOI18N
        backsettings.setContentAreaFilled(false);
        backsettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backsettingsActionPerformed(evt);
            }
        });
        jPanel18.add(backsettings);

        jLabel11.setFont(new java.awt.Font("SF Pro Display", 0, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 0, 0));
        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pomi/resources/settings/settings.png"))); // NOI18N
        jLabel11.setText("Settings");
        jPanel18.add(jLabel11);

        jPanel5.add(jPanel18, java.awt.BorderLayout.WEST);

        jPanel4.add(jPanel5, java.awt.BorderLayout.NORTH);

        jPanel24.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 30, 10, 30));
        jPanel24.setOpaque(false);
        jPanel24.setLayout(new java.awt.GridLayout(2, 1, 0, 50));

        jPanel47.setOpaque(false);
        jPanel47.setLayout(new java.awt.BorderLayout(0, 10));

        Jlabel19.setFont(new java.awt.Font("SF Pro Display", 0, 24)); // NOI18N
        Jlabel19.setForeground(new java.awt.Color(96, 0, 0));
        Jlabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Jlabel19.setText("Duration");
        Jlabel19.setToolTipText("");
        jPanel47.add(Jlabel19, java.awt.BorderLayout.NORTH);

        jPanel25.setOpaque(false);
        jPanel25.setLayout(new java.awt.GridLayout(1, 0, 30, 0));

        jPanel11.setBackground(new java.awt.Color(227, 227, 227));
        jPanel11.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 0, 10));
        jPanel11.setLayout(new java.awt.GridLayout(3, 1));

        jPanel40.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(96, 0, 0)));
        jPanel40.setLayout(new java.awt.GridLayout(1, 0));

        settingspomodoroduration.setBackground(new java.awt.Color(227, 227, 227));
        settingspomodoroduration.setFont(new java.awt.Font("SF Pro Display", 0, 36)); // NOI18N
        settingspomodoroduration.setForeground(new java.awt.Color(96, 0, 0));
        settingspomodoroduration.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        settingspomodoroduration.setText("25");
        settingspomodoroduration.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        settingspomodoroduration.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                settingspomodorodurationKeyReleased(evt);
            }
        });
        jPanel40.add(settingspomodoroduration);

        jPanel11.add(jPanel40);

        Jlabel11.setFont(new java.awt.Font("SF Pro Display", 0, 14)); // NOI18N
        Jlabel11.setForeground(new java.awt.Color(96, 0, 0));
        Jlabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Jlabel11.setText("minutes");
        Jlabel11.setToolTipText("");
        Jlabel11.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel11.add(Jlabel11);

        Jlabel9.setFont(new java.awt.Font("SF Pro Display", 1, 12)); // NOI18N
        Jlabel9.setForeground(new java.awt.Color(96, 0, 0));
        Jlabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Jlabel9.setText("POMODORO");
        Jlabel9.setToolTipText("");
        Jlabel9.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel11.add(Jlabel9);

        jPanel25.add(jPanel11);

        jPanel12.setBackground(new java.awt.Color(227, 227, 227));
        jPanel12.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 0, 10));
        jPanel12.setLayout(new java.awt.GridLayout(3, 1));

        jPanel39.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(96, 0, 0)));
        jPanel39.setOpaque(false);
        jPanel39.setLayout(new java.awt.GridLayout(1, 0));

        settingsbreakduration.setBackground(new java.awt.Color(227, 227, 227));
        settingsbreakduration.setFont(new java.awt.Font("SF Pro Display", 0, 36)); // NOI18N
        settingsbreakduration.setForeground(new java.awt.Color(96, 0, 0));
        settingsbreakduration.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        settingsbreakduration.setText("25");
        settingsbreakduration.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        settingsbreakduration.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                settingsbreakdurationKeyReleased(evt);
            }
        });
        jPanel39.add(settingsbreakduration);

        jPanel12.add(jPanel39);

        Jlabel14.setFont(new java.awt.Font("SF Pro Display", 0, 14)); // NOI18N
        Jlabel14.setForeground(new java.awt.Color(96, 0, 0));
        Jlabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Jlabel14.setText("minutes");
        Jlabel14.setToolTipText("");
        Jlabel14.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel12.add(Jlabel14);

        Jlabel12.setFont(new java.awt.Font("SF Pro Display", 1, 12)); // NOI18N
        Jlabel12.setForeground(new java.awt.Color(96, 0, 0));
        Jlabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Jlabel12.setText("Break");
        Jlabel12.setToolTipText("");
        Jlabel12.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel12.add(Jlabel12);

        jPanel25.add(jPanel12);

        jPanel13.setBackground(new java.awt.Color(227, 227, 227));
        jPanel13.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 0, 10));
        jPanel13.setLayout(new java.awt.GridLayout(3, 1));

        jPanel26.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(96, 0, 0)));
        jPanel26.setLayout(new java.awt.GridLayout(1, 0));

        settingslongbreakduration.setBackground(new java.awt.Color(227, 227, 227));
        settingslongbreakduration.setFont(new java.awt.Font("SF Pro Display", 0, 36)); // NOI18N
        settingslongbreakduration.setForeground(new java.awt.Color(96, 0, 0));
        settingslongbreakduration.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        settingslongbreakduration.setText("25");
        settingslongbreakduration.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        settingslongbreakduration.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                settingslongbreakdurationKeyReleased(evt);
            }
        });
        jPanel26.add(settingslongbreakduration);

        jPanel13.add(jPanel26);

        Jlabel17.setFont(new java.awt.Font("SF Pro Display", 0, 14)); // NOI18N
        Jlabel17.setForeground(new java.awt.Color(96, 0, 0));
        Jlabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Jlabel17.setText("minutes");
        Jlabel17.setToolTipText("");
        Jlabel17.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel13.add(Jlabel17);

        Jlabel15.setFont(new java.awt.Font("SF Pro Display", 1, 12)); // NOI18N
        Jlabel15.setForeground(new java.awt.Color(96, 0, 0));
        Jlabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Jlabel15.setText("Long Break");
        Jlabel15.setToolTipText("");
        Jlabel15.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel13.add(Jlabel15);

        jPanel25.add(jPanel13);

        jPanel47.add(jPanel25, java.awt.BorderLayout.CENTER);

        jPanel24.add(jPanel47);

        jPanel48.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 30, 0));
        jPanel48.setOpaque(false);
        jPanel48.setLayout(new java.awt.BorderLayout(0, 10));

        Jlabel20.setFont(new java.awt.Font("SF Pro Display", 0, 24)); // NOI18N
        Jlabel20.setForeground(new java.awt.Color(96, 0, 0));
        Jlabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Jlabel20.setText("Pet Preferences");
        Jlabel20.setToolTipText("");
        jPanel48.add(Jlabel20, java.awt.BorderLayout.NORTH);

        jPanel42.setBackground(new java.awt.Color(255, 255, 255));
        jPanel42.setLayout(new java.awt.GridLayout(1, 3, 20, 0));

        jPanel43.setBackground(new java.awt.Color(227, 227, 227));
        jPanel43.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel43.setLayout(new java.awt.BorderLayout());

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pomi/resources/pets/pomi/pomi_icon.png"))); // NOI18N
        jLabel12.setToolTipText("");
        jLabel12.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel43.add(jLabel12, java.awt.BorderLayout.CENTER);

        settingsChooseaPet.add(choosepomi1);
        choosepomi1.setForeground(new java.awt.Color(96, 0, 0));
        choosepomi1.setText("Pomi");
        choosepomi1.setToolTipText("");
        choosepomi1.setName("pomi"); // NOI18N
        choosepomi1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                choosepomi1ActionPerformed(evt);
            }
        });
        jPanel43.add(choosepomi1, java.awt.BorderLayout.NORTH);

        jPanel42.add(jPanel43);

        jPanel44.setBackground(new java.awt.Color(227, 227, 227));
        jPanel44.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel44.setLayout(new java.awt.BorderLayout());

        settingsChooseaPet.add(chooseterry1);
        chooseterry1.setForeground(new java.awt.Color(96, 0, 0));
        chooseterry1.setText("Terry");
        chooseterry1.setName("terry"); // NOI18N
        chooseterry1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseterry1ActionPerformed(evt);
            }
        });
        jPanel44.add(chooseterry1, java.awt.BorderLayout.NORTH);

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pomi/resources/pets/terry/terry_icon.png"))); // NOI18N
        jLabel31.setToolTipText("");
        jLabel31.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel44.add(jLabel31, java.awt.BorderLayout.CENTER);

        jPanel42.add(jPanel44);

        jPanel45.setBackground(new java.awt.Color(227, 227, 227));
        jPanel45.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel45.setName(""); // NOI18N
        jPanel45.setLayout(new java.awt.BorderLayout());

        settingsChooseaPet.add(choosepenny1);
        choosepenny1.setForeground(new java.awt.Color(96, 0, 0));
        choosepenny1.setText("Penny");
        choosepenny1.setName("penny"); // NOI18N
        choosepenny1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                choosepenny1ActionPerformed(evt);
            }
        });
        jPanel45.add(choosepenny1, java.awt.BorderLayout.NORTH);

        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pomi/resources/pets/penny/penny_icon.png"))); // NOI18N
        jLabel32.setToolTipText("");
        jLabel32.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel45.add(jLabel32, java.awt.BorderLayout.CENTER);
        jPanel45.add(canvas3, java.awt.BorderLayout.PAGE_END);

        jPanel42.add(jPanel45);

        jPanel46.setBackground(new java.awt.Color(227, 227, 227));
        jPanel46.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel46.setName(""); // NOI18N
        jPanel46.setLayout(new java.awt.BorderLayout());

        settingsChooseaPet.add(choosefelix1);
        choosefelix1.setForeground(new java.awt.Color(96, 0, 0));
        choosefelix1.setText("Felix");
        choosefelix1.setName("felix"); // NOI18N
        choosefelix1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                choosefelix1ActionPerformed(evt);
            }
        });
        jPanel46.add(choosefelix1, java.awt.BorderLayout.NORTH);

        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pomi/resources/pets/felix/felix_icon.png"))); // NOI18N
        jLabel33.setToolTipText("");
        jLabel33.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel46.add(jLabel33, java.awt.BorderLayout.CENTER);

        jPanel42.add(jPanel46);

        jPanel48.add(jPanel42, java.awt.BorderLayout.CENTER);

        jPanel24.add(jPanel48);

        jPanel4.add(jPanel24, java.awt.BorderLayout.CENTER);

        jPanel41.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel41.setOpaque(false);
        jPanel41.setLayout(new java.awt.BorderLayout());

        accountResetButton.setFont(new java.awt.Font("SF Pro Display", 0, 14)); // NOI18N
        accountResetButton.setForeground(new java.awt.Color(96, 0, 0));
        accountResetButton.setText("Reset");
        accountResetButton.setContentAreaFilled(false);
        accountResetButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        accountResetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                accountResetButtonActionPerformed(evt);
            }
        });
        jPanel41.add(accountResetButton, java.awt.BorderLayout.WEST);

        jPanel4.add(jPanel41, java.awt.BorderLayout.SOUTH);

        settingsDialog.getContentPane().add(jPanel4, java.awt.BorderLayout.CENTER);

        access.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        access.setTitle("PomoPets Access");
        access.setResizable(false);
        access.setSize(new java.awt.Dimension(554, 458));
        access.getContentPane().setLayout(new javax.swing.BoxLayout(access.getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        signinOrsignup.setBackground(new java.awt.Color(204, 204, 204));
        signinOrsignup.setForeground(new java.awt.Color(96, 0, 0));
        signinOrsignup.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        signinOrsignup.setName(""); // NOI18N
        signinOrsignup.setOpaque(true);
        signinOrsignup.setPreferredSize(new java.awt.Dimension(554, 458));

        signin_panel.setLayout(new java.awt.BorderLayout());

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 1, 1, 1));
        jPanel6.setLayout(new java.awt.GridLayout(2, 1));

        jLabel16.setFont(new java.awt.Font("Poppins Medium", 0, 36)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(96, 0, 0));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pomi/resources/loadingscreen/icon.png"))); // NOI18N
        jPanel6.add(jLabel16);

        jLabel26.setFont(new java.awt.Font("Poppins Medium", 0, 36)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(96, 0, 0));
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setText("Sign in to Account");
        jPanel6.add(jLabel26);

        signin_panel.add(jPanel6, java.awt.BorderLayout.NORTH);

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 100, 30, 100));
        jPanel7.setMinimumSize(new java.awt.Dimension(136, 10));
        jPanel7.setLayout(new java.awt.GridLayout(0, 1));

        jLabel21.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(96, 0, 0));
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel21.setText("Username");
        jLabel21.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jPanel7.add(jLabel21);

        signin_username_field.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        signin_username_field.setToolTipText("");
        jPanel7.add(signin_username_field);

        jLabel22.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(96, 0, 0));
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel22.setText("Password");
        jLabel22.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jLabel22.setFocusable(false);
        jPanel7.add(jLabel22);

        signin_password_field.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        signin_password_field.setToolTipText("");
        jPanel7.add(signin_password_field);

        signin_panel.add(jPanel7, java.awt.BorderLayout.CENTER);

        jPanel14.setBackground(new java.awt.Color(255, 255, 255));
        jPanel14.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 200, 30, 200));
        jPanel14.setLayout(new java.awt.GridLayout(1, 0));

        signin_button.setBackground(new java.awt.Color(96, 0, 0));
        signin_button.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        signin_button.setForeground(new java.awt.Color(255, 255, 255));
        signin_button.setText("Sign in");
        signin_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                signin_buttonActionPerformed(evt);
            }
        });
        jPanel14.add(signin_button);

        signin_panel.add(jPanel14, java.awt.BorderLayout.SOUTH);
        signin_panel.add(canvas1, java.awt.BorderLayout.EAST);

        signinOrsignup.addTab("Sign In", signin_panel);

        signup_panel.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        signup_panel.setLayout(new java.awt.BorderLayout());

        jPanel15.setBackground(new java.awt.Color(255, 255, 255));
        jPanel15.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 1, 1, 1));
        jPanel15.setLayout(new java.awt.GridLayout(2, 1));

        jLabel23.setFont(new java.awt.Font("Poppins Medium", 0, 36)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(96, 0, 0));
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pomi/resources/loadingscreen/icon.png"))); // NOI18N
        jLabel23.setToolTipText("");
        jPanel15.add(jLabel23);

        jLabel27.setFont(new java.awt.Font("Poppins Medium", 0, 36)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(96, 0, 0));
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setText("Create Account");
        jLabel27.setToolTipText("");
        jPanel15.add(jLabel27);

        signup_panel.add(jPanel15, java.awt.BorderLayout.NORTH);

        jPanel16.setBackground(new java.awt.Color(255, 255, 255));
        jPanel16.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 100, 30, 100));
        jPanel16.setMinimumSize(new java.awt.Dimension(136, 10));
        jPanel16.setLayout(new java.awt.GridLayout(0, 1));

        jLabel24.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(96, 0, 0));
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel24.setText("Username");
        jLabel24.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jPanel16.add(jLabel24);

        signup_username_field.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        signup_username_field.setToolTipText("");
        jPanel16.add(signup_username_field);

        jLabel25.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(96, 0, 0));
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel25.setText("Password");
        jLabel25.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jLabel25.setFocusable(false);
        jPanel16.add(jLabel25);

        signup_password_field.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        signup_password_field.setToolTipText("");
        jPanel16.add(signup_password_field);

        signup_panel.add(jPanel16, java.awt.BorderLayout.CENTER);

        jPanel17.setBackground(new java.awt.Color(255, 255, 255));
        jPanel17.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 200, 30, 200));
        jPanel17.setLayout(new java.awt.GridLayout(1, 0));

        signup_button.setBackground(new java.awt.Color(96, 0, 0));
        signup_button.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        signup_button.setForeground(new java.awt.Color(255, 255, 255));
        signup_button.setText("Sign Up");
        signup_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                signup_buttonActionPerformed(evt);
            }
        });
        jPanel17.add(signup_button);

        signup_panel.add(jPanel17, java.awt.BorderLayout.SOUTH);
        signup_panel.add(canvas2, java.awt.BorderLayout.EAST);

        signinOrsignup.addTab("Sign Up", signup_panel);

        access.getContentPane().add(signinOrsignup);

        welcome.setUndecorated(true);
        welcome.setResizable(false);
        welcome.setSize(new java.awt.Dimension(570, 370));

        jPanel27.setBackground(new java.awt.Color(255, 255, 255));
        jPanel27.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 0, 20));
        jPanel27.setLayout(new java.awt.BorderLayout());

        jPanel28.setBackground(new java.awt.Color(255, 255, 255));
        jPanel28.setLayout(new java.awt.GridLayout(2, 1));

        namewelcome1.setFont(new java.awt.Font("SF Pro Display", 0, 43)); // NOI18N
        namewelcome1.setForeground(new java.awt.Color(96, 0, 0));
        namewelcome1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        namewelcome1.setText("Welcome <User>!");
        namewelcome1.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jPanel28.add(namewelcome1);

        namewelcome3.setFont(new java.awt.Font("SF Pro Display", 0, 14)); // NOI18N
        namewelcome3.setForeground(new java.awt.Color(96, 0, 0));
        namewelcome3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        namewelcome3.setText("To get started, choose what duration you would like for your POMODORO?");
        namewelcome3.setToolTipText("");
        namewelcome3.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel28.add(namewelcome3);

        jPanel27.add(jPanel28, java.awt.BorderLayout.NORTH);

        jPanel29.setBackground(new java.awt.Color(255, 255, 255));
        jPanel29.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 50, 10, 50));
        jPanel29.setLayout(new java.awt.GridLayout(1, 3, 20, 0));

        jPanel35.setBackground(new java.awt.Color(227, 227, 227));
        jPanel35.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel35.setLayout(new java.awt.GridLayout(3, 1));

        Jlabel1.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        Jlabel1.setForeground(new java.awt.Color(96, 0, 0));
        Jlabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Jlabel1.setText("POMODORO");
        Jlabel1.setToolTipText("");
        Jlabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel35.add(Jlabel1);

        jPanel36.setBackground(new java.awt.Color(227, 227, 227));
        jPanel36.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(96, 0, 0)));
        jPanel36.setLayout(new java.awt.GridLayout(1, 0));

        welcomepomodoro.setBackground(new java.awt.Color(227, 227, 227));
        welcomepomodoro.setFont(new java.awt.Font("Poppins", 0, 24)); // NOI18N
        welcomepomodoro.setForeground(new java.awt.Color(96, 0, 0));
        welcomepomodoro.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        welcomepomodoro.setText("25");
        welcomepomodoro.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel36.add(welcomepomodoro);

        jPanel35.add(jPanel36);

        Jlabel25.setFont(new java.awt.Font("SF Pro Display", 0, 14)); // NOI18N
        Jlabel25.setForeground(new java.awt.Color(96, 0, 0));
        Jlabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Jlabel25.setText("minutes");
        Jlabel25.setToolTipText("");
        Jlabel25.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel35.add(Jlabel25);

        jPanel29.add(jPanel35);

        jPanel33.setBackground(new java.awt.Color(227, 227, 227));
        jPanel33.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel33.setLayout(new java.awt.GridLayout(3, 1));

        Jlabel4.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        Jlabel4.setForeground(new java.awt.Color(96, 0, 0));
        Jlabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Jlabel4.setText("Break");
        Jlabel4.setToolTipText("");
        Jlabel4.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel33.add(Jlabel4);

        jPanel34.setBackground(new java.awt.Color(227, 227, 227));
        jPanel34.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(96, 0, 0)));
        jPanel34.setLayout(new java.awt.GridLayout(1, 1));

        welcomebreakpomodoro.setBackground(new java.awt.Color(227, 227, 227));
        welcomebreakpomodoro.setFont(new java.awt.Font("Poppins", 0, 24)); // NOI18N
        welcomebreakpomodoro.setForeground(new java.awt.Color(96, 0, 0));
        welcomebreakpomodoro.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        welcomebreakpomodoro.setText("25");
        welcomebreakpomodoro.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel34.add(welcomebreakpomodoro);

        jPanel33.add(jPanel34);

        Jlabel24.setFont(new java.awt.Font("SF Pro Display", 0, 14)); // NOI18N
        Jlabel24.setForeground(new java.awt.Color(96, 0, 0));
        Jlabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Jlabel24.setText("minutes");
        Jlabel24.setToolTipText("");
        Jlabel24.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel33.add(Jlabel24);

        jPanel29.add(jPanel33);

        jPanel31.setBackground(new java.awt.Color(227, 227, 227));
        jPanel31.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel31.setLayout(new java.awt.GridLayout(3, 1));

        Jlabel7.setFont(new java.awt.Font("Poppins Medium", 0, 14)); // NOI18N
        Jlabel7.setForeground(new java.awt.Color(96, 0, 0));
        Jlabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Jlabel7.setText("Long Break");
        Jlabel7.setToolTipText("");
        Jlabel7.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel31.add(Jlabel7);

        jPanel32.setBackground(new java.awt.Color(227, 227, 227));
        jPanel32.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 2, 0, new java.awt.Color(96, 0, 0)));
        jPanel32.setLayout(new java.awt.GridLayout(1, 0));

        welcomelongbreakpomodoro.setBackground(new java.awt.Color(227, 227, 227));
        welcomelongbreakpomodoro.setFont(new java.awt.Font("Poppins", 0, 24)); // NOI18N
        welcomelongbreakpomodoro.setForeground(new java.awt.Color(96, 0, 0));
        welcomelongbreakpomodoro.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        welcomelongbreakpomodoro.setText("25");
        welcomelongbreakpomodoro.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel32.add(welcomelongbreakpomodoro);

        jPanel31.add(jPanel32);

        Jlabel21.setFont(new java.awt.Font("SF Pro Display", 0, 14)); // NOI18N
        Jlabel21.setForeground(new java.awt.Color(96, 0, 0));
        Jlabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Jlabel21.setText("minutes");
        Jlabel21.setToolTipText("");
        Jlabel21.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel31.add(Jlabel21);

        jPanel29.add(jPanel31);

        jPanel27.add(jPanel29, java.awt.BorderLayout.CENTER);

        jPanel37.setBackground(new java.awt.Color(255, 255, 255));
        jPanel37.setBorder(javax.swing.BorderFactory.createEmptyBorder(40, 220, 40, 220));
        jPanel37.setLayout(new java.awt.GridLayout(1, 0));

        okwelcome.setBackground(new java.awt.Color(76, 90, 35));
        okwelcome.setFont(new java.awt.Font("SF Pro Display", 0, 18)); // NOI18N
        okwelcome.setForeground(new java.awt.Color(255, 255, 255));
        okwelcome.setText("Ok!");
        okwelcome.setBorderPainted(false);
        okwelcome.setFocusPainted(false);
        okwelcome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okwelcomeActionPerformed(evt);
            }
        });
        jPanel37.add(okwelcome);

        jPanel27.add(jPanel37, java.awt.BorderLayout.SOUTH);

        welcome.getContentPane().add(jPanel27, java.awt.BorderLayout.CENTER);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("PomoPets");
        setUndecorated(true);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        content_panel.setBackground(new java.awt.Color(96, 0, 0));
        content_panel.setMinimumSize(new java.awt.Dimension(1280, 1020));
        content_panel.setPreferredSize(new java.awt.Dimension(1280, 1020));
        content_panel.setLayout(new java.awt.BorderLayout());

        appspanel.setBackground(new java.awt.Color(96, 0, 0));
        appspanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 40, 10, 40));
        appspanel.setOpaque(false);
        appspanel.setLayout(new java.awt.GridLayout(11, 2, 10, 30));

        jLabel20.setFont(new java.awt.Font("Poppins", 0, 10)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel20.setText("Work Apps");
        jLabel20.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        appspanel.add(jLabel20);

        jLabel18.setFont(new java.awt.Font("Poppins", 0, 10)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("Fun Apps");
        jLabel18.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        appspanel.add(jLabel18);

        work_app1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        work_removeapp1.setBackground(new java.awt.Color(0, 0, 0));
        work_removeapp1.setFont(new java.awt.Font("SF Pro Display", 0, 24)); // NOI18N
        work_removeapp1.setForeground(new java.awt.Color(255, 255, 255));
        work_removeapp1.setText("-");
        work_removeapp1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        work_removeapp1.setBorderPainted(false);
        work_removeapp1.setFocusPainted(false);
        work_removeapp1.setFocusable(false);
        work_removeapp1.setName("work_removeapp1"); // NOI18N
        work_removeapp1.setRequestFocusEnabled(false);
        work_removeapp1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                work_removeapp1ActionPerformed(evt);
            }
        });
        work_app1.add(work_removeapp1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 20, 20));

        work_appbutton1.setBackground(new java.awt.Color(214, 46, 46));
        work_appbutton1.setFont(new java.awt.Font("Segoe MDL2 Assets", 0, 24)); // NOI18N
        work_appbutton1.setForeground(new java.awt.Color(255, 255, 255));
        work_appbutton1.setText("");
        work_appbutton1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        work_appbutton1.setBorderPainted(false);
        work_appbutton1.setFocusPainted(false);
        work_appbutton1.setFocusable(false);
        work_appbutton1.setName("work_appbutton1"); // NOI18N
        work_appbutton1.setRequestFocusEnabled(false);
        work_appbutton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                work_appbutton1ActionPerformed(evt);
            }
        });
        work_app1.add(work_appbutton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 70, 60));

        appspanel.add(work_app1);

        fun_app1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        fun_removeapp1.setBackground(new java.awt.Color(0, 0, 0));
        fun_removeapp1.setFont(new java.awt.Font("SF Pro Display", 1, 24)); // NOI18N
        fun_removeapp1.setForeground(new java.awt.Color(255, 255, 255));
        fun_removeapp1.setText("-");
        fun_removeapp1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        fun_removeapp1.setBorderPainted(false);
        fun_removeapp1.setFocusable(false);
        fun_removeapp1.setName("fun_removeapp1"); // NOI18N
        fun_removeapp1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fun_removeapp1ActionPerformed(evt);
            }
        });
        fun_app1.add(fun_removeapp1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 20, 20));

        fun_appbutton1.setBackground(new java.awt.Color(76, 90, 35));
        fun_appbutton1.setFont(new java.awt.Font("Segoe MDL2 Assets", 0, 24)); // NOI18N
        fun_appbutton1.setForeground(new java.awt.Color(255, 255, 255));
        fun_appbutton1.setText("");
        fun_appbutton1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        fun_appbutton1.setBorderPainted(false);
        fun_appbutton1.setFocusable(false);
        fun_appbutton1.setName("fun_appbutton1"); // NOI18N
        fun_appbutton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fun_appbutton1ActionPerformed(evt);
            }
        });
        fun_app1.add(fun_appbutton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 70, 60));

        appspanel.add(fun_app1);

        work_app2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        work_removeapp2.setBackground(new java.awt.Color(0, 0, 0));
        work_removeapp2.setFont(new java.awt.Font("SF Pro Display", 1, 24)); // NOI18N
        work_removeapp2.setForeground(new java.awt.Color(255, 255, 255));
        work_removeapp2.setText("-");
        work_removeapp2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        work_removeapp2.setBorderPainted(false);
        work_removeapp2.setFocusPainted(false);
        work_removeapp2.setFocusable(false);
        work_removeapp2.setName("work_removeapp2"); // NOI18N
        work_removeapp2.setRequestFocusEnabled(false);
        work_removeapp2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                work_removeapp2ActionPerformed(evt);
            }
        });
        work_app2.add(work_removeapp2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 20, 20));

        work_appbutton2.setBackground(new java.awt.Color(214, 46, 46));
        work_appbutton2.setFont(new java.awt.Font("Segoe MDL2 Assets", 0, 24)); // NOI18N
        work_appbutton2.setForeground(new java.awt.Color(255, 255, 255));
        work_appbutton2.setText("");
        work_appbutton2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        work_appbutton2.setBorderPainted(false);
        work_appbutton2.setFocusPainted(false);
        work_appbutton2.setFocusable(false);
        work_appbutton2.setName("work_appbutton2"); // NOI18N
        work_appbutton2.setRequestFocusEnabled(false);
        work_appbutton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                work_appbutton2ActionPerformed(evt);
            }
        });
        work_app2.add(work_appbutton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 70, 60));

        appspanel.add(work_app2);

        fun_app2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        fun_removeapp2.setBackground(new java.awt.Color(0, 0, 0));
        fun_removeapp2.setFont(new java.awt.Font("SF Pro Display", 1, 24)); // NOI18N
        fun_removeapp2.setForeground(new java.awt.Color(255, 255, 255));
        fun_removeapp2.setText("-");
        fun_removeapp2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        fun_removeapp2.setBorderPainted(false);
        fun_removeapp2.setFocusPainted(false);
        fun_removeapp2.setFocusable(false);
        fun_removeapp2.setName("fun_removeapp2"); // NOI18N
        fun_removeapp2.setRequestFocusEnabled(false);
        fun_removeapp2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fun_removeapp2ActionPerformed(evt);
            }
        });
        fun_app2.add(fun_removeapp2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 20, 20));

        fun_appbutton2.setBackground(new java.awt.Color(76, 90, 35));
        fun_appbutton2.setFont(new java.awt.Font("Segoe MDL2 Assets", 0, 24)); // NOI18N
        fun_appbutton2.setForeground(new java.awt.Color(255, 255, 255));
        fun_appbutton2.setText("");
        fun_appbutton2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        fun_appbutton2.setBorderPainted(false);
        fun_appbutton2.setFocusPainted(false);
        fun_appbutton2.setFocusable(false);
        fun_appbutton2.setName("fun_appbutton2"); // NOI18N
        fun_appbutton2.setRequestFocusEnabled(false);
        fun_appbutton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fun_appbutton2ActionPerformed(evt);
            }
        });
        fun_app2.add(fun_appbutton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 70, 60));

        appspanel.add(fun_app2);

        work_app3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        work_removeapp3.setBackground(new java.awt.Color(0, 0, 0));
        work_removeapp3.setFont(new java.awt.Font("SF Pro Display", 1, 24)); // NOI18N
        work_removeapp3.setForeground(new java.awt.Color(255, 255, 255));
        work_removeapp3.setText("-");
        work_removeapp3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        work_removeapp3.setBorderPainted(false);
        work_removeapp3.setFocusPainted(false);
        work_removeapp3.setFocusable(false);
        work_removeapp3.setName("work_removeapp3"); // NOI18N
        work_removeapp3.setRequestFocusEnabled(false);
        work_removeapp3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                work_removeapp3ActionPerformed(evt);
            }
        });
        work_app3.add(work_removeapp3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 20, 20));

        work_appbutton3.setBackground(new java.awt.Color(214, 46, 46));
        work_appbutton3.setFont(new java.awt.Font("Segoe MDL2 Assets", 0, 24)); // NOI18N
        work_appbutton3.setForeground(new java.awt.Color(255, 255, 255));
        work_appbutton3.setText("");
        work_appbutton3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        work_appbutton3.setBorderPainted(false);
        work_appbutton3.setFocusPainted(false);
        work_appbutton3.setFocusable(false);
        work_appbutton3.setName("work_appbutton3"); // NOI18N
        work_appbutton3.setRequestFocusEnabled(false);
        work_appbutton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                work_appbutton3ActionPerformed(evt);
            }
        });
        work_app3.add(work_appbutton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 70, 60));

        appspanel.add(work_app3);

        fun_app3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        fun_removeapp3.setBackground(new java.awt.Color(0, 0, 0));
        fun_removeapp3.setFont(new java.awt.Font("SF Pro Display", 1, 24)); // NOI18N
        fun_removeapp3.setForeground(new java.awt.Color(255, 255, 255));
        fun_removeapp3.setText("-");
        fun_removeapp3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        fun_removeapp3.setBorderPainted(false);
        fun_removeapp3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        fun_removeapp3.setFocusPainted(false);
        fun_removeapp3.setFocusable(false);
        fun_removeapp3.setName("fun_removeapp3"); // NOI18N
        fun_removeapp3.setRequestFocusEnabled(false);
        fun_removeapp3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fun_removeapp3ActionPerformed(evt);
            }
        });
        fun_app3.add(fun_removeapp3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 20, 20));

        fun_appbutton3.setBackground(new java.awt.Color(76, 90, 35));
        fun_appbutton3.setFont(new java.awt.Font("Segoe MDL2 Assets", 0, 24)); // NOI18N
        fun_appbutton3.setForeground(new java.awt.Color(255, 255, 255));
        fun_appbutton3.setText("");
        fun_appbutton3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        fun_appbutton3.setBorderPainted(false);
        fun_appbutton3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        fun_appbutton3.setFocusPainted(false);
        fun_appbutton3.setFocusable(false);
        fun_appbutton3.setName("fun_appbutton3"); // NOI18N
        fun_appbutton3.setRequestFocusEnabled(false);
        fun_appbutton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fun_appbutton3ActionPerformed(evt);
            }
        });
        fun_app3.add(fun_appbutton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 70, 60));

        appspanel.add(fun_app3);

        work_app4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        work_removeapp4.setBackground(new java.awt.Color(0, 0, 0));
        work_removeapp4.setFont(new java.awt.Font("SF Pro Display", 1, 24)); // NOI18N
        work_removeapp4.setForeground(new java.awt.Color(255, 255, 255));
        work_removeapp4.setText("-");
        work_removeapp4.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        work_removeapp4.setBorderPainted(false);
        work_removeapp4.setFocusPainted(false);
        work_removeapp4.setFocusable(false);
        work_removeapp4.setName("work_removeapp4"); // NOI18N
        work_removeapp4.setRequestFocusEnabled(false);
        work_removeapp4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                work_removeapp4ActionPerformed(evt);
            }
        });
        work_app4.add(work_removeapp4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 20, 20));

        work_appbutton4.setBackground(new java.awt.Color(214, 46, 46));
        work_appbutton4.setFont(new java.awt.Font("Segoe MDL2 Assets", 0, 24)); // NOI18N
        work_appbutton4.setForeground(new java.awt.Color(255, 255, 255));
        work_appbutton4.setText("");
        work_appbutton4.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        work_appbutton4.setBorderPainted(false);
        work_appbutton4.setFocusPainted(false);
        work_appbutton4.setFocusable(false);
        work_appbutton4.setName("work_appbutton4"); // NOI18N
        work_appbutton4.setRequestFocusEnabled(false);
        work_appbutton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                work_appbutton4ActionPerformed(evt);
            }
        });
        work_app4.add(work_appbutton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 70, 60));

        appspanel.add(work_app4);

        fun_app4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        fun_removeapp4.setBackground(new java.awt.Color(0, 0, 0));
        fun_removeapp4.setFont(new java.awt.Font("SF Pro Display", 1, 24)); // NOI18N
        fun_removeapp4.setForeground(new java.awt.Color(255, 255, 255));
        fun_removeapp4.setText("-");
        fun_removeapp4.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        fun_removeapp4.setBorderPainted(false);
        fun_removeapp4.setFocusPainted(false);
        fun_removeapp4.setFocusable(false);
        fun_removeapp4.setName("fun_removeapp4"); // NOI18N
        fun_removeapp4.setRequestFocusEnabled(false);
        fun_removeapp4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fun_removeapp4ActionPerformed(evt);
            }
        });
        fun_app4.add(fun_removeapp4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 20, 20));

        fun_appbutton4.setBackground(new java.awt.Color(76, 90, 35));
        fun_appbutton4.setFont(new java.awt.Font("Segoe MDL2 Assets", 0, 24)); // NOI18N
        fun_appbutton4.setForeground(new java.awt.Color(255, 255, 255));
        fun_appbutton4.setText("");
        fun_appbutton4.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        fun_appbutton4.setBorderPainted(false);
        fun_appbutton4.setFocusPainted(false);
        fun_appbutton4.setFocusable(false);
        fun_appbutton4.setName("fun_appbutton4"); // NOI18N
        fun_appbutton4.setRequestFocusEnabled(false);
        fun_appbutton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fun_appbutton4ActionPerformed(evt);
            }
        });
        fun_app4.add(fun_appbutton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 70, 60));

        appspanel.add(fun_app4);

        work_app5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        work_removeapp5.setBackground(new java.awt.Color(0, 0, 0));
        work_removeapp5.setFont(new java.awt.Font("SF Pro Display", 1, 24)); // NOI18N
        work_removeapp5.setForeground(new java.awt.Color(255, 255, 255));
        work_removeapp5.setText("-");
        work_removeapp5.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        work_removeapp5.setBorderPainted(false);
        work_removeapp5.setFocusPainted(false);
        work_removeapp5.setFocusable(false);
        work_removeapp5.setName("work_removeapp5"); // NOI18N
        work_removeapp5.setRequestFocusEnabled(false);
        work_removeapp5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                work_removeapp5ActionPerformed(evt);
            }
        });
        work_app5.add(work_removeapp5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 20, 20));

        work_appbutton5.setBackground(new java.awt.Color(214, 46, 46));
        work_appbutton5.setFont(new java.awt.Font("Segoe MDL2 Assets", 0, 24)); // NOI18N
        work_appbutton5.setForeground(new java.awt.Color(255, 255, 255));
        work_appbutton5.setText("");
        work_appbutton5.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        work_appbutton5.setBorderPainted(false);
        work_appbutton5.setFocusPainted(false);
        work_appbutton5.setFocusable(false);
        work_appbutton5.setName("work_appbutton5"); // NOI18N
        work_appbutton5.setRequestFocusEnabled(false);
        work_appbutton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                work_appbutton5ActionPerformed(evt);
            }
        });
        work_app5.add(work_appbutton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 70, 60));

        appspanel.add(work_app5);

        fun_app5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        fun_removeapp5.setBackground(new java.awt.Color(0, 0, 0));
        fun_removeapp5.setFont(new java.awt.Font("SF Pro Display", 1, 24)); // NOI18N
        fun_removeapp5.setForeground(new java.awt.Color(255, 255, 255));
        fun_removeapp5.setText("-");
        fun_removeapp5.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        fun_removeapp5.setBorderPainted(false);
        fun_removeapp5.setFocusPainted(false);
        fun_removeapp5.setFocusable(false);
        fun_removeapp5.setName("fun_removeapp5"); // NOI18N
        fun_removeapp5.setRequestFocusEnabled(false);
        fun_removeapp5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fun_removeapp5ActionPerformed(evt);
            }
        });
        fun_app5.add(fun_removeapp5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 20, 20));

        fun_appbutton5.setBackground(new java.awt.Color(76, 90, 35));
        fun_appbutton5.setFont(new java.awt.Font("Segoe MDL2 Assets", 0, 24)); // NOI18N
        fun_appbutton5.setForeground(new java.awt.Color(255, 255, 255));
        fun_appbutton5.setText("");
        fun_appbutton5.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        fun_appbutton5.setBorderPainted(false);
        fun_appbutton5.setFocusPainted(false);
        fun_appbutton5.setFocusable(false);
        fun_appbutton5.setName("fun_appbutton5"); // NOI18N
        fun_appbutton5.setRequestFocusEnabled(false);
        fun_appbutton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fun_appbutton5ActionPerformed(evt);
            }
        });
        fun_app5.add(fun_appbutton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 70, 60));

        appspanel.add(fun_app5);

        work_app6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        work_removeapp6.setBackground(new java.awt.Color(0, 0, 0));
        work_removeapp6.setFont(new java.awt.Font("SF Pro Display", 1, 24)); // NOI18N
        work_removeapp6.setForeground(new java.awt.Color(255, 255, 255));
        work_removeapp6.setText("-");
        work_removeapp6.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        work_removeapp6.setBorderPainted(false);
        work_removeapp6.setFocusPainted(false);
        work_removeapp6.setFocusable(false);
        work_removeapp6.setName("work_removeapp6"); // NOI18N
        work_removeapp6.setRequestFocusEnabled(false);
        work_removeapp6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                work_removeapp6ActionPerformed(evt);
            }
        });
        work_app6.add(work_removeapp6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 20, 20));

        work_appbutton6.setBackground(new java.awt.Color(214, 46, 46));
        work_appbutton6.setFont(new java.awt.Font("Segoe MDL2 Assets", 0, 24)); // NOI18N
        work_appbutton6.setForeground(new java.awt.Color(255, 255, 255));
        work_appbutton6.setText("");
        work_appbutton6.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        work_appbutton6.setBorderPainted(false);
        work_appbutton6.setFocusPainted(false);
        work_appbutton6.setFocusable(false);
        work_appbutton6.setName("work_appbutton6"); // NOI18N
        work_appbutton6.setRequestFocusEnabled(false);
        work_appbutton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                work_appbutton6ActionPerformed(evt);
            }
        });
        work_app6.add(work_appbutton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 70, 60));

        appspanel.add(work_app6);

        fun_app6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        fun_removeapp6.setBackground(new java.awt.Color(0, 0, 0));
        fun_removeapp6.setFont(new java.awt.Font("SF Pro Display", 1, 24)); // NOI18N
        fun_removeapp6.setForeground(new java.awt.Color(255, 255, 255));
        fun_removeapp6.setText("-");
        fun_removeapp6.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        fun_removeapp6.setBorderPainted(false);
        fun_removeapp6.setFocusPainted(false);
        fun_removeapp6.setFocusable(false);
        fun_removeapp6.setName("fun_removeapp6"); // NOI18N
        fun_removeapp6.setRequestFocusEnabled(false);
        fun_removeapp6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fun_removeapp6ActionPerformed(evt);
            }
        });
        fun_app6.add(fun_removeapp6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 20, 20));

        fun_appbutton6.setBackground(new java.awt.Color(76, 90, 35));
        fun_appbutton6.setFont(new java.awt.Font("Segoe MDL2 Assets", 0, 24)); // NOI18N
        fun_appbutton6.setForeground(new java.awt.Color(255, 255, 255));
        fun_appbutton6.setText("");
        fun_appbutton6.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        fun_appbutton6.setBorderPainted(false);
        fun_appbutton6.setFocusPainted(false);
        fun_appbutton6.setFocusable(false);
        fun_appbutton6.setName("fun_appbutton6"); // NOI18N
        fun_appbutton6.setRequestFocusEnabled(false);
        fun_appbutton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fun_appbutton6ActionPerformed(evt);
            }
        });
        fun_app6.add(fun_appbutton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 70, 60));

        appspanel.add(fun_app6);

        work_app7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        work_removeapp7.setBackground(new java.awt.Color(0, 0, 0));
        work_removeapp7.setFont(new java.awt.Font("SF Pro Display", 1, 24)); // NOI18N
        work_removeapp7.setForeground(new java.awt.Color(255, 255, 255));
        work_removeapp7.setText("-");
        work_removeapp7.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        work_removeapp7.setBorderPainted(false);
        work_removeapp7.setFocusPainted(false);
        work_removeapp7.setFocusable(false);
        work_removeapp7.setName("work_removeapp7"); // NOI18N
        work_removeapp7.setRequestFocusEnabled(false);
        work_removeapp7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                work_removeapp7ActionPerformed(evt);
            }
        });
        work_app7.add(work_removeapp7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 20, 20));

        work_appbutton7.setBackground(new java.awt.Color(214, 46, 46));
        work_appbutton7.setFont(new java.awt.Font("Segoe MDL2 Assets", 0, 24)); // NOI18N
        work_appbutton7.setForeground(new java.awt.Color(255, 255, 255));
        work_appbutton7.setText("");
        work_appbutton7.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        work_appbutton7.setBorderPainted(false);
        work_appbutton7.setFocusPainted(false);
        work_appbutton7.setFocusable(false);
        work_appbutton7.setName("work_appbutton7"); // NOI18N
        work_appbutton7.setRequestFocusEnabled(false);
        work_appbutton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                work_appbutton7ActionPerformed(evt);
            }
        });
        work_app7.add(work_appbutton7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 70, 60));

        appspanel.add(work_app7);

        fun_app7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        fun_removeapp7.setBackground(new java.awt.Color(0, 0, 0));
        fun_removeapp7.setFont(new java.awt.Font("SF Pro Display", 1, 24)); // NOI18N
        fun_removeapp7.setForeground(new java.awt.Color(255, 255, 255));
        fun_removeapp7.setText("-");
        fun_removeapp7.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        fun_removeapp7.setBorderPainted(false);
        fun_removeapp7.setFocusPainted(false);
        fun_removeapp7.setFocusable(false);
        fun_removeapp7.setName("fun_removeapp7"); // NOI18N
        fun_removeapp7.setRequestFocusEnabled(false);
        fun_removeapp7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fun_removeapp7ActionPerformed(evt);
            }
        });
        fun_app7.add(fun_removeapp7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 20, 20));

        fun_appbutton7.setBackground(new java.awt.Color(76, 90, 35));
        fun_appbutton7.setFont(new java.awt.Font("Segoe MDL2 Assets", 0, 24)); // NOI18N
        fun_appbutton7.setForeground(new java.awt.Color(255, 255, 255));
        fun_appbutton7.setText("");
        fun_appbutton7.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        fun_appbutton7.setBorderPainted(false);
        fun_appbutton7.setFocusPainted(false);
        fun_appbutton7.setFocusable(false);
        fun_appbutton7.setName("fun_appbutton7"); // NOI18N
        fun_appbutton7.setRequestFocusEnabled(false);
        fun_appbutton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fun_appbutton7ActionPerformed(evt);
            }
        });
        fun_app7.add(fun_appbutton7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 70, 60));

        appspanel.add(fun_app7);

        work_app8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        work_removeapp8.setBackground(new java.awt.Color(0, 0, 0));
        work_removeapp8.setFont(new java.awt.Font("SF Pro Display", 1, 24)); // NOI18N
        work_removeapp8.setForeground(new java.awt.Color(255, 255, 255));
        work_removeapp8.setText("-");
        work_removeapp8.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        work_removeapp8.setBorderPainted(false);
        work_removeapp8.setFocusPainted(false);
        work_removeapp8.setFocusable(false);
        work_removeapp8.setName("work_removeapp8"); // NOI18N
        work_removeapp8.setRequestFocusEnabled(false);
        work_removeapp8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                work_removeapp8ActionPerformed(evt);
            }
        });
        work_app8.add(work_removeapp8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 20, 20));

        work_appbutton8.setBackground(new java.awt.Color(214, 46, 46));
        work_appbutton8.setFont(new java.awt.Font("Segoe MDL2 Assets", 0, 24)); // NOI18N
        work_appbutton8.setForeground(new java.awt.Color(255, 255, 255));
        work_appbutton8.setText("");
        work_appbutton8.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        work_appbutton8.setBorderPainted(false);
        work_appbutton8.setFocusPainted(false);
        work_appbutton8.setFocusable(false);
        work_appbutton8.setName("work_appbutton8"); // NOI18N
        work_appbutton8.setRequestFocusEnabled(false);
        work_appbutton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                work_appbutton8ActionPerformed(evt);
            }
        });
        work_app8.add(work_appbutton8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 70, 60));

        appspanel.add(work_app8);

        fun_app8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        fun_removeapp8.setBackground(new java.awt.Color(0, 0, 0));
        fun_removeapp8.setFont(new java.awt.Font("SF Pro Display", 1, 24)); // NOI18N
        fun_removeapp8.setForeground(new java.awt.Color(255, 255, 255));
        fun_removeapp8.setText("-");
        fun_removeapp8.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        fun_removeapp8.setBorderPainted(false);
        fun_removeapp8.setFocusPainted(false);
        fun_removeapp8.setFocusable(false);
        fun_removeapp8.setName("fun_removeapp8"); // NOI18N
        fun_removeapp8.setRequestFocusEnabled(false);
        fun_removeapp8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fun_removeapp8ActionPerformed(evt);
            }
        });
        fun_app8.add(fun_removeapp8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 20, 20));

        fun_appbutton8.setBackground(new java.awt.Color(76, 90, 35));
        fun_appbutton8.setFont(new java.awt.Font("Segoe MDL2 Assets", 0, 24)); // NOI18N
        fun_appbutton8.setForeground(new java.awt.Color(255, 255, 255));
        fun_appbutton8.setText("");
        fun_appbutton8.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        fun_appbutton8.setBorderPainted(false);
        fun_appbutton8.setFocusPainted(false);
        fun_appbutton8.setFocusable(false);
        fun_appbutton8.setName("fun_appbutton8"); // NOI18N
        fun_appbutton8.setRequestFocusEnabled(false);
        fun_appbutton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fun_appbutton8ActionPerformed(evt);
            }
        });
        fun_app8.add(fun_appbutton8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 70, 60));

        appspanel.add(fun_app8);

        work_app9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        work_removeapp9.setBackground(new java.awt.Color(0, 0, 0));
        work_removeapp9.setFont(new java.awt.Font("SF Pro Display", 1, 24)); // NOI18N
        work_removeapp9.setForeground(new java.awt.Color(255, 255, 255));
        work_removeapp9.setText("-");
        work_removeapp9.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        work_removeapp9.setBorderPainted(false);
        work_removeapp9.setFocusPainted(false);
        work_removeapp9.setFocusable(false);
        work_removeapp9.setName("work_removeapp9"); // NOI18N
        work_removeapp9.setRequestFocusEnabled(false);
        work_removeapp9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                work_removeapp9ActionPerformed(evt);
            }
        });
        work_app9.add(work_removeapp9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 20, 20));

        work_appbutton9.setBackground(new java.awt.Color(214, 46, 46));
        work_appbutton9.setFont(new java.awt.Font("Segoe MDL2 Assets", 0, 24)); // NOI18N
        work_appbutton9.setForeground(new java.awt.Color(255, 255, 255));
        work_appbutton9.setText("");
        work_appbutton9.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        work_appbutton9.setBorderPainted(false);
        work_appbutton9.setFocusPainted(false);
        work_appbutton9.setFocusable(false);
        work_appbutton9.setName("work_appbutton9"); // NOI18N
        work_appbutton9.setRequestFocusEnabled(false);
        work_appbutton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                work_appbutton9ActionPerformed(evt);
            }
        });
        work_app9.add(work_appbutton9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 70, 60));

        appspanel.add(work_app9);

        fun_app9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        fun_removeapp9.setBackground(new java.awt.Color(0, 0, 0));
        fun_removeapp9.setFont(new java.awt.Font("SF Pro Display", 1, 24)); // NOI18N
        fun_removeapp9.setForeground(new java.awt.Color(255, 255, 255));
        fun_removeapp9.setText("-");
        fun_removeapp9.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        fun_removeapp9.setBorderPainted(false);
        fun_removeapp9.setFocusPainted(false);
        fun_removeapp9.setFocusable(false);
        fun_removeapp9.setName("fun_removeapp9"); // NOI18N
        fun_removeapp9.setRequestFocusEnabled(false);
        fun_removeapp9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fun_removeapp9ActionPerformed(evt);
            }
        });
        fun_app9.add(fun_removeapp9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 20, 20));

        fun_appbutton9.setBackground(new java.awt.Color(76, 90, 35));
        fun_appbutton9.setFont(new java.awt.Font("Segoe MDL2 Assets", 0, 24)); // NOI18N
        fun_appbutton9.setForeground(new java.awt.Color(255, 255, 255));
        fun_appbutton9.setText("");
        fun_appbutton9.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        fun_appbutton9.setBorderPainted(false);
        fun_appbutton9.setFocusPainted(false);
        fun_appbutton9.setFocusable(false);
        fun_appbutton9.setName("fun_appbutton9"); // NOI18N
        fun_appbutton9.setRequestFocusEnabled(false);
        fun_appbutton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fun_appbutton9ActionPerformed(evt);
            }
        });
        fun_app9.add(fun_appbutton9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 70, 60));

        appspanel.add(fun_app9);

        work_app10.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        work_removeapp10.setBackground(new java.awt.Color(0, 0, 0));
        work_removeapp10.setFont(new java.awt.Font("SF Pro Display", 1, 24)); // NOI18N
        work_removeapp10.setForeground(new java.awt.Color(255, 255, 255));
        work_removeapp10.setText("-");
        work_removeapp10.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        work_removeapp10.setBorderPainted(false);
        work_removeapp10.setFocusable(false);
        work_removeapp10.setName("work_removeapp10"); // NOI18N
        work_removeapp10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                work_removeapp10ActionPerformed(evt);
            }
        });
        work_app10.add(work_removeapp10, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 20, 20));

        work_appbutton10.setBackground(new java.awt.Color(214, 46, 46));
        work_appbutton10.setFont(new java.awt.Font("Segoe MDL2 Assets", 0, 24)); // NOI18N
        work_appbutton10.setForeground(new java.awt.Color(255, 255, 255));
        work_appbutton10.setText("");
        work_appbutton10.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        work_appbutton10.setBorderPainted(false);
        work_appbutton10.setFocusable(false);
        work_appbutton10.setName("work_appbutton10"); // NOI18N
        work_appbutton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                work_appbutton10ActionPerformed(evt);
            }
        });
        work_app10.add(work_appbutton10, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 70, 60));

        appspanel.add(work_app10);

        fun_app10.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        fun_removeapp10.setBackground(new java.awt.Color(0, 0, 0));
        fun_removeapp10.setFont(new java.awt.Font("SF Pro Display", 1, 24)); // NOI18N
        fun_removeapp10.setForeground(new java.awt.Color(255, 255, 255));
        fun_removeapp10.setText("-");
        fun_removeapp10.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        fun_removeapp10.setBorderPainted(false);
        fun_removeapp10.setFocusable(false);
        fun_removeapp10.setName("fun_removeapp10"); // NOI18N
        fun_removeapp10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fun_removeapp10ActionPerformed(evt);
            }
        });
        fun_app10.add(fun_removeapp10, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 20, 20));

        fun_appbutton10.setBackground(new java.awt.Color(76, 90, 35));
        fun_appbutton10.setFont(new java.awt.Font("Segoe MDL2 Assets", 0, 24)); // NOI18N
        fun_appbutton10.setForeground(new java.awt.Color(255, 255, 255));
        fun_appbutton10.setText("");
        fun_appbutton10.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        fun_appbutton10.setBorderPainted(false);
        fun_appbutton10.setFocusable(false);
        fun_appbutton10.setName("fun_appbutton10"); // NOI18N
        fun_appbutton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fun_appbutton10ActionPerformed(evt);
            }
        });
        fun_app10.add(fun_appbutton10, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 70, 60));

        appspanel.add(fun_app10);

        content_panel.add(appspanel, java.awt.BorderLayout.WEST);

        actions.setBackground(new java.awt.Color(96, 0, 0));
        actions.setBorder(javax.swing.BorderFactory.createEmptyBorder(40, 40, 40, 40));
        actions.setOpaque(false);
        actions.setLayout(new java.awt.BorderLayout());

        jPanel49.setOpaque(false);
        jPanel49.setLayout(new java.awt.GridLayout(1, 0));

        settings.setFont(new java.awt.Font("SF Pro Display", 0, 14)); // NOI18N
        settings.setForeground(new java.awt.Color(255, 255, 255));
        settings.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pomi/resources/GUIicons/settings.png"))); // NOI18N
        settings.setText("Settings");
        settings.setBorder(null);
        settings.setContentAreaFilled(false);
        settings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingsActionPerformed(evt);
            }
        });
        jPanel49.add(settings);

        exit.setFont(new java.awt.Font("SF Pro Display", 0, 14)); // NOI18N
        exit.setForeground(new java.awt.Color(255, 255, 255));
        exit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pomi/resources/GUIicons/logout.png"))); // NOI18N
        exit.setText("Exit");
        exit.setBorder(null);
        exit.setContentAreaFilled(false);
        exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitActionPerformed(evt);
            }
        });
        jPanel49.add(exit);

        actions.add(jPanel49, java.awt.BorderLayout.EAST);

        content_panel.add(actions, java.awt.BorderLayout.SOUTH);

        timer_panel.setBackground(new java.awt.Color(96, 0, 0));
        timer_panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(150, 150, 150, 150));
        timer_panel.setOpaque(false);
        timer_panel.setLayout(new java.awt.BorderLayout());

        heading_panel.setOpaque(false);
        heading_panel.setLayout(new java.awt.GridLayout(2, 1));

        instruct_pet.setFont(new java.awt.Font("SF Pro Display", 1, 24)); // NOI18N
        instruct_pet.setForeground(new java.awt.Color(255, 255, 255));
        instruct_pet.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        instruct_pet.setText("Tap on Pomi to begin!");
        instruct_pet.setToolTipText("");
        heading_panel.add(instruct_pet);

        instruct_pet1.setFont(new java.awt.Font("SF Pro Display", 0, 14)); // NOI18N
        instruct_pet1.setForeground(new java.awt.Color(255, 255, 255));
        instruct_pet1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        instruct_pet1.setText("Please place all your apps on the app launcher.   ");
        heading_panel.add(instruct_pet1);

        timer_panel.add(heading_panel, java.awt.BorderLayout.NORTH);

        clock.setOpaque(false);
        clock.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        successTimer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pomi/resources/pets/pomi/pomi_timer.gif"))); // NOI18N
        successTimer.setBorderPainted(false);
        successTimer.setContentAreaFilled(false);
        successTimer.setFocusPainted(false);
        successTimer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                successTimerActionPerformed(evt);
            }
        });
        clock.add(successTimer, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 120, 350, 320));

        startButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pomi/resources/pets/pomi/pomi_start.png"))); // NOI18N
        startButton.setContentAreaFilled(false);
        startButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        startButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                startButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                startButtonMouseExited(evt);
            }
        });
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });
        clock.add(startButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 130, 300, 300));

        maxtimepomodoro.setFont(new java.awt.Font("SF Pro Display", 0, 24)); // NOI18N
        maxtimepomodoro.setForeground(new java.awt.Color(255, 255, 255));
        maxtimepomodoro.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        maxtimepomodoro.setText("00:15:30");
        clock.add(maxtimepomodoro, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 320, 310, -1));

        timerpomodoro.setFont(new java.awt.Font("SF Pro Display", 1, 60)); // NOI18N
        timerpomodoro.setForeground(new java.awt.Color(255, 255, 255));
        timerpomodoro.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        timerpomodoro.setText("00:00:00");
        clock.add(timerpomodoro, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 190, 320, -1));

        taskpomodoro.setFont(new java.awt.Font("SF Pro Display", 0, 36)); // NOI18N
        taskpomodoro.setForeground(new java.awt.Color(255, 255, 255));
        taskpomodoro.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        taskpomodoro.setText("Pomodoro");
        clock.add(taskpomodoro, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 270, 330, -1));

        timerpomodoro1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pomi/resources/clocks/pause.png"))); // NOI18N
        timerpomodoro1.setContentAreaFilled(false);
        timerpomodoro1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        timerpomodoro1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timerpomodoro1ActionPerformed(evt);
            }
        });
        clock.add(timerpomodoro1, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 370, 40, 40));

        resetpomodoro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pomi/resources/clocks/reset.png"))); // NOI18N
        resetpomodoro.setContentAreaFilled(false);
        resetpomodoro.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        resetpomodoro.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        resetpomodoro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetpomodoroActionPerformed(evt);
            }
        });
        clock.add(resetpomodoro, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 370, 40, 40));

        playbutton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pomi/resources/clocks/playbutton.png"))); // NOI18N
        playbutton.setContentAreaFilled(false);
        playbutton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        playbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playbuttonActionPerformed(evt);
            }
        });
        clock.add(playbutton, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 130, 200, 300));

        setspomodoro.setOpaque(false);
        setspomodoro.setLayout(new java.awt.GridLayout(1, 0, 3, 0));

        pomodoroset1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pomi/resources/clocks/notset circle.png"))); // NOI18N
        setspomodoro.add(pomodoroset1);

        pomodoroset2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pomi/resources/clocks/notset circle.png"))); // NOI18N
        setspomodoro.add(pomodoroset2);

        pomodoroset3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pomi/resources/clocks/notset circle.png"))); // NOI18N
        setspomodoro.add(pomodoroset3);

        pomodoroset4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pomi/resources/clocks/notset circle.png"))); // NOI18N
        setspomodoro.add(pomodoroset4);
        setspomodoro.add(blank);

        pomodoroset5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pomi/resources/clocks/notset circle.png"))); // NOI18N
        setspomodoro.add(pomodoroset5);

        pomodoroset6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pomi/resources/clocks/notset circle.png"))); // NOI18N
        setspomodoro.add(pomodoroset6);

        pomodoroset7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pomi/resources/clocks/notset circle.png"))); // NOI18N
        setspomodoro.add(pomodoroset7);

        pomodoroset8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pomi/resources/clocks/notset circle.png"))); // NOI18N
        setspomodoro.add(pomodoroset8);

        clock.add(setspomodoro, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 460, -1, -1));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pomi/resources/clocks/circle.png"))); // NOI18N
        clock.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(-70, 80, -1, -1));

        timer_panel.add(clock, java.awt.BorderLayout.CENTER);

        content_panel.add(timer_panel, java.awt.BorderLayout.CENTER);

        workspace_panel.setBackground(new java.awt.Color(96, 0, 0));
        workspace_panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(40, 20, 40, 40));
        workspace_panel.setOpaque(false);
        workspace_panel.setLayout(new java.awt.BorderLayout());

        time_date_panel.setBackground(new java.awt.Color(96, 0, 0));
        time_date_panel.setOpaque(false);
        time_date_panel.setLayout(new java.awt.BorderLayout());

        timeicon.setFont(new java.awt.Font("SF Pro Display", 0, 14)); // NOI18N
        timeicon.setForeground(new java.awt.Color(255, 255, 255));
        timeicon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        timeicon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pomi/resources/timer/iconclock.png"))); // NOI18N
        time_date_panel.add(timeicon, java.awt.BorderLayout.WEST);

        jPanel30.setBackground(new java.awt.Color(96, 0, 0));
        jPanel30.setOpaque(false);
        jPanel30.setLayout(new java.awt.GridLayout(2, 1));

        time.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        time.setForeground(new java.awt.Color(255, 255, 255));
        time.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        time.setText("Time");
        jPanel30.add(time);

        date.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        date.setForeground(new java.awt.Color(255, 255, 255));
        date.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        date.setText("Date");
        jPanel30.add(date);

        time_date_panel.add(jPanel30, java.awt.BorderLayout.CENTER);

        workspace_panel.add(time_date_panel, java.awt.BorderLayout.NORTH);

        taskslist.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 0, 20, 0));
        taskslist.setFocusable(false);
        taskslist.setOpaque(false);
        taskslist.setLayout(new java.awt.GridLayout(8, 1, 0, 4));

        task1.setBackground(new java.awt.Color(255, 255, 255));
        task1.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        task1.setLayout(new java.awt.GridLayout(1, 0));

        pomodorotasktime1.setFont(new java.awt.Font("SF Pro Display", 1, 18)); // NOI18N
        pomodorotasktime1.setForeground(new java.awt.Color(23, 39, 53));
        pomodorotasktime1.setText("12:30 pm");
        task1.add(pomodorotasktime1);

        pomodorotasktextfield1.setFont(new java.awt.Font("SF Pro Display", 0, 18)); // NOI18N
        pomodorotasktextfield1.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        pomodorotasktextfield1.setText("---");
        pomodorotasktextfield1.setToolTipText("");
        pomodorotasktextfield1.setBorder(null);
        pomodorotasktextfield1.setFocusable(false);
        pomodorotasktextfield1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                pomodorotasktextfield1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                pomodorotasktextfield1MouseExited(evt);
            }
        });
        task1.add(pomodorotasktextfield1);

        taskslist.add(task1);

        task2.setBackground(new java.awt.Color(255, 255, 255));
        task2.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        task2.setLayout(new java.awt.GridLayout(1, 0));

        pomodorotasktime2.setFont(new java.awt.Font("SF Pro Display", 1, 18)); // NOI18N
        pomodorotasktime2.setForeground(new java.awt.Color(23, 39, 53));
        pomodorotasktime2.setText("12:30 pm");
        task2.add(pomodorotasktime2);

        pomodorotasktextfield2.setFont(new java.awt.Font("SF Pro Display", 0, 18)); // NOI18N
        pomodorotasktextfield2.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        pomodorotasktextfield2.setText("---");
        pomodorotasktextfield2.setToolTipText("");
        pomodorotasktextfield2.setBorder(null);
        pomodorotasktextfield2.setFocusable(false);
        pomodorotasktextfield2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                pomodorotasktextfield2MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                pomodorotasktextfield2MouseExited(evt);
            }
        });
        task2.add(pomodorotasktextfield2);

        taskslist.add(task2);

        task3.setBackground(new java.awt.Color(255, 255, 255));
        task3.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        task3.setLayout(new java.awt.GridLayout(1, 0));

        pomodorotasktime3.setFont(new java.awt.Font("SF Pro Display", 1, 18)); // NOI18N
        pomodorotasktime3.setForeground(new java.awt.Color(23, 39, 53));
        pomodorotasktime3.setText("12:30 pm");
        task3.add(pomodorotasktime3);

        pomodorotasktextfield3.setFont(new java.awt.Font("SF Pro Display", 0, 18)); // NOI18N
        pomodorotasktextfield3.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        pomodorotasktextfield3.setText("---");
        pomodorotasktextfield3.setToolTipText("");
        pomodorotasktextfield3.setBorder(null);
        pomodorotasktextfield3.setFocusable(false);
        pomodorotasktextfield3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                pomodorotasktextfield3MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                pomodorotasktextfield3MouseExited(evt);
            }
        });
        task3.add(pomodorotasktextfield3);

        taskslist.add(task3);

        task4.setBackground(new java.awt.Color(255, 255, 255));
        task4.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        task4.setLayout(new java.awt.GridLayout(1, 0));

        pomodorotasktime4.setFont(new java.awt.Font("SF Pro Display", 1, 18)); // NOI18N
        pomodorotasktime4.setForeground(new java.awt.Color(23, 39, 53));
        pomodorotasktime4.setText("12:30 pm");
        task4.add(pomodorotasktime4);

        pomodorotasktextfield4.setFont(new java.awt.Font("SF Pro Display", 0, 18)); // NOI18N
        pomodorotasktextfield4.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        pomodorotasktextfield4.setText("---");
        pomodorotasktextfield4.setToolTipText("");
        pomodorotasktextfield4.setBorder(null);
        pomodorotasktextfield4.setFocusable(false);
        pomodorotasktextfield4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                pomodorotasktextfield4MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                pomodorotasktextfield4MouseExited(evt);
            }
        });
        task4.add(pomodorotasktextfield4);

        taskslist.add(task4);

        task5.setBackground(new java.awt.Color(255, 255, 255));
        task5.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        task5.setLayout(new java.awt.GridLayout(1, 0));

        pomodorotasktime5.setFont(new java.awt.Font("SF Pro Display", 1, 18)); // NOI18N
        pomodorotasktime5.setForeground(new java.awt.Color(23, 39, 53));
        pomodorotasktime5.setText("12:30 pm");
        task5.add(pomodorotasktime5);

        pomodorotasktextfield5.setFont(new java.awt.Font("SF Pro Display", 0, 18)); // NOI18N
        pomodorotasktextfield5.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        pomodorotasktextfield5.setText("---");
        pomodorotasktextfield5.setToolTipText("");
        pomodorotasktextfield5.setBorder(null);
        pomodorotasktextfield5.setFocusable(false);
        pomodorotasktextfield5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                pomodorotasktextfield5MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                pomodorotasktextfield5MouseExited(evt);
            }
        });
        task5.add(pomodorotasktextfield5);

        taskslist.add(task5);

        task6.setBackground(new java.awt.Color(255, 255, 255));
        task6.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        task6.setLayout(new java.awt.GridLayout(1, 0));

        pomodorotasktime6.setFont(new java.awt.Font("SF Pro Display", 1, 18)); // NOI18N
        pomodorotasktime6.setForeground(new java.awt.Color(23, 39, 53));
        pomodorotasktime6.setText("12:30 pm");
        task6.add(pomodorotasktime6);

        pomodorotasktextfield6.setFont(new java.awt.Font("SF Pro Display", 0, 18)); // NOI18N
        pomodorotasktextfield6.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        pomodorotasktextfield6.setText("---");
        pomodorotasktextfield6.setToolTipText("");
        pomodorotasktextfield6.setBorder(null);
        pomodorotasktextfield6.setFocusable(false);
        pomodorotasktextfield6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                pomodorotasktextfield6MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                pomodorotasktextfield6MouseExited(evt);
            }
        });
        task6.add(pomodorotasktextfield6);

        taskslist.add(task6);

        task7.setBackground(new java.awt.Color(255, 255, 255));
        task7.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        task7.setLayout(new java.awt.GridLayout(1, 0));

        pomodorotasktime7.setFont(new java.awt.Font("SF Pro Display", 1, 18)); // NOI18N
        pomodorotasktime7.setForeground(new java.awt.Color(23, 39, 53));
        pomodorotasktime7.setText("12:30 pm");
        task7.add(pomodorotasktime7);

        pomodorotasktextfield7.setFont(new java.awt.Font("SF Pro Display", 0, 18)); // NOI18N
        pomodorotasktextfield7.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        pomodorotasktextfield7.setText("---");
        pomodorotasktextfield7.setToolTipText("");
        pomodorotasktextfield7.setBorder(null);
        pomodorotasktextfield7.setFocusable(false);
        pomodorotasktextfield7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                pomodorotasktextfield7MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                pomodorotasktextfield7MouseExited(evt);
            }
        });
        task7.add(pomodorotasktextfield7);

        taskslist.add(task7);

        task8.setBackground(new java.awt.Color(255, 255, 255));
        task8.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        task8.setLayout(new java.awt.GridLayout(1, 0));

        pomodorotasktime8.setFont(new java.awt.Font("SF Pro Display", 1, 18)); // NOI18N
        pomodorotasktime8.setForeground(new java.awt.Color(23, 39, 53));
        pomodorotasktime8.setText("12:30 pm");
        task8.add(pomodorotasktime8);

        pomodorotasktextfield8.setFont(new java.awt.Font("SF Pro Display", 0, 18)); // NOI18N
        pomodorotasktextfield8.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        pomodorotasktextfield8.setText("---");
        pomodorotasktextfield8.setToolTipText("");
        pomodorotasktextfield8.setBorder(null);
        pomodorotasktextfield8.setFocusable(false);
        pomodorotasktextfield8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                pomodorotasktextfield8MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                pomodorotasktextfield8MouseExited(evt);
            }
        });
        task8.add(pomodorotasktextfield8);

        taskslist.add(task8);

        workspace_panel.add(taskslist, java.awt.BorderLayout.CENTER);

        stickynotes.setOpaque(false);
        stickynotes.setPreferredSize(new java.awt.Dimension(266, 266));
        stickynotes.setLayout(new java.awt.GridLayout(1, 0));

        notes.setBackground(new java.awt.Color(255, 221, 0));
        notes.setColumns(20);
        notes.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        notes.setForeground(new java.awt.Color(0, 0, 0));
        notes.setLineWrap(true);
        notes.setRows(5);
        notes.setWrapStyleWord(true);
        notes.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        notes.setCaretColor(new java.awt.Color(0, 0, 0));
        notes.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                notesKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(notes);

        stickynotes.add(jScrollPane1);

        workspace_panel.add(stickynotes, java.awt.BorderLayout.SOUTH);

        content_panel.add(workspace_panel, java.awt.BorderLayout.EAST);

        getContentPane().add(content_panel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitActionPerformed
        int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Confirm Exit", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION){
            System.exit(0);
        }
    }//GEN-LAST:event_exitActionPerformed

    private void oknameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_oknameActionPerformed
        addname.setVisible(false);
        name = user.getText();

        // Call welcome() method
        welcome();
        welcome.setVisible(true);
    }//GEN-LAST:event_oknameActionPerformed

    private void choosepet_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_choosepet_buttonActionPerformed
        
        String selectedValue = null;
        for (Enumeration<AbstractButton> buttons = chooseapet.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();
            // Check if the current button is selected
            if (button.isSelected()) {
                selectedValue = button.getText();
                break; // Once found, break the loop
            }
        }
        
        // Map selected pet name to an index
        switch (selectedValue) {
            case "Pomi":
                pet = 0;
                break;
            case "Terry":
                pet = 1;
                break;
            case "Penny":
                pet = 2;
                break;
            case "Felix":
                pet = 3;
                break;
            default:
                // Handle unrecognized pet name here
                break;
        }
        saveProfile();
        
        loadProfile();
        choosepet.setVisible(false);
    }//GEN-LAST:event_choosepet_buttonActionPerformed

    private void settingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settingsActionPerformed
        settingsDialog.setVisible(true);
    }//GEN-LAST:event_settingsActionPerformed

    private void userKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_userKeyReleased
        okname.setEnabled(true);
    }//GEN-LAST:event_userKeyReleased

    private void playbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playbuttonActionPerformed
        playbutton.setVisible(false);
        
        timerpomodoro(seconds-1, lastSecond);
        setUITimerMode(true);
    }//GEN-LAST:event_playbuttonActionPerformed

    private void timerpomodoro1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timerpomodoro1ActionPerformed
        pauseTimer();        
    }//GEN-LAST:event_timerpomodoro1ActionPerformed

    private void work_appbutton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_work_appbutton1ActionPerformed
        addApp(0,work_appbutton1, false);
    }//GEN-LAST:event_work_appbutton1ActionPerformed

    private void work_appbutton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_work_appbutton2ActionPerformed
        addApp(1,work_appbutton2, false);
    }//GEN-LAST:event_work_appbutton2ActionPerformed

    private void work_appbutton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_work_appbutton3ActionPerformed
        addApp(2,work_appbutton3, false);
    }//GEN-LAST:event_work_appbutton3ActionPerformed

    private void work_appbutton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_work_appbutton4ActionPerformed
        addApp(3,work_appbutton4, false);
    }//GEN-LAST:event_work_appbutton4ActionPerformed

    private void work_appbutton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_work_appbutton5ActionPerformed
        addApp(4,work_appbutton5, false);
    }//GEN-LAST:event_work_appbutton5ActionPerformed

    private void work_appbutton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_work_appbutton6ActionPerformed
        addApp(5,work_appbutton6, false);
    }//GEN-LAST:event_work_appbutton6ActionPerformed

    private void work_appbutton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_work_appbutton7ActionPerformed
        addApp(6,work_appbutton7, false);
    }//GEN-LAST:event_work_appbutton7ActionPerformed

    private void work_appbutton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_work_appbutton8ActionPerformed
        addApp(7,work_appbutton8, false);
    }//GEN-LAST:event_work_appbutton8ActionPerformed

    private void work_appbutton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_work_appbutton9ActionPerformed
        addApp(8,work_appbutton9, false);
    }//GEN-LAST:event_work_appbutton9ActionPerformed

    private void work_appbutton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_work_appbutton10ActionPerformed
        addApp(9,work_appbutton10, false);
    }//GEN-LAST:event_work_appbutton10ActionPerformed

    private void fun_appbutton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fun_appbutton1ActionPerformed
        addApp(0,fun_appbutton1, true);
    }//GEN-LAST:event_fun_appbutton1ActionPerformed

    private void fun_appbutton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fun_appbutton2ActionPerformed
        addApp(1,fun_appbutton2, true);
    }//GEN-LAST:event_fun_appbutton2ActionPerformed

    private void fun_appbutton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fun_appbutton3ActionPerformed
        addApp(2,fun_appbutton3, true);
    }//GEN-LAST:event_fun_appbutton3ActionPerformed

    private void fun_appbutton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fun_appbutton4ActionPerformed
        addApp(3,fun_appbutton4, true);
    }//GEN-LAST:event_fun_appbutton4ActionPerformed

    private void fun_appbutton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fun_appbutton5ActionPerformed
        addApp(4,fun_appbutton5, true);
    }//GEN-LAST:event_fun_appbutton5ActionPerformed

    private void fun_appbutton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fun_appbutton6ActionPerformed
        addApp(5,fun_appbutton6, true);
    }//GEN-LAST:event_fun_appbutton6ActionPerformed

    private void fun_appbutton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fun_appbutton7ActionPerformed
        addApp(6,fun_appbutton7, true);
    }//GEN-LAST:event_fun_appbutton7ActionPerformed

    private void fun_appbutton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fun_appbutton8ActionPerformed
        addApp(7,fun_appbutton8, true);
    }//GEN-LAST:event_fun_appbutton8ActionPerformed

    private void fun_appbutton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fun_appbutton9ActionPerformed
        addApp(8,fun_appbutton9, true);
    }//GEN-LAST:event_fun_appbutton9ActionPerformed

    private void fun_appbutton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fun_appbutton10ActionPerformed
        addApp(9,fun_appbutton10, true);
    }//GEN-LAST:event_fun_appbutton10ActionPerformed

    private void resetpomodoroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetpomodoroActionPerformed
        int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to reset the Pomodoro progress?", "Reset Pomodoro", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION) {
            stopTimer();
            reset();
        }
    }//GEN-LAST:event_resetpomodoroActionPerformed

    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
        startTimer();
    }//GEN-LAST:event_startButtonActionPerformed

    private void work_removeapp1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_work_removeapp1ActionPerformed
        removeapp(0,work_appbutton1, false);
    }//GEN-LAST:event_work_removeapp1ActionPerformed

    private void fun_removeapp1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fun_removeapp1ActionPerformed
        removeapp(0,fun_appbutton1, true);
    }//GEN-LAST:event_fun_removeapp1ActionPerformed

    private void work_removeapp2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_work_removeapp2ActionPerformed
        removeapp(1,work_appbutton2, false);
    }//GEN-LAST:event_work_removeapp2ActionPerformed

    private void fun_removeapp2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fun_removeapp2ActionPerformed
        removeapp(1,fun_appbutton2, true);
    }//GEN-LAST:event_fun_removeapp2ActionPerformed

    private void work_removeapp3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_work_removeapp3ActionPerformed
        removeapp(2,work_appbutton3, false);
    }//GEN-LAST:event_work_removeapp3ActionPerformed

    private void fun_removeapp3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fun_removeapp3ActionPerformed
        removeapp(2,fun_appbutton3, true);
    }//GEN-LAST:event_fun_removeapp3ActionPerformed

    private void work_removeapp4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_work_removeapp4ActionPerformed
        removeapp(3,work_appbutton4, false);
    }//GEN-LAST:event_work_removeapp4ActionPerformed

    private void fun_removeapp4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fun_removeapp4ActionPerformed
        removeapp(3,fun_appbutton4, true);
    }//GEN-LAST:event_fun_removeapp4ActionPerformed

    private void work_removeapp5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_work_removeapp5ActionPerformed
        removeapp(4,work_appbutton5, false);
    }//GEN-LAST:event_work_removeapp5ActionPerformed

    private void fun_removeapp5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fun_removeapp5ActionPerformed
        removeapp(4,fun_appbutton5, true);
    }//GEN-LAST:event_fun_removeapp5ActionPerformed

    private void work_removeapp6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_work_removeapp6ActionPerformed
        removeapp(5,work_appbutton6, false);
    }//GEN-LAST:event_work_removeapp6ActionPerformed

    private void fun_removeapp6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fun_removeapp6ActionPerformed
        removeapp(5,fun_appbutton6, true);
    }//GEN-LAST:event_fun_removeapp6ActionPerformed

    private void work_removeapp7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_work_removeapp7ActionPerformed
        removeapp(6,work_appbutton7, false);
    }//GEN-LAST:event_work_removeapp7ActionPerformed

    private void fun_removeapp7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fun_removeapp7ActionPerformed
        removeapp(6,fun_appbutton7, true);
    }//GEN-LAST:event_fun_removeapp7ActionPerformed

    private void work_removeapp8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_work_removeapp8ActionPerformed
        removeapp(7,work_appbutton8, false);
    }//GEN-LAST:event_work_removeapp8ActionPerformed

    private void fun_removeapp8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fun_removeapp8ActionPerformed
        removeapp(7,fun_appbutton8, true);
    }//GEN-LAST:event_fun_removeapp8ActionPerformed

    private void work_removeapp9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_work_removeapp9ActionPerformed
        removeapp(8,work_appbutton9, false);
    }//GEN-LAST:event_work_removeapp9ActionPerformed

    private void fun_removeapp9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fun_removeapp9ActionPerformed
        removeapp(8,fun_appbutton9, true);
    }//GEN-LAST:event_fun_removeapp9ActionPerformed

    private void work_removeapp10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_work_removeapp10ActionPerformed
        removeapp(9,work_appbutton10, false);
    }//GEN-LAST:event_work_removeapp10ActionPerformed

    private void fun_removeapp10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fun_removeapp10ActionPerformed
        removeapp(9,fun_appbutton10, true);
    }//GEN-LAST:event_fun_removeapp10ActionPerformed

    private void pomodorotasktextfield1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pomodorotasktextfield1MouseEntered
        pomodorotasktextfield1.setFocusable(true);
    }//GEN-LAST:event_pomodorotasktextfield1MouseEntered

    private void pomodorotasktextfield2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pomodorotasktextfield2MouseEntered
        pomodorotasktextfield2.setFocusable(true);
    }//GEN-LAST:event_pomodorotasktextfield2MouseEntered

    private void pomodorotasktextfield4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pomodorotasktextfield4MouseEntered
        pomodorotasktextfield4.setFocusable(true);
    }//GEN-LAST:event_pomodorotasktextfield4MouseEntered

    private void pomodorotasktextfield5MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pomodorotasktextfield5MouseEntered
        pomodorotasktextfield5.setFocusable(true);
    }//GEN-LAST:event_pomodorotasktextfield5MouseEntered

    private void pomodorotasktextfield6MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pomodorotasktextfield6MouseEntered
        pomodorotasktextfield6.setFocusable(true);
    }//GEN-LAST:event_pomodorotasktextfield6MouseEntered

    private void pomodorotasktextfield7MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pomodorotasktextfield7MouseEntered
        pomodorotasktextfield7.setFocusable(true);
    }//GEN-LAST:event_pomodorotasktextfield7MouseEntered

    private void pomodorotasktextfield8MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pomodorotasktextfield8MouseEntered
        pomodorotasktextfield8.setFocusable(true);
    }//GEN-LAST:event_pomodorotasktextfield8MouseEntered

    private void pomodorotasktextfield3MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pomodorotasktextfield3MouseEntered
        pomodorotasktextfield3.setFocusable(true);
    }//GEN-LAST:event_pomodorotasktextfield3MouseEntered

    private void pomodorotasktextfield1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pomodorotasktextfield1MouseExited
         pomodorotasktextfield1.setFocusable(false);
         taskManager[0] = pomodorotasktextfield1.getText();
    }//GEN-LAST:event_pomodorotasktextfield1MouseExited

    private void pomodorotasktextfield2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pomodorotasktextfield2MouseExited
         pomodorotasktextfield2.setFocusable(false);
         taskManager[1] = pomodorotasktextfield2.getText();
    }//GEN-LAST:event_pomodorotasktextfield2MouseExited

    private void pomodorotasktextfield3MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pomodorotasktextfield3MouseExited
         pomodorotasktextfield3.setFocusable(false);
         taskManager[2] = pomodorotasktextfield3.getText();
    }//GEN-LAST:event_pomodorotasktextfield3MouseExited

    private void pomodorotasktextfield4MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pomodorotasktextfield4MouseExited
         pomodorotasktextfield4.setFocusable(false);
         taskManager[3] = pomodorotasktextfield4.getText();
    }//GEN-LAST:event_pomodorotasktextfield4MouseExited

    private void pomodorotasktextfield5MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pomodorotasktextfield5MouseExited
        pomodorotasktextfield5.setFocusable(false);
        taskManager[4] = pomodorotasktextfield5.getText();
    }//GEN-LAST:event_pomodorotasktextfield5MouseExited

    private void pomodorotasktextfield6MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pomodorotasktextfield6MouseExited
        pomodorotasktextfield6.setFocusable(false);
        taskManager[5] = pomodorotasktextfield6.getText();
    }//GEN-LAST:event_pomodorotasktextfield6MouseExited

    private void pomodorotasktextfield7MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pomodorotasktextfield7MouseExited
        pomodorotasktextfield7.setFocusable(false);
        taskManager[6] = pomodorotasktextfield7.getText();
    }//GEN-LAST:event_pomodorotasktextfield7MouseExited

    private void pomodorotasktextfield8MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pomodorotasktextfield8MouseExited
        pomodorotasktextfield8.setFocusable(false);
        taskManager[7] = pomodorotasktextfield8.getText();
    }//GEN-LAST:event_pomodorotasktextfield8MouseExited

    private void notesKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_notesKeyReleased
        saveNotes();
    }//GEN-LAST:event_notesKeyReleased

    private void successTimerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_successTimerActionPerformed
        setSetUI();
        pomodoroLogic();
        setUITimerMode(true);
        successTimer.setVisible(false);
        stopMusic();
    }//GEN-LAST:event_successTimerActionPerformed
    
    private Clip clip;

    public void playMusic() {
        try {
            // Load the audio file
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("ringtone.wav"));
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            
            // Start playing the audio
            clip.loop(Clip.LOOP_CONTINUOUSLY); // You can also use clip.start() for playing once without looping
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
        }
    }

    public void stopMusic() {
        if (clip != null) {
            clip.stop();
            clip.close();
        }
    }
    
    void pomodoroLogic(){
        if(iteration % 2 == 0){
            set++;
            enableEnter();
            taskpomodoro.setText(taskManager[set]);
            maxtimepomodoro.setText(stringConvert(secondsToMillis(duration[0] * 60)));
            timerpomodoro(0,duration[0]*60);            
            disableEnter();
        }else{
            if (set == 3 || set == 7){
                taskpomodoro.setText("Long Break!");
                maxtimepomodoro.setText(stringConvert(secondsToMillis(duration[2] * 60)));
                timerpomodoro(0,duration[2]*60);
            }
            else{
                taskpomodoro.setText("Break!");
                maxtimepomodoro.setText(stringConvert(secondsToMillis(duration[1] * 60)));
                timerpomodoro(0,duration[1]*60);
            }
            enableEnter();
        }
    }
    
    void setSetUI(){
            String path = Paths.get("resources", "clocks","set circle.png").toString();

        
            switch (set){
            case 0:
              pomodoroset1.setIcon(new ImageIcon(path));
              break;
            case 1:
              pomodoroset2.setIcon(new ImageIcon(path));
              break;
            case 2:
              pomodoroset3.setIcon(new ImageIcon(path));
              break;
            case 3:
              pomodoroset4.setIcon(new ImageIcon(path));
              break;
            case 4:
              pomodoroset5.setIcon(new ImageIcon(path));
              break;
            case 5:
              pomodoroset6.setIcon(new ImageIcon(path));
              break;
            case 6:
              pomodoroset7.setIcon(new ImageIcon(path));
              break;
            default:
              pomodoroset8.setIcon(new ImageIcon(path));
              break;
        }
    }
    
    private void signin_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_signin_buttonActionPerformed
        String username = signin_username_field.getText();
        char[] passwordChars = signin_password_field.getPassword();
        String password = new String(passwordChars);

        if (validateInput(username, password)) {
            userId = signIn(username, password);
            if (userId != -1) {
                // Sign in successful
                showMessageDialog("Sign In Successful!");
                // Here, you can use the userId as needed
                signup_username_field.setText("");
                signup_password_field.setText("");
                access.setVisible(false);
                loadProfile();
            } else {
                // Sign in failed
                showMessageDialog("Sign In Failed! Invalid username or password.");
            }
        }
    }//GEN-LAST:event_signin_buttonActionPerformed
    
    private void showMessageDialog(String message) {
        javax.swing.JOptionPane.showMessageDialog(this, message);
    }
    
    private void signup_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_signup_buttonActionPerformed
        String username = signup_username_field.getText();
        char[] passwordChars = signup_password_field.getPassword();
        String password = new String(passwordChars);

        if (validateInput(username, password)) {
            if (signUp(username, password)) {
                // Sign up successful
                showMessageDialog("Sign Up Successful!");
                signup_username_field.setText("");
                signup_password_field.setText("");
                signinOrsignup.setSelectedIndex(0);
            } else {
                // Sign up failed
                showMessageDialog("Sign Up Failed! Username may already exist.");
            }
        }
    }//GEN-LAST:event_signup_buttonActionPerformed

    private void startButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_startButtonMouseEntered
        mouseEnteredPet(pet);
    }//GEN-LAST:event_startButtonMouseEntered

    private void startButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_startButtonMouseExited
        mouseExitedPet(pet);
    }//GEN-LAST:event_startButtonMouseExited

    private void okwelcomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okwelcomeActionPerformed
        welcome.setVisible(false);
        duration[0] = Integer.parseInt(welcomepomodoro.getText());
        duration[1] = Integer.parseInt(welcomebreakpomodoro.getText());
        duration[2] = Integer.parseInt(welcomelongbreakpomodoro.getText());
        choosepet.setVisible(true);
    }//GEN-LAST:event_okwelcomeActionPerformed

    private void accountResetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_accountResetButtonActionPerformed
        int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to reset?", "Confirm Exit", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION){
            deletePreference();
            settingsDialog.setVisible(false);
            resetUIPreferences();
        }
    }//GEN-LAST:event_accountResetButtonActionPerformed
    
    void resetUIPreferences(){
        workApps = new String[10];
        funApps = new String[10];

        name = "";
        pet = 0;
        
        // Reset duration settings
        int[] defaultDurations = {25, 5, 15}; // Default durations for pomodoro, break, and long break
        System.arraycopy(defaultDurations, 0, duration, 0, defaultDurations.length);

        // Reset task manager
        String[] defaultTasks = {"Task 1", "Task 2", "Task 3", "Task 4", "Task 5", "Task 6", "Task 7"};
        System.arraycopy(defaultTasks, 0, taskManager, 0, defaultTasks.length);

        // Reset UI elements
        user.setText("");
        settingspomodoroduration.setText("25"); // Reset default pomodoro duration
        settingsbreakduration.setText("5"); // Reset default break duration
        settingslongbreakduration.setText("15"); // Reset default long break duration
    }
   
    void deletePreference() {
        try {
            // Prepare SQL statement
            String updateQuery = "UPDATE Account SET name = ? WHERE user_id = ?";
            PreparedStatement pstmt = connection.prepareStatement(updateQuery);

            // Set parameters
            pstmt.setString(1, null);
            pstmt.setInt(2, userId); // Assuming userId is an int

            // Execute the update statement
            int rowsAffected = pstmt.executeUpdate();

            // Check if any rows were affected by the update
            if (rowsAffected > 0) {
                // Update successful
                System.out.println("Update successful");
            } else {
                // No rows were updated, handle this case appropriately
                System.out.println("No rows updated");
            }

            // Close PreparedStatement
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
                
        try {
            // Delete preferences associated with the user_id
            String deletePrefsQuery = "DELETE FROM Preferences WHERE user_id = ?";
            PreparedStatement deletePrefsStmt = connection.prepareStatement(deletePrefsQuery);
            deletePrefsStmt.setInt(1, userId);

            int prefsRowsAffected = deletePrefsStmt.executeUpdate();

            if (prefsRowsAffected > 0) {
                System.out.println("Preferences deleted successfully");
            } else {
                System.out.println("No preferences deleted");
            }

            deletePrefsStmt.close();

            // Delete notes associated with the user_id
            String deleteNotesQuery = "DELETE FROM Notes WHERE user_id = ?";
            PreparedStatement deleteNotesStmt = connection.prepareStatement(deleteNotesQuery);
            deleteNotesStmt.setInt(1, userId);

            int notesRowsAffected = deleteNotesStmt.executeUpdate();

            if (notesRowsAffected > 0) {
                System.out.println("Notes deleted successfully");
            } else {
                System.out.println("No notes deleted");
            }
            setVisible(false);
            addname.setVisible(true);
            
            deleteNotesStmt.close();
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
    }

    private void backsettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backsettingsActionPerformed
        settingsDialog.setVisible(false);
    }//GEN-LAST:event_backsettingsActionPerformed

    private void settingslongbreakdurationKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_settingslongbreakdurationKeyReleased
        duration[2] = Integer.parseInt(settingslongbreakduration.getText());
        updateDuraction();
    }//GEN-LAST:event_settingslongbreakdurationKeyReleased

    private void settingsbreakdurationKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_settingsbreakdurationKeyReleased
        duration[1] = Integer.parseInt(settingspomodoroduration.getText());
        updateDuraction();

    }//GEN-LAST:event_settingsbreakdurationKeyReleased

    private void settingspomodorodurationKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_settingspomodorodurationKeyReleased
        duration[0] = Integer.parseInt(settingspomodoroduration.getText());
        updateDuraction();

    }//GEN-LAST:event_settingspomodorodurationKeyReleased

    private void chooseterry1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chooseterry1ActionPerformed
        updatePetUI(1);
    }//GEN-LAST:event_chooseterry1ActionPerformed

    private void choosepomi1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_choosepomi1ActionPerformed
        updatePetUI(0);
    }//GEN-LAST:event_choosepomi1ActionPerformed

    private void choosepenny1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_choosepenny1ActionPerformed
        updatePetUI(2);
    }//GEN-LAST:event_choosepenny1ActionPerformed

    private void choosefelix1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_choosefelix1ActionPerformed
        updatePetUI(3);
    }//GEN-LAST:event_choosefelix1ActionPerformed
    
    void updateDuraction(){
        try {
            // Check if the user_id already exists in the Preferences table
            String selectQuery = "SELECT COUNT(*) AS count FROM Preferences WHERE user_id = ?";
            PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
            selectStmt.setInt(1, userId);
            ResultSet resultSet = selectStmt.executeQuery();
            
            // Format the duration array into a string
            StringBuilder durationBuilder = new StringBuilder();
            durationBuilder.append("[");
            for (int i = 0; i < duration.length; i++) {
                durationBuilder.append(duration[i]);
                if (i < duration.length - 1) {
                    durationBuilder.append(",");
                }
            }
            durationBuilder.append("]");
            String durationString = durationBuilder.toString();

            if(resultSet.next() && resultSet.getInt("count") > 0) {
                // User already exists in Preferences table, so update the record
                String updateQuery = "UPDATE Preferences SET duration = ? WHERE user_id = ?";
                PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
                updateStmt.setString(1, durationString);
                updateStmt.setInt(2, userId);

                int rowsAffected = updateStmt.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Update successful");
                } else {
                    System.out.println("No rows updated");
                }

                updateStmt.close();
            } else {
                // User doesn't exist in Preferences table, so insert a new record
                String insertQuery = "INSERT INTO Preferences (user_id, duration) VALUES (?, ?)";
                PreparedStatement insertStmt = connection.prepareStatement(insertQuery);
                insertStmt.setInt(1, userId);
                insertStmt.setString(2, durationString);

                int rowsAffected = insertStmt.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Insert successful");
                } else {
                    System.out.println("No rows inserted");
                }

                insertStmt.close();
            }

            // Close PreparedStatement for select statement
            selectStmt.close();
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
    }
    
    void updatePetUI(int selectedValue){
        pet = selectedValue;
        try {
            // Check if the user_id already exists in the Preferences table
            String selectQuery = "SELECT COUNT(*) AS count FROM Preferences WHERE user_id = ?";
            PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
            selectStmt.setInt(1, userId);
            ResultSet resultSet = selectStmt.executeQuery();
            
            if(resultSet.next() && resultSet.getInt("count") > 0) {
                // User already exists in Preferences table, so update the record
                String updateQuery = "UPDATE Preferences SET pets = ? WHERE user_id = ?";
                PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
                updateStmt.setInt(1, pet);
                updateStmt.setInt(2, userId);

                int rowsAffected = updateStmt.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Update successful");
                } else {
                    System.out.println("No rows updated");
                }

                updateStmt.close();
            } else {
                // User doesn't exist in Preferences table, so insert a new record
                String insertQuery = "INSERT INTO Preferences (user_id, pets) VALUES (?, ?)";
                PreparedStatement insertStmt = connection.prepareStatement(insertQuery);
                insertStmt.setInt(1, userId);
                insertStmt.setInt(2, pet);

                int rowsAffected = insertStmt.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Insert successful");
                } else {
                    System.out.println("No rows inserted");
                }

                insertStmt.close();
            }
            selectPet(pet);
            
            // Close PreparedStatement for select statement
            selectStmt.close();
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PomiGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PomiGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PomiGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PomiGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        FlatLightLaf.setup();
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PomiGUI().setVisible(false);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Jlabel1;
    private javax.swing.JLabel Jlabel11;
    private javax.swing.JLabel Jlabel12;
    private javax.swing.JLabel Jlabel14;
    private javax.swing.JLabel Jlabel15;
    private javax.swing.JLabel Jlabel17;
    private javax.swing.JLabel Jlabel19;
    private javax.swing.JLabel Jlabel20;
    private javax.swing.JLabel Jlabel21;
    private javax.swing.JLabel Jlabel24;
    private javax.swing.JLabel Jlabel25;
    private javax.swing.JLabel Jlabel4;
    private javax.swing.JLabel Jlabel7;
    private javax.swing.JLabel Jlabel9;
    private javax.swing.JFrame access;
    private javax.swing.JButton accountResetButton;
    private javax.swing.JPanel actions;
    private javax.swing.JDialog addname;
    private javax.swing.JPanel appspanel;
    private javax.swing.JButton backsettings;
    private javax.swing.JLabel blank;
    private java.awt.Canvas canvas1;
    private java.awt.Canvas canvas2;
    private java.awt.Canvas canvas3;
    private javax.swing.ButtonGroup chooseapet;
    private javax.swing.JRadioButton choosefelix1;
    private javax.swing.JRadioButton choosepenny;
    private javax.swing.JRadioButton choosepenny1;
    private javax.swing.JDialog choosepet;
    private javax.swing.JButton choosepet_button;
    private javax.swing.JRadioButton choosepomi;
    private javax.swing.JRadioButton choosepomi1;
    private javax.swing.JRadioButton chooseterry;
    private javax.swing.JRadioButton chooseterry1;
    private javax.swing.JPanel clock;
    private javax.swing.JPanel content_panel;
    private javax.swing.JLabel date;
    private javax.swing.JButton exit;
    private javax.swing.JPanel fun_app1;
    private javax.swing.JPanel fun_app10;
    private javax.swing.JPanel fun_app2;
    private javax.swing.JPanel fun_app3;
    private javax.swing.JPanel fun_app4;
    private javax.swing.JPanel fun_app5;
    private javax.swing.JPanel fun_app6;
    private javax.swing.JPanel fun_app7;
    private javax.swing.JPanel fun_app8;
    private javax.swing.JPanel fun_app9;
    private javax.swing.JButton fun_appbutton1;
    private javax.swing.JButton fun_appbutton10;
    private javax.swing.JButton fun_appbutton2;
    private javax.swing.JButton fun_appbutton3;
    private javax.swing.JButton fun_appbutton4;
    private javax.swing.JButton fun_appbutton5;
    private javax.swing.JButton fun_appbutton6;
    private javax.swing.JButton fun_appbutton7;
    private javax.swing.JButton fun_appbutton8;
    private javax.swing.JButton fun_appbutton9;
    private javax.swing.JButton fun_removeapp1;
    private javax.swing.JButton fun_removeapp10;
    private javax.swing.JButton fun_removeapp2;
    private javax.swing.JButton fun_removeapp3;
    private javax.swing.JButton fun_removeapp4;
    private javax.swing.JButton fun_removeapp5;
    private javax.swing.JButton fun_removeapp6;
    private javax.swing.JButton fun_removeapp7;
    private javax.swing.JButton fun_removeapp8;
    private javax.swing.JButton fun_removeapp9;
    private javax.swing.JPanel heading_panel;
    private javax.swing.JLabel instruct_pet;
    private javax.swing.JLabel instruct_pet1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JPanel jPanel31;
    private javax.swing.JPanel jPanel32;
    private javax.swing.JPanel jPanel33;
    private javax.swing.JPanel jPanel34;
    private javax.swing.JPanel jPanel35;
    private javax.swing.JPanel jPanel36;
    private javax.swing.JPanel jPanel37;
    private javax.swing.JPanel jPanel38;
    private javax.swing.JPanel jPanel39;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel40;
    private javax.swing.JPanel jPanel41;
    private javax.swing.JPanel jPanel42;
    private javax.swing.JPanel jPanel43;
    private javax.swing.JPanel jPanel44;
    private javax.swing.JPanel jPanel45;
    private javax.swing.JPanel jPanel46;
    private javax.swing.JPanel jPanel47;
    private javax.swing.JPanel jPanel48;
    private javax.swing.JPanel jPanel49;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel maxtimepomodoro;
    private javax.swing.JLabel namewelcome;
    private javax.swing.JLabel namewelcome1;
    private javax.swing.JLabel namewelcome3;
    private javax.swing.JLabel namewelcome4;
    private javax.swing.JTextArea notes;
    private javax.swing.JButton okname;
    private javax.swing.JButton okwelcome;
    private javax.swing.JButton playbutton;
    private javax.swing.JLabel pomodoroset1;
    private javax.swing.JLabel pomodoroset2;
    private javax.swing.JLabel pomodoroset3;
    private javax.swing.JLabel pomodoroset4;
    private javax.swing.JLabel pomodoroset5;
    private javax.swing.JLabel pomodoroset6;
    private javax.swing.JLabel pomodoroset7;
    private javax.swing.JLabel pomodoroset8;
    private javax.swing.JTextField pomodorotasktextfield1;
    private javax.swing.JTextField pomodorotasktextfield2;
    private javax.swing.JTextField pomodorotasktextfield3;
    private javax.swing.JTextField pomodorotasktextfield4;
    private javax.swing.JTextField pomodorotasktextfield5;
    private javax.swing.JTextField pomodorotasktextfield6;
    private javax.swing.JTextField pomodorotasktextfield7;
    private javax.swing.JTextField pomodorotasktextfield8;
    private javax.swing.JLabel pomodorotasktime1;
    private javax.swing.JLabel pomodorotasktime2;
    private javax.swing.JLabel pomodorotasktime3;
    private javax.swing.JLabel pomodorotasktime4;
    private javax.swing.JLabel pomodorotasktime5;
    private javax.swing.JLabel pomodorotasktime6;
    private javax.swing.JLabel pomodorotasktime7;
    private javax.swing.JLabel pomodorotasktime8;
    private javax.swing.JLabel preview;
    private javax.swing.JProgressBar progressbar;
    private javax.swing.JButton resetpomodoro;
    private javax.swing.JPanel setspomodoro;
    private javax.swing.JButton settings;
    private javax.swing.ButtonGroup settingsChooseaPet;
    private javax.swing.JDialog settingsDialog;
    private javax.swing.JTextField settingsbreakduration;
    private javax.swing.JTextField settingslongbreakduration;
    private javax.swing.JTextField settingspomodoroduration;
    private javax.swing.JTabbedPane signinOrsignup;
    private javax.swing.JButton signin_button;
    private java.awt.Panel signin_panel;
    private javax.swing.JPasswordField signin_password_field;
    private javax.swing.JTextField signin_username_field;
    private javax.swing.JButton signup_button;
    private java.awt.Panel signup_panel;
    private javax.swing.JPasswordField signup_password_field;
    private javax.swing.JTextField signup_username_field;
    private javax.swing.JDialog splashscreen;
    private javax.swing.JButton startButton;
    private javax.swing.JPanel stickynotes;
    private javax.swing.JButton successTimer;
    private javax.swing.JPanel task1;
    private javax.swing.JPanel task2;
    private javax.swing.JPanel task3;
    private javax.swing.JPanel task4;
    private javax.swing.JPanel task5;
    private javax.swing.JPanel task6;
    private javax.swing.JPanel task7;
    private javax.swing.JPanel task8;
    private javax.swing.JLabel taskpomodoro;
    private javax.swing.JPanel taskslist;
    private javax.swing.JLabel time;
    private javax.swing.JPanel time_date_panel;
    private javax.swing.JLabel timeicon;
    private javax.swing.JPanel timer_panel;
    private javax.swing.JLabel timerpomodoro;
    private javax.swing.JButton timerpomodoro1;
    private javax.swing.JTextField user;
    private javax.swing.JDialog welcome;
    private javax.swing.JTextField welcomebreakpomodoro;
    private javax.swing.JTextField welcomelongbreakpomodoro;
    private javax.swing.JTextField welcomepomodoro;
    private javax.swing.JPanel work_app1;
    private javax.swing.JPanel work_app10;
    private javax.swing.JPanel work_app2;
    private javax.swing.JPanel work_app3;
    private javax.swing.JPanel work_app4;
    private javax.swing.JPanel work_app5;
    private javax.swing.JPanel work_app6;
    private javax.swing.JPanel work_app7;
    private javax.swing.JPanel work_app8;
    private javax.swing.JPanel work_app9;
    private javax.swing.JButton work_appbutton1;
    private javax.swing.JButton work_appbutton10;
    private javax.swing.JButton work_appbutton2;
    private javax.swing.JButton work_appbutton3;
    private javax.swing.JButton work_appbutton4;
    private javax.swing.JButton work_appbutton5;
    private javax.swing.JButton work_appbutton6;
    private javax.swing.JButton work_appbutton7;
    private javax.swing.JButton work_appbutton8;
    private javax.swing.JButton work_appbutton9;
    private javax.swing.JButton work_removeapp1;
    private javax.swing.JButton work_removeapp10;
    private javax.swing.JButton work_removeapp2;
    private javax.swing.JButton work_removeapp3;
    private javax.swing.JButton work_removeapp4;
    private javax.swing.JButton work_removeapp5;
    private javax.swing.JButton work_removeapp6;
    private javax.swing.JButton work_removeapp7;
    private javax.swing.JButton work_removeapp8;
    private javax.swing.JButton work_removeapp9;
    private javax.swing.JPanel workspace_panel;
    // End of variables declaration//GEN-END:variables
}
