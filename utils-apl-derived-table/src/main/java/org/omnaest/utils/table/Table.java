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

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.regex.Pattern;

import org.omnaest.utils.events.exception.ExceptionHandlerSerializable;
import org.omnaest.utils.table.TableSelect.TableJoin;
import org.omnaest.utils.table.impl.TableEventHandlerRegistration;

/**
 * <h1>General</h1><br>
 * A {@link Table} represents a two dimensional container which allows modification of {@link Row}s and {@link Column}s.<br>
 * <br>
 * 
 * <pre>
 * Table&lt;String&gt; table = new ArrayTable&lt;String&gt;( String.class );
 * table.addRowElements( &quot;a&quot;, &quot;b&quot;, &quot;c&quot;, &quot;d&quot; );
 * 
 * String element = table.getElement( 7, 4 );
 * 
 * for ( Map&lt;String, String&gt; map : table.rows( 3, 6 ).to().maps() )
 * {
 *   //...
 * }
 * </pre>
 * 
 * <h2>Index</h2><br>
 * With the {@link #index()} method the {@link Table} allows to create {@link TableIndex}s of several forms. Those indexes can be
 * backed by the {@link Table} which does hide the complexity of updates within the {@link Table} implementation.<br>
 * 
 * <pre>
 * TableIndex&lt;String, Cell&lt;String&gt;&gt; tableIndex = table.index().of( table.column( 1 ) );
 * </pre>
 * 
 * <h2>Select</h2><br>
 * With {@link #select()} is a simple {@link TableJoin} related to SQL possible. <br>
 * 
 * <pre>
 * Table&lt;String&gt; result = table.select()
 *                             .column( 1 )
 *                             .column( 2 )
 *                             .join( tableOther )
 *                             .allColumns()
 *                             .onEqual( table.column( 1 ), tableOther.column( 0 ) )
 *                             .as()
 *                             .table();
 * </pre>
 * 
 * <h2>Serialization</h2><br>
 * With {@link #serializer()} it is possible to serialize and deserialzie the {@link Table} content into and from several formats
 * like Json, Xml, plain text. <br>
 * 
 * <pre>
 * String content = table.serializer().marshal().asJson().toString();
 * </pre>
 * 
 * <h2>Persistence</h2><br>
 * The {@link Table} can be attached and detached to and from a {@link TablePersistence} instance.<br>
 * There are rudimentary file and directory based implementations, which are serializing the objects e.g. as xml or Java object
 * {@link Serializable}. (Some formats imply the import of third party dependencies)<br>
 * <br>
 * Example:
 * 
 * <pre>
 * Table&lt;String&gt; table = new ArrayTable&lt;String&gt;( String.class ).persistence().attach().asXML().usingJAXB().toDirectory( directory );
 * </pre>
 * 
 * <h2>Copy</h2><br>
 * With {@link #copy()} the table can copy content of other sources, like other {@link Table} instances or any other
 * {@link TableDataSource}. E.g. a {@link ResultSet} can be used as source as well. <br>
 * <h2>Adapter</h2><br>
 * The {@link #as()} method provides adapter instances which allow to access the {@link Table} e.g. as a {@link List} of Java
 * beans. <br>
 * 
 * <pre>
 * List&lt;Domain&gt; domainList = table.as().beanList( Domain.class );
 * domainList.add( new Domain() );
 * </pre>
 * 
 * @see #select()
 * @see #serializer()
 * @see #index()
 * @see #copy()
 * @see #as()
 * @see Row
 * @see Column
 * @see Stripe
 * @see ImmutableTable
 * @author Omnaest
 * @param <E>
 */
public interface Table<E> extends ImmutableTable<E>, Serializable
{
  
  /**
   * Appends new elements as {@link Column} to the {@link Table}
   * 
   * @param elements
   * @return this
   */
  public Table<E> addColumnElements( E... elements );
  
