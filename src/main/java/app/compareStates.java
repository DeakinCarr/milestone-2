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
        // In this example the model will be filled with:
        //  - Title to give to the h1 tag
        //  - Array list of all movies for the UL element
        Map<String, Object> model = new HashMap<String, Object>();

        String countryCode = context.formParam("country");
        if (countryCode == null) {
            countryCode = "AU";
        }
        model.put("country", countryCode);

        JDBCConnection jdbc = new JDBCConnection();
        ArrayList<ArrayList<String>> table = jdbc.getStatesTableValues(countryCode);
        HashMap<String, String> codes = jdbc.getCountriesWithStates();
        System.out.print(codes);
        model.put("table_vals", table);
        model.put("codes", codes);
        


        // DO NOT MODIFY THIS
        // Makes Javalin render the webpage using Thymeleaf
        context.render(TEMPLATE, model);
        


        
    }

}