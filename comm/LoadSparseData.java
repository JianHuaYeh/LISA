/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package comm;

import java.util.*;
import java.io.*;
/**
 *
 * @author jhyeh
 */
public class LoadSparseData {
    public int umax, umin, tmax, tmin;
    
    // fetch two-dim raw data
    public SparseMatrix loadRaw(String fname) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(fname));
            String line="";
            umax = Integer.MIN_VALUE;
            umin = Integer.MAX_VALUE;
            tmax = Integer.MIN_VALUE;
            tmin = Integer.MAX_VALUE;
            while ((line=br.readLine()) != null) {
                // find umax, umin, tmax, tmin (user & item)
                StringTokenizer st = new StringTokenizer(line);
                int uid = Integer.parseInt(st.nextToken());
                int tid = Integer.parseInt(st.nextToken());
                if (uid > umax) umax = uid; if (uid < umin) umin = uid;
                if (tid > tmax) tmax = tid; if (tid < tmin) tmin = tid;
            }
            br.close();
            int rows = umax-umin+1;
            int cols = tmax-tmin+1;
            System.err.println("LoadData: total "+rows+" rows and "+cols+
                    " columns.");
            System.err.println("Uid max="+umax+", min="+umin);
            System.err.println("Tid max="+tmax+", min="+tmin);
            //double[][] data = new double[rows][cols];
            SparseMatrix data = new SparseMatrix(rows, cols);

            br = new BufferedReader(new FileReader(fname));
            line="";
            while ((line=br.readLine()) != null) {
                // find umax, umin, tmax, tmin (user & item)
                StringTokenizer st = new StringTokenizer(line);
                int uid = Integer.parseInt(st.nextToken());
                int tid = Integer.parseInt(st.nextToken());
                int r = Integer.parseInt(st.nextToken());
                int row = uid-umin;
                int col = tid-tmin;
                //data[row][col] = r;
                data.put(row, col, r);
            }
            br.close();
            return data;
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return null;
    }

    public SparseMatrix loadRawTransposed(String fname) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(fname));
            String line="";
            umax = Integer.MIN_VALUE;
            umin = Integer.MAX_VALUE;
            tmax = Integer.MIN_VALUE;
            tmin = Integer.MAX_VALUE;
            while ((line=br.readLine()) != null) {
                // find umax, umin, tmax, tmin (user & item)
                StringTokenizer st = new StringTokenizer(line);
                int uid = Integer.parseInt(st.nextToken());
                int tid = Integer.parseInt(st.nextToken());
                if (uid > umax) umax = uid; if (uid < umin) umin = uid;
                if (tid > tmax) tmax = tid; if (tid < tmin) tmin = tid;
            }
            br.close();
            int rows = umax-umin+1;
            int cols = tmax-tmin+1;
            System.err.println("LoadData: total "+rows+" rows and "+cols+
                    " columns. Transposed will be "+cols+"x"+rows+".");
            System.err.println("Uid max="+umax+", min="+umin);
            System.err.println("Tid max="+tmax+", min="+tmin);
            
            //double[][] data = new double[cols][rows];
            SparseMatrix data = new SparseMatrix(cols, rows);

            br = new BufferedReader(new FileReader(fname));
            line="";
            int count=0;
            while ((line=br.readLine()) != null) {
                if (++count%10000 == 0) System.gc();
                // find umax, umin, tmax, tmin (user & item)
                StringTokenizer st = new StringTokenizer(line);
                int uid = Integer.parseInt(st.nextToken());
                int tid = Integer.parseInt(st.nextToken());
                int r = Integer.parseInt(st.nextToken());
                int row = uid-umin;
                int col = tid-tmin;
                //data[row][col] = r;
                //data[col][row] = r;
                data.put(col, row, r);
            }
            br.close();
            return data;
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return null;
    }

    public SparseMatrix loadMatrix(String fname) {
        try {
            ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(fname));
            Object obj = ois.readObject();
            SparseMatrix mat = null;
            if ((obj != null) && (obj instanceof SparseMatrix))
                mat = (SparseMatrix)obj;
            ois.close();
            return mat;
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return null;
    }

}