  /**
   * Adds new elements as {@link Column} to the {@link Table} at the specific column index position
   * 
   * @param columnIndex
   * @param elements
   * @return this
   */
  public Table<E> addColumnElements( int columnIndex, E... elements );
  
  /**
   * Adds a new {@link Column} title to the end of the columns
   * 
   * @param columnTitle
   * @return this
   */
  public Table<E> addColumnTitle( String columnTitle );
  
  /**
   * Adds new elements as {@link Row} to the {@link Table}
   * 
   * @param elements
   * @return this
   */
  public Table<E> addRowElements( E... elements );
  
  /**
   * Adds new elements as {@link Row} to the {@link Table} at the specific row index position.
   * 
   * @param rowIndex
   * @param elements
   * @return this
   */
  public Table<E> addRowElements( int rowIndex, E... elements );
  
  /**
   * Similar to {@link #addRowElements(Object...)}
   * 
   * @param elementIterable
   * @return this
   */
  public Table<E> addRowElements( Iterable<E> elementIterable );
  
  /**
   * Adds the values of a given {@link Map} as new row to the {@link Table}. The keys of the {@link Map} are treated as column
   * titles. Any key within the {@link Map} which is not available as column title will create a new column with exactly this
   * title.
   * 
   * @see #addRowElements(Map, boolean)
   * @param columnToElementMap
   * @return this
   */
  public Table<E> addRowElements( Map<String, E> columnToElementMap );
  
  /**
   * Adds the values of a given {@link Map} as new row to the {@link Table}. The keys of the {@link Map} are treated as column
   * titles. Any key within the {@link Map} which is not available as column title will create a new column with exactly this
   * title if the given createColumnTitleIfDontExists flag is set to true.
   * 
   * @param columnToElementMap
   * @param createColumnTitleIfDontExists
   * @return this
   */
  public Table<E> addRowElements( Map<String, E> columnToElementMap, boolean createColumnTitleIfDontExists );
  
  /**
   * Returns a {@link TableAdapterManager} instance which allows to craeate adapter instances
   * 
   * @return
   */
  public TableAdapterManager<E> as();
  
  /**
   * Returns an {@link Cell} instance for the given row and column index position
   * 
   * @param rowIndex
   * @param columnIndex
   * @return new {@link Cell} instance
   */
  @Override
  public Cell<E> cell( int rowIndex, int columnIndex );
  
  /**
   * Returns an {@link Iterable} over all {@link Cell}s which traverses through the {@link Row}s traversing through their
   * {@link Row#cells()}
   * 
   * @return
   */
  @Override
  public Iterable<Cell<E>> cells();
  
  /**
   * Clears the {@link Table}
   * 
   * @return
   */
  public Table<E> clear();
  
  /**
   * Clones the current {@link Table} structure into a new instance
   * 
   * @return new instance
   */
  @Override
  public Table<E> clone();
  
  /**
   * Returns a new {@link Column} currently related to the given column index position
   * 
   * @param columnIndex
   * @return new {@link Column} instance
   */
  @Override
  public Column<E> column( int columnIndex );
  
  /**
   * Similar to {@link #column(int)} based on the first matching column title
   * 
   * @param columnTitle
   * @return
   */
  @Override
  public Column<E> column( String columnTitle );
  
  /**
   * Returns the {@link Columns} of the {@link Table}
   * 
   * @return
   */
  @Override
  public Columns<E, Column<E>> columns();
  
  /**
   * Returns an {@link Iterable} over all {@link Column}s which have a column title matched by the given {@link Pattern}
   * 
   * @return
   */
  @Override
  public Columns<E, Column<E>> columns( Pattern columnTitlePattern );
  
  /**
   * Returns an {@link Iterable} over all {@link Column}s which have a column title included in the given {@link Set} of titles
   * 
   * @return
   */
  @Override
  public Columns<E, Column<E>> columns( Set<String> columnTitleSet );
  
