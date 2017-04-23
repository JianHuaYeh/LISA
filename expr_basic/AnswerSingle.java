/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package expr_basic;

import java.io.*;
import java.util.*;
/**
 *
 * @author jhyeh
 */
public class AnswerSingle {
    private String fname;
    private int rating;
    
    public static void main(String[] args) {
        int i = Integer.parseInt(args[1]);
        AnswerSingle az = new AnswerSingle(args[0], i);
        az.go();
    }
    
    public AnswerSingle(String s, int i) {
        this.fname = s;
        this.rating = i;
    }
    
    public void go() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(this.fname));
            String line="";
            double sum = 0.0;
            int count = 0;
            while ((line=br.readLine()) != null) {
                // data format:
                // 1	6	5	887431973
                // uid mid rating timestamp
                StringTokenizer st = new StringTokenizer(line);
                st.nextToken();st.nextToken();
                int r = Integer.parseInt(st.nextToken());
                double r2 = this.rating;
                if (r2 < 0) r2 = Math.random()*4+1.0;
                sum += (r2-r)*(r2-r);
                count++;                
            }
            br.close();
            double rmse = (count!=0)?Math.sqrt(sum/count):-1.0;
            System.out.println(rmse+" RMSE with all rating set to "+this.rating+"(-1 means random answering)");
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
    
}
