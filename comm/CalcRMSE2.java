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
public class CalcRMSE2 {
    private String qafile;

    public static void main(String[] args) {
        CalcRMSE2 rmse = new CalcRMSE2(args[0]);
        rmse.doCalc();
    }

    public CalcRMSE2(String str) {
        this.qafile = str;
    }

    public void doCalc() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(qafile));
            String line="";
            double sum=0.0, sum2=0.0;
            int count=0;
            int miss=0, miss2=0;
            // line format: uid, mid, anwer, guess
            while ((line=br.readLine()) != null) {
                //if (count%100==0) System.out.println("Question "+count);
                StringTokenizer st = new StringTokenizer(line, ",");
                String uid = st.nextToken().trim();
                String mid = st.nextToken().trim();
                double answer = Double.parseDouble(st.nextToken().trim());
                double lisa = Double.parseDouble(st.nextToken().trim());
                double sorp = Double.parseDouble(st.nextToken().trim());
                if (answer != 0) {
                    sum += (1.0-lisa/5.0)*(1.0-lisa/5.0);
                    sum2 += (1.0-sorp/5.0)*(1.0-sorp/5.0);
                }
                //sum += (answer-guess)*(answer-guess);
                if (answer-lisa >= 0.5) {
                    //System.out.println("Record miss: uid="+uid+", mid="+mid+
                    //        ", answer="+answer+", guess="+guess);
                    miss++;
                }
                if (answer-sorp >= 0.5) {
                    //System.out.println("Record miss: uid="+uid+", mid="+mid+
                    //        ", answer="+answer+", guess="+guess);
                    miss2++;
                }
                count++;
            }
            br.close();
            System.out.println("Question count = "+count);
            System.out.println("LISA prediction stat:");
            System.out.println("Error sum square = "+sum);
            double rmse = Math.sqrt(sum/count);
            System.out.println("RMSE = "+rmse);
            System.out.println(""+miss+" pairs missed(error > 0.5).");
            System.out.println("SORP prediction stat:");
            System.out.println("Error sum square = "+sum2);
            rmse = Math.sqrt(sum2/count);
            System.out.println("RMSE = "+rmse);
            System.out.println(""+miss2+" pairs missed(error > 0.5).");
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}
