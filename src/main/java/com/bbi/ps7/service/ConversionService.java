package com.bbi.ps7.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ConversionService {

    private String csvFile = "C:\\Users\\FCI773\\Downloads\\ps7\\src\\main\\resources\\test.csv"; // Replace with your CSV file path
    private String line;
    private String csvSplitBy = ","; // CSV separator
    private String tableName = "PersonTable2";

    // Database connection details
    private String dbUrl = "jdbc:h2:mem:testdb"; // Adjust URL as per your database
    private String dbUser = "sa"; // Your database username
    private String dbPassword = "password"; // Your database password

    public void csvService() {
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            Statement statement = connection.createStatement();

            if ((line = br.readLine()) != null) {
                // Split the header line to get column names
                String[] columns = line.split(csvSplitBy);
                List<String> columnTypes = inferColumnTypes(br);

                // Create a table with the provided table name and dynamic columns
                StringBuilder createTableQuery = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableName + " (");
                for (int i = 0; i < columns.length; i++) {
                    createTableQuery.append(columns[i]).append(" ").append(columnTypes.get(i));
                    if (i < columns.length - 1) {
                        createTableQuery.append(", ");
                    }
                }
                createTableQuery.append(")");
                statement.executeUpdate(createTableQuery.toString());
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Second try-with-resources block for reading and inserting data
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            br.readLine(); // Skip header line

            Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            Statement statement = connection.createStatement();

            while ((line = br.readLine()) != null) {
                // Split the line by the comma separator
                String[] values = line.split(csvSplitBy);

                // Generate dynamic insert query based on columns
                StringBuilder insertQuery = new StringBuilder("INSERT INTO " + tableName + " VALUES (");
                for (int i = 0; i < values.length; i++) {
                    insertQuery.append("'").append(values[i]).append("'");
                    if (i < values.length - 1) {
                        insertQuery.append(", ");
                    }
                }
                insertQuery.append(")");
                statement.executeUpdate(insertQuery.toString());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<String> inferColumnTypes(BufferedReader br) throws IOException {
        List<String> columnTypes = new ArrayList<>();
        String dataLine;
        if ((dataLine = br.readLine()) != null) {
            String[] values = dataLine.split(csvSplitBy);
            for (String value : values) {
                // Infer data type based on the value
                if (value.matches("^\\d+$")) {
                    columnTypes.add("INT");
                } else if (value.matches("^\\d*\\.\\d+$")) {
                    columnTypes.add("DOUBLE");
                } else {
                    columnTypes.add("VARCHAR(255)");
                }
            }
        }
        return columnTypes;
    }
}
