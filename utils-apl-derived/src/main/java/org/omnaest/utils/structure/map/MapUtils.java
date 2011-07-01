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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.omnaest.utils.structure.collection.CollectionUtil.ElementConverter;
import org.omnaest.utils.structure.collection.CollectionUtil.IdentityElementConverter;
import org.omnaest.utils.tuple.TupleDuad;

/**
 * Helper class for {@link Map} operations.
 * 
 * @author Omnaest
 */
public class MapUtils
{
  
  /**
   * Merges all given {@link Map} instances into a single {@link LinkedHashMap}.
   * 
   * @see #mergeAll(Map...)
   * @param <K>
   * @param <V>
   * @param mapCollection
   * @return
   */
  public static <K, V> Map<K, V> mergeAll( Collection<Map<K, V>> mapCollection )
  {
    //
    Map<K, V> retmap = new LinkedHashMap<K, V>();
    
    //
    for ( Map<K, V> map : mapCollection )
    {
      retmap.putAll( map );
    }
    
    //
    return retmap;
  }
  
  /**
   * Merges all given {@link Map} instances into a single {@link LinkedHashMap}.
   * 
   * @see #mergeAll(Collection)
   * @param <K>
   * @param <V>
   * @param maps
   * @return
   */
  public static <K, V> Map<K, V> mergeAll( Map<K, V>... maps )
  {
    return MapUtils.mergeAll( Arrays.asList( maps ) );
  }
  
  /**
   * Returns a list of {@link TupleDuad} instances which have always the value of the first map and the value of the second map
   * which share the same key over both maps.
   * 
   * @param <K>
   * @param <VA>
   * @param <VB>
   * @param mapA
   * @param mapB
   * @return
   */
  public static <K, VA, VB> List<TupleDuad<VA, VB>> innerJoinMapByKey( Map<K, VA> mapA, Map<K, VB> mapB )
  {
    //
    List<TupleDuad<VA, VB>> retlist = new ArrayList<TupleDuad<VA, VB>>();
    
    //
    if ( mapA != null && mapB != null )
    {
      //
      for ( K key : mapA.keySet() )
      {
        //
        VA valueA = mapA.get( key );
        VB valueB = mapB.get( key );
        
        //
        retlist.add( new TupleDuad<VA, VB>( valueA, valueB ) );
      }
    }
    
    //
    return retlist;
  }
  
  /**
   * Converts the key of a map into another key.
   * 
   * @param <KeyFrom>
   * @param <KeyTo>
   * @param <Value>
   * @param map
   * @param keyElementConverter
   * @return
   */
  public static <KeyFrom, KeyTo, Value> Map<KeyTo, Value> convertMapKey( Map<KeyFrom, Value> map,
                                                                         ElementConverter<KeyFrom, KeyTo> keyElementConverter )
  {
    return MapUtils.convertMap( map, keyElementConverter, new IdentityElementConverter<Value>() );
  }
  
  /**
   * Converts a given map into a new map with new types. Makes use of element converters.
   * 
   * @see ElementConverter
   * @param map
   * @param keyElementConverter
   * @param valueElementConverter
   * @return
   */
  public static <KeyFrom, KeyTo, ValueFrom, ValueTo> Map<KeyTo, ValueTo> convertMap( Map<KeyFrom, ValueFrom> map,
                                                                                     ElementConverter<KeyFrom, KeyTo> keyElementConverter,
                                                                                     ElementConverter<ValueFrom, ValueTo> valueElementConverter )
  {
    //
    Map<KeyTo, ValueTo> retmap = null;
    
    //
    if ( map != null )
    {
      //
      retmap = new LinkedHashMap<KeyTo, ValueTo>( map.size() );
      
      //
      for ( KeyFrom keyFrom : map.keySet() )
      {
        KeyTo keyTo = keyElementConverter.convert( keyFrom );
        ValueTo valueTo = valueElementConverter.convert( map.get( keyFrom ) );
        
        retmap.put( keyTo, valueTo );
      }
      
    }
    
    //
    return retmap;
  }
}