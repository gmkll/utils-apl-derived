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
package org.omnaest.utils.beans.replicator2;

import java.io.Serializable;

/**
 * An {@link InstanceCache} is used to retrieve already transformed instances related to a target type. So it is possible to store
 * multiple transformed instances for the same type.
 * 
 * @author Omnaest
 */
@SuppressWarnings("javadoc")
interface InstanceCache extends Serializable
{
  /**
   * @param targetType
   * @param value
   * @param valueReplica
   */
  public void addReplicaInstance( Class<?> targetType, Object value, Object valueReplica );
  
  /**
   * @param targetType
   * @param value
   * @return
   */
  public Object getReplicaInstance( Class<?> targetType, Object value );
  
}
