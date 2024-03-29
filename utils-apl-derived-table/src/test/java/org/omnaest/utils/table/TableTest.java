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
package org.omnaest.utils.table;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.omnaest.utils.events.exception.basic.ExceptionHandlerEPrintStackTrace;
import org.omnaest.utils.structure.array.ArrayUtils;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.collection.set.SetUtils;
import org.omnaest.utils.structure.element.KeyExtractor;
import org.omnaest.utils.structure.element.ValueExtractor;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.iterator.IterableUtils;
import org.omnaest.utils.structure.map.MapUtils;
import org.omnaest.utils.table.ImmutableTableSerializer.Marshaller.MarshallingConfiguration;
import org.omnaest.utils.table.ImmutableTableSerializer.MarshallerCsv.CSVMarshallingConfiguration;
import org.omnaest.utils.table.impl.ArrayTable;
import org.omnaest.utils.table.impl.datasource.TableDataSourceResultSet;
import org.omnaest.utils.table.impl.persistence.SimpleFileBasedTablePersistence;

/**
 * @see Table
 * @author Omnaest
 */
public abstract class TableTest
{
  protected static class SimpleTestBean
  {
    private String c0;
    private String c1;
    private String c2;
    private String c3;
    private String c4;
    
    public SimpleTestBean()
    {
      super();
    }
    
    public String getC0()
    {
      return this.c0;
    }
    
    public void setC0( String c0 )
    {
      this.c0 = c0;
    }
    
    public String getC1()
    {
      return this.c1;
    }
    
    public void setC1( String c1 )
    {
      this.c1 = c1;
    }
    
    public String getC2()
    {
      return this.c2;
    }
    
    public void setC2( String c2 )
    {
      this.c2 = c2;
    }
    
    public String getC4()
    {
      return this.c4;
    }
    
    public void setC4( String c4 )
    {
      this.c4 = c4;
    }
    
    public String getC3()
    {
      return this.c3;
    }
    
    public void setC3( String c3 )
    {
      this.c3 = c3;
    }
    
  }
  
  protected static class TestDomain
  {
    private String fieldString;
    private Date   fieldDate;
    private Double fieldDouble;
    private Long   fieldLong;
    
    public TestDomain()
    {
      super();
    }
    
    public String getFieldString()
    {
      return this.fieldString;
    }
    
    public void setFieldString( String fieldString )
    {
      this.fieldString = fieldString;
    }
    
    public Date getFieldDate()
    {
      return this.fieldDate;
    }
    
    public void setFieldDate( Date fieldDate )
    {
      this.fieldDate = fieldDate;
    }
    
    public Double getFieldDouble()
    {
      return this.fieldDouble;
    }
    
    public void setFieldDouble( Double fieldDouble )
    {
      this.fieldDouble = fieldDouble;
    }
    
    public Long getFieldLong()
    {
      return this.fieldLong;
    }
    
    public void setFieldLong( Long fieldLong )
    {
      this.fieldLong = fieldLong;
    }
    
  }
  
  private static void assertSameColumnOrRowValue( String[] cellElements, final int index )
  {
    List<String> elementList = ListUtils.valueOf( cellElements );
    elementList = ListUtils.convert( elementList, new ElementConverter<String, String>()
    {
      @Override
      public String convert( String element )
      {
        return element.split( ":" )[index];
      }
    } );
    Set<String> elementRowSet = SetUtils.valueOf( elementList );
    assertEquals( 1, elementRowSet.size() );
  }
  
  protected static Date newRelativeDate( final Date date, int ii )
  {
    final Date fieldDate;
    {
      final Calendar calendar = Calendar.getInstance();
      calendar.setTime( date );
      calendar.add( Calendar.DAY_OF_MONTH, ii );
      fieldDate = calendar.getTime();
    }
    return fieldDate;
  }
  
  protected Table<String> filledTable( int rowSize, int columnSize )
  {
    String[][] elementMatrix = new String[rowSize][columnSize];
    for ( int ii = 0; ii < rowSize; ii++ )
    {
      for ( int jj = 0; jj < columnSize; jj++ )
      {
        elementMatrix[ii][jj] = ii + ":" + jj;
      }
    }
    return this.newTable( elementMatrix, String.class );
  }
  
  protected Table<String> filledTableWithTitles( int rowSize, int columnSize )
  {
    final Table<String> table = this.filledTable( rowSize, columnSize );
    
    table.setTableName( "table name" );
    
    List<String> columnTitleList = new ArrayList<String>();
    for ( int ii = 0; ii < columnSize; ii++ )
    {
      columnTitleList.add( "c" + ii );
    }
    table.setColumnTitles( columnTitleList );
    
    List<String> rowTitleList = new ArrayList<String>();
    for ( int ii = 0; ii < rowSize; ii++ )
    {
      rowTitleList.add( "r" + ii );
    }
    table.setRowTitles( rowTitleList );
    
    return table;
  }
  
  public abstract <E> Table<E> newTable( E[][] elementMatrix, Class<E> type );
  
  @SuppressWarnings({ "unchecked", "cast" })
  @Test
  public void testAdapterOneColumnMap()
  {
    Table<String> table = this.filledTableWithTitles( 10, 4 );
    
    final int columnIndexKey = 1;
    Map<String, Set<Row<String>>> map = table.as().map( columnIndexKey );
    assertNotNull( map );
    assertEquals( 10, map.size() );
    
    {
      final Set<Row<String>> rowSet = map.get( "0:1" );
      assertEquals( 1, rowSet.size() );
      assertArrayEquals( table.row( 0 ).getElements(), rowSet.iterator().next().getElements() );
    }
    {
      Set<Row<String>> previous = map.put( "0:1", SetUtils.<Row<String>> valueOf( (Row<String>) table.row( 9 ) ) );
      assertArrayEquals( table.row( 0 ).getElements(), previous.iterator().next().getElements() );
    }
    {
      Set<Row<String>> remove = map.remove( "9:1" );
      final Row<String> row = IterableUtils.firstElement( remove );
      assertTrue( row.isDeleted() );
      assertEquals( 9, table.rowSize() );
    }
  }
  
  @Test
  public void testAdapterTwoColumnMap()
  {
    Table<String> table = this.filledTableWithTitles( 10, 4 );
    
    final int columnIndexKey = 1;
    final int columnIndexValue = 3;
    Map<String, Set<String>> map = table.as().map( columnIndexKey, columnIndexValue );
    assertNotNull( map );
    
    {
      assertEquals( 10, map.size() );
      assertEquals( SetUtils.valueOf( "0:3" ), map.get( "0:1" ) );
      assertEquals( SetUtils.valueOf( "9:3" ), map.get( "9:1" ) );
      assertEquals( SetUtils.emptySet(), map.get( "10:1" ) );
      assertEquals( SetUtils.emptySet(), map.get( "0:2" ) );
      assertTrue( map.containsKey( "0:1" ) );
      assertEquals( table.column( 1 ).to().set(), map.keySet() );
    }
    {
      Set<String> previous = map.put( "0:1", SetUtils.valueOf( "xxx" ) );
      assertEquals( SetUtils.valueOf( "0:3" ), previous );
      assertEquals( SetUtils.valueOf( "xxx" ), map.get( "0:1" ) );
      assertEquals( "xxx", table.cell( 0, 3 ).getElement() );
    }
    {
      Cell<String> cell = table.cell( 0, 3 );
      Set<String> remove = map.remove( "0:1" );
      assertEquals( SetUtils.valueOf( "xxx" ), remove );
      assertEquals( SetUtils.valueOf( (String) null ), map.get( "0:1" ) );
      assertTrue( cell.isModified() );
    }
  }
  
