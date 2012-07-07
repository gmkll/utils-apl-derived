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

import java.util.BitSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import org.omnaest.utils.operation.special.OperationVoid;
import org.omnaest.utils.table2.ImmutableRow;
import org.omnaest.utils.table2.Table;
import org.omnaest.utils.table2.TablePersistence;
import org.omnaest.utils.table2.TablePersistenceRegistration;
import org.omnaest.utils.tuple.KeyValue;

/**
 * {@link TablePersistenceRegistration} implementation
 * 
 * @author Omnaest
 * @param <E>
 */
final class TablePersistenceRegistrationImpl<E> implements TablePersistenceRegistration<E>, TableEventHandler<E>
{
  private static final long              serialVersionUID    = -8588863418066581642L;
  
  private final Table<E>                 table;
  private final ReadWriteLock            tableLock;
  private final Set<TablePersistence<E>> tablePersistenceSet = new LinkedHashSet<TablePersistence<E>>();
  
  TablePersistenceRegistrationImpl( Table<E> table, ReadWriteLock tableLock )
  {
    this.table = table;
    this.tableLock = tableLock;
  }
  
  @Override
  public Table<E> attach( TablePersistence<E> tablePersistence )
  {
    if ( tablePersistence != null )
    {
      final Lock writeLock = this.tableLock.writeLock();
      writeLock.lock();
      try
      {
        this.synchronizeTableWithPersistence( tablePersistence );
        this.tablePersistenceSet.add( tablePersistence );
      }
      finally
      {
        writeLock.unlock();
      }
      
    }
    return this.table;
  }
  
  @Override
  public Table<E> detach( TablePersistence<E> tablePersistence )
  {
    if ( tablePersistence != null )
    {
      this.tablePersistenceSet.remove( tablePersistence );
    }
    return this.table;
  }
  
  private void executeOnAllTablePersistenceInstances( OperationVoid<TablePersistence<E>> operation )
  {
    for ( TablePersistence<E> tablePersistence : this.tablePersistenceSet )
    {
      operation.execute( tablePersistence );
    }
  }
  
  @Override
  public void handleAddedColumn( int columnIndex, E... elements )
  {
    this.updateAllRows();
  }
  
  @Override
  public void handleAddedRow( final int rowIndex, final E... elements )
  {
    this.executeOnAllTablePersistenceInstances( new OperationVoid<TablePersistence<E>>()
    {
      @Override
      public void execute( TablePersistence<E> tablePersistence )
      {
        tablePersistence.add( rowIndex, elements );
      }
    } );
    
  }
  
  @Override
  public void handleClearTable()
  {
    this.executeOnAllTablePersistenceInstances( new OperationVoid<TablePersistence<E>>()
    {
      @Override
      public void execute( TablePersistence<E> tablePersistence )
      {
        tablePersistence.removeAll();
      }
    } );
  }
  
  @Override
  public void handleRemovedColumn( int columnIndex, E[] previousElements )
  {
    this.updateAllRows();
  }
  
  @Override
  public void handleRemovedRow( final int rowIndex, E[] previousElements )
  {
    this.executeOnAllTablePersistenceInstances( new OperationVoid<TablePersistence<E>>()
    {
      @Override
      public void execute( TablePersistence<E> tablePersistence )
      {
        tablePersistence.remove( rowIndex );
      }
    } );
  }
  
  @Override
  public void handleUpdatedCell( final int rowIndex, int columnIndex, E element, E previousElement )
  {
    this.updateSingleRow( rowIndex );
  }
  
  @Override
  public void handleUpdatedRow( int rowIndex, E[] elements, E[] previousElements, BitSet modifiedIndices )
  {
    this.updateSingleRow( rowIndex );
  }
  
  private void synchronizeTableWithPersistence( TablePersistence<E> tablePersistence )
  {
    final Set<Integer> rowIndexSet = new HashSet<Integer>();
    final Iterable<KeyValue<Integer, E[]>> allElements = tablePersistence.allElements();
    if ( allElements != null )
    {
      for ( KeyValue<Integer, E[]> keyValue : allElements )
      {
        final Integer rowIndex = keyValue.getKey();
        final E[] elements = keyValue.getValue();
        if ( rowIndex == null )
        {
          rowIndexSet.add( this.table.rowSize() );
          this.table.addRowElements( elements );
        }
        else
        {
          rowIndexSet.add( rowIndex );
          this.table.addRowElements( rowIndex, elements );
        }
      }
    }
    
    for ( ImmutableRow<E> row : this.table )
    {
      final int rowIndex = row.index();
      if ( !rowIndexSet.contains( rowIndex ) )
      {
        final E[] elements = row.getElements();
        tablePersistence.add( rowIndex, elements );
      }
    }
  }
  
  private void updateAllRows()
  {
    final Table<E> table = this.table;
    this.executeOnAllTablePersistenceInstances( new OperationVoid<TablePersistence<E>>()
    {
      @Override
      public void execute( TablePersistence<E> tablePersistence )
      {
        for ( int id = 0; id < table.rowSize(); id++ )
        {
          final E[] elements = table.row( id ).getElements();
          tablePersistence.update( id, elements );
        }
      }
    } );
  }
  
  private void updateSingleRow( final int rowIndex )
  {
    final Table<E> table = this.table;
    this.executeOnAllTablePersistenceInstances( new OperationVoid<TablePersistence<E>>()
    {
      @Override
      public void execute( TablePersistence<E> tablePersistence )
      {
        final E[] elements = table.row( rowIndex ).getElements();
        tablePersistence.update( rowIndex, elements );
      }
    } );
  }
}