/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package expr_randsel;

/**
 *
 * @author jhyeh
 */
public class RandomSelection {
    private double[][] data;
    private double ratio;

    public static void main(String[] args) {
        double r = Double.parseDouble(args[1]);
        RandomSelection pd = new RandomSelection(args[0], r);
        pd.go();
    }

    public RandomSelection(String s, double r) {
        comm.LoadData ld = new comm.LoadData();
        this.data = ld.loadRaw(s);
        if (this.data == null){
            System.err.println("Data loading error.");
            System.exit(0);
        }
        this.ratio = r;
    }

    public void swapColumn(int col1, int col2) {
        int rows = this.data.length;
        for (int i=0; i<rows; i++) {
            double tmp = this.data[i][col1];
            this.data[i][col1] = this.data[i][col2];
            this.data[i][col2] = tmp;
        }
    }

    public void doShuffle() {
        // randomly swap columns
        int cols = this.data[0].length;
        int rounds = cols*2;
        for (int i=0; i<rounds; i++) {
            int col1 = (int)(Math.random()*cols);
            int col2 = (int)(Math.random()*cols);
            if (col1 == col2) continue;
            // do column swapping
            swapColumn(col1, col2);
        }
    }

    public void go() {
        // just output the matrix
        int rows = this.data.length;
        int cols = this.data[0].length;
        int k = (int)Math.round(cols/this.ratio);
        doShuffle();
        for (int i=0; i<rows; i++) {
            StringBuilder result = new StringBuilder();
            for (int j=0; j<k; j++)
                result.append(data[i][j]).append(" ");
            System.out.println(result.toString().trim());
        }
    }
        
}
