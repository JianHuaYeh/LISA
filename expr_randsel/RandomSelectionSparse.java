/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package expr_randsel;

import java.util.*;
import comm.*;
/**
 *
 * @author jhyeh
 */
public class RandomSelectionSparse {
    private SparseMatrix data;
    private double ratio;
    private int umin, umax, tmin, tmax;

    public static void main(String[] args) {
        double r = Double.parseDouble(args[1]);
        RandomSelectionSparse pd = new RandomSelectionSparse(args[0], r);
        pd.go();
    }

    public RandomSelectionSparse(String s, double r) {
        comm.LoadSparseData ld = new comm.LoadSparseData();
        this.data = ld.loadRaw(s);
        if (this.data == null){
            System.err.println("Data loading error.");
            System.exit(0);
        }
        System.err.println("Raw data loaded.");
        this.umax = ld.umax;
        this.umin = ld.umin;
        this.tmax = ld.tmax;
        this.tmin = ld.tmin;
        
        this.ratio = r;
    }

    public void swapColumn(int col1, int col2) {
        //int rows = this.data.length;
        int rows = this.data.rowDim();
        for (int i=0; i<rows; i++) {
            //double v1 = this.data[i][col1];
            double v1 = this.data.get(i, col1);
            //this.data[i][col1] = this.data[i][col2];
            double v2 = this.data.get(i, col2);
            if (v2 != 0) this.data.put(i, col1, v2);
            //this.data[i][col2] = tmp;
            if (v1 != 0) this.data.put(i, col2, v1);
        }
    }

    /*public void doShuffle(int rounds) {
        // randomly swap columns
        //int cols = this.data[0].length;
        int cols = this.data.colDim();
        //int rounds = cols;
        for (int i=0; i<rounds; i++) {
            System.err.println("Shuffle round: "+(i+1)+"/"+rounds);
            int col1 = (int)(Math.random()*cols);
            int col2 = (int)(Math.random()*cols);
            if (col1 == col2) continue;
            // do column swapping
            swapColumn(col1, col2);
            System.gc();
        }
    }*/

    /*private void outputColumn(int newcol, int col) {
        int rows = this.data.rowDim();
        for (int i=0; i<rows; i++) {
            //double v1 = this.data[i][col1];
            int v1 = (int)this.data.get(i, col);
            if (v1 != 0)
                System.out.println(i+" "+newcol+" "+v1);
        }
    }*/
    
    private void outputColumn(int newcol) {
        int rows = this.data.rowDim();
        for (int i=0; i<rows; i++) {
            //double v1 = this.data[i][col1];
            int row = this.umin+i;
            int col = this.tmin+newcol;
            int v1 = (int)this.data.get(row, col);
            if (v1 != 0)
                System.out.println(row+" "+col+" "+v1);
        }
    }

    public void go() {
        // just output the matrix
        //int rows = this.data.length;
        int rows = this.data.rowDim();
        //int cols = this.data[0].length;
        int cols = this.data.colDim();
        int k = (int)Math.round(cols/this.ratio);
        System.err.println("Ratio="+this.ratio+", reduce column from "+cols+
                " to "+k);

        TreeSet colset = new TreeSet();
        //for (int i=0; i<k; i++) {
        //int i=0;
        while (colset.size() < k) {
            // we need k columns
            int col = (int)(Math.random()*cols);
            if (colset.contains(col)) continue;
            // print out this column
            colset.add(col);
            outputColumn(col);
            //i++;
        }
        
        //doShuffle(k);
        /*for (int i=0; i<rows; i++) {
            StringBuilder result = new StringBuilder();
            for (int j=0; j<k; j++)
                result.append(data[i][j]).append(" ");
            System.out.println(result.toString().trim());
        }*/
        //SparseMatrix result = new SparseMatrix(rows, k);
        /*for (int i=0; i<rows; i++)
            for (int j=0; j<k; j++)
                result.put(i, j, this.data.get(i, j));*/
        /*for (int i=0; i<rows; i++)
            for (int j=k; j<cols; j++) {
                System.err.println("Shrinking round: "+(j-k+1)+"/"+(cols-k));
                this.data.deleteCol(j);
                System.gc();
            }
        */
        
        /*try {
            ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream("cluster.obj"));
            oos.writeObject(result);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }*/
        //this.data.save("train.obj");
    }
        
}
