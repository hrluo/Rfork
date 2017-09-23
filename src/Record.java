import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

import java.io.*; 
import java.util.*;
import java.lang.Math;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.nio.file.Paths;
import java.lang.reflect.*;

public class Record{
	private Rengine reng= new Rengine(new String[] { "--vanilla" }, false, null);
	//Rengine reng=Rengine.getMainEngine();
	private int myint;
	private double mydouble;
	private String myString;
	Record(){
		this.myint=0;
	//This is the constructor of Record;
	}
	Record(int ConstructorInt){
		this.myint=ConstructorInt;
	//This is the constructor of Record with integer parameter;
	}
	Record(String ConstructorString){
		this.myString=ConstructorString;
	//This is the constructor of Record with String parameter;
	}
	Record(double Constructordouble){
		this.mydouble=Constructordouble;
	//This is the constructor of Record with double parameter;
	}
	//Record is a class used to store the record of the line of record that Rbuffer reads.
	//The benefit of doing this is that we handle an REngine object which deals with the io. 
	public REXP Reval(String command) {
		REXP xind=new REXP();
      	try{
			xind=this.reng.eval(command);		
		}catch(Exception elog){ 
        	elog.printStackTrace();
        }
		return(xind);
	}
	//Reval directly execute the command passed into it using its own Rengine object, therefore a simplisitic way of doing multi-thread is to creat many Record objects and assign jobs to each of them.
	public REXP Rfunc(String scriptloc) {
		REXP xind=new REXP();
      	try{
			if(scriptloc==""){
				xind=this.reng.eval("source('Rfunc.R')");		
			}else{
				xind=this.reng.eval("source('"+scriptloc+"')");	
			}
		}catch(Exception elog){ 
        	elog.printStackTrace();
        }
		return(xind);
	}
	//Rfunc execute the R script named Rfunc.R within the same directory of the Rbuffer file, or you can supply an absolute location to a certain R script file you want to run.
	public REXP Rsend(String scriptloc,String content) {
		REXP xind=new REXP();
      	try{
			if(scriptloc==""){
				xind=this.reng.eval("source('Rfunc.R "+content+"')");		
			}else{
				xind=this.reng.eval("source('"+scriptloc+" "+content+"')");	
			}
		}catch(Exception elog){ 
        	elog.printStackTrace();
        }
		return(xind);
	}
	//Rsend basically do the same thing as Rfunc but you can pass an arguement to the Rscript, the difference is that when you only want to load certain functions from R, then a blend of Rfunc and Reval are enough while Rsend blends with Java.
	public REXP Rassign(String nameOfRVariable, String valueOfRVariable) {
		REXP xind=new REXP();
      	try{
			this.reng.assign(nameOfRVariable, valueOfRVariable);	
		}catch(Exception elog){ 
        	elog.printStackTrace();
        }
		return(xind);
	}
	//Rassign family of functions send the data buffered by Java to another nameOfRVariable variable in R.
	
	public void pushlog(String message, String loggerfilename) {
        try{
	        BufferedWriter bwlogger = new BufferedWriter(new FileWriter(loggerfilename, true));      
	        String outputInstant = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
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
}

