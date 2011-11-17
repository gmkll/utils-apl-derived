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

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * A decorator for any {@link Map} implementation
 * 
 * @author Omnaest
 */
@XmlRootElement(name = "map")
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class MapDecorator<K, V> implements Map<K, V>
{
  /* ********************************************** Variables ********************************************** */
  @XmlElementWrapper(name = "entries")
  protected Map<K, V> map = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see MapDecorator
   * @param map
   */
  public MapDecorator( Map<K, V> map )
  {
    super();
    this.map = map;
  }
  
  /**
   * @see MapDecorator
   */
  protected MapDecorator()
  {
    super();
  }
  
  /**
   * @return
   * @see java.util.Map#size()
   */
  @Override
  public int size()
  {
    return this.map.size();
  }
  
  /**
   * @return
   * @see java.util.Map#isEmpty()
   */
  @Override
  public boolean isEmpty()
  {
    return this.map.isEmpty();
  }
  
  /**
   * @param key
   * @return
   * @see java.util.Map#containsKey(java.lang.Object)
   */
  @Override
  public boolean containsKey( Object key )
  {
    return this.map.containsKey( key );
  }
  
  /**
   * @param value
   * @return
   * @see java.util.Map#containsValue(java.lang.Object)
   */
  @Override
  public boolean containsValue( Object value )
  {
    return this.map.containsValue( value );
  }
  
  /**
   * @param key
   * @return
   * @see java.util.Map#get(java.lang.Object)
   */
  @Override
  public V get( Object key )
  {
    return this.map.get( key );
  }
  
  /**
   * @param key
   * @param value
   * @return
   * @see java.util.Map#put(java.lang.Object, java.lang.Object)
   */
  @Override
  public V put( K key, V value )
  {
    return this.map.put( key, value );
  }
  
  /**
   * @param key
   * @return
   * @see java.util.Map#remove(java.lang.Object)
   */
  @Override
  public V remove( Object key )
  {
    return this.map.remove( key );
  }
  
  /**
   * @param m
   * @see java.util.Map#putAll(java.util.Map)
   */
  @Override
  public void putAll( Map<? extends K, ? extends V> m )
  {
    this.map.putAll( m );
  }
  
  /**
   * @see java.util.Map#clear()
   */
  @Override
  public void clear()
  {
    this.map.clear();
  }
  
  /**
   * @return
   * @see java.util.Map#keySet()
   */
  @Override
  public Set<K> keySet()
  {
    return this.map.keySet();
  }
  
  /**
   * @return
   * @see java.util.Map#values()
   */
  @Override
  public Collection<V> values()
  {
    return this.map.values();
  }
  
  /**
   * @return
   * @see java.util.Map#entrySet()
   */
  @Override
  public Set<java.util.Map.Entry<K, V>> entrySet()
  {
    return this.map.entrySet();
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals( Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( obj == null )
    {
      return false;
    }
    if ( !( obj instanceof Map ) )
    {
      return false;
    }
    Map<?, ?> other = (Map<?, ?>) obj;
    if ( this.map == null )
    {
      return false;
    }
    else if ( !this.map.equals( other ) )
    {
      return false;
    }
    return true;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( this.map == null ) ? 0 : this.map.hashCode() );
    return result;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( this.map );
    return builder.toString();
  }
  
  /**
   * @return the map
   */
  protected Map<K, V> getMap()
  {
    return this.map;
  }
  
  /**
   * @param map
   *          the map to set
   */
  protected void setMap( Map<K, V> map )
  {
    this.map = map;
  }
  
}
