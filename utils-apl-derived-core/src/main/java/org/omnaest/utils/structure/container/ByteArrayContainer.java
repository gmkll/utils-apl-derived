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
package org.omnaest.utils.structure.container;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.download.DownloadConnection;
import org.omnaest.utils.download.URIHelper;
import org.omnaest.utils.download.URLHelper;
import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.streams.StreamConnector;

/**
 * This class is a simple container to hold a byte array.<br>
 * It additionally offers some simple methods to load the byte array from different ways.
 * 
 * @author Omnaest
 */
public class ByteArrayContainer
{
  /* ********************************************** Constants ********************************************** */
  public final static String ENCODING_UTF8      = "utf-8";
  
  /** UTF-8 */
  public final static String DEFAULTENCODING    = ENCODING_UTF8;
  public final static String DEFAULTZIPFILENAME = "data.dat";
  
  /* ********************************************** Variables ********************************************** */
  private byte[]             content            = null;
  private boolean            isContentInvalid   = false;
  
  /* ********************************************** Beans / Services / References ********************************************** */
  private ExceptionHandler   exceptionHandler   = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * Default {@link ByteArrayContainer} creating an instance with no content.
   */
  public ByteArrayContainer()
  {
    super();
  }
  
  /**
   * Creates an {@link ByteArrayContainer} with a copied byte array content.
   * 
   * @see #copyFrom(byte[])
   * @param content
   */
  public ByteArrayContainer( byte[] content )
  {
    this();
    this.copyFrom( content );
  }
  
  /**
   * @see #copyFrom(String)
   * @param content
   */
  public ByteArrayContainer( String content )
  {
    this();
    this.copyFrom( content );
  }
  
  /**
   * @see #copyFrom(String, String)
   * @param content
   */
  public ByteArrayContainer( String content, String encoding )
  {
    this();
    this.copyFrom( content, encoding );
  }
  
  /**
   * @see #copyFrom(CharSequence)
   * @param charsequence
   */
  public ByteArrayContainer( CharSequence charsequence, String encoding )
  {
    this();
    this.copyFrom( charsequence, encoding );
  }
  
  /**
   * @param sourceInputStream
   * @see #copyFrom(InputStream)
   */
  public ByteArrayContainer( InputStream sourceInputStream )
  {
    this();
    this.copyFrom( sourceInputStream );
  }
  
  /**
   * @see #copyFrom(CharSequence)
   * @param charsequence
   */
  public ByteArrayContainer( CharSequence charsequence )
  {
    this();
    this.copyFrom( charsequence );
  }
  
  /**
   * @see ByteArrayContainer
   * @see #copyFrom(Readable)
   * @param readable
   */
  public ByteArrayContainer( Readable readable )
  {
    this();
    this.copyFrom( readable );
  }
  
  /**
   * @return true: content is empty, false: container has content with size > 0
   * @see #isNotEmpty()
   */
  public boolean isEmpty()
  {
    return ( this.content == null || this.content.length == 0 );
  }
  
  /**
   * @see #isEmpty()
   * @return false: content is empty, true: container has content with size > 0
   */
  public boolean isNotEmpty()
  {
    return !this.isEmpty();
  }
  
  /**
   * @see #download(URL)
   * @param uri
   */
  public void download( URI uri )
  {
    this.download( URIHelper.getURLfromURI( uri ) );
  }
  
  /**
   * Downloads the content from the given url resource.
   * 
   * @see #download(URI)
   * @see #download(String)
   * @param url
   */
  public void download( URL url )
  {
    if ( url != null )
    {
      DownloadConnection downloadConnection = new DownloadConnection();
      downloadConnection.download( url );
      this.copyFrom( downloadConnection.getContentAsBytes() );
    }
    else
    {
      this.clear();
    }
  }
  
  /**
   * Downloads the content from the given url resource. Ensure the urlStr is encoded correctly and is valid at all.
   * 
   * @see #download(URI)
   * @see #download(URL)
   * @param urlStr
   */
  public void download( String urlStr )
  {
    this.download( URLHelper.createURL( urlStr ) );
  }
  
  /**
   * Loads the content of the given file into the container.
   * 
   * @param file
   * @deprecated use {@link #copyFrom(File)} instead
   */
  @Deprecated
  public void load( File file )
  {
    this.copyFrom( file );
  }
  
