import com.google.gson.Gson;
import java.util.*;
import static spark.Spark.*;

/**
 * This class starts the server and handles the API requests to our resources.
 *
 * @author Ardian Glamniki, Motaz Kasem
 */

public class ServerRunner {

    public static void main(String[] args) {
        Gson gson = new Gson();
        Storage storage = new Storage();
        port(4999);

        /*
        * Since CORS is a problem when running the server on the same domain as the client, the options needs to be tweaked.
        * The following code is necessary to enable the CORS functionality
        */
        options("/*",
                (request, response) -> {
                    String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");

                    if (accessControlRequestHeaders != null) {
                        response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
                    }

                    String accessControlRequestMethod = request.headers("Access-Control-Request-Method");

                    if (accessControlRequestMethod != null) {
                        response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
                    }
                    return "OK";
                }
        );

        before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));

        /* Endpoint to get the recommended events, made from the mashup of external APIs.
         * Query parameters startDate and endDate can be used to filter the results within two dates.
         */
        get("/api/v1/events", (req, res) -> {
            storage.updateStorage();
            ArrayList<Event> events = storage.getRecommendedEvents();
            res.type("application/json");

            if(events == null) {
                res.status(500);
                res.body("500 Internal Server Error");
            }

            if(req.queryParams().isEmpty()) {
                res.body(gson.toJson(events));
                res.status(200);
            } else {
                String startDate = req.queryParams("startDate");
                String endDate = req.queryParams("endDate");
                if(startDate != null && endDate != null) {
                    ArrayList<Event> eventsBetweenDates = storage.getEventsByDate(startDate, endDate);
                    if(eventsBetweenDates.isEmpty()) {
                        res.status(404); // not found
                        res.body("404");
                    } else {
                        res.body(gson.toJson(eventsBetweenDates));
                        res.status(200);
                    }
                } else {
                    res.status(400);
                    res.body("400"); // bad request
                }
            }
            return res.body();
        });


        // Endpoint to get an event, given an id.
        get("/api/v1/events/:id", (req, res) -> {
            storage.updateStorage();
            int id = Integer.parseInt(req.params("id"));

            Event event = storage.getEventByID(id);

            if(event == null) {
                res.status(404);
                res.body("404");
            }
            res.type("application/json");
            res.body(gson.toJson(event));

            return res.body();
        });
    }
}