  @Test
  public void testAddColumn()
  {
    Table<String> table = this.filledTable( 3, 4 );
    
    table.addColumnElements( "a", "b", "c" );
    
    assertEquals( 5, table.columnSize() );
    Column<String> column = table.column( table.columnSize() - 1 );
    assertArrayEquals( new String[] { "a", "b", "c" }, column.to().array() );
  }
  
  @Test
  public void testColumn() throws Exception
  {
    Table<String> table = this.newTable( new String[][] { { "a", "b", "c" }, { "d", "e", "f" } }, String.class );
    
    {
      Column<String> column = table.column( 0 );
      assertNotNull( column );
      assertEquals( "a", column.getElement( 0 ) );
      assertEquals( "d", column.getElement( 1 ) );
      assertEquals( null, column.getElement( 2 ) );
      assertEquals( null, column.getElement( -1 ) );
      
      column.setCellElement( 0, "a2" );
      assertEquals( "a2", column.getElement( 0 ) );
    }
    {
      Column<String> column = table.column( 2 );
      assertNotNull( column );
      assertEquals( "c", column.getElement( 0 ) );
      assertEquals( "f", column.getElement( 1 ) );
      assertEquals( null, column.getElement( 2 ) );
    }
    {
      Column<String> column = table.column( 3 );
      assertNotNull( column );
      assertEquals( null, column.getElement( 0 ) );
    }
    {
      assertNull( table.column( -1 ) );
    }
    
  }
  
  @Test
  public void testColumns()
  {
    Table<String> table = this.filledTableWithTitles( 10, 8 );
    
    {
      Iterable<Column<String>> columns = table.columns( "c1", "c3" );
      assertEquals( 2, IterableUtils.size( columns ) );
      assertEquals( table.column( 1 ).id(), IterableUtils.elementAt( columns, 0 ).id() );
      assertEquals( table.column( 3 ).id(), IterableUtils.elementAt( columns, 1 ).id() );
    }
    {
      Iterable<Column<String>> columns = table.columns( Pattern.compile( "c1|c3" ) );
      assertEquals( 2, IterableUtils.size( columns ) );
      assertEquals( table.column( 1 ).id(), IterableUtils.elementAt( columns, 0 ).id() );
      assertEquals( table.column( 3 ).id(), IterableUtils.elementAt( columns, 1 ).id() );
    }
  }
  
  @Test
  public void testExecuteWithLocks() throws InterruptedException,
                                    ExecutionException
  {
    final Table<String> table = this.filledTableWithTitles( 100, 3 );
    
    ExecutorService executorService = Executors.newFixedThreadPool( 10 );
    final List<Future<Boolean>> futureList = new ArrayList<Future<Boolean>>();
    for ( int ii = 0; ii < 10; ii++ )
    {
      futureList.add( executorService.submit( new Callable<Boolean>()
      {
        @Override
        public Boolean call() throws Exception
        {
          final AtomicBoolean retval = new AtomicBoolean();
          
          table.executeWithReadLock( new TableExecution<ImmutableTable<String>, String>()
          {
            @Override
            public void execute( ImmutableTable<String> table )
            {
              ImmutableRow<String> row = table.row( 10 );
              final List<String> elementList = row.to().list();
              
              try
              {
                Thread.sleep( 5 );
              }
              catch ( InterruptedException e )
              {
              }
              
              retval.set( elementList.equals( table.row( 10 ).to().list() ) );
            }
            
          } );
          
          return retval.get();
        }
      } ) );
      futureList.add( executorService.submit( new Callable<Boolean>()
      {
        @Override
        public Boolean call() throws Exception
        {
          final AtomicBoolean retval = new AtomicBoolean();
          
          table.executeWithWriteLock( new TableExecution<Table<String>, String>()
          {
            @Override
            public void execute( Table<String> table )
            {
              Row<String> row = table.row( 10 );
              row.cell( 1 ).setElement( "xxx" + Math.random() );
              final List<String> elementList = row.to().list();
              
              try
              {
                Thread.sleep( 5 );
              }
              catch ( InterruptedException e )
              {
              }
              
              retval.set( elementList.equals( table.row( 10 ).to().list() ) );
            }
          } );
          
          return retval.get();
        }
      } ) );
    }
    
    executorService.shutdown();
    
    for ( Future<Boolean> future : futureList )
    {
      assertTrue( future.get() );
    }
  }
  
  @Test
  public void testGetAndSetCellElement() throws Exception
  {
    Table<String> table = this.newTable( new String[][] { { "a", "b", "c" }, { "d", "e", "f" } }, String.class );
    
    {
      Cell<String> cell = table.cell( 0, 0 );
      assertNotNull( cell );
      assertEquals( "a", cell.getElement() );
      assertEquals( 0, cell.columnIndex() );
      assertEquals( 0, cell.rowIndex() );
      
      table.addRowElements( 0, new String[] { "g", "h", "i" } );
      assertEquals( "a", cell.getElement() );
      assertEquals( 0, cell.columnIndex() );
      assertEquals( 1, cell.rowIndex() );
    }
    
    {
      Cell<String> cell = table.cell( 0, 2 );
      assertNotNull( cell );
      assertEquals( "i", cell.getElement() );
      assertEquals( 2, cell.columnIndex() );
      assertEquals( 0, cell.rowIndex() );
    }
    
    {
      Cell<String> cell = table.cell( 0, 3 );
      assertNotNull( cell );
      assertEquals( null, cell.getElement() );
      assertEquals( 3, cell.columnIndex() );
      assertEquals( 0, cell.rowIndex() );
    }
    
    {
      assertNull( table.cell( -1, 0 ) );
      assertNull( table.cell( 0, -1 ) );
    }
  }
  
  @Test
  public void testIndex() throws Exception
  {
    Table<String> table = this.filledTable( 100, 5 );
    
    TableIndex<String, Cell<String>> tableIndex = table.index().of( 1 );
    assertNotNull( tableIndex );
    
    {
      assertFalse( tableIndex.containsKey( "0:0" ) );
      assertTrue( tableIndex.containsKey( "0:1" ) );
      
      table.setElement( 0, 1, "xxx" );
      assertFalse( tableIndex.containsKey( "0:1" ) );
      assertTrue( tableIndex.containsKey( "xxx" ) );
      
      Set<Cell<String>> cellSet = tableIndex.get( "10:1" );
      assertEquals( 1, cellSet.size() );
    }
    {
      assertSame( tableIndex, table.index().of( 1 ) );
      assertSame( tableIndex, table.index().of( table.column( 1 ) ) );
    }
    {
      SortedMap<String, Set<Cell<String>>> sortedMap = tableIndex;
      Set<Cell<String>> set = sortedMap.get( "10:1" );
      assertNotNull( set );
      assertEquals( 1, set.size() );
      Cell<String> cell = set.iterator().next();
      assertEquals( "10:1", cell.getElement() );
    }
    {
      table.clear();
      assertTrue( tableIndex.isEmpty() );
    }
  }
  
