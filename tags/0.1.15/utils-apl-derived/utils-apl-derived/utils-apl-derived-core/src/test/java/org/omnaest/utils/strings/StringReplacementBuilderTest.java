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
package org.omnaest.utils.strings;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @see StringReplacementBuilder
 * @author Omnaest
 */
public class StringReplacementBuilderTest
{
  
  @Test
  public void testProcess()
  {
    final String value = "A value and its replacement";
    assertEquals( "A replacement and its replacement",
                  new StringReplacementBuilder().add( "value", "replacement" ).process( value ) );
  }
  
}
