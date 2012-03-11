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
package org.omnaest.utils.threads;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.omnaest.utils.structure.element.ExceptionHandledResult;

/**
 * @see FutureTaskManager
 * @author Omnaest
 */
public class FutureTaskManagerTest
{
  /* ********************************************** Variables ********************************************** */
  private int                     corePoolSize    = 10;
  private int                     maximumPoolSize = 10;
  private long                    keepAliveTime   = 1;
  private TimeUnit                unit            = TimeUnit.SECONDS;
  private BlockingQueue<Runnable> workQueue       = new ArrayBlockingQueue<Runnable>( 10 );
  private ExecutorService         executorService = new ThreadPoolExecutor( this.corePoolSize, this.maximumPoolSize,
                                                                            this.keepAliveTime, this.unit, this.workQueue );
  
  /* ********************************************** Methods ********************************************** */
  
  @Test
  public void testWaitForTaskToFinish() throws InterruptedException
  {
    //
    Runnable runnable = new Runnable()
    {
      
      @Override
      public void run()
      {
        throw new RuntimeException();
      }
    };
    
    //
    Future<?> future = this.executorService.submit( runnable );
    
    //
    ExceptionHandledResult<?> exceptionHandledResult = FutureTaskManager.waitForTaskToFinish( future );
    assertTrue( exceptionHandledResult.hasExceptions() );
    assertTrue( exceptionHandledResult.containsAssignableException( RuntimeException.class ) );
  }
  
}