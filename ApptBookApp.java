package edu.uwm.cs351;

import java.util.Calendar;
import java.util.Iterator;
import java.util.TimeZone;

/**
 * The main class for the ApptBook application. Reads appointments from a file
 * into an ApptBook object, and then checks for any conflicts among the
 * appointments.
 */
public class ApptBookApp {
	public static void main(String args[]) {
		NewApptBook book = new NewApptBook();

		// Read appointments from a file into the ApptBook object
		ApptBookIO.read(book);

		// Get the current time
		Time now = new Time();
		Calendar cal = now.asCalendar();
		cal.setTimeZone(TimeZone.getDefault());
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0); // start of day: 0:00 (midnight)
		Time startOfDay = new Time(cal);

		// Create an appointment representing the first possible time slot of the day
		Appointment firstPossible = new Appointment(new Period(startOfDay, Duration.MILLISECOND), "");

		Appointment prev = null;
		int conflictsFound = 0;

		// Iterate through the appointments starting from the first possible time slot
		Iterator<Appointment> it = book.iterator(firstPossible);
		while (it.hasNext()) {
			Appointment next = it.next();

			// Check if the appointment is beyond the current day
			if (next.getTime().getStart().difference(startOfDay).compareTo(Duration.DAY) >= 0)
				break;

			if (prev != null) {
				// Check for conflicts with the previous appointment
				if (prev.getTime().overlap(next.getTime())) {
					System.out.println("Conflict:");
					System.out.println("    " + prev);
					System.out.println("    " + next);
					++conflictsFound;
				}
			}

			prev = next;
		}

		System.out.println("Conflicts found: " + conflictsFound);
	}
}
