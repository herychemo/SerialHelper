package com.optimizedproductions.serialHelper.serial_utils;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.Scanner;

public class TerminalHelper {
	
	public String[] executeLines(String[] lines) throws Exception{
		ProcessBuilder builder = new ProcessBuilder("/bin/bash");
		
		
		
        Process p=null;
        p=builder.start();

        BufferedWriter p_stdin = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
        
        for(String line : lines){
        	p_stdin.write(line);
        	p_stdin.newLine();
        	p_stdin.flush();
        }
        
        p_stdin.write("exit");
        p_stdin.newLine();
        p_stdin.flush();
        
        Scanner s = new Scanner( p.getInputStream() );
        LinkedList<String> temp=new LinkedList<String>();
        
        while (s.hasNext())
            temp.offer(s.next());
        s.close();
        
        String[] results=new String[temp.size()];
        
        for(int i=0;i<results.length;i++)
        	results[i]=temp.poll();
        
        return results;
	}
	public String[] executeLine(String c) throws Exception{
		return executeLines(new String[]{c});
	}
	
	public static void main1(){
		TerminalHelper t = new TerminalHelper();
		try {
			//String[] g=t.executeLine("ls /home/heriberto");
			//String[] g=new String[]{"ls /home","ls /home/heriberto"};
			String[] g=new String[]{"touch /tmp/hola.txt","echo 'hola !' >> /tmp/hola.txt","echo listo"};
			
			g=t.executeLines(g);
			
			for(String y:g){
				System.out.println(y);
			}
		} catch (Exception e) {e.printStackTrace();}
	}
	
	public static void main(String args[]){
		main1();
	}
	
}
