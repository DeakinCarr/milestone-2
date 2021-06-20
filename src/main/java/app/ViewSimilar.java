package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.javalin.http.Context;
import io.javalin.http.Handler;

/**
 * Example Index HTML class using Javalin
 * <p>
 * Generate a static HTML page using Javalin
 * by writing the raw HTML into a Java String object
 *
 * @author Timothy Wiley, 2021. email: timothy.wiley@rmit.edu.au
 * @author Santha Sumanasekara, 2021. email: santha.sumanasekara@rmit.edu.au
 */
public class ViewSimilar implements Handler {

    // URL of this page relative to http://localhost:7000/
    public static final String URL = "/similar";

    // Name of the Thymeleaf HTML template page in the resources folder
    private static final String TEMPLATE = ("ViewSimilar.html");

    @Override
    public void handle(Context context) throws Exception {
        // The model of data to provide to Thymeleaf.
        // In this example the model will remain empty
        Map<String, Object> model = new HashMap<String, Object>();

        
        String countryCode = context.formParam("country");
        if (countryCode == null) {
            countryCode = "AU";
        }


        
        model.put("country", countryCode);
        JDBCConnection jdbc = new JDBCConnection();
        ArrayList<ArrayList<String>> table = jdbc.getStatesTableValues(countryCode);
        HashMap<String, String> codes = jdbc.getStatesCodes();
        System.out.print(codes);
        model.put("table_vals", table);
        model.put("codes", codes);
        
        

        // DO NOT MODIFY THIS
        // Makes Javalin render the webpage using Thymeleaf
        context.render(TEMPLATE, model);
    }

}