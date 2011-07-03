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
package org.omnaest.utils.structure.table.view.concrete;

import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.concrete.ArrayTable;
import org.omnaest.utils.structure.table.view.BeanTable;

/**
 * TODO undone
 * 
 * @see ArrayTable
 * @see BeanTable
 * @author Omnaest
 * @param <B>
 *          Java Bean type
 */
public abstract class BeanArrayTable<B> implements BeanTable<B>
{
  /* ********************************************** Variables ********************************************** */
  protected Table<Object> table = null;
  
  /* ********************************************** Methods ********************************************** */
  /**
   * Creates a new {@link BeanTable} with an underlying {@link ArrayTable}
   */
  public BeanArrayTable()
  {
    super();
    this.table = new ArrayTable<Object>();
  }
  
  /**
   * Creates a new {@link BeanTable} with the given {@link Table} as underlying {@link Table} object.
   * 
   * @param table
   */
  public BeanArrayTable( Table<Object> table )
  {
    super();
    this.table = table;
  }
  
  @Override
  public Table<Object> getTable()
  {
    return this.table;
  }
  
}
