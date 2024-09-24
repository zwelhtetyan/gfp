import cats.effect.IO
import cats.implicits.*
import cats.effect.unsafe.implicits.global

import scala.util.Try

object Learn {
  // unsafe api call function
  def calendarEntriesApiCall(name: String): List[MeetingTime] = {
    import scala.jdk.CollectionConverters._
    ch08_SchedulingMeetingsAPI.calendarEntriesApiCall(name).asScala.toList
  }
  
  def createMeetingApiCall(names: List[String], meetingTime: MeetingTime): Unit = {
    import scala.jdk.CollectionConverters._
    ch08_SchedulingMeetingsAPI.createMeetingApiCall(names.asJava, meetingTime)
  }

  def calendarEntries(name: String): IO[List[MeetingTime]] = {
    IO.delay(calendarEntriesApiCall(name))
  }

  def createMeeting(names: List[String], meetingTime: MeetingTime): IO[Unit] = {
    IO.delay(createMeetingApiCall(names, meetingTime))
  }

  // implementation
  case class HourBetween(start: Int, end: Int)

  def availableSlotsForGivenHours(lengthHour: Int, between: HourBetween): List[MeetingTime] = {
    List.range(between.start, between.end - lengthHour + 1).map(start => MeetingTime(start, start + lengthHour))
  }

  def availableMeetingsForEachAttendee(attendees: List[String]): IO[List[MeetingTime]] = {
    attendees.map(attendee => retry(calendarEntries(attendee), 11)).sequence.map(_.flatten)
  }

  def possibleMeetings(existingMeetings: List[MeetingTime], between: HourBetween, lengthHour: Int): List[MeetingTime] = {
    val availableSlots = availableSlotsForGivenHours(lengthHour, between)

    availableSlots.filter(slot => existingMeetings.forall(m => !(m.endHour > slot.startHour && slot.endHour > m.startHour)))
  }

  def schedule(attendees: List[String], lengthHour: Int): IO[Option[MeetingTime]] = {
    for {
      existingMeetings <- availableMeetingsForEachAttendee(attendees)
      meetings = possibleMeetings(existingMeetings, HourBetween(8, 16), lengthHour)
      meeting = meetings.headOption
      _ = meeting match {
        case Some(meeting) => createMeeting(attendees, meeting)
        case None => IO.unit
      }
    } yield meeting
  }

  def retry[A](action: IO[A], maxRetries: Int): IO[A] = {
    List
      .range(0, maxRetries)
      .map(_ => action)
      .foldLeft(action)((program, retryAction) =>program.orElse(retryAction))
  }

  // val program: IO[Option[MeetingTime]] = schedule(List("Alice", "Bob"), 1)
  // program.unsafeRunSync()
}

