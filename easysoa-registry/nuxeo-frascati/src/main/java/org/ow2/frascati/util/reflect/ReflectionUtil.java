/**
 * OW2 FraSCAti
 * Copyright (C) 2011 INRIA, University of Lille 1
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * Contact: frascati@ow2.org
 *
 * Author: Christophe Munilla
 *
 * Contributor(s): 
 *
 */
package org.ow2.frascati.util.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;


public abstract class ReflectionUtil {
            
    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger(ReflectionUtil.class.getCanonicalName());
    
    public static Class<?> forName(ClassLoader classLoader,String className){
        try {
            return classLoader.loadClass(className);            
        } catch (ClassNotFoundException e) {
            //log.log(Level.SEVERE,e.getMessage(),e);
        }
        return null;        
    }
    
    public static ClassLoader getCallerClassLoader(){       
        return (ClassLoader) invoke(ClassLoader.class, "getCallerClassLoader");
    }

    /**
     * Invoke a constructor with no parameters of the class which name is className
     * 
     * @param className
     * @param classLoader
     * @return
     *        a class which name is className instance
     */
    public static Object construct(ClassLoader classLoader,String className){   
        return construct(classLoader,className,(Class<?>[])null,(Object[])null);
    }
    
    /**
     * Invoke a constructor of the class which name is className
     * 
     * @param className
     * @param classLoader
     * @return
     *        a class which name is className instance
     */
    public static Object construct(ClassLoader classLoader,String className,
            Class<?>[] classes,Object[] objects){       
        Class<?> instanceClass = forName(classLoader,className);
        if(instanceClass == null){
            return null;
        }
        return construct(instanceClass,classes,objects);
    }   
    
    /**
     * Invoke a constructor with no parameters of the instanceClass
     * 
     * @param instanceClass
     * @return
     *        an instanceClass instance
     */
    public static Object construct(Class<?> instanceClass){
        return construct(instanceClass,(Class<?>[]) null,(Object[]) null);
    }   
    
    /**
     * Invoke a constructor of the instanceClass
     * 
     * @param instanceClass
     * @param classes
     * @param objects
     * @return
     *        an instanceClass instance
     */
    public static Object construct(Class<?> instanceClass,
            Class<?>[] classes,Object[] objects){
        Constructor<?> constructor;
        try {
            constructor = instanceClass.getDeclaredConstructor(classes);
            constructor.setAccessible(true);            
            return constructor.newInstance(objects);
        } catch (SecurityException e) {
            log.log(Level.SEVERE,e.getMessage(),e);
        } catch (NoSuchMethodException e) {
            log.log(Level.SEVERE,e.getMessage(),e);
        } catch (IllegalArgumentException e) {
            log.log(Level.SEVERE,e.getMessage(),e);
        } catch (IllegalAccessException e) {
            log.log(Level.SEVERE,e.getMessage(),e);
        } catch (InvocationTargetException e) {
            log.log(Level.SEVERE,e.getMessage(),e);
        } catch (InstantiationException e) {
            log.log(Level.SEVERE,e.getMessage(),e);
        } catch (Exception e) {
            log.log(Level.SEVERE,e.getMessage(),e);
        }
        return null;
    }   
    
    /**
     * @param instanceClass
     * @param classes
     * @param objects
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <E> E newArgumentClassInstance(Class<?> instanceClass,
            Class<?>[] classes,Object[] objects){
            Class<E> eclass = (Class<E>) getArgumentClass(instanceClass);       
            E result = (E) construct(eclass,classes,objects);
            return result;
    }
    
    /**
     * @param instanceClass
     * @return
     */
    private static<E> Class<E> getArgumentClass(Class<?> instanceClass){
        
        Object o = instanceClass;
        ParameterizedType pt  = null;       
        Class<E> eclass = null;
        
        try {
            pt = (ParameterizedType)instanceClass.getGenericSuperclass();
        } catch(ClassCastException e){
            Type[] types = instanceClass.getGenericInterfaces();
            int n=0;
            for(;n<types.length;n++){
                try{
                    pt = (ParameterizedType)types[n];
                    break;
                }catch(Exception ex){
                    log.log(Level.CONFIG,ex.getMessage());
                }
            }
        }
        try{
            eclass = (Class<E>)pt.getActualTypeArguments()[0];
        } catch(ClassCastException e){
            log.log(Level.CONFIG,e.getMessage());
            }
        return eclass;
    }
    
