/**
 * 
 */
package com.awpl.sandbox.appointmentmanager;

/**
 * @author Yogish Shenoy
 *
 */
public class MeetingSlot
{
	String table;
	String slot;
	int tableNo;
	int slotNo;

	public MeetingSlot(int tableNo, int slotNo)
	{
		this.tableNo = tableNo+1;
		this.slotNo = slotNo+1;
		this.table = "T" + (tableNo + 1);
		this.slot = "S" + (slotNo + 1);
	}

	public String getTable()
	{
		return table;
	}

	public void setTable(String table)
	{
		this.table = table;
	}

	public String getSlot()
	{
		return slot;
	}

	public void setSlot(String slot)
	{
		this.slot = slot;
	}

	public String toString()
	{
		return table + " " + slot;
	}
}