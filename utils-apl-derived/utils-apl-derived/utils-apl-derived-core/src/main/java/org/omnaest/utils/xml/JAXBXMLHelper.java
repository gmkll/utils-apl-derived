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
package org.omnaest.utils.xml;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Arrays;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.ArrayUtils;
import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.structure.container.ByteArrayContainer;
import org.w3c.dom.Node;

/**
 * Helper class for JAXB annotated classes.
 * 
 * @see XMLHelper
 * @see XMLIteratorFactory
 * @see JAXBMap
 * @see JAXBList
 * @see JAXBSet
 * @see JAXBCollection
 * @author Omnaest
 */
public class JAXBXMLHelper
{
  /* ********************************************** Constants ********************************************** */
  final static public String DEFAULT_ENCODING = "utf-8";
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * Configuration for the marshalling process
   * 
   * @author Omnaest
   */
  protected static abstract class MarshallingAndUnmarshallingConfigurationAbstractBase
  {
    /* ********************************************** Variables ********************************************** */
    protected String           encoding         = DEFAULT_ENCODING;
    protected ExceptionHandler exceptionHandler = null;
    protected Class<?>[]       knownTypes       = null;
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @return the encoding
     */
    public String getEncoding()
    {
      return this.encoding;
    }
    
    /**
     * @see MarshallingAndUnmarshallingConfigurationAbstractBase
     */
    protected MarshallingAndUnmarshallingConfigurationAbstractBase()
    {
      super();
    }
    
    /**
     * @see MarshallingAndUnmarshallingConfigurationAbstractBase
     * @param encoding
     * @param exceptionHandler
     * @param knownTypes
     */
    protected MarshallingAndUnmarshallingConfigurationAbstractBase( String encoding, ExceptionHandler exceptionHandler,
                                                                    Class<?>[] knownTypes )
    {
      super();
      this.encoding = encoding;
      this.exceptionHandler = exceptionHandler;
      this.knownTypes = knownTypes;
    }
    
    /**
     * @param encoding
     *          the encoding to set
     */
    public void setEncoding( String encoding )
    {
      this.encoding = encoding;
    }
    
    /**
     * @return the exceptionHandler
     */
    public ExceptionHandler getExceptionHandler()
    {
      return this.exceptionHandler;
    }
    
    /**
     * @param exceptionHandler
     *          the exceptionHandler to set
     */
    public void setExceptionHandler( ExceptionHandler exceptionHandler )
    {
      this.exceptionHandler = exceptionHandler;
    }
    
    /**
     * @return the knownTypes
     */
    public Class<?>[] getKnownTypes()
    {
      return this.knownTypes;
    }
    
    /**
     * @param knownTypes
     *          the knownTypes to set
     */
    public void setKnownTypes( Class<?>... knownTypes )
    {
      this.knownTypes = knownTypes;
    }
    
  }
  
  /**
   * @author Omnaest
   */
  public static class UnmarshallingConfiguration extends MarshallingAndUnmarshallingConfigurationAbstractBase
  {
    
    /**
     * @see UnmarshallingConfiguration
     */
    public UnmarshallingConfiguration()
    {
      super();
      
    }
    
    /**
     * @see UnmarshallingConfiguration
     * @param encoding
     * @param exceptionHandler
     * @param knownTypes
     */
    
    public UnmarshallingConfiguration( String encoding, ExceptionHandler exceptionHandler, Class<?>[] knownTypes )
    {
      super( encoding, exceptionHandler, knownTypes );
    }
    
