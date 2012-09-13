package net.pusuo.cms.client.compile;

import java.io.*;
import java.util.*;

import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class XMLProperties
{
	private static final Log log = LogFactory.getLog(XMLProperties.class);

	private File file = null;
	private Document doc = null;
	//private HashMap propertyCache = new HashMap();

	public XMLProperties(String filename)
	throws Exception
	{
		try
		{
			this.file = new File(filename);
			SAXBuilder builder = new SAXBuilder();
			doc = builder.build( this.file );
		}catch( Exception e)
		{
			log.error("initial xml file error. "+e.toString());
			throw new Exception("initial xml file error. "+e.toString());
		}
	}

        public XMLProperties( InputStream in )
        throws Exception
        {
                try
                {
                        SAXBuilder builder = new SAXBuilder();
                        doc = builder.build( in );
                }catch( Exception e) {
                        log.error("initial xml file error. "+e.toString());
                        throw new Exception("initial xml file error. "+e.toString());
                }
        }


	private synchronized void saveProperties()
	{
		OutputStream out = null;
		boolean error = false;
		File tempFile = null;
		try
		{
			tempFile = new File( this.file.getParent() + this.file.getName()+".tmp" );
			XMLOutputter outputter = new XMLOutputter( Format.getPrettyFormat() );

			out = new BufferedOutputStream( new FileOutputStream(tempFile) );
			outputter.output( doc, out );
		}catch(Exception e)
		{
			error = true;
			log.error("save compile config file error. "+e.toString());
		}finally
		{
			try
			{
				if( out!=null ) out.close();
			}catch(Exception e)
			{
				log.error("close config file error. "+e.toString());
			}
		}
		if(!error)
		{
			this.file.delete();
			tempFile.renameTo( this.file );
		}
	}

	private String[] parsePropName( String key )
	{
		if(key.indexOf(".")==-1)
		{
			log.warn("invalid key "+key);
			return null;
		}
		return key.split("\\.");
	}

	public void setProperty( String key, String value )
	{
		//propertyCache.put( key, value );

		String[] propName = parsePropName( key );

		Element element = doc.getRootElement();
		for(int i=0; propName!=null && i<propName.length; i++)
		{
			if( element.getChild( propName[i] ) == null )
			{
				element.addContent( new Element(propName[i]) );
			}
			element = element.getChild( propName[i] );
		}
		element.setText( value );
		saveProperties();
	}

	public void setProperties( String key, Element e )
	{
		Element element = doc.getRootElement();
		String[] propName = parsePropName( key );
		for(int i=0; propName!=null && i<propName.length; i++)
		{
			if( element.getChild(propName[i])==null )
			{
				return;
			}
			element = element.getChild( propName[i] );
		}
		element.addContent( e );
		saveProperties();
	}

	public String getProperty( String key )
	{
		/*
		if( propertyCache.containsKey(key) )
		{
			return (String)propertyCache.get(key);
		}
		*/
		String[] propName = parsePropName(key);
		Element element = doc.getRootElement();
		for(int i=0; propName!=null && i<propName.length; i++)
		{
			element = element.getChild( propName[i] );
			if(element==null)
			{
				log.warn("element "+propName[i]+" is null");
				return null;
			}
		}
		String value = element.getText();
		if(value.equals(""))
		{
			log.warn("element "+ element.getName() +" value is null");
			return null;
		}
		else
		{
			value = value.trim();
			//propertyCache.put( key, value );
			return value;
		}
	}

	public Integer getIntProperty( String key )
	{
		String temp = getProperty(key);
		try
		{
			return Integer.valueOf(temp);
		}catch(NumberFormatException e)
		{
			return null;
		}
	}
	public Long getLongProperty( String key )
	{
		String temp = getProperty(key);
		try
		{
			return Long.valueOf(temp);
		}catch(NumberFormatException e)
		{
			return null;
		}
	}

	public Element[] getProperties( String key )
	{
		String[] propName = parsePropName( key );
		Element element = doc.getRootElement();

		for(int i=0; propName!=null && i<propName.length; i++)
		{
			element = element.getChild( propName[i] );
			if( element==null )
			{
				log.warn("invalid element "+propName[i]+" @ "+key);
				return null;
			}
		}
		List list = element.getChildren();
		
		if( list==null || list.size()==0 )
		{
			return new Element[] {};
		}else
		{
			Element[] values = new Element[ list.size() ];
			for(int i=0; i<list.size(); i++)
			{
				values[i] = (Element)list.get(i);
			}
			return values;
		}
	}

	public void deleteProperty( String key )
	{
		String[] propName = parsePropName( key );
		Element element = doc.getRootElement();
		for(int i=0; propName!=null && i<propName.length-1; i++)
		{
			element = element.getChild( propName[i] );
			if(element==null) return;
		}
		element.removeChild( propName[propName.length-1] );
		saveProperties();
	}

	public void deleteProperties( String key )
	{
		String[] propName = parsePropName( key );
		Element element = doc.getRootElement();
		for(int i=0; propName!=null && i<propName.length; i++)
		{
			element = element.getChild( propName[i] );
			if(element==null)
			{
				log.error("element is null "+propName[i]);
				return;
			}
		}
		element.setText("");
		saveProperties();
	}

	public void dumpCache()
	{
		//propertyCache.clear();
	}
}

