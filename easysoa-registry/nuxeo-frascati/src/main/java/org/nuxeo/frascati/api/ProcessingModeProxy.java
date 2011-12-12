/**
 * EasySOA - Nuxeo FraSCAti
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */
package org.nuxeo.frascati.api;

import org.nuxeo.frascati.factory.ClassLoaderSingleton;
import org.ow2.frascati.util.reflect.ReflectionHelper;

/**
 * ProcessingModeProxy allow to define and get a ProcessingMode for a FraSCAtiProcessingContextItf instance
 */
public abstract class ProcessingModeProxy {
			
	public static final int parse = 0;
	public static final int check = 1;
	public static final int generate = 2 ;
	public static final int compile = 3;
	public static final int instantiate = 4;
	public static final int complete = 5;
	public static final int start = 6;
	public static final int all = 7;
	
	private static final ReflectionHelper processingMode = new ReflectionHelper(
			ClassLoaderSingleton.classLoader(),
			"org.ow2.frascati.assembly.factory.api.ProcessingMode");
	
	private static final Object[] modes = processingMode.getReflectedClass().getEnumConstants();
	
	/**
	 * Returns the ProcessingMode object conversion of the integer value 
	 * passed as parameter
	 * 
	 * @param mode
	 * 	the integer value to convert
	 * @return
	 * 	the ProcessingMode object conversion of the integer value
	 */
	public static final Object getProcessingMode(int mode){	
		if(mode < 0 || mode > 7){
			return null;
		}
		return modes[mode];		
	}

	/**
	 * Returns the FraSCAti ProcessingMode.class object
	 * @return
	 * 	the ProcessingMode.class object
	 */
	public static final Class<?> getProcessingModeClass(){		
		return processingMode.getReflectedClass();
	}
	
	/**
	 * Returns the integer conversion of a ProcessingMode instance
	 * 
	 * @param processingModeObject
	 * 	a FraSCAti ProcessingMode object
	 * @return
	 * 	the integer value conversion of the ProcessingMode Object
	 */
	public static final int convert(Object processingModeObject){
		int n=0;		
		for(;n<modes.length;n++){
			if(modes[n] == processingModeObject){
				return n;
			}
		}
		return -1;
	}

} 