  @Test
  public void testIterator()
  {
    //
    final String[][] elementMatrix = new String[][] { { "a", "b", "c" }, { "d", "e", "f" } };
    Table<String> tableAbstract = this.newTable( elementMatrix, String.class );
    
    //
    Iterator<ImmutableRow<String>> iterator = tableAbstract.iterator();
    assertNotNull( iterator );
    assertTrue( iterator.hasNext() );
    assertTrue( iterator.hasNext() );
    assertArrayEquals( elementMatrix[0], ArrayUtils.valueOf( iterator.next(), String.class ) );
    assertTrue( iterator.hasNext() );
    assertArrayEquals( elementMatrix[1], ArrayUtils.valueOf( iterator.next(), String.class ) );
    assertFalse( iterator.hasNext() );
    
  }
  
  @Test
  public void testLargeRemoveAddCycles()
  {
    final int rowSizeMax = 20;
    final int columnSizeMax = 10;
    final int cycles = 16 + 4;
    Table<String> table = this.filledTableWithTitles( rowSizeMax, columnSizeMax );
    //System.out.println( table );
    
    final int rowSize = table.rowSize();
    for ( int ii = 0; ii < cycles; ii++ )
    {
      {
        final int rowIndex = (int) ( rowSizeMax * Math.random() );
        Row<String> row = table.row( rowIndex );
        String[] cellElements = row.getElements();
        String title = row.getTitle();
        
        row.remove();
        
        table.addRowElements( cellElements );
        final Row<String> lastRow = table.row( table.rowSize() - 1 );
        lastRow.setTitle( title );
        assertArrayEquals( cellElements, lastRow.getElements() );
      }
      
      for ( int jj = 0; jj < cycles; jj++ )
      {
        final int columnIndex = (int) ( columnSizeMax * Math.random() );
        Column<String> column = table.column( columnIndex );
        final String[] cellElements = column.getElements();
        final String title = column.getTitle();
        
        column.remove();
        
        final Column<String> column19 = table.column( columnSizeMax - 1 );
        for ( int rowIndex = 0; rowIndex < rowSize; rowIndex++ )
        {
          column19.setCellElement( rowIndex, cellElements[rowIndex] );
        }
        column19.setTitle( title );
        assertArrayEquals( cellElements, column19.getElements() );
      }
    }
    
    assertEquals( rowSizeMax, rowSize );
    assertEquals( columnSizeMax, table.columnSize() );
    
    int counter = 0;
    for ( Cell<String> cell : table.cells() )
    {
      assertNotNull( cell.getElement() );
      counter++;
    }
    assertEquals( rowSizeMax * columnSizeMax, counter );
    
    for ( Row<String> row : table.rows() )
    {
      String[] cellElements = row.getElements();
      assertSameColumnOrRowValue( cellElements, 0 );
    }
    
    for ( Column<String> column : table.columns() )
    {
      String[] cellElements = column.getElements();
      assertSameColumnOrRowValue( cellElements, 1 );
    }
    
    //System.out.println( table );
  }
  
  @Test
  public void testRemoveColumn() throws Exception
  {
    Table<String> table = this.filledTableWithTitles( 10, 5 );
    Column<String> column4 = table.column( 4 );
    Column<String> column2 = table.column( 2 );
    assertFalse( column4.isDeleted() );
    assertFalse( column2.isDeleted() );
    
    table.removeColumn( 2 );
    assertEquals( 4, table.columnSize() );
    assertFalse( column4.isDeleted() );
    assertTrue( column2.isDeleted() );
    
    column4.remove();
    assertTrue( column4.isDeleted() );
    assertEquals( 3, table.columnSize() );
  }
  
  @SuppressWarnings("cast")
  @Test
  public void testRow()
  {
    Table<String> table = this.newTable( new String[][] { { "a", "b", "c" }, { "d", "e", "f" } }, String.class );
    
    String[] values = new String[] { "a", "b", "c" };
    table.addRowElements( values );
    
    {
      Row<String> row = table.row( 0 );
      assertEquals( Arrays.asList( values ), ListUtils.valueOf( (Iterable<String>) row ) );
    }
    {
      Row<String> row = table.row( 1 );
      assertEquals( Arrays.asList( "d", "e", "f" ), ListUtils.valueOf( (Iterable<String>) row ) );
    }
    {
      Row<String> row = table.row( 2 );
      assertEquals( Arrays.asList( "a", "b", "c" ), ListUtils.valueOf( (Iterable<String>) row ) );
    }
    {
      Row<String> row = table.row( 0 );
      row.setElement( 1, "b2" );
      assertEquals( "b2", row.getElement( 1 ) );
    }
    {
      assertNull( table.row( -1 ) );
    }
    {
      BitSet indexFilter = new BitSet();
      indexFilter.set( 1 );
      indexFilter.set( 2 );
      Iterable<Row<String>> rows = table.rows( indexFilter );
      assertEquals( 2, IterableUtils.size( rows ) );
      assertEquals( table.row( 1 ).id(), IterableUtils.elementAt( rows, 0 ).id() );
      assertEquals( table.row( 2 ).id(), IterableUtils.elementAt( rows, 1 ).id() );
    }
    
  }
  
  @Test
  public void testRows()
  {
    Table<String> table = this.filledTableWithTitles( 10, 5 );
    {
      final Iterable<Map<String, String>> maps = table.rows( 3, 6 ).to().maps();
      assertEquals( 3, IterableUtils.size( maps ) );
      Map<String, String> map = maps.iterator().next();
      assertEquals( SetUtils.valueOf( table.getColumnTitleList() ), map.keySet() );
    }
  }
  
