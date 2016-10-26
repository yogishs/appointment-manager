/**
 * 
 */
package com.awpl.sandbox.appointmentmanager;



/**
 * @author Yogish Shenoy
 *
 */

import com.google.common.collect.Table;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Multimap;
 
// Given

public class GuavaTableTest
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		String[] names = { "Bob", "Alice", "Andy", "Carol", "Ben" };
		 
		// Table of names
		Table<Character, Integer, String> table = HashBasedTable.create();
		 
		// First letter is a row key, length is a column key
		for (String name : names) {
		    table.put(name.charAt(0), name.length(), name);
		}
		 
		// Value corresponding to the given row and column keys
		System.out.println(table.get('A', 5)); // -> Alice
		System.out.println(table.get('B', 3)); // -> Ben
		 
		// Set of column keys that have one or more values in the table
		System.out.println(table.columnKeySet()); // -> [4, 5, 3]
		 
		// View of all mappings that have the given row key
		System.out.println(table.row('A')); ; // -> {4=Andy, 5=Alice}
		System.out.println(table.rowKeySet());
		
		Multimap<Character, String> multimap = ArrayListMultimap.create();
		 
		// Fill multimap with data
		for (String name : names) {
		    multimap.put(name.charAt(0), name);
		}
		 
		// Use of the multimap
		multimap.get('C'); // -> [Carol]
		multimap.get('A'); // -> [Alice, Andy]
		 
		multimap.remove('A', "Andy");
		multimap.get('A'); // -> [Alice]
		 
		multimap.removeAll('C');
		multimap.get('C'); // -> []
	}

}
