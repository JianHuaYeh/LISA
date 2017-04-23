package expr_kmeans;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.*;
import java.util.*;
import comm.*;
/**
 *
 * @author Administrator
 */
public class KMeansClusteringSparse {
    private SparseMatrixDB data;
    private int dim;
    private int method;
    private double ratio;
    private int umax;
    private int umin;
    private int tmax;
    private int tmin;

    public static void main(String[] args) throws Exception {
        double r = Double.parseDouble(args[1]);
        int m = Integer.parseInt(args[2]);
        KMeansClusteringSparse kmc = new KMeansClusteringSparse(args[0], r, m);
        kmc.doClustering();
    }

    // find ranges for initial random k centers
    private double[][] findRanges() {
        double[][] ranges = new double[this.dim][];
        //int len = this.data[0].length;
        int len = this.data.colDim();
        System.err.println("FindRanges: "+this.dim+" ranges to find, "+
                "data length = "+len);
        for (int i=0; i<this.dim; i++) {
            double[] range = new double[2];
            range[0] = Double.MAX_VALUE;
            range[1] = Double.MIN_VALUE;
            //for (Iterator it=this.data.keySet().iterator(); it.hasNext(); ) {
            for (int j=0; j<len; j++) {
                //double freq = this.data[i][j];
                double freq = this.data.get(i, j);
                if (freq < range[0]) range[0]=freq;
                if (freq > range[1]) range[1]=freq;
            }
            ranges[i] = range;
        }
        return ranges;
    }

    private double distance(double[] vec0, double[] vec1, int method) {
        switch (method) {
            case 0: return comm.Similarity.eucledianSimilarity(vec0, vec1);
            case 1: return comm.Similarity.pearsonSimilarity(vec0, vec1);
            case 2:
            default: return comm.Similarity.cosineSimilarity(vec0, vec1);
        }
    }

    public void doClustering() {
        // ranges[this.dim][2]
        double[][] ranges = findRanges();
        // clusters[k][this.dim]
        int k = (int)Math.round(this.dim/this.ratio);
        //int len = this.data[0].length;
        //int len = this.data.colDim();
        int len = this.dim;
        System.err.println("Cluster into "+k+" groups.");
        //double[][] clusters = new double[k][len];
        SparseMatrixDB clusters = new SparseMatrixDB(k, len);
        // random initial centroids
        for (int i=0; i<k; i++) {
            for (int j=0; j<len; j++) {
                double min = ranges[j][0];
                double max = ranges[j][1];
                //clusters[i][j] = min+(max-min)*Math.random();
                clusters.put(i, j, min+(max-min)*Math.random());
            }
        }

        ArrayList lastmatches = new ArrayList();
        for (int t=0; t<100; t++) { //????
            ArrayList bestmatches = new ArrayList();
            for (int i=0; i<k; i++) bestmatches.add(new HashSet());

            //for (int j=0; j<this.data.length; j++) {
            for (int j=0; j<this.data.rowDim(); j++) {
                //double[] freqs = this.data[j];
                double[] freqs = this.data.getRow(j);

                int bestmatch = 0;
                double d = Double.MAX_VALUE;
                for (int i=0; i<k; i++) {
                    //double dist = distance(clusters[i], freqs, method);
                    double dist = distance(clusters.getRow(i), freqs, method);
                    if (d > dist) { d = dist; bestmatch = i; }
                }
                HashSet set = (HashSet)bestmatches.get(bestmatch);
                set.add(j);
            }

            if (bestmatches.equals(lastmatches)) break;
            lastmatches = bestmatches;

            for (int i=0; i<k; i++) {
                HashSet set = (HashSet)bestmatches.get(i);
                int count=0;
                double[] row = new double[len];
                for (Iterator it=set.iterator(); it.hasNext(); ) {
                    int which = (Integer)it.next();
                    //double[] freqs = this.data[which];
                    double[] freqs = this.data.getRow(which);
                    for (int j=0; j<len; j++) {
                        row[j] += freqs[j];
                    }
                    count++;
                }
                for (int j=0; j<row.length; j++) {
                    if (count > 0) {
                        row[j] /= count;
                    }
                }
                //clusters[i] = row;
                clusters.setRow(i, row);
            }
        }

        // beginning output in transposed (back) order, with class label
        //double[][] clusters = new double[k][this.dim];
        /*int rows = clusters[0].length;
        int cols = clusters.length;
        
        for (int i=0; i<rows; i++) {
            String result = "";
            for (int j=0; j<cols; j++) {
                result += " "+clusters[j][i];
            }
            System.out.println(result.trim());
        }*/
        // save cluster result
        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream("cluster.obj"));
            oos.writeObject(clusters);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public KMeansClusteringSparse(String s, double r, int m) {
        this.ratio = r;
        this.method = m;
        comm.LoadSparseDataDB ld = new comm.LoadSparseDataDB();
        this.data = ld.loadRawTransposed(s);
        if (this.data == null){
            System.err.println("Data loading error.");
            System.exit(0);
        }
        this.umax = ld.umax;    this.umin = ld.umin;
        this.tmax = ld.tmax;    this.tmin = ld.tmin;

        this.dim = this.tmax - this.tmin + 1;
    }

}
