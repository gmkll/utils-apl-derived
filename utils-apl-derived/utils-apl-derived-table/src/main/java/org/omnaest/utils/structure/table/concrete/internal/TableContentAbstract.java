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
package org.omnaest.utils.structure.table.concrete.internal;

import org.omnaest.utils.structure.table.Table.Stripe.StripeType;
import org.omnaest.utils.structure.table.Table.TableSize;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeDataList;
import org.omnaest.utils.structure.table.internal.TableInternal.TableContent;

/**
 * @see TableContent
 * @author Omnaest
 * @param <E>
 */
public abstract class TableContentAbstract<E> implements TableContent<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = 58312083803417020L;
  /* ********************************************** Variables ********************************************** */
  protected TableSize       tableSize        = new TableSizeImpl( this );
  
  /* ********************************************** Methods ********************************************** */
  
  @Override
  public StripeDataList<E> getRowStripeDataList()
  {
    return this.getStripeDataList( StripeType.ROW );
  }
  
  @Override
  public StripeDataList<E> getColumnStripeDataList()
  {
    return this.getStripeDataList( StripeType.COLUMN );
  }
  
  @Override
  public TableSize getTableSize()
  {
    return this.tableSize;
  }
  
}