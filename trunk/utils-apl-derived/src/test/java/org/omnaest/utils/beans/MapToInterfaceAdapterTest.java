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

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.omnaest.utils.beans.MapToInterfaceAdapter.UnderlyingMapAware;

public class MapToInterfaceAdapterTest
{
  
  @Before
  public void setUp() throws Exception
  {
  }
  
  protected static interface TestType extends UnderlyingMapAware<Map<String, Object>>
  {
    public Double getFieldDouble();
    
    public void setFieldDouble( Double fieldDouble );
    
    public String getFieldString();
    
    public void setFieldString( String fieldString );
    
  }
  
  @Test
  public void testNewInstance()
  {
    //
    Map<String, Object> map = new HashMap<String, Object>();
    
    //reading from facade
    TestType testType = MapToInterfaceAdapter.newInstance( map, TestType.class );
    
    //
    map.put( "fieldString", "String value" );
    map.put( "fieldDouble", 10.0 );
    
    assertEquals( "String value", testType.getFieldString() );
    assertEquals( 10.0, testType.getFieldDouble(), 0.01 );
    
    //writing to facade
    testType.setFieldString( "New String value" );
    testType.setFieldDouble( 11.0 );
    
    assertEquals( "New String value", map.get( "fieldString" ) );
    assertEquals( 11.0, (Double) map.get( "fieldDouble" ), 0.01 );
    assertEquals( 2, map.size() );
    
    //
    Map<String, Object> underlyingMap = testType.getUnderlyingMap();
    assertEquals( map, underlyingMap );
  }
}
