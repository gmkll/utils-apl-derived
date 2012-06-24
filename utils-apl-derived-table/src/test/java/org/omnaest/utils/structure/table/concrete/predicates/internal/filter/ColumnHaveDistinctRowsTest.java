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
package org.omnaest.utils.structure.table.concrete.predicates.internal.filter;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.TableFiller;
import org.omnaest.utils.structure.table.concrete.ArrayTable;
import org.omnaest.utils.structure.table.subspecification.TableSelectable.Selection;

/**
 * @see ColumnHaveDistinctRows
 * @see Selection#distinct()
 * @author Omnaest
 */
public class ColumnHaveDistinctRowsTest
{
  /* ********************************************** Variables ********************************************** */
  protected Table<String> table1 = new ArrayTable<String>();
  protected Table<String> table2 = new ArrayTable<String>();
  
  /* ********************************************** Methods ********************************************** */
  
  @Before
  public void setUp()
  {
    //
    {
      //
      int rows = 10;
      int columns = 2;
      String tableName = "Table1";
      TableFiller.fillTableWithMatrixNumbers( rows, columns, tableName, this.table1 );
    }
    
    //
    {
      //
      int rows = 10;
      int columns = 2;
      String tableName = "Table2";
      TableFiller.fillTableWithMatrixNumbers( rows, columns, tableName, this.table2 );
    }
  }
  
  @Test
  public void testColumnHaveDistinctRowsIdentity()
  {
    //
    Table<String> tableResult = this.table1.select().allColumns().distinct().asTable();
    
    //
    //System.out.println( tableResult );
    
    //
    assertEquals( this.table1, tableResult );
    
  }
  
  @Test
  public void testColumnHaveDistinctRowsForRowsWithDuplicates()
  {
    //
    this.table1.putTable( this.table2, 10, 0 );
    Table<String> tableResult = this.table1.select().allColumns().distinct().asTable();
    
    //
    //    System.out.println( this.table1 );
    //    System.out.println( tableResult );
    
    //
    assertEquals( 10, tableResult.getTableSize().getRowSize() );
    
  }
  
  @Test
  public void testColumnHaveDistinctRowsForRowsWithDuplicates2()
  {
    //
    this.table1.putTable( this.table2, 5, 0 );
    Table<String> tableResult = this.table1.select().allColumns().distinct().asTable();
    
    //
    //    System.out.println( this.table1 );
    //    System.out.println( tableResult );
    
    //
    assertEquals( 10, tableResult.getTableSize().getRowSize() );
  }
  
}
