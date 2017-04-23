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
public class VSessionDump {
    private HashMap vsmap;
    private String prefix;

    public static void main(String[] args) {
        VSessionDump vd = new VSessionDump(args[0], args[1]);
        vd.doGenerate();
    }

    public VSessionDump(String s1, String s2) {
        this.vsmap = loadVSMap(s1);
        this.prefix = s2;
    }

    private HashMap loadVSMap(String fname) {
        HashMap result = new HashMap();
        try {
            ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(fname));
            result = (HashMap)ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return result;
    }

    public void doGenerate() {
        for (int i=0; i<this.vsmap.size(); i++) {
            String key = this.prefix+i+".txt";
            double[] vec = (double[])vsmap.get(key);
            if (vec == null) {
                System.out.println("Error fecthing topic vector for "+key);
                continue;
            }
            String result = "";
            for (int j=0; i<vec.length; j++)
                result += " "+vec[j];
            System.out.println(result);
        }
    }

}
