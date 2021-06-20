package app;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class for Managing the JDBC Connection to a SQLLite Database.
 * Allows SQL queries to be used with the SQLLite Databse in Java.
 * 
 * This is an example JDBC Connection that has a single query for the Movies Database
 * This is similar to the project workshop JDBC examples.
 *
 * @author Santha Sumanasekara, 2021. email: santha.sumanasekara@rmit.edu.au
 * @author Timothy Wiley, 2021. email: timothy.wiley@rmit.edu.au
 */
public class JDBCConnection2 {

    // Name of database file (contained in database folder)
    private static final String DATABASE = "jdbc:sqlite:database/Covid19.db";

    public JDBCConnection2() {
        System.out.println("Created JDBC Connection Object");
    }

    /**
     * Get all of the Movies in the database
     */
    public int getDeathsTot() {
        int count = 0;
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DATABASE);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            String query = "SELECT SUM(Deaths) FROM Infections_Deaths";
            ResultSet results = statement.executeQuery(query);
            while (results.next()) {
                int id = results.getInt("SUM(Deaths)");
                count = id;
            }
            statement.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }

        return count;
    }
    public int getInfectionsTot() {
        int count = 0;
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DATABASE);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            String query = "SELECT SUM(Infections) FROM Infections_Deaths;";
            ResultSet results = statement.executeQuery(query);
            while (results.next()) {
                int id = results.getInt("SUM(Infections)");
                count = id;
            }
            statement.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }

        return count;
    }
    public String readFile(String path) throws IOException {
        String everything = "";
        BufferedReader br = new BufferedReader(new FileReader(path));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            everything = sb.toString();
        } finally {
            br.close();
        }
        return everything;
    }

        // Max's Table Values
        public ArrayList<ArrayList<String>> getSimilarCountryTableValues(String Country_Code) throws IOException {
            System.out.println("Called: getSimilarCountryTableValues()");
            ArrayList<ArrayList<String>> table = new ArrayList<>();
            ArrayList<ArrayList<String>> tempLongLat = new ArrayList<>();
            Connection connection = null;
    
            
    
            try {
                connection = DriverManager.getConnection(DATABASE);
    
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);
    
                int temp = 0;
                Double[] longFull = new Double[248];
                Double[] latFull = new Double[248];
                String[] countCod = new String[248];
                String question = readFile("database/scripts/getFullLongLat.query");
                ResultSet fullLongLat = statement.executeQuery(question);
                // Gets the initial Longitude and Latitude of the country
                String getLongLatInfo = readFile("database/scripts/getLongLatInfo.query") + " \"" + Country_Code + "\"";
                ResultSet longLat = statement.executeQuery(getLongLatInfo);
                while (longLat.next()){
                    Double longitude = longLat.getDouble("Longitude");
                    Double latitude = longLat.getDouble("Latitude");
                    while (fullLongLat.next()) {
                        countCod[temp] = fullLongLat.getString("Country_Code");
                        longFull[temp] = fullLongLat.getDouble("Longitude");
                        latFull[temp] = fullLongLat.getDouble("Latitude");
                        if (longFull[temp] >= (longitude - 15) && longFull[temp] <= (longitude + 15) && latFull[temp] >= (latitude - 15) && latFull[temp] <= (latitude + 15)) {
                         ++temp;   
                        }
                    }
                }
                // Gets all other Longitude Latitude and Country names
    
                
    
    
                String query = readFile("database/scripts/getSimilarDataTable.query") + " WHERE Country_Code IN ('" + countCod[0] + "'";
                for (int i = 1; i < countCod.length; ++i) {
                    query = query + ",'" + countCod[i] + "'";
                }
                query += ")";
                System.out.println(query);
    
                ResultSet results = statement.executeQuery(query);
    
                while (results.next()){
                    ArrayList<String> res = new ArrayList<>();
                    res.add(results.getString("Country_Code"));
                    res.add(results.getString("Country_Region_Name"));
                    res.add(results.getString("Cases"));
                    res.add(results.getString("newCases"));
                    res.add(results.getString("Deaths"));
                    res.add(results.getString("newDeaths"));
                    res.add(String.valueOf(results.getDouble("Deaths %") / 100.0));
                    res.add(results.getString("Population"));
                    res.add(String.valueOf(results.getDouble("Population Infected %") / 100.0));
                    res.add("nan");
                    res.add("nan");
                    table.add(res);
                }
    
                statement.close();
            
            } catch (SQLException e) {
                // If there is an error, lets just pring the error
                System.err.println(e.getMessage());
            } finally {
                // Safety code to cleanup
                try {
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    // connection close failed.
                    System.err.println(e.getMessage());
                }
            }
            System.out.println("Concluded: getSimilarDataTable\n");
    
            return table;
        }
    
}

