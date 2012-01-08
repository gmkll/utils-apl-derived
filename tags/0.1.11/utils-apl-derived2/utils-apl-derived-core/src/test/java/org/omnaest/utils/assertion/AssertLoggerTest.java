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
package org.omnaest.utils.assertion;

import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;
import org.slf4j.Logger;

/**
 * @see AssertLogger
 * @author Omnaest
 */
public class AssertLoggerTest
{
  
  /* ********************************************** Variables ********************************************** */
  private Logger         logger       = Mockito.mock( Logger.class );
  protected AssertLogger assertLogger = new AssertLogger( this.logger );
  
  /* ********************************************** Methods ********************************************** */
  
  @Test
  public void testAssertLoggerLogger()
  {
    //
    this.assertLogger.debug.assertThat.isTrue( true );
    Mockito.verify( this.logger, new Times( 0 ) ).debug( Matchers.anyString() );
    
    //
    this.assertLogger.debug.assertThat.isTrue( false );
    Mockito.verify( this.logger, new Times( 1 ) ).debug( Matchers.anyString(), Matchers.any( Exception.class ) );
    
    //
    this.assertLogger.trace.assertThat.isNotNull( new Object() );
    Mockito.verify( this.logger, new Times( 0 ) ).trace( Matchers.anyString() );
    
    //
    this.assertLogger.trace.assertThat.isNotNull( null );
    Mockito.verify( this.logger, new Times( 1 ) ).trace( Matchers.anyString(), Matchers.any( Exception.class ) );
    
    //
    this.assertLogger.info.assertThat.fails();
    Mockito.verify( this.logger, new Times( 0 ) ).info( Matchers.anyString() );
    Mockito.verify( this.logger, new Times( 1 ) ).info( Matchers.anyString(), Matchers.any( Exception.class ) );
    
    //
    this.assertLogger.info.assertThat.fails( "Additional message" );
    Mockito.verify( this.logger, new Times( 2 ) ).info( Matchers.anyString(), Matchers.any( Exception.class ) );
    
    //
    this.assertLogger.warn.assertThat.isNotNull( new Object(), "Additional message" );
    Mockito.verify( this.logger, new Times( 0 ) ).warn( Matchers.anyString() );
    
    //
    this.assertLogger.warn.assertThat.isNotNull( null, "Additional message" );
    Mockito.verify( this.logger, new Times( 1 ) ).warn( Matchers.anyString(), Matchers.any( Exception.class ) );
    
    //
    this.assertLogger.error.assertThat.isTrue( true, "Additional message" );
    Mockito.verify( this.logger, new Times( 0 ) ).error( Matchers.anyString() );
    
    //
    this.assertLogger.error.assertThat.isTrue( false, "Additional message" );
    Mockito.verify( this.logger, new Times( 1 ) ).error( Matchers.anyString(), Matchers.any( Exception.class ) );
  }
  
}