  /**
   * Copies the content of the given {@link File} into the {@link ByteArrayContainer}. <br>
   * <br>
   * If the operation fails the {@link #isContentInvalid()} will return true
   * 
   * @see #isContentInvalid()
   * @param file
   *          {@link File}
   * @return this
   */
  public ByteArrayContainer copyFrom( File file )
  {
    //
    this.setContentInvalid( true );
    this.clear();
    
    //
    try
    {
      //
      final FileInputStream fileInputStream = new FileInputStream( file );
      final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      try
      {
        //
        final BufferedInputStream bufferedInputStream = new BufferedInputStream( fileInputStream );
        StreamConnector.connect( bufferedInputStream, byteArrayOutputStream );
        bufferedInputStream.close();
      }
      finally
      {
        fileInputStream.close();
        byteArrayOutputStream.close();
      }
      
      //
      this.content = byteArrayOutputStream.toByteArray();
      
      //
      this.setContentInvalid( false );
    }
    catch ( Exception e )
    {
      handleException( e );
    }
    
    //
    return this;
  }
  
  /**
   * Handles the given {@link Exception} using the internal {@link #exceptionHandler} if the reference is not null
   * 
   * @param e
   */
  private void handleException( Exception e )
  {
    if ( this.exceptionHandler != null )
    {
      this.exceptionHandler.handleException( e );
    }
  }
  
  /**
   * Saves the content of the {@link ByteArrayContainer} to a given {@link File}
   * 
   * @param file
   *          {@link File}
   * @deprecated use {@link #writeTo(File)} instead
   */
  @Deprecated
  public void save( File file )
  {
    this.writeTo( file );
  }
  
  /**
   * Writes the content of the {@link ByteArrayContainer} to the given {@link File}
   * 
   * @param file
   *          {@link File}
   */
  public void writeTo( File file )
  {
    //
    try
    {
      //
      final OutputStream fileOutputStream = new FileOutputStream( file );
      try
      {
        final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream( fileOutputStream );
        try
        {
          bufferedOutputStream.write( this.content );
          bufferedOutputStream.flush();
        }
        finally
        {
          bufferedOutputStream.close();
        }
        fileOutputStream.flush();
      }
      finally
      {
        fileOutputStream.close();
      }
    }
    catch ( Exception e )
    {
      handleException( e );
    }
  }
  
  /**
   * Copies the content from another container into this one.
   * 
   * @param sourceByteArrayContainer
   * @return this
   */
  public ByteArrayContainer copyFrom( ByteArrayContainer sourceByteArrayContainer )
  {
    this.copyFrom( sourceByteArrayContainer.getInputStream() );
    return this;
  }
  
  /**
   * Copies the content from an {@link InputStream} into the {@link ByteArrayContainer} without closing the {@link InputStream} <br>
   * <br>
   * If the copy operation fails the content of the {@link ByteArrayContainer} is set to invalid.
   * 
   * @see #isContentInvalid()
   * @param sourceInputStream
   * @return this
   */
  public ByteArrayContainer copyFrom( InputStream sourceInputStream )
  {
    //
    this.isContentInvalid = true;
    this.clear();
    
    //
    try
    {
      //
      Assert.isNotNull( sourceInputStream, "sourceInputStream must not be null" );
      
      //
      OutputStream outputStream = this.getOutputStream();
      StreamConnector.connect( sourceInputStream, outputStream );
      outputStream.close();
      this.isContentInvalid = false;
    }
    catch ( Exception e )
    {
      this.handleException( e );
    }
    
    //
    return this;
  }
  
  /**
   * Uses the given {@link URL#openStream()} to retrieve data which will be available within the {@link ByteArrayContainer} <br>
   * <br>
   * If the data cannot be resolved completely the {@link #isContentInvalid()} will return true afterwards.
   * 
   * @see #isContentInvalid()
   * @see #copyFrom(InputStream)
   * @param url
   *          {@link URL}
   * @return this
   */
  public ByteArrayContainer copyFrom( URL url )
  {
    //
    this.isContentInvalid = true;
    this.clear();
    InputStream sourceInputStream = null;
    try
    {
      Assert.isNotNull( url, "url must not be null" );
      
      sourceInputStream = url.openStream();
      BufferedInputStream bufferedInputStream = new BufferedInputStream( sourceInputStream );
      this.copyFrom( bufferedInputStream );
      bufferedInputStream.close();
      
      this.isContentInvalid = false;
    }
    catch ( IOException e )
    {
      this.handleException( e );
    }
    finally
    {
      try
      {
        sourceInputStream.close();
      }
      catch ( Exception e )
      {
        this.handleException( e );
      }
    }
    
    //
    return this;
  }
  
