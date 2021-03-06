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

    // Gets the total number of Deaths
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
    // Gets the total number of infections
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

        // Gets table values for countries near to each other
        public ArrayList<ArrayList<String>> getSimilarCountryTableValues(String Country_Code) throws IOException {
            System.out.println("Called: getSimilarCountryTableValues()");
            ArrayList<ArrayList<String>> table = new ArrayList<>();
            Connection connection = null;
    
            
    
            try {
                connection = DriverManager.getConnection(DATABASE);
    
                Statement statement = connection.createStatement();
                Statement longLatStat = connection.createStatement();
                statement.setQueryTimeout(30);
    
                int temp = 0;
                Double[] longFull = new Double[312];
                Double[] latFull = new Double[312];
                String[] countCod = new String[312];
                String question = readFile("database/scripts/getFullLongLat.query");
                ResultSet fullLongLat = longLatStat.executeQuery(question);
                // Gets the Longitude and Latitude of the country that is selected
                String getLongLatInfo = readFile("database/scripts/getLongLatInfo.query") + " \"" + Country_Code + "\"";
                ResultSet longLat = statement.executeQuery(getLongLatInfo);
                while (longLat.next()){
                    Double longitude = longLat.getDouble("Longitude");
                    Double latitude = longLat.getDouble("Latitude");
                    // Compares all other countries with the initial country selected removing 
                    // the ones that don't meet the criteria. Also some error handling.
                    while (fullLongLat.next()) {
                        countCod[temp] = fullLongLat.getString("Country_Code");
                        longFull[temp] = fullLongLat.getDouble("Longitude");
                        latFull[temp] = fullLongLat.getDouble("Latitude");
                        // Checks if the country is Zimbabwe
                        // Also has some error handling as Zimbabwe is the last country in the list and therefore needs 
                        // to be prevented from showing up when it does not fit the criteria
                        if (!countCod[temp].equals("ZW")){ 
                            if (longFull[temp] >= (longitude - 15) 
                                && longFull[temp] <= (longitude + 15) 
                                && latFull[temp] >= (latitude - 8) 
                                && latFull[temp] <= (latitude + 8)) {  
                                    ++temp; 
                            }
                        }
                        // Error handling for Zimbabwe
                        else {
                            if (longFull[temp] >= (longitude - 15)
                            && longFull[temp] <= (longitude + 15) 
                            && latFull[temp] >= (latitude - 8)
                            && latFull[temp] <= (latitude + 8)){
                            }
                            else {
                                countCod[temp] = null;
                                ++temp;
                            }
                        }
                    }
                }
                longLatStat.close();
                
                // Generates a table of data based on the countries that match the above criteria
                String query = readFile("database/scripts/getSimilarDataTable.query") + " WHERE Country_Code IN ('" + countCod[0] + "'";
                for (int i = 1; i < countCod.length; ++i) {
                    query = query + ",'" + countCod[i] + "'";
                }
                query += ")";
    
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

                // Gets table values for countries with similar populations 
                public ArrayList<ArrayList<String>> getPopulationCountryTableValues(String Country_Code) throws IOException {
                    System.out.println("Called: getSimilarCountryTableValues()");
                    ArrayList<ArrayList<String>> table = new ArrayList<>();
                    Connection connection = null;
            
                    
            
                    try {
                        connection = DriverManager.getConnection(DATABASE);
            
                        Statement statement = connection.createStatement();
                        Statement popStat = connection.createStatement();
                        statement.setQueryTimeout(30);
            
                        int temp = 0;
                        int[] popFull = new int[312];
                        String[] countCod = new String[312];
                        String question = readFile("database/scripts/getFullPop.query");
                        ResultSet fullPop = popStat.executeQuery(question);
                        // Gets the Population of the country that is selected
                        String getPopulation = readFile("database/scripts/getPopulation.query") + " \"" + Country_Code + "\"";
                        ResultSet pop = statement.executeQuery(getPopulation);
                        while (pop.next()){
                            Double basePopulation = pop.getDouble("SUM(Population)");
                            // Compares the population of the selected country to all other countries to make sure it's 
                            // within an acceptable range. Also some error handling.
                            while (fullPop.next()) {
                                countCod[temp] = fullPop.getString("Country_Code");
                                popFull[temp] = fullPop.getInt("SUM(Population)");
                                if (popFull[temp] >= (basePopulation - (basePopulation * 0.3)) 
                                    && popFull[temp] <= (basePopulation + (basePopulation * 0.3))) {
                                    ++temp;   
                                }
                                // Checks if popFull[temp] is equal to the population of Zimbabwe
                                // Also has some error handling as Zimbabwe is the last country in the list and therefore needs 
                                // to be prevented from showing up when it does not fit the criteria
                                if (popFull[temp] == 14862927) {
                                    if (!(popFull[temp] >= (basePopulation - (basePopulation * 0.3)) 
                                    && popFull[temp] <= (basePopulation + (basePopulation * 0.3)))) {
                                        countCod[temp] = null;
                                    }
                                }
                            }
                        }
                        
                        popStat.close();
                        
            
                        // Generates a table of data based on the countries that match the above criteria
                        String query = readFile("database/scripts/getSimilarDataTable.query") + " WHERE Country_Code IN ('" + countCod[0] + "'";
                        for (int i = 1; i < countCod.length; ++i) {
                            query = query + ",'" + countCod[i] + "'";
                        }
                        query += ")";
            
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

