package app;

import java.util.HashMap;
import java.util.Map;

import io.javalin.http.Context;
import io.javalin.http.Handler;


public class Facts implements Handler {

    // URL of this page relative to http://localhost:7000/
    public static final String URL = "/facts";

    // Name of the Thymeleaf HTML template page in the resources folder
    private static final String TEMPLATE = ("Facts.html");

    @Override
    public void handle(Context context) throws Exception {
        // The model of data to provide to Thymeleaf.
        Map<String, Object> model = new HashMap<String, Object>();

        // Establish a new connection to the database
        JDBCConnection2 jdbc2 = new JDBCConnection2();
        
        // Get the total infections and add it to the model
        int infectionsTot = jdbc2.getInfectionsTot();
        model.put("infectionsTot", infectionsTot);

        // Get the total deaths and add it to the model
        int deathsTot = jdbc2.getDeathsTot();
        model.put("deathsTot", deathsTot);
    
        // Makes Javalin render the webpage using Thymeleaf
        context.render(TEMPLATE, model);
    }

}