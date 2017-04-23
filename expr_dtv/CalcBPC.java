/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package expr_dtv;

import java.util.*;
import java.io.*;
import Jama.*;
/**
 *
 * @author jhyeh
 */
public class CalcBPC {
    private double threshold;
    private double alpha;
    private double beta;
    private int dim;
    private double[][] M;
    private double[] bpc;
    private String bpcfname;
    private ArrayList ulist;
    private static double THRESHOLD = 1.0E-5;
    private static int MAX_ITERATION = 1000;

    public static void main(String[] args) {
        CalcBPC bpc = null;
        if (args.length == 1)
            bpc = new CalcBPC(args[0]);
        else if (args.length == 2)
            bpc = new CalcBPC(args[0], args[1]);
        else
            bpc = new CalcBPC(args[0], args[1], args[2], args[3]);
        try {
            bpc.doCalc();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public CalcBPC(String str, String str2) {
        this(str, str2, THRESHOLD, 0.4);
    }

    public CalcBPC(String str) {
        this(str, null, THRESHOLD, 0.4);
    }

    public CalcBPC(String str, String str2, String str3, String str4) {
        this(str, str2, Double.parseDouble(str3), Double.parseDouble(str4));
    }

    public CalcBPC(String str, String str2, double t, double a) {
        this(str, str2, t, a, 0.6);
    }

    public CalcBPC(String str, String str2, double t, double a, double b) {
        this.threshold = t;
        this.alpha = a;
        this.dim = this.getDim(str);
        this.M = this.readMatrixData(str);
        this.ulist = this.readUserList(str2);
        this.bpcfname = str+".bpc.log";
        this.beta = b;
    }

    private ArrayList readUserList(String str) {
        ArrayList result = new ArrayList();
        for (int i=0; i<this.dim; i++)
            result.add(""+i);
        try {
            BufferedReader br = new BufferedReader(new FileReader(str));
            String line = "";
            while ((line=br.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line);
                int index = Integer.parseInt(st.nextToken().trim());
                String id = st.nextToken().trim();
                result.set(index, id);
                //System.err.println(index+"=>"+id);
            }
            br.close();
        } catch (Exception e) {
            System.err.println("Error reading user list, "+
            "use ordered number list instead(0,1,2,...).");
            result = new ArrayList();
            for (int i=0; i<this.dim; i++)
                result.add(""+i);
        }
        return result;
    }

    protected int getDim() { return this.dim; }
    protected double getThreshold() { return this.threshold; }
    protected double[][] getMatrix() { return this.M; }
    protected double getAlpha() { return this.alpha; }
    protected double getBeta() { return this.beta; }
    protected ArrayList getUList() { return this.ulist; }

    private int getDim(String str) {
        int dim = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(str));
            String line = "";
            while ((line=br.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line);
                int x = Integer.parseInt(st.nextToken());
                int y = Integer.parseInt(st.nextToken());
                if (x > dim) dim = x;
                if (y > dim) dim = y;
            }
            br.close();
            dim = dim + 1;
        } catch (FileNotFoundException e) {
            System.err.println("File not found: "+str);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        return dim;
    }

    protected double[][] readMatrixData(String fname) {
        double[][] ma = new double[dim][dim];
        for (int i=0; i<dim; i++)
            for (int j=0; j<dim; j++)
                ma[i][j] = 0.0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(fname));
            String line = "";
            //System.err.print("Loading matrix data");
            //int count = 0;
            while ((line=br.readLine()) != null) {
                //if (count++ % 100 == 0) System.err.print("#");
                if ("".equals(line.trim())) continue;
                StringTokenizer st = new StringTokenizer(line);
                int x = Integer.parseInt(st.nextToken());
                int y = Integer.parseInt(st.nextToken());
                double d = Double.parseDouble(st.nextToken());
                ma[x][y] = d;
            }
            //System.err.println("done.");
            br.close();
        } catch (Exception e) {
            //e.printStackTrace();
            System.err.println(e);
        }
        return ma;
    }

    public double[] getBPCVector() {
        return bpc;
    }

    protected double meanMatrix(double[] v) {
        double mean = 0.0;
        double sum = 0.0;
        for (int i = 0; i < v.length; i++) {
            sum += v[i];
        }
        mean = sum / v.length;
        return mean;
    }

    private Matrix powerEigenvalueMatrix(double[] d, int power) {
        int dim = d.length;
        double[][] result = new double[dim][dim];
        for (int i=0; i<dim; i++)
            for (int j=0; j<dim; j++) {
                if (i!=j) result[i][j] = 0.0;
                else // i==j
                    result[i][i] = Math.pow(d[i], power);
            }
        return new Matrix(result);
    }

    private double findMax(double[] d) {
        double max = Double.MIN_VALUE;
        for (int i=0; i<d.length; i++)
            if (d[i] > max) max = d[i];
        return max;
    }

    public void doCalc() throws Exception {
        Matrix ma = new Matrix(this.M);
        if (ma.det() == 0.0) {
            System.err.println("Singular matrix, exit.");
            return;
        }
        System.err.println("Calculating eigenvalues...");
        EigenvalueDecomposition ed = new EigenvalueDecomposition(ma);
        double[] d = ed.getRealEigenvalues();
        double ei = findMax(d);
        this.beta = Math.abs(1.0/(ei*1.1));
        // make diagnal matrix D
        //for (int i=0; i<d.length; i++) {
        //    System.err.println("Eigenvalue"+i+" = "+d[i]);
        //}
        //System.err.println();
        //System.err.println("Calculating eigenmatrix...");
        Matrix ev = ed.getV();
        //System.err.println("Calculating eigenmatrix inverse...");
        Matrix evi = ev.inverse();

        System.err.println("Major eigenvalue="+ei+
                ", set beta to 1/(lambda*1.1)="+this.beta);
        if (this.beta > 1.0) {
            System.err.println("Beta value will not converge in BPC, "+
                    "set to default value=0.6");
            this.beta = 0.6;
        }

        int power=2;
        double[] v = new double[dim];
        for (int n = 0; n < v.length; n++) v[n]=1.0;
        double mean=0.0, prev_mean=0.0;

        while (true) {
            //if (power%100==0) System.err.println("Power "+power);
            // calculate dp = D^power
            Matrix dp = this.powerEigenvalueMatrix(d, power);
            // calculate mp = ev*dp*evi
            Matrix mp = (ev.times(dp)).times(evi);
            // ...
            double B = Math.pow(beta, power-1);
            for (int i = 0; i < dim; i++) {
                double value = 0.0;
                for (int j = 0; j < dim; j++)
                    value += mp.get(i,j); //將matrix每一列加總，縮減成v向量
                v[i] += value*B; //將加總後的值，乘以B，並加到v矩陣中
            }
            prev_mean = mean;
            mean = meanMatrix(v);
            if (power != 2) {
                //double diff = Math.abs(mean-prev_mean);
                double diff = Math.abs(mean/prev_mean-1.0);
                //double diff = Math.abs(prev_mean-mean);
                if (power%10 == 2)
                    System.err.println(this.bpcfname+": power="+power+
                            ", diff="+diff+", threshold="+threshold);

                if (diff<threshold || power>MAX_ITERATION) {
                    System.err.println(this.bpcfname+": power="+power+
                            ", diff="+diff+", threshold="+threshold);
                    break;
                }
            }
            power++;
        }
        try {
            //long time = System.currentTimeMillis();
            //this.bpcfname = ""+time+".bpc.log";
            PrintWriter pw = new PrintWriter(new FileOutputStream(bpcfname));
            for (int k = 0; k < v.length; k++) {
                String uid = (String)this.ulist.get(k);
                pw.println(""+uid+" "+v[k]*alpha);
            }
            pw.close();
        } catch (Exception e) {
            //e.printStackTrace();
            System.err.println(e);
        }
    }

    public String getBPCFileName() {
        return this.bpcfname;
    }

}