  /**
   * Similar to {@link #copyFrom(URL)}
   * 
   * @param urlConnection
   *          {@link URLConnection}
   * @return this
   */
  public ByteArrayContainer copyFrom( URLConnection urlConnection )
  {
    //
    this.isContentInvalid = true;
    this.clear();
    
    //
    InputStream sourceInputStream = null;
    try
    {
      //
      Assert.isNotNull( urlConnection, "urlConnection must not be null" );
      
      //
      sourceInputStream = urlConnection.getInputStream();
      BufferedInputStream bufferedInputStream = new BufferedInputStream( sourceInputStream );
      this.copyFrom( bufferedInputStream );
      bufferedInputStream.close();
      
      //
      this.isContentInvalid = true;
    }
    catch ( IOException e )
    {
      this.handleException( e );
    }
    finally
    {
      try
      {
        //
        sourceInputStream.close();
      }
      catch ( Exception e )
      {
        this.handleException( e );
      }
    }
    
    //
    return this;
  }
  
  /**
   * Copies the content from a {@link Readable} using the {@value #ENCODING_UTF8} encoding
   * 
   * @param readable
   * @return this
   */
  public ByteArrayContainer copyFrom( Readable readable )
  {
    return this.copyFrom( readable, ENCODING_UTF8 );
  }
  
  /**
   * Copies the content from a {@link Readable} using the given encoding
   * 
   * @param readable
   * @param encoding
   * @return this
   */
  public ByteArrayContainer copyFrom( Readable readable, String encoding )
  {
    //
    this.isContentInvalid = false;
    if ( readable != null )
    {
      //
      encoding = StringUtils.defaultString( encoding, ENCODING_UTF8 );
      
      //
      try
      {
        //
        final StringBuffer stringBuffer = new StringBuffer();
        final CharBuffer charBuffer = CharBuffer.wrap( new char[1000] );
        for ( int read = 0; read >= 0; )
        {
          //
          charBuffer.clear();
          read = readable.read( charBuffer );
          charBuffer.flip();
          if ( read > 0 )
          {
            stringBuffer.append( charBuffer, 0, read );
          }
        }
        
        this.copyFrom( stringBuffer, encoding );
      }
      catch ( IOException e )
      {
        //
        this.isContentInvalid = true;
        
        //
        this.handleException( e );
      }
    }
    
    //
    return this;
  }
  
  /**
   * Copies the content of a {@link String} into the {@link ByteArrayContainer}. <br>
   * <br>
   * If the copy operation fails the content of the {@link ByteArrayContainer} is set to invalid.
   * 
   * @see #isContentInvalid()
   * @param string
   * @param encoding
   * @return this
   */
  public ByteArrayContainer copyFrom( String string, String encoding )
  {
    //
    this.isContentInvalid = true;
    try
    {
      final OutputStream outputStream = this.getOutputStream();
      try
      {
        final OutputStreamWriter osw = new OutputStreamWriter( outputStream, encoding );
        osw.write( string );
        osw.close();
        this.isContentInvalid = false;
      }
      finally
      {
        outputStream.close();
      }
    }
    catch ( IOException e )
    {
      this.handleException( e );
    }
    
    //
    return this;
  }
  
  /**
   * Copies the content of a String into the container.
   * 
   * @param string
   * @return this
   */
  public ByteArrayContainer copyFrom( String string )
  {
    this.copyFrom( string, DEFAULTENCODING );
    return this;
  }
  
  /**
   * Copies the content of a {@link CharSequence} into the {@link ByteArrayContainer} using the {@value #DEFAULTENCODING}.
   * 
   * @param charSequence
   * @return this
   */
  public ByteArrayContainer copyFrom( CharSequence charSequence )
  {
    this.copyFrom( charSequence, DEFAULTENCODING );
    return this;
  }
  
  /**
   * Copies the content of a {@link CharSequence} into the {@link ByteArrayContainer}.
   * 
   * @param charSequence
   * @param encoding
   * @return this
   */
  public ByteArrayContainer copyFrom( CharSequence charSequence, String encoding )
  {
    this.copyFrom( charSequence.toString(), DEFAULTENCODING );
    return this;
  }
  
