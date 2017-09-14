package com.optimizedproductions.serialHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Scanner;

import gnu.io.DriverHelper;

class ConfigSystemHelper {
	
	public void doMyWork(){
		
		String sys=System.getProperty("os.name");
		
		/*System.out.println(sys);
        String osArch = System.getProperty("sun.arch.data.model");
        System.out.println(osArch);
        System.out.println(System.getProperty("java.library.path"));
        System.out.println(System.getProperty("gnu.io.rxtx.SerialPorts"));
        */
		
		if (sys.contains("Windows")){
			helpToWindows();
		}else if(sys.contains("Linux")){
			//helpToLinux();
			helpToLinuxTest();
		}else
			System.out.println("Sistema no depurado, Podría haber problemas");
		
	}
	
	private void helpToWindows(){
        if (!new DriverHelper().checkSystemDrivers()) 
            new DriverHelper().buildSerialDrivers();
	}
	
	private void helpToLinux(){
		/*Podría no ser necesario más por la adap
		 * 
		System.setProperty( "java.library.path", System.getProperty("java.library.path")+":/usr/lib/jni/" );
		try{
			Field fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
			fieldSysPath.setAccessible( true );
			fieldSysPath.set( null, null );
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		*/
		
	}
	
	private void helpToLinuxTest(){

        if (!new DriverHelper().checkSystemDrivers()) 
            new DriverHelper().buildSerialDrivers();
		
		//System.out.println("before");
		
		//--
		
		//System.out.println(System.getProperty("java.library.path"));
        //System.out.println(System.getProperty("gnu.io.rxtx.SerialPorts"));
		
        /*
		System.setProperty( "java.library.path", System.getProperty("java.library.path")+":/usr/lib/jni/" );

		System.out.println(System.getProperty("java.library.path"));
        System.out.println(System.getProperty("gnu.io.rxtx.SerialPorts"));
        */
        
		/* Gracias al fix para los ACM ya no es necesaria esta linea ;)
		System.setProperty("gnu.io.rxtx.SerialPorts",
				"/dev/ttyS0:/dev/ttyS1:/dev/ttyS2:/dev/ttyS3:"+
				"/dev/ttyACM0:/dev/ttyACM1:/dev/ttyACM2:/dev/ttyACM3:"+
				"/dev/ttyUSB0:/dev/ttyUSB1:/dev/ttyUSB2:/dev/ttyUSB3");
		*//*
		
		try{
			Field fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
			fieldSysPath.setAccessible( true );
			fieldSysPath.set( null, null );
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		*/
		/*
        System.out.println(System.getProperty("java.library.path"));
        System.out.println(System.getProperty("gnu.io.rxtx.SerialPorts"));
        System.out.println();
        */
	}
	
	public static void testConsole(){
		try{
			//String[] args=new String[]{"/bin/bash","/home/heriberto/Documentos/groupHacksTest/3/uno.sh"};
			String[] args=new String[]{"ls"};

			Process p =	Runtime.getRuntime().exec(args);
			
			BufferedWriter pAux = 
			          new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
			pAux.write("ls");
			pAux.newLine();
			pAux.flush();
			
			BufferedReader in=new BufferedReader(new InputStreamReader(p.getInputStream()));
			p.waitFor();				
			String line = "";			
		    while ((line = in.readLine())!= null)
		    	System.out.println(line);
		    
		}catch(Exception e){System.out.println(e.getMessage());
		System.exit(0);}
	}
	
	public static void testConsole2(){
		//init shell
        ProcessBuilder builder = new ProcessBuilder( "/bin/bash" );
        Process p=null;
        try {
            p = builder.start();
        }
        catch (IOException e) {
            System.out.println(e);
        }
        //get stdin of shell
        BufferedWriter p_stdin = 
          new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));

        // execute the desired command (here: ls) n times
        int n=1;
        for (int i=0; i<n; i++) {
            try {
                //single execution
            	p_stdin.write("gnome-terminal -e 'bash /home/heriberto/Documentos/groupHacksTest/3/uno.sh'");
            	p_stdin.newLine();
            	p_stdin.flush();
            }
            catch (IOException e) {
            System.out.println(e);
            }
        }

        // finally close the shell by execution exit command
        try {
            p_stdin.write("exit");
            p_stdin.newLine();
            p_stdin.flush();
        }
        catch (IOException e) {
            System.out.println(e);
        }
        
        // write stdout of shell (=output of all commands)
        Scanner s = new Scanner( p.getInputStream() );
        while (s.hasNext())
        {
            System.out.println( s.next() );
        }
        s.close();
           
	}
	
	public static void main(String args[]){
		System.out.println("resultado:");
		//testConsole2();
	}
}
