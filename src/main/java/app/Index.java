package app;

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
public class Index implements Handler {

    // URL of this page relative to http://localhost:7000/
    public static final String URL = "/";

    // Name of the Thymeleaf HTML template page in the resources folder
    private static final String TEMPLATE = ("index.html");

    @Override
    public void handle(Context context) throws Exception {
        // The model of data to provide to Thymeleaf.
        // In this example the model will remain empty
        Map<String, Object> model = new HashMap<String, Object>();

        JDBCConnection jdbc = new JDBCConnection();
        Map<String, Object> wwCumData = jdbc.getCumulative();

        String query = "SELECT * FROM getMostRecent ORDER BY NewCases Desc LIMIT 1;";

        for (int i = 0; i < wwCumData.size(); i++){
            model.put(wwCumData.keySet().toArray()[i].toString(), wwCumData.values().toArray()[i]);
        }

        context.render(TEMPLATE, model);
    }

}
