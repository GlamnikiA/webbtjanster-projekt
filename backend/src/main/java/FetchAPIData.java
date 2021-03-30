import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;

public class FetchAPIData {
    private final static String EVENT_URL = "https://api.helsingborg.se/event/json/wp/v2/event?per_page=100";
    private final static String SMHI_URL = "http://opendata-download-metfcst.smhi.se/api/category/pmp3g/version/2/geotype/point/lon/14/lat/56/data.json";



    public ArrayList<Event> consumeEventAPI() {
        ArrayList<Event> events = new ArrayList<>();
        JSONArray eventResponse;
        try {
            eventResponse = Unirest.get(EVENT_URL).asJson().getBody().getArray();
            for (int i = 0; i < eventResponse.length(); i++) {
                JSONObject jsonObject = eventResponse.getJSONObject(i);
                ArrayList<Event> eventArrayList = fromJSONToEvent(jsonObject);
                for (int j = 0; j < eventArrayList.size(); j++) {
                    events.add(eventArrayList.get(j));
                }
            }
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return events;
    }

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
                if(jsonObject.getJSONArray("parameters").getJSONObject(18).getJSONArray("values").getInt(0) >= 7) {
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

    private ArrayList<Event> fromJSONToEvent(JSONObject jsonObject) {
        ArrayList<Event> events = new ArrayList<>();
        if (!jsonObject.isNull("occasions")) {
            JSONArray occasionsArray = jsonObject.getJSONArray("occasions");
            for (int j = 0; j < occasionsArray.length(); j++) {
                if (!jsonObject.isNull("location") && (!jsonObject.getJSONObject("location").isNull("city"))) {
                    String title = jsonObject.getJSONObject("title").getString("plain_text");
                    String date = occasionsArray.getJSONObject(j).getString("start_date");
                    String city = jsonObject.getJSONObject("location").getString("city");
                    events.add(new Event(title,date, city));

                    System.out.println("date: " + date + " title "
                            + title + " location "
                            + city);
                } else {
                    String title = jsonObject.getJSONObject("title").getString("plain_text");
                    String date = occasionsArray.getJSONObject(j).getString("start_date");
                    String city = "";
                    events.add(new Event(title,date, city));
                }
            }
        } else {
            if (!jsonObject.isNull("location") && !jsonObject.getJSONObject("location").isNull("city")) {
                String title = jsonObject.getJSONObject("title").getString("plain_text");
                String date = jsonObject.getString("date");
                String city = jsonObject.getJSONObject("location").getString("city");
                events.add(new Event(title,date, city));
            } else {
                String title = jsonObject.getJSONObject("title").getString("plain_text");
                String date = jsonObject.getString("date");
                String city = "";
                events.add(new Event(title,date, city));
            }
        }
        return events;
    }
    public static void main(String[] args) {
        FetchAPIData test = new FetchAPIData();

        ArrayList<Event> events = test.consumeEventAPI();

        for (int i = 0; i < events.size(); i++) {
            System.out.println(events.get(i).getTitle());
        }
        ArrayList<Map> map = test.consumeWeatherAPI();
        System.out.println(map.get(1).get("2021-04-02"));
    }
}
