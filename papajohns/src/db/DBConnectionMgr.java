package db;

/**
 * Copyright(c) 2001 iSavvix Corporation (http://www.isavvix.com/)
 *
 *                        All rights reserved
 *
 * Permission to use, copy, modify and distribute this material for
 * any purpose and without fee is hereby granted, provided that the
 * above copyright notice and this permission notice appear in all
 * copies, and that the name of iSavvix Corporation not be used in
 * advertising or publicity pertaining to this material without the
 * specific, prior written permission of an authorized representative of
 * iSavvix Corporation.
 *
 * ISAVVIX CORPORATION MAKES NO REPRESENTATIONS AND EXTENDS NO WARRANTIES,
 * EXPRESS OR IMPLIED, WITH RESPECT TO THE SOFTWARE, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR ANY PARTICULAR PURPOSE, AND THE WARRANTY AGAINST
 * INFRINGEMENT OF PATENTS OR OTHER INTELLECTUAL PROPERTY RIGHTS.  THE
 * SOFTWARE IS PROVIDED "AS IS", AND IN NO EVENT SHALL ISAVVIX CORPORATION OR
 * ANY OF ITS AFFILIATES BE LIABLE FOR ANY DAMAGES, INCLUDING ANY
 * LOST PROFITS OR OTHER INCIDENTAL OR CONSEQUENTIAL DAMAGES RELATING
 * TO THE SOFTWARE.
 *
 */


import java.sql.*;
import java.util.Properties;
import java.util.Vector;


/**
 * Manages a java.sql.Connection pool.
 *
 * @author  Anil Hemrajani
 */
public class DBConnectionMgr {

	
    private Vector connections = new Vector(10); //동시 접근x
    private String _driver = "org.mariadb.jdbc.Driver",
    _url = "jdbc:mariadb://127.0.0.1:8001/papajohns_server?useUnicode=true&characterEncoding=UTF-08",
    _user = "root",
    _password = "toor";
    
    private boolean _traceOn = false;
    private boolean initialized = false;
    private int _openConnections = 50;
    private static DBConnectionMgr instance = null;

    private DBConnectionMgr() { //싱글톤1
    }

    /** Use this method to set the maximum number of open connections before
     unused connections are closed.
     */

    public static DBConnectionMgr getInstance() { //싱글톤2
        if (instance == null) {
            synchronized (DBConnectionMgr.class) {
                if (instance == null) {
                    instance = new DBConnectionMgr();
                }
            }
        }

        return instance;
    }

    public void setOpenConnectionCount(int count) { //setter
        _openConnections = count;
    }


    public void setEnableTrace(boolean enable) { //setter
        _traceOn = enable;
    }


    /** Returns a Vector of java.sql.Connection objects */
    public Vector getConnectionList() { //getter
        return connections;
    }


    /** Opens specified "count" of connections and adds them to the existing pool */
    public synchronized void setInitOpenConnections(int count) //Initializing connections(Vector)
            throws SQLException {
        Connection c = null;
        ConnectionObject co = null;

        for (int i = 0; i < count; i++) {
            c = createConnection();
            co = new ConnectionObject(c, false); //Connection 객체생성

            connections.addElement(co); //add()
            trace("ConnectionPoolManager: Adding new DB connection to pool (" + connections.size() + ")");
        }
    }


    /** Returns a count of open connections */
    public int getConnectionCount() { //size()
        return connections.size();
    }


    /** Returns an unused existing or new connection.  */
    public synchronized Connection getConnection() //사용하는 getConnection()
            throws Exception {
        if (!initialized) {
            Class c = Class.forName(_driver); //Driver class (위에서 입력했던 경로의 클래스)
            DriverManager.registerDriver((Driver) c.newInstance()); //DriverManager

            initialized = true;
        }


        Connection c = null;
        ConnectionObject co = null;
        boolean badConnection = false;


        for (int i = 0; i < connections.size(); i++) {
            co = (ConnectionObject) connections.elementAt(i); //connection 불러오기

            // If connection is not in use, test to ensure it's still valid!
            if (!co.inUse) { //사용되지 않는 경우 여전히 사용되지 않는지 확인
                try {
                    badConnection = co.connection.isClosed(); //close됐는지 확인
                    if (!badConnection)
                        badConnection = (co.connection.getWarnings() != null);
                } catch (Exception e) {
                    badConnection = true;
                    e.printStackTrace();
                }

                // Connection is bad, remove from pool
                if (badConnection) {
                    connections.removeElementAt(i); //사용되지 않았다면 제거
                    trace("ConnectionPoolManager: Remove disconnected DB connection #" + i);
                    continue;
                }

                c = co.connection;
                co.inUse = true;

                trace("ConnectionPoolManager: Using existing DB connection #" + (i + 1));
                break;
            }
        }

        if (c == null) { //만약 객체가 없다면 생성 (맨 처음엔 항상 없음)
            c = createConnection();
            co = new ConnectionObject(c, true);
            connections.addElement(co); //Vector에 추가

            trace("ConnectionPoolManager: Creating new DB connection #" + connections.size());
        }

        return c;
    }