  /**
   * Returns all {@link Column}s which have a column title included in the given titles
   * 
   * @param columnTitles
   * @return
   */
  @Override
  public Columns<E, Column<E>> columns( String... columnTitles );
  
  /**
   * Returns a {@link TableDataSourceCopier} instance
   * 
   * @return
   */
  public TableDataSourceCopier<E> copy();
  
  /**
   * Executes a {@link TableExecution} with a table wide {@link WriteLock}
   * 
   * @param tableExecution
   *          {@link TableExecution}
   * @return this
   */
  public Table<E> executeWithWriteLock( TableExecution<Table<E>, E> tableExecution );
  
  /**
   * Returns the {@link TableIndexManager} instance which allows to create {@link TableIndex} instances based on {@link Column}s
   * of the {@link Table}
   * 
   * @return
   */
  @Override
  public TableIndexManager<E, Cell<E>> index();
  
  /**
   * Returns the last {@link Row}
   * 
   * @return
   */
  @Override
  public Row<E> lastRow();
  
  /**
   * Returns a new {@link Row} which points to the next unused row index position
   * 
   * @return new {@link Row} at the end of the {@link Table}
   */
  public Row<E> newRow();
  
  /**
   * Returns the {@link TablePersistenceRegistration} instance
   * 
   * @return
   */
  public TablePersistenceRegistration<E> persistence();
  
  @Override
  public Table<E> register( StripeTransformerPlugin<E, ?> stripeTransformerPlugin );
  
  /**
   * Removes the {@link Column} for the given column index position
   * 
   * @param columnIndex
   * @return this
   */
  public Table<E> removeColumn( int columnIndex );
  
  /**
   * Removes a {@link Row} at the given row index position
   * 
   * @param rowIndex
   * @return this
   */
  public Table<E> removeRow( int rowIndex );
  
  /**
   * Returns a new {@link Row} related to the given row index position
   * 
   * @param rowIndex
   * @return
   */
  @Override
  public Row<E> row( int rowIndex );
  
  /**
   * Returns a detached {@link Row}. A detached {@link Row} does not reflect any changes done to the underlying {@link Table}
   * 
   * @param rowIndex
   * @param detached
   * @return new detached {@link Row} instance
   */
  @Override
  public Row<E> row( int rowIndex, boolean detached );
  
  /**
   * Similar to {@link #row(int)} based on the first matching row title
   * 
   * @param rowTitle
   * @return
   */
  @Override
  public Row<E> row( String rowTitle );
  
  /**
   * Returns an {@link Iterable} instance over all {@link Row}s of the {@link Table}
   * 
   * @return new {@link Rows}
   */
  @Override
  public Rows<E, Row<E>> rows();
  
  /**
   * Returns an {@link Iterable} over all {@link Row}s where the row index position has an enabled bit within the filter
   * {@link BitSet}
   * 
   * @param indexFilter
   * @return new {@link Rows}
   */
  @Override
  public Rows<E, Row<E>> rows( BitSet indexFilter );
  
  /**
   * Similar to {@link #rows(BitSet)} and {@link #row(int, boolean)}
   * 
   * @param filter
   * @param detached
   * @return {@link Rows}
   */
  @Override
  public Rows<E, Row<E>> rows( BitSet filter, boolean detached );
  
  /**
   * Similar to {@link #rows()} but allows to return detached {@link Row} instances
   * 
   * @param detached
   * @return new {@link Rows}
   */
  @Override
  public Rows<E, Row<E>> rows( final boolean detached );
  
  /**
   * Returns an {@link Iterable} over all {@link Row}s which are between the two given row index positions. The lower index is
   * inclusive the upper index position is exclusive.
   * 
   * @param rowIndexFrom
   * @param rowIndexTo
   * @return new {@link Rows}
   */
  @Override
  public Rows<E, Row<E>> rows( int rowIndexFrom, int rowIndexTo );
  
