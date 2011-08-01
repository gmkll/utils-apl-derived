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
package org.omnaest.utils.structure.table.adapter;

import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.Table.Row;

/**
 * FIXME move this to the beans package<br>
 * <br>
 * A {@link TableToTypeListAdapter} treats the horizontal {@link Row}s of the {@link Table} as Java Beans. The {@link Column}
 * titles of the {@link Table} have to be equal to (all or a subset) of the property names of the given {@link Table}, since the
 * property values of the Java Bean object will be written to the {@link Column} with the respective title.
 * 
 * @see #newInstance(Class, Table)
 * @author Omnaest
 * @param <B>
 *          Java Bean type
 */
public class TableToTypeListAdapter<B>
{
  /* ********************************************** Variables ********************************************** */
  protected Table<Object> table = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * Creates a new {@link TableToTypeListAdapter} with the given {@link Table} as underlying {@link Table} object.
   * 
   * @see #newInstance(Class, Table)
   * @param beanClass
   * @param table
   */
  @SuppressWarnings("unchecked")
  protected TableToTypeListAdapter( Class<B> beanClass, Table<? extends Object> table )
  {
    super();
    this.table = (Table<Object>) table;
  }
  
  /**
   * Factory methods which creates a new {@link TableToTypeListAdapter} instance for the given Java Bean {@link Class} and
   * {@link Table} .
   * 
   * @param <B>
   * @param beanClass
   * @param table
   * @return
   */
  public static <B> TableToTypeListAdapter<B> newInstance( Class<B> beanClass, Table<?> table )
  {
    //    
    TableToTypeListAdapter<B> retval = null;
    
    //
    if ( beanClass != null && table != null )
    {
      retval = new TableToTypeListAdapter<B>( beanClass, table );
    }
    
    //
    return retval;
  }
  
  /**
   * Returns the {@link Row} for the given row index position as Java Bean.
   * 
   * @param rowIndex
   * @return
   */
  public B getRow( int rowIndex )
  {
    //TODO
    return null;
  }
  
  /**
   * Sets the {@link Row} of the underling {@link Table} to the values of the given Java Bean.
   * 
   * @param rowIndex
   * @param bean
   */
  public void setRow( int rowIndex, B bean )
  {
    
  }
  
  /**
   * Appends a new {@link Row} to the underlying {@link Table} based on the values of the given Java Bean.
   * 
   * @param bean
   */
  public void appendRow( B bean )
  {
    
  }
  
  /**
   * Returns the underlying {@link Table} data structure.
   * 
   * @return
   */
  public Table<Object> getTable()
  {
    return this.table;
  }
}