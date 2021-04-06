import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.*;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This class fetches and handles data from external APIs.
 * The data used from the weather API and event API are used to create recommended event objects.
 *
 * @author Ardian Glamniki, Motaz Kasem
 */

public class FetchAPIData {
    private final static String EVENT_URL = "https://api.helsingborg.se/event/json/wp/v2/event?per_page=100";

    // Path parameters longitude and latitude are set to 14 respective 56 which returns the weather on a single point located in Skane county.
    private final static String SMHI_URL = "http://opendata-download-metfcst.smhi.se/api/category/pmp3g/version/2/geotype/point/lon/14/lat/56/data.json";



    /*
     * Makes an API request to fetch events from the event API.
     * The data returned from the API call is used to create event objects, put them in a list and return the list.
     */
    public ArrayList<Event> consumeEventAPI() {
        ArrayList<Event> events = new ArrayList<>();
        JSONArray eventResponse;
        try {
            eventResponse = Unirest.get(EVENT_URL).asJson().getBody().getArray();
            for (int i = 0; i < eventResponse.length(); i++) {
                JSONObject jsonObject = eventResponse.getJSONObject(i);
                ArrayList<Event> eventArrayList = fromJSONToEvent(jsonObject);
                if(checkForCategories(jsonObject)) {
                    for (int j = 0; j < eventArrayList.size(); j++) {
                        Event event = eventArrayList.get(j);
                        JSONArray categoriesList = jsonObject.getJSONArray("event_categories");
                        String[] categories = new String[categoriesList.length()];
                        for (int k = 0; k < categoriesList.length(); k++) {
                            categories[k] = categoriesList.getString(k);
                        }
                        event.setCategories(categories);
                        events.add(event);
                    }
                } else {
                    for (int j = 0; j < eventArrayList.size(); j++) {
                        events.add(eventArrayList.get(j));
                    }
                }
            }
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return events;
    }

    /*
     * Makes an API request to the weather API to fetch the weather in Skane county.
     * The data returned from the API call is of JSON format and provides the weather forecast ten days ahead.
     * The data is used to create a hashmap that maps day to weather status to check whether or not it will rain that day.
     */
    public ArrayList<Map> consumeWeatherAPI() {
        ArrayList<Map> mapList = new ArrayList<>();
        Weather weatherMap = new Weather();
        JSONArray weatherResponse;

        try {
            weatherResponse = Unirest.get(SMHI_URL).asJson().getBody().getObject().getJSONArray("timeSeries");

            for (int i = 0; i < weatherResponse.length(); i++) {
                JSONObject jsonObject = weatherResponse.getJSONObject(i);
                String date = jsonObject.getString("validTime").substring(0, 10);
                int weather  = jsonObject.getJSONArray("parameters").getJSONObject(18).getJSONArray("values").getInt(0);
                weatherMap.setTimeToWeather(date, weather);
                if(jsonObject.getJSONArray("parameters").getJSONObject(18).getJSONArray("values").getInt(0) > 7) {
                    weatherMap.setDayToRain(date, "rain");
                }
            }
            mapList.add(weatherMap.getTimeToWeather());
            mapList.add(weatherMap.getDayToRain());
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return mapList;
    }

    /*
     * Converts a jsonObject given from the event API into an event object.
     * The method places the objects in a list, then returns it.
     */
    private ArrayList<Event> fromJSONToEvent(JSONObject jsonObject) {
        ArrayList<Event> events = new ArrayList<>();
        if (!jsonObject.isNull("occasions")) {
            JSONArray occasionsArray = jsonObject.getJSONArray("occasions");
            ArrayList<String> occasionList = populateOccasionList(occasionsArray);
            for (int j = 0; j < occasionsArray.length(); j++) {
                if (checkForCities(jsonObject)) {
                    String title = jsonObject.getJSONObject("title").getString("plain_text");
                    String date = occasionsArray.getJSONObject(j).getString("start_date");
                    String location = jsonObject.getJSONObject("location").getString("formatted_address");
                    int id = jsonObject.getInt("id");
                    Event event = new Event(title,date,location,id);
                    event.setOccasions(occasionList);
                    events.add(event);

                } else {
                    String title = jsonObject.getJSONObject("title").getString("plain_text");
                    String date = occasionsArray.getJSONObject(j).getString("start_date");
                    int id = jsonObject.getInt("id");
                    String city = "";
                    Event event = new Event(title, date, city, id);
                    event.setOccasions(occasionList);
                    events.add(event);
                }
            }
        } else {
            if (checkForCities(jsonObject)) {
                String title = jsonObject.getJSONObject("title").getString("plain_text");
                String date = jsonObject.getString("date");
                String city = jsonObject.getJSONObject("location").getString("city") + " " + jsonObject.getJSONObject("location").getString("formatted_address");
                int id = jsonObject.getInt("id");
                events.add(new Event(title,date, city, id));
            } else {
                String title = jsonObject.getJSONObject("title").getString("plain_text");
                String date = jsonObject.getString("date");
                String city = "";
                int id = jsonObject.getInt("id");
                events.add(new Event(title,date, city, id));
            }
        }
        return events;
    }

    /*
     * Creates and returns the recommended events, based on the combined data from the external APIs.
     * Recommended events are based on precipitation and event categories.
     * If it rains then the events that take place inside will be presented.
     * If it doesn't rain then events that take place outside will be presented.
     */
    public ArrayList<Event> getRecommendedEvents() {
        ArrayList<Event> recommendedEvents = new ArrayList<>();
        ArrayList<Map> weatherMap = consumeWeatherAPI();
        HashMap<String, Integer> weatherToDay = (HashMap<String, Integer>) weatherMap.get(0);
        HashMap<String, String> dayToRain = (HashMap<String, String>) weatherMap.get(1);
        ArrayList<Event> events = consumeEventAPI();

        for (int i = 0; i < events.size(); i++) {
            if(weatherToDay.containsKey(events.get(i).getDate().substring(0, 10))) {
                String raining = dayToRain.get(events.get(i).getDate().substring(0, 10));

                String[] categories = events.get(i).getCategories();

                if(categories != null) {
                    if(raining != "rain" && checkForOutsideCategories(events.get(i).getCategories())) {
                        Event event = events.get(i);
                        event.setWeather("good");
                        recommendedEvents.add(event);
                    } else if(raining == "rain" && checkForInsideCategories(events.get(i).getCategories())) {
                        Event event = events.get(i);
                        event.setWeather("bad");
                        recommendedEvents.add(event);
                    }
                }
            }
        }
        return recommendedEvents;
    }
    private ArrayList<String> populateOccasionList(JSONArray occasions) {
        ArrayList<String> occasionsList = new ArrayList<>();
        for (int i = 0; i < occasions.length(); i++) {
            JSONObject occasion = occasions.getJSONObject(i);
            String occasio = occasion.getString("start_date");
            occasionsList.add(occasio);
        }
        return occasionsList;
    }

    // Checks if the event takes place outside based on some predefined common categories.
    private boolean checkForOutsideCategories(String[] categories) {
        List<String> categoriesList = Arrays.asList(categories);
        return categoriesList.contains("Utomhus") || categoriesList.contains("Natur") || categoriesList.contains("Utflykter")
                || categoriesList.contains("Aktiviteter") && !categoriesList.contains("Digitalt") && !categoriesList.contains("Spel och e-sport")
                && !categoriesList.contains("Spel och teknik") && !categoriesList.contains("Film") && !categoriesList.contains("Konst");
    }

    // Checks if the event takes place inside based on some predefined common categories.
    private boolean checkForInsideCategories(String[] categories) {
        List<String> categoriesList = Arrays.asList(categories);
        return categoriesList.contains("Inomhus") || categoriesList.contains("Konsert") || categoriesList.contains("Digitalt")
                || categoriesList.contains("Digitalt skapande") || categoriesList.contains("Spel och e-sport") || categoriesList.contains("Konst")
                || categoriesList.contains("Film");
    }

    private boolean checkForCategories(JSONObject obj) {
        return !obj.isNull("event_categories");
    }
    private boolean checkForCities(JSONObject obj) {
        return !obj.isNull("location") && !obj.getJSONObject("location").isNull("city");
    }

}
