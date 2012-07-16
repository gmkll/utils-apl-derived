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
package org.omnaest.utils.table2.impl;

import java.io.Serializable;

import org.omnaest.utils.table2.ImmutableTable;
import org.omnaest.utils.table2.Table;
import org.omnaest.utils.table2.TableEventHandler;

/**
 * Allows to {@link #attach(TableEventHandler)} or {@link #detach(TableEventHandler)} {@link TableEventHandler}s to the
 * underlying {@link Table}
 * 
 * @author Omnaest
 * @param <E>
 * @param <T>
 */
public interface TableEventHandlerRegistration<E, T extends ImmutableTable<E>> extends Serializable
{
  /**
   * @param tableEventHandler
   * @return underlying {@link Table}
   */
  public T attach( TableEventHandler<E> tableEventHandler );
  
  /**
   * @param tableEventHandler
   * @return underlying {@link Table}
   */
  public T detach( TableEventHandler<E> tableEventHandler );
}