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
package org.omnaest.utils.events.exception.basic;

import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.events.exception.ExceptionHandlerSerializable;
import org.slf4j.Logger;

/**
 * Simple {@link ExceptionHandler} which logs as ERROR using a slf4j {@link Logger} instance
 * 
 * @author Omnaest
 */
public class ExceptionHandlerLogging implements ExceptionHandlerSerializable
{
  /* ************************************************** Constants *************************************************** */
  private static final long serialVersionUID = -7990568786881923896L;
  /* ***************************** Beans / Services / References / Delegates (external) ***************************** */
  private final Logger      logger;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see ExceptionHandlerLogging
   * @param logger
   */
  public ExceptionHandlerLogging( Logger logger )
  {
    super();
    this.logger = logger;
  }
  
  @Override
  public void handleException( Exception e )
  {
    if ( this.logger != null )
    {
      this.logger.error( "", e );
    }
  }
  
}
