/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package comm;

import java.sql.*;
/**
 *
 * @author jhyeh
 */
public class SparseMatrixDB {
    private Connection _conn=null;
    private String tableName;
    private int x;
    private int y;

    public SparseMatrixDB(int i1, int i2) {
        this.x = i1;
        this.y = i2;
        this.tableName = "sm"+System.currentTimeMillis();
        this.createTable(tableName);
    }

    public Connection getConnection() {
        if (_conn != null) return _conn;
        try {
            Class.forName("org.hsqldb.jdbcDriver");
            _conn = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/");
        } catch (Exception e) {
            System.err.println(e);
            _conn = null;
        }
        return _conn;
    }

    public boolean execute(String sqlstr) {
        boolean result = false;
        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            stmt.execute(sqlstr);
            result = true;
        } catch (Exception e) {
            System.err.println(e);
            result = false;
        }
        return result;
    }

    public ResultSet executeQuery(String sqlstr) {
        ResultSet rs = null;
        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlstr);
        } catch (Exception e) {
            System.err.println(e);
            rs = null;
        }
        return rs;
    }

    private void createTable(String tname) {
        String sqlstr = "create table "+tname+" (row int, col int, val double)";
        boolean result = execute(sqlstr);
        if (result) {
            System.err.println("Table created: "+tname);
            System.err.println(sqlstr);
        }
    }

    private void dropTable(String tname) {
        String sqlstr = "drop table "+tname;
        boolean result = execute(sqlstr);
        if (result) {
            System.err.println("Table dropped: "+tname);
            System.err.println(sqlstr);
        }
    }

    private void close() {
        this.dropTable(this.tableName);
    }

    public int rowDim() { return this.x; }
    public int colDim() { return this.y; }

    public void set(int i1, int i2, double val) {
        this.put(i1, i2, val);
    }
    public void put(int i1, int i2, double val) {
        if (val == 0.0) {
            this.deleteCell(i1, i2);
            return;
        }
        else this.deleteCell(i1, i2);
        // put (i1, i2, val) into db table
        String sqlstr = "insert into "+this.tableName+" set val="+val+
                " where row="+i1+" and col="+i2;
        boolean result = execute(sqlstr);
        if (!result) {
            System.err.println("Cell set failed: "+this.tableName);
            System.err.println(sqlstr);
        }
    }

    public void deleteCell(int i1, int i2) {
        // delete table cell data (i1, i2)
        String sqlstr = "delete from "+this.tableName+" where row="+i1+" and "+
                "col="+i2;
        boolean result = execute(sqlstr);
        if (!result) {
            System.err.println("Cell delete failed: "+this.tableName);
            System.err.println(sqlstr);
        }
    }

    public void deleteCol(int col) {
        for (int i=0; i<this.x; i++) {
            this.deleteCell(i, col);
            for (int j=col+1; i<this.y; j++)
                if (j<this.y) this.set(i, j-1, this.get(i, j));
        }
        this.y--;
    }
    
    public double get(int i1, int i2) {
        // get table cell data (i1, i2)
        String sqlstr = "select val from "+this.tableName+" where row="+i1+
                " and col="+i2;
        ResultSet rs = executeQuery(sqlstr);
        if (rs == null) return 0.0;
        try {
            if (rs.next()) {
                double val = rs.getDouble("val");
                return val;
            }
        } catch (Exception e) {}
        return 0.0;
    }

    public double[] getRow(int i1) {
        double[] result = new double[this.y];
        // get table cell data (i1, *)
        for (int i=0; i<this.y; i++)
            result[i] = this.get(i1, i);
        return result;
    }

    public void setRow(int i1, double[] row) {
        // set table cell data (i1, *)
        for (int i=0; i<row.length; i++)
            this.set(i1, i, row[i]);
    }
}