  @Test
  public void testSelect() throws Exception
  {
    Table<String> table = this.filledTableWithTitles( 20, 4 );
    Table<String> table2 = this.filledTableWithTitles( 10, 8 );
    Table<String> table3 = this.filledTableWithTitles( 3, 3 );
    
    table.setTableName( "table1" );
    table2.setTableName( "table2" );
    table3.setTableName( "table3" );
    
    {
      Table<String> result = table.select().allColumns( table ).as().table();
      assertTrue( result.equalsInContent( table ) );
    }
    {
      Table<String> result = table.select().allColumns().as().table();
      assertTrue( result.equalsInContent( table ) );
    }
    {
      Table<String> result = table.select().columns( 0, 1, 2, 3 ).as().table();
      assertTrue( result.equalsInContent( table ) );
    }
    {
      Table<String> result = table.select().column( 0 ).allColumns( table ).as().table();
      assertTrue( table.equalsInContent( result.select().columns( 1, 2, 3, 4 ).as().table() ) );
      assertEquals( table.column( 0 ).to().list(), result.column( 0 ).to().list() );
      assertEquals( table.column( 0 ).to().list(), result.column( 1 ).to().list() );
    }
    {
      Table<String> result = table.select()
                                  .column( 1 )
                                  .join( table2 )
                                  .allColumns()
                                  .onEqual( table.column( 0 ), table2.column( 0 ) )
                                  .as()
                                  .table();
      assertNotNull( result );
      assertEquals( 10, result.rowSize() );
      assertEquals( "0:1", result.getElement( 0, 0 ) );
      assertEquals( "0:0", result.getElement( 0, 1 ) );
      assertEquals( "0:1", result.getElement( 0, 2 ) );
      assertEquals( "0:6", result.getElement( 0, 7 ) );
      assertEquals( "0:7", result.getElement( 0, 8 ) );
      assertNull( result.getElement( 0, 9 ) );
      
      assertEquals( "9:1", result.getElement( 9, 0 ) );
      assertEquals( "9:0", result.getElement( 9, 1 ) );
      assertEquals( "9:1", result.getElement( 9, 2 ) );
      assertEquals( "9:6", result.getElement( 9, 7 ) );
      assertEquals( "9:7", result.getElement( 9, 8 ) );
    }
    {
      Table<String> result = table.select()
                                  .columns( 1, 2 )
                                  .join( table2 )
                                  .columns( 6, 7 )
                                  .onEqual( table.column( 0 ), table2.column( 0 ) )
                                  .as()
                                  .table();
      assertNotNull( result );
      assertEquals( 10, result.rowSize() );
      assertEquals( "0:1", result.getElement( 0, 0 ) );
      assertEquals( "0:2", result.getElement( 0, 1 ) );
      assertEquals( "0:6", result.getElement( 0, 2 ) );
      assertEquals( "0:7", result.getElement( 0, 3 ) );
      
      assertEquals( "9:1", result.getElement( 9, 0 ) );
      assertEquals( "9:2", result.getElement( 9, 1 ) );
      assertEquals( "9:6", result.getElement( 9, 2 ) );
      assertEquals( "9:7", result.getElement( 9, 3 ) );
      
    }
    
    {
      /*
       * Cartesian product with table1, table2 and table3
       */
      Table<String> result = table.select()
                                  .columns( 1, 2 )
                                  .join( table2 )
                                  .columns( 6, 7 )
                                  .onEqual( table.column( 0 ), table2.column( 0 ) )
                                  .join( table3 )
                                  .column( 0 )
                                  .as()
                                  .table();
      
      //System.out.println( result );
      
      /*
          0:1,0:2,0:6,0:7,0:0
          0:1,0:2,0:6,0:7,1:0
          0:1,0:2,0:6,0:7,2:0
          1:1,1:2,1:6,1:7,0:0
          ...
          8:1,8:2,8:6,8:7,2:0
          9:1,9:2,9:6,9:7,0:0
          9:1,9:2,9:6,9:7,1:0
          9:1,9:2,9:6,9:7,2:0
       */
      
      assertNotNull( result );
      assertEquals( 30, result.rowSize() );
      assertEquals( "0:1", result.getElement( 0, 0 ) );
      assertEquals( "0:2", result.getElement( 0, 1 ) );
      assertEquals( "0:6", result.getElement( 0, 2 ) );
      assertEquals( "0:7", result.getElement( 0, 3 ) );
      assertEquals( "0:0", result.getElement( 0, 4 ) );
      
      assertEquals( "0:1", result.getElement( 2, 0 ) );
      assertEquals( "0:2", result.getElement( 2, 1 ) );
      assertEquals( "0:6", result.getElement( 2, 2 ) );
      assertEquals( "0:7", result.getElement( 2, 3 ) );
      assertEquals( "2:0", result.getElement( 2, 4 ) );
      
      assertEquals( "9:1", result.getElement( 27, 0 ) );
      assertEquals( "9:2", result.getElement( 27, 1 ) );
      assertEquals( "9:6", result.getElement( 27, 2 ) );
      assertEquals( "9:7", result.getElement( 27, 3 ) );
      assertEquals( "0:0", result.getElement( 27, 4 ) );
      
      assertEquals( "9:1", result.getElement( 29, 0 ) );
      assertEquals( "9:2", result.getElement( 29, 1 ) );
      assertEquals( "9:6", result.getElement( 29, 2 ) );
      assertEquals( "9:7", result.getElement( 29, 3 ) );
      assertEquals( "2:0", result.getElement( 29, 4 ) );
      
    }
    {
      Table<String> result = table.select()
                                  .columns( 1, 2 )
                                  .join( table2 )
                                  .columns( 6, 7 )
                                  .onEqual( table.column( 0 ), table2.column( 0 ) )
                                  .join( table3 )
                                  .onEqual( table.column( 0 ), table3.column( 0 ) )
                                  .column( 0 )
                                  .as()
                                  .table();
      
      //System.out.println( result );
      
      /*
          0:1,0:2,0:6,0:7,0:0
          1:1,1:2,1:6,1:7,1:0
          2:1,2:2,2:6,2:7,2:0
       */
      
      assertNotNull( result );
      assertEquals( 3, result.rowSize() );
      assertEquals( "0:1", result.getElement( 0, 0 ) );
      assertEquals( "0:2", result.getElement( 0, 1 ) );
      assertEquals( "0:6", result.getElement( 0, 2 ) );
      assertEquals( "0:7", result.getElement( 0, 3 ) );
      assertEquals( "0:0", result.getElement( 0, 4 ) );
      
      assertEquals( "2:1", result.getElement( 2, 0 ) );
      assertEquals( "2:2", result.getElement( 2, 1 ) );
      assertEquals( "2:6", result.getElement( 2, 2 ) );
      assertEquals( "2:7", result.getElement( 2, 3 ) );
      assertEquals( "2:0", result.getElement( 2, 4 ) );
      
    }
    {
      Table<String> result = table.select()
                                  .columns( 1, 2 )
                                  .join( table2 )
                                  .columns( 6, 7 )
                                  .onEqual( table.column( 0 ), table2.column( 0 ) )
                                  .join( table3 )
                                  .onEqual( table.column( 1 ), table3.column( 1 ) )
                                  .column( 0 )
                                  .as()
                                  .table();
      
      //      System.out.println( table );
      //      System.out.println( table2 );
      //      System.out.println( table3 );
      //      System.out.println( result );
      
      /*
          0:1,0:2,0:6,0:7,0:0
          1:1,1:2,1:6,1:7,1:0
          2:1,2:2,2:6,2:7,2:0
       */
      
      assertNotNull( result );
      assertEquals( 3, result.rowSize() );
      assertEquals( "0:1", result.getElement( 0, 0 ) );
      assertEquals( "0:2", result.getElement( 0, 1 ) );
      assertEquals( "0:6", result.getElement( 0, 2 ) );
      assertEquals( "0:7", result.getElement( 0, 3 ) );
      assertEquals( "0:0", result.getElement( 0, 4 ) );
      
      assertEquals( "2:1", result.getElement( 2, 0 ) );
      assertEquals( "2:2", result.getElement( 2, 1 ) );
      assertEquals( "2:6", result.getElement( 2, 2 ) );
      assertEquals( "2:7", result.getElement( 2, 3 ) );
      assertEquals( "2:0", result.getElement( 2, 4 ) );
      
    }
    
    {
      SortedMap<String, Set<Row<String>>> result = table.select()
                                                        .columns( 2, 1 )
                                                        .join( table2 )
                                                        .columns( 6, 7 )
                                                        .onEqual( table.column( 0 ), table2.column( 0 ) )
                                                        .join( table3 )
                                                        .onEqual( table.column( 1 ), table3.column( 1 ) )
                                                        .column( 0 )
                                                        .as()
                                                        .sortedMap();
      
      // System.out.println( result );
      
      /*
               index
                / 
          0:1,0:2,0:6,0:7,0:0
          1:1,1:2,1:6,1:7,1:0
          2:1,2:2,2:6,2:7,2:0
       */
      
      assertNotNull( result );
      assertEquals( 3, result.size() );
      assertArrayEquals( new String[] { "0:2", "0:1", "0:6", "0:7", "0:0" }, result.get( "0:2" ).iterator().next().to().array() );
      assertArrayEquals( new String[] { "2:2", "2:1", "2:6", "2:7", "2:0" }, result.get( "2:2" ).iterator().next().to().array() );
    }
  }
  
  @Test
  public void testSerializationJavaSerializable()
  {
    Table<String> table = this.filledTableWithTitles( 4, 5 );
    Table<String> clone = SerializationUtils.clone( table );
    assertTrue( table.equalsInContent( clone ) );
    assertTrue( table.equalsInContentAndMetaData( clone ) );
    
    //System.out.println( clone );
  }
  
