/*
 * Created on 2005-12-29
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.pusuo.cms.search.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author Alfred.Yuan
 *         <p/>
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class DbConnection {

    /**
     * 关闭数据库连接所使用的资源
     *
     * @param rs    记录集
     * @param pstmt 预处理语句
     * @param conn  数据库连接
     */
    public static void close(
            ResultSet rs,
            PreparedStatement pstmt,
            Connection conn) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (Exception e) {
        }

        try {
            if (pstmt != null)
                pstmt.close();
        } catch (Exception e) {
        }

        try {
            if (conn != null)
                conn.close();
        } catch (Exception e) {
        }
    }

    /**
     * 关闭数据库连接所使用的资源
     *
     * @param rs   记录集
     * @param st  预处理语句
     * @param conn 数据库连接
     */
    public static void close(
            ResultSet rs,
            Statement st,
            Connection conn) {
        try {
            if (rs != null)
                rs.close();
        } catch (Exception e) {
        }

        try {
            if (st != null)
                st.close();
        } catch (Exception e) {
        }

        try {
            if (conn != null)
                conn.close();
        } catch (Exception e) {
        }
    }

    /**
     * 关闭数据库连接所使用的资源
     *
     * @param st  预处理语句
     * @param conn 数据库连接
     */
    public static void close(
            Statement st,
            Connection conn) {
        close(null, st, conn);
    }

    /**
     * 关闭数据库连接所使用的资源
     *
     * @param pstmt  预处理语句
     * @param conn 数据库连接
     */
    public static void close(
            PreparedStatement pstmt,
            Connection conn) {
        close(null, pstmt, conn);
    }

    /**
     * 关闭数据库连接所使用的资源
     *
     * @param conn 数据库连接
     */
    public static void close(Connection conn) {
        close(null, null, conn);
    }

    /**
     * 关闭数据库连接所使用的资源
     *
     * @param rs  记录集
     * @param st 预处理语句
     */
    public static void close(ResultSet rs, Statement st) {
        close(rs, st, null);
    }

    /**
     * 关闭数据库连接所使用的资源
     *
     * @param rs  记录集
     * @param pstmt 预处理语句
     */
    public static void close(ResultSet rs, PreparedStatement pstmt) {
        close(rs, pstmt, null);
    }

    /**
     * 关闭数据库连接所使用的资源
     *
     * @param rs 记录集
     */
    public static void close(ResultSet rs) {
        close(rs, null, null);
    }

    /**
     * 关闭数据库连接所使用的资源
     *
     * @param st 预处理语句
     */
    public static void close(Statement st) {
        close(null, st, null);
    }

    /**
     * 关闭数据库连接所使用的资源
     *
     * @param pstmt 预处理语句
     */
    public static void close(PreparedStatement pstmt) {
        close(null, pstmt, null);
    }

}
