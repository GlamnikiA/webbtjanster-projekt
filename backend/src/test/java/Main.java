import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {
    public static void main(String[] args) throws ParseException {
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2021-04-09");
        String d1 = "2021-04-05";
        String d2 = "2021-04-08";
        Date start = new SimpleDateFormat("yyyy-MM-dd").parse(d1);
        Date end = new SimpleDateFormat("yyyy-MM-dd").parse(d2);

        if(date.compareTo(start) >= 0 &&  date.compareTo(end) <= 0) {
            System.out.println(true);
        } else {
            System.out.println(false);
        }
    }
}
