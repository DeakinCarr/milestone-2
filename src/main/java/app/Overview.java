package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.javalin.http.Context;
import io.javalin.http.Handler;

public class Overview implements Handler{
    // URL of this page relative to http://localhost:7000/
    public static final String URL = "/countries";

    // Name of the Thymeleaf HTML template page in the resources folder
    private static final String TEMPLATE = ("overview.html");

    @Override
    public void handle(Context context) throws Exception {
        // The model of data to provide to Thymeleaf.
        Map<String, Object> model = new HashMap<String, Object>();

        // Get the value submitted in the web-request. This simultaneously assigns 
        // a value, as well as actually checks if a post request was sent.
        String countryCode = context.formParam("country");
        if (countryCode == null) {
            countryCode = "AU";
        }
        // Assumes the same role as above
        String countryCode2 = context.formParam("country2");
        if (countryCode2 == null) {
            countryCode2 = "AU";
        }
        
        model.put("country", countryCode);
        model.put("country2", countryCode2);


        // Create new instance of database handler
        JDBCConnection jdbc = new JDBCConnection();

        // Get all the cumulative statistics for the requested country
        Map<String, Object> cumData = jdbc.getCumulative(countryCode);
        // Get the recent stats for the requested country
        Map<String, Object> mostRecent = jdbc.getMostRecent(countryCode);
        // And likewise do the same for the secondary country that is passed in
        Map<String, Object> cumData2 = jdbc.getCumulative(countryCode2);
        Map<String, Object> mostRecent2 = jdbc.getMostRecent(countryCode2);

        // Get the world wide cumulative statistics
        Map<String, Object> wwCumData = jdbc.getCumulative();
        // Get a table of current cases and deaths, as well as new cases and deaths. Population is also provided
        // by this call
        ArrayList<ArrayList<String>> table = jdbc.getTableValues();
        // Make a colour gradiant that corosponds to the global cases statistics
        ArrayList<Map<String, Object>> heatMap = jdbc.getHeatMap();

        // Add all the statistics gathered for the first country to the model
        for (int i = 0; i < cumData.size(); i++){
            model.put(cumData.keySet().toArray()[i].toString(), cumData.values().toArray()[i]);
        }
        for (int i = 0; i < mostRecent.size(); i++){
            model.put(mostRecent.keySet().toArray()[i].toString(), mostRecent.values().toArray()[i]);
        }

        // Add all the statistics gathered for the second country to the model
        for (int i = 0; i < cumData2.size(); i++){
            model.put(cumData2.keySet().toArray()[i].toString() + "2", cumData2.values().toArray()[i]);
        }
        for (int i = 0; i < mostRecent2.size(); i++){
            model.put(mostRecent2.keySet().toArray()[i].toString() + "2", mostRecent2.values().toArray()[i]);
        }

        // Get the world wide statistics and place them into the model.
        for (int i = 0; i < wwCumData.size(); i++){
            model.put(wwCumData.keySet().toArray()[i].toString(), wwCumData.values().toArray()[i]);
        }

        model.put("table_vals", table);
        model.put("heatMap", heatMap);
        
        
        // Query the regions on earth where no data is available and place them into the model
        ArrayList<Map<String, String>> datalessRegions = jdbc.getDatalessRegions();
        model.put("datalessRegions", datalessRegions);

        // Creates a 1-D array that consists of the country codes found in the previous queries
        ArrayList<String> excludedRegions = new ArrayList<>();
        for (Map<String, String> region : datalessRegions) {
            excludedRegions.add(region.get("Country_Code"));
        }
        // Add antarctia because there is no data for it, and it is not a recognised Alpha-2 code in the 
        // database
        excludedRegions.add("AQ");

        // Append the 1D array to the model
        model.put("excludedRegions", excludedRegions);

        // Makes Javalin render the webpage using Thymeleaf
        context.render(TEMPLATE, model);
        
    }

}