  @Test
  public void testSerializationJson()
  {
    Table<String> table = this.filledTableWithTitles( 10, 5 );
    table.setExceptionHandler( new ExceptionHandlerEPrintStackTrace() );
    
    final MarshallingConfiguration configuration = new MarshallingConfiguration().setHasEnabledColumnTitles( true )
                                                                                 .setHasEnabledRowTitles( true )
                                                                                 .setHasEnabledTableName( true );
    
    String content = table.serializer().marshal().asJson().using( configuration ).toString();
    //System.out.println( content );
    
    Table<String> clone = new ArrayTable<String>( String.class ).serializer()
                                                                .unmarshal()
                                                                .asJson()
                                                                .using( configuration )
                                                                .from( content );
    
    //System.out.println( clone );
    assertTrue( table.equalsInContent( clone ) );
    assertTrue( table.equalsInContentAndMetaData( clone ) );
    
  }
  
  @Test
  public void testSerializationXML()
  {
    Table<String> table = this.filledTableWithTitles( 10, 5 );
    table.setExceptionHandler( new ExceptionHandlerEPrintStackTrace() );
    
    final MarshallingConfiguration configuration = new MarshallingConfiguration().setHasEnabledColumnTitles( true )
                                                                                 .setHasEnabledRowTitles( true )
                                                                                 .setHasEnabledTableName( true );
    
    String content = table.serializer().marshal().asXml().using( configuration ).toString();
    //System.out.println( content );
    
    Table<String> clone = new ArrayTable<String>( String.class ).serializer()
                                                                .unmarshal()
                                                                .asXml()
                                                                .using( configuration )
                                                                .from( content );
    
    //System.out.println( clone );
    assertTrue( table.equalsInContent( clone ) );
    assertTrue( table.equalsInContentAndMetaData( clone ) );
    
  }
  
  @Test
  public void testSerializingCSV()
  {
    Table<String> table = this.filledTableWithTitles( 20, 3 );
    
    final CSVMarshallingConfiguration configuration = new CSVMarshallingConfiguration().setHasEnabledRowTitles( true )
                                                                                       .setHasEnabledTableName( true );
    String content = table.serializer().marshal().asCsv().using( configuration ).toString();
    
    //System.out.println( content );
    
    Table<String> result = new ArrayTable<String>( String.class ).serializer()
                                                                 .unmarshal()
                                                                 .asCsv()
                                                                 .using( configuration )
                                                                 .from( content );
    assertTrue( table.equalsInContentAndMetaData( result ) );
    
  }
  
  @Test
  public void testSerializingPlainText()
  {
    Table<String> table = this.filledTableWithTitles( 20, 3 );
    
    final MarshallingConfiguration configuration = new MarshallingConfiguration().setHasEnabledRowTitles( true )
                                                                                 .setHasEnabledTableName( true );
    String content = table.serializer().marshal().asPlainText().using( configuration ).toString();
    
    //System.out.println( table );
    
    Table<String> result = new ArrayTable<String>( String.class ).serializer()
                                                                 .unmarshal()
                                                                 .asPlainText()
                                                                 .using( configuration )
                                                                 .from( content );
    assertTrue( table.equalsInContentAndMetaData( result ) );
    
  }
  
  @Test
  public void testTo() throws Exception
  {
    final String[][] elementMatrix = new String[][] { { "a", "b", "c" }, { "d", "e", "f" } };
    Table<String> table = this.newTable( elementMatrix, String.class );
    
    final String[][] array = table.to().array();
    assertArrayEquals( elementMatrix, array );
  }
  
  @Test
  public void testToMap() throws Exception
  {
    Table<String> table = this.filledTable( 3, 4 );
    {
      final SortedMap<String, String[]> sortedMap = table.to().sortedMap( 1 );
      assertNotNull( sortedMap );
      assertEquals( 3, sortedMap.size() );
      assertEquals( Arrays.asList( "0:1", "1:1", "2:1" ), ListUtils.valueOf( sortedMap.keySet() ) );
      assertArrayEquals( new String[] { "1:0", "1:1", "1:2", "1:3" }, sortedMap.get( "1:1" ) );
    }
    {
      final SortedMap<String, String> sortedMap = table.to().sortedMap( 1, 3 );
      assertNotNull( sortedMap );
      assertEquals( 3, sortedMap.size() );
      assertEquals( Arrays.asList( "0:1", "1:1", "2:1" ), ListUtils.valueOf( sortedMap.keySet() ) );
      assertEquals( "1:3", sortedMap.get( "1:1" ) );
    }
  }
  
