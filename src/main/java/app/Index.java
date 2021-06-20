package app;

import java.util.HashMap;
import java.util.Map;

import io.javalin.http.Context;
import io.javalin.http.Handler;

public class Index implements Handler {

    // URL of this page relative to http://localhost:7000/
    public static final String URL = "/";

    // Name of the Thymeleaf HTML template page in the resources folder
    private static final String TEMPLATE = ("index.html");

    @Override
    public void handle(Context context) throws Exception {
        // The model of data to provide to Thymeleaf.
        Map<String, Object> model = new HashMap<String, Object>();

        // Establish a new connection to the database
        JDBCConnection jdbc = new JDBCConnection();

        // Query the relavent data incl. Total Cases, Total Deaths, and New Cases
        Map<String, String> indexData = jdbc.getIndexPageInfo();

        // Iterate through each of the keys in the indexData map, and add it to the model with its 
        // associated value
        for (int i = 0; i < indexData.size(); i++){
            model.put(
                indexData.keySet().toArray()[i].toString(), 
                indexData.values().toArray()[i]
            );
        }

        // Beep-Boop render man
        context.render(TEMPLATE, model);
    }

}
