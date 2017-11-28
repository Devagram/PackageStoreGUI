import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.text.*;
import java.io.*;
import java.util.logging.*;

/**
* Main access point
*/
public class MainApp extends JFrame implements ActionListener, WindowListener{
    
    private static final Logger logger = Logger.getLogger(MainApp.class.getName());
    
    private TextField tfInput;  // Single-line TextField to receive tfInput key
    private TextArea taDisplay; // Multi-line TextArea to taDisplay result
    private JButton buttonShP = new JButton("Show Packages");
    private JButton buttonAdP = new JButton("Add Package");
    private JButton buttonDeP = new JButton("Delete Package");
    private JButton buttonSe = new JButton("Search");
    private JButton buttonDel = new JButton("Deliver Package");
    private JButton buttonShT = new JButton("Show Transactions");
    private JButton buttonShU = new JButton("Show Users");
    private JButton buttonAdU = new JButton("Add User");
    private JButton buttonUpU = new JButton("Update User");
    Container contentPane = getContentPane();
    JPanel buttonPane = new JPanel();
    JPanel textPane = new JPanel();
    ShippingStore ss;
    private Scanner sc; // Used to read from System's standard input
    private int packageType = 0;
    
    /**
    * This method servers as the main interface between the program and the user.
    * The method interacts with the user by printing out a set of options, and
    * asking the user to select one.
    */
   public void runSoftware() {
      buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.PAGE_AXIS));
      textPane.setLayout(new BoxLayout(textPane, BoxLayout.PAGE_AXIS));
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setTitle("Shipping Store");
      
      //button area
      buttonPane.add(new Label("Package Options: "));
      
      buttonPane.add(buttonShP);
      
      buttonPane.add(buttonAdP);
      
      buttonPane.add(buttonDeP);
      
      buttonPane.add(buttonSe);
      
      buttonPane.add(buttonDel);
      
      buttonPane.add(buttonShT);
      
      buttonPane.add(new Label("User Options: "));
      
      buttonPane.add(buttonShU);
      
      buttonPane.add(buttonAdU);
      
      buttonPane.add(buttonUpU);
      buttonPane.setBorder(BorderFactory.createEmptyBorder(20,20,140,20));
      
      //text area
      taDisplay = new TextArea(32, 112); 
      taDisplay.setFont(new Font("Courier New",Font.PLAIN,12));
      textPane.add(taDisplay);
      textPane.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
 
      buttonShP.addActionListener(this);
      buttonAdP.addActionListener(this);
      buttonDeP.addActionListener(this);
      buttonSe.addActionListener(this);
      buttonDel.addActionListener(this);
      buttonShT.addActionListener(this);
      buttonShU.addActionListener(this);
      buttonAdU.addActionListener(this);
      buttonUpU.addActionListener(this);
         // tfInput TextField (source) fires KeyEvent.
         // tfInput adds "this" object as a KeyEvent listener.
 
      setPreferredSize(new Dimension(1100, 600));         // "super" Frame sets initial size
      
      //Put everything together, using the content pane's BorderLayout.
      contentPane.add(buttonPane, BorderLayout.CENTER);
      contentPane.add(textPane, BorderLayout.EAST);
      setResizable(false);
      pack();
      setVisible(true);
      
      redirectSystemStreams();
      ss = ShippingStore.readDatabase();
      sc = new Scanner(System.in);
      
      addWindowListener(new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
              ss.writeDatabase();
          }
});
   }
   
    private void updateTextArea(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                taDisplay.append(text);
            }
        });
   }

   private void redirectSystemStreams() {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
               updateTextArea(String.valueOf((char) b));
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                updateTextArea(new String(b, off, len));
            }

            @Override
            public void write(byte[] b) throws IOException {
                write(b, 0, b.length);
            }
        };

        System.setOut(new PrintStream(out, true));
   }
   
    public void actionPerformed(ActionEvent e) { 
        if(e.getSource() == buttonShP){
           showAllPackages();
           logger.log(Level.INFO, "buttonShP pushed");
        }
        if(e.getSource() == buttonAdP){
           setVisible(false);
           addNewPackage();
           logger.log(Level.INFO, "buttonAdP pushed");
        }
        if(e.getSource() == buttonDeP){
           setVisible(false);
           deletePackage();
           logger.log(Level.INFO, "buttonDeP pushed");
        }
        if(e.getSource() == buttonSe){
           setVisible(false);
           searchPackage();
           logger.log(Level.INFO, "buttonSe pushed");
        }
        if(e.getSource() == buttonDel){
            setVisible(false);
           deliverPackage();
           logger.log(Level.INFO, "buttonDel pushed");
        }
        if(e.getSource() == buttonShT){
           showAllTransactions();
           logger.log(Level.INFO, "buttonShT pushed");
        }
        if(e.getSource() == buttonShU){
           showAllUsers();
           logger.log(Level.INFO, "buttonShU pushed");
        }
        if(e.getSource() == buttonAdU){
           setVisible(false);
           addNewUser();
           logger.log(Level.INFO, "buttonAdU pushed");
        }
        if(e.getSource() == buttonUpU){
           setVisible(false);
           updateUser();
           logger.log(Level.INFO, "buttonUpU pushed");
        }
    }  
   
    /**
     * This method allows the user to enter a new package to the list
     * database.
     * @throws shippingstore.BadInputException bad input
     */
    public void addNewPackage(){
        JFrame popUp = new JFrame();
        JPanel packPane = new JPanel();
        JPanel trackPane = new JPanel();
        JPanel specPane = new JPanel();
        JPanel classPane = new JPanel();
        JPanel varPane = new JPanel();
        JPanel enterPane = new JPanel();
    
        String[] packStrings = { "Envelope", "Box", "Crate", "Drum"};
        String[] specStrings = { "Fragile", "Books", "Catalogs", "Do-not-bend", "N/A"};
        String[] classStrings = { "First-Class", "Priority", "Retail", "Ground", "Metro."};
        String[] matStrings = { "Plastic", "Fiber"};
    
        JComboBox packList = new JComboBox(packStrings);
        JTextField trackField = new JTextField(5);
        JComboBox specList = new JComboBox(specStrings);
        JComboBox classList = new JComboBox(classStrings);
        JTextField varField = new JTextField(7);
        JTextField varField2 = new JTextField(7);
        JComboBox matList = new JComboBox(matStrings);
        JButton enter = new JButton("Add Package");
    
        Label varLabel = new Label("");
        JPanel panel = new JPanel(new FlowLayout());
        
        popUp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE );
        popUp.setTitle("Add a Package");
        JLabel msgLabel = new JLabel(" ");
        
        
        packPane.setLayout(new BoxLayout(packPane, BoxLayout.PAGE_AXIS));
        trackPane.setLayout(new BoxLayout(trackPane, BoxLayout.PAGE_AXIS));
        specPane.setLayout(new BoxLayout(specPane, BoxLayout.PAGE_AXIS));
        classPane.setLayout(new BoxLayout(classPane, BoxLayout.PAGE_AXIS));
        varPane.setLayout(new BoxLayout(varPane, BoxLayout.PAGE_AXIS));
        enterPane.setLayout(new BoxLayout(enterPane, BoxLayout.PAGE_AXIS));
        
        //////////////
        
        packList.setSelectedItem(null);
        packList.addItemListener(new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == packList) {
            JComboBox comboBox = (JComboBox) e.getSource();
            Object selected = comboBox.getSelectedItem();
            
            if("Envelope".equals(selected)){
                logger.log(Level.INFO, "Envelope selected");
                varLabel.setText("Height, width:");
                varPane.remove(matList);
                varPane.add(varLabel);
                varPane.add(varField);
                varPane.add(varField2);
            }
            else if("Box".equals(selected)){
                logger.log(Level.INFO, "Box selected");
                varLabel.setText("Largest dimension, volume: ");
                varPane.remove(matList);
                varPane.add(varLabel);
                varPane.add(varField);
                varPane.add(varField2);
            }
            else if("Crate".equals(selected)){
                logger.log(Level.INFO, "Crate selected");
                varLabel.setText("Max load weight (lb, float), content:");
                varPane.remove(matList);
                varPane.add(varLabel);
                varPane.add(varField);
                varPane.add(varField2);
            }
            else if("Drum".equals(selected)){
                logger.log(Level.INFO, "Drum selected");
                varLabel.setText("Material, diameter:");
                varPane.remove(varField);
                varPane.add(varLabel);
                varPane.add(matList);
                varPane.add(varField2);
                }
            panel.add(varPane);
            enter.setAlignmentX(Component.CENTER_ALIGNMENT) ;
            msgLabel.setAlignmentX(Component.CENTER_ALIGNMENT) ;
            enter.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    boolean valid = true;
                    msgLabel.setText(" ");
                    String track = null; 
                    track = trackField.getText();
                    
                    if (track.length() > 5) {
                            logger.log(Level.WARNING, "Field text length overfilled");
                            msgLabel.setText("Tracking number should not be more that 5 characters long.");
                            panel.revalidate();
                            panel.repaint();
                            valid = false;
                        }
                    if (ss.packageExists(track)) {
                        logger.log(Level.WARNING, "Non existant package");
                            msgLabel.setText("Package with the same tracking number exists, try again");
                            panel.revalidate();
                            panel.repaint();
                            valid = false;
                        }
                    if("Envelope".equals(selected)){
                        int height = 0;
                        String test = varField.getText();
                        if (test.length() > 0){
                            try {// if is number
                                height = Integer.parseInt(varField.getText());
                                if (height < 0) {
                                    logger.log(Level.WARNING, "Negative Height");
                                    msgLabel.setText("Height of Envelope cannot be negative.");
                                    valid = false;
                                }
                            } catch (NumberFormatException exc) {
                                logger.log(Level.WARNING, "Non INT");
                                msgLabel.setText("Height must be an int!");
                                valid = false;
                            }
                        }else{
                            logger.log(Level.WARNING, "Fields empty");
                            msgLabel.setText("Requires all fields.");
                            valid = false;
                        }
                        int width = 0;
                        String test2 = varField2.getText();
                        if (test2.length() > 0){
                            try {// if is number
                                height = Integer.parseInt(varField2.getText());
                                if (width < 0) {
                                    logger.log(Level.WARNING, "negative int");
                                    msgLabel.setText("Width of Envelope cannot be negative.");
                                    valid = false;
                                }
                            } catch (NumberFormatException exc) {
                                logger.log(Level.WARNING, "non int");
                                msgLabel.setText("Width must be an int!");
                                valid = false;
                            } 
                        }else{
                            logger.log(Level.WARNING, "Field empty");
                            msgLabel.setText("Requires all fields.");
                            valid = false;
                        }
                        if(valid == true){
                            logger.log(Level.INFO, "added to package database");
                            msgLabel.setText("Success, added to database.");
                            ss.addEnvelope(track, specList.getSelectedItem().toString(), classList.getSelectedItem().toString(), height, width);
                        }
                    }
                    else if("Box".equals(selected)){
                        int dimension = 0;
                        String test = varField.getText();
                        if (test.length() > 0){
                            try {// if is number
                                dimension = Integer.parseInt(varField.getText());
                                if (dimension < 0) {
                                    msgLabel.setText("Largest dimension of Box cannot be negative.");
                                    logger.log(Level.WARNING, "Field negative");
                                    valid = false;
                                }
                            } catch (NumberFormatException exc) {
                                msgLabel.setText("Largest dimension must be an int!");
                                logger.log(Level.WARNING, "non int");
                                valid = false;
                            }
                        }else{
                            msgLabel.setText("Requires all fields.");
                            logger.log(Level.WARNING, "Missing fields");
                            valid = false;
                        }
                        int volume = 0;
                        String test2 = varField2.getText();
                        if (test2.length() > 0){
                            try {// if is number
                                volume = Integer.parseInt(varField2.getText());
                                if (volume < 0) {
                                    msgLabel.setText("Volume of Box cannot be negative.");
                                    logger.log(Level.WARNING, "negative float");
                                    valid = false;
                                }
                            } catch (NumberFormatException exc) {
                                msgLabel.setText("Volume must be an int!");
                                logger.log(Level.WARNING, "Non int");
                                valid = false;
                            } 
                        }else{
                            msgLabel.setText("Requires all fields.");
                            logger.log(Level.WARNING, "empty fields");
                            valid = false;
                        }
                        if(valid == true){
                            msgLabel.setText("Success, added to database.");
                            logger.log(Level.INFO, "Added to database");
                            ss.addBox(track, specList.getSelectedItem().toString(), classList.getSelectedItem().toString(), dimension, volume);
                        }
                    }
                    else if("Crate".equals(selected)){
                        float weight = 0.0f;
                        String test = varField.getText();
                        if (test.length() > 0){
                            try {// if is number
                                weight = Float.parseFloat(varField.getText());
                                if (weight < 0) {
                                    msgLabel.setText("Maximum load weight of Crate cannot be negative.");
                                    logger.log(Level.WARNING, "negative flaot");
                                    valid = false;
                                }
                            } catch (NumberFormatException exc) {
                                msgLabel.setText("Maximum Load weight must be a float!");
                                logger.log(Level.WARNING, "Non float");
                                valid = false;
                            }
                        }else{
                            msgLabel.setText("Requires all fields.");
                            logger.log(Level.WARNING, "empty fields");
                            valid = false;
                        }
                        
                        String content = varField2.getText();
                        if (content.length() == 0){
                            msgLabel.setText("Requires all fields.");
                            logger.log(Level.WARNING, "empty fields");
                            valid = false;
                        }
                        if(valid == true){
                            msgLabel.setText("Success, added to database.");
                            logger.log(Level.WARNING, "user Added package to databse");
                            ss.addCrate(track, specList.getSelectedItem().toString(), classList.getSelectedItem().toString(), weight, content);
                        }
                    }
                    else if("Drum".equals(selected)){
                        
                        float diameter = 0.0f;
                        String test2 = varField2.getText();
                        if (test2.length() > 0){
                            try {// if is number
                                diameter = Float.parseFloat(varField2.getText());
                                if (diameter < 0) {
                                    msgLabel.setText("Diameter of Drum cannot be negative.");
                                    logger.log(Level.WARNING, "negative float");
                                    valid = false;
                                }
                            } catch (NumberFormatException exc) {
                                msgLabel.setText("Diameter must be a float!");
                                logger.log(Level.WARNING, "Non float");
                                valid = false;
                            } 
                        }else{
                            msgLabel.setText("Requires all fields.");
                            logger.log(Level.WARNING, "empty fields");
                            valid = false;
                        }
                        if(valid == true){
                            msgLabel.setText("Success, added to database.");
                            logger.log(Level.INFO, "user added package to database");
                            ss.addDrum(track, specList.getSelectedItem().toString(), classList.getSelectedItem().toString(), matList.getSelectedItem().toString(),diameter);
                        }
                    }
                }
            });
            enterPane.add(enter);
            panel.revalidate();
            panel.repaint();
            popUp.pack();
        }}});
        
        packPane.add(new Label("Package Type: "));
        packPane.add(packList);

        //////////////
        
        trackField.addActionListener(this);
        
        trackPane.add(new Label("Tracking Number: "));
        trackPane.add(trackField);
        
        //////////////
        
        specList.setSelectedIndex(0);
        
        specPane.add(new Label("Specification: "));
        specPane.add(specList);
        
        //////////////
        
        classList.setSelectedIndex(0);
        
        classPane.add(new Label("Mail Class: "));
        classPane.add(classList);
        
        ///////////////
        
        panel.add(packPane);
        panel.add(trackPane);
        panel.add(specPane);
        panel.add(classPane);
        
        enterPane.add(panel);
        enterPane.add(msgLabel);
        
        popUp.add(enterPane);
        popUp.setPreferredSize(new Dimension(750, 150));
        popUp.setResizable(false);
        popUp.pack();
        popUp.addWindowListener(this);
        
        popUp.setVisible(true);
        
    }
   

    
    /**
     * This method prints out all the package currently in the inventory, in a
     * formatted manner.
     */
    public void showAllPackages() {
        System.out.println(ss.getAllPackagesFormatted());
    }
    
    /**
     * This method allows the user to delete a package from the inventory
     * database.
     */
    public void deletePackage() {
        JFrame popUp = new JFrame();
        JPanel packPane = new JPanel();
        JTextField trackField = new JTextField("", 5);
        trackField.setSize(new Dimension(5, 1));
        JLabel msgLabel = new JLabel(" ");
        JButton delButton = new JButton("Delete");
        delButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    msgLabel.setText(" ");
                    packPane.revalidate();
                    packPane.repaint();
                    String track = null; 
                    track = trackField.getText();
                    
                    if (track.length() > 5) {
                        msgLabel.setText("Tracking number should not be no more that 5 characters long.");
                        logger.log(Level.WARNING, "> 5 characters");
                        packPane.revalidate();
                        packPane.repaint();
                    }else{
                        if (ss.deletePackage(track)) {
                            msgLabel.setText("Package found and deleted");
                            logger.log(Level.INFO, "Package deleted by user");
                            packPane.revalidate();
                            packPane.repaint();
                        }else {
                            msgLabel.setText("Package with given tracking number not found in the database.");
                            logger.log(Level.WARNING, "package not found");
                            packPane.revalidate();
                            packPane.repaint();
                        }
                    }
                }
            });
        popUp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE );
        popUp.setTitle("Delete a Package");
        
        packPane.setLayout(new BoxLayout(packPane, BoxLayout.PAGE_AXIS));
        
        packPane.add(new Label("Tracking Number: "));
        packPane.add(trackField);
        delButton.setAlignmentX(Component.CENTER_ALIGNMENT) ;
        msgLabel.setAlignmentX(Component.CENTER_ALIGNMENT) ;
        packPane.add(msgLabel);
        packPane.add(delButton);
        
        popUp.add(packPane);
        popUp.setPreferredSize(new Dimension(400, 115));
        popUp.setResizable(false);
        popUp.addWindowListener(this);
        popUp.pack();
        popUp.setVisible(true);
        //ss.deletePackage(ptn)
        
    }
    
    /**
     * This method allows the users to search for a package given its tracking number
     * and then it prints details about the package.
     */
    public void searchPackage() {
        JFrame popUp = new JFrame();
        JPanel packPane = new JPanel();
        JTextField trackField = new JTextField(5);
        JButton serButton = new JButton("Search");
        JLabel msgLabel = new JLabel(" ");
        
        serButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    msgLabel.setText(" ");
                    packPane.revalidate();
                    packPane.repaint();
                    String track = null; 
                    track = trackField.getText();
                    
                    if (track.length() > 5) {
                        msgLabel.setText("Tracking number should not be no more that 5 characters long.");
                        logger.log(Level.WARNING, "> 5 chars");
                        packPane.revalidate();
                        packPane.repaint();
                    }else{
                        if (ss.packageExists(track)) {
                            msgLabel.setText("Package found and printed to console.");
                            
                            logger.log(Level.INFO, "user found package in search");
                            System.out.println("******---------------------******----------------FOUND PACKAGE----------------******---------------------******");
                            System.out.println(ss.getPackageFormatted(track));
                            System.out.println("******---------------------******--------------^^FOUND PACKAGE^^--------------******---------------------******");
                            packPane.revalidate();
                            packPane.repaint();
                        }else {
                            msgLabel.setText("Package with PTN " + track + " not found in the database");
                            logger.log(Level.WARNING, "Package not found");
                            packPane.revalidate();
                            packPane.repaint();
                        }
                    }
                }
            });
        
        popUp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE );
        popUp.setTitle("Search for a Package");
        
        packPane.setLayout(new BoxLayout(packPane, BoxLayout.PAGE_AXIS));
        
        

        
        packPane.add(new Label("Tracking Number: "));
        packPane.add(trackField);
        serButton.setAlignmentX(Component.CENTER_ALIGNMENT) ;
        msgLabel.setAlignmentX(Component.CENTER_ALIGNMENT) ;
        packPane.add(msgLabel);
        packPane.add(serButton);
        
        popUp.add(packPane);
        popUp.setPreferredSize(new Dimension(400, 115));
        popUp.setResizable(false);
        popUp.addWindowListener(this);
        popUp.pack();
        popUp.setVisible(true);
    }
    
     /**
     * Prints out a list of all users in the database.
     */
    public void showAllUsers() {
        System.out.println(ss.getAllUsersFormatted());
    }
    
    /**
     * This method allows a new user to be added to the database.
     *
     */
    public void addNewUser() {
        JFrame popUp = new JFrame();
        JPanel typePane = new JPanel();
        JPanel lNamePane = new JPanel();
        JPanel fNamePane = new JPanel();
        JPanel idPane = new JPanel();
        JPanel varPane = new JPanel();
        JPanel enterPane = new JPanel();
    
        String[] typeStrings = { "Customer", "Employee" };

    
        JComboBox typeList = new JComboBox(typeStrings);
        JTextField lNameField = new JTextField(5);
        JTextField fNameField = new JTextField(5);
        JTextField idField = new JTextField(5);

        JTextField varField = new JTextField(7);
        JTextField varField2 = new JTextField(7);
        JTextField varField3 = new JTextField(7);

        JButton enter = new JButton("Add User");
    
        Label varLabel = new Label("");
        JLabel msgLabel = new JLabel(" ");
        JPanel panel = new JPanel(new FlowLayout());
        
        popUp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE );
        popUp.setTitle("Add a User");

        typePane.setLayout(new BoxLayout(typePane, BoxLayout.PAGE_AXIS));
        lNamePane.setLayout(new BoxLayout(lNamePane, BoxLayout.PAGE_AXIS));
        fNamePane.setLayout(new BoxLayout(fNamePane, BoxLayout.PAGE_AXIS));
        idPane.setLayout(new BoxLayout(idPane, BoxLayout.PAGE_AXIS));
        varPane.setLayout(new BoxLayout(varPane, BoxLayout.PAGE_AXIS));
        enterPane.setLayout(new BoxLayout(enterPane, BoxLayout.PAGE_AXIS));
        
        //////////////
        
        typeList.setSelectedItem(null);
        typeList.addItemListener(new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == typeList) {
            JComboBox comboBox = (JComboBox) e.getSource();
            Object selected = comboBox.getSelectedItem();
            
            if("Employee".equals(selected)){
                logger.log(Level.INFO, "employee selected");
                varLabel.setText("Salary, SSN, Bank Acc#: ");
                varPane.add(varLabel);
                varPane.add(varField);
                varPane.add(varField2);
                varPane.add(varField3);
            }
            else if("Customer".equals(selected)){
                logger.log(Level.INFO, "Customer selected");
                varPane.remove(varField3);
                varLabel.setText("Phone number, address: ");
                varPane.add(varLabel);
                varPane.add(varField);
                varPane.add(varField2);
            }
            panel.add(varPane);
            enter.setAlignmentX(Component.CENTER_ALIGNMENT) ;
            enter.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    boolean valid = true;
                    msgLabel.setText(" ");
                    String firstName = null;
                    String lastName = null;
                    String address = null;
                    String phoneNumber = null;
                    float monthlySalary = 0.0f;
                    int ssn = 0;
                    if(fNameField.getText().length() > 0){
                        firstName = fNameField.getText();
                    }else{
                        valid = false;
                        msgLabel.setText("Requires all fields.");
                        logger.log(Level.WARNING, "missing fields");
                    }

                    if(lNameField.getText().length() > 0){
                        lastName = lNameField.getText();
                    }else{
                        valid = false;
                        msgLabel.setText("Requires all fields.");
                        logger.log(Level.WARNING, "missing fields");
                    }
                    
                    if("Customer".equals(selected)){
                        if(varField2.getText().length() > 0){
                            address = varField2.getText();
                        }else{
                            valid = false;
                            msgLabel.setText("Requires all fields.");
                            logger.log(Level.WARNING, "missing fields");
                        }

                        if(varField.getText().length() > 0){
                            phoneNumber = varField.getText();
                        }else{
                            valid = false;
                            msgLabel.setText("Requires all fields.");
                            logger.log(Level.WARNING, "missing fields");
                        }
                        if(valid == true){
                            msgLabel.setText("Success, added to database.");
                            logger.log(Level.INFO, "Added to database");
                            ss.addCustomer(firstName, lastName, phoneNumber, address);
                        }
                    }else {
                        if(varField.getText().length() > 0){
                            try{
                                monthlySalary = Float.parseFloat(varField.getText());
                                if (monthlySalary < 0.0f) {
                                    valid = false;
                                    msgLabel.setText("Monthly salary cannot be negative.");
                                    logger.log(Level.WARNING, "Negative");
                                } 
                            }catch(NumberFormatException exc) {
                                msgLabel.setText("Monthly Salary must be a float!");
                                logger.log(Level.WARNING, "non float");
                                valid = false;
                            }
                        }else{
                            valid = false;
                            msgLabel.setText("Requires all fields.");
                            logger.log(Level.WARNING, "missing fields");
                        }

                        if(varField2.getText().length() != 9){
                            valid = false;
                            msgLabel.setText("That is not a nine digit number for ssn.");
                            logger.log(Level.WARNING, "< 9 digit SSN");
                        } else if(Integer.parseInt(varField2.getText()) < 10000000 || Integer.parseInt(varField2.getText()) > 999999999){
                            msgLabel.setText("Give a correct 9 digit integer for ssn.");
                            logger.log(Level.WARNING, "invalid SSN");
                        }
                        if(varField2.getText().length() > 0){
                            ssn = Integer.parseInt(varField.getText());
                        }else{
                            valid = false;
                            msgLabel.setText("Requires all fields.");
                            logger.log(Level.WARNING, "missing fields");
                        }
                        
                        int bankAccNumber = 0;
                        
                        if(varField3.getText().length() > 0){
                            try{
                                bankAccNumber = Integer.parseInt(varField3.getText());
                                if (monthlySalary < 0.0f) {
                                    valid = false;
                                    msgLabel.setText("Bank Account Number cannot be negative.");
                                    logger.log(Level.WARNING, "negative");
                                } 
                            }catch(NumberFormatException exc) {
                                msgLabel.setText("Bank Account Number must be an int.");
                                logger.log(Level.WARNING, "Non int");
                                valid = false;
                            }
                        }else{
                            valid = false;
                            msgLabel.setText("Requires all fields.");
                            logger.log(Level.WARNING, "Missing fields");
                        }
                        
                        if(valid == true){
                            msgLabel.setText("Success, added to database.");
                            logger.log(Level.INFO, "Added to database");
                            ss.addEmployee(firstName, lastName, ssn, monthlySalary, bankAccNumber);
                        }
                    }
                }
            });
            enterPane.add(enter);
            panel.revalidate();
            panel.repaint();
            popUp.pack();
        }}});
        
        typePane.add(new Label("User Type: "));
        typePane.add(typeList);
        
        
        //////////////
        
        fNameField.addActionListener(this);
        
        fNamePane.add(new Label("First Name: "));
        fNamePane.add(fNameField);
        
        //////////////
        
        lNameField.addActionListener(this);
        
        lNamePane.add(new Label("Last Name: "));
        lNamePane.add(lNameField);
        
        //////////////        
        
        idField.addActionListener(this);
        
        idPane.add(new Label("User ID: "));
        idPane.add(idField);
        
        //////////////
        panel.add(typePane);
        panel.add(fNamePane);
        panel.add(lNamePane);
        
        
        enterPane.add(panel);
        msgLabel.setAlignmentX(Component.CENTER_ALIGNMENT) ;
        enterPane.add(msgLabel);
        
        popUp.add(enterPane);
        popUp.setPreferredSize(new Dimension(750, 175));
        popUp.setResizable(false);
        popUp.pack();
        popUp.addWindowListener(this);
        
        popUp.setVisible(true);
        
        //    ss.addCustomer(firstName, lastName, phoneNumber, address);

            //ss.addEmployee(firstName, lastName, ssn, monthlySalary, bankAccNumber);
        

    }
    
    /**
     * This method can be used to update a user's information, given their user
     * ID.
     *
     * @throws shippingstore.BadInputException
     */
    public void updateUser(){
        JFrame popUp = new JFrame();
        JPanel packPane = new JPanel();
        JPanel typePane = new JPanel();
        JPanel lNamePane = new JPanel();
        JPanel fNamePane = new JPanel();
        JPanel idPane = new JPanel();
        JPanel varPane = new JPanel();
        JPanel enterPane = new JPanel();
        
        JTextField lNameField = new JTextField(5);
        JTextField fNameField = new JTextField(5);
        JTextField idField = new JTextField(5);

        JTextField varField = new JTextField(7);
        JTextField varField2 = new JTextField(7);
        JTextField varField3 = new JTextField(7);

        JButton enter = new JButton("Update User");
        Label idLabel = new Label("ID Number: ");
        Label varLabel = new Label("");
        
        JPanel panel = new JPanel(new FlowLayout());
        
        JTextField trackField = new JTextField(5);
        JButton serButton = new JButton("Search");
        JLabel msgLabel = new JLabel(" ");
        
        popUp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE );
        popUp.setTitle("Search for a User to modify");
        
        packPane.setLayout(new BoxLayout(packPane, BoxLayout.PAGE_AXIS));
        typePane.setLayout(new BoxLayout(typePane, BoxLayout.PAGE_AXIS));
        lNamePane.setLayout(new BoxLayout(lNamePane, BoxLayout.PAGE_AXIS));
        fNamePane.setLayout(new BoxLayout(fNamePane, BoxLayout.PAGE_AXIS));
        idPane.setLayout(new BoxLayout(idPane, BoxLayout.PAGE_AXIS));
        varPane.setLayout(new BoxLayout(varPane, BoxLayout.PAGE_AXIS));
        enterPane.setLayout(new BoxLayout(enterPane, BoxLayout.PAGE_AXIS));
        
        
        serButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                msgLabel.setText(" ");
                packPane.revalidate();
                packPane.repaint();
                String track = null; 
                track = trackField.getText();
                    
                    
                if (ss.userExists(Integer.parseInt(track))) {
                    
                    Label idFin = new Label (trackField.getText());
                    packPane.remove(idLabel);
                    packPane.remove(trackField);
        
                    fNamePane.add(new Label("First Name: "));
                    fNamePane.add(fNameField);
        
                    //////////////

                    lNamePane.add(new Label("Last Name: "));
                    lNamePane.add(lNameField);
                    
                    //////////////
                    idPane.add(new Label("User ID: "));
                    idPane.add(new Label(track));
                    
                    panel.add(typePane);
                    panel.add(fNamePane);
                    panel.add(lNamePane);
                    panel.add(idPane);
        
                    enterPane.add(panel);
                    enter.setAlignmentX(Component.CENTER_ALIGNMENT) ;
                    msgLabel.setAlignmentX(Component.CENTER_ALIGNMENT) ;
                    enterPane.add(msgLabel);
                    enter.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    boolean valid = true;
                    msgLabel.setText(" ");
                    String firstName = null;
                    String lastName = null;
                    String address = null;
                    String phoneNumber = null;
                    String track2 = null; 
                    track2 = trackField.getText();
                    float monthlySalary = 0.0f;
                    int ssn = 0;
                    if(fNameField.getText().length() > 0){
                        firstName = fNameField.getText();
                    }else{
                        valid = false;
                        msgLabel.setText("Requires all fields.");
                        logger.log(Level.WARNING, "missing fields");
                    }

                    if(lNameField.getText().length() > 0){
                        lastName = lNameField.getText();
                    }else{
                        valid = false;
                        msgLabel.setText("Requires all fields.");
                        logger.log(Level.WARNING, "missing fields");
                    }
                    
                    if(ss.isCustomer(Integer.parseInt(track2.toString()))){
                        if(varField2.getText().length() > 0){
                            address = varField2.getText();
                        }else{
                            valid = false;
                            msgLabel.setText("Requires all fields.");
                            logger.log(Level.WARNING, "missing fields");
                        }

                        if(varField.getText().length() > 0){
                            phoneNumber = varField.getText();
                        }else{
                            valid = false;
                            msgLabel.setText("Requires all fields.");
                            logger.log(Level.WARNING, "missing fields");
                        }
                        if(valid == true){
                            msgLabel.setText("Success, updated in database.");
                            logger.log(Level.INFO, "Updated user in database");
                            ss.updateCustomer(Integer.parseInt(track2.toString()), firstName, lastName, phoneNumber, address);
                        }
                    }else {
                        if(varField.getText().length() > 0){
                            try{
                                monthlySalary = Float.parseFloat(varField.getText());
                                if (monthlySalary < 0.0f) {
                                    valid = false;
                                    msgLabel.setText("Monthly salary cannot be negative.");
                                    logger.log(Level.WARNING, "negative");
                                } 
                            }catch(NumberFormatException exc) {
                                msgLabel.setText("Monthly Salary must be a float!");
                                logger.log(Level.WARNING, "non float");
                                valid = false;
                            }
                        }else{
                            valid = false;
                            msgLabel.setText("Requires all fields.");
                            logger.log(Level.WARNING, "missing fields");
                        }

                        if(varField2.getText().length() != 9){
                            valid = false;
                            msgLabel.setText("That is not a nine digit number for ssn.");
                            logger.log(Level.WARNING, "< 9 digit ssn");
                        } else if(Integer.parseInt(varField2.getText()) < 10000000 || Integer.parseInt(varField2.getText()) > 999999999){
                            msgLabel.setText("Give a correct 9 digit integer for ssn.");
                            logger.log(Level.WARNING, "Incorrect SSN");
                        }
                        if(varField2.getText().length() > 0){
                            ssn = Integer.parseInt(varField.getText());
                        }else{
                            valid = false;
                            msgLabel.setText("Requires all fields.");
                            logger.log(Level.WARNING, "missing fields");
                        }
                        
                        int bankAccNumber = 0;
                        
                        if(varField3.getText().length() > 0){
                            try{
                                bankAccNumber = Integer.parseInt(varField3.getText());
                                if (monthlySalary < 0.0f) {
                                    valid = false;
                                    msgLabel.setText("Bank Account Number cannot be negative.");
                                    logger.log(Level.WARNING, "negative");
                                } 
                            }catch(NumberFormatException exc) {
                                msgLabel.setText("Bank Account Number must be an int.");
                                logger.log(Level.WARNING, "non int");
                                valid = false;
                            }
                        }else{
                            valid = false;
                            msgLabel.setText("Requires all fields.");
                            logger.log(Level.WARNING, "missing fields");
                        }
                        
                        if(valid == true){
                            msgLabel.setText("Success, updated in database.");
                            logger.log(Level.INFO, "Updated user in database");
                            ss.updateEmployee(Integer.parseInt(track2.toString()),firstName, lastName, ssn, monthlySalary, bankAccNumber);
                        }
                    }
                }
            });
                    enterPane.add(enter);
                    popUp.remove(packPane);
                    popUp.add(enterPane);
                    
                    
                    if(ss.isCustomer(Integer.parseInt(track))){
                        typePane.add(new Label("User Type: "));
                        typePane.add(new Label("Customer"));
                        varLabel.setText("Phone number, address:");
                        varPane.add(varLabel);
                        varPane.add(varField);
                        varPane.add(varField2);
                        panel.add(varPane);
                        panel.revalidate();
                        panel.repaint();
                        popUp.setPreferredSize(new Dimension(700, 155));
                        popUp.pack();
                    }
                    else{
                        typePane.add(new Label("User Type: "));
                        typePane.add(new Label("Employee"));
                        varLabel.setText("Salary, SSN, Bank Acc#: ");
                        varPane.add(varLabel);
                        varPane.add(varField);
                        varPane.add(varField2);
                        varPane.add(varField3);
                        panel.add(varPane);
                        panel.revalidate();
                        panel.repaint();
                        popUp.setPreferredSize(new Dimension(700, 155));
                        popUp.pack();
                    }
                            
                }else {
                            msgLabel.setText("User with ID " + track + " not found in the database");
                            logger.log(Level.WARNING, "Not found");
                            packPane.revalidate();
                            packPane.repaint();
                }  
            }
        });
        
        packPane.add(idLabel);
        packPane.add(trackField);
        serButton.setAlignmentX(Component.CENTER_ALIGNMENT) ;
        msgLabel.setAlignmentX(Component.CENTER_ALIGNMENT) ;
        packPane.add(msgLabel);
        packPane.add(serButton);
        
        popUp.add(packPane);
        popUp.setPreferredSize(new Dimension(400, 115));
        popUp.setResizable(false);
        popUp.addWindowListener(this);
        popUp.pack();
        popUp.setVisible(true);
    }
    
    /**
     * This method is used to complete a package shipping/delivery transaction.
     *
     * @throws shippingstore.BadInputException
     */
    public void deliverPackage() {

        Date currentDate = new Date(System.currentTimeMillis());
        
        JFrame popUp = new JFrame();
        JPanel cusPane = new JPanel();
        JPanel empPane = new JPanel();
        JPanel trackPane = new JPanel();
        JPanel pricePane = new JPanel();
        JPanel enterPane = new JPanel();
        JLabel msgLabel = new JLabel("");
        JPanel panel = new JPanel(new FlowLayout());
    

        JTextField cusField = new JTextField(5);
        JTextField empField = new JTextField(5);
        JTextField trackField = new JTextField(5);
        JTextField priceField = new JTextField(5);
        JButton enter = new JButton("Deliver Package");
        
        //ss.addShppingTransaction(customerId, employeeId, ptn, currentDate, currentDate, price);
        //ss.deletePackage(ptn);
        enter.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    msgLabel.setText(" ");
                    panel.revalidate();
                    panel.repaint();
                    String track = null; 
                    track = trackField.getText();
                    boolean valid = true;
                    boolean customerExists = ss.userExists(Integer.parseInt(cusField.getText().toString()));
                    boolean employeeExists = ss.userExists(Integer.parseInt(empField.getText().toString()));
                    
                    if (!customerExists) {
                        msgLabel.setText("Customer ID you have entered does not exist in the database.");
                        logger.log(Level.WARNING, "Not found");
                        valid = false;
                    }
                    
                    if (!employeeExists) {
                        msgLabel.setText("Employee ID you have entered does not exist in the database.");
                        logger.log(Level.WARNING, "Not found");
                        valid = false;
                    }
                    
                    if (!ss.packageExists(track)) {
                        msgLabel.setText("Tracking num does not exist in the database. Aborting transaction.");
                        logger.log(Level.WARNING, "Not found");
                        valid = false;
                    }
                    
                    float price = Float.parseFloat(priceField.getText());
                    if (price < 0.0f) {
                        msgLabel.setText("Price cannot be negative.");
                        valid = false;
                    }
                    
                    if (valid == true) {
                        ss.addShppingTransaction(Integer.parseInt(cusField.getText().toString()), Integer.parseInt(empField.getText().toString()), track, currentDate, currentDate, price);
                        ss.deletePackage(track);

                        msgLabel.setText("\nTransaction Completed!");
                        logger.log(Level.WARNING, "Transaction completed");
                    
                    }
                    panel.revalidate();
                    panel.repaint();
                }
            });
   
        
        
        popUp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE );
        popUp.setTitle("Add a Package");
        
        
        cusPane.setLayout(new BoxLayout(cusPane, BoxLayout.PAGE_AXIS));
        empPane.setLayout(new BoxLayout(empPane, BoxLayout.PAGE_AXIS));
        trackPane.setLayout(new BoxLayout(trackPane, BoxLayout.PAGE_AXIS));
        pricePane.setLayout(new BoxLayout(pricePane, BoxLayout.PAGE_AXIS));
        enterPane.setLayout(new BoxLayout(enterPane, BoxLayout.PAGE_AXIS));
        
        //////////////

        
        cusPane.add(new Label("Customer ID (int): "));
        cusPane.add(cusField);       
        
        //////////////
        

        
        empPane.add(new Label("Employee ID (int): "));
        empPane.add(empField);
        
        //////////////
        
        //////////////

        
        trackPane.add(new Label("Tracking Number: "));
        trackPane.add(trackField);
        
        ///////////////
        

        pricePane.add(new Label("Price: "));
        pricePane.add(priceField);
        
        ///////////////
        
        panel.add(cusPane);
        panel.add(empPane);
        panel.add(trackPane);
        panel.add(pricePane);
        
        enterPane.add(panel);
        enter.setAlignmentX(Component.CENTER_ALIGNMENT) ;
        msgLabel.setAlignmentX(Component.CENTER_ALIGNMENT) ;
        enterPane.add(msgLabel);
        enterPane.add(enter);
        
        
        popUp.add(enterPane);
        popUp.setPreferredSize(new Dimension(750, 150));
        popUp.setResizable(false);
        popUp.pack();
        popUp.addWindowListener(this);
        popUp.setVisible(true);
    }
    
    
    /**
     * Prints out a list of all recorded transactions.
     */
    public void showAllTransactions() {
        System.out.println(ss.getAllTransactionsText());
    }

    public void windowClosing(WindowEvent e){ setVisible(true);}
    public void windowOpened(WindowEvent e) {   }
    public void windowClosed(WindowEvent e) {   }
    public void windowActivated(WindowEvent e) {   }
    public void windowDeactivated(WindowEvent e) {   }
    public void windowIconified(WindowEvent e) {   }
    public void windowDeiconified(WindowEvent e) {   }
    
    /**
     * The main method of the program.
     *
     * @param args the command line arguments
     */
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        MainApp app = new MainApp();
        app.runSoftware();
    }
}
