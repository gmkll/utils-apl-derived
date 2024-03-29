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
package org.omnaest.utils.structure.map;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.omnaest.utils.structure.collection.set.SetUtils;
import org.omnaest.utils.structure.element.converter.ElementBidirectionalConverter;

/**
 * This abstract {@link Map} implementation does implement all methods which rely only on other methods within the {@link Map}
 * interface. This results in sub classes only to have to implement the really needed methods.
 * 
 * @author Omnaest
 * @param <K>
 * @param <V>
 */
public abstract class MapAbstract<K, V> implements Map<K, V>, Serializable
{
  /* ************************************************** Constants *************************************************** */
  private static final long serialVersionUID = -8854027357614884818L;
  
  /* *************************************************** Methods **************************************************** */
  @Override
  public boolean isEmpty()
  {
    return this.size() == 0;
  }
  
  @Override
  public boolean containsKey( Object key )
  {
    return this.get( key ) != null;
  }
  
  @Override
  public boolean containsValue( Object value )
  {
    return this.values().contains( value );
  }
  
  @Override
  public void putAll( Map<? extends K, ? extends V> map )
  {
    if ( map != null )
    {
      for ( K key : map.keySet() )
      {
        this.put( key, map.get( key ) );
      }
    }
  }
  
  @Override
  public Set<java.util.Map.Entry<K, V>> entrySet()
  {
    //
    final ElementBidirectionalConverter<K, Entry<K, V>> elementBidirectionalConverter = new ElementBidirectionalConverter<K, Map.Entry<K, V>>()
    {
      @Override
      public java.util.Map.Entry<K, V> convert( final K key )
      {
        // 
        return new Entry<K, V>()
        {
          @Override
          public K getKey()
          {
            return key;
          }
          
          @Override
          public V getValue()
          {
            return MapAbstract.this.get( key );
          }
          
          @Override
          public V setValue( V value )
          {
            return MapAbstract.this.put( key, value );
          }
          
          @Override
          public String toString()
          {
            try
            {
              return "[" + String.valueOf( this.getKey() ) + ":" + String.valueOf( this.getValue() + "]" );
            }
            catch ( Exception e )
            {
              return "";
            }
          }
          
        };
      }
      
      @Override
      public K convertBackwards( java.util.Map.Entry<K, V> entry )
      {
        return entry.getKey();
      }
    };
    return SetUtils.adapter( this.keySet(), elementBidirectionalConverter );
  }
  
  @Override
  public int size()
  {
    return this.keySet().size();
  }
  
  @Override
  public void clear()
  {
    //
    for ( K key : this.keySet() )
    {
      this.remove( key );
    }
  }
  
  @Override
  public String toString()
  {
    return MapUtils.toString( this );
  }
  
}
