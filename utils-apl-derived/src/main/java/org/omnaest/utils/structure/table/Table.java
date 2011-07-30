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
package org.omnaest.utils.structure.table;

import java.io.Serializable;
import java.util.List;

import org.omnaest.utils.structure.table.Table.Row;
import org.omnaest.utils.structure.table.concrete.ArrayTable;
import org.omnaest.utils.structure.table.concrete.internal.helper.StripeTypeHelper;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeDataList;
import org.omnaest.utils.structure.table.subspecification.TableCore;
import org.omnaest.utils.structure.table.subspecification.TableSelectable;
import org.omnaest.utils.structure.table.subspecification.TableSerializable;

/**
 * {@link Table} representation. Allows to create arbitrary {@link Table} structures. Offers rudimentary methods for joining.
 * 
 * @see IndexTable
 * @see ArrayTable
 * @author Omnaest
 */
public interface Table<E> extends TableCore<E>, TableSelectable<E>, Iterable<Row<E>>, TableSerializable<E>, Serializable

{
  /* ********************************************** Classes ********************************************** */
  
  /**
   * Marker interface for components of a {@link Table}
   * 
   * @author Omnaest
   */
  public interface TableComponent extends Serializable
  {
  }
  
  /**
   * Holds methods to return the current table size for rows and columns. If the data of the underlying table changes, the methods
   * will return the new actual results then, too!
   * 
   * @see Table
   */
  public interface TableSize extends TableComponent
  {
    /**
     * Returns the number of all available {@link Cell}s within the {@link Table}
     * 
     * @return
     */
    public int getCellSize();
    
    /**
     * Returns the number of {@link Row}s for the {@link Table}
     * 
     * @return
     */
    public int getRowSize();
    
    /**
     * Returns the number of {@link Column}s for the {@link Table}
     * 
     * @return
     */
    public int getColumnSize();
  }
  
  /**
   * Common abstraction for a {@link Column} and a {@link Row}
   * 
   * @see Table
   * @see Row
   * @see Column
   * @author Omnaest
   * @param <E>
   */
  public static interface Stripe<E> extends TableComponent, Iterable<Cell<E>>
  {
    /* ********************************************** Classes/Interfaces ********************************************** */
    
    /**
     * {@link StripeType} of a {@link Stripe}
     * 
     * @see Stripe
     * @see StripeDataList
     * @see StripeTypeHelper
     * @author Omnaest
     */
    public static enum StripeType
    {
      ROW,
      COLUMN
    }
    
    /**
     * {@link Title} of a single {@link Stripe}.
     * 
     * @see Stripe
     */
    public static interface Title extends TableComponent
    {
      
      /**
       * Gets the value of a {@link Title}
       * 
       * @return
       */
      public Object getValue();
      
      /**
       * Gets the value of a {@link Title} as {@link String}
       * 
       * @return
       */
      public String getValueAsString();
      
      /**
       * Sets the value of the {@link Title}
       * 
       * @param value
       */
      public void setValue( Object value );
    }
    
    /* ********************************************** Methods ********************************************** */
    /**
     * Get the {@link Cell} for the given index position.
     * 
     * @param indexPosition
     * @return
     */
    public Cell<E> getCell( int indexPosition );
    
    /**
     * Returns the {@link Cell#getElement()} for the given index position
     * 
     * @param indexPosition
     * @return
     */
    public E getCellElement( int indexPosition );
    
    /**
     * Returns the {@link Cell} for the title value of the orthogonal {@link Stripe}
     * 
     * @param titleValue
     * @return
     */
    public Cell<E> getCell( Object titleValue );
    
    /**
     * Returns the {@link Cell#getElement()} for the given index position
     * 
     * @param titleValue
     * @return
     */
    public E getCellElement( Object titleValue );
    
    /**
     * Returns the {@link Title}.
     * 
     * @return
     */
    public Title getTitle();
    
    /**
     * Returns true if this {@link Stripe} contains the given {@link Cell}.
     * 
     * @param cell
     * @return
     */
    public boolean contains( Cell<E> cell );
    
