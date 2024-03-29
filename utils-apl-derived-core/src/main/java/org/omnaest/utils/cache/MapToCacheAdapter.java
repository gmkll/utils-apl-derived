/*******************************************************************************
 * Copyright 2012 Danny Kunz
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
package org.omnaest.utils.cache;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Adapter from {@link Map} to {@link Cache}<br>
 * <br>
 * Note: {@link #entrySet()}, {@link #values()} and {@link #keySet()} will return an unmodifiable instance
 * 
 * @see Cache
 * @author Omnaest
 * @param <K>
 * @param <V>
 */
public final class MapToCacheAdapter<K, V> extends CacheAbstract<K, V>
{
  /* ************************************************** Constants *************************************************** */
  private static final long serialVersionUID = 6347242138203739267L;
  /* ********************************************** Variables ********************************************** */
  private final Map<K, V>   map;
  
  /* ********************************************** Methods ********************************************** */
  /**
   * @see MapToCacheAdapter
   * @param map
   */
  @SuppressWarnings("unchecked")
  public MapToCacheAdapter( Map<? extends K, ? extends V> map )
  {
    this.map = (Map<K, V>) map;
  }
  
  @Override
  public int size()
  {
    return this.map.size();
  }
  
  @Override
  public boolean isEmpty()
  {
    return this.map.isEmpty();
  }
  
  @Override
  public boolean containsKey( Object key )
  {
    return this.map.containsKey( key );
  }
  
  @Override
  public boolean containsValue( Object value )
  {
    return this.map.containsValue( value );
  }
  
  @Override
  public V get( Object key )
  {
    return this.map.get( key );
  }
  
  @Override
  public V put( K key, V value )
  {
    return this.map.put( key, value );
  }
  
  @Override
  public V remove( Object key )
  {
    return this.map.remove( key );
  }
  
  @Override
  public void putAll( Map<? extends K, ? extends V> m )
  {
    this.map.putAll( m );
  }
  
  @Override
  public void clear()
  {
    this.map.clear();
  }
  
  @Override
  public Set<K> keySet()
  {
    return Collections.unmodifiableSet( this.map.keySet() );
  }
  
  @Override
  public Collection<V> values()
  {
    return Collections.unmodifiableCollection( this.map.values() );
  }
  
  @Override
  public Set<java.util.Map.Entry<K, V>> entrySet()
  {
    return Collections.unmodifiableSet( this.map.entrySet() );
  }
  
  @Override
  public boolean equals( Object o )
  {
    return this.map.equals( o );
  }
  
  @Override
  public int hashCode()
  {
    return this.map.hashCode();
  }
}
