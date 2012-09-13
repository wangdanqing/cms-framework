package net.pusuo.cms.client.util;

import java.util.*;
import java.io.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.MethodRetryHandler;
import org.apache.commons.httpclient.DefaultMethodRetryHandler;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;

public class ClientHttpFile
{
	private static final Log log = LogFactory.getLog( ClientHttpFile.class );

	/*
	public static InputStream wgetInputstream( String weburl, Map params )
	{
		HttpClient client = new HttpClient();
		PostMethod method = new PostMethod( weburl );

		InputStream in = null;
		try
		{
			DefaultMethodRetryHandler retryHandler = new DefaultMethodRetryHandler();
			retryHandler.setRetryCount( 3 );
			method.setMethodRetryHandler( retryHandler );

			Iterator itr = params.keySet().iterator();
			NameValuePair[] nvps = new NameValuePair[ params.size() ];

			int c = 0;
			while( itr.hasNext() )
			{
				String name = (String)itr.next();
				String value = (String)params.get( name );

				NameValuePair nvp = new NameValuePair(name, value);
				nvps[c++] = nvp;
			}
			method.setRequestBody( nvps );
			int status = client.executeMethod( method );
			if( status!=HttpStatus.SC_OK )
			{
				log.warn("wgetStream not expected. url="+weburl+"  status="+status);
				return null;
			}
			return method.getResponseBodyAsStream();
		}catch(Exception e){
			log.error("wgetStream exception. url="+weburl, e);
			return null;
		//}finally {
		//	method.releaseConnection();
		}
	}
	*/

	public static String wgetString( String weburl, Map params )
	{
		HttpClientParams clientParams = new HttpClientParams();
		clientParams.setSoTimeout( 2000 );

		HttpClient client = new HttpClient( clientParams );
		PostMethod method = new PostMethod( weburl );

		String ret = null;
		int status = -1;
		try
		{
			DefaultMethodRetryHandler retryHandler = new DefaultMethodRetryHandler();
			retryHandler.setRetryCount( 3 );
			method.setMethodRetryHandler( retryHandler );

			Iterator itr = params.keySet().iterator();
			NameValuePair[] nvps = new NameValuePair[ params.size() ];

			int c = 0;
			while( itr.hasNext() )
			{
				String name = (String)itr.next();
				String value = (String)params.get( name );
				NameValuePair nvp = new NameValuePair( name, value );
				nvps[c++] = nvp;
			}
			method.setRequestBody( nvps );
			status = client.executeMethod( method );
			if( status!=HttpStatus.SC_OK )
			{
				log.warn("wgetString not expected -- url="+weburl+" status="+status);
				return null;
			}
			return method.getResponseBodyAsString();
		}catch(Exception e){
			log.warn("wgetString not expected -- url="+weburl+" status="+status);
			return null;
		}finally {
			method.releaseConnection();
		}
	}	
	
	/**
	 * 由于sogou的关键词接口总是超时,影响编译速度,这个方法主要是减少soTimeOut的值
	 * @param weburl
	 * @param params
	 * @param soTimeout
	 * @return
	 */
	public static String wgetIfcString( String weburl, Map params,int soTimeout )
	{
		HttpClientParams clientParams = new HttpClientParams();
		clientParams.setSoTimeout(soTimeout);

		HttpClient client = new HttpClient( clientParams );
		PostMethod method = new PostMethod( weburl );

		String ret = null;
		int status = -1;
		try
		{
			Iterator itr = params.keySet().iterator();
			NameValuePair[] nvps = new NameValuePair[ params.size() ];

			int c = 0;
			while( itr.hasNext() )
			{
				String name = (String)itr.next();
				String value = (String)params.get( name );
				NameValuePair nvp = new NameValuePair( name, value );
				nvps[c++] = nvp;
			}
			method.setRequestBody( nvps );
			status = client.executeMethod( method );
			if( status!=HttpStatus.SC_OK )
			{
				log.warn("wgetIfcString status error. url="+weburl+"  status="+status);
				return null;
			}
			return method.getResponseBodyAsString();
		}catch(Exception e){
			log.error("wgetIfcString exception. url="+weburl+" ......status="+status+":"+e.getMessage());
			return null;
		}finally {
			method.releaseConnection();
		}
	}
	public static String wgetIfcString( String weburl, int soTimeout )
	{
		HttpClientParams clientParams = new HttpClientParams();
		clientParams.setSoTimeout(soTimeout);

		HttpClient client = new HttpClient( clientParams );
		GetMethod method = new GetMethod( weburl );

		String ret = null;
		int status = -1;
		try
		{
			status = client.executeMethod( method );
			if( status!=HttpStatus.SC_OK )
			{
				log.warn("wgetIfcString status error. url="+weburl+"  status="+status);
				return null;
			}
			return method.getResponseBodyAsString();
		}catch(Exception e){
			log.error("wgetIfcString exception. url="+weburl+" ......status="+status+":"+e.getMessage());
			return null;
		}finally {
			method.releaseConnection();
		}
	}
}

