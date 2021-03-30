
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import spark.ModelAndView;
import spark.Request;

import java.util.*;

import static spark.Spark.*;
import spark.template.pebble.PebbleTemplateEngine;

public class ServerRunner {

    public static void main(String[] args) {
        Gson gson = new Gson();
        port(4999);

        // Hämtar all data
        get("/", (req, res) -> {
            return "Hello world";
        });
    }
}
