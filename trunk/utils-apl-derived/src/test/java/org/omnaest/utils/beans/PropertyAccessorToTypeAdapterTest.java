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
package org.omnaest.utils.beans;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;
import org.omnaest.utils.beans.PropertyAccessorToTypeAdapter.PropertyAccessor;

/**
 * @see PropertyAccessorToTypeAdapter
 * @author Omnaest
 */
public class PropertyAccessorToTypeAdapterTest
{
  /* ********************************************** Variables ********************************************** */
  private PropertyAccessor propertyAccessor = Mockito.mock( PropertyAccessor.class );
  private TestType         testType         = PropertyAccessorToTypeAdapter.newInstance( TestType.class, this.propertyAccessor );
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  public static interface TestType
  {
    public String getFieldString();
    
    public void setFieldString( String value );
    
    public double getFieldPrimitiveDouble();
    
    public void setFieldPrimitiveDouble( double value );
  }
  
  /* ********************************************** Methods ********************************************** */
  
  @Before
  public void setUp()
  {
    Mockito.when( this.propertyAccessor.getValue( "fieldString" ) ).thenReturn( "return string" );
    Mockito.when( this.propertyAccessor.getValue( "fieldPrimitiveDouble" ) ).thenReturn( 1234.2234 );
  }
  
  @Test
  public void testNewInstance()
  {
    //
    assertEquals( "return string", this.testType.getFieldString() );
    assertEquals( 1234.2234, this.testType.getFieldPrimitiveDouble(), 0.0001 );
    
    //
    this.testType.setFieldString( "new string value" );
    this.testType.setFieldPrimitiveDouble( 1234.223 );
    
    //
    Mockito.verify( this.propertyAccessor, new Times( 1 ) ).getValue( "fieldString" );
    Mockito.verify( this.propertyAccessor, new Times( 1 ) ).setValue( "fieldString", "new string value" );
    Mockito.verify( this.propertyAccessor, new Times( 1 ) ).getValue( "fieldPrimitiveDouble" );
    Mockito.verify( this.propertyAccessor, new Times( 1 ) ).setValue( "fieldPrimitiveDouble", 1234.223 );
  }
}
