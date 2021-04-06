import java.util.ArrayList;

/**
 * This class represents a recommended event
 *
 * @author Ardian Glamniki, Motaz Kasem
 */
public class Event {
    private String title;
    private String date;
    private String location;
    private String weather;
    private String[] categories;
    private int id;
    private ArrayList<String> occasions;

    public Event() {

    }

    public Event(String title, String date, String location, int id) {
        this.title = title;
        this.date = date;
        this.location = location;
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }
    public String getWeather() {
        return weather;
    }
    public void setCategories(String[] categories) {
        this.categories = categories;
    }

    public String[] getCategories() {
        return categories;
    }

    public int getId() {
        return id;
    }

    public ArrayList<String> getOccasions() {
        return occasions;
    }

    public void setOccasions(ArrayList<String> occasions) {
        this.occasions = occasions;
    }
}
