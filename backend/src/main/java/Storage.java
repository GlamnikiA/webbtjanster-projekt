import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * This class is used to store the recommended event objects.
 *
 * @author Ardian Glamniki, Motaz Kasem
 */
public class Storage {
    private FetchAPIData apiFetcher;
    private ArrayList<Event> recommendedEvents;

    public Storage() {
        apiFetcher = new FetchAPIData();
    }

    public void updateStorage() {
        recommendedEvents = apiFetcher.getRecommendedEvents();
    }

    public ArrayList<Event> getRecommendedEvents() {
        return recommendedEvents;
    }


    // This method is called to check if there are any recommended events between two defined dates.
   public ArrayList<Event> getEventsByDate(String startDate, String endDate) {
       ArrayList<Event> eventsBetweenRange = new ArrayList<>();
       eventsBetweenRange = checkEventsDate(recommendedEvents, startDate, endDate);
       return eventsBetweenRange;
   }

   /*
    * Checks for events that take place between two defined dates.
    * If the events satisfy the conditions then they will be placed in a list then returned.
    */
   private ArrayList<Event> checkEventsDate(ArrayList<Event> eventList, String startDate, String endDate) {
        ArrayList<Event> eventsBetweenRange = new ArrayList<>();
       for (int i = 0; i < eventList.size(); i++) {
           try {
               Date date = new SimpleDateFormat("yyyy-MM-dd").parse(eventList.get(i).getDate());
               Date start = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
               Date end = new SimpleDateFormat("yyyy-MM-dd").parse(endDate);

               if(date.compareTo(start) >= 0 &&  date.compareTo(end) <= 0) {
                   eventsBetweenRange.add(eventList.get(i));
               }
           } catch (ParseException e) {
               e.printStackTrace();
           }
       }
       return eventsBetweenRange;
   }
   // Return an event based on the id given by the client.
   public Event getEventByID(int id) {
        Event event = new Event();

       for (int i = 0; i < recommendedEvents.size(); i++) {
           if(recommendedEvents.get(i).getId() == id) {
               event.setTitle(recommendedEvents.get(i).getTitle());
               event.setId(recommendedEvents.get(i).getId());
               event.setLocation(recommendedEvents.get(i).getLocation());
               event.setOccasions(recommendedEvents.get(i).getOccasions());
           }
       }
       return event;
   }
}