    /**
     * Returns a new {@link UnmarshallingConfiguration} if a null reference is given, otherwise the given instance.
     * 
     * @param unmarshallingConfiguration
     * @return
     */
    public static UnmarshallingConfiguration defaultUnmarshallingConfiguration( UnmarshallingConfiguration unmarshallingConfiguration )
    {
      return unmarshallingConfiguration == null ? new UnmarshallingConfiguration() : unmarshallingConfiguration;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "UnmarshallingConfiguration [encoding=" );
      builder.append( this.encoding );
      builder.append( ", exceptionHandler=" );
      builder.append( this.exceptionHandler );
      builder.append( ", knownTypes=" );
      builder.append( Arrays.toString( this.knownTypes ) );
      builder.append( "]" );
      return builder.toString();
    }
    
  }
  
  /**
   * @author Omnaest
   */
  public static class MarshallingConfiguration extends MarshallingAndUnmarshallingConfigurationAbstractBase
  {
    /* ********************************************** Variables ********************************************** */
    private boolean formattingOutput = true;
    
    /* ********************************************** Methods ********************************************** */
    /**
     * @return
     */
    public boolean isFormattingOutput()
    {
      return this.formattingOutput;
    }
    
    /**
     * @param formattingOutput
     */
    public void setFormattingOutput( boolean formattingOutput )
    {
      this.formattingOutput = formattingOutput;
    }
    
    /**
     * Returns a new {@link MarshallingConfiguration} if a null reference is given, otherwise the given instance.
     * 
     * @param marshallingConfiguration
     * @return
     */
    public static MarshallingConfiguration defaultMarshallingConfiguration( MarshallingConfiguration marshallingConfiguration )
    {
      return marshallingConfiguration == null ? new MarshallingConfiguration() : marshallingConfiguration;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "MarshallingConfiguration [formattingOutput=" );
      builder.append( this.formattingOutput );
      builder.append( ", encoding=" );
      builder.append( this.encoding );
      builder.append( ", exceptionHandler=" );
      builder.append( this.exceptionHandler );
      builder.append( ", knownTypes=" );
      builder.append( Arrays.toString( this.knownTypes ) );
      builder.append( "]" );
      return builder.toString();
    }
    
    /**
     * Returns an {@link UnmarshallingConfiguration} based on the settings of this {@link MarshallingConfiguration} instance
     * 
     * @return
     */
    public UnmarshallingConfiguration asUnmarshallingConfiguration()
    {
      return new UnmarshallingConfiguration( this.encoding, this.exceptionHandler, this.knownTypes );
    }
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * Stores a given JAXB annotated object to the given {@link OutputStream} using the {@link #DEFAULT_ENCODING}
   * 
   * @see #storeObjectAsXML(Object, OutputStream, String)
   * @param object
   * @param outputStream
   */
  public static void storeObjectAsXML( Object object, OutputStream outputStream )
  {
    String encoding = DEFAULT_ENCODING;
    JAXBXMLHelper.storeObjectAsXML( object, outputStream, encoding );
  }
  
  /**
   * @see #storeObjectAsXML(Object)
   * @param object
   * @param outputStream
   * @param exceptionHandler
   */
  public static void storeObjectAsXML( Object object, OutputStream outputStream, ExceptionHandler exceptionHandler )
  {
    String encoding = DEFAULT_ENCODING;
    JAXBXMLHelper.storeObjectAsXML( object, outputStream, encoding, exceptionHandler );
  }
  
  /**
   * Stores a given JAXB annotated object to the given {@link OutputStream} using the given character encoding
   * 
   * @see #storeObjectAsXML(Object, OutputStream)
   * @param object
   * @param outputStream
   * @param encoding
   */
  public static void storeObjectAsXML( Object object, OutputStream outputStream, String encoding )
  {
    //
    final ExceptionHandler exceptionHandler = null;
    storeObjectAsXML( object, outputStream, encoding, exceptionHandler );
  }
  
  /**
   * Stores a given JAXB annotated object to the given {@link OutputStream} using the given character encoding
   * 
   * @see #storeObjectAsXML(Object, OutputStream)
   * @param object
   * @param outputStream
   * @param encoding
   * @param exceptionHandler
   *          {@link ExceptionHandler}
   */
  public static void storeObjectAsXML( Object object,
                                       OutputStream outputStream,
                                       String encoding,
                                       ExceptionHandler exceptionHandler )
  {
    //
    final MarshallingConfiguration marshallingConfiguration = new MarshallingConfiguration();
    marshallingConfiguration.setEncoding( encoding );
    marshallingConfiguration.setExceptionHandler( exceptionHandler );
    storeObjectAsXML( object, outputStream, marshallingConfiguration );
  }
  
  /**
   * Stores a given JAXB annotated object to the given {@link OutputStream} using the given {@link MarshallingConfiguration}
   * 
   * @see #storeObjectAsXML(Object, OutputStream)
   * @param object
   * @param outputStream
   * @param marshallingConfiguration
   *          {@link MarshallingConfiguration}
   */
  public static void storeObjectAsXML( Object object, OutputStream outputStream, MarshallingConfiguration marshallingConfiguration )
  {
    //
    marshallingConfiguration = MarshallingConfiguration.defaultMarshallingConfiguration( marshallingConfiguration );
    
    //
    final String encoding = marshallingConfiguration.getEncoding();
    final ExceptionHandler exceptionHandler = marshallingConfiguration.getExceptionHandler();
    final Class<?>[] knownTypes = marshallingConfiguration.getKnownTypes();
    final boolean formattingOutput = marshallingConfiguration.isFormattingOutput();
    
    // 
    try
    {
      //
      final Class<? extends Object> objectType = object.getClass();
      final Class<?>[] contextTypes = ArrayUtils.add( knownTypes, objectType );
      
      JAXBContext context = JAXBContext.newInstance( contextTypes );
      Marshaller marshaller = context.createMarshaller();
      marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, formattingOutput );
      
      //
      if ( encoding != null )
      {
        marshaller.setProperty( Marshaller.JAXB_ENCODING, encoding );
      }
      marshaller.marshal( object, outputStream );
      outputStream.flush();
    }
    catch ( Exception e )
    {
      if ( exceptionHandler != null )
      {
        exceptionHandler.handleException( e );
      }
    }
  }
  
  /**
   * Stores the given objects as XML within the given {@link Appendable} using the {@link #DEFAULT_ENCODING}
   * 
   * @param object
   * @param appendable
   */
  public static void storeObjectAsXML( Object object, Appendable appendable )
  {
    final String encoding = DEFAULT_ENCODING;
    JAXBXMLHelper.storeObjectAsXML( object, appendable, encoding );
  }
  
  /**
   * Stores the given object as XML within the given {@link Appendable} using the given encoding. E.g as {@link Appendable} a
   * {@link StringBuilder} or {@link StringBuffer} can be used.
   * 
   * @param object
   * @param appendable
   * @param encoding
   */
  public static void storeObjectAsXML( Object object, Appendable appendable, String encoding )
  {
    //
    if ( object != null && appendable != null )
    {
      //
      ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
      
      //
      JAXBXMLHelper.storeObjectAsXML( object, byteArrayContainer.getOutputStream(), encoding );
      
      //
      byteArrayContainer.writeTo( appendable, encoding );
    }
  }
  
  /**
   * Stores the given object as XML {@link String} using the {@link #DEFAULT_ENCODING}
   * 
   * @see #storeObjectAsXML(Object, String)
   * @param object
   * @return
   */
  public static String storeObjectAsXML( Object object )
  {
    return JAXBXMLHelper.storeObjectAsXML( object, DEFAULT_ENCODING );
  }
  
  /**
   * Similar to {@link #storeObjectAsXML(Object)} but allows to specify a {@link MarshallingConfiguration} instance
   * 
   * @param object
   * @param marshallingConfiguration
   *          {@link MarshallingConfiguration}
   * @return xml content
   */
  public static String storeObjectAsXML( Object object, MarshallingConfiguration marshallingConfiguration )
  {
    //
    marshallingConfiguration = MarshallingConfiguration.defaultMarshallingConfiguration( marshallingConfiguration );
    
    //
    final ExceptionHandler exceptionHandler = marshallingConfiguration.getExceptionHandler();
    final String encoding = marshallingConfiguration.getEncoding();
    
    //
    final ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
    final OutputStream outputStream = byteArrayContainer.getOutputStream();
    
    //
    try
    {
      JAXBXMLHelper.storeObjectAsXML( object, outputStream, marshallingConfiguration );
      outputStream.close();
    }
    catch ( Exception e )
    {
      if ( exceptionHandler != null )
      {
        exceptionHandler.handleException( e );
      }
    }
    
    //
    return byteArrayContainer.toString( encoding );
  }
  
  /**
   * Stores the given object as XML {@link String} using the {@link #DEFAULT_ENCODING}
   * 
   * @see #storeObjectAsXML(Object, String)
   * @param object
   * @param exceptionHandler
   *          {@link ExceptionHandler}
   * @return
   */
  public static String storeObjectAsXML( Object object, ExceptionHandler exceptionHandler )
  {
    return JAXBXMLHelper.storeObjectAsXML( object, DEFAULT_ENCODING, exceptionHandler );
  }
  
  /**
   * Stores the given object as XML {@link String} using the given encoding.
   * 
   * @see #storeObjectAsXML(Object, OutputStream)
   * @see #storeObjectAsXML(Object)
   * @param object
   * @param encoding
   * @return
   */
  public static String storeObjectAsXML( Object object, String encoding )
  {
    //
    ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
    
    //
    JAXBXMLHelper.storeObjectAsXML( object, byteArrayContainer.getOutputStream(), encoding );
    
    //
    return byteArrayContainer.toString( encoding );
  }
  
  /**
   * @see #storeObjectAsXML(Object, String)
   * @param object
   * @param encoding
   * @param exceptionHandler
   * @return
   */
  public static String storeObjectAsXML( Object object, String encoding, ExceptionHandler exceptionHandler )
  {
    //
    ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
    
    //
    JAXBXMLHelper.storeObjectAsXML( object, byteArrayContainer.getOutputStream(), encoding, exceptionHandler );
    
    //
    return byteArrayContainer.toString( encoding );
  }
  
  /**
   * Loads an object from the given class type from an {@link InputStream}. The class has to have JAXB annotations.
   * 
   * @param <E>
   * @param inputStream
   * @param typeClazz
   * @return
   */
  public static <E> E loadObjectFromXML( InputStream inputStream, Class<E> typeClazz )
  {
    final ExceptionHandler exceptionHandler = null;
    return loadObjectFromXML( inputStream, typeClazz, exceptionHandler );
  }
  
  /**
   * Loads an object of the given class type from an {@link InputStream}. The class has to have JAXB annotations.
   * 
   * @param <E>
   * @param inputStream
   * @param type
   * @param exceptionHandler
   * @return
   */
  public static <E> E loadObjectFromXML( InputStream inputStream, Class<E> type, ExceptionHandler exceptionHandler )
  {
    //
    final UnmarshallingConfiguration unmarshallingConfiguration = null;
    return loadObjectFromXML( inputStream, type, unmarshallingConfiguration );
  }
  
  /**
   * Similar to {@link #loadObjectFromXML(InputStream, Class)} allowing to declare a {@link UnmarshallingConfiguration}
   * 
   * @param <E>
   * @param inputStream
   *          {@link InputStream}
   * @param type
   * @param unmarshallingConfiguration
   *          {@link UnmarshallingConfiguration}
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <E> E loadObjectFromXML( InputStream inputStream,
                                         Class<E> type,
                                         UnmarshallingConfiguration unmarshallingConfiguration )
  {
    //
    E retval = null;
    
    //
    unmarshallingConfiguration = UnmarshallingConfiguration.defaultUnmarshallingConfiguration( unmarshallingConfiguration );
    final ExceptionHandler exceptionHandler = unmarshallingConfiguration.getExceptionHandler();
    Class<?>[] knownTypes = unmarshallingConfiguration.getKnownTypes();
    final String encoding = unmarshallingConfiguration.getEncoding();
    
    //
    try
    {
      //
      final Class<?>[] contextTypes = ArrayUtils.add( knownTypes, type );
      final JAXBContext context = JAXBContext.newInstance( contextTypes );
      final Unmarshaller um = context.createUnmarshaller();
      
      //
      if ( encoding != null )
      {
        //
        final Reader reader = new InputStreamReader( inputStream, encoding );
        retval = (E) um.unmarshal( reader );
      }
      else
      {
        //
        retval = (E) um.unmarshal( inputStream );
      }
    }
    catch ( Exception e )
    {
      if ( exceptionHandler != null )
      {
        exceptionHandler.handleException( e );
      }
    }
    //
    return retval;
  }
  
  /**
   * Similar to {@link #loadObjectFromNode(Node, Class, ExceptionHandler)} but ignoring {@link Exception}s
   * 
   * @param <E>
   * @param node
   *          {@link Node}
   * @param type
   * @return
   */
  public static <E> E loadObjectFromNode( Node node, Class<E> type )
  {
    //
    final ExceptionHandler exceptionHandler = null;
    return loadObjectFromNode( node, type, exceptionHandler );
  }
  
  /**
   * Loads an object of the given class type from an {@link Node}. The class has to have JAXB annotations.
   * 
   * @param <E>
   * @param node
   *          {@link Node}
   * @param type
   * @param exceptionHandler
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <E> E loadObjectFromNode( Node node, Class<E> type, ExceptionHandler exceptionHandler )
  {
    //
    E retval = null;
    
    //
    try
    {
      //
      final JAXBContext context = JAXBContext.newInstance( type );
      final Unmarshaller um = context.createUnmarshaller();
      
      //
      retval = (E) um.unmarshal( node );
    }
    catch ( Exception e )
    {
      if ( exceptionHandler != null )
      {
        exceptionHandler.handleException( e );
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Loads an {@link Object} from a {@link CharSequence} which contains valid xml text content. The given {@link Class} type
   * specifies the root object type.
   * 
   * @param charSequence
   * @param type
   * @return
   */
  public static <E> E loadObjectFromXML( CharSequence charSequence, Class<E> type )
  {
    //
    final ExceptionHandler exceptionHandler = null;
    return loadObjectFromXML( charSequence, type, exceptionHandler );
  }
  
  /**
   * Loads an {@link Object} from a {@link CharSequence} which contains valid xml text content. The given {@link Class} type
   * specifies the root object type.
   * 
   * @param charSequence
   * @param type
   * @param exceptionHandler
   * @return
   */
  public static <E> E loadObjectFromXML( CharSequence charSequence, Class<E> type, ExceptionHandler exceptionHandler )
  {
    //
    ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
    byteArrayContainer.copyFrom( charSequence );
    
    //
    return JAXBXMLHelper.loadObjectFromXML( byteArrayContainer.getInputStream(), type, exceptionHandler );
  }
  
  /**
   * Similar to {@link #loadObjectFromXML(CharSequence, Class)} allowing to specify an {@link UnmarshallingConfiguration}
   * 
   * @param charSequence
   *          {@link CharSequence}
   * @param type
   * @param unmarshallingConfiguration
   *          {@link UnmarshallingConfiguration}
   * @return
   */
  public static <E> E loadObjectFromXML( CharSequence charSequence,
                                         Class<E> type,
                                         UnmarshallingConfiguration unmarshallingConfiguration )
  {
    //
    unmarshallingConfiguration = UnmarshallingConfiguration.defaultUnmarshallingConfiguration( unmarshallingConfiguration );
    final String encoding = unmarshallingConfiguration.getEncoding();
    
    //
    final ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
    byteArrayContainer.copyFrom( charSequence, encoding );
    
    //
    return JAXBXMLHelper.loadObjectFromXML( byteArrayContainer.getInputStream(), type, unmarshallingConfiguration );
  }
  
  /**
   * @param xmlContent
   * @param typeClazz
   * @return
   */
  public static <E> E loadObjectFromXML( String xmlContent, Class<E> typeClazz )
  {
    //
    final ExceptionHandler exceptionHandler = null;
    return loadObjectFromXML( xmlContent, typeClazz, exceptionHandler );
  }
  
  /**
   * @param xmlContent
   * @param typeClazz
   * @param exceptionHandler
   * @return
   */
  public static <E> E loadObjectFromXML( String xmlContent, Class<E> typeClazz, ExceptionHandler exceptionHandler )
  {
    //
    ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
    byteArrayContainer.copyFrom( xmlContent );
    
    //
    return JAXBXMLHelper.loadObjectFromXML( byteArrayContainer.getInputStream(), typeClazz, exceptionHandler );
  }
  
  /**
   * Clones a given {@link Object} using {@link #storeObjectAsXML(Object)} and {@link #loadObjectFromXML(CharSequence, Class)}
   * 
   * @param object
   * @return
   */
  public static <E> E cloneObject( E object )
  {
    //
    final MarshallingConfiguration marshallingConfiguration = null;
    return cloneObject( object, marshallingConfiguration );
  }
  
  /**
   * Similar to {@link #cloneObject(Object)} allowing to specify a {@link MarshallingConfiguration}
   * 
   * @param object
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <E> E cloneObject( E object, MarshallingConfiguration marshallingConfiguration )
  {
    //
    E retval = null;
    
    //
    marshallingConfiguration = MarshallingConfiguration.defaultMarshallingConfiguration( marshallingConfiguration );
    final ExceptionHandler exceptionHandler = marshallingConfiguration.getExceptionHandler();
    
    //
    if ( object != null )
    {
      //
      try
      {
        
        //
        final ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
        final OutputStream outputStream = byteArrayContainer.getOutputStream();
        
        //
        JAXBXMLHelper.storeObjectAsXML( object, outputStream, marshallingConfiguration );
        outputStream.close();
        
        //
        retval = (E) JAXBXMLHelper.loadObjectFromXML( byteArrayContainer.getInputStream(), object.getClass(),
                                                      marshallingConfiguration.asUnmarshallingConfiguration() );
      }
      catch ( Exception e )
      {
        if ( exceptionHandler != null )
        {
          exceptionHandler.handleException( e );
        }
      }
    }
    
    //
    return retval;
  }
}