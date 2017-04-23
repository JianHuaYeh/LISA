/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package expr_basic;

import java.io.*;
import java.util.*;
import comm.*;
/**
 *
 * @author jhyeh
 */
public class WeightedAvgOnly {
    private String trfname;
    private String tsfname;
    private int method;
    
    public class ASObject implements Comparable, java.io.Serializable {
        public int label;
        public double sim;

        public ASObject(int s, double d) {
            this.label = s;
            this.sim = d;
        }

        public int compareTo(Object obj) {
            ASObject other = (ASObject)obj;
            if (this.sim > other.sim) return -1;
            else if (this.sim < other.sim) return 1;
            else return 0;
        }
    }

    public static void main(String[] args) {
        int method = Integer.parseInt(args[2]);
        WeightedAvgOnly wao = new WeightedAvgOnly(args[0], args[1], method);
        wao.go();
    }
    
    public WeightedAvgOnly(String s1, String s2, String s3) {
        int method = Integer.parseInt(s3);
        init(s1, s2, method);
    }
    
    public WeightedAvgOnly(String s1, String s2, int i) {
        init(s1, s2, i);
    }
    
    public void init(String s1, String s2, int i) {
        this.trfname = s1;
        this.tsfname = s2;
        this.method = i;
    }
    
    public double calcSimilarity(double[] vec0, double[] vec1, int m) {
        if ((vec0==null) || (vec1==null)) return 0.0;
        double sim = 0.0;
        switch (m) {
            case 0: sim = Similarity.eucledianSimilarity(vec0, vec1); break;
            case 1: sim = Similarity.pearsonSimilarity(vec0, vec1); break;
            case 2:
            default: sim = Similarity.cosineSimilarity(vec0, vec1); break;
        }
        return sim;
    }
    
    public ArrayList getSimilarityList(int uid, LoadSparseData lsd, 
            SparseMatrix sm, int method) {
        if (uid > lsd.umax) return null;
        ArrayList al = new ArrayList();
        int row = uid-lsd.umin;
        double[] data = sm.getRow(row);
        //System.err.println("data len="+data.length);
        if (data == null) return null; // no uid data
        
        for (int i=lsd.umin; i<=lsd.umax; i++) {
            if (i==uid) continue;
            int row2 = i-lsd.umin;
            double[] data2 = sm.getRow(row2);
            //System.err.println("data2 len="+data2.length);
            if (data2 == null) continue;
            double sim = calcSimilarity(data, data2, method);
            if (sim == 0.0) continue;
            //System.err.println("getSimilarityList: calc sim between "+
            //        uid+" and "+i+": sim="+sim);
            al.add(new ASObject(i, sim));
        }
        return al;
    }
    
    public double calcWeightedAverage(int mid, ArrayList al, LoadSparseData lsd, 
            SparseMatrix sm) {
        double defaultGuess = 5.0;
        if (mid > lsd.tmax) return defaultGuess;
        double sum = 0.0;
        double simSum = 0.0;
        for (Iterator it=al.iterator(); it.hasNext(); ) {
            ASObject obj = (ASObject)it.next();
            if (obj.label > lsd.umax) continue;
            int row = obj.label-lsd.umin;
            int col = mid-lsd.tmin;
            //double[] data = sm.getRow(row);
            //double rating = data[col];
            double rating = sm.get(row, col);
            if (rating > 0.0) {
                sum += rating*obj.sim;
                simSum += obj.sim;
            }
        }
        double guess = (simSum==0.0)?defaultGuess:sum/simSum;
        return guess;
    }
    
    public void go() {
        LoadSparseData lsd = new LoadSparseData();
        SparseMatrix sm = lsd.loadRaw(this.trfname);
        // user range: lsd.umax, lsd.umin
        // movie range: lsd.tmax, lsd.tmin
        try {
            BufferedReader br = new BufferedReader(new FileReader(this.tsfname));
            String line="";
            double sum = 0.0;
            int count = 0;
            while ((line=br.readLine()) != null) {
                if (count%10000==0) {
                    System.err.println(count+" records. Memory free: "+
                            Runtime.getRuntime().freeMemory()+" bytes.");
                    //System.gc();
                }
                //System.err.println(count);
                // data format:
                // 1	6	5	887431973
                // uid mid rating timestamp
                StringTokenizer st = new StringTokenizer(line);
                int uid = Integer.parseInt(st.nextToken());
                if (uid > lsd.umax) continue;
                int mid = Integer.parseInt(st.nextToken());
                if (mid > lsd.tmax) continue;
                int r = Integer.parseInt(st.nextToken());
                //int row = uid-lsd.umin;
                //int col = mid-lsd.tmin;
                ArrayList al = getSimilarityList(uid, lsd, sm, this.method);
                if (al==null) continue;
                double guess = calcWeightedAverage(mid, al, lsd, sm);
                //System.err.println(count+": ans="+r+", guess="+guess);
                sum += (guess-r)*(guess-r);
                count++;                
            }
            br.close();
            double rmse = (count!=0)?Math.sqrt(sum/count):-1.0;
            System.out.println(rmse+" RMSE under method "+this.method+
                    "(0: Eucledian, 1:Pearson, 2:cosine)");
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
    
}
