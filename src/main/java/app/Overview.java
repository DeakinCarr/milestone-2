package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.javalin.http.Context;
import io.javalin.http.Handler;

public class Overview implements Handler{
    // URL of this page relative to http://localhost:7000/
    public static final String URL = "/overview";

    // Name of the Thymeleaf HTML template page in the resources folder
    private static final String TEMPLATE = ("overview.html");

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
        String countryCode2 = context.formParam("country2");
        if (countryCode2 == null) {
            countryCode2 = "AU";
        }
        
        model.put("country", countryCode);
        model.put("country2", countryCode2);

        JDBCConnection jdbc = new JDBCConnection();

        Map<String, Object> cumData = jdbc.getCumulative(countryCode);
        Map<String, Object> mostRecent = jdbc.getMostRecent(countryCode);
        Map<String, Object> cumData2 = jdbc.getCumulative(countryCode2);
        Map<String, Object> mostRecent2 = jdbc.getMostRecent(countryCode2);

        Map<String, Object> wwCumData = jdbc.getCumulative();
        ArrayList<ArrayList<String>> table = jdbc.getTableValues();
        ArrayList<Map<String, Object>> heatMap = jdbc.getHeatMap();

        for (int i = 0; i < cumData.size(); i++){
            model.put(cumData.keySet().toArray()[i].toString(), cumData.values().toArray()[i]);
        }
        for (int i = 0; i < mostRecent.size(); i++){
            model.put(mostRecent.keySet().toArray()[i].toString(), mostRecent.values().toArray()[i]);
        }

        for (int i = 0; i < cumData2.size(); i++){
            model.put(cumData2.keySet().toArray()[i].toString() + "2", cumData2.values().toArray()[i]);
        }
        for (int i = 0; i < mostRecent2.size(); i++){
            model.put(mostRecent2.keySet().toArray()[i].toString() + "2", mostRecent2.values().toArray()[i]);
        }

        for (int i = 0; i < wwCumData.size(); i++){
            model.put(wwCumData.keySet().toArray()[i].toString(), wwCumData.values().toArray()[i]);
        }

        model.put("table_vals", table);
        model.put("heatMap", heatMap);
        
        // DO NOT MODIFY THIS
        // Makes Javalin render the webpage using Thymeleaf
        context.render(TEMPLATE, model);
        


        
    }

}

/*For populating the table

SELECT co.Country, da.Date, Cases, Deaths
FROM Statistics st
LEFT JOIN Location lo ON
    st.Location = lo.ID    
LEFT JOIN Countries co ON
    lo.Country = co.ID
LEFT JOIN Dates da ON
    da.ID = st.Date
WHERE Deaths >= 0 AND Cases >= 0
GROUP BY co.Country, da.Date
ORDER BY da.ID DESC
LIMIT 189;
*/