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
public class CalcRecommendation {
    private String training;
    private String testing;
    private int method;
    private double[][] rawdata, data;
    private int umax;
    private int umin;
    private int tmax;
    private int tmin;
    
    // just do weighted average
    public static void main(String[] args) {
        int m = Integer.parseInt(args[3]);
        CalcRecommendation nr = new CalcRecommendation(args[0], args[1],
                args[2], m);
        nr.go();
    }

    public CalcRecommendation(String raw, String tr, String ts, int m) {
        this.training = tr;
        this.testing = ts;
        this.method = m;

        comm.LoadData ld = new comm.LoadData();
        this.rawdata = ld.loadRaw(raw);
        if (this.rawdata == null) {
            System.err.println("Raw data loading failed.");
            System.exit(0);
        }
        this.tmax = ld.tmax;    this.tmin = ld.tmin;
        this.umax = ld.umax;    this.umin = ld.umin;

        this.data = ld.loadMatrix(training);
        if (this.data == null) {
            System.err.println("Data loading failed.");
            System.exit(0);
        }
    }

    public void go() {
        try {
            BufferedReader br = new BufferedReader(
                                    new FileReader(this.testing));
            String line="";
            while ((line=br.readLine()) != null) {
                // find umax, umin, tmax, tmin (user & item)
                StringTokenizer st = new StringTokenizer(line);
                int uid = Integer.parseInt(st.nextToken());
                int tid = Integer.parseInt(st.nextToken());
                int ans = Integer.parseInt(st.nextToken());
                double predict = doPredict(uid, tid);
                System.out.println(uid+" "+tid+" "+ans+" "+predict);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private double similarity(double[] vec0, double[] vec1, int method) {
        switch (method) {
            case 0: return comm.Similarity.eucledianSimilarity(vec0, vec1);
            case 1: return comm.Similarity.pearsonSimilarity(vec0, vec1);
            case 2:
            default: return comm.Similarity.cosineSimilarity(vec0, vec1);
        }
    }

    public double doPredict(int uid, int tid) {
        int rows = this.data.length;
        int cols = this.data[0].length;
        double simSum = 0.0;
        double total = 0.0;
        for (int i=0; i<rows; i++) {
            int uid2 = umin+i;
            if (uid == uid2) continue;

            double[] vec0 = this.data[uid-umin];
            double[] vec1 = this.data[i]; // i = uid2-umin

            double rating = 0.0;
            try {
                rating = this.rawdata[i][tid-tmin];
            } catch (Exception e) {
                //System.err.println("Error fetching raw data["+i+"]["+
                //        (tid-tmin)+"]: tid="+tid+", tmin="+tmin);
                rating = 0.0;
            }
            if (rating == 0) continue;
            
            double sim = similarity(vec0, vec1, this.method);
            if (sim <= 0) continue;

            total += sim*rating;
            simSum += sim;
        }
        double score = total/simSum;
        if (Double.isNaN(score)) score = 0.0;
        return score;
    }
}
