package com.optimizedproductions.serialHelper.serial_utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.LinkedList;

public class FromResToFileHelper {

	public static final String aux_dir = "files_to_export";

	private String base;
	
	public FromResToFileHelper(){
		if(System.getProperty("os.name").contains("indows"))
			base="C:";
		else
			base="";
	}
	
	public boolean getSomething(String from,  String to){
		if(from==null||to==null)
			return false;
		
		String[] path = getStepByStep(to);
		to=to.substring(to.lastIndexOf('/')+1);
		
		return makeFile(from,makePath(path),to);
	}
	private boolean makeFile(String resPath, String fullPath,String nombre){
        				//resPAth es path del recurso, fullPath es dirección a donde se creará el archivo nombre.
		InputStream res = FromResToFileHelper.class.getResourceAsStream(resPath);
		byte[] b = new byte[2048];
        int d;
        FileOutputStream file;
        fullPath+='/'+nombre;
        
        try {
            if (!new File(fullPath).exists()) {
                file = new FileOutputStream(fullPath);
                d = res.read(b);
                while (d != -1) {
                    file.write(b, 0, d);
                    d = res.read(b);
                }
                file.close();
            }
            return true;
        }catch(Exception e){
        	System.out.println(e.getMessage());
        	return false;
        }
	}
	private String[] getStepByStep(String g){
		LinkedList<String> aux=new LinkedList<String>();
		g=g.replace("C:", "");
		g=g.substring(1, g.length());
		
		String util;
		int i;
		while((i=g.indexOf('/'))!=-1)
		{
			util=g.substring(0, i);
			g=g.substring(i+1);
			aux.offer(util);
		}
		
		String[] gg=new String[aux.size()];
		
		for(int ii=0;ii<gg.length;ii++)
			gg[ii]=(String)aux.poll();
		
		return gg;
	}
	
	private String makePath(String[] path){
		String fullPath=base;
		for(int i=0;i<path.length;i++){
			fullPath+='/'+path[i];
			if(!new File(fullPath).exists())
				new File(fullPath).mkdir();
			//System.out.println(fullPath);
		}
		return fullPath;
	}
	
	public static void main(String[] args){
		//new FromResToFileHelper().getSomething("/aux/Sh/UrlUtils.txt","/tmp/sh/equisde.txt");
	}
	
}
