
import java.util.HashMap;

public class Weather {
    private HashMap<String, Integer> timeToWeather = new HashMap<>();
    private HashMap<String, String> dayToRain = new HashMap<>();

    public void setTimeToWeather(String time, int weather) {
        timeToWeather.put(time, weather);
    }
    public  void setDayToRain(String day, String precipitation) {
        dayToRain.put(day, precipitation);
    }

    public HashMap<String, Integer> getTimeToWeather() {
        return timeToWeather;
    }

    public HashMap<String, String> getDayToRain() {
        return dayToRain;
    }
}
