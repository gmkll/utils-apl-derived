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
package org.omnaest.utils.structure.table.concrete.predicates.internal;

import org.omnaest.utils.structure.table.TableSelectable.Where.Predicate;
import org.omnaest.utils.structure.table.concrete.predicates.PredicateFactory;
import org.omnaest.utils.structure.table.internal.TableInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeData;

import com.sun.rowset.internal.Row;

/**
 * Every {@link PredicateInternal} should be represented by a static factory method within the {@link PredicateFactory}.
 * 
 * @see PredicateFactory
 * @see Predicate
 * @author Omnaest
 * @param <E>
 */
public interface PredicateInternal<E> extends Predicate<E>
{
  /**
   * Removes the {@link StripeData} instances of {@link Row}s from a given {@link TableInternal}
   */
  public void filterStripeDataSet( TableInternal<E> tableInternal );
}
