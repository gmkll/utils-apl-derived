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
package org.omnaest.utils.table.connector;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.omnaest.utils.structure.map.MapUtils;
import org.omnaest.utils.table2.Table;
import org.omnaest.utils.table2.impl.ArrayTable;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

/**
 * @see StripeTransformerPluginODocument
 * @author Omnaest
 */
public class StripeTransformerPluginODocumentTest
{
  
  @Test
  public void testGetType()
  {
    ODatabaseDocumentTx db = new ODatabaseDocumentTx( "memory:testdb" );
    db.create();
    {
      {
        Table<String> table = new ArrayTable<String>( String.class ).register( new StripeTransformerPluginODocument<String>() )
                                                                    .setColumnTitles( Arrays.asList( "column1", "column2",
                                                                                                     "column3" ) );
        
        table.addRowElements( "a", "b", "c" );
        ODocument document = table.row( 0 ).to().type( new ODocument( "Test" ) );
        document.save();
        
        ODocument document2 = table.row( 0 ).to().type( ODocument.class );
        document2.setClassName( "Test" );
        document2.save();
        
      }
      {
        List<ODocument> result = db.query( new OSQLSynchQuery<ODocument>( "select * from Test" ) );
        assertEquals( 2, result.size() );
        {
          final Iterator<ODocument> iterator = result.iterator();
          while ( iterator.hasNext() )
          {
            final Map<String, String> resultMap = new LinkedHashMap<String, String>();
            {
              final ODocument document = iterator.next();
              final String[] fieldNames = document.fieldNames();
              for ( String fieldName : fieldNames )
              {
                Object value = document.field( fieldName );
                resultMap.put( fieldName, (String) value );
              }
            }
            assertEquals( MapUtils.builder()
                                  .put( "column1", "a" )
                                  .put( "column2", "b" )
                                  .put( "column3", "c" )
                                  .buildAs()
                                  .linkedHashMap(), resultMap );
          }
        }
      }
    }
    db.close();
  }
}