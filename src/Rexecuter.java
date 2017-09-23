import java.io.*; 
import java.util.*;
import java.lang.Math;
import java.lang.reflect.*;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.nio.file.Paths;

import java.util.*;
import java.util.concurrent.*;
import static java.util.Arrays.asList;

public class Rexecuter {
    
    static class Rjob implements Callable<Long> {
        private final String RbufferFile;
        private final String RbufferOutput;
		private final String RecordCommand;
		private Rbuffer myRbuffer;
        Rjob(String ConstructorFile, String ConstructorOutput, String ConstructorCommand) {
		//This is a constructor, we suggest no polymorphism here.
            this.RbufferFile= ConstructorFile;
            this.RbufferOutput= ConstructorOutput;
			this.RecordCommand = ConstructorCommand;
			this.myRbuffer=new Rbuffer(RbufferFile,RecordCommand,RbufferOutput);
        }
        
        @Override
        public Long call() {
            Long flag = 0L;
            flag = new Long( myRbuffer.bufferRead() );
			//flag = myRbuffer.bufferWrite();
			//It depends on whether you want to store your output or not, in this simple example we do not need any output.
            return flag;
        }      
    }
	
	//Following is another way of doing the same thing using fork/join mechanism.
    static class RTask extends RecursiveTask<String> {
    private final String readFile;
    private final String RCommand;
    
	    RTask(String ConstructorreadFile, String ConstructorRcommand) {
	        super();
	        this.readFile = ConstructorreadFile;
	        this.RCommand = ConstructorRcommand;
	    }
    
    	@Override
	    protected String compute() {
	        String count = new String();
	        List<RecursiveTask<String>> forks = new LinkedList<>();
			List<String> myList = Arrays.asList(new String[] {"rnorm(101)", "rnorm(102)","rnorm(103)"});
	        for (String subTask : myList) {
	            RTask task = new RTask(readFile,subTask);
				//Or if you want to switch between files instead of commands, then
				//RTask task = new RTask(subTask,Rcommand);
				//Or you can even change both parameters at the same time using two recursive lists.
				//RTask task = new RTask(subTask1,subTask2);
	            forks.add(task);
	            task.fork();
	        }
	        for (RecursiveTask<String> task : forks) {
	            count = count + task.join();
	        }
	        return count;
	    }
		private final ForkJoinPool forkJoinPool = new ForkJoinPool();
	    String parallelExec(String readIn, String Cmd) {
	        return forkJoinPool.invoke(new RTask(readIn, Cmd));
	    }
		String parallelExec() {
	        return forkJoinPool.invoke(new RTask(readFile, RCommand));
	    }
	}
    
	//Following is the main program.
    public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			String DIRE=Paths.get(".").toAbsolutePath().normalize().toString()+"/";
			//System.out.println(DIRE);
			//System.setProperty("java.library.path", System.getProperty("java.library.path")+DIRE);
			System.setProperty("java.library.path", System.getProperty("java.library.path")+":/home/luo.619/R/x86_64-redhat-linux-gnu-library/3.4/rJava/jri/:");
			//Replace the ":/home/luo.619/R/x86_64-redhat-linux-gnu-library/3.4/rJava/jri/:" with the local R library for rJava. You can get library path using .libPaths() after installation of R.
			//To use this framework you need to install "rJava" package, which can be installed using install.packages("rJava") in R prompt lines.
			Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
		    fieldSysPath.setAccessible( true );
		    fieldSysPath.set( null, null );
			System.out.println( System.getProperty("java.library.path") );
			System.out.println("...JRI Library successfully checked.");

			//These lines incorporates the automatic setting of JRI library using reflection. Ref(https://stackoverflow.com/questions/7016391/difference-between-system-load-and-system-loadlibrary-in-java)
	        Record rec= new Record();
			System.out.println(rec.Reval("version"));
			System.out.println("...Java-R connection successfully checked.");
			rec.Rassign("var1","6000");
			rec.Reval("print(var1)");
	        rec.pushlog("...Push-log successfully checked.","log1.log");
			System.out.println( "...Initialization complete!");
			//In following lines, we can load a data file into a BufferReader object and then read line by line.
			
			ExecutorService executor = Executors.newFixedThreadPool(2);
	        List <Future<Long>> jobsPool = executor.invokeAll(asList(
	            new Rjob("test.txt", "test_output.txt","rnorm(100)"), 
				new Rjob("test.txt", "test_output.txt","rnorm(101)"), 
				new Rjob("test.txt", "test_output.txt","rnorm(102)")
				//Split your jobs into a couple Rjobs and put them here.
	        ));
	        executor.shutdown();
	        for (Future<Long> oneJob : jobsPool) {
	            System.out.println(oneJob.get());
	        } 
			//Join.fork mechanism
			RTask rexe= new RTask("test.txt","rnorm(999)");
			rexe.parallelExec();
			//In each of these jobs, we load a Rbuffer which split the R jobs into many sub-jobs that uses their own R kernels, by doing this on a multi-core machine we can actually realized the multi-thread scheme.
		}catch(Exception elog){ 
        	elog.printStackTrace();
        }
	}

}