package edu.uwm.cs351;

import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * A class to create appointment books for use with the GUI. A more realistic
 * version would read/write appointments from files.
 */
public class ApptBookIO {

	private static Appointment createAppointment(Calendar start, Duration len, String topic) {
		return new Appointment(new Period(new Time(start), len), topic);
	}

	private static void insertAppointment(NewApptBook book, Calendar start, Duration len, String topic) {
		book.add(createAppointment(start, len, topic));
	}

	/**
	 * Read a stored appointment book. Actually, it generates new appointments.
	 * 
	 * @param book the appointment book to read elements into
	 * @exception IOException if there is a problem reading
	 */
	public static void read(NewApptBook book) {
		Time now = new Time();
		Calendar cal = now.asCalendar();
		cal.setTimeZone(TimeZone.getDefault());
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		cal.set(Calendar.HOUR_OF_DAY, 10);
		insertAppointment(book, cal, Duration.MINUTE.scale(50), "class");
		cal.set(Calendar.HOUR_OF_DAY, 11);
		insertAppointment(book, cal, Duration.HOUR, "study");
		cal.set(Calendar.HOUR_OF_DAY, 12);
		insertAppointment(book, cal, Duration.HOUR, "lunch");
		cal.set(Calendar.MINUTE, 30);
		insertAppointment(book, cal, Duration.HOUR.scale(5), "work");
		cal.set(Calendar.HOUR_OF_DAY, 17);
		cal.set(Calendar.MINUTE, 0);
		insertAppointment(book, cal, Duration.HOUR.scale(2), "family");
		cal.set(Calendar.HOUR_OF_DAY, 22);
		cal.set(Calendar.MINUTE, 0);
		insertAppointment(book, cal, Duration.HOUR.scale(8), "sleep");

		// tomorrow
		cal.add(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 7);
		insertAppointment(book, cal, Duration.HOUR.scale(0.5), "exercise");
		cal.set(Calendar.HOUR_OF_DAY, 10);
		insertAppointment(book, cal, Duration.HOUR.scale(2), "study");
		cal.set(Calendar.HOUR_OF_DAY, 11);
		cal.set(Calendar.MINUTE, 30);
		insertAppointment(book, cal, Duration.HOUR, "meet Pat over lunch");

		// yesterday
		cal.add(Calendar.DAY_OF_MONTH, -2);
		cal.set(Calendar.HOUR_OF_DAY, 8);
		for (int i = 0; i < 16; ++i) {
			insertAppointment(book, cal, Duration.HOUR, "artificial #" + i);
			cal.add(Calendar.MINUTE, 15);
		}
	}

	/**
	 * Write an appointment book for use later. However, it is not able to write
	 * anything.
	 * 
	 * @param book the appointment book to write
	 * @exception IOException if there is a problem with writing
	 */
	public static void write(NewApptBook book) throws IOException {
		// XXX Do nothing
		throw new IOException("write is not implemented");
	}
}
