package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;


public class JDBCConnection {

    // Name of database file (contained in database folder)
    private static final String DATABASE = "jdbc:sqlite:database/Covid19.db";

    public JDBCConnection() {
        System.out.println("Created JDBC Connection Object");
    }

    /**
     * Quickly allows the reading of files on the local machine.
     * The url must specify a relative path to the file from the initial application.
     * Very useful for reading in SQL scripts.
     * 
     * @param path the path to the file to read.
     * @return  the string value of the files contents.
     * @throws IOException
     */
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

    /**
     * Gets the most recent data of a specified country.
     * The input is the Alpha-2 string of the desired location.
     * 
     * @param countryCode   Two character long string of a countries Alpha-2 code
     * @return              A map consisting of the countries most recent statistics
     * @throws IOException
     */
    public Map<String, Object> getMostRecent(String countryCode) throws IOException {
        System.out.println("Called: getMostRecent()");
        Map<String, Object> output = new HashMap<String, Object>();
        Connection connection = null;
        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = String.format("SELECT * FROM getMostRecent WHERE Country_Code = '%s'", countryCode);

            // Get Result
            ResultSet results = statement.executeQuery(query);

            DecimalFormat formatter = new DecimalFormat("#,###");

            // Iterate through results and populate the map
            while(results.next()){

                String Country = results.getString("Country_Region_Name");
                String Date = results.getString("Date");
                String Cases = formatter.format(results.getInt("NewCases"));
                String Deaths = formatter.format(results.getInt("NewDeaths"));

                output.put("Country", Country);
                output.put("LastDate", Date);
                output.put("NewCases", Cases);
                output.put("NewDeaths", Deaths);
            }

            // Close the statement because we are done with it
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
        System.out.println("Concluded: getMostRecent()\n");
        return output;
    }

    /**
     * Queries the cumulative statistics for a given country. 
     * Any numerical out puts become ',' seperated.
     * The inputted countryCode represents a given countries Alpha-2 code.
     * 
     * @param countryCode   Two character long string of a countries Alpha-2 code
     * @return              A map consisting of the relevant countries cumulative statistical information.
     * @throws IOException
     */
    public Map<String, Object> getCumulative(String countryCode) throws IOException {
        System.out.println("Called: getCumulative(countryCode)");
        Map<String, Object> output = new HashMap<String, Object>();
        Connection connection = null;
        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            
            String query = "SELECT * FROM getCumulative WHERE Country_Code = '%s'";
            query = String.format(query, countryCode);


            // Get Result
            ResultSet results = statement.executeQuery(query);

            DecimalFormat formatter = new DecimalFormat("#,###");
            
            // Iterate through results and populate the map with relevant field values
            while(results.next()){

                String Country = results.getString("Country_Region_Name");
                String Cases = formatter.format(results.getInt("Cases"));
                String Deaths = formatter.format(results.getInt("Deaths"));

                output.put("Country", Country);
                output.put("cumCases", Cases);
                output.put("cumDeaths", Deaths);
            }

            // Close the statement because we are done with it
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
        System.out.println("Concluded: getCumulative(countryCode)\n");
        return output;
    }

    /**
     * Queries the cumulative information from the databases getCumulative view.
     * It then returns a map of the relevant statistical information, with numerical values seperated by ','
     * @return              A map of cumulative values for all locations
     * @throws IOException
     */
    public Map<String, Object> getCumulative() throws IOException {
        System.out.println("Called: getCumulative()");
        Map<String, Object> output = new HashMap<String, Object>();
        Connection connection = null;
        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT * FROM getCumulative";

            // Get Result
            ResultSet results = statement.executeQuery(query);
            
            // Iterate through results and populate the map with relevant field values
            DecimalFormat formatter = new DecimalFormat("#,###");
            while(results.next()){

                String Cases = formatter.format(results.getInt("Cases"));
                String Deaths = formatter.format(results.getInt("Deaths"));

                output.put("wwCumCases", Cases);
                output.put("wwCumDeaths", Deaths);
            }

            // Close the statement because we are done with it
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
        System.out.println("Concluded: getCumulative()\n");
        return output;
    }

