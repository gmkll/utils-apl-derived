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
package org.omnaest.utils.table.impl;

import java.util.Arrays;
import java.util.BitSet;

import org.omnaest.utils.structure.array.ArrayUtils;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.table.Cell;
import org.omnaest.utils.table.Row;
import org.omnaest.utils.table.Table;

/**
 * @see Row
 * @author Omnaest
 * @param <E>
 */
class RowImpl<E> extends StripeImpl<E> implements Row<E>
{
  /* ************************************************** Constants *************************************************** */
  private static final long serialVersionUID = -1519020631976249637L;
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private volatile int      rowIndex;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see RowImpl
   * @param rowIndex
   * @param table
   * @param isDetached
   */
  protected RowImpl( int rowIndex, Table<E> table, boolean isDetached )
  {
    super( table, isDetached );
    this.rowIndex = rowIndex;
  }
  
  @Override
  public Row<E> add( E element )
  {
    int columnIndex = this.size();
    this.table.setElement( this.rowIndex, columnIndex, element );
    return this;
  }
  
  @Override
  public Cell<E> cell( int columnIndex )
  {
    return this.table.cell( this.rowIndex, columnIndex );
  }
  
  @Override
  public Row<E> clear()
  {
    final int size = this.size();
    for ( int ii = 0; ii < size; ii++ )
    {
      this.cell( ii ).clear();
    }
    return this;
  }
  
  @Override
  public E getElement( int columnIndex )
  {
    return this.table.getElement( this.rowIndex, columnIndex );
  }
  
  @Override
  public E getElement( String title )
  {
    return this.table.getElement( this.rowIndex, title );
  }
  
  @Override
  public String getTitle()
  {
    return this.table.getRowTitle( this.rowIndex );
  }
  
  @Override
  public void handleAddedColumn( int columnIndex, E... elements )
  {
    this.isModified = true;
  }
  
  @Override
  public void handleAddedRow( int rowIndex, E... elements )
  {
    if ( this.rowIndex >= rowIndex )
    {
      this.rowIndex++;
    }
  }
  
  @Override
  public void handleClearTable()
  {
    this.markAsDeleted();
  }
  
  @Override
  public void handleRemovedColumn( int columnIndex, E[] previousElements, String columnTitle )
  {
    this.isModified = true;
  }
  
  @Override
  public void handleRemovedRow( int rowIndex, E[] previousElements, String rowTitle )
  {
    if ( rowIndex == this.rowIndex )
    {
      this.markAsDeleted();
    }
    else if ( rowIndex < this.rowIndex )
    {
      this.rowIndex--;
    }
  }
  
  @Override
  public void handleUpdatedCell( int rowIndex, int columnIndex, E element, E previousElement )
  {
    if ( this.rowIndex == rowIndex )
    {
      this.isModified = true;
    }
  }
  
  @Override
  public void handleUpdatedRow( int rowIndex, E[] elements, E[] previousElements, BitSet modifiedIndices )
  {
    if ( this.rowIndex == rowIndex )
    {
      this.isModified = true;
    }
  }
  
  @Override
  public RowIdentity<E> id()
  {
    return new RowIdentityImpl<E>( this.table, this );
  }
  
  @Override
  public int index()
  {
    return this.rowIndex;
  }
  
  private void markAsDeleted()
  {
    this.isDeleted = true;
    this.rowIndex = -1;
  }
  
  @Override
  public Row<E> remove()
  {
    this.table.removeRow( this.rowIndex );
    return this;
  }
  
  @Override
  public Row<E> setElement( int columnIndex, E element )
  {
    this.table.setElement( this.rowIndex, columnIndex, element );
    return this;
  }
  
  @Override
  public Row<E> setElement( String columnTitle, E element )
  {
    this.table.setElement( this.rowIndex, columnTitle, element );
    return this;
  }
  
  @Override
  public Row<E> setElements( E... elements )
  {
    this.clear();
    for ( int ii = 0; ii < elements.length; ii++ )
    {
      this.setElement( ii, elements[ii] );
    }
    return this;
  }
  
  @Override
  public Row<E> setTitle( String rowTitle )
  {
    this.table.setRowTitle( this.rowIndex, rowTitle );
    return this;
  }
  
  @Override
  public int size()
  {
    return this.table.columnSize();
  }
  
  @Override
  public Row<E> moveTo( int newRowIndex )
  {
    if ( this.rowIndex != newRowIndex )
    {
      final E[] elements = this.getElements();
      final String title = this.getTitle();
      
      this.table.addRowElements( newRowIndex, elements );
      this.table.setRowTitle( newRowIndex, title );
      
      final int oldRowIndex = this.rowIndex;
      this.rowIndex = newRowIndex;
      
      this.table.removeRow( oldRowIndex );
    }
    
    return this;
  }
  
  @Override
  public Row<E> switchWith( int otherRowIndex )
  {
    return this.switchWith( this.table.row( otherRowIndex ) );
  }
  
  @Override
  public Row<E> switchWith( Row<E> otherRow )
  {
    if ( otherRow != null )
    {
      final E[] elements = this.getElements();
      final String rowTitle = this.getTitle();
      
      this.setElements( otherRow.getElements() );
      this.setTitle( otherRow.getTitle() );
      
      otherRow.setElements( elements );
      otherRow.setTitle( rowTitle );      
    }
    return this;
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "RowImpl [rowIndex=" );
    builder.append( this.rowIndex );
    builder.append( ", isDeleted=" );
    builder.append( this.isDeleted );
    builder.append( ", isModified=" );
    builder.append( this.isModified );
    builder.append( ", isDetached=" );
    builder.append( this.isDetached );
    builder.append( ", getCellElements()=" );
    builder.append( Arrays.toString( this.getElements() ) );
    builder.append( "]" );
    return builder.toString();
  }
  
  @Override
  protected String[] getOrthogonalTitles()
  {
    return ArrayUtils.valueOf( this.table.getColumnTitleList(), String.class );
  }
  
  @Override
  public Row<E> apply( ElementConverter<E, E> elementConverter )
  {
    super.apply( elementConverter );
    return this;
  }
  
  @Override
  public void handleModifiedColumnTitle( int columnIndex, String columnTitle, String columnTitlePrevious )
  {
  }
  
  @Override
  public void handleModifiedRowTitle( int rowIndex, String rowTitle, String rowTitlePrevious )
  {
  }
  
  @Override
  public void handleModifiedColumnTitles( String[] columnTitles, String[] columnTitlesPrevious )
  {
  }
  
  @Override
  public void handleModifiedRowTitles( String[] rowTitles, String[] rowTitlesPrevious )
  {
  }
  
  @Override
  public void handleModifiedTableName( String tableName, String tableNamePrevious )
  {
  }
}
