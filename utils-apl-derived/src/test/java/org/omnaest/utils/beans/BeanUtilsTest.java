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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.omnaest.utils.beans.result.BeanPropertyAccessor;

/**
 * @see BeanUtils
 * @author Omnaest
 */
public class BeanUtilsTest
{
  
  @Before
  public void setUp() throws Exception
  {
  }
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  protected static class TestBeanImpl implements TestBean
  {
    /* ********************************************** Variables ********************************************** */
    protected String fieldString = null;
    protected Double fieldDouble = null;
    
    /* ********************************************** Methods ********************************************** */
    @Override
    public String getFieldString()
    {
      return this.fieldString;
    }
    
    @Override
    public void setFieldString( String fieldString )
    {
      this.fieldString = fieldString;
    }
    
    @Override
    public Double getFieldDouble()
    {
      return this.fieldDouble;
    }
    
    @Override
    public void setFieldDouble( Double fieldDouble )
    {
      this.fieldDouble = fieldDouble;
    }
    
    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + ( ( this.fieldDouble == null ) ? 0 : this.fieldDouble.hashCode() );
      result = prime * result + ( ( this.fieldString == null ) ? 0 : this.fieldString.hashCode() );
      return result;
    }
    
    @Override
    public boolean equals( Object obj )
    {
      if ( this == obj )
      {
        return true;
      }
      if ( obj == null )
      {
        return false;
      }
      if ( !( obj instanceof TestBeanImpl ) )
      {
        return false;
      }
      TestBeanImpl other = (TestBeanImpl) obj;
      if ( this.fieldDouble == null )
      {
        if ( other.fieldDouble != null )
        {
          return false;
        }
      }
      else if ( !this.fieldDouble.equals( other.fieldDouble ) )
      {
        return false;
      }
      if ( this.fieldString == null )
      {
        if ( other.fieldString != null )
        {
          return false;
        }
      }
      else if ( !this.fieldString.equals( other.fieldString ) )
      {
        return false;
      }
      return true;
    }
    
  }
  
  protected static interface TestBean
  {
    public void setFieldDouble( Double fieldDouble );
    
    public Double getFieldDouble();
    
    public void setFieldString( String fieldString );
    
    public String getFieldString();
  }
  
  /* ********************************************** Methods ********************************************** */
  @Test
  public void testDeterminePropertyNames()
  {
    //
    String[] propertyNames = BeanUtils.determinePropertyNamesForMethodAccess( TestBean.class );
    
    //
    Assert.assertArrayEquals( new String[] { "fieldString", "fieldDouble" }, propertyNames );
  }
  
  @Test
  public void testCopyPropertyValues()
  {
    //
    TestBeanImpl beanSource = new TestBeanImpl();
    beanSource.setFieldString( "value1" );
    beanSource.setFieldDouble( 1.3 );
    
    //
    TestBeanImpl beanDestination = new TestBeanImpl();
    assertTrue( !beanSource.equals( beanDestination ) );
    
    //
    BeanUtils.copyPropertyValues( beanSource, beanDestination );
    
    //
    assertEquals( beanSource, beanDestination );
  }
  
  @Test
  public void testDetermineBeanMethodInformationSet()
  {
    //
    assertEquals( 4, BeanUtils.determineBeanMethodInformationSet( TestBean.class ).size() );
  }
  
  @Test
  public void testDeterminePropertyNameToBeanPropertyAccessorMap()
  {
    //
    assertEquals( 2, BeanUtils.determinePropertyNameToBeanPropertyAccessorMap( TestBean.class ).size() );
    assertEquals( 2, BeanUtils.determinePropertyNameToBeanPropertyAccessorMap( TestBeanImpl.class ).size() );
  }
  
  @Test
  public void testDetermineBeanPropertyAccessorSet()
  {
    //
    assertEquals( 2, BeanUtils.determineBeanPropertyAccessorSet( TestBean.class ).size() );
    assertEquals( 2, BeanUtils.determineBeanPropertyAccessorSet( TestBeanImpl.class ).size() );
  }
  
  @Test
  public void testDetermineBeanPropertyAccessor() throws Throwable
  {
    //
    {
      BeanPropertyAccessor<TestBeanImpl> beanPropertyAccessor = BeanUtils.determineBeanPropertyAccessor( TestBeanImpl.class,
                                                                                                         TestBeanImpl.class.getDeclaredFields()[0] );
      
      assertNotNull( beanPropertyAccessor );
      assertEquals( true, beanPropertyAccessor.hasGetterAndSetter() );
    }
    
    //
    {
      BeanPropertyAccessor<TestBeanImpl> beanPropertyAccessor = BeanUtils.determineBeanPropertyAccessor( TestBeanImpl.class,
                                                                                                         TestBeanImpl.class.getDeclaredField( "fieldString" ) );
      
      assertNotNull( beanPropertyAccessor );
      assertEquals( true, beanPropertyAccessor.hasGetterAndSetter() );
    }
  }
  
  @Test
  public void testDeterminePropertyNamesForMethodAccess()
  {
    String[] propertyNamesForMethodAccess = BeanUtils.determinePropertyNamesForMethodAccess( TestBean.class );
    Assert.assertArrayEquals( new String[] { "fieldString", "fieldDouble" }, propertyNamesForMethodAccess );
  }
  
  @Test
  public void testDetermineNumberOfProperties()
  {
    Assert.assertEquals( 2, BeanUtils.determinePropertyNamesForMethodAccess( TestBean.class ).length );
  }
  
  @Test
  public void testTransformBeanIntoMap()
  {
    //
    TestBeanImpl beanSource = new TestBeanImpl();
    beanSource.setFieldString( "value1" );
    beanSource.setFieldDouble( 1.3 );
    
    //
    Map<String, Object> map = BeanUtils.transformBeanIntoMap( beanSource );
    assertNotNull( map );
    assertEquals( 2, map.size() );
    assertEquals( beanSource.getFieldString(), map.get( "fieldString" ) );
    assertEquals( beanSource.getFieldDouble(), map.get( "fieldDouble" ) );
  }
  
  @Test
  public void testDeterminePropertyTypeToBeanPropertyAccessorSetMap()
  {
    //
    Map<Class<?>, Set<BeanPropertyAccessor<TestBean>>> propertyTypeToBeanPropertyAccessorSetMap = BeanUtils.determinePropertyTypeToBeanPropertyAccessorSetMap( TestBean.class );
    assertEquals( 2, propertyTypeToBeanPropertyAccessorSetMap.size() );
    assertEquals( 1, propertyTypeToBeanPropertyAccessorSetMap.get( String.class ).size() );
    assertEquals( 1, propertyTypeToBeanPropertyAccessorSetMap.get( Double.class ).size() );
  }
  
  @Test
  public void testDeterminePropertyNameToBeanPropertyValueMap()
  {
    //
    TestBeanImpl beanSource = new TestBeanImpl();
    beanSource.setFieldString( "value1" );
    beanSource.setFieldDouble( 1.3 );
    
    //
    Map<String, Object> propertynameToBeanPropertyValueMap = BeanUtils.determinePropertyNameToBeanPropertyValueMap( beanSource );
    assertNotNull( propertynameToBeanPropertyValueMap );
    assertEquals( 2, propertynameToBeanPropertyValueMap.size() );
    assertEquals( beanSource.getFieldString(), propertynameToBeanPropertyValueMap.get( "fieldString" ) );
    assertEquals( beanSource.getFieldDouble(), propertynameToBeanPropertyValueMap.get( "fieldDouble" ) );
  }
  
  @Test
  public void testDeterminePropertyNameToBeanPropertyValueMapForPropertyNames()
  {
    //
    TestBeanImpl beanSource = new TestBeanImpl();
    beanSource.setFieldString( "value1" );
    beanSource.setFieldDouble( 1.3 );
    
    //
    Map<String, Object> propertynameToBeanPropertyValueMap = BeanUtils.determinePropertyNameToBeanPropertyValueMap( beanSource,
                                                                                                                    "fieldString" );
    assertNotNull( propertynameToBeanPropertyValueMap );
    assertEquals( 1, propertynameToBeanPropertyValueMap.size() );
    assertEquals( beanSource.getFieldString(), propertynameToBeanPropertyValueMap.get( "fieldString" ) );
    
  }
}