  /**
   * Similar to {@link #rows(BitSet, boolean)} and {@link #rows(int, int)}
   * 
   * @param rowIndexFrom
   * @param rowIndexTo
   * @param detached
   * @return {@link Rows}
   */
  @Override
  public Rows<E, Row<E>> rows( int rowIndexFrom, int rowIndexTo, boolean detached );
  
  /**
   * Returns a {@link TableSerializer} instance
   * 
   * @return
   */
  @Override
  public TableSerializer<E> serializer();
  
  /**
   * Sets the title of the {@link Column} with the given column index position
   * 
   * @param columnIndex
   * @param columnTitle
   * @return this
   */
  public Table<E> setColumnTitle( int columnIndex, String columnTitle );
  
  /**
   * Clears and sets the names of the {@link Column}s to the given values
   * 
   * @param columnTitleIterable
   * @return this
   */
  public Table<E> setColumnTitles( Iterable<String> columnTitleIterable );
  
  /**
   * Similar to {@link #setColumnTitles(Iterable)}
   * 
   * @param columnTitles
   * @return this
   */
  public Table<E> setColumnTitles( String... columnTitles );
  
  /**
   * @return this
   */
  public Table<E> setColumnTitlesUsingFirstRow();
  
  /**
   * Sets the element for a given row and column index position
   * 
   * @param rowIndex
   * @param columnIndex
   * @param element
   * @return
   */
  public Table<E> setElement( int rowIndex, int columnIndex, E element );
  
  /**
   * Similar to {@link #setElement(int, int, Object)}
   * 
   * @param rowIndex
   * @param columnTitle
   * @param element
   * @return this
   */
  public Table<E> setElement( int rowIndex, String columnTitle, E element );
  
  /**
   * Similar to {@link #setElement(int, int, Object)}
   * 
   * @param rowTitle
   * @param columnIndex
   * @param element
   * @return this
   */
  public Table<E> setElement( String rowTitle, int columnIndex, E element );
  
  /**
   * Similar to {@link #setElement(int, int, Object)}
   * 
   * @param rowTitle
   * @param columnTitle
   * @param element
   * @return this
   */
  public Table<E> setElement( String rowTitle, String columnTitle, E element );
  
  /**
   * Sets the {@link ExceptionHandlerSerializable} instance
   * 
   * @param exceptionHandler
   * @return this
   */
  public Table<E> setExceptionHandler( ExceptionHandlerSerializable exceptionHandler );
  
  /**
   * Sets the elements of the {@link Row} at the given row index position
   * 
   * @param rowIndex
   * @param elements
   * @return this
   */
  public Table<E> setRowElements( int rowIndex, E... elements );
  
  /**
   * Sets the title of the {@link Row} with the given row index position
   * 
   * @param rowIndex
   * @param rowTitle
   * @return this
   */
  public Table<E> setRowTitle( int rowIndex, String rowTitle );
  
  /**
   * Clears and sets the names of the {@link Row}s to the given values
   * 
   * @param rowTitleIterable
   * @return this
   */
  public Table<E> setRowTitles( Iterable<String> rowTitleIterable );
  
  /**
   * Similar to {@link #setRowTitles(Iterable)}
   * 
   * @param rowTitles
   * @return this
   */
  public Table<E> setRowTitles( String... rowTitles );
  
  /**
   * @return this
   */
  public Table<E> setRowTitlesUsingFirstColumn();
  
  /**
   * Sets the name of the {@link Table}
   * 
   * @param tableName
   * @return this
   */
  public Table<E> setTableName( String tableName );
  
  /**
   * Returns a new {@link TableSorter} instance
   * 
   * @return {@link TableSorter} instance
   */
  public TableSorter<E> sort();
  
  /**
   * Returns a {@link TableEventHandlerRegistration} instance allowing to attach or detach {@link TableEventHandler} instances
   * 
   * @return
   */
  @Override
  public TableEventHandlerRegistration<E, Table<E>> tableEventHandlerRegistration();
}
