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
package org.omnaest.utils.structure.table.concrete.components.body;

import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.Table.Row;
import org.omnaest.utils.structure.table.Table.Stripe;
import org.omnaest.utils.structure.table.Table.Stripe.StripeType;
import org.omnaest.utils.structure.table.internal.TableInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.CellAndStripeResolver;
import org.omnaest.utils.structure.table.internal.TableInternal.ColumnInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.RowInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeList;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeListContainer;

/**
 * @see TableInternal
 * @see CellAndStripeResolver
 * @author Omnaest
 * @param <E>
 */
public class CellAndStripeResolverImpl<E> implements CellAndStripeResolver<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long  serialVersionUID = 7793892246619215531L;
  
  /* ********************************************** Variables ********************************************** */
  protected TableInternal<E> tableInternal    = null;
  
  /* ********************************************** Methods ********************************************** */

  /**
   * @param tableInternal
   */
  public CellAndStripeResolverImpl( TableInternal<E> tableInternal )
  {
    super();
    this.tableInternal = tableInternal;
  }
  
  @Override
  public Cell<E> resolveCell( int rowIndexPosition, int columnIndexPosition )
  {
    return this.resolveCell( this.resolveRow( rowIndexPosition ), this.resolveColumn( columnIndexPosition ) );
  }
  
  @Override
  public Cell<E> resolveCell( Row<E> row, int columnIndexPosition )
  {
    return this.resolveCell( row, this.resolveColumn( columnIndexPosition ) );
  }
  
  @Override
  public Cell<E> resolveCell( int rowIndexPosition, Column<E> column )
  {
    return this.resolveCell( this.resolveRow( rowIndexPosition ), column );
  }
  
  @Override
  public Cell<E> resolveCell( Row<E> row, Column<E> column )
  {
    //
    Cell<E> retval = null;
    
    //
    if ( row != null && column != null )
    {
      //
      for ( Cell<E> cell : row )
      {
        if ( column.contains( cell ) )
        {
          retval = cell;
          break;
        }
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Resolves a {@link Row} for the given {@link Row} index position.
   * 
   * @param rowIndexPosition
   * @return
   */
  @Override
  public RowInternal<E> resolveRow( int rowIndexPosition )
  {
    //
    RowInternal<E> retval = null;
    
    //
    Stripe<E> stripe = this.resolveStripe( StripeType.ROW, rowIndexPosition );
    if ( stripe instanceof RowInternal )
    {
      retval = (RowInternal<E>) stripe;
    }
    
    //
    return retval;
  }
  
  /**
   * Resolves a {@link Column} for the given {@link Column} index position.
   * 
   * @param rowIndexPosition
   * @return
   */
  @Override
  public ColumnInternal<E> resolveColumn( int columnIndexPosition )
  {
    //
    ColumnInternal<E> retval = null;
    
    //
    Stripe<E> stripe = this.resolveStripe( StripeType.COLUMN, columnIndexPosition );
    if ( stripe instanceof ColumnInternal )
    {
      retval = (ColumnInternal<E>) stripe;
    }
    
    //
    return retval;
  }
  
  /**
   * Resolves the {@link Stripe} for the given {@link StripeType} and index position from the internal {@link Table} reference.
   * 
   * @param stripeType
   * @param indexPosition
   * @return
   */
  protected Stripe<E> resolveStripe( StripeType stripeType, int indexPosition )
  {
    //
    Stripe<E> retval = null;
    
    //
    if ( stripeType != null )
    {
      //
      StripeListContainer<E> stripeListContainer = this.tableInternal.getStripeListContainer();
      
      //
      StripeList<E> stripeList = stripeListContainer.getStripeList( stripeType );
      
      //
      retval = stripeList.get( indexPosition );
    }
    
    //
    return retval;
  }
  
}