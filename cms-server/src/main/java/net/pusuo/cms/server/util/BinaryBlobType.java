package net.pusuo.cms.server.util;

import java.sql.PreparedStatement; 
import java.sql.ResultSet; 
import java.sql.SQLException; 
import java.sql.Types; 
import java.sql.Blob; 
import java.sql.Connection;

import oracle.sql.BLOB;

import java.io.*;

import net.sf.hibernate.Hibernate; 
import net.sf.hibernate.HibernateException; 
import net.sf.hibernate.UserType; 

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
	通过继承HIBERNATE的UserType接口，将ORACLE BLOB字段映射成byte[]

	@since	CMS4.0
	@version 4.0
	@author Mark
*/
public class BinaryBlobType implements UserType 
{
	private static final Log log = LogFactory.getLog(BinaryBlobType.class);

	public int[] sqlTypes() 
	{ 
		return new int[] { Types.BLOB }; 
	}

	public Class returnedClass() 
	{ 
		return byte[].class; 
	} 

	public boolean equals(Object x, Object y) 
	{ 
		return (x == y) 
			|| (x != null 
			&& y != null 
			&& java.util.Arrays.equals((byte[]) x, (byte[]) y)); 
	} 

	public Object nullSafeGet(ResultSet rs, String[] names, Object owner) 
	throws HibernateException, SQLException 
	{ 
		//final Blob blob = rs.getBlob(names[0]); 
		InputStream in = rs.getBinaryStream( names[0] );
		if( in==null ) return null;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		byte[] b = new byte[1024];
		int len = 0;
		try
		{
			while( (len=(in.read(b))) !=-1 )
			{
				baos.write( b, 0, len );
			}
		} catch(Exception e) {
			log.error("nullSafeGet exception -- ", e);
			throw new HibernateException( e.getMessage() );
		} finally {
			try
			{
				if( baos!=null ) baos.close();
			} catch(Exception e){}
		}
		return baos.toByteArray();
		//return blob != null?blob.getBytes(1, (int)blob.length()):null;
	} 

	public void nullSafeSet(PreparedStatement st, Object value, int index) 
	throws HibernateException, SQLException 
	{
		/*
		//st.setBlob(index, Hibernate.createBlob((byte[]) value)); 
		try
		{
			byte [] tmp = (byte[])value;
			java.io.ByteArrayInputStream bai = new java.io.ByteArrayInputStream(tmp);
			st.setBinaryStream(index,bai,tmp.length);
			bai.close();
		}
		catch(java.io.IOException e)
		{
			throw new SQLException("failed write to blob -- " + e.getMessage());
		}
		*/

		// modified by wangzhigang 2005..03.03
		if( value==null )
		{
			st.setNull( index, sqlTypes()[0] );
			return;
		}

		OutputStream out = null;
		try
		{
			Connection conn = st.getConnection().getMetaData().getConnection();
			BLOB blob = BLOB.createTemporary( conn, true, BLOB.DURATION_SESSION );
			try
			{
				blob.open( BLOB.MODE_READWRITE );
				out = blob.getBinaryOutputStream();
				out.write( (byte[])value );
				out.flush();
			} finally {
				if( out!=null ) out.close();
			}
			st.setBlob( index, (Blob)blob );
		} catch(Exception e) {
			throw new SQLException( "failed write to blob" + e.getMessage() );
		}
	} 

	public Object deepCopy(Object value) 
	{ 
		if (value == null) return null; 

		byte[] bytes = (byte[]) value; 
		byte[] result = new byte[bytes.length]; 
		System.arraycopy(bytes, 0, result, 0, bytes.length); 

		return result; 
	} 

	public boolean isMutable() 
	{ 
		return true; 
	} 
}
