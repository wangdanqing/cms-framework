/*
 * Created on 2005-12-29
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.pusuo.cms.client.xport.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author Alfred.Yuan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DbConnection {
    
    /**
     * �ر���ݿ�������ʹ�õ���Դ
     * @param rs ��¼��
     * @param pstmt Ԥ�������
     * @param conn ��ݿ�����
     */
    public static void close (
        ResultSet rs,
        PreparedStatement pstmt,
        Connection conn)
    {
        try
        {
            if(rs!=null){
                rs.close();
            }
        }
        catch (Exception e)
        {
        }

        try
        {
            if(pstmt!=null)
                pstmt.close();
        }
        catch (Exception e)
        {
        }

        try
        {
            if(conn!=null)
                conn.close();
        }
        catch (Exception e)
        {
        }
    }

    /**
     * �ر���ݿ�������ʹ�õ���Դ
     * @param rs ��¼��
     * @param pst Ԥ�������
     * @param conn ��ݿ�����
     */
    public static void close (
        ResultSet rs,
        Statement st,
        Connection conn)
    {
        try
        {
            if(rs!=null)
                rs.close();
        }
        catch (Exception e)
        {}

        try
        {
            if(st!=null)
                st.close();
        }
        catch (Exception e)
        {
        }

        try
        {
            if(conn!=null)
                conn.close();
        }
        catch (Exception e)
        {}
    }

    /**
     * �ر���ݿ�������ʹ�õ���Դ
     * @param pst Ԥ�������
     * @param conn ��ݿ�����
     */
    public static void close (
        Statement st,
        Connection conn)
    {
        close(null, st, conn);
    }

    /**
     * �ر���ݿ�������ʹ�õ���Դ
     * @param pst Ԥ�������
     * @param conn ��ݿ�����
     */
    public static void close (
        PreparedStatement pstmt,
        Connection conn)
    {
        close(null, pstmt, conn);
    }

    /**
     * �ر���ݿ�������ʹ�õ���Դ
     * @param conn ��ݿ�����
     */
    public static void close (Connection conn)
    {
        close(null, null, conn);
    }

    /**
     * �ر���ݿ�������ʹ�õ���Դ
     * @param rs ��¼��
     * @param pst Ԥ�������
     */
    public static void close (ResultSet rs, Statement st)
    {
        close(rs, st, null);
    }

    /**
     * �ر���ݿ�������ʹ�õ���Դ
     * @param rs ��¼��
     * @param pst Ԥ�������
     */
    public static void close (ResultSet rs, PreparedStatement pstmt)
    {
        close(rs, pstmt, null);
    }

    /**
     * �ر���ݿ�������ʹ�õ���Դ
     * @param rs ��¼��
     */
    public static void close (ResultSet rs)
    {
        close(rs, null, null);
    }

    /**
     * �ر���ݿ�������ʹ�õ���Դ
     * @param pst Ԥ�������
     */
    public static void close (Statement st)
    {
        close(null, st, null);
    }

    /**
     * �ر���ݿ�������ʹ�õ���Դ
     * @param pst Ԥ�������
     */
    public static void close (PreparedStatement pstmt)
    {
        close(null, pstmt, null);
    }

}
