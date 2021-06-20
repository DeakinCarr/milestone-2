package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.javalin.http.Context;
import io.javalin.http.Handler;

public class BigMap implements Handler {

    // URL of this page relative to http://localhost:7000/
    public static final String URL = "/map";

    // Name of the Thymeleaf HTML template page in the resources folder
    private static final String TEMPLATE = ("map-visual.html");

    @Override
    public void handle(Context context) throws Exception {
        // The model of data to provide to Thymeleaf.
        Map<String, Object> model = new HashMap<String, Object>();

        // Look up some information from JDBC
        // First we need to use your JDBCConnection class
        JDBCConnection jdbc = new JDBCConnection();

        // Query all the data used to populate the map visualisation
        // then append the results onto the model
        ArrayList<Map<String, Object>> mapVals = jdbc.getBigMap();
        model.put("mapVals", mapVals);

        // Query all the regions that exist that do not have any data associated with the
        // then append the results onto the model
        ArrayList<Map<String, String>> datalessRegions = jdbc.getDatalessRegions();
        model.put("datalessRegions", datalessRegions);

        // Create a new single dimensional array to store the raw Alpha-2 codes for the
        // regions which have no relavant data.
        ArrayList<String> excludedRegions = new ArrayList<>();
        // Iterate through ArrayList of Maps (created earlier) and extracts the Alpha-2 codes into 
        // the 1-D array.
        for (Map<String, String> region : datalessRegions) {
            excludedRegions.add(region.get("Country_Code"));
        }
        // Add Antarctica as well. No one wants to see that (plus there is no data for it either).
        excludedRegions.add("AQ");

        // Append the Array of excluded regions to the model.
        model.put("excludedRegions", excludedRegions);

        // Render away mon-fre
        context.render(TEMPLATE, model);
    }

}
