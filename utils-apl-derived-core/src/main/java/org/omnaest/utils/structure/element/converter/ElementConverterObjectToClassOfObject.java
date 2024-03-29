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
package org.omnaest.utils.structure.element.converter;

/**
 * This is an {@link ElementConverter} implementation which returns the {@link Class} instances of a given {@link Object} by
 * calling {@link Object#getClass()}
 * 
 * @author Omnaest
 */
public class ElementConverterObjectToClassOfObject implements ElementConverterSerializable<Object, Class<? extends Object>>
{
  private static final long serialVersionUID = -510983792483348192L;

  @Override
  public Class<? extends Object> convert( Object element )
  {
    return element == null ? null : element.getClass();
  }
}
