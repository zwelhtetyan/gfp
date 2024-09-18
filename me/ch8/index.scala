import cats.effect.IO
import cats.implicits.*
import cats.effect.unsafe.implicits.global

import scala.util.Try

case class MeetingTime(startHour: Int, endHour: Int)

object ch08_SchedulingMeetings {
  // unsafe api call function
  def calendarEntriesApiCall(name: String): List[MeetingTime] = {
    import scala.jdk.CollectionConverters._
    ch08_SchedulingMeetingsAPI.calendarEntriesApiCall(name).asScala.toList
  }

  def createMeetingApiCall(names: List[String], meetingTime: MeetingTime): Unit = {
    import scala.jdk.CollectionConverters._
    ch08_SchedulingMeetingsAPI.createMeetingApiCall(names.asJava, meetingTime)
  }

  // pure IO function
  def calendarEntries(name: String): IO[List[MeetingTime]] = {
    IO.delay(calendarEntriesApiCall(name))
  }

  def createMeeting(names: List[String], meetingTime: MeetingTime): IO[Unit] = {
    IO.delay(createMeetingApiCall(names, meetingTime))
  }

  def scheduledMeetings(persons: List[String]): IO[List[MeetingTime]] = {
    persons.map(p => retry(calendarEntries(p), 10)).sequence.map(_.flatten)
  }

  def possibleMeetings(existingMeetings: List[MeetingTime], startHour: Int, endHour: Int, lengthHours: Int): List[MeetingTime] = {
    val slots = List.range(startHour, endHour - lengthHours + 1).map(start => MeetingTime(start, start + lengthHours))

    slots.filter(slot => existingMeetings.forall(m => !(m.endHour > slot.startHour && slot.endHour > m.startHour)))
  }

  def schedule(persons: List[String], lengthHours: Int): IO[Option[MeetingTime]] = {
    for {
      existingMeetings <- scheduledMeetings(persons)
      possibleMeetings = possibleMeetings(existingMeetings, 8, 16, lengthHours)
      meeting = possibleMeetings.headOption
      _ <- meeting match {
        case Some(m) => createMeeting(List(persons), m)
        case None => IO.unit // same as IO.pure(())
      }
    } yield meeting
  }

  def retry[A](action: IO[A], maxRetries: Int): IO[A] = {
    List.range(0, maxRetries).map(_ => action).foldLeft(action)(program, retryAction) = {
      program.orElse(retryAction)
    }
  }

  val program: IO[Option[MeetingTime]] = schedule("Alice", "Bob", 1)
  program.unsafeRunSync()
}

