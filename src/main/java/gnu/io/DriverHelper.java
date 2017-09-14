package gnu.io;

import com.optimizedproductions.serialHelper.serial_utils.FromResToFileHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class DriverHelper {
	
	public static String getPath(){
		String sys=System.getProperty("os.name");
		return getPath(sys);
	}
	public static String getPath(String sys){
		if(sys.contains("indows"))
			return "C:/Tools/Driver/Serial/rxtxSerial.dll";
		else if(sys.contains("inux")){
			String HOME=valueToHome();
			return HOME+"/Tools/Driver/Serial/librxtxSerial.so";
		}
		return "";
	}
	
	private static String valueToHome(){
		String line = "";	
		try{
			String f="/tmp/a.sh";
			if(!new File(f).exists())
				new FromResToFileHelper().getSomething(FromResToFileHelper.aux_dir + "/Sh/giveMeHome.sh",f);
				
			String[] cmds=new String[]{"/bin/bash",f};
			
			Process p =	Runtime.getRuntime().exec(cmds);
			
			BufferedReader in=new BufferedReader(new InputStreamReader(p.getInputStream()));
			p.waitFor();
			String line2="";
		    while ((line2 = in.readLine())!= null)
		    	line+=line2;
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	    return line;
	}
	public static void main(String args[]){
		System.out.println(valueToHome());
		System.out.println(getPath());
	}
	
	
	public boolean checkSystemDrivers(){
		return new File(getPath()).exists();
	}
	public void buildSerialDrivers(){
		String sys = System.getProperty("os.name");
        String arc = System.getProperty("sun.arch.data.model");
        if(sys.contains("indows"))
        	if(arc.contains("32"))
        		new FromResToFileHelper().getSomething("/files_to_export/Driver/x86/rxtxSerial.dll", getPath(sys));
        	else
        		new FromResToFileHelper().getSomething("/files_to_export/Driver/x64/rxtxSerial.dll", getPath(sys));
        else if(sys.contains("inux"))
        	if(arc.contains("32"))
        		new FromResToFileHelper().getSomething("/files_to_export/Driver/x86/librxtxSerial.so", getPath(sys));
        	else
        		new FromResToFileHelper().getSomething("/files_to_export/Driver/x64/librxtxSerial.so", getPath(sys));

	}
}
