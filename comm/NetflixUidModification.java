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
public class NetflixUidModification {
    private String fname;
    private TreeSet uset;

    public static void main(String[] args) {
        NetflixUidModification um = new NetflixUidModification(args[0]);
        um.go();
    }

    public NetflixUidModification(String s) {
        this.fname = s;
        this.uset = new TreeSet();
    }

    public void go() {
        try {
            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;
            BufferedReader br = new BufferedReader(new FileReader(this.fname));
            String line="";
            int count=0;
            while ((line=br.readLine()) != null) {
                if (++count % 1000000 == 0) System.err.println(count);
                StringTokenizer st = new StringTokenizer(line);
                String uidstr = st.nextToken();
                this.uset.add(uidstr);
                int uid = Integer.parseInt(uidstr);
                if (uid < min) min = uid;
                if (uid > max) max = uid;
            }
            br.close();

            System.err.println("Total "+uset.size()+" distinct users.");
            System.err.println("Max user id = "+max);
            System.err.println("Min user id = "+min);
            System.err.println("Saving uid set into hash...");
            count=0;
            HashMap umap = new HashMap();
            for (Iterator it=uset.iterator(); it.hasNext(); ) {
                String uidstr = (String)it.next();
                umap.put(uidstr, ""+count);
                count++;
            }
            ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream("uidhash.map"));
            oos.writeObject(umap);
            oos.close();

            System.err.println("Modifying uid...");
            br = new BufferedReader(new FileReader(this.fname));
            line="";
            while ((line=br.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line);
                String uidstr = st.nextToken();
                uidstr = (String)umap.get(uidstr);
                String midstr = st.nextToken();
                String rstr = st.nextToken();
                System.out.println(uidstr+" "+Integer.parseInt(midstr)+" "+rstr);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }


    }

}
