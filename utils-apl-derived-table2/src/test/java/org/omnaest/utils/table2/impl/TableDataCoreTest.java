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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Ignore;
import org.junit.Test;

public class TableDataCoreTest
{
  
  @Test
  public void testAddRow() throws Exception
  {
    TableDataCore<String> tableDataCore = new TableDataCore<String>( String.class );
    
    {
      final String[] data = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m" };
      tableDataCore.addRow( data );
      
      assertArrayEquals( data, tableDataCore.getRow( 0 ) );
      assertEquals( "a", tableDataCore.getElement( 0, 0 ) );
      assertEquals( "m", tableDataCore.getElement( 0, 12 ) );
      assertNull( tableDataCore.getElement( 0, 13 ) );
      assertEquals( 1, tableDataCore.rowSize() );
    }
    
    //
    for ( int ii = 1; ii < 255; ii++ )
    {
      final String[] data = new String[] { "a" + ii, "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m" };
      tableDataCore.addRow( data );
      assertArrayEquals( data, tableDataCore.getRow( ii ) );
    }
    
    //
    {
      final int rowSizeBefore = tableDataCore.rowSize();
      final String[] data = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m" };
      tableDataCore.addRow( 1, data );
      assertArrayEquals( data, tableDataCore.getRow( 1 ) );
      assertEquals( rowSizeBefore + 1, tableDataCore.rowSize() );
    }
    {
      final String[] data = new String[] { "a" + 1, "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m" };
      assertArrayEquals( data, tableDataCore.getRow( 2 ) );
    }
    
    //System.out.println( tableDataCore );
  }
  
  @Test
  public void testAddColumn() throws Exception
  {
    TableDataCore<String> tableDataCore = new TableDataCore<String>( String.class );
    
    {
      final String[] data = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m" };
      tableDataCore.addColumn( data );
      
      assertArrayEquals( data, tableDataCore.getColumn( 0 ) );
      assertEquals( "a", tableDataCore.getElement( 0, 0 ) );
      assertEquals( "m", tableDataCore.getElement( 12, 0 ) );
      assertNull( tableDataCore.getElement( 13, 0 ) );
      assertEquals( 1, tableDataCore.columnSize() );
    }
    
    //
    for ( int ii = 1; ii < 255; ii++ )
    {
      final String[] data = new String[] { "a" + ii, "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m" };
      tableDataCore.addColumn( data );
      assertArrayEquals( data, tableDataCore.getColumn( ii ) );
    }
    
    //System.out.println( tableDataCore );
  }
  
  @Test
  public void testSet() throws Exception
  {
    TableDataCore<String> tableDataCore = new TableDataCore<String>( String.class );
    
    assertNull( tableDataCore.getElement( 0, 0 ) );
    tableDataCore.set( "00", 0, 0 );
    assertEquals( "00", tableDataCore.getElement( 0, 0 ) );
    
    tableDataCore.set( "10", 1, 0 );
    assertEquals( "10", tableDataCore.getElement( 1, 0 ) );
    
    tableDataCore.set( "11", 1, 1 );
    assertEquals( "11", tableDataCore.getElement( 1, 1 ) );
    
    tableDataCore.set( "01", 0, 1 );
    assertEquals( "01", tableDataCore.getElement( 0, 1 ) );
    
    tableDataCore.set( "99", 9, 9 );
    assertEquals( "99", tableDataCore.getElement( 9, 9 ) );
    
    //System.out.println( tableDataCore );
  }
  
  @Test
  public void testRemoveRow() throws Exception
  {
    //
    TableDataCore<String> tableDataCore = new TableDataCore<String>( String.class );
    for ( int ii = 0; ii < 12; ii++ )
    {
      final String[] data = new String[] { "a" + ii, "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m" };
      tableDataCore.addRow( data );
      assertArrayEquals( data, tableDataCore.getRow( ii ) );
    }
    
    //
    assertEquals( 12, tableDataCore.rowSize() );
    tableDataCore.removeRow( 5 );
    assertEquals( 11, tableDataCore.rowSize() );
    assertEquals( "a6", tableDataCore.getElement( 5, 0 ) );
    
    //
    for ( int ii = 0; ii < 10; ii++ )
    {
      tableDataCore.removeRow( 0 );
    }
    assertEquals( 1, tableDataCore.rowSize() );
    
    {
      final String[] data = new String[] { "a11", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m" };
      assertArrayEquals( data, tableDataCore.getRow( 0 ) );
    }
  }
  
  @Test
  @Ignore("Performance test")
  public void testPerformance() throws Exception
  {
    //
    TableDataCore<String> tableDataCore = new TableDataCore<String>( String.class );
    
    //
    for ( int ii = 0; ii < 100000; ii++ )
    {
      final String[] data = new String[] { "" + ii, "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m" };
      tableDataCore.addRow( data );
    }
    
    //    for ( int ii = 0; ii < 100000; ii++ )
    //    {
    //      if ( ii % 2 == 0 )
    //      {
    //        tableDataCore.removeRow( ii );
    //      }
    //    }
  }
  
  @Test
  public void testClear() throws Exception
  {
    TableDataCore<String> tableDataCore = new TableDataCore<String>( String.class );
    for ( int ii = 0; ii < 12; ii++ )
    {
      final String[] data = new String[] { "a" + ii, "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m" };
      tableDataCore.addRow( data );
      assertArrayEquals( data, tableDataCore.getRow( ii ) );
    }
    
    tableDataCore.clear();
    assertEquals( 0, tableDataCore.rowSize() );
    assertArrayEquals( new String[0], tableDataCore.getRow( 0 ) );
  }
  
}