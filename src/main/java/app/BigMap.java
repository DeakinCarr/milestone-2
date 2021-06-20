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
        // In this example the model will be filled with:
        //  - Title to give to the h1 tag
        //  - Array list of all movies for the UL element
        Map<String, Object> model = new HashMap<String, Object>();

        // Look up some information from JDBC
        // First we need to use your JDBCConnection class
        JDBCConnection jdbc = new JDBCConnection();

        ArrayList<Map<String, Object>> mapVals = jdbc.getBigMap();
        model.put("mapVals", mapVals);

        ArrayList<Map<String, String>> datalessRegions = jdbc.getDatalessRegions();
        model.put("datalessRegions", datalessRegions);

        ArrayList<String> excludedRegions = new ArrayList<>();
        for (Map<String, String> region : datalessRegions) {
            excludedRegions.add(region.get("Country_Code"));
        }
        excludedRegions.add("AQ");

        model.put("excludedRegions", excludedRegions);
        // DO NOT MODIFY THIS
        // Makes Javalin render the webpage using Thymeleaf
        context.render(TEMPLATE, model);
    }

}
