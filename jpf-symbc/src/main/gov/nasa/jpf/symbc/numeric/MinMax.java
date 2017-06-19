/*
 * Copyright (C) 2014, United States Government, as represented by the
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 *
 * Symbolic Pathfinder (jpf-symbc) is licensed under the Apache License, 
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0. 
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

//
//Copyright (C) 2006 United States Government as represented by the
//Administrator of the National Aeronautics and Space Administration
//(NASA).  All Rights Reserved.
//
//This software is distributed under the NASA Open Source Agreement
//(NOSA), version 1.3.  The NOSA has been approved by the Open Source
//Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
//directory tree for the complete NOSA document.
//
//THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
//KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
//LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
//SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
//A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
//THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
//DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//

package gov.nasa.jpf.symbc.numeric;

import java.util.HashMap;
import java.util.Map;
import gov.nasa.jpf.Config;

public class MinMax {
	public static class IllegalRangeException extends IllegalArgumentException {		
		public IllegalRangeException(String message) {
	        super (message);
	    }
	}

	public static int Debug_no_path_constraints = 0;
	public static int Debug_no_path_constraints_sat = 0;
	public static int Debug_no_path_constraints_unsat = 0;

	public static int UniqueId = 0; // Unique id for each SymbolicInteger or
									// SymbolicReal created

	public static void reset() {
		UniqueId = 0;
		minInt = Integer.MIN_VALUE;
		maxInt = Integer.MAX_VALUE;
		minByte = Byte.MIN_VALUE;
		maxByte = Byte.MAX_VALUE;
		minShort = Short.MIN_VALUE;
		maxShort = Short.MAX_VALUE;
		minLong = Long.MIN_VALUE;
		maxLong = Long.MAX_VALUE;
		minChar = Character.MIN_VALUE;
		maxChar = Character.MAX_VALUE;
		minDouble = Double.MIN_VALUE;
		maxDouble = Double.MAX_VALUE; 
	}

	/**
	 * Lower bound on symbolic integer variables.
	 */
	private static int minInt = Integer.MIN_VALUE;

	/**
	 * Upper bound on symbolic integer variables.
	 */
	private static int maxInt = Integer.MAX_VALUE;

	/**
	 * Lower bound on symbolic byte variables.
	 */
	private static byte minByte = Byte.MIN_VALUE;

	/**
	 * Upper bound on symbolic short variables.
	 */
	private static byte maxByte = Byte.MAX_VALUE;

	/**
	 * Lower bound on symbolic short variables.
	 */
	private static short minShort = Short.MIN_VALUE;

	/**
	 * Upper bound on symbolic short variables.
	 */
	private static short maxShort = Short.MAX_VALUE;

	/**
	 * Lower bound on symbolic long variables.
	 */
	private static long minLong = Long.MIN_VALUE;

	/**
	 * Upper bound on symbolic long variables.
	 */
	private static long maxLong = Long.MAX_VALUE;

	/**
	 * Lower bound on symbolic integer variables.
	 */
	private static int minChar = Character.MIN_VALUE;

	/**
	 * Upper bound on symbolic integer variables.
	 */
	private static int maxChar = Character.MAX_VALUE;

	/**
	 * Lower bound on symbolic real variables.
	 */
	private static double minDouble = Double.MIN_VALUE;

	/**
	 * Upper bound on symbolic real variables.
	 */
	private static double maxDouble = Double.MAX_VALUE; 

	/**
	 * Mapping from variable names to minimum integer values.
	 */
	private static Map<String, Long> varMinIntMap;

	/**
	 * Mapping from variable names to maximum integer values.
	 */
	private static Map<String, Long> varMaxIntMap;

	/**
	 * Mapping from variable names to minimum real values.
	 */
	private static Map<String, Double> varMinDoubleMap;

	/**
	 * Mapping from variable names to maximum real values.
	 */
	private static Map<String, Double> varMaxDoubleMap;
	
	public static int getMinInt() {
		return minInt;
	}
	
	public static int getMaxInt() {
		return maxInt;
	}
	
	public static byte getMinByte() {
		return minByte;
	}
	
	public static byte getMaxByte() {
		return maxByte;
	}
	
	public static short getMinShort() {
		return minShort;
	}
	
	public static short getMaxShort() {
		return maxShort;
	}
	
	public static long getMinLong() {
		return minLong;
	}
	
	public static long getMaxLong() {
		return maxLong;
	}
	
	public static int getMinChar() {
		return minChar;
	}
	
	public static int getMaxChar() {
		return maxChar;
	}
	
	public static double getMinDouble() {
		return minDouble;
	}
	
	public static double getMaxDouble() {
		return maxDouble;
	}

	/**
	 * Collects information about the lower and upper bounds on integer and real
	 * symbolic variables, and specific bounds for variables, given by name. The
	 * default bounds are now specified as follows:
	 * 
	 * <pre>
	 * symbolic.min_int = 0
	 * symbolic.max_int = 999
	 * symbolic.min_double = -0.5
	 * symbolic.max_double = 1.5
	 * </pre>
	 * 
	 * To specific a overriding bound for symbolic variables "<code>abc</code> "
	 * and "<code>xyz</code>", we write
	 * 
	 * <pre>
	 * symbolic.min_int_abc = 10
	 * symbolic.max_int_abc = 99
	 * symbolic.min_double_xyz = -0.9
	 * symbolic.max_double_xyz = 0.9
	 * </pre>
	 * 
	 * @param config
	 *            the JPF configuration file
	 */
	public static void loadBounds(Config config) {
		minInt = config.getInt("symbolic.min_int", minInt);
		maxInt = config.getInt("symbolic.max_int", maxInt);
		if (minInt >= maxInt) {
			throw new IllegalRangeException(String.format("integer range: [%s, %s]", minInt, maxInt));
		}

		minLong = config.getLong("symbolic.min_long", minLong);
		maxLong = config.getLong("symbolic.max_long", maxLong);
		if (minLong >= maxLong) {
			throw new IllegalRangeException(String.format("long range: [%s, %s]", minLong, maxLong));
		}

		minShort = (short) config.getInt("symbolic.min_short", minShort);
		maxShort = (short) config.getInt("symbolic.max_short", maxShort);
		if (minShort >= maxShort) {
			throw new IllegalRangeException(String.format("short range: [%s, %s]", minShort, maxShort));
		}

		minByte = (byte) config.getInt("symbolic.min_byte", minByte);
		maxByte = (byte) config.getInt("symbolic.max_byte", maxByte);
		if (minByte >= maxByte) {
			throw new IllegalRangeException(String.format("byte range: [%s, %s]", minByte, maxByte));
		}

		minChar = (char) config.getInt("symbolic.min_char", minChar);
		maxChar = (char) config.getInt("symbolic.max_char", maxChar);
		if (minChar >= maxChar) {
			throw new IllegalRangeException(String.format("char range: [%s, %s]", minChar, maxChar));
		}

		minDouble = config.getDouble("symbolic.min_double", minDouble);
		maxDouble = config.getDouble("symbolic.max_double", maxDouble);
		if (minDouble >= maxDouble) {
			throw new IllegalRangeException(String.format("double range: [%s, %s]", minDouble, maxDouble));
		}

		// Collect specific integer bounds by variable name
		long y;
		varMinIntMap = new HashMap<String, Long>();
		varMaxIntMap = new HashMap<String, Long>();
		int prefixLength = "symbolic.min_int_".length();
		for (String k : config.getKeysStartingWith("symbolic.min_int_")) {
			String name = k.substring(prefixLength);
			y = config.getLong(k, Long.MAX_VALUE);
			if (y != Long.MAX_VALUE) {
				varMinIntMap.put(name, y);
			}
		}
		for (String k : config.getKeysStartingWith("symbolic.max_int_")) {
			String name = k.substring(prefixLength);
			y = config.getLong(k, Long.MIN_VALUE);
			if (y != Long.MIN_VALUE) {
				varMaxIntMap.put(name, y);
			}
		}
		for (String k : varMinIntMap.keySet()) {
			long min = varMinIntMap.get(k);
			long max = maxInt;
			if (varMaxIntMap.containsKey(k)) {
				max = varMaxIntMap.get(k);
			}
			if (min >= max) {
				throw new IllegalRangeException(String.format("int range for %s: [%s, %s]", k, min, max));
			}
		}
		for (String k : varMaxIntMap.keySet()) {
			long min = minInt;
			long max = varMaxIntMap.get(k);
			if (varMinIntMap.containsKey(k)) {
				min = varMinIntMap.get(k);
			}
			if (min >= max) {
				throw new IllegalRangeException(String.format("int range for %s: [%s, %s]", k, min, max));
			}
		}

		// Collect specific real bounds by variable name
		double z;
		varMinDoubleMap = new HashMap<String, Double>();
		varMaxDoubleMap = new HashMap<String, Double>();
		prefixLength = "symbolic.min_double_".length();
		for (String k : config.getKeysStartingWith("symbolic.min_double_")) {
			String name = k.substring(prefixLength);
			z = config.getDouble(k, Double.MAX_VALUE);
			if (z != Double.MAX_VALUE) {
				varMinDoubleMap.put(name, z);
			}
		}
		for (String k : config.getKeysStartingWith("symbolic.max_double_")) {
			String name = k.substring(prefixLength);
			z = config.getDouble(k, Double.MIN_VALUE);
			if (z != Double.MIN_VALUE) {
				varMaxDoubleMap.put(name, z);
			}
		}
		for (String k : varMinDoubleMap.keySet()) {
			double min = varMinDoubleMap.get(k);
			double max = maxDouble;
			if (varMaxDoubleMap.containsKey(k)) {
				max = varMaxDoubleMap.get(k);
			}
			if (min >= max) {
				throw new IllegalRangeException(String.format("double range for %s: [%s, %s]", k, min, max));
			}
		}
		for (String k : varMaxDoubleMap.keySet()) {
			double min = minDouble;
			double max = varMaxDoubleMap.get(k);
			if (varMinDoubleMap.containsKey(k)) {
				min = varMinDoubleMap.get(k);
			}
		}

		// Display the bounds collected from the configuration
		System.out.println("symbolic.min_int=" + minInt);
		System.out.println("symbolic.min_long=" + minLong);
		System.out.println("symbolic.min_short=" + minShort);
		System.out.println("symbolic.min_byte=" + minByte);
		System.out.println("symbolic.min_char=" + minChar);
		for (String k : varMinIntMap.keySet()) {
			System.out.println("symbolic.min_int_" + k + "=" + varMinIntMap.get(k));
		}
		System.out.println("symbolic.max_int=" + maxInt);
		System.out.println("symbolic.max_long=" + maxLong);
		System.out.println("symbolic.max_short=" + maxShort);
		System.out.println("symbolic.max_byte=" + maxByte);
		System.out.println("symbolic.max_char=" + maxChar);
		for (String k : varMaxIntMap.keySet()) {
			System.out.println("symbolic.max_int_" + k + "=" + varMaxIntMap.get(k));
		}
		System.out.println("symbolic.min_double=" + minDouble);
		for (String k : varMinDoubleMap.keySet()) {
			System.out.println("symbolic.min_double_" + k + "=" + varMinDoubleMap.get(k));
		}
		System.out.println("symbolic.max_double=" + maxDouble);
		for (String k : varMaxDoubleMap.keySet()) {
			System.out.println("symbolic.max_double_" + k + "=" + varMaxDoubleMap.get(k));
		}
	}

	private static long getVarMin(String varname, long min) {
		if (varname.endsWith("_SYMINT")) {
			varname = varname.replaceAll("_[0-9][0-9]*_SYMINT", "");
		}
		return varMinIntMap != null && varMinIntMap.containsKey(varname) ? varMinIntMap.get(varname) : min;
	}

	/**
	 * Return the minimum integer value that a given variable can assume.
	 * 
	 * @param varname
	 *            the name of the variable
	 * @return the minimum value of the variable
	 */
	public static long getVarMinInt(String varname) {
		return getVarMin(varname, minInt);
	}

	/**
	 * Return the minimum long value that a given variable can assume.
	 * 
	 * @param varname
	 *            the name of the variable
	 * @return the minimum value of the variable
	 */
	public static long getVarMinLong(String varname) {
		return getVarMin(varname, minLong);
	}

	/**
	 * Return the minimum short value that a given variable can assume.
	 * 
	 * @param varname
	 *            the name of the variable
	 * @return the minimum value of the variable
	 */
	public static long getVarMinShort(String varname) {
		return getVarMin(varname, minShort);
	}

	/**
	 * Return the minimum byte value that a given variable can assume.
	 * 
	 * @param varname
	 *            the name of the variable
	 * @return the minimum value of the variable
	 */
	public static long getVarMinByte(String varname) {
		return getVarMin(varname, minByte);
	}

	/**
	 * Return the minimum char value that a given variable can assume.
	 * 
	 * @param varname
	 *            the name of the variable
	 * @return the minimum value of the variable
	 */
	public static long getVarMinChar(String varname) {
		return getVarMin(varname, minChar);
	}

	private static long getVarMax(String varname, long max) {
		if (varname.endsWith("_SYMINT")) {
			varname = varname.replaceAll("_[0-9][0-9]*_SYMINT", "");
		}
		return varMaxIntMap != null && varMaxIntMap.containsKey(varname) ? varMaxIntMap.get(varname) : max;
	}

	/**
	 * Return the maximum integer value that a given variable can assume.
	 * 
	 * @param varname
	 *            the name of the variable
	 * @return the maximum value of the variable
	 */
	public static long getVarMaxInt(String varname) {
		return getVarMax(varname, maxInt);
	}

	/**
	 * Return the maximum long value that a given variable can assume.
	 * 
	 * @param varname
	 *            the name of the variable
	 * @return the maximum value of the variable
	 */
	public static long getVarMaxLong(String varname) {
		return getVarMax(varname, maxLong);
	}

	/**
	 * Return the maximum short value that a given variable can assume.
	 * 
	 * @param varname
	 *            the name of the variable
	 * @return the maximum value of the variable
	 */
	public static long getVarMaxShort(String varname) {
		return getVarMax(varname, maxShort);
	}

	/**
	 * Return the maximum byte value that a given variable can assume.
	 * 
	 * @param varname
	 *            the name of the variable
	 * @return the maximum value of the variable
	 */
	public static long getVarMaxByte(String varname) {
		return getVarMax(varname, maxByte);
	}

	/**
	 * Return the maximum char value that a given variable can assume.
	 * 
	 * @param varname
	 *            the name of the variable
	 * @return the maximum value of the variable
	 */
	public static long getVarMaxChar(String varname) {
		return getVarMax(varname, maxChar);
	}

	/**
	 * Return the minimum real value that a given variable can assume.
	 * 
	 * @param varname
	 *            the name of the variable
	 * @return the minimum value of the variable
	 */
	public static double getVarMinDouble(String varname) {
		if (varname.endsWith("_SYMINT")) {
			varname = varname.replaceAll("_[0-9][0-9]*_SYMINT", "");
		}
		return varMinDoubleMap.containsKey(varname) ? varMinDoubleMap.get(varname) : minDouble;
	}

	/**
	 * Return the maximum real value that a given variable can assume.
	 * 
	 * @param varname
	 *            the name of the variable
	 * @return the maximum value of the variable
	 */
	public static double getVarMaxDouble(String varname) {
		if (varname.endsWith("_SYMINT")) {
			varname = varname.replaceAll("_[0-9][0-9]*_SYMINT", "");
		}
		return varMaxDoubleMap.containsKey(varname) ? varMaxDoubleMap.get(varname) : maxDouble;
	}
}
