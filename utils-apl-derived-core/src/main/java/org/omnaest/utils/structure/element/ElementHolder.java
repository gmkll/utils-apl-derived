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
package org.omnaest.utils.structure.element;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.omnaest.utils.structure.element.accessor.Accessor;

/**
 * Modifiable version of an {@link ElementHolderUnmodifiable} allows to {@link #setElement(Object)}
 * 
 * @see ElementHolderUnmodifiable
 * @author Omnaest
 * @param <E>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ElementHolder<E> extends ElementHolderUnmodifiable<E> implements Accessor<E>, Serializable
{
  
  private static final long serialVersionUID = -4566820555702467091L;
  
  /**
   * @param element
   */
  public ElementHolder( E element )
  {
    super( element );
  }
  
  /**
   * 
   */
  public ElementHolder()
  {
    super( null );
  }
  
  @Override
  public ElementHolder<E> setElement( E element )
  {
    this.element = element;
    return this;
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "ElementHolder [element=" );
    builder.append( this.element );
    builder.append( "]" );
    return builder.toString();
  }
  
}