    /**
     * Invoke a static method with no parameters of the methodClass
     * 
     * @param methodClass
     * @param methodName
     * @return
     */
    public static Object invoke(Class<?> methodClass,String methodName){        
        return invoke(methodClass, methodName,(Class<?>[])null,(Object[])null);     
    }
    
    /**
     * Invoke a static method of the methodClass
     * 
     * @param methodClass
     * @param methodName
     * @return
     */
    public static Object invoke(Class<?> methodClass,String methodName,
            Class<?>[] classes,Object[] objects){       
        return invoke(methodClass,(Object) null, methodName, classes, objects);
    }

    /**
     * Invoke a method with no parameters of the methodClass on the methodCaller object
     * 
     * @param methodClass
     * @param methodCaller
     * @param methodName
     * @return
     */
    public static Object invoke(Class<?> methodClass,
            Object methodCaller, String methodName){        
        return invoke(methodClass,methodCaller, methodName,(Class<?>[])null,(Object[]) null);
    }   
        
    /**
     * find and invoke a method of the methodClass on the methodCaller object which name is methodName
     * 
     * @param methodClass
     * @param methodCaller
     * @param methodName
     * @param classes
     * @param objects
     * @return
     */
    public static Object invoke(Class<?> methodClass,Object methodCaller, 
            String methodName,Class<?>[] classes,Object[] objects){
        Method method;
        try {           
            method = getMethod(methodClass,methodName,classes); 
            if(method != null){
                return invoke(methodCaller, method,objects);
            } else {
                return null;
            }                           
        } catch (SecurityException e) {
            log.log(Level.SEVERE,e.getMessage(),e);
        } catch (IllegalArgumentException e) {
            log.log(Level.SEVERE,e.getMessage(),e);
        } catch (Exception e) {
            log.log(Level.SEVERE,e.getMessage(),e);
        }
        return null;
    }

    /**
     * Invoke the Method method of the methodClass on the methodCaller object
     * 
     * @param methodClass
     * @param methodCaller
     * @param methodName
     * @param classes
     * @param objects
     * @return
     */
    public static Object invoke(Object methodCaller,Method method,Object[] objects){
        try {           
            return method.invoke(methodCaller,objects);
        } catch (SecurityException e) {
            log.log(Level.CONFIG,e.getMessage(),e);
        } catch (IllegalArgumentException e) {
            log.log(Level.CONFIG,e.getMessage(),e);
        } catch (IllegalAccessException e) {
            log.log(Level.CONFIG,e.getMessage(),e);
        } catch (InvocationTargetException e) {
            log.log(Level.CONFIG,e.getMessage(),e);
        } catch (Exception e) {
            log.log(Level.SEVERE,e.getMessage(),e);
        }
        return null;
    }
    
    public static Method getMethod(Class<?> methodClass,
            String methodName,Class<?>[] classes){      
        Method method = null;
        try {
            method = methodClass.getDeclaredMethod(methodName,classes);
            method.setAccessible(true); 
        } catch (SecurityException e) {
            log.log(Level.SEVERE,e.getMessage());
        } catch (NoSuchMethodException e) {
            log.log(Level.SEVERE,e.getMessage());
        } catch (Exception e) {
            log.log(Level.SEVERE,e.getMessage());
        }               
        return method;
    }
    
    public static Field getField(Class<?> fieldClass,String fieldName){     
        Field field = null;
        try {
            field = fieldClass.getDeclaredField(fieldName);
            field.setAccessible(true);  
        } catch (SecurityException e) {
            log.log(Level.SEVERE,e.getMessage());
        } catch (NoSuchFieldException e) {
            log.log(Level.SEVERE,e.getMessage());
        }       
        return field;
    }   
}
