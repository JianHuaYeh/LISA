/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package comm;

import java.io.*;
import java.util.*;
/**
 *
 * @author jhyeh
 */
public class SparseMatrix implements Serializable {
    private HashMap data;
    private int x;
    private int y;

    public SparseMatrix(int i1, int i2) {
        this.x = i1;
        this.y = i2;
        this.data = new HashMap();
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
        HashMap rowData = (HashMap)this.data.get(i1);
        if (rowData == null) {
            rowData = new HashMap();
            this.data.put(i1, rowData);
        }
        rowData.put(i2, val);
    }

    public void deleteCell(int i1, int i2) {
        // zero the cell
        HashMap rowData = (HashMap)this.data.get(i1);
        if (rowData == null) return;
        rowData.remove(i2);
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
        HashMap rowData = (HashMap)this.data.get(i1);
        if (rowData == null) return 0.0;
        Double dd = (Double)rowData.get(i2);
        if (dd == null) return 0.0;
        return dd.doubleValue();
    }

    public double[] getRow(int i1) {
        double[] result = new double[this.y];
        HashMap rowData = (HashMap)this.data.get(i1);
        if (rowData == null) return result;
        for (Iterator it=rowData.keySet().iterator(); it.hasNext(); ) {
            int i2 = (Integer)it.next();
            double val = (Double)rowData.get(i2);
            result[i2] = val;
        }
        return result;
    }

    public void setRow(int i1, double[] row) {
        HashMap rowData = new HashMap();
        for (int i=0; i<row.length; i++) {
            if (row[i] != 0) rowData.put(i, row[i]);
        }
        this.data.put(i1, rowData);
    }

    public void save(String s) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(s));
            oos.writeInt(x);
            oos.writeInt(y);
            oos.writeObject(this.data);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public void load(String s) throws Exception {
        try {
            ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(s));
            this.x = ois.readInt();
            this.y = ois.readInt();
            Object obj = ois.readObject();
            if ((obj != null) && (obj instanceof HashMap))
                this.data = (HashMap)obj;
            ois.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            this.data = null;
        }
        if (this.data == null) throw new Exception("Data format error.");
    }


}
