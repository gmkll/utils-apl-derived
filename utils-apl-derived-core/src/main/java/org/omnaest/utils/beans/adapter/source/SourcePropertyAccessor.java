/*******************************************************************************
 * Copyright 2011 Danny Kunz
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.omnaest.utils.beans.adapter.source;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;

import org.omnaest.utils.beans.autowired.AutowiredContainer;

/**
 * Simple {@link SourcePropertyAccessor} interface which reduces to a {@link #setValue(String, Object, Class, PropertyMetaInformation)} and
 * {@link #getValue(String, Class, PropertyMetaInformation)} method signature.
 * 
 * @author Omnaest
 */
public interface SourcePropertyAccessor extends Serializable
{
  /* ********************************************** Classes/Interfaces ********************************************** */
  /**
   * Contains further meta information about a property
   * 
   * @author Omnaest
   */
  public static class PropertyMetaInformation implements Serializable
  {
    /* ********************************************** Constants ********************************************** */
    private static final long                      serialVersionUID = -1057678339296760166L;
    /* ********************************************** Variables ********************************************** */
    protected final Object[]                       additionalArguments;
    protected final AutowiredContainer<Annotation> propertyAnnotationAutowiredContainer;
    protected final AutowiredContainer<Annotation> classAnnotationAutowiredContainer;
    protected final ParameterizedType              genericType;
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @see PropertyMetaInformation
     * @param additionalArguments
     * @param genericType
     * @param propertyAnnotationAutowiredContainer
     * @param classAnnotationAutowiredContainer
     */
    public PropertyMetaInformation( Object[] additionalArguments, ParameterizedType genericType,
                                    AutowiredContainer<Annotation> propertyAnnotationAutowiredContainer,
                                    AutowiredContainer<Annotation> classAnnotationAutowiredContainer )
    {
      super();
      this.additionalArguments = additionalArguments;
      this.propertyAnnotationAutowiredContainer = propertyAnnotationAutowiredContainer;
      this.classAnnotationAutowiredContainer = classAnnotationAutowiredContainer;
      this.genericType = genericType;
    }
    
    /**
     * @return
     */
    public Object[] getAdditionalArguments()
    {
      return this.additionalArguments;
    }
    
    /**
     * @return
     */
    public AutowiredContainer<Annotation> getPropertyAnnotationAutowiredContainer()
    {
      return this.propertyAnnotationAutowiredContainer;
    }
    
    /**
     * @return
     */
    public AutowiredContainer<Annotation> getClassAnnotationAutowiredContainer()
    {
      return this.classAnnotationAutowiredContainer;
    }
    
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "PropertyMetaInformation [additionalArguments=" );
      builder.append( Arrays.toString( this.additionalArguments ) );
      builder.append( ", propertyAnnotationAutowiredContainer=" );
      builder.append( this.propertyAnnotationAutowiredContainer );
      builder.append( ", classAnnotationAutowiredContainer=" );
      builder.append( this.classAnnotationAutowiredContainer );
      builder.append( "]" );
      return builder.toString();
    }
    
    public ParameterizedType getGenericType()
    {
      return this.genericType;
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */
  /**
   * Sets the given value for the given property name.
   * 
   * @see PropertyMetaInformation
   * @param propertyName
   * @param value
   * @param parameterType
   *          : type of the first parameter of the property setter method, or type of the return type of the getter method
   * @param propertyMetaInformation
   */
  public void setValue( String propertyName, Object value, Class<?> parameterType, PropertyMetaInformation propertyMetaInformation );
  
  /**
   * Returns the value related to the given property name.
   * 
   * @see PropertyMetaInformation
   * @param propertyName
   * @param returnType
   * @param propertyMetaInformation
   * @return
   */
  public Object getValue( String propertyName, Class<?> returnType, PropertyMetaInformation propertyMetaInformation );
}
