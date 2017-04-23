/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package expr_fcm;

import java.util.*;
/**
 *
 * @author jhyeh
 */
public class FCMClustering {
    private double[][] data;
    private double[][] weight;
    private double[][] centers;
    private int dim;
    private int k;
    private int cols;
    private int method;
    private double ratio;
    private int umax;
    private int umin;
    private int tmax;
    private int tmin;

    public static void main(String[] args) throws Exception {
        double r = Double.parseDouble(args[1]);
        int m = Integer.parseInt(args[2]);
        FCMClustering fcm = new FCMClustering(args[0], r, m);
        fcm.doClustering();
        fcm.closestHard();
    }

    private double distance(double[] vec0, double[] vec1, int method) {
        switch (method) {
            case 0: return comm.Similarity.eucledianSimilarity(vec0, vec1);
            case 1: return comm.Similarity.pearsonSimilarity(vec0, vec1);
            case 2:
            default: return comm.Similarity.cosineSimilarity(vec0, vec1);
        }
    }

    public void doClustering() {
        double[] fcm=new double[2];
        double[] amplitude={1.0,2.0};
        fcm[0]=Double.MAX_VALUE;
        int stable=0;
        //this.k = (int)Math.round((1/ratio)*this.cols);
        this.k = (int)Math.round(this.dim/this.ratio);
        //System.err.println("k = "+k);
        double time=System.currentTimeMillis();

        //System.err.println("FCM init");
        this.init();
        if(Double.isNaN(weight[0][0])) System.err.println("init");
        if(Double.isNaN(weight[0][0])) System.err.print("init weight is NaN");
        //System.err.println("method"+this.method);
        for(int t=0; t<120; t++){

            //System.err.println("("+t+")FCM centers");
            this.centers();

            //System.err.println("("+t+")Calculate JFCM");
            fcm[1]=this.JFCM();
            if((amplitude[0]=Math.abs(fcm[0]-fcm[1]))>0.025){

                if(Math.abs(amplitude[0])==Math.abs(amplitude[1]))stable++;

                amplitude[1]=amplitude[0];
                if(stable>10){
                    System.err.println("("+t+")amplitude stable");
                    break;
                }
                //System.err.print("\n"+(fcm[0]-fcm[1])+"\t");
                fcm[0]=fcm[1];
                //System.err.println("("+t+")reset weight");
                this.resetWeight();
                if(Double.isNaN(weight[0][0]))
                    System.err.print("weight is NaN");
                //System.err.println((System.currentTimeMillis()-time)+","+stable+","+amplitude[0]+","+amplitude[1]);
            }
            else{
                System.err.println("("+t+")FCM break\tFCM:"+amplitude[0]);
                break;
            }
        }

        /*
          for(int i=0;i<weight.length;i++){
             System.err.print(weight[i][0]);
            for(int j=1;j<weight[0].length;j++){
                System.err.print(","+weight[i][j]);
            }
            System.err.println();
        }
         */
      if(Double.isNaN(weight[0][0]))
                System.err.print("*");
    }

    //random weight[label.length(data numbers)][k]
    private void init(){
        weight = new double[this.cols][this.k];
        double one;
        for(int i=0;i<this.cols;i++){
            one=1;
            for(int j=0;j<this.k-1;j++){
                weight[i][j]=Math.random()%one;
                one-=weight[i][j];
            }
            weight[i][this.k-1]=one;
        }
    }

    private void centers(){
        System.err.println("centers dim: "+k+"x"+this.dim);
        centers = new double[k][this.dim];
        double[] sum1,sum2;
        double temp;
        for(int i=0; i<k; i++){
            sum1=new double[this.dim];
            sum2=new double[this.dim];
            for(int j=0;j<this.cols;j++){
                temp=Math.pow(weight[j][i],2.0);
                for(int l=0;l<this.dim;l++){
                    //System.err.println("(i, j, l) = ("+i+","+j+","+l+")");
                    sum1[l]+=temp*data[j][l];
                    sum2[l]+=temp;
                }
            }
            for(int l=0;l<this.dim;l++)
                centers[i][l]=sum1[l]/sum2[l];
        }

    }