  @Test
  public void testToString() throws Exception
  {
    Table<String> table = this.filledTable( 3, 4 );
    String content = table.to().string();
    
    //System.out.println( string );
    assertNotNull( content );
    assertEquals( table.toString(), content );
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testSortBy()
  {
    Table<String> table = this.filledTableWithTitles( 10, 4 );
    //System.out.println( table );
    
    {
      Table<String> tableSorted = table.clone();
      tableSorted.row( 4 ).switchWith( 0 );
      tableSorted.row( 1 ).switchWith( 9 );
      tableSorted.row( 8 ).switchWith( 2 );
      tableSorted.row( 3 ).switchWith( 7 );
      assertEquals( "0:0", tableSorted.row( 4 ).getElement( 0 ) );
      assertEquals( "4:0", tableSorted.row( 0 ).getElement( 0 ) );
      assertEquals( "1:0", tableSorted.row( 9 ).getElement( 0 ) );
      assertEquals( "9:0", tableSorted.row( 1 ).getElement( 0 ) );
      //System.out.println( tableSorted );
      
      tableSorted.sort().by( 1 );
      
      //System.out.println( tableSorted );
      assertEquals( table.rowSize(), tableSorted.rowSize() );
      assertTrue( table.equalsInContent( tableSorted ) );
      assertTrue( table.equalsInContentAndMetaData( tableSorted ) );
    }
    
    {
      Table<String> tableSorted = table.clone();
      tableSorted.row( 4 ).switchWith( 0 );
      tableSorted.row( 1 ).switchWith( 9 );
      tableSorted.row( 8 ).switchWith( 2 );
      tableSorted.row( 3 ).switchWith( 7 );
      tableSorted.sort()
                 .withTableLock()
                 .using( ComparatorUtils.reversedComparator( ComparatorUtils.naturalComparator() ) )
                 .by( 1 );
      //System.out.println( tableSorted );
      assertEquals( table.rowSize(), tableSorted.rowSize() );
      assertEquals( "0:0", tableSorted.row( 9 ).getElement( 0 ) );
      assertEquals( "9:0", tableSorted.row( 0 ).getElement( 0 ) );
    }
  }
  
  @Test
  public void testPersistence()
  {
    final File file = null;// new File( "target/persistence.data" );
    final TablePersistence<String> tablePersistence = new SimpleFileBasedTablePersistence<String>(
                                                                                                   file,
                                                                                                   new ExceptionHandlerEPrintStackTrace() );
    
    Table<String> table = this.filledTable( 20, 5 );
    table.persistence().attach( tablePersistence );
    assertEquals( 20, table.rowSize() );
    
    {
      Table<String> tableOther = new ArrayTable<String>( String.class ).persistence().attach( tablePersistence );
      //System.out.println( tableOther );
      assertEquals( table.rowSize(), tableOther.rowSize() );
      assertTrue( table.equalsInContent( tableOther ) );
    }
    
    table.row( 16 ).switchWith( 4 );
    table.row( 5 ).switchWith( 15 );
    table.row( 14 ).switchWith( 6 );
    table.row( 7 ).switchWith( 14 );
    //System.out.println( table );
    
    {
      Table<String> tableOther = new ArrayTable<String>( String.class ).persistence().attach( tablePersistence );
      //System.out.println( tableOther );
      assertTrue( table.equalsInContent( tableOther ) );
    }
  }
  
  @Test
  public void testMoreComplexManagedBeanListAdapter()
  {
    final Table<Object> table = new ArrayTable<Object>( Object.class ).setExceptionHandler( new ExceptionHandlerEPrintStackTrace() );
    table.setColumnTitles( Arrays.asList( "fieldDate", "fieldDouble", "fieldLong", "fieldString" ) );
    final SortedMap<Date, Set<TestDomain>> sortedMap = table.index().of( new KeyExtractor<Date, TestDomain>()
    {
      private static final long serialVersionUID = -2612713147012980832L;
      
      @Override
      public Date extractKey( TestDomain testDomain )
      {
        final Date date = testDomain.getFieldDate();
        return date;
      }
    }, TestDomain.class );
    final List<TestDomain> domainList = table.as().managedBeanList( TestDomain.class );
    final Date date = new Date();
    for ( int ii = 0; ii < 30; ii++ )
    {
      TestDomain testDomain = new TestDomain();
      final Date fieldDate = newRelativeDate( date, ii / 2 );
      testDomain.setFieldDate( fieldDate );
      testDomain.setFieldDouble( 123.5 + ii );
      testDomain.setFieldLong( 134l + ii );
      testDomain.setFieldString( "test" + ii );
      domainList.add( testDomain );
      assertEquals( 1 + ii, domainList.size() );
      assertEquals( domainList.size(), table.rowSize() );
      assertEquals( Math.round( domainList.size() / 2.0 ), sortedMap.size() );
    }
    {
      final int daysBack = 5;
      SortedMap<Date, Set<TestDomain>> headMap = sortedMap.headMap( newRelativeDate( date, daysBack ) );
      //System.out.println( headMap );
      
      Set<TestDomain> mergeAll = SetUtils.mergeAll( headMap.values() );
      //System.out.println( mergeAll );
      assertEquals( daysBack * 2, mergeAll.size() );
    }
    //System.out.println( table );
    //System.out.println( MapUtils.toString( sortedMap ) );
    
  }
  
  @Test
  public void testIndexOfArbitraryKeyExtractor()
  {
    Table<String> table = this.filledTable( 100, 5 );
    
    KeyExtractor<Integer, RowDataReader<String>> keyExtractor = new KeyExtractor<Integer, RowDataReader<String>>()
    {
      private static final long serialVersionUID = -4201644938610833630L;
      
      @Override
      public Integer extractKey( RowDataReader<String> rowDataReader )
      {
        String[] elements = rowDataReader.getElements();
        String[] tokens = elements[1].split( ":" );
        return Integer.valueOf( tokens[0] );
      }
    };
    SortedMap<Integer, Set<Row<String>>> sortedMap = table.index().of( keyExtractor );
    {
      assertNotNull( sortedMap );
      assertEquals( table.rowSize(), sortedMap.size() );
      assertTrue( sortedMap.containsKey( 0 ) );
    }
    
    table.removeRow( 0 );
    {
      assertFalse( sortedMap.containsKey( 0 ) );
      assertTrue( sortedMap.containsKey( 1 ) );
      assertFalse( sortedMap.containsKey( 101 ) );
      
      table.setElement( 0, 1, "101:88" );
      assertTrue( sortedMap.containsKey( 101 ) );
      
      Set<Row<String>> rowSet = sortedMap.get( 101 );
      assertEquals( 1, rowSet.size() );
    }
    {
      assertSame( sortedMap, table.index().of( keyExtractor ) );
    }
    
    table.setRowElements( 1, "0:0", "200:0" );
    {
      assertTrue( sortedMap.containsKey( 200 ) );
    }
    {
      SortedMap<Integer, Set<Row<String>>> tailMap = sortedMap.tailMap( 90 );
      assertEquals( 100 - 90 + 2, tailMap.size() );
      assertEquals( 90, tailMap.firstKey().intValue() );
      assertEquals( 200, tailMap.lastKey().intValue() );
    }
    {
      SortedMap<Integer, Set<Row<String>>> headMap = sortedMap.headMap( 10 );
      assertEquals( 9 - 2, headMap.size() );
      assertEquals( 3, headMap.firstKey().intValue() );
      assertEquals( 9, headMap.lastKey().intValue() );
    }
    {
      table.clear();
      assertTrue( sortedMap.isEmpty() );
    }
  }
  
  @SuppressWarnings({ "unchecked", "cast" })
  @Test
  public void testIndexOfArbitraryKeyExtractorWithValueExtractor()
  {
    Table<String> table = this.filledTable( 100, 5 );
    
    KeyExtractor<Integer, RowDataReader<String>> keyExtractor = new KeyExtractor<Integer, RowDataReader<String>>()
    {
      private static final long serialVersionUID = -7714884390309847394L;
      
      @Override
      public Integer extractKey( RowDataReader<String> rowDataReader )
      {
        String[] elements = rowDataReader.getElements();
        String[] tokens = elements[1].split( ":" );
        return Integer.valueOf( tokens[0] );
      }
    };
    ValueExtractor<Integer, Set<String[]>> valueExtractor = new ValueExtractor<Integer, Set<String[]>>()
    {
      @Override
      public Integer extractValue( Set<String[]> elementsSet )
      {
        final String[] elements = IterableUtils.firstElement( elementsSet );
        final String[] tokens = elements[1].split( ":" );
        return Integer.valueOf( tokens[1] );
      }
    };
    
    SortedMap<Integer, Integer> sortedMap = table.index()
                                                 .of( keyExtractor,
                                                      valueExtractor,
                                                      (Comparator<Integer>) ComparatorUtils.reversedComparator( ComparatorUtils.NATURAL_COMPARATOR ) );
    {
      assertNotNull( sortedMap );
      assertEquals( table.rowSize(), sortedMap.size() );
      assertTrue( sortedMap.containsKey( 0 ) );
    }
    
    table.removeRow( 0 );
    {
      assertFalse( sortedMap.containsKey( 0 ) );
      assertTrue( sortedMap.containsKey( 1 ) );
      assertFalse( sortedMap.containsKey( 101 ) );
      
      table.setElement( 0, 1, "101:88" );
      assertTrue( sortedMap.containsKey( 101 ) );
      
      Integer columnIndex = sortedMap.get( 101 );
      assertEquals( 88, columnIndex.intValue() );
    }
    
  }
  
  @Test
  public void testListAdapter()
  {
    Table<String> table = this.filledTableWithTitles( 10, 5 ).setExceptionHandler( new ExceptionHandlerEPrintStackTrace() );
    
    List<SimpleTestBean> beanList = table.as().managedBeanList( SimpleTestBean.class );
    assertNotNull( beanList );
    assertEquals( table.rowSize(), beanList.size() );
    for ( int ii = 0; ii < 10; ii++ )
    {
      SimpleTestBean bean = beanList.get( ii );
      assertEquals( ii + ":0", bean.getC0() );
      assertEquals( ii + ":1", bean.getC1() );
      assertEquals( ii + ":2", bean.getC2() );
      assertEquals( ii + ":3", bean.getC3() );
      assertEquals( ii + ":4", bean.getC4() );
    }
    
    {
      SimpleTestBean bean = new SimpleTestBean();
      bean.setC0( "C0" );
      bean.setC1( "C1" );
      bean.setC2( "C2" );
      bean.setC3( "C3" );
      bean.setC4( "C4" );
      beanList.add( bean );
      
      Row<String> lastRow = table.lastRow();
      assertEquals( "C0", lastRow.getElement( "c0" ) );
      assertEquals( "C1", lastRow.getElement( "c1" ) );
      assertEquals( "C2", lastRow.getElement( "c2" ) );
      assertEquals( "C3", lastRow.getElement( "c3" ) );
      assertEquals( "C4", lastRow.getElement( "c4" ) );
    }
    {
      Row<String> row = table.row( 5 );
      beanList.remove( 5 );
      assertTrue( row.isDeleted() );
    }
    
    //System.out.println( table );
  }
  
  @Test
  public void testMoreComplexDTObasedListAdapter()
  {
    final Table<Object> table = new ArrayTable<Object>( Object.class ).setExceptionHandler( new ExceptionHandlerEPrintStackTrace() );
    table.setColumnTitles( Arrays.asList( "fieldDate", "fieldDouble", "fieldLong", "fieldString" ) );
    final List<TestDomain> domainList = table.as().beanList( TestDomain.class );
    final Date date = new Date();
    final int numberOfRows = 20;
    for ( int ii = 0; ii < numberOfRows; ii++ )
    {
      TestDomain testDomain = new TestDomain();
      {
        final Date fieldDate = newRelativeDate( date, ii / 2 );
        testDomain.setFieldDate( fieldDate );
        testDomain.setFieldDouble( 123.5 + ii );
        testDomain.setFieldLong( 134l + ii );
        testDomain.setFieldString( "test" + ii );
      }
      domainList.add( testDomain );
      assertEquals( 1 + ii, domainList.size() );
      assertEquals( domainList.size(), table.rowSize() );
    }
    assertEquals( numberOfRows, domainList.size() );
    assertNotNull( ListUtils.firstElement( domainList ) );
    assertNotNull( ListUtils.lastElement( domainList ) );
    
    {
      TestDomain testDomain = ListUtils.get( domainList, 0 );
      assertEquals( "test0", testDomain.getFieldString() );
      assertEquals( 134l, testDomain.getFieldLong().longValue() );
    }
    {
      TestDomain testDomain = ListUtils.get( domainList, 10 );
      assertEquals( "test10", testDomain.getFieldString() );
      assertEquals( 134l + 10, testDomain.getFieldLong().longValue() );
    }
    
    //System.out.println( table );    
  }
  
  @Test
  public void testResultSet() throws SQLException
  {
    Table<String> table = this.filledTableWithTitles( 10, 5 );
    {
      ResultSet resultSet = table.as().resultSet();
      for ( int ii = 0; ii < 10; ii++ )
      {
        assertTrue( resultSet.next() );
        assertEquals( ii + ":0", resultSet.getString( 1 ) );
        assertEquals( ii + ":1", resultSet.getString( "c1" ) );
      }
      assertFalse( resultSet.next() );
    }
    {
      ResultSet resultSet = table.as().resultSet();
      Table<String> tableOther = new ArrayTable<String>( String.class );
      tableOther.copy().from( new TableDataSourceResultSet<String>( resultSet, String.class ) );
      assertTrue( table.equalsInContent( tableOther ) );
      //System.out.println( tableOther );
    }
  }
  
  @Test
  public void testSelectOn()
  {
    Table<String> table = this.filledTableWithTitles( 10, 5 );
    Table<String> table2 = table.clone();
    
    {
      Table<String> result = table.select().allColumns().whereEqual( table.column( 1 ), "2:1" ).as().table();
      
      //System.out.println( result );
      
      assertEquals( 1, result.rowSize() );
      assertEquals( "2:0", result.getElement( 0, 0 ) );
    }
    {
      Table<String> result = table.select().allColumns().whereLike( table.column( 1 ), Pattern.compile( "5:1" ) ).as().table();
      
      //System.out.println( result );
      
      assertEquals( 1, result.rowSize() );
      assertEquals( "5:0", result.getElement( 0, 0 ) );
    }
    {
      Table<String> result = table.select().allColumns().whereWithin( table.column( 1 ), SetUtils.valueOf( "3:1" ) ).as().table();
      
      //System.out.println( result );
      
      assertEquals( 1, result.rowSize() );
      assertEquals( "3:0", result.getElement( 0, 0 ) );
    }
    
    {
      Table<String> result = table.select()
                                  .allColumns()
                                  .join( table2 )
                                  .allColumns()
                                  .onEqual( table.column( 0 ), table2.column( 0 ) )
                                  .onEqual( table2.column( 1 ), "2:1" )
                                  .as()
                                  .table();
      
      //System.out.println( result );
      
      assertEquals( 1, result.rowSize() );
      assertEquals( "2:0", result.getElement( 0, 0 ) );
    }
    {
      Table<String> result = table.select()
                                  .allColumns()
                                  .join( table2 )
                                  .allColumns()
                                  .onEqual( table.column( 0 ), table2.column( 0 ) )
                                  .onLike( table2.column( 1 ), Pattern.compile( "5:1" ) )
                                  .as()
                                  .table();
      
      //System.out.println( result );
      
      assertEquals( 1, result.rowSize() );
      assertEquals( "5:0", result.getElement( 0, 0 ) );
    }
    {
      Table<String> result = table.select()
                                  .allColumns()
                                  .join( table2 )
                                  .allColumns()
                                  .onEqual( table.column( 0 ), table2.column( 0 ) )
                                  .onWithin( table2.column( 1 ), SetUtils.valueOf( "3:1" ) )
                                  .as()
                                  .table();
      
      //System.out.println( result );
      
      assertEquals( 1, result.rowSize() );
      assertEquals( "3:0", result.getElement( 0, 0 ) );
    }
    
  }
  
  @Test
  public void testSerializationXHTML()
  {
    Table<String> table = this.filledTableWithTitles( 10, 5 );
    table.setExceptionHandler( new ExceptionHandlerEPrintStackTrace() );
    
    final MarshallingConfiguration configuration = new MarshallingConfiguration().setHasEnabledColumnTitles( true )
                                                                                 .setHasEnabledRowTitles( true )
                                                                                 .setHasEnabledTableName( true );
    
    String content = table.serializer().marshal().asXHtml().using( configuration ).toString();
    //System.out.println( content );
    
    Table<String> clone = new ArrayTable<String>( String.class ).serializer()
                                                                .unmarshal()
                                                                .asXHtml()
                                                                .using( configuration )
                                                                .from( content );
    
    //System.out.println( clone );
    assertTrue( table.equalsInContent( clone ) );
    assertEquals( table.getColumnTitleList(), clone.getColumnTitleList() );
    
  }

  @Test
  public void testSwapped()
  {
    Table<String> table = this.filledTableWithTitles( 10, 5 );
    Table<String> swapped = table.to().swapped();
    //System.out.println( swapped );
    
    assertEquals( 5, swapped.rowSize() );
    assertEquals( 10, swapped.columnSize() );
    assertTrue( table.equalsInContentAndMetaData( swapped.to().swapped() ) );
  }

  @Test
  public void testTransformFirstRowAndColumnToTitles()
  {
    Table<String> table = this.filledTableWithTitles( 10, 5 );
    
    final String[] columnTitles = Arrays.copyOfRange( table.row( 0 ).to().array(), 1, 5 );
    final String[] rowTitles = Arrays.copyOfRange( table.column( 0 ).to().array(), 1, 10 );
    
    table.setRowTitlesUsingFirstColumn();
    //System.out.println( table );
    
    table.setColumnTitlesUsingFirstRow();
    //System.out.println( table );
    
    assertArrayEquals( rowTitles, table.getRowTitles() );
    assertArrayEquals( columnTitles, table.getColumnTitles() );
  }

  @Test
  public void testSubTable()
  {
    Table<String> table = this.filledTableWithTitles( 10, 5 );
    
    final int rowIndexFrom = 1;
    final int rowIndexTo = 6;
    final int columnIndexFrom = 1;
    final int columnIndexTo = 4;
    Table<String> subTable = table.to().subTable( rowIndexFrom, rowIndexTo, columnIndexFrom, columnIndexTo );
    
    //System.out.println( subTable );
    /*
    ===table name===
    !  !c1 !c2 !c3 !
    !r1!1:1|1:2|1:3|
    !r2!2:1|2:2|2:3|
    !r3!3:1|3:2|3:3|
    !r4!4:1|4:2|4:3|
    !r5!5:1|5:2|5:3|
    ----------------
     */
    
    assertEquals( rowIndexTo - rowIndexFrom, subTable.rowSize() );
    assertEquals( columnIndexTo - columnIndexFrom, subTable.columnSize() );
    assertEquals( "1:1", subTable.getElement( 0, 0 ) );
    assertEquals( "5:3", subTable.getElement( 4, 2 ) );
    assertEquals( Arrays.asList( "c1", "c2", "c3" ), subTable.getColumnTitleList() );
    assertEquals( Arrays.asList( "r1", "r2", "r3", "r4", "r5" ), subTable.getRowTitleList() );
  }

  @Test
  public void testApplyConverter()
  {
    Table<String> table = this.filledTableWithTitles( 10, 5 );
    
    ElementConverter<String, String> elementConverter = new ElementConverter<String, String>()
    {
      @Override
      public String convert( String element )
      {
        return StringUtils.replace( element, ":", " " );
      }
    };
    table.rows( 1, 4 ).apply( elementConverter );
    
    //System.out.println( table );
    /*
    =======table name=======
    !  !c0 !c1 !c2 !c3 !c4 !
    !r0!0:0|0:1|0:2|0:3|0:4|
    !r1!1 0|1 1|1 2|1 3|1 4|
    !r2!2 0|2 1|2 2|2 3|2 4|
    !r3!3 0|3 1|3 2|3 3|3 4|
    !r4!4:0|4:1|4:2|4:3|4:4|
    !r5!5:0|5:1|5:2|5:3|5:4|
    !r6!6:0|6:1|6:2|6:3|6:4|
    !r7!7:0|7:1|7:2|7:3|7:4|
    !r8!8:0|8:1|8:2|8:3|8:4|
    !r9!9:0|9:1|9:2|9:3|9:4|
    ------------------------
     */
    assertEquals( "1 0", table.getElement( 1, 0 ) );
    assertEquals( "3 4", table.getElement( 3, 4 ) );
    assertEquals( "4:4", table.getElement( 4, 4 ) );
    assertEquals( "0:0", table.getElement( 0, 0 ) );
  }

  @Test
  public void testSelectTopAndSkip()
  {
    Table<String> table = this.filledTableWithTitles( 20, 2 );
    {
      Table<String> result = table.select().allColumns().top( 5 ).as().table();
      //System.out.println( result );
      
      assertEquals( 5, result.rowSize() );
      assertArrayEquals( new String[] { "0:0", "0:1" }, result.row( 0 ).to().array() );
    }
    {
      Table<String> result = table.select().allColumns().skip( 3 ).as().table();
      //System.out.println( result );
      
      assertEquals( 17, result.rowSize() );
      assertArrayEquals( new String[] { "3:0", "3:1" }, result.row( 0 ).to().array() );
    }
    {
      Table<String> result = table.select().allColumns().top( 5 ).skip( 3 ).as().table();
      //System.out.println( result );
      
      assertEquals( 5, result.rowSize() );
      assertArrayEquals( new String[] { "3:0", "3:1" }, result.row( 0 ).to().array() );
    }
  }

  @Test
  public void testSelectSelfJoin()
  {
    Table<String> table = this.filledTableWithTitles( 4, 2 );
    
    Table<String> result = table.select().allColumns().join( table ).allColumns().as().table();
    //System.out.println( result );
    
    /*
    =======================table name========================
    !table name.c0!table name.c1!table name.c0!table name.c1!
    |     0:0     |     0:1     |     0:0     |     0:1     |
    |     0:0     |     0:1     |     1:0     |     1:1     |
    |     0:0     |     0:1     |     2:0     |     2:1     |
    |     0:0     |     0:1     |     3:0     |     3:1     |
    |     1:0     |     1:1     |     0:0     |     0:1     |
    |     1:0     |     1:1     |     1:0     |     1:1     |
    |     1:0     |     1:1     |     2:0     |     2:1     |
    |     1:0     |     1:1     |     3:0     |     3:1     |
    |     2:0     |     2:1     |     0:0     |     0:1     |
    |     2:0     |     2:1     |     1:0     |     1:1     |
    |     2:0     |     2:1     |     2:0     |     2:1     |
    |     2:0     |     2:1     |     3:0     |     3:1     |
    |     3:0     |     3:1     |     0:0     |     0:1     |
    |     3:0     |     3:1     |     1:0     |     1:1     |
    |     3:0     |     3:1     |     2:0     |     2:1     |
    |     3:0     |     3:1     |     3:0     |     3:1     |
    ---------------------------------------------------------
    */
    for ( int ii = 0; ii < 4; ii++ )
    {
      assertArrayEquals( new String[] { "0:0", "0:1", ii + ":0", ii + ":1" }, result.row( ii ).to().array() );
    }
    for ( int ii = 0; ii < 4; ii++ )
    {
      assertArrayEquals( new String[] { "3:0", "3:1", ii + ":0", ii + ":1" }, result.row( 4 * 3 + ii ).to().array() );
    }
    
  }

  @Test
  public void testAddRowElements() throws Exception
  {
    {
      Table<String> table = this.filledTableWithTitles( 0, 3 );
      
      final Map<String, String> columnToElementMap = MapUtils.builder()
                                                             .put( "c0", "0:0" )
                                                             .put( "c3", "0:3" )
                                                             .buildAs()
                                                             .linkedHashMap();
      table.addRowElements( columnToElementMap );
      
      /*
      ==table name===
      !c0 !c1!c2!c3 !
      |0:0|  |  |0:3|
      ---------------
       */
      //System.out.println( table );
      assertArrayEquals( new String[] { "0:0", null, null, "0:3" }, table.row( 0 ).getElements() );
      assertEquals( 1, table.rowSize() );
    }
    {
      Table<String> table = this.filledTableWithTitles( 0, 3 );
      
      final Map<String, String> columnToElementMap = MapUtils.builder()
                                                             .put( "c0", "0:0" )
                                                             .put( "c3", "0:3" )
                                                             .buildAs()
                                                             .linkedHashMap();
      final boolean createColumnTitleIfDontExists = false;
      table.addRowElements( columnToElementMap, createColumnTitleIfDontExists );
      
      /*
      table name
      !c0 !
      |0:0|
      -----
       */
      //System.out.println( table );
      assertArrayEquals( new String[] { "0:0" }, table.row( 0 ).getElements() );
      assertEquals( 1, table.rowSize() );
    }
  }
}
