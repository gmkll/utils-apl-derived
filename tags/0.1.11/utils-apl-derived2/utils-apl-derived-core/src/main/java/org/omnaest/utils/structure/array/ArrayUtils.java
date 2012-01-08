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
package org.omnaest.utils.structure.array;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import org.omnaest.utils.structure.collection.CollectionUtils;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.element.ObjectUtils;
import org.omnaest.utils.structure.element.converter.ElementConverter;

/**
 * Helper methods for arrays.
 * 
 * @see CollectionUtils
 * @author Omnaest
 */
public class ArrayUtils
{
  /**
   * Converts a given array into a new array with a new element type.
   * 
   * @param arrayFrom
   * @param arrayTo
   *          : instance of the array later returned. Use the return value to set this list. The array will not be necessarily
   *          changed only by the call as parameter.
   * @param elementConverter
   * @return
   */
  
  public static <TO, FROM> TO[] convertArray( FROM[] arrayFrom, TO[] arrayTo, ElementConverter<FROM, TO> elementConverter )
  {
    List<TO> listTo = ListUtils.convert( Arrays.asList( arrayFrom ), elementConverter );
    return listTo == null ? null : listTo.toArray( arrayTo );
  }
  
  /**
   * Converts a given array into a new list with a new element type, whereby all elements which convert to null will be excluded
   * in the target list.
   * 
   * @param arrayFrom
   * @param arrayTo
   * @param elementConverter
   * @return
   */
  public static <TO, FROM> TO[] convertArrayExcludingNullElements( FROM[] arrayFrom,
                                                                   TO[] arrayTo,
                                                                   ElementConverter<FROM, TO> elementConverter )
  {
    List<TO> listTo = ListUtils.convertExcludingNullElements( Arrays.asList( arrayFrom ), elementConverter );
    return listTo == null ? null : listTo.toArray( arrayTo );
  }
  
  /**
   * Returns a new array with all tokens of the given string array being trimmed
   * 
   * @param stringArray
   * @return new array with all trimmed tokens
   */
  public static String[] trimStringArrayTokens( String[] stringArray )
  {
    //
    String[] retvals = null;
    
    //
    if ( stringArray != null )
    {
      //
      retvals = new String[stringArray.length];
      
      //
      for ( int ii = 0; ii < retvals.length; ii++ )
      {
        //
        String value = stringArray[ii];
        value = value != null ? value.trim() : null;
        
        //
        retvals[ii] = value;
      }
    }
    
    //
    return retvals;
  }
  
  /**
   * Returns a non primitive {@link Array} for a primitive one
   * 
   * @param primitiveArray
   * @return
   */
  public static Object[] toObject( Object primitiveArray )
  {
    //    
    Object[] retvals = null;
    
    //
    if ( primitiveArray != null )
    {
      try
      {
        //
        Class<? extends Object> primitiveArrayType = primitiveArray.getClass();
        Class<?> componentType = primitiveArrayType.getComponentType();
        if ( componentType.isPrimitive() )
        {
          componentType = ObjectUtils.primitiveWrapperTypeFor( componentType );
        }
        int length = Array.getLength( primitiveArray );
        retvals = (Object[]) Array.newInstance( componentType, length );
        
        //
        for ( int ii = 0; ii < retvals.length; ii++ )
        {
          retvals[ii] = Array.get( primitiveArray, ii );
        }
      }
      catch ( Exception e )
      {
      }
    }
    
    //
    return retvals;
  }
  
  /**
   * Returns true if the given type is not null and {@link Class#isArray()} is true
   * 
   * @param type
   * @return
   */
  public static boolean isArrayType( Class<?> type )
  {
    return type != null && type.isArray();
  }
  
  /**
   * Returns true if the object is an {@link Array}
   * 
   * @param object
   * @return
   */
  public static boolean isArray( Object object )
  {
    return object != null && isArrayType( object.getClass() );
  }
  
  /**
   * Returns the component type of a given array type. Returns null if the given type is no array type or there can no component
   * type be detected.
   * 
   * @param arrayType
   * @return
   */
  public static Class<?> componentType( Class<?> arrayType )
  {
    return isArrayType( arrayType ) ? arrayType.getComponentType() : null;
  }
  
  /**
   * Returns the length of a given array instance or -1 if the given instance is not an array instance.
   * 
   * @param arrayObject
   * @return
   */
  public static int length( Object arrayObject )
  {
    return isArray( arrayObject ) ? Array.getLength( arrayObject ) : -1;
  }
  
  /**
   * @param elements
   * @return
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static <E> E[] valueOf( E... elements )
  {
    //
    E[] retvals = null;
    
    //
    Class componentType = elements.length == 0 ? Object.class : elements[0].getClass();
    int length = elements.length;
    retvals = (E[]) Array.newInstance( componentType, length );
    
    //
    for ( int ii = 0; ii < length; ii++ )
    {
      retvals[ii] = elements[ii];
    }
    
    //
    return retvals;
  }
}
