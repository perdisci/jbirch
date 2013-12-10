package net.sourceforge.sizeof;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.instrument.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * The original code for this class can be found at
 * http://sourceforge.net/projects/sizeof/
 * 
 * I modified it to better fit my needs.
 * The original author info is:
 * 
 * @author nicola
 * @see java#lang#instrument#Instrument
 * @version 0.5
 */
public class SizeOf
{
	private static OutputStream out = System.out;
	
	/**
	 * Instance of java.lang.instrument.Instrument injected by the Java VM 
	 * @see premain(String options, Instrumentation inst) 
	 */
	private static Instrumentation inst;

	private static final boolean SKIP_STATIC_FIELD = false;
	private static final boolean SKIP_FINAL_FIELD = false;
	
	/**
	 * Callback method used by the Java VM to inject the java.lang.instrument.Instrument
	 * instance
	 */
	public static void premain(String options, Instrumentation inst)
	{
		SizeOf.inst = inst;
	}

	/**
	 * Calls java.lang.instrument.Instrument.getObjectSize(object).
	 *  
	 * @param object the object to size
	 * @return an implementation-specific approximation of the amount of storage consumed 
	 * 	by the specified object
	 * @see java#lang#instrument#Instrument#Instrumentation#getObjectSize(Object objectToSize)
	 */
	public static long sizeOf(Object object)
	{
		if (inst == null)
			throw new IllegalStateException("Instrumentation is null");
		return inst.getObjectSize(object);
	}

	private static String[] unit = { "b", "Kb", "Mb" };

	/**
	 * Format size in a human readable format
	 * 
	 * @param size
	 * @return a string representation of the size argument followed by
	 * 	b for byte, Kb for kilobyte or Mb for megabyte 
	 */
	public static String humanReadable(long size)
	{
		int i;
		double dSize = size;//new Double(size);
		for (i = 0; i < 3; ++i)
		{
			if (dSize < 1024)
				break;
			dSize /= 1024;
		}

		return dSize + unit[i];
	}

	/**
	 * Computes an implementation-specific approximation of the amount of storage consumed 
	 * by objectToSize and by all the objects reachable from it 
	 * 
	 * @param objectToSize
	 * @return an implementation-specific approximation of the amount of storage consumed 
	 * 	by objectToSize and by all the objects reachable from it
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws IOException 
	 */
	public static long iterativeSizeOf(Object objectToSize) throws IllegalArgumentException,
			IllegalAccessException, IOException
	{
		Set<Integer> doneObj = new HashSet<Integer>();
		return iterativeSizeOf(objectToSize, doneObj);
	}
	
	private static long iterativeSizeOf(Object o, Set<Integer> doneObj)
			throws IllegalArgumentException, IllegalAccessException, IOException
	{
		if (o == null)
			return 0;
		
		long size = 0;
		int hash = System.identityHashCode(o);
		//String hash = o.getClass().toString();
		
		if (doneObj.contains(hash))
			return 0;
		
		doneObj.add(hash);
		size = sizeOf(o);

		if (o instanceof Object[])
		{
			for (Object obj : (Object[]) o)
				size += iterativeSizeOf(obj, doneObj);
		} 
		else {

			Field[] fields = o.getClass().getDeclaredFields();

			for (Field f : fields)
			{
				f.setAccessible(true);
				Object obj = f.get(o);
				if (isComputable(f))
					size += iterativeSizeOf(obj, doneObj);
			}
		}
		
		return size;
	}
	
	private static boolean isAPrimitiveType(Class c)
	{
		 if (c==java.lang.Boolean.TYPE) return true;

		 if (c==java.lang.Character.TYPE) return true;

		 if (c==java.lang.Byte.TYPE) return true;

		 if (c==java.lang.Short.TYPE) return true;

		 if (c==java.lang.Integer.TYPE) return true;

		 if (c==java.lang.Long.TYPE) return true;

		 if (c==java.lang.Float.TYPE) return true;

		 if (c==java.lang.Double.TYPE) return true;

		 if (c==java.lang.Void.TYPE) return true;
		 
		 return false;
	}
	
	private static boolean isComputable(Field f)
	{
		int modificatori = f.getModifiers();
		
		if(isAPrimitiveType(f.getType()))
			return false;
		else if (SKIP_STATIC_FIELD && Modifier.isStatic(modificatori))
			return false;
		else if (SKIP_FINAL_FIELD && Modifier.isFinal(modificatori))
			return false;
		else
			return true;
	}
}
