/**
 * 
 */
package com.awpl.sandbox.appointmentmanager;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.dozer.CsvDozerBeanWriter;
import org.supercsv.prefs.CsvPreference;

/**
 * @author Yogish Shenoy
 *
 */
public class AppointmentManager
{

	private static final Logger LOG = Logger.getGlobal();
	private int tables;
	private final String[] FIELD_MAPPING_FOR_OUTPUT_FILE = new String[]
	{ "meetingSlot.table", "meetingSlot.slot", "appointmentPref.owner", "appointmentPref.meetingPref" };
	private int slots;
	private Map<String, List<AppointmentPreference>> appointmentPrefs;
	List<MeetingSlot> meetingSlots;
	private List<Appointment> appointments;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		if (args.length >= 2)
		{
			AppointmentManager appointmentManager = new AppointmentManager();
			final String appointmentPrefsFile = args[0];
			appointmentManager.tables = Integer.parseInt(args[1]);
			appointmentManager.slots = Integer.parseInt(args[2]);
			appointmentManager.readAppointmentsWithCsvBeanReader(appointmentPrefsFile);
			appointmentManager.buildMeetingSlots();

			long seed = System.nanoTime();

			// Collections.shuffle(appointmentManager.appointmentPrefs, new
			// Random(seed));

			// Collections.shuffle(appointmentManager.meetingSlots, new
			// Random(seed));
			appointmentManager.meetingSlots.sort((s1, s2) -> (s1.slot.compareTo(s2.slot)));

			appointmentManager.createAppointments();
			appointmentManager.writeAppointmentsToFile();
		}
		else
		{
			LOG.log(Level.SEVERE, "Please enter the following\n1. Full path to the appointments CSV file\n2. Total number of tables\n3. Number of slots per table");
		}

	}

	private void readAppointmentsWithCsvBeanReader(final String fileName) throws Exception
	{
		appointmentPrefs = new HashMap<String, List<AppointmentPreference>>();
		AppointmentPreference appointmentPreference = null;
		CsvBeanReader beanReader = null;
		try
		{
			beanReader = new CsvBeanReader(new FileReader(fileName), CsvPreference.STANDARD_PREFERENCE);

			final String[] header = beanReader.getHeader(true);
			final CellProcessor[] processors = new CellProcessor[header.length];

			while ((appointmentPreference = beanReader.read(AppointmentPreference.class, header, processors)) != null)
			{
				if (appointmentPrefs.get(appointmentPreference.owner) == null)
				{
					List<AppointmentPreference> apptPrefs = new ArrayList<AppointmentPreference>();
					apptPrefs.add(appointmentPreference);
					appointmentPrefs.put(appointmentPreference.owner, apptPrefs);
				}
				else
				{
					appointmentPrefs.get(appointmentPreference.owner).add(appointmentPreference);
				}
				LOG.info(String.format("lineNo=%s, rowNo=%s, = %s", beanReader.getLineNumber(), beanReader.getRowNumber(), appointmentPreference));
			}
		}
		finally
		{
			if (beanReader != null)
			{
				beanReader.close();
			}
		}
	}

	private void buildMeetingSlots()
	{
		meetingSlots = new ArrayList<MeetingSlot>();
		for (int i = 0; i < tables; i++)
		{
			for (int j = 0; j < slots; j++)
			{
				meetingSlots.add(new MeetingSlot(i, j));
			}
		}
	}

	private void createAppointments() throws IOException
	{
		appointments = new ArrayList<Appointment>();
		Iterator<MeetingSlot> slotIterator = meetingSlots.iterator();
		MeetingSlot meetingSlot = slotIterator.next();
		boolean slotAssigned = false;
		do
		{

			for (String owner : appointmentPrefs.keySet())
			{
				List<AppointmentPreference> apptPrefs = appointmentPrefs.get(owner);
				slotAssigned = false;
				int loopCounter = 0;
				do
				{
					slotAssigned = checkAndAssignInclusiveAppointment(apptPrefs, meetingSlot, loopCounter);
					loopCounter++;
				}
				while ((!slotAssigned) && loopCounter <= slots);
				if (slotAssigned)
				{
					meetingSlots.remove(meetingSlot);
					slotIterator = meetingSlots.iterator();
					meetingSlot = meetingSlots.iterator().next();
				}
			}
			if(slotAssigned== false)
			{
				meetingSlots.remove(meetingSlot);
				slotIterator = meetingSlots.iterator();
				if(meetingSlots.iterator().hasNext())
				{
					meetingSlot = meetingSlots.iterator().next();
				}
			}
		}
		while (slotIterator.hasNext() && (slotAssigned == true || meetingSlot.slotNo <= slots));
		LOG.info("AppointmentPrefs - > " + appointmentPrefs);
		LOG.info("Appointments - > " + appointments);
	}

	private CellProcessor[] getProcessors()
	{

		final CellProcessor[] processors = new CellProcessor[]
		{ new NotNull(), new NotNull(), null, null };

		return processors;
	}

	public void writeAppointmentsToFile() throws IOException
	{
		CsvDozerBeanWriter appointmentWriter = new CsvDozerBeanWriter(new FileWriter("appointments.csv"), CsvPreference.STANDARD_PREFERENCE);
		String header[] =
		{ "Table#", "Slot#", "Meeting Owner", "Meeting Participant" };
		appointmentWriter.configureBeanMapping(Appointment.class, FIELD_MAPPING_FOR_OUTPUT_FILE);
		appointmentWriter.writeHeader(header);
		for (Appointment appointment : appointments)
		{
			appointmentWriter.write(appointment, getProcessors());
			appointmentWriter.flush();
		}
		appointmentWriter.close();
	}

	public boolean checkAndAssignInclusiveAppointment(List<AppointmentPreference> appointmentPrefs, MeetingSlot meetingSlot, int meetingCap)
	{
		boolean slotAssigned = false;
		boolean freeAppointment = false;
		Iterator<AppointmentPreference> appointmentPrefsIterator = appointmentPrefs.iterator();
		while (freeAppointment == false && appointmentPrefsIterator.hasNext())
		{
			final AppointmentPreference appointmentPref = appointmentPrefsIterator.next();

			List<Appointment> apptsForTableSlot = appointments.stream().filter(appt -> ((appt.appointmentPref.owner.equals(appointmentPref.owner) || appt.appointmentPref.owner.equals(appointmentPref.meetingPref) || appt.appointmentPref.meetingPref.equals(appointmentPref.owner) || appt.appointmentPref.meetingPref.equals(appointmentPref.meetingPref)) && appt.meetingSlot.slotNo == meetingSlot.slotNo)).collect(Collectors.toList());
			List<Appointment> apptsForowner = appointments.stream().filter(appt -> ((appointmentPref.owner.equals(appt.appointmentPref.owner) || appointmentPref.owner.equals(appt.appointmentPref.meetingPref)))).collect(Collectors.toList());
			List<Appointment> apptsForNgoParticipant = appointments.stream().filter(appt -> ((appointmentPref.meetingPref.equals(appt.appointmentPref.owner) || appointmentPref.meetingPref.equals(appt.appointmentPref.meetingPref)))).collect(Collectors.toList());
			freeAppointment = ((apptsForTableSlot.size() == 0 && apptsForowner.size() <= meetingCap && apptsForNgoParticipant.size() <= slots) || (apptsForTableSlot.size() == 0 && apptsForowner.size() <= slots && apptsForNgoParticipant.size() <= meetingCap)) ? true : false;
			if (freeAppointment)
			{
				Appointment appointment = new Appointment();
				appointment.setAppointmentPref(appointmentPref);
				appointment.setMeetingSlot(meetingSlot);
				appointments.add(appointment);
				appointmentPrefs.remove(appointmentPref);
				appointmentPrefsIterator = appointmentPrefs.iterator();
				slotAssigned = true;
			}
			else
			{
				LOG.log(Level.WARNING, String.format("Clashing appointment detected for this meeting preference: %s", appointmentPref));
			}
		}
		return slotAssigned;
	}
}
