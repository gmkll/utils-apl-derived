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
package org.omnaest.utils.structure.collection.list.sorted;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Required;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Test;
import org.omnaest.utils.structure.collection.SortedListTestAbstract;
import org.omnaest.utils.structure.collection.list.ListUtils;

import com.google.common.collect.SortedMultiset;
import com.google.common.collect.TreeMultiset;

/**
 * @see TreeList
 * @author Omnaest
 */
public class TreeListTest extends SortedListTestAbstract
{
  // @Rule
  public ContiPerfRule               contiPerfRule = new ContiPerfRule();
  
  /* ********************************************** Variables ********************************************** */
  protected final List<String>       sourceList    = Arrays.asList( "e", "c", "e", "d", "e", "b" );
  protected final SortedList<String> treeList      = new TreeList<String>( this.sourceList );
  
  /* ********************************************** Methods ********************************************** */
  
  private static void runLoad( Collection<String> collection )
  {
    for ( int ii = 0; ii < 100000; ii++ )
    {
      collection.add( "a" + Math.round( Math.random() * 100 ) );
    }
    for ( int ii = 0; ii < 100000; ii++ )
    {
      collection.contains( "a" + Math.round( Math.random() * 100 ) );
    }
    for ( @SuppressWarnings("unused")
    String value : collection )
    {
    }
  }
  
  @PerfTest(invocations = 2)
  @Required(average = 2000)
  @Test
  public void testPerformanceTreeList()
  {
    List<String> treeList = new TreeList<String>();
    runLoad( treeList );
  }
  
  @PerfTest(invocations = 2)
  @Required(average = 2000)
  @Test
  public void testPerformanceSortedSet()
  {
    final SortedSet<String> sortedSet = new TreeSet<String>();
    runLoad( sortedSet );
  }
  
  @PerfTest(invocations = 2)
  @Required(average = 2000)
  @Test
  public void testPerformanceSortedMultiSet()
  {
    final SortedMultiset<String> sortedSet = TreeMultiset.create();
    runLoad( sortedSet );
  }
  
  @PerfTest(invocations = 2)
  @Required(average = 2000)
  @Test
  public void testPerformanceApacheCommonsTreeList()
  {
    @SuppressWarnings("unchecked")
    final List<String> list = new org.apache.commons.collections.list.TreeList();
    runLoad( list );
  }
  
  @Test
  public void testGet()
  {
    //
    final List<String> sortedSourceList = ListUtils.sorted( this.sourceList );
    for ( int index = 0; index < sortedSourceList.size(); index++ )
    {
      assertEquals( sortedSourceList.get( index ), this.treeList.get( index++ ) );
    }
  }
  
  @Test
  public void testSet()
  {
    this.treeList.set( 1, "z" );
    assertEquals( "d", this.treeList.get( 1 ) );
    assertEquals( "z", this.treeList.get( this.sourceList.size() - 1 ) );
  }
  
  @Test
  public void testAddIntE()
  {
    this.treeList.add( 4, "a" );
    assertEquals( this.sourceList.size() + 1, this.treeList.size() );
    assertEquals( 0, this.treeList.indexOf( "a" ) );
    assertEquals( 0, this.treeList.lastIndexOf( "a" ) );
  }
  
  @Test
  public void testRemoveInt()
  {
    //
    this.treeList.remove( 1 );
    assertEquals( "d", this.treeList.get( 1 ) );
    assertEquals( "e", this.treeList.get( this.sourceList.size() - 2 ) );
    
    //
    this.treeList.add( "c" );
    this.treeList.add( "c" );
    this.treeList.remove( 1 );
    assertEquals( "c", this.treeList.get( 1 ) );
    assertEquals( "e", this.treeList.get( this.sourceList.size() - 1 ) );
    
  }
  
  @Test
  public void testIndexOf()
  {
    assertEquals( -1, this.treeList.indexOf( "z" ) );
    assertEquals( 1, this.treeList.indexOf( "c" ) );
  }
  
  @Test
  public void testLastIndexOf()
  {
    this.treeList.add( "c" );
    this.treeList.add( "c" );
    assertEquals( -1, this.treeList.lastIndexOf( "z" ) );
    assertEquals( 3, this.treeList.lastIndexOf( "c" ) );
  }
  
  @Test
  public void testRemove()
  {
    //
    this.treeList.remove( "c" );
    assertEquals( this.sourceList.size() - 1, this.treeList.size() );
    
    //
    this.treeList.add( "c" );
    this.treeList.add( "c" );
    this.treeList.remove( "c" );
    assertEquals( this.sourceList.size(), this.treeList.size() );
  }
  
  @Test
  public void testContains()
  {
    //
    this.treeList.add( "c" );
    this.treeList.add( "c" );
    
    //
    assertTrue( this.treeList.contains( "b" ) );
    assertTrue( this.treeList.contains( "c" ) );
    assertFalse( this.treeList.contains( "z" ) );
  }
  
  @Test
  public void testIsEmpty()
  {
    //
    assertFalse( this.treeList.isEmpty() );
    this.treeList.removeAll( this.sourceList );
    assertTrue( this.treeList.isEmpty() );
    
    //
    assertTrue( new TreeList<String>().isEmpty() );
  }
  
  @Test
  public void testClear()
  {
    assertFalse( this.treeList.isEmpty() );
    this.treeList.clear();
    assertTrue( this.treeList.isEmpty() );
  }
  
  @Override
  protected SortedList<String> newSortedList()
  {
    return new TreeList<String>();
  }
  
}
