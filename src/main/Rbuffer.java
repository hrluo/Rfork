package main;
import java.io.*; 
import java.time.Instant;
import java.util.*;
import java.lang.Math;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

public class Rbuffer {
	public static class Record{
		//Record is a class used to store the record of the methylated cytosines pair scanning record.
		public static Rengine reng= new Rengine(new String[] { "--vanilla" }, false, null);
		//Rengine reng=Rengine.getMainEngine();
		private int position;
		private double r1;
		private double f1;
		private double r2;
		private double f2;
		public Record(){}
		public Record(double position1,double r11,double f11,double r21,double f21){
			Double position2=new Double(position1);
			position=position2.intValue();
			r1=r11;
			f1=f11;
			r2=r21;
			f2=f21;
		}
		public Record(String position1,String r11,String f11,String r21,String f21){
			Double position2=new Double(position1);
			position=position2.intValue();
			r1=Double.parseDouble(r11);
			f1=Double.parseDouble(f11);
			r2=Double.parseDouble(r21);
			f2=Double.parseDouble(f21);
		}
		public int getposition(){
			return position;
	 	}
		public String fisher(){
			REXP x1;
	        reng.eval("source('package.test.R')");
	        double[] var1= {r1,f1,r2,f2};
	        reng.assign("mat1", var1);
	        //REXP x5;
	        //x5=reng.eval("mat1");
	        x1=reng.eval("Fisher.test(matrix(mat1,nrow=2))");
	        String ret = String.valueOf(x1.asDouble());
	        return( ret );
		}
		public String chisq(){
			REXP x2;
	        reng.eval("source('package.test.R')");
	        double[] var1= {r1,f1,r2,f2};
	        reng.assign("mat1", var1);
	        //REXP x5;
	        //x5=reng.eval("mat1");
	        x2=reng.eval("chisq2.test(matrix(mat1,nrow=2))");
	        String ret = String.valueOf(x2.asDouble());
	        return( ret );
		}
		public String SK(){
			REXP x3;
	        reng.eval("source('package.test.R')");
	        double[] var1= {r1,f1,r2,f2};
	        reng.assign("mat1", var1);
	        //REXP x5;
	        //x5=reng.eval("mat1");
	        x3=reng.eval("StorerKim.test(matrix(mat1,nrow=2))");
	        String ret = String.valueOf(x3.asDouble());
	        return( ret );
		}
		public static double choose(double n, double r){
		    if (r < 0 || r > n) return 0;
		    if (r > n/2) {r = n - r;}
		    double denominator = 1.0;
		    double numerator = 1.0;
		    for (int i = 1; i <= r; i++) {
		        denominator = denominator* i;
		        numerator = numerator* (n + 1 - i);
		    }
		    return numerator / denominator;
		}
		public static double b(double r, double n, double p){
		    double result=0.0;
		    result=Record.choose(n, r)*Math.pow(p,r)*Math.pow(1-p,n-r);
		    return result;
		}
		public void getdata(){
			System.out.println("\t Pop1 \t Pop2");
			System.out.println("Suc "+r1+"\t"+r2);
			System.out.println("Fai "+f1+"\t"+f2);
		}
		public static int FindPosition(double [][] array2d,double position1){
			int flag=-1;
			int max=array2d.length;
			for(int k=0;k<max;k=k+1){
				if(array2d[k][0]==position1){flag=k;}
			}
			return flag;
		}
	}
	public static void pushlog(String message, String loggerfilename) {
        try{
	        BufferedWriter bwlogger = new BufferedWriter(new FileWriter(loggerfilename, true));      
	        Instant instant = Instant.now (); // Current date-time in UTC.
	        String outputInstant = instant.toString ();
	        bwlogger.newLine();
	        String contentapp="Time="+outputInstant+" & Message="+message;
	        bwlogger.write(contentapp);
	        System.out.println(contentapp);
	        bwlogger.newLine();
	        bwlogger.close();
        }catch(Exception elog){ 
        	elog.printStackTrace();
        }
        return;
	}
	public static int countLines(String filename) throws IOException {
	    InputStream is = new BufferedInputStream(new FileInputStream(filename));
	    try {
	        byte[] c = new byte[1024];
	        int count = 0;
	        int readChars = 0;
	        boolean empty = true;
	        while ((readChars = is.read(c)) != -1) {
	            empty = false;
	            for (int i = 0; i < readChars; ++i) {
	                if (c[i] == '\n') {
	                    ++count;
	                }
	            }
	        }
	        return (count == 0 && !empty) ? 1 : count;
	    } finally {
	        is.close();
	    }
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//testing if package.test.R work
		//Record rtest=new Record("123456","24","10","20","17");
		//System.out.println(rtest.SK());
        String Chr="Y"; 
        String DIRE="C:\\Users\\Henry\\Desktop\\";
        String FILE_h1=DIRE+"mc_h1_"+Chr;
        String FILE_i90=DIRE+"mc_i90_"+Chr;
        //following are temps
        String FILE_index=DIRE+"Index_Chr"+Chr+".txt";
        String FILE_combined=DIRE+"Combined_Chr"+Chr+".txt";
        //following is final output ready to bake.
        String FILE_tested= DIRE+"Tested_Chr"+Chr+".txt";
        String FILE_log=DIRE+"logs_Chr"+Chr+".ini";
        pushlog("Environment Initilized.",FILE_log);//////////
        Record.reng.eval("version");
        pushlog("JRI Initilized.",FILE_log);//////////
        //Now we make a index file using data files.
        //output file

        REXP xind;
        Record.reng.eval("source('package.indexmaker.R')");
        String[] var2= {FILE_h1,FILE_i90,FILE_index};
        Record.reng.assign("param", var2);
        xind=Record.reng.eval("indexmaker(param[1],param[2],param[3])");
        pushlog("H1^I90 index Constructed.",FILE_log);//////////
        //
        try {
        	int index_dim=countLines(FILE_index);
        	System.out.println(index_dim+" lines of GWAS data.");
        	//
        	//
        	double[][] array_merge=new double[index_dim+1][5];
        	//Put length(index) into the first dimension+1.
        	FileReader fr_index = new FileReader(FILE_index); 
        	BufferedReader br_index = new BufferedReader(fr_index); 
        	br_index.readLine();
            String bline_index = null;
            int ct=0;
            while ((bline_index = br_index.readLine()) != null) { 
	            double str_index=Integer.parseInt(bline_index);	   
	           //System.out.println(str_h1[1]);
	            array_merge[ct][0]=str_index;
	            ct=ct+1;
            }
            br_index.close();
            
        	FileReader fr_h1 = new FileReader(FILE_h1); 
        	BufferedReader br_h1 = new BufferedReader(fr_h1); 
        	br_h1.readLine();
            String bline_h1 = null;
            ct=0;
            //reset counter
            while ((bline_h1 = br_h1.readLine()) != null) { 
	            String[] str_h1=bline_h1.split("\\t");	  
	            if(Double.valueOf(str_h1[1])==array_merge[ct][0]){
	            	//System.out.println(str_h1[1]);
		            array_merge[ct][0]=Integer.parseInt(str_h1[1]);
		            array_merge[ct][1]=Integer.parseInt(str_h1[4]);
		            array_merge[ct][2]=Integer.parseInt(str_h1[5])-Integer.parseInt(str_h1[4]);
		            ct=ct+1;
		            System.out.print("H1 <="+array_merge[ct][0]+"-th line from h1.\r");
	            }
	            
            }
            br_h1.close();
            pushlog("H1 Scanned.",FILE_log);//////////
            FileReader fr_i90 = new FileReader(FILE_i90); 
        	BufferedReader br_i90 = new BufferedReader(fr_i90); 
        	br_i90.readLine();
            String bline_i90 = null;
            //reset counter
            ct=0;
            while ((bline_i90 = br_i90.readLine()) != null) { 
	            String[] str_i90=bline_i90.split("\\t");
	            ct=Record.FindPosition(array_merge,Double.valueOf(str_i90[1]));
	            if(ct!=-1){
		            //System.out.println(str_i90[1]);
		            array_merge[ct][0]=Integer.parseInt(str_i90[1]);
		            array_merge[ct][3]=Integer.parseInt(str_i90[4]);
		            array_merge[ct][4]=Integer.parseInt(str_i90[5])-Integer.parseInt(str_i90[4]);
		            System.out.print("I90 <="+array_merge[ct][0]+"-th line from i90.\r");
	            }

            }
            br_i90.close();
            pushlog("I90 Scanned.",FILE_log);//////////
          
            //Prepare input file, to save RAM, even JAVA cannot handle huge double matrix...
            File fileRAW = new File(FILE_combined);  //RAW temp file.
            FileWriter outRAW = new FileWriter(fileRAW);  //Use BufferedWriter will cause error here...
            //\\t seperate.
	        for(int i=0;i<=index_dim;i++){
				 for(int j=0;j<=4;j=j+1){
				  outRAW.write(array_merge[i][j]+"\t");
				 }
				 System.out.print("Constructing the "+i+"-th line out of "+index_dim+".\r");
				 outRAW.write("\r\n");
	        }
            outRAW.close();
            pushlog("H1-I90 Merged.",FILE_log);//////////
            
            //Read data from RAW temp file.
            FileReader fr1 = new FileReader(FILE_combined); 
        	BufferedReader br1 = new BufferedReader(fr1); 
            String bline1 = null;
            //reset counter
            ct=0;
            
            File file1 = new File(FILE_tested); 
            //output file
            BufferedWriter bw1 = new BufferedWriter(new FileWriter(file1)); 
            while ((bline1 = br1.readLine()) != null) { 
            	String[] str1=bline1.split("\\t");
	    	    Record record1=new Record(str1[0],str1[1],str1[2],str1[3],str1[4]);
	    	    String str2 = record1.getposition()+"\t"+str1[1]+"\t"+str1[2]+"\t"+str1[3]+"\t"+str1[4]+"\t"
	    	                 +record1.fisher()+"\t"+record1.chisq()+"\t"+record1.SK();
	    	    System.out.println(str2);
	    	    bw1.write(str2);
                bw1.newLine();//next line
                bw1.flush();
            }
            pushlog("H1-I90 Tested.",FILE_log);//////////
            bw1.close();
            br1.close();
        } catch (Exception e1) {  
            e1.printStackTrace();  
        }
	}
}

