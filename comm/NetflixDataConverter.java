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
public class NetflixDataConverter {
    private String dirname;

    public static void main(String[] args) {
        NetflixDataConverter conv = new NetflixDataConverter(args[0]);
        conv.go();
    }

    public NetflixDataConverter(String s) {
        this.dirname = s.endsWith("/")?s:s+"/";
    }

    private void doDump(String fname, String mid) throws Exception {
        BufferedReader br = new BufferedReader(
                new FileReader(this.dirname+fname));
        String line = "";
        while ((line=br.readLine()) != null) {
            StringTokenizer st = new StringTokenizer(line, ",");
            String uid = st.nextToken();
            String rate = st.nextToken();
            System.out.println(uid+" "+mid+" "+rate);
        }
        br.close();
    }

    private String getMid(String fname) {
        String patt = "mv2_";
        String patt2 = ".txt";
        int start = fname.indexOf(patt)+patt.length();
        int end = fname.indexOf(patt2);
        return fname.substring(start, end);
    }

    public void go() {
        try {
            File dir = new File(this.dirname);
            String[] flist = dir.list();
            for (String fname: flist) {
                String mid = this.getMid(fname);
                this.doDump(fname, mid);
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}
