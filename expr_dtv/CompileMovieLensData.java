/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package expr_dtv;

import java.io.*;
import java.util.*;

/**
 *
 * @author jhyeh
 */
public class CompileMovieLensData {
    private String fname;
    private HashMap<String, HashMap> data;
    private String vspath;

    public static void main(String[] args) {
        CompileMovieLensData cd = new CompileMovieLensData(args[0], args[1]);
        cd.go();
    }

    public CompileMovieLensData(String s, String s2) {
        this.fname = s;
        this.data = new HashMap<String, HashMap>();
        this.vspath = s2.endsWith("/")?s2:s2+"/";
    }

    public void go() {
        // data read in
        try {
            BufferedReader br = new BufferedReader(new FileReader(this.fname));
            String line="";
            while ((line=br.readLine()) != null) {
                // sample data, tabbed separated
                // 1	22	4	875072404
                StringTokenizer st = new StringTokenizer(line);
                String uid = st.nextToken();
                String mid = st.nextToken();
                String rstr = st.nextToken();
                int rate = Integer.parseInt(rstr);
                // fetch local map
                HashMap<String, Integer> local = this.data.get(uid);
                if (local == null) {
                    local = new HashMap<String, Integer>();
                }
                local.put(mid, rate);
                this.data.put(uid, local);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        // begin to output
        for (Iterator<String> it=this.data.keySet().iterator(); it.hasNext(); ) {
            String uid = it.next();
            HashMap<String, Integer> local = this.data.get(uid);
            if (local != null)
                dumpVirtualSession(uid, local);
        }

        // save data
        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream("data.obj"));
            oos.writeObject(this.data);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dumpVirtualSession(String uid, HashMap<String, Integer> local) {
        String fname = this.vspath+uid+".txt";
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(fname));
            for (Iterator<String> it=local.keySet().iterator(); it.hasNext(); ) {
                String mid = it.next();
                int rate = local.get(mid);
                String outstr = "";
                for (int i=0; i<rate; i++)
                    outstr += "L"+mid+" ";
                pw.println(outstr.trim());
            }
            pw.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}
