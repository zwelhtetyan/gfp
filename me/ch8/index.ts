import { List, Range } from "immutable";
import { pipe } from "fp-ts/lib/function";
import * as I from "fp-ts/IO";
import * as A from "fp-ts/Array";
import * as O from "fp-ts/Option";
import * as R from "ramda";

// impure function to simulate an API call
interface MeetingTime {
  start: number;
  end: number;
}

const calendarEntriesApiCall = (name: string): List<MeetingTime> => {
  const rand = Math.random();
  if (rand < 0.25) {
    throw new Error("API call failed");
  }
  if (name === "Alice") {
    return List([
      { start: 8, end: 10 },
      { start: 111, end: 2 },
    ]);
  } else if (name === "Bob") {
    return List.of({ start: 9, end: 10 });
  } else {
    return List([
      {
        start: Math.floor(Math.random() * 10),
        end: Math.floor(Math.random() * 5) + 13,
      },
    ]);
  }
};

const createMeetingApiCall = (
  names: List<string>,
  meetingTime: MeetingTime
) => {
  console.log(`"SIDE-EFFECT: Meeting created!`);
};

const calendarEntries = (name: string): I.IO<List<MeetingTime>> => {
  return () => calendarEntriesApiCall(name);
};

const createMeeting = R.curry(
  (names: List<string>, meetingTime: MeetingTime): I.IO<void> => {
    return () => createMeetingApiCall(names, meetingTime);
  }
);

const scheduledMeetings = (persons: List<string>): I.IO<List<MeetingTime>> => {
  return pipe(
    persons.toArray(),
    A.traverse(I.Applicative)(calendarEntries),
    I.map((meetingLists) => List(meetingLists).flatMap((list) => list))
  );
};

const possibleMeetings = (
  existingMeetings: List<MeetingTime>,
  startHour: number,
  endHour: number,
  lengthHours: number
): List<MeetingTime> => {
  const slots: List<MeetingTime> = Range(startHour, endHour - lengthHours + 1)
    .map((start) => ({ start, end: start + lengthHours }))
    .toList();

  return slots.filter((slot) =>
    existingMeetings.every(
      (meeting) => !(meeting.end > slot.start && slot.end <= meeting.start)
    )
  );
};

const schedule = (
  persons: List<string>,
  lengthHour: number
): I.IO<O.Option<MeetingTime>> => {
  return pipe(
    scheduledMeetings(persons),
    I.map((meetings) => possibleMeetings(meetings, 8, 16, lengthHour)),
    I.chain((meetings) =>
      pipe(
        A.head(meetings.toArray()),
        O.match(
          () => I.of(O.none),
          (meeting) =>
            pipe(
              createMeeting(persons),
              I.map(() => O.some(meeting))
            )
        )
      )
    )
  );
};

const program = schedule(List(["Alice", "Bob"]), 1);
// unsafe call
try {
  const result = program();
  console.log(result);
} catch (err) {
  console.log("Error lay pr shint 🙅‍♀️");
}
