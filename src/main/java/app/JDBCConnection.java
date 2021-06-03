package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;

/**
 * Class for Managing the JDBC Connection to a SQLLite Database.
 * Allows SQL queries to be used with the SQLLite Databse in Java.
 *
 * @author Santha Sumanasekara, 2021. email: santha.sumanasekara@rmit.edu.au
 * @author Timothy Wiley, 2021. email: timothy.wiley@rmit.edu.au
 */
public class JDBCConnection {

    // Name of database file (contained in database folder)
    private static final String DATABASE = "jdbc:sqlite:database/Covid19.db";

    public JDBCConnection() {
        System.out.println("Created JDBC Connection Object");
    }

    /**
     * Get all of the Movies in the database
     */
    public ArrayList<String> getMovies() {
        ArrayList<String> movies = new ArrayList<String>();

        // Setup the variable for the JDBC connection
        Connection connection = null;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // The Query
            String query = "SELECT * FROM movie";
            
            // Get Result
            ResultSet results = statement.executeQuery(query);

            // Process all of the results
            // The "results" variable is similar to an array
            // We can iterate through all of the database query results
            while (results.next()) {
                // We can lookup a column of the a single record in the
                // result using the column name
                // BUT, we must be careful of the column type!
                int id              = results.getInt("mvnumb");
                String movieName     = results.getString("mvtitle");
                int year            = results.getInt("yrmde");
                String type         = results.getString("mvtype");

                // For now we will just store the movieName and ignore the id
                movies.add(movieName);
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

        // Finally we return all of the movies
        return movies;
    }

    /**
     * Get all the movies in the database by a given type.
     * Note this takes a string of the type as an argument!
     * This has been implemented for you as an example.
     * HINT: you can use this to find all of the horror movies!
     */
    public ArrayList<String> getMoviesByType(String movieType) {
        ArrayList<String> movies = new ArrayList<String>();

        // Setup the variable for the JDBC connection
        Connection connection = null;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // The Query
            String query = "SELECT * FROM movie WHERE mvtype = '" + movieType + "'";
            System.out.println(query);
            
            // Get Result
            ResultSet results = statement.executeQuery(query);

            // Process all of the results
            while (results.next()) {
                String movieName     = results.getString("mvtitle");
                movies.add(movieName);
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

        // Finally we return all of the movies
        return movies;
    }

    public Map<String, Object> getMostRecent(String countryCode) {
        Map<String, Object> output = new HashMap<String, Object>();
        Connection connection = null;
        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT co.Country, da.Date, Cases, Deaths "
                + "FROM Statistics st "
                + "LEFT JOIN Location lo ON "
                + "st.Location = lo.ID "    
                + "LEFT JOIN Countries co ON "
                + "lo.Country = co.ID "
                + "LEFT JOIN Dates da ON "
                + "da.ID = st.Date "
                + "WHERE Deaths >= 0 AND Cases >= 0 "
                + "AND co.Country_Code = '" + countryCode + "' "    
                + "ORDER BY da.ID DESC "
                + "LIMIT 1;";

            // Get Result
            ResultSet results = statement.executeQuery(query);

            DecimalFormat formatter = new DecimalFormat("#,###");

            while(results.next()){

                String Country = results.getString("Country");
                String Date = results.getString("Date");
                String Cases = formatter.format(results.getInt("Cases"));
                String Deaths = formatter.format(results.getInt("Deaths"));

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
        return output;
    }

    public Map<String, Object> getCumulative(String countryCode) {
        Map<String, Object> output = new HashMap<String, Object>();
        Connection connection = null;
        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT co.Country, SUM(Cases) AS Cases, SUM(Deaths) AS Deaths "
            + "FROM Statistics st "
            + "LEFT JOIN Location lo ON "
                + "st.Location = lo.ID "
            + "LEFT JOIN Countries co ON "
                + "lo.Country = co.ID "
            + "WHERE Deaths >= 0 AND Cases >= 0 "
                + "AND co.Country_Code = '" + countryCode + "'";

            // Get Result
            ResultSet results = statement.executeQuery(query);

            DecimalFormat formatter = new DecimalFormat("#,###");
            
            while(results.next()){

                String Country = results.getString("Country");
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
        return output;
    }
    public Map<String, Object> getCumulative() {
        Map<String, Object> output = new HashMap<String, Object>();
        Connection connection = null;
        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT SUM(Cases) AS Cases, SUM(Deaths) AS Deaths "
            + "FROM Statistics st "
            + "LEFT JOIN Location lo ON "
                + "st.Location = lo.ID "
            + "LEFT JOIN Countries co ON "
                + "lo.Country = co.ID "
            + "WHERE Deaths >= 0 AND Cases >= 0 ";

            // Get Result
            ResultSet results = statement.executeQuery(query);

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
        return output;
    }

    public ArrayList<Map<String, Object>> getHeatMap() {
        ArrayList<Map<String, Object>> output = new ArrayList<>();
        Connection connection = null;
        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT Countries.Country_Code, Sum(Statistics.Cases) AS SumOfCases "
            + "FROM States INNER JOIN ((Countries INNER JOIN Location ON Countries.ID = Location.Country) INNER JOIN (Dates INNER JOIN Statistics ON Dates.ID = Statistics.Date) ON Location.ID = Statistics.Location) ON States.ID = Location.State "
            + "GROUP BY Countries.Country_Code";
            

            // Get Result
            ResultSet results = statement.executeQuery(query);

            while(results.next()){
                Map<String, Object> tmp = new HashMap<String, Object>();

                String Code = results.getString("Country_Code");
                int Cases = results.getInt("SumOfCases");

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
        return output;
    }
    
    public ArrayList<ArrayList<String>> getTableValues() {
        ArrayList<ArrayList<String>> table = new ArrayList<>();
        Connection connection = null;

        

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT CUM.Country_Code AS 'Code', cou.Country, cum.SumOfCases AS 'Total Cases', cum.SumOfDeaths AS 'Total Deaths', pop.SumOfPopulation AS 'Population', ((cum.SumOfCases) * 10000 / pop.SumOfPopulation) AS 'Population Infected %', ((cum.SumOfDeaths) * 10000 / pop.SumOfPopulation) AS 'Deaths %', new.newestcases AS 'New Cases', new.newestdeaths AS 'New Deaths' "
            + "FROM Countries "
            + "LEFT JOIN ( "
            + "        SELECT Countries.Country_Code, Sum(Statistics.Cases) AS SumOfCases, Sum(Statistics.Deaths) AS SumOfDeaths "
            + "        FROM States INNER JOIN ((Countries INNER JOIN Location ON Countries.ID = Location.Country) INNER JOIN (Dates INNER JOIN Statistics ON Dates.ID = Statistics.Date) ON Location.ID = Statistics.Location) ON States.ID = Location.State "
            + "        GROUP BY Countries.Country_Code) Cum "
            + "    ON cum.Country_Code = Countries.Country_Code "
            + "LEFT JOIN ( "
            + "        SELECT Countries.Country_Code, Sum(Location.Population) AS SumOfPopulation "
            + "        FROM States INNER JOIN (Countries INNER JOIN Location ON Countries.ID = Location.Country) ON States.ID = Location.State "
            + "        GROUP BY Countries.Country_Code) Pop "
            + "    ON pop.Country_Code = Countries.Country_Code "
            + "LEFT JOIN ( "
            + "        SELECT Countries.Country_Code, Countries.Country "
            + "        FROM Countries) Cou "
            + "    ON cou.Country_Code = Countries.Country_Code "
            + "LEFT JOIN ( "
            + "        SELECT Countries.Country_Code, SUM(Statistics.Cases) AS 'NewestCases', SUM(Statistics.Deaths) AS 'NewestDeaths' "
            + "        FROM (States INNER JOIN (Countries INNER JOIN Location ON Countries.ID = Location.Country) ON States.ID = Location.State) INNER JOIN (Dates INNER JOIN Statistics ON Dates.ID = Statistics.Date) ON Location.ID = Statistics.Location "
            + "        GROUP BY Countries.Country, Dates.ID ORDER BY Dates.ID DESC "
            + "        LIMIT 189) new "
            + "    ON new.Country_Code = Countries.Country_Code "
            + "WHERE NOT CUM.Country_Code IS NULL ";

            ResultSet results = statement.executeQuery(query);

            while (results.next()){
                ArrayList<String> res = new ArrayList<>();
                res.add(results.getString("Code"));
                res.add(results.getString("Country"));
                res.add(results.getString("Total Cases"));
                res.add(results.getString("New Cases"));
                res.add(results.getString("Total Deaths"));
                res.add(results.getString("New Deaths"));
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

        return table;
    }

    public ArrayList<Map<String, Object>> addColours(ArrayList<Map<String, Object>> values, int[] minRGB, int[] maxRGB) {
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String queryGetMax = "SELECT Countries.Country, Sum(Statistics.Cases) AS SumOfCases "
            + "FROM States INNER JOIN ((Countries INNER JOIN Location ON Countries.ID = Location.Country) INNER JOIN (Dates INNER JOIN Statistics ON Dates.ID = Statistics.Date) ON Location.ID = Statistics.Location) ON States.ID = Location.State "
            + "GROUP BY Countries.Country_Code "
            + "ORDER BY SumOfCases DESC "
            + "LIMIT 1";
            String queryGetMin = "SELECT Countries.Country, Sum(Statistics.Cases) AS SumOfCases "
            + "FROM States INNER JOIN ((Countries INNER JOIN Location ON Countries.ID = Location.Country) INNER JOIN (Dates INNER JOIN Statistics ON Dates.ID = Statistics.Date) ON Location.ID = Statistics.Location) ON States.ID = Location.State "
            + "GROUP BY Countries.Country_Code "
            + "ORDER BY SumOfCases ASC "
            + "LIMIT 1";

            ResultSet result = statement.executeQuery(queryGetMax);
            result.next();
            int max = result.getInt("SumOfCases");

            result = statement.executeQuery(queryGetMin);
            result.next();
            int min = result.getInt("SumOfCases");

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

    public ArrayList<Map<String, Object>> getBigMap() {
        ArrayList<Map<String, Object>> table = new ArrayList<>();
        Connection connection = null;

        

        try {
            connection = DriverManager.getConnection(DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT CUM.Country_Code AS 'Code', cou.Country, cum.SumOfCases AS 'Total Cases', cum.SumOfDeaths AS 'Total Deaths', pop.SumOfPopulation AS 'Population', ((cum.SumOfCases) * 10000 / pop.SumOfPopulation) AS 'Population Infected %', ((cum.SumOfDeaths) * 10000 / pop.SumOfPopulation) AS 'Deaths %', new.newestcases AS 'New Cases', new.newestdeaths AS 'New Deaths' "
            + "FROM Countries "
            + "LEFT JOIN ( "
            + "        SELECT Countries.Country_Code, Sum(Statistics.Cases) AS SumOfCases, Sum(Statistics.Deaths) AS SumOfDeaths "
            + "        FROM States INNER JOIN ((Countries INNER JOIN Location ON Countries.ID = Location.Country) INNER JOIN (Dates INNER JOIN Statistics ON Dates.ID = Statistics.Date) ON Location.ID = Statistics.Location) ON States.ID = Location.State "
            + "        GROUP BY Countries.Country_Code) Cum "
            + "    ON cum.Country_Code = Countries.Country_Code "
            + "LEFT JOIN ( "
            + "        SELECT Countries.Country_Code, Sum(Location.Population) AS SumOfPopulation "
            + "        FROM States INNER JOIN (Countries INNER JOIN Location ON Countries.ID = Location.Country) ON States.ID = Location.State "
            + "        GROUP BY Countries.Country_Code) Pop "
            + "    ON pop.Country_Code = Countries.Country_Code "
            + "LEFT JOIN ( "
            + "        SELECT Countries.Country_Code, Countries.Country "
            + "        FROM Countries) Cou "
            + "    ON cou.Country_Code = Countries.Country_Code "
            + "LEFT JOIN ( "
            + "        SELECT Countries.Country_Code, SUM(Statistics.Cases) AS 'NewestCases', SUM(Statistics.Deaths) AS 'NewestDeaths' "
            + "        FROM (States INNER JOIN (Countries INNER JOIN Location ON Countries.ID = Location.Country) ON States.ID = Location.State) INNER JOIN (Dates INNER JOIN Statistics ON Dates.ID = Statistics.Date) ON Location.ID = Statistics.Location "
            + "        GROUP BY Countries.Country, Dates.ID ORDER BY Dates.ID DESC "
            + "        LIMIT 189) new "
            + "    ON new.Country_Code = Countries.Country_Code "
            + "WHERE NOT CUM.Country_Code IS NULL ";

            ResultSet results = statement.executeQuery(query);

            DecimalFormat formatter = new DecimalFormat("#,###");
            while (results.next()){
                Map<String, Object> res = new HashMap<String, Object>();
                res.put("id", results.getString("Code"));
                res.put("cases", formatter.format(results.getInt("Total Cases")));
                res.put("newCases", formatter.format(results.getInt("New Cases")));
                res.put("deaths", formatter.format(results.getInt("Total Deaths")));
                res.put("newDeaths", formatter.format(results.getInt("New Deaths")));
                res.put("deathsPercentage", String.valueOf(results.getDouble("Deaths %") / 100.0));
                res.put("population", formatter.format(results.getInt("Population")));
                res.put("populationpercentage", String.valueOf(results.getDouble("Population Infected %") / 100.0));
                table.add(res);
            }
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

        return table;
    }
}
