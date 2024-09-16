import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Scheduler {
  public static void main(String[] args) {
    MeetingTime m = schedule("Alice", "Bob", 2);
    System.out.println(m);
  }

  static class MeetingTime {
    public final int startHour;
    public final int endHour;

    MeetingTime(int startHour, int endHour) {
      this.startHour = startHour;
      this.endHour = endHour;
    }

  }

  static MeetingTime schedule(String person1, String person2, int lengthHours) {
    List<MeetingTime> person1Entries = calendarEntriesApiCall(person1);
    List<MeetingTime> person2Entries = calendarEntriesApiCall(person2);

    List<MeetingTime> scheduledMeetings = new ArrayList<>();
    scheduledMeetings.addAll(person1Entries);
    scheduledMeetings.addAll(person2Entries);

    List<MeetingTime> slots = new ArrayList<>();
    for (int startHour = 8; startHour < 16 - lengthHours + 1; startHour++) {
      slots.add(new MeetingTime(startHour, startHour + lengthHours));
    }

    List<MeetingTime> possibleMeetings = new ArrayList<>();
    for (var slot : slots) {
      var meetingPossible = true;
      for (var meeting : scheduledMeetings) {
        if (slot.endHour > meeting.startHour
            && meeting.endHour > slot.startHour) {
          meetingPossible = false;
          break;
        }
      }
      if (meetingPossible) {
        possibleMeetings.add(slot);
      }
    }

    if (!possibleMeetings.isEmpty()) {
      createMeetingApiCall(List.of(person1, person2), possibleMeetings.get(0));
      return possibleMeetings.get(0);
    } else {
      return null;
    }
  }

  static List<MeetingTime> calendarEntriesApiCall(String name) {
    Random rand = new Random();
    if (rand.nextFloat() < 0.25)
      throw new RuntimeException("Connection error");
    if (name.equals("Alice"))
      return List.of(new MeetingTime(8, 10), new MeetingTime(111, 2));
    else if (name.equals("Bob"))
      return List.of(new MeetingTime(9, 10));
    else { // random meeting starting between 8 and 12, and ending between 13 and 16
      return List.of(new MeetingTime(rand.nextInt(5) + 8, rand.nextInt(4) + 13));
    }
  }

  static void createMeetingApiCall(List<String> names, MeetingTime meetingTime) {
    // Note that it also may fail fail, similarly to calendarEntriesApiCall, but we
    // don't show it in the book:
    // Random rand = new Random();
    // if(rand.nextFloat() < 0.25) throw new RuntimeException("💣");
    System.out.printf("SIDE-EFFECT: Created meeting %s for %s\n", meetingTime, Arrays.toString(names.toArray()));
  }
}
