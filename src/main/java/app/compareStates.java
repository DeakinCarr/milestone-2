package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.javalin.http.Context;
import io.javalin.http.Handler;

public class compareStates implements Handler{
    // URL of this page relative to http://localhost:7000/
    public static final String URL = "/states";

    // Name of the Thymeleaf HTML template page in the resources folder
    private static final String TEMPLATE = ("compareStates.html");

    @Override
    public void handle(Context context) throws Exception {
        // The model of data to provide to Thymeleaf.
        Map<String, Object> model = new HashMap<String, Object>();

        // Get the value submitted in the web-request. This simultaneously assigns 
        // a value, as well as actually checks if a post request was sent.
        String countryCode = context.formParam("country");
        // If this is a get request, assume the user wants to know about Australia
        if (countryCode == null) {
            countryCode = "AU";
        }
        // Slap the desired country value back into the model
        model.put("country", countryCode);

        // Open a new connection to the database
        JDBCConnection jdbc = new JDBCConnection();
        // Query the data of states inside the country in question
        ArrayList<ArrayList<String>> table = jdbc.getStatesTableValues(countryCode);
        // Append said data into the model
        model.put("table_vals", table);

        // Get a dictionary of Countries in the database that have subregions/states/provinces
        HashMap<String, String> codes = jdbc.getCountriesWithStates();
        // I'm sure you can guess what this does
        model.put("codes", codes);
        
        // Makes Javalin render the webpage using Thymeleaf
        context.render(TEMPLATE, model);
        


        
    }

}