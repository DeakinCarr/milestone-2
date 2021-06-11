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
public class Facts implements Handler {

    // URL of this page relative to http://localhost:7000/
    public static final String URL = "/facts";

    // Name of the Thymeleaf HTML template page in the resources folder
    private static final String TEMPLATE = ("Facts.html");

    @Override
    public void handle(Context context) throws Exception {
        // The model of data to provide to Thymeleaf.
        // In this example the model will remain empty
        Map<String, Object> model = new HashMap<String, Object>();

        
        JDBCConnection2 jdbc2 = new JDBCConnection2();
        
        int infectionsTot = jdbc2.getInfectionsTot();
        model.put("infectionsTot", infectionsTot);
        int deathsTot = jdbc2.getDeathsTot();
        model.put("deathsTot", deathsTot);
        

        // DO NOT MODIFY THIS
        // Makes Javalin render the webpage using Thymeleaf
        context.render(TEMPLATE, model);
    }

}