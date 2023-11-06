package viewer;

import org.sqlite.SQLiteDataSource;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class SQLiteViewer extends JFrame {
    private JButton openFileBtn, executeBtn;
    private JTextField dbFileNameTxt;
    private JComboBox<String> tableSelect;
    private JTextArea queryTxt;
    private SQLiteDataSource dataSource = new SQLiteDataSource();
    private JTable queryResults;

    public SQLiteViewer() {
        super("SQLite Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 900);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(null);

        JLabel dbLabel = new JLabel("Database:");
        dbLabel.setBounds(15, 15, 75, 30);

        dbFileNameTxt = new JTextField();
        dbFileNameTxt.setName("FileNameTextField");
        dbFileNameTxt.setBounds(85, 15, 500, 30);

        openFileBtn = new JButton("Open");
        openFileBtn.setName("OpenFileButton");
        openFileBtn.setBounds(600, 15, 75, 30);
        openFileBtn.addActionListener(e -> openDb());

        JLabel tableLabel = new JLabel("Table:");
        tableLabel.setBounds(15, 60, 75, 30);

        tableSelect = new JComboBox<>();
        tableSelect.setName("TablesComboBox");
        tableSelect.setBounds(85, 60, 590, 30);
        tableSelect.addActionListener(e -> createQuery((String) tableSelect.getSelectedItem()));

        JLabel queryLabel = new JLabel("Query:");
        queryLabel.setBounds(15, 105, 75, 30);

        queryTxt = new JTextArea();
        queryTxt.setName("QueryTextArea");
        queryTxt.setBounds(85, 105, 590, 90);

        executeBtn = new JButton("Execute");
        executeBtn.setName("ExecuteQueryButton");
        executeBtn.setBounds(575, 210, 100, 30);
        executeBtn.addActionListener(e -> executeQuery());

        queryResults = new JTable();
        queryResults.setName("Table");
        queryResults.setBounds(15, 255, 660, 600);

        JScrollPane scrollPane = new JScrollPane(queryResults);

        add(scrollPane);
        add(dbLabel);
        add(dbFileNameTxt);
        add(openFileBtn);
        add(tableLabel);
        add(tableSelect);
        add(queryLabel);
        add(queryTxt);
        add(executeBtn);
        add(queryResults);

        //tableSelect.setEnabled(false);
        queryTxt.setEnabled(false);
        executeBtn.setEnabled(false);

        setVisible(true);
    }

    private void executeQuery() {
        try (Connection conn = dataSource.getConnection()) {
            if (conn.isValid(5)) {
                //System.out.println("Connection successful.");
                Statement statement = conn.createStatement();
                String query = queryTxt.getText();
                statement.execute(query);

                ResultSet rs = statement.getResultSet();
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();

                String[] columns = new String[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    columns[i] = rsmd.getColumnName(i + 1);
                    //System.out.println(columns[i]);
                }

                Statement s2 = conn.createStatement();
                ResultSet r = s2.executeQuery("SELECT COUNT(*) AS recordCount FROM " +
                        tableSelect.getSelectedItem());
                r.next();
                int rowCount = r.getInt("recordCount");
                r.close();

                Object[][] data = new String[rowCount][columnCount];

                int i = 0;
                while (rs.next()) {
                    for (int j = 0; j < columnCount; j++) {
                        //System.out.println("Column: " + (j + 1));
                        data[i][j] = rs.getString(j + 1);
                        //System.out.println(data[i][j]);
                    }
                    i++;
                }

                DefaultTableModel model = new DefaultTableModel(data, columns);
                queryResults.setModel(model);

                queryResults.setShowGrid(true);
                queryResults.setShowVerticalLines(true);
                statement.close();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(new Frame(), e.getMessage());
            e.printStackTrace();
        }

    }

    private void createQuery(String selectedItem) {
        queryTxt.setText("SELECT * FROM " + selectedItem + ";");
    }

    private void openDb() {
        File dbFile = new File(dbFileNameTxt.getText());
        if (!dbFile.exists()) {
            JOptionPane.showMessageDialog(new Frame(), "File doesn't exist!");
            return;
        }

        String url = "jdbc:sqlite:" + dbFileNameTxt.getText();
        dataSource.setUrl(url);

        try (Connection conn = dataSource.getConnection()) {
            if (conn.isValid(5)) {
                System.out.println("Connection successful.");
                Statement statement = conn.createStatement();

                String getTables = "SELECT name FROM sqlite_master " +
                        "WHERE type ='table' AND name NOT LIKE 'sqlite_%';";
                statement.execute(getTables);
                ResultSet tables = statement.getResultSet();
                List<String> tableList = new ArrayList<>();

                while (tables.next()) {
                    System.out.println("Processing row");
                    tableList.add(tables.getString("name"));
                }

                if (tableList.isEmpty()) {
                    JOptionPane.showMessageDialog(new Frame(), "Wrong file name!");
                    tableSelect.setEnabled(false);
                    queryTxt.setEnabled(false);
                    executeBtn.setEnabled(false);
                    return;
                }

                tableSelect.removeAllItems();
                for (String table : tableList) {
                    tableSelect.addItem(table);
                    System.out.println(table);
                }
                tables.close();
                statement.close();

                tableSelect.setEnabled(true);
                queryTxt.setEnabled(true);
                executeBtn.setEnabled(true);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(new Frame(), "Wrong file name!");
            e.printStackTrace();
            tableSelect.setEnabled(false);
            queryTxt.setEnabled(false);
            executeBtn.setEnabled(false);
        }
    }

}
