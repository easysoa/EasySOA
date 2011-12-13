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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.util.logging.Logger;
import java.util.logging.Level;

public class ReflectionHelper {

    private Class<?> reflectedClass;
    private Object reflected;
    private ClassLoader loader; 
    private boolean unknownClass;
    
    private final Logger log = Logger.getLogger(ReflectionHelper.class.getCanonicalName());
    
    public ReflectionHelper(ClassLoader classLoader,String className){
        
        reflectedClass = ReflectionUtil.forName(classLoader, className);
        if(reflectedClass == null){
            unknownClass = true;
            log.log(Level.INFO,"The object class has not been found for : " + className);
        } else {
            unknownClass = false;
        }
        loader = classLoader;
    }
    
    public ReflectionHelper(Class<?> clazz){
        
        reflectedClass = clazz;
        if(reflectedClass == null){
            unknownClass = true;
        } else {
            unknownClass = false;
        }
        loader = clazz.getClassLoader();
    }

    public boolean isClassKnown(){
        return !unknownClass;
    }
    
    public Object newInstance(){    
        if(unknownClass){
            log.log(Level.INFO,"The object class has not been found");
            return null;
        }
        return newInstance((Class<?>[])null,(Object[]) null);       
    }
    
    public Object newInstance(Class<?>[] classes, Object[] objects){        
        if(unknownClass){
            log.log(Level.INFO,"The object class has not been found");
            return null;
        }   
        reflected = ReflectionUtil.construct(reflectedClass, classes, objects);
        return reflected;       
    }   

    public Object invoke(String methodName){        
        if(unknownClass){
            log.log(Level.INFO,"The object class has not been found");
            return null;
        }   
        return invoke(methodName,(Class<?>[])null,(Object[])null);      
    }

    public Object invokeStatic(String methodName){      
        if(unknownClass){
            log.log(Level.INFO,"The object class has not been found");
            return null;
        }   
        return invokeStatic(methodName,(Class<?>[])null,(Object[])null);        
    } 
    
    public Object invokeInherited(String methodName,String className){      
        if(unknownClass){
            log.log(Level.INFO,"The object class has not been found");
            return null;
        }   
        return invokeInherited(methodName,className,(Class<?>[])null,(Object[])null);       
    } 
        
    public Object invoke(String methodName,Class<?>[] classes,Object[] objects){    
        if(unknownClass){
            log.log(Level.INFO,"The object class has not been found");
            return null;
        }       
        Method method = ReflectionUtil.getMethod(reflectedClass, methodName, classes);
        
        if(method != null){         
            return ReflectionUtil.invoke(reflected, method, objects);           
        }   
        System.out.println("the method "+ methodName + " doesn't exist in " + 
                reflectedClass.getCanonicalName());
        return null;            
    }
    
    public Object invokeStatic(String methodName,Class<?>[] classes,Object[] objects){  
        if(unknownClass){
            log.log(Level.INFO,"The object class has not been found");
            return null;
        }       
        Method method = ReflectionUtil.getMethod(reflectedClass, methodName, classes);
        
        if(method != null){         
            return ReflectionUtil.invoke(null, method, objects);            
        }
        
        return null;            
    }
    
    public Object invokeInherited(String methodName,String className,
            Class<?>[] classes,Object[] objects){           
        if(unknownClass){
            log.log(Level.INFO,"The object class has not been found");
            return null;
        }
        Class<?> inheritedClass = ReflectionUtil.forName(loader, className);
        Method method = ReflectionUtil.getMethod(inheritedClass, methodName, classes);      
        if(method != null){         
            return ReflectionUtil.invoke(reflected, method, objects);           
        }       
        return null;            
    }
    
    public void set(String fieldName,Object value){         
        if(unknownClass){
            log.log(Level.INFO,"The object class has not been found");
            return;
        }
        Field field = ReflectionUtil.getField(reflectedClass,fieldName);                
        try {
             field.set(reflected,value);
             
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        
    }
    
    public void setInherited(String className,String fieldName,Object value){   
        if(unknownClass){
            log.log(Level.INFO,"The object class has not been found");
            return;
        }       
        Class<?> inheritedClass = ReflectionUtil.forName(loader, className);
        Field field = ReflectionUtil.getField(inheritedClass,fieldName);                
        try {
             field.set(reflected,value);             
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        
    }
    
    public void setStatic(String fieldName,Object value){   
        if(unknownClass){
            log.log(Level.INFO,"The object class has not been found");
            return;
        }       
        Field field = ReflectionUtil.getField(reflectedClass,fieldName);                
        try {
             field.set(null,value);          
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        
    }
    
    public Class<?> getReflectedClass(){
        
        return reflectedClass;
    }
    
    public Object get(String fieldName){
        
        if(unknownClass){
            log.log(Level.INFO,"The object class has not been found");
            return null;
        }   
        Field field = ReflectionUtil.getField(reflectedClass,fieldName);        
        if(field == null){          
            return null;            
        }       
        try {
            return field.get(reflected);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }       
        return null;
    }
    
    public Object getStatic(String fieldName){          
        if(unknownClass){
            log.log(Level.INFO,"The object class has not been found");
            return null;
        }
        Field field = ReflectionUtil.getField(reflectedClass,fieldName);        
        if(field == null){          
            return null;            
        }       
        try {
            return field.get(null);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public Object getInherited(String className,String fieldName){  
        if(unknownClass){
            log.log(Level.INFO,"The object class has not been found");
            return null;
        }
        Class<?> inheritedClass = ReflectionUtil.forName(loader, className);
        Field field = ReflectionUtil.getField(inheritedClass,fieldName);        
        if(field == null){          
            return null;            
        }       
        try {
            return field.get(reflected);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public Object get(){    
        if(unknownClass){
            log.log(Level.INFO,"The object class has not been found");
            return null;
        }
        return reflected;
    }
    
    public void set(Object reflected){  
        if(unknownClass){
            log.log(Level.INFO,"The object class has not been found");
            return;
        }
        this.reflected = reflectedClass.cast(reflected);
    }

}