  /**
   * Copies the content from a byte array into the container. The data is copied and not the given byte array is used.
   * 
   * @param source
   * @return this
   */
  public ByteArrayContainer copyFrom( byte[] source )
  {
    //
    ByteArrayInputStream bis = new ByteArrayInputStream( source );
    this.copyFrom( bis );
    
    //
    return this;
  }
  
  /**
   * Serializes any given {@link Serializable} element and stores it
   * 
   * @see #toDeserializedElement()
   * @param element
   * @return this
   */
  public <S extends Serializable> ByteArrayContainer copyFromAsSerialized( S element )
  {
    final byte[] content = element != null ? SerializationUtils.serialize( element ) : null;
    this.setContent( content );
    
    return this;
  }
  
  /**
   * Deserializes the content into an element
   * 
   * @see #copyFromAsSerialized(Serializable)
   * @return new element instance
   */
  @SuppressWarnings("unchecked")
  public <S extends Serializable> S toDeserializedElement()
  {
    return this.content != null ? (S) SerializationUtils.deserialize( this.content ) : null;
  }
  
  /**
   * Transforms the content into a String. As default utf-8 is used.
   * 
   * @see #toString(String)
   */
  public String toString()
  {
    return this.toString( DEFAULTENCODING );
  }
  
  /**
   * Transforms the content into a String.
   * 
   * @see #toString()
   * @param encoding
   *          specifies the encoding of the binary data. Example: "utf-8"
   * @return String, null if error happens
   */
  public String toString( String encoding )
  {
    //
    String retval = null;
    
    //
    try
    {
      StringBuffer sb = new StringBuffer();
      StreamConnector.connect( this.getInputStream(), sb, encoding );
      retval = sb.toString();
    }
    catch ( IOException e )
    {
      this.handleException( e );
    }
    
    //
    return retval;
  }
  
  /**
   * Returns the content as a list of strings, separated by line feed and/or carriage return. The default encoding "utf-8" is
   * used.
   * 
   * @return
   */
  public List<String> toStringList()
  {
    return this.toStringList( DEFAULTENCODING );
  }
  
  /**
   * Returns the content as a list of strings, separated by line feed and/or carriage return.
   * 
   * @param encoding
   *          : for example = "utf-8"
   * @return
   */
  public List<String> toStringList( String encoding )
  {
    //
    final String regExDelimiter = "[\r\n]+";
    return this.toStringList( encoding, regExDelimiter );
  }
  
  /**
   * Returns the content as a list of strings, separated by an arbitrary regular expression.
   * 
   * @param encoding
   *          : for example = "utf-8"
   * @see #ENCODING_UTF8
   * @return
   */
  public List<String> toStringList( String encoding, String regExDelimiter )
  {
    //
    List<String> retlist = new ArrayList<String>();
    
    //
    String content = this.toString( encoding );
    
    //
    final String tokenDelimiter = "°}>|<{°";
    content = content.replaceAll( regExDelimiter, tokenDelimiter );
    String[] lines = StringUtils.splitByWholeSeparatorPreserveAllTokens( content, tokenDelimiter );
    CollectionUtils.addAll( retlist, lines );
    
    //
    return retlist;
  }
  