    /**
     * Creates an array of dictionaries containing case counts and the locations Alpha-2 code.
     * @return              An ArrayList of maps including cumulative case values and countryCodes
     * @throws IOException
     */
    public ArrayList<Map<String, Object>> getHeatMap() throws IOException {
        System.out.println("Called: getHeatMap()");
        ArrayList<Map<String, Object>> output = new ArrayList<>();
        Connection connection = null;
        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT Country_Code, Cases FROM getCumulative";
            

            // Get Result
            ResultSet results = statement.executeQuery(query);

            // Iterate through results and populate the map with relevant field values
            while(results.next()){
                Map<String, Object> tmp = new HashMap<String, Object>();

                String Code = results.getString("Country_Code");
                int Cases = results.getInt("Cases");

                tmp.put("id", Code);
                tmp.put("value", Cases);
                output.add(tmp);
            }

            // Close the statement because we are done with it
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
        System.out.println("Concluded: getHeatMap()\n");
        return output;
    }
    
    /**
     * Generates a table including Total, New, and population % affected for both Case and Death fields.
     * Additionally the table includes population information.
     * @return              A table consisting of basically all data in the database.
     * @throws IOException
     */
    public ArrayList<ArrayList<String>> getTableValues() throws IOException {
        System.out.println("Called: getTableValues()");
        ArrayList<ArrayList<String>> table = new ArrayList<>();
        Connection connection = null;

        
        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = readFile("database/scripts/getDataTable.query");

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
        System.out.println("Concluded: getTableValues()\n");

        return table;
    }

