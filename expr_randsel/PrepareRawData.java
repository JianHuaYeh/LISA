/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package expr_randsel;

/**
 *
 * @author jhyeh
 */
public class PrepareRawData {
    private double[][] data;

    public static void main(String[] args) {
        PrepareRawData pd = new PrepareRawData(args[0]);
        pd.go();
    }

    public PrepareRawData(String s) {
        comm.LoadData ld = new comm.LoadData();
        this.data = ld.loadRaw(s);
        if (this.data == null){
            System.err.println("Data loading error.");
            System.exit(0);
        }
    }

    public void go() {
        // just output the matrix
        int rows = this.data.length;
        int cols = this.data[0].length;
        StringBuilder result = new StringBuilder();
        for (int i=0; i<rows; i++)
            for (int j=0; j<cols; j++)
                result.append(data[i][j]).append(" ");
        System.out.println(result.toString().trim());
    }
        
}
