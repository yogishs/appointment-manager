package com.awpl.sandbox.appointmentmanager;

public class Appointment
{
	AppointmentPreference appointmentPref;
	MeetingSlot meetingSlot;

	public AppointmentPreference getAppointmentPref()
	{
		return appointmentPref;
	}

	public void setAppointmentPref(AppointmentPreference appointmentPref)
	{
		this.appointmentPref = appointmentPref;
	}

	public MeetingSlot getMeetingSlot()
	{
		return meetingSlot;
	}

	public void setMeetingSlot(MeetingSlot meetingSlot)
	{
		this.meetingSlot = meetingSlot;
	}

	public String toString()
	{
		return "NGO Owner: " + this.appointmentPref.owner + " Meeting Participant: " + this.appointmentPref.meetingPref + " Table: " + this.meetingSlot.table + " Slot: " + this.meetingSlot.slot;
	}
}
