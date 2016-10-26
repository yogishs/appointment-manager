/**
 * 
 */
package com.awpl.sandbox.appointmentmanager;

/**
 * @author Yogish Shenoy
 *
 */
public class AppointmentPreference
{
	String owner;
	String meetingPref;

	public String getOwner()
	{
		return owner;
	}

	public void setOwner(String ngoOwner)
	{
		this.owner = ngoOwner;
	}

	public String getMeetingPref()
	{
		return meetingPref;
	}

	public void setMeetingPref(String meetingPreference)
	{
		this.meetingPref = meetingPreference;
	}
	
	public String toString()
	{
		return "NGO Owner: "+ this.owner+" Meeting Preference: " + this.meetingPref;
	}
}