    /**
     * Returns true if one of the {@link Cell}s of this {@link Stripe} contains the given element
     * 
     * @param element
     * @return
     */
    public boolean contains( E element );
    
    /**
     * Sets the {@link Cell#setElement(Object)} for the {@link Cell} corresponding to the given title value
     * 
     * @param titleValue
     * @param element
     * @return this
     */
    public Stripe<E> setCellElement( Object titleValue, E element );
    
    /**
     * Sets the {@link Cell#setElement(Object)} of the whole {@link Stripe}
     * 
     * @param elements
     * @return this
     */
    public Stripe<E> setCellElements( Iterable<E> elements );
    
    /**
     * @see #setCellElements(Iterable)
     * @param elements
     * @return
     */
    public Stripe<E> setCellElements( E... elements );
    
    /**
     * Set the {@link Cell#setElement(Object)} for the {@link Cell} corresponding to the given orthogonal index position
     * 
     * @param indexPosition
     * @param element
     * @return this
     */
    public Stripe<E> setCellElement( int indexPosition, E element );
    
    /**
     * Determines the number of {@link Cell}s currently stored within this {@link Stripe}
     * 
     * @return
     */
    public int determineNumberOfCells();
    
    /**
     * Returns true if the {@link Stripe} has a {@link Title} value not null
     * 
     * @return
     */
    public boolean hasTitle();
    
    /**
     * Determines the index position of this {@link Stripe} within the {@link Table}
     * 
     * @return
     */
    public int determineIndexPosition();
    
    /**
     * Returns an {@link Iterable} for all {@link Cell}s of this {@link Stripe}
     * 
     * @return
     */
    public Iterable<Cell<E>> cells();
    
    /**
     * Returns a new {@link List} instance with all {@link Cell#getElement()}s
     * 
     * @return
     */
    public List<E> asNewListOfCellElements();
    
    /**
     * Sets all available {@link Cell#setElement(Object)} to null
     * 
     * @return
     */
    public Stripe<E> clearCellElements();
  }
  
  /**
   * @see Table
   * @author Omnaest
   * @param <E>
   */
  public static interface Row<E> extends Stripe<E>
  {
  }
  
  /**
   * @see Table
   * @author Omnaest
   * @param <E>
   */
  public static interface Column<E> extends Stripe<E>
  {
  }
  
  /**
   * @see Table
   * @author Omnaest
   * @param <E>
   */
  public static interface Cell<E> extends TableComponent
  {
    
    /**
     * Returns the value of the {@link Cell}
     * 
     * @return
     */
    public E getElement();
    
    /**
     * Sets the value of the {@link Cell}
     * 
     * @param element
     */
    public void setElement( E element );
    
    /**
     * Returns true if the given element equals the current {@link Cell#getElement()}
     * 
     * @param element
     * @return
     */
    public boolean hasElement( E element );
    
  }
  
  /**
   * Interface that is used to convert a {@link Table} into another {@link Table} generics form. For example convert a
   * Table&lt;String&gt; to a Table&lt;Integer&gt;.
   * 
   * @param <FROM>
   * @param <TO>
   * @author Omnaest
   * @see TableCore#convert(TableCellConverter)
   */
  public static interface TableCellConverter<FROM, TO>
  {
    /**
     * This method is called for every {@link Cell} element, which should be converted.
     * 
     * @see TableCellConverter
     * @param cellElement
     * @return converted value
     */
    public TO convert( FROM cellElement );
  }
  
  /**
   * Used by the table processor as visitor interface.
   * 
   * @author Omnaest
   * @see TableCore#processTableCells(TableCellVisitor)
   */
  public static interface TableCellVisitor<E>
  {
    /**
     * Inspect method
     * 
     * @param rowIndexPosition
     * @param columnIndexPosition
     * @param cell
     */
    public void process( int rowIndexPosition, int columnIndexPosition, Cell<E> cell );
  }
  
}