    /** Marks a flag in the ConnectionObject to indicate this connection is no longer in use */
    public synchronized void freeConnection(Connection c) { //connection 반환
        if (c == null)
            return;

        ConnectionObject co = null;

        for (int i = 0; i < connections.size(); i++) { 
            co = (ConnectionObject) connections.elementAt(i);
            if (c == co.connection) {
                co.inUse = false;
                break;
            }
        }

        for (int i = 0; i < connections.size(); i++) {
            co = (ConnectionObject) connections.elementAt(i);
            if ((i + 1) > _openConnections && !co.inUse)
                removeConnection(co.connection);
        }
    }

    public void freeConnection(Connection c, PreparedStatement p, ResultSet r) { //overload
        try {
            if (r != null) r.close();
            if (p != null) p.close();
            freeConnection(c);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void freeConnection(Connection c, Statement s, ResultSet r) { //overload
        try {
            if (r != null) r.close();
            if (s != null) s.close();
            freeConnection(c);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void freeConnection(Connection c, PreparedStatement p) { //overload
        try {
            if (p != null) p.close();
            freeConnection(c);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void freeConnection(Connection c, Statement s) { //overload
        try {
            if (s != null) s.close();
            freeConnection(c);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /** Marks a flag in the ConnectionObject to indicate this connection is no longer in use */
    public synchronized void removeConnection(Connection c) {
        if (c == null)
            return;

        ConnectionObject co = null;
        for (int i = 0; i < connections.size(); i++) {
            co = (ConnectionObject) connections.elementAt(i);
            if (c == co.connection) { //삭제하려는 객체 찾아서
                try {
                    c.close(); //반환후
                    connections.removeElementAt(i); //삭제
                    trace("Removed " + c.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            }
        }
    }


    private Connection createConnection() //Connection 생성
            throws SQLException {
        Connection con = null;

        try {
            if (_user == null)
                _user = "";
            if (_password == null)
                _password = "";

            Properties props = new Properties(); //HashTable을 상속
            props.put("user", _user);
            props.put("password", _password);

            con = DriverManager.getConnection(_url, props); //DriverManager
        } catch (Throwable t) {
            throw new SQLException(t.getMessage());
        }

        return con;
    }


    /** Closes all connections and clears out the connection pool */
    public void releaseFreeConnections() {
        trace("ConnectionPoolManager.releaseFreeConnections()");

        Connection c = null;
        ConnectionObject co = null;

        for (int i = 0; i < connections.size(); i++) { //connections에서
            co = (ConnectionObject) connections.elementAt(i);
            if (!co.inUse) //사용중이지 않다면
                removeConnection(co.connection); //삭제
        }
    }


    /** Closes all connections and clears out the connection pool */
    public void finalize() { //flush
        trace("ConnectionPoolManager.finalize()");

        Connection c = null;
        ConnectionObject co = null;

        for (int i = 0; i < connections.size(); i++) {
            co = (ConnectionObject) connections.elementAt(i);
            try {
                co.connection.close(); //반환
            } catch (Exception e) {
                e.printStackTrace();
            }

            co = null; // set null
        }

        connections.removeAllElements(); //삭제
    }


    private void trace(String s) {
        if (_traceOn)
            System.err.println(s); //출력
    }

}


class ConnectionObject {
    public java.sql.Connection connection = null; //Connection
    public boolean inUse = false; //inUse Flag

    public ConnectionObject(Connection c, boolean useFlag) { //allArgumentConstructor
        connection = c;
        inUse = useFlag;
    }
}
