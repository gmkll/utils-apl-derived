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
package org.omnaest.utils.listener;

import java.util.List;

import org.omnaest.utils.listener.concrete.ListenerManagerImpl;
import org.omnaest.utils.listener.concrete.ListenerRegistrationImpl;

public interface ListenerManager<EVENT, RESULT> extends Listenable<EVENT, RESULT>
{
  
  /**
   * Removes all listeners from the {@link ListenerManagerImpl} instance.
   * 
   * @return this
   */
  public ListenerManager<EVENT, RESULT> clearListeners();
  
  /**
   * Simple method for handling events.
   * 
   * @see #handleEvent(Object, ListenerRegistrationImpl)
   * @param parameter
   * @return
   */
  public List<RESULT> handleEvent( EVENT parameter );
  
  public List<RESULT> handleEvent( EVENT parameter, ListenerRegistration<EVENT, RESULT> listenerRegistration );
  
  /**
   * Connects the current {@link ListenerManagerImpl} to the {@link ListenerRegistrationImpl} from another {@link ListenerManagerImpl}
   * instance. This allows to chain {@link ListenerManagerImpl} instances.
   * 
   * @see #listenTo(ListenerManagerImpl, ListenerAdapter)
   * @see #listenTo(ListenerRegistrationImpl, ListenerAdapter)
   * @see #disconnectFrom(ListenerRegistrationImpl)
   * @see ListenerAdapter
   * @param listenerRegistration
   * @return this
   */
  @SuppressWarnings("rawtypes")
  public ListenerManager<EVENT, RESULT> disconnectFrom( final ListenerManagerImpl listenerManagerImpl );
  
  /**
   * @see #disconnectFrom(ListenerManagerImpl)
   * @see #listenTo(ListenerRegistrationImpl, ListenerAdapter)
   * @see ListenerAdapter
   * @param listenerRegistration
   * @return this
   */
  public ListenerManager<EVENT, RESULT> disconnectFrom( @SuppressWarnings("rawtypes") final ListenerRegistration listenerRegistration );
  
  /**
   * @see #listenTo(ListenerRegistrationImpl, ListenerAdapter)
   * @param <OTHER_PARAMETER>
   * @param <OTHER_RETURN_INFO>
   * @param listenerRegistration
   * @return this
   */
  public ListenerManager<EVENT, RESULT> listenTo( final ListenerRegistration<EVENT, RESULT> listenerRegistration );
  
  /**
   * Connects the current {@link ListenerManagerImpl} to the {@link ListenerRegistrationImpl} from another {@link ListenerManagerImpl}
   * instance. This allows to chain {@link ListenerManagerImpl} instances.
   * 
   * @see ListenerAdapter
   * @see #listenTo(ListenerManagerImpl, ListenerAdapter)
   * @see #disconnectFrom(ListenerRegistrationImpl)
   * @param <OTHER_EVENT>
   * @param <OTHER_RESULT>
   * @param listenerRegistration
   * @param listenerAdapter
   * @return this
   */
  public <OTHER_EVENT, OTHER_RESULT> ListenerManager<EVENT, RESULT> listenTo( final ListenerRegistration<OTHER_EVENT, OTHER_RESULT> listenerRegistration,
                                                                               final ListenerAdapter<OTHER_EVENT, OTHER_RESULT, EVENT, RESULT> listenerAdapter );
  
  /**
   * @see #listenTo(ListenerRegistrationImpl)
   * @param listenerManager
   * @return
   */
  public ListenerManager<EVENT, RESULT> listenTo( ListenerManagerImpl<EVENT, RESULT> listenerManager );
  
  /**
   * @see #disconnectFrom(ListenerManagerImpl)
   * @see #listenTo(ListenerRegistrationImpl, ListenerAdapter)
   * @param <OTHER_EVENT>
   * @param <OTHER_RESULT>
   * @param listenerManager
   * @param listenerAdapter
   */
  public <OTHER_EVENT, OTHER_RESULT> ListenerManager<EVENT, RESULT> listenTo( ListenerManagerImpl<OTHER_EVENT, OTHER_RESULT> listenerManager,
                                                                               ListenerAdapter<OTHER_EVENT, OTHER_RESULT, EVENT, RESULT> listenerAdapter );
  
}
