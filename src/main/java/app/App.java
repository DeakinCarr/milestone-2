package app;

import io.javalin.Javalin;
import io.javalin.core.util.RouteOverviewPlugin;


/**
 * Main Application Class.
 * <p>
 * Running this class as regular java application will start the 
 * Javalin HTTP Server and our web application.
 *
 * @author Timothy Wiley, 2021. email: timothy.wiley@rmit.edu.au
 * @author Santha Sumanasekara, 2021. email: santha.sumanasekara@rmit.edu.au
 */
public class App {

    public static final int         JAVALIN_PORT    = 7000;
    public static final String      ASSET_DIR         = "/";

    public static void main(String[] args) {
        // Create our HTTP server and listen in port 7000
        Javalin app = Javalin.create(config -> {
            config.registerPlugin(new RouteOverviewPlugin("/help/routes"));
            
            // Uncomment this if you have files in the CSS Directory
            config.addStaticFiles(ASSET_DIR);

        }).start(JAVALIN_PORT);


        // Configure Web Routes
        configureRoutes(app);
    }

    public static void configureRoutes(Javalin app) {
        // Note in this example we must add Movies Type as a GET and a POST!
        
        // ADD ALL OF YOUR WEBPAGES HERE
        app.get(Index.URL, new Index());
        app.get(Overview.URL, new Overview());
        app.get(BigMap.URL, new BigMap());
        app.get(Facts.URL, new Facts());
        app.get(compareStates.URL, new compareStates());
        app.get(ViewSimilar.URL, new ViewSimilar());
        app.get(ViewPopulation.URL, new ViewPopulation());

        // POST pages can accept form data
        app.post(Overview.URL, new Overview());
        app.post(compareStates.URL, new compareStates());
        app.post(ViewSimilar.URL, new ViewSimilar());
        app.post(ViewPopulation.URL, new ViewPopulation());
    }

}
