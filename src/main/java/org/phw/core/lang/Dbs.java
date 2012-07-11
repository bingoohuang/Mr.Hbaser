package org.phw.core.lang;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * A collection of JDBC helper methods.  This class is thread safe.
 * Most of code are from apache commons-dbutils.
 * @author BingooHuang
 *
 */
public class Dbs {
    /**
     * Close a <code>Connection</code>, avoid closing if null and hide
     * any SQLExceptions that occur.
     *
     * @param conn Connection to close.
     */
    public static void closeQuietly(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e) {
            // quiet
        }
    }

    /**
     * Close a <code>Connection</code>, <code>Statement</code> and 
     * <code>ResultSet</code>.  Avoid closing if null and hide any 
     * SQLExceptions that occur.
     *
     * @param conn Connection to close.
     * @param stmt Statement to close.
     * @param rs ResultSet to close.
     */
    public static void closeQuietly(Connection conn, Statement stmt, ResultSet rs) {

        try {
            closeQuietly(rs);
        }
        finally {
            try {
                closeQuietly(stmt);
            }
            finally {
                closeQuietly(conn);
            }
        }

    }

    /**
     * Close a <code>ResultSet</code>, avoid closing if null and hide any
     * SQLExceptions that occur.
     *
     * @param rs ResultSet to close.
     */
    public static void closeQuietly(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e) {
            // quiet
        }
    }

    /**
     * Close a <code>Statement</code>, avoid closing if null and hide
     * any SQLExceptions that occur.
     *
     * @param stmt Statement to close.
     */
    public static void closeQuietly(Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
            }
        }
        catch (SQLException e) {
            // quiet
        }
    }

    /**
     * Loads and registers a database driver class.
     * If this succeeds, it returns true, else it returns false.
     *
     * @param driverClassName of driver to load
     * @return boolean <code>true</code> if the driver was found, otherwise <code>false</code>
     */
    public static boolean loadDriver(String driverClassName) {
        try {
            Class.forName(driverClassName).newInstance();
            return true;

        }
        catch (ClassNotFoundException e) {
            return false;

        }
        catch (IllegalAccessException e) {
            // Constructor is private, OK for DriverManager contract
            return true;

        }
        catch (InstantiationException e) {
            return false;

        }
        catch (Throwable e) {
            return false;
        }
    }
}
