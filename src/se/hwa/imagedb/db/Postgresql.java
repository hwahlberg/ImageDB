/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.hwa.imagedb.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.Logger;
import se.hwa.imagedb.main.Constants;
import se.hwa.imagedb.main.ImageObject;

/**
 *
 * @author bbnthwa
 */
public class Postgresql {
        private Connection con = null;
    private boolean writeflag = false;
    private int dbError;
    private static final Logger LOG = Logger.getLogger(Postgresql.class.getSimpleName());

    public Postgresql() {
        writeflag = false;
        dbError = Constants.DB_OK;
    }

    /**
     *
     * @param writeflag
     */
    public void setWriteflag(boolean writeflag) {
        this.writeflag = writeflag;
    }

    public void open(String host, String database, String user, String password) {
        String url = "jdbc:postgresql://" + host + "/" + database;
        Statement st = null;
        ResultSet rs = null;

        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            rs = st.executeQuery("SELECT VERSION()");

            if (rs.next()) {
                System.out.println(rs.getString(1));
            }

            // no autocommit
            con.setAutoCommit(false);

        } catch (SQLException ex) {
            LOG.error(ex.getMessage());
            dbError = Constants.DB_NOTOPEN;

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }

            } catch (SQLException ex) {
                LOG.error(ex.getMessage());
            }
        }

    }

    /**
     *
     * @return
     */
    public int getDbError() {
        return dbError;
    }

    /**
     *
     * Wrapper for insert
     *
     * @param sql
     * @return true if success
     */
    public int insert(String sql) {

        LOG.trace(sql);

        if (writeflag) {
            try {
                doInsert(sql);
            } catch (SQLException ex) {
                try {
                    con.rollback();
                } catch (Exception e) {
                }

                return parseError(ex);
            }
        }

        return Constants.DB_OK;
    }
    
    
    /**
     * 
     * @param img
     * @return 
     */
    public int writeDb(ImageObject img) {
        return Constants.DB_OK;
    }
    
    
    

    /**
     * Execute insert statement
     *
     * @param sql
     * @throws SQLException
     */
    private void doInsert(String sql) throws SQLException {
        Statement stmt = null;

        stmt = con.createStatement();
        stmt.executeUpdate(sql);
        con.commit();
        if (stmt != null) {
            stmt.close();
        }

    }

    /**
     *
     * @param ex
     */
    private int parseError(SQLException ex) {
        if (ex.getMessage().contains("violates unique constraint")) {
            dbError = Constants.DB_DUPLICATE;
        } else if (ex.getMessage().contains("current transaction is aborted")) {
            dbError = Constants.DB_TRANSABORT;
        } else {
            LOG.warn(ex.getMessage());
            dbError = Constants.DB_UNKNOWN;
        }

        return dbError;
    }

    
}