    private double JFCM(){
        double fcm=0.0;
        for(int i=0;i<k;i++){
            for(int j=0;j<this.cols;j++){
                //System.err.println(Math.abs(distance(data[j],centers[i],this.method)));
                fcm += Math.pow(weight[j][i],2.0)*
                       Math.pow(Math.abs(distance(data[j],centers[i],this.method)),2.0);
            }
        }
        return fcm;
    }

    private void resetWeight(){
        double temp;
        for(int l=0; l<k; l++){
            for(int i=0; i<this.cols; i++){
                temp = 0.0;
                for(int j=0; j<k; j++){
                    if(Math.abs(distance(data[i],centers[j],this.method))==0)
                        temp=temp+Math.pow(Math.abs(distance(data[i],centers[l],this.method))/1, 2.0);
                    else
                        temp=temp+Math.pow(Math.abs(distance(data[i],centers[l],this.method))/Math.abs(distance(data[i],centers[j],this.method)), 2.0);
                    if(Double.isNaN(temp))System.err.println("resetWeight temp is NaN\ttemp="+Math.abs(distance(data[i],centers[l],this.method))+"/"+Math.abs(distance(data[i],centers[j],this.method)));
                }

                if(temp == 0){
                    weight[i][0] = 1;
                    for(int x=1; x<k; x++) weight[i][x]=0;
                }
                else weight[i][l]=1/temp;
            }
        }
    }

    public void closestHard(){
        //System.out.println(weight.length);
        //System.out.println(weight[0].length);

        double[][] output = new double[this.k][this.dim];
                //System.out.println(weight.length+","+weight[0].length);
        //System.out.println(output.length+","+output[0].length);
        int index;
        double max;
        ArrayList<HashSet<Integer>> bestmatches = new ArrayList<HashSet<Integer>>();
        for (int i = 0; i < k; i++)
            bestmatches.add(new HashSet<Integer>());
        for(int i=0;i<weight.length;i++){
            index=-1;max=Double.MIN_VALUE;
            for(int j=0;j<weight[i].length;j++)
                if(weight[i][j]>max){index=j;max=weight[i][j];}
            HashSet<Integer> set = new HashSet<Integer>();
            try{
                    set =(HashSet<Integer>) bestmatches.get(index);
            }catch(Exception e){
                e.printStackTrace(System.err);
                for(int x=0;x<weight.length;x++){
                    for(int y = 0; y<weight[0].length;y++)
                        System.err.print(weight[x][y]+",");
                    System.err.println();
                }
            }
            // set.add(blog);
            set.add(i);
            //System.out.println(i+"\t"+index);
        }

        int rows=0;
        for (int i = 0; i < k; i++) {
            HashSet<Integer> set = (HashSet<Integer>) bestmatches.get(i);
            int count = 0;
            double[] row = new double[this.dim];
            for (Iterator<Integer> it = set.iterator(); it.hasNext();) {
                // String blog = (String)it.next();
		int which = it.next();
		// double[] freqs = (double[])this.data.get(blog);
		double[] freqs = this.data[which];
		for (int j = 0; j < this.dim; j++) {
                    row[j] += freqs[j];
		}
		count++;
            }
            for (int j = 0; j < row.length; j++) {
                if (count > 0) {
                    row[j] /= count;
                }
            }
            output[i] = row;
	}


        // beginning output in transposed (back) order, with class label
        // double[][] clusters = new double[k][this.dim];
        rows = output[0].length;
        cols = output.length;
        for (int i=0; i<rows; i++) {
            String result="";
            for (int j=0; j<cols; j++) {
            	result += output[j][i]+" ";
            }
            System.out.println(result);
	}
    }

    public FCMClustering(String s, double r, int m) {
        this.ratio = r;
        this.method = m;
        comm.LoadData ld = new comm.LoadData();
        this.data = ld.loadRawTransposed(s);
        if (this.data == null){
            System.err.println("Data loading error.");
            System.exit(0);
        }
        this.umax = ld.umax;    this.umin = ld.umin;
        this.tmax = ld.tmax;    this.tmin = ld.tmin;

        this.cols = this.tmax - this.tmin + 1;
        this.dim = this.umax - this.umin + 1;
    }

}
