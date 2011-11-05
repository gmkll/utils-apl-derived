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
package org.omnaest.utils.beans.autowired;

import java.util.Iterator;
import java.util.Set;

import org.omnaest.utils.structure.collection.ListUtils;

/**
 * Abstract implementation for {@link AutowiredContainer} which reduces the number of methods to be implemented.
 * 
 * @param <E>
 * @author Omnaest
 */
public abstract class AutowiredContainerAbstract<E> implements AutowiredContainer<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = -7792783078590040662L;
  
  /* ********************************************** Methods ********************************************** */
  
  @Override
  public <O extends E> O getValue( Class<O> clazz )
  {
    //
    O retval = null;
    
    //
    Set<O> valueSet = this.getValueSet( clazz );
    if ( valueSet != null && valueSet.size() == 1 )
    {
      retval = valueSet.iterator().next();
    }
    
    //
    return retval;
  }
  
  @Override
  public int putAll( Iterable<E> iterable )
  {
    //
    int retval = 0;
    
    //
    if ( iterable != null )
    {
      for ( E element : iterable )
      {
        this.put( element );
      }
    }
    
    // 
    return retval;
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "AutowiredContainerAbstract [content=" );
    builder.append( ListUtils.from( this ) );
    builder.append( "]" );
    return builder.toString();
  }
  
  @Override
  public <O extends E> boolean containsAssignable( Class<O> type )
  {
    Set<O> valueSet = this.getValueSet( type );
    return valueSet != null && !valueSet.isEmpty();
  }
  
  @Override
  public boolean isEmpty()
  {
    // 
    Iterator<E> iterator = this.iterator();
    return iterator == null || !iterator.hasNext();
  }
  
}