    /**
     * Generates a table including Total, New, and population % affected for both Case and Death fields for each
     * state.
     * Additionally the table includes population information.
     * @return              A table consisting of basically all data in the database for each state.
     * @throws IOException
     */
    public ArrayList<ArrayList<String>> getStatesTableValues(String Country_Code) throws IOException {
        System.out.println("Called: getStatesTableValues(String Country_Code)");
        ArrayList<ArrayList<String>> table = new ArrayList<>();
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = readFile("database/scripts/getStatesDataTable.query");
            query += "WHERE NOT gC.State_Province_Name IS NULL AND ";
            query += "gC.Country_Region_Name = (";
            query += "SELECT Alpha_2_Name FROM Country_Codes WHERE Country_Code = '" + Country_Code + "')";

            ResultSet results = statement.executeQuery(query);

            // Iterate through results and populate the map with relevant field values.
            while (results.next()){
                ArrayList<String> res = new ArrayList<>();
                res.add(results.getString("State_Province_Name"));
                res.add(results.getString("Country_Region_Name"));
                res.add(results.getString("Country_Code"));
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
        System.out.println("Concluded: getStatesTableValues(String Country_Code)\n");

        return table;
    }

    /**
     * Quiries for records of all countries that have subregions/provinces/states and returns it in the form of
     * a hashmap made from the locations Alpha-2 Code (Key) and the official name of the location (Value)
     * @return              Hashmap of Alpha-2 codes (key) and names (value) for countries with subregions.
     * @throws IOException
     */
    public HashMap<String, String> getCountriesWithStates() throws IOException {
        System.out.println("Called: getCountriesWithStates()");
        HashMap<String, String> codes = new HashMap<>();
        Connection connection = null;

        

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = readFile("database/scripts/getCountriesWithStates.query");

            ResultSet results = statement.executeQuery(query);

            // Iterate through results and populate the map with relevant field values
            while (results.next()){
                codes.put(results.getString("Country_Code"), results.getString("Country_Region_Name"));
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
        System.out.println("Concluded: getCountriesWithStates()\n");

        return codes;
    }

    /**
     * Creates a colour gradiant between two given values, and assigns a colour to each input value according to
     * its numerical value. It then assigns the value of each provided key in the format rgb(?,?,?) in an RGB
     * structure. This array of locations & associated colour key is returned in the form of an Array of Maps.
     * @param values    An array of Maps in the from of ``id: numerical_value``
     * @param minRGB    The minimum RGB values (array) to use in the gradiant.
     * @param maxRGB    The maximum RGB values (array) to use in the gradiant.
     * @return
     */
    public ArrayList<Map<String, Object>> addColours(ArrayList<Map<String, Object>> values, int[] minRGB, int[] maxRGB) {
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // Get the minimum and maximum Case numbers in the database as to determine the colouring of the
            // input values.
            String queryGetMax = "SELECT Country_Region_Name, Cases FROM getCumulative ORDER BY cases DESC LIMIT 1";
            String queryGetMin = "SELECT Country_Region_Name, Cases FROM getCumulative ORDER BY cases ASC LIMIT 1";

            ResultSet result = statement.executeQuery(queryGetMax);
            result.next();
            int max = result.getInt("Cases");

            result = statement.executeQuery(queryGetMin);
            result.next();
            int min = result.getInt("Cases");

            // Iterate through each of the input values and reassign the value as an expression of its place on
            // the generated RGB gradiant.
            for (int i = 0; i < values.size(); i++){
                double cases = Double.valueOf(String.valueOf(values.get(i).get("cases")).replaceAll(",",""));
                double percentage = (((double)(cases) - min) / (double)(max - min));
                int[] tmp = {0, 0, 0};
                tmp[0] = minRGB[0] + (int)((double)(maxRGB[0] - minRGB[0]) * percentage);
                tmp[1] = minRGB[1] + (int)((double)(maxRGB[1] - minRGB[1]) * percentage);
                tmp[2] = minRGB[2] + (int)((double)(maxRGB[2] - minRGB[2]) * percentage);
                values.get(i).put("color", "rgb(" + tmp[0] + ", " + tmp[1] + ", " + tmp[2] + ")");
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
        return values;
    }

    /**
     * Create all the values used to populate the interactable map visualisation.
     * @return              An array of maps that contain values relative to a specific country.
     * @throws IOException
     */
    public ArrayList<Map<String, Object>> getBigMap() throws IOException {
        System.out.println("Called: getBigMap()");
        ArrayList<Map<String, Object>> table = new ArrayList<>();
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // Read in the SQL query
            String query = readFile("database/scripts/getDataTable.query");

            ResultSet results = statement.executeQuery(query);

            // Iterate through results and populate the map with relevant field values
            DecimalFormat formatter = new DecimalFormat("#,###");
            while (results.next()){
                Map<String, Object> res = new HashMap<String, Object>();
                res.put("id", results.getString("Country_Code"));
                res.put("cases", formatter.format(results.getInt("Cases")));
                res.put("newCases", formatter.format(results.getInt("newCases")));
                res.put("deaths", formatter.format(results.getInt("Deaths")));
                res.put("newDeaths", formatter.format(results.getInt("newDeaths")));
                res.put("deathsPercentage", String.valueOf(results.getDouble("Deaths %") / 100.0));
                res.put("population", formatter.format(results.getInt("Population")));
                res.put("populationpercentage", String.valueOf(results.getDouble("Population Infected %") / 100.0));
                table.add(res);
            }

            // Generate the colours to go with the values
            int[] minRGB = {245, 138, 151};
            int[] maxRGB = {240, 0, 32};
            table = addColours(table, minRGB, maxRGB);

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
        System.out.println("Concluded: getBigMap()\n");

        return table;
    }

    /**
     * Queries the Regions in the database which have no associated data. Returns them in the form of a map.
     * @return              An array of maps containing the name and code of regions without any available data
     * @throws IOException
     */
    public ArrayList<Map<String, String>> getDatalessRegions() throws IOException{
        ArrayList<Map<String, String>> res = new ArrayList<>();
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = readFile("database/scripts/getDatalessRegions.query");

            ResultSet results = statement.executeQuery(query);
            
            while (results.next()){
                Map<String, String> tmp = new HashMap<String, String>();
                tmp.put("Alpha_2_Name", results.getString("Alpha_2_Name"));
                tmp.put("Country_Code", results.getString("Country_Code"));
                res.add(tmp);
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

        return res;
    }

    /**
     * Queries the data to be used on the index page. Specifically, global cumulative cases, global cumulative 
     * deaths, and global new cases. These values are returned in a map.
     * @return              A map containing global cumulative cases, global cumulative deaths, and global new cases.
     * @throws IOException
     */
    public Map<String, String> getIndexPageInfo() throws IOException{
        System.out.println("Called: getIndexPageInfo()");
        Map<String, String> res = new HashMap<>();

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = readFile("database/scripts/getIndexPageInfo.query");

            ResultSet results = statement.executeQuery(query);
            
            DecimalFormat formatter = new DecimalFormat("#,###");
            
            // Iterate through results and populate the map with relevant field values
            while (results.next()){
                res.put("Ttl_Cases",  formatter.format(results.getInt("Ttl_Cases")));
                res.put("Ttl_Deaths", formatter.format(results.getInt("Ttl_Deaths")));
                res.put("New_Cases",  formatter.format(results.getInt("New_Cases")));
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

        System.out.println("Concluded: getIndexPageInfo()\n");
        return res;
    }

}