  /**
   * Writes the content of the {@link ByteArrayContainer} to the given {@link Writer} instance. The {@link Writer#flush()} method
   * will be invoked afterwards but it will not be closed.
   * 
   * @see #writeTo(Writer)
   * @param writer
   * @param encoding
   * @return true, if no error occurs
   */
  public boolean writeTo( Writer writer, String encoding )
  {
    //
    boolean retval = true;
    
    //
    if ( writer != null )
    {
      try
      {
        writer.write( this.toString( encoding ) );
        writer.flush();
      }
      catch ( IOException e )
      {
        retval = false;
        this.handleException( e );
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Similar to {@link #writeTo(Writer, String)} using the default encoding {@value #ENCODING_UTF8}
   * 
   * @see #writeTo(Writer, String)
   * @param writer
   * @return true, if no error occurs
   */
  public boolean writeTo( Writer writer )
  {
    return this.writeTo( writer, ENCODING_UTF8 );
  }
  
  /**
   * Writes the content of the {@link ByteArrayContainer} to an {@link Appendable} e.g. a {@link StringBuilder} or
   * {@link StringBuffer}
   * 
   * @param appendable
   * @param encoding
   * @return true : transfer successful
   */
  public boolean writeTo( Appendable appendable, String encoding )
  {
    //
    boolean retval = true;
    
    //
    try
    {
      StringBuffer sb = new StringBuffer();
      StreamConnector.connect( this.getInputStream(), sb, encoding );
      
      //
      appendable.append( sb );
    }
    catch ( IOException e )
    {
      retval = false;
      this.handleException( e );
    }
    
    //
    return retval;
  }
  
  /**
   * Writes the content of the {@link ByteArrayContainer} to a given {@link OutputStream} without closing the {@link OutputStream}
   * but flushing the content to it.
   * 
   * @param outputStream
   * @return true : transfer was successful
   */
  public boolean writeTo( OutputStream outputStream )
  {
    return StreamConnector.transfer( this.getInputStream(), outputStream ).isSuccessful();
  }
  
  /**
   * Clears the content.
   */
  public void clear()
  {
    this.setContent( null );
  }
  
  /**
   * @return
   */
  public byte[] getContent()
  {
    return this.content;
  }
  
  /**
   * @param content
   * @return
   */
  public ByteArrayContainer setContent( byte[] content )
  {
    this.content = content;
    return this;
  }
  
  /**
   * @return
   */
  public InputStream getInputStream()
  {
    InputStream is = null;
    if ( this.content != null )
    {
      is = new ByteArrayInputStream( this.content );
    }
    return is;
  }
  
  /**
   * Returns a {@link Reader} with the default encoding {@value #DEFAULTENCODING}
   * 
   * @return
   */
  public Reader getReader()
  {
    return this.getReader( DEFAULTENCODING );
  }
  
  /**
   * Returns a {@link Reader} using the given encoing
   * 
   * @param encoding
   * @return
   */
  public Reader getReader( String encoding )
  {
    Reader retval = null;
    try
    {
      retval = new InputStreamReader( this.getInputStream(), encoding );
    }
    catch ( UnsupportedEncodingException e )
    {
      this.handleException( e );
    }
    return retval;
  }
  
  /**
   * Returns an outputstream, that allows to write the byte array content directly.<br>
   * Be aware of the fact, that the content is written only if the outputstream is closed correctly!
   * 
   * @return
   */
  public OutputStream getOutputStream()
  {
    return new ByteArrayOutputStream( 0 )
    {
      @Override
      public void close()
      {
        try
        {
          super.close();
        }
        catch ( IOException e )
        {
          if ( ByteArrayContainer.this.exceptionHandler != null )
          {
            ByteArrayContainer.this.exceptionHandler.handleException( e );
          }
        }
        ByteArrayContainer.this.content = this.toByteArray();
      }
      
      @Override
      public void flush() throws IOException
      {
        super.flush();
        ByteArrayContainer.this.content = this.toByteArray();
      }
    };
  }
  
  /**
   * Returns an {@link OutputStreamWriter} using the given encoding
   * 
   * @param encoding
   * @return
   */
  public OutputStreamWriter getOutputStreamWriter( String encoding )
  {
    //
    OutputStreamWriter retval = null;
    
    //
    try
    {
      retval = new OutputStreamWriter( this.getOutputStream(), encoding );
    }
    catch ( Exception e )
    {
      handleException( e );
    }
    
    //
    return retval;
  }
  
  /**
   * Like {@link #getOutputStreamWriter(String)} using the {@link #DEFAULTENCODING}
   * 
   * @see #getOutputStreamWriter(String)
   * @return
   */
  public OutputStreamWriter getOutputStreamWriter()
  {
    return this.getOutputStreamWriter( DEFAULTENCODING );
  }
  
  /**
   * Converts the content into a zip file content. For example, load a file content, call this method, and save the content back
   * to the same file, to make the file zipped.
   */
  public void zip( String zipFileName )
  {
    if ( StringUtils.isNotBlank( zipFileName ) )
    {
      //
      final Map<String, ByteArrayContainer> byteArrayContainerMap = new HashMap<String, ByteArrayContainer>();
      byteArrayContainerMap.put( zipFileName, this );
      ByteArrayContainer tempByteArrayContainer = ByteArrayContainer.zipFilenameByteArrayContainerMap( byteArrayContainerMap );
      if ( tempByteArrayContainer != null )
      {
        this.content = tempByteArrayContainer.content;
      }
    }
  }
  
  /**
   * @see #zip(String)
   */
  public void zip()
  {
    this.zip( ByteArrayContainer.generateRandomDefaultFileName() );
  }
  
  /**
   * Zipps all ByteArrayContainers of a given map, which contains filenames with corresponding unzipped ByteArrayContainer
   * objects.
   * 
   * @param byteArrayContainerMap
   */
  public static ByteArrayContainer zipFilenameByteArrayContainerMap( Map<String, ByteArrayContainer> byteArrayContainerMap )
  {
    //
    final ExceptionHandler exceptionHandler = null;
    return zipFilenameByteArrayContainerMap( byteArrayContainerMap, exceptionHandler );
  }
  
  /**
   * Zipps all ByteArrayContainers of a given map, which contains filenames with corresponding unzipped ByteArrayContainer
   * objects.
   * 
   * @param byteArrayContainerMap
   * @param exceptionHandler
   *          {@link ExceptionHandler}
   */
  public static ByteArrayContainer zipFilenameByteArrayContainerMap( Map<String, ByteArrayContainer> byteArrayContainerMap,
                                                                     ExceptionHandler exceptionHandler )
  {
    //
    ByteArrayContainer zippedBac = new ByteArrayContainer();
    try
    {
      //
      ZipOutputStream zos = new ZipOutputStream( zippedBac.getOutputStream() );
      
      //
      for ( String iFilename : byteArrayContainerMap.keySet() )
      {
        //
        ByteArrayContainer sourceByteArrayContainer = byteArrayContainerMap.get( iFilename );
        
        //
        if ( StringUtils.isNotBlank( iFilename ) && sourceByteArrayContainer != null && sourceByteArrayContainer.isNotEmpty() )
        {
          ZipEntry zipEntry = new ZipEntry( iFilename );
          zos.putNextEntry( zipEntry );
          StreamConnector.connect( sourceByteArrayContainer.getInputStream(), zos );
        }
      }
      
      zos.close();
    }
    catch ( Exception e )
    {
      if ( exceptionHandler != null )
      {
        exceptionHandler.handleException( e );
      }
    }
    
    //
    return zippedBac;
  }
  
  /**
   * Generates a random filename basing on the zip default filename and a random number.
   * 
   * @return
   */
  private static String generateRandomDefaultFileName()
  {
    return DEFAULTZIPFILENAME + String.valueOf( System.currentTimeMillis() )
           + String.valueOf( Math.round( Math.random() * 1000000000l ) );
  }
  
  /**
   * Converts a zip file content into unzipped content. For example load a zip file, and call this method, to get the unzipped
   * content of the file. If an error occurs, the content will be set to null, and null will be returned. <br>
   * <br>
   * Ensure the zip file contains only one file, if you wanna use the current ByteArrayContainer obejct. Only the first file is
   * read for the current container, all others are only saved within the returned map.
   * 
   * @see #unzipIntoFilenameByteArrayContainerMap(ByteArrayContainer)
   * @return Map of the zip file names with unzipped ByteArrayContainer objects, or null, if unzipping was unsuccesful.
   */
  public Map<String, ByteArrayContainer> unzip()
  {
    //
    Map<String, ByteArrayContainer> retmap = ByteArrayContainer.unzipIntoFilenameByteArrayContainerMap( this );
    
    //
    if ( retmap != null && retmap.size() > 0 )
    {
      this.content = null;
      this.copyFrom( retmap.values().iterator().next() );
    }
    else
    {
      this.content = null;
    }
    
    //
    return retmap;
  }
  
  /**
   * Unzipps the given ByteArrayContainer object into a map containing the filenames and unzipped ByteArrayContainer objects for
   * each file.
   * 
   * @param byteArrayContainer
   * @return
   */
  public static Map<String, ByteArrayContainer> unzipIntoFilenameByteArrayContainerMap( ByteArrayContainer byteArrayContainer )
  {
    final ExceptionHandler exceptionHandler = null;
    return unzipIntoFilenameByteArrayContainerMap( byteArrayContainer, exceptionHandler );
  }
  
  /**
   * Unzipps the given ByteArrayContainer object into a map containing the filenames and unzipped ByteArrayContainer objects for
   * each file.
   * 
   * @param byteArrayContainer
   * @param exceptionHandler
   *          {@link ExceptionHandler}
   * @return
   */
  public static Map<String, ByteArrayContainer> unzipIntoFilenameByteArrayContainerMap( ByteArrayContainer byteArrayContainer,
                                                                                        ExceptionHandler exceptionHandler )
  {
    //
    Map<String, ByteArrayContainer> retmap = new LinkedHashMap<String, ByteArrayContainer>();
    
    //
    try
    {
      //
      ZipInputStream zis = new ZipInputStream( byteArrayContainer.getInputStream() );
      
      //
      for ( ZipEntry zipEntry = zis.getNextEntry(); zipEntry != null; zipEntry = zis.getNextEntry() )
      {
        ByteArrayContainer unzippedBac = new ByteArrayContainer();
        StreamConnector.connect( zis, unzippedBac.getOutputStream() );
        retmap.put( zipEntry.getName(), unzippedBac );
      }
    }
    catch ( IOException e )
    {
      if ( exceptionHandler != null )
      {
        exceptionHandler.handleException( e );
      }
    }
    
    //
    return retmap;
  }
  
  /* ********************************************** STATIC FACTORY METHOD PART ********************************************** */
  
  /**
   * Used by the factory method.
   * 
   * @see #createNewInstance
   */
  public static Class<? extends ByteArrayContainer> implementationForByteArrayContainerClass = ByteArrayContainer.class;
  
  /**
   * Creates a new instance of this class.
   * 
   * @see #implementationForByteArrayContainerClass
   */
  public static ByteArrayContainer createNewInstance()
  {
    ByteArrayContainer result = null;
    
    try
    {
      result = ByteArrayContainer.implementationForByteArrayContainerClass.newInstance();
    }
    catch ( Exception e )
    {
    }
    
    return result;
  }
  
  /**
   * Returns true if a previous operation on the {@link ByteArrayContainer} has put the content into a malformed state.
   * 
   * @see #copyFrom(InputStream)
   * @see #copyFrom(String)
   * @return
   */
  public boolean isContentInvalid()
  {
    return this.isContentInvalid;
  }
  
  /**
   * Sets the content of the {@link ByteArrayContainer} to be marked as invalid
   * 
   * @param isContentInvalid
   */
  public void setContentInvalid( boolean isContentInvalid )
  {
    this.isContentInvalid = isContentInvalid;
  }
  
  /**
   * Returns a new {@link PrintStream} using the {@value #DEFAULTENCODING} encoding
   * 
   * @see PrintStream
   * @return
   */
  public PrintStream getPrintStreamWriter()
  {
    //
    return this.getPrintStreamWriter( DEFAULTENCODING );
  }
  
  /**
   * Returns a new {@link PrintStream}
   * 
   * @see PrintStream
   * @param encoding
   * @return
   */
  public PrintStream getPrintStreamWriter( String encoding )
  {
    //
    boolean autoFlush = true;
    return this.getPrintStreamWriter( encoding, autoFlush );
  }
  
  /**
   * Returns a new {@link PrintStream}
   * 
   * @see PrintStream
   * @param encoding
   * @param autoFlush
   * @return
   */
  public PrintStream getPrintStreamWriter( String encoding, boolean autoFlush )
  {
    //
    PrintStream retval = null;
    
    //    
    final OutputStream outputStream = this.getOutputStream();
    try
    {
      retval = new PrintStream( outputStream, autoFlush, encoding );
    }
    catch ( UnsupportedEncodingException e )
    {
      this.handleException( e );
    }
    
    //
    return retval;
  }
  
  /**
   * @param exceptionHandler
   *          {@link ExceptionHandler}
   * @return this
   */
  public ByteArrayContainer setExceptionHandler( ExceptionHandler exceptionHandler )
  {
    //
    this.exceptionHandler = exceptionHandler;
    return this;
  }
  
  /**
   * Returns a new {@link ByteArrayContainer} instance mapping the same byte[] array as the given {@link ByteArrayContainer}
   * 
   * @param byteArrayContainer
   * @return new instance
   */
  public static ByteArrayContainer valueOf( ByteArrayContainer byteArrayContainer )
  {
    final ByteArrayContainer retval = new ByteArrayContainer();
    if ( byteArrayContainer != null )
    {
      retval.setContent( byteArrayContainer.getContent() );
    }
    return retval;
  }
  
}
