import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

import java.io.*; 
import java.util.*;
import java.lang.Math;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.nio.file.Paths;
import java.lang.reflect.*;
public class Rbuffer {
	private String myfile;
	private Record myrec;
	private String mycommand;
	private String myoutput;
	Rbuffer(String ConstructorString,String ConstructorCommand, String ConstructorOutput){
		this.myfile=ConstructorString;
		this.myoutput=ConstructorOutput;
		this.myrec=new Record();
		this.mycommand=ConstructorCommand;
	//This is the constructor of Record with String parameter;
	}
	public int bufferRead(){
	 	try{			
			FileReader fr_index = new FileReader(myfile); 
	    	BufferedReader br_index = new BufferedReader(fr_index); 
	    	br_index.readLine();
	        String bline_index = null;
	        int ct=0;
		    while ((bline_index = br_index.readLine()) != null) { 
		       int intInThisLine=Integer.parseInt(bline_index);
			   double doubleInThisLine=Double.parseDouble(bline_index);	
		       String StringInThisLine=String.valueOf(bline_index);
			   //These three variables storage the line you read in form of int, double and String respectively.
			   //myrec.Reval("rnorm(1)");
			   //myrec.Rassign();
			   //myrec.Reval();
			   String myresult = String.valueOf(myrec.Reval(mycommand).asDouble());
			   //This line means that you expect the returning result from R is a double type number, analougously below.
			   //String myresult = String.valueOf(myrec.Reval().asInt());
			   //String myresult = String.valueOf(myrec.Reval().asString());
			   System.out.println(myresult);
			   //ct is the line counter.
		       ct=ct+1;
		    }
		    br_index.close();
			return(1);
		}catch(Exception elog){ 
        	elog.printStackTrace();
			return(0);
        }
	}
	public int bufferWrite(String outputfile){
	 	try{
			FileWriter fw_index = new FileWriter(outputfile,true);
	        BufferedWriter bw_index = new BufferedWriter(fw_index);      
			FileReader fr_index = new FileReader(myfile); 
	    	BufferedReader br_index = new BufferedReader(fr_index); 
	    	br_index.readLine();
	        String bline_index = null;
	        int ct=0;
		    while ((bline_index = br_index.readLine()) != null) { 
		       int intInThisLine=Integer.parseInt(bline_index);
			   double doubleInThisLine=Double.parseDouble(bline_index);	
		       String StringInThisLine=String.valueOf(bline_index);
			   //These three variables storage the line you read in form of int, double and String respectively.
			   //myrec.Reval("rnorm(1)");
			   //myrec.Rassign();
			   //myrec.Reval();
			   String myresult = String.valueOf(myrec.Reval(mycommand).asDouble());
			   //This line means that you expect the returning result from R is a double type number, analougously below.
			   //String myresult = String.valueOf(myrec.Reval().asInt());
			   //String myresult = String.valueOf(myrec.Reval().asString());
			   bw_index.write(myresult);
			   bw_index.flush(); 
			   //ct is the line counter.
		       ct=ct+1;
		    }
		    br_index.close();
			return(1);
		}catch(Exception elog){ 
        	elog.printStackTrace();
			return(0);
        }
	}
}

