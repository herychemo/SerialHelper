package com.optimizedproductions.serialHelper;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.util.LinkedList;
import java.util.Scanner;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import com.optimizedproductions.serialHelper.events.*;

public class SerialAdmin {
	
	//utilidades !!
		private char C='▓';
		private char P='░';
		private String ESTADO="BETA";
		public static String VERSION="J-SerialHelper-1.7.5c";
		private String NOTAS="Versión en pruebas para estandarizar en Linux :)\nCosas por hacer:\n"
				+P+"Terminar de escribir e implementar los bash necesarios para la siguiente tarea.\n"
				+P+"Conseguir permisos para acceder a dispositivos automaticamente.\n"
				+P+"Implementar los bash .\n"
				+C+"Fix para los estado lock .\n"
				+C+"Implementar tipos de recibir.\n"
				+C+"Mejorar tipos de recibir.\n";
	
	//Constantes publicas !!
		//estas son para definir, cada cuando se ejecuta el evento que llega al iniciar la conexión
	final private int RECIBIR_NADA=0;				//cuando no se desea recibir datos 
	final private int RECIBIR_CADA_DISPONIBLE=1;		//cuando se recibe un String, según va llegando
	final private int RECIBIR_CADA_LINEA=2;			//cuando se recibe un String, según cada linea que llega 
	final private int RECIBIR_CADA_CARACTER=3;		//cuando se recibe caracter por caracter
	final private int RECIBIR_CADA_BYTE=4;			//cuando se recibe byte por byte
	final private int RECIBIR_CADA_BYTE_ARRAY=5;		//cuando se recibe un buffer de bytes según va llegando
	
	//Constantes privadas !!
	private final int TIMEOUT = 2000;	//TIMEOUT Por defecto
	
	//ATRIBUTOS !!
	private OutputStream out = null;	//para escribir en puerto serial
	private	InputStream in=null;		//para leer desde puerto serial
	private SerialPort serial;			//auxiliar desde RXTX
	private Object event;			//evento para manejar la información que se recibe 
	private boolean active=false;		//para saber si hay una conexión activa

	private String PORT =null;			//nombre del puerto a conectarnos
	private int DATA_RATE=9600;			//Default 9600 baudio - velocidad de los datos
	private int RECEIVE=0;
		
	public SerialAdmin(){
		new ConfigSystemHelper().doMyWork();
		impLib();
	}
	private void impLib(){
		String div2="==========================================================================================";
		String div1="------------------------------------------------------------------------------------------";
		System.out.println(div1);
		System.out.println("Librería "+ESTADO);
		System.out.println(div2);
		System.out.println("Utility lib Version:");
		System.out.println("\t"+VERSION);
		System.out.println("Notas:");
		System.out.println("\t"+NOTAS);
		System.out.println(div2+"\n");
		System.out.println(div1);
	}
	
	public boolean begin(String PUERTO, int VELOCIDAD, final StringAvailableListener evento){
		return beginPro(PUERTO,VELOCIDAD,evento);
	}
	public boolean begin(String PUERTO, int VELOCIDAD, final LineAvailableListener evento){
		return beginPro(PUERTO,VELOCIDAD,evento);
	}
	public boolean begin(String PUERTO, int VELOCIDAD, final CharAvailableListener evento){
		return beginPro(PUERTO,VELOCIDAD,evento);
	}
	public boolean begin(String PUERTO, int VELOCIDAD, final ByteAvailableListener evento){
		return beginPro(PUERTO,VELOCIDAD,evento);
	}
	public boolean begin(String PUERTO, int VELOCIDAD, final ByteArrayAvailableListener evento){
		return beginPro(PUERTO,VELOCIDAD,evento);
	}
	
	private boolean beginPro(String PUERTO, int VELOCIDAD, final Object evento){
		if(!finish())
			return false;
		
		this.PORT = PUERTO;
		this.DATA_RATE = VELOCIDAD;
		this.event=evento;
		
		if(!turnOn())
			return false;
		
		this.RECEIVE=getReceive(event);
		try{
			if(this.RECEIVE!=0){
				//Agregar el evento listener, este llega desde afuera para mayor comodidad en cada App 
				serial.addEventListener(new SerialPortEventListener(){
					//@Override
					public void serialEvent(SerialPortEvent ev) {
						if(ev.getEventType()==SerialPortEvent.DATA_AVAILABLE){
							switch (RECEIVE){
							case 1:
								((StringAvailableListener) event).onStringAvailable(readAvailable());
								break;
							case 2:
								((LineAvailableListener) event).onLineAvailable(readLine());
								break;
							case 3:
								((CharAvailableListener) event).onCharAvailable(readChar());
								break;
							case 4:
								((ByteAvailableListener) event).onByteAvailable(readByte());
								break;
							case 5:
								((ByteArrayAvailableListener) event).onByteArrayAvailable(readByteArray());
								break;
							
							}
						}
					}});
				serial.notifyOnDataAvailable(true);
			}
			active=true;
			return true;
			
		}catch(Exception e){
			System.out.println("Error agregando evento");
			return false;
		}		
	}
	
	private boolean turnOn() {
		//MÉTODO FUNDAMENTAL PARA CONEXIÓN
		CommPortIdentifier portID = null;
		Enumeration<CommPortIdentifier> puertoE = CommPortIdentifier.getPortIdentifiers();
		
		while (puertoE.hasMoreElements()) {
			CommPortIdentifier actualPuertoID = puertoE.nextElement();
			if (PORT.equals(actualPuertoID.getName())) {
				portID = actualPuertoID;
				break;
			}
		}
		
		if (portID == null)
			return false;
		
		try {// abrir puerto serie
			serial = (SerialPort) portID.open(this.PORT,this.TIMEOUT);

			// configurarlo
			serial.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8,SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			
			//abrir escrituras lecturas
			out = serial.getOutputStream();
			in = serial.getInputStream();

			return true;
		} catch (Exception e){
			e.printStackTrace();
			return false;
		}	
	}
	private int getReceive(Object event){
		if(event ==null){
			return this.RECIBIR_NADA;
		}else if(event instanceof StringAvailableListener){
			return this.RECIBIR_CADA_DISPONIBLE;
		}else if(event instanceof LineAvailableListener){
			return this.RECIBIR_CADA_LINEA;
		}else if(event instanceof CharAvailableListener){
			return this.RECIBIR_CADA_CARACTER;
		}else if(event instanceof ByteAvailableListener){
			return this.RECIBIR_CADA_BYTE;
		}else if(event instanceof ByteArrayAvailableListener){
			return this.RECIBIR_CADA_BYTE_ARRAY;
		}else{
			return 0;
		}
	}
	
	public String[] getPorts(){		//Devuelve unalista con los nombres de los puertos.
		LinkedList<String> PORTS=new LinkedList<String>();

		Enumeration<CommPortIdentifier> puertoE = CommPortIdentifier.getPortIdentifiers();
		while (puertoE.hasMoreElements()) {
			CommPortIdentifier actualPuertoID = puertoE.nextElement();
			PORTS.offer(actualPuertoID.getName());
		}
		
		String Ps[]=new String[PORTS.size()];
		
		for(int i=0;i<Ps.length;i++)
			Ps[i]=PORTS.poll();
		
		return Ps;
	}

	public boolean finish(){
		try {
			//Compreba si hay conexón activa..
			if(!active) return true;
			
			//desactiva todo para cierre seguro
			out.flush();
			in.close();
			out.close();
			serial.removeEventListener();
			serial.close();
			active=false;
			
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	public boolean writeln(String datos) {
		//Para escribir una linea en nuestro Serial
		try {
			if(datos.charAt(datos.length()-1)!='\n')
				datos+="\n";
			out.write(datos.getBytes());
			out.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean write(String datos) {
		//Para escribir en nuestro Serial
		try {
			out.write(datos.getBytes());
			out.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	public boolean write(byte[] datos) {
		//Para escribir en nuestro Serial
		try {
			out.write(datos);
			out.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	public boolean write(int dato) {
		//Para escribir en nuestro Serial
		try {
			out.write(dato);
			out.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	public boolean write(byte[] datos, int off,int len) {
		//Para escribir en nuestro Serial
		try {
			out.write(datos, off, len);
			out.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	public String readAvailable(){
		//para recibir datos disponibles desde serial
		byte[] b = new byte[128];
		try {
			
			int data = in.read(b);
			return new String(b,0,data);
		} catch (Exception e) {e.printStackTrace();}
		return "";
	}
	private String readLine() {
		//para recibir lineas desde Serial. 
		String g = "";
		try {
			int data = in.read();
			do {
				g += (char) data;
				data = in.read();
			}while (data != -1 && data != 10 && data != 13);
		} catch (Exception e) {
			e.printStackTrace();
		}
		g=g.replaceAll("\r", "");
		g=g.replaceAll("\n", "");
		return g;
	}
	
	public char readChar(){	
		//para recibir caracteres desde Serial. 
		return (char)readByte();
	}
	public byte readByte(){
		//para recibir bytes desde Serial. 
		byte g ='0';
		try {
			int data = in.read();
			g =  (byte) data;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return g;
	}
	public byte[] readByteArray(){
		//para recibir datos disponibles desde serial
		try {
			byte[] b = new byte[in.available()];
			for(int i=0;i<b.length;i++){
				b[i]=(byte) in.read();
			}
			return b;
		} catch (Exception e) {e.printStackTrace();}
		return null;
	}
	
	public String getPORTNAME(){
		return PORT;
	}
	public String getVersion(){
		return VERSION;
	}
	public String getPortName(){
		return this.PORT;
	}
	public int getDataRate(){
		return this.DATA_RATE;
	}
	public int getRecibir(){
		return this.RECEIVE;
	}
	private Object getPortEvent(){
		return this.event;
	}
	
	public boolean changuePortName(String PORT){
		if(!active) return false;
		this.PORT=PORT;
		return beginPro(PORT,getDataRate(),getPortEvent());
	}
	public boolean changueDataRate(int DR){
		if(!active) return false;
		this.DATA_RATE=DR;
		return beginPro(getPortName(),DR,getPortEvent());
	}
	

	public static void myMain1(){
		final SerialAdmin g=new SerialAdmin();
		
		String[] gg=g.getPorts();
		
		System.out.println("* Puertos Detectados ");
		for(int i=0;i<gg.length;i++)
			System.out.println(gg[i]);

		//if ( gg.length <150 )   return ;
		
		String nombrePuerto="COM7";
		//String nombrePuerto="COM3";
		//String nombrePuerto="/dev/ttyACM0";

		boolean x=g.begin(nombrePuerto, 250000, new StringAvailableListener(){
			public void onStringAvailable(String data) {
				System.out.print(data);	
			}
		});
		
		if(x){
			System.out.println("Abrio correctamente");
			System.out.println("Datos desde "+nombrePuerto+" : \n--------");
		}else{
			System.out.println("Algo paso");
			System.exit(0);
		}
		
		Thread t=new Thread(new Runnable(){
			//@Override
			public void run() {
				try{Thread.sleep(12000*6);	}catch(Exception e){}
				
				if(g.finish())
					System.out.println("\nCerro correctamente");
				else
					System.out.println("\nno cerro");
			}});
		
		t.start();
	}
	
	public static void myMain2()throws Exception{
		
		final SerialAdmin serial=new SerialAdmin();
		
		String nombrePuerto="COM10";
		//String nombrePuerto="/dev/ttyACM0";
		String file1="C:/Tools/fastTesting/file/doc1.txt";
		String file2="C:/Tools/fastTesting/file/doc2.txt";
		
		final InputStream in=new FileInputStream(file1);
		
		if(new File(file2).exists())
			new File(file2).delete();
			
		final FileOutputStream file=new FileOutputStream(file2);
		
		boolean x=serial.begin(nombrePuerto, 38400, new ByteArrayAvailableListener(){

			public void onByteArrayAvailable(byte[] data) {
				try {
					file.write(data);
				} catch (IOException e) {	e.printStackTrace();}
			}});
		
		if(!x)	{
			System.out.println("Algo salio mal");
			System.exit(1);
		}
		
		try{
			System.out.println("comenzo conexión,, estabilizando ...");
			Thread.sleep(2200);
			System.out.println("Comienza envio de archivo");
			
			int c = in.read();
			while(c!=-1){
				serial.write(c);
				c=in.read();
			}

			System.out.println("Se termino de escribir");
			Thread.sleep(20);
			
			serial.finish();
			in.close();
			file.close();
			
		}catch(Exception e){e.printStackTrace();}

	}
	
	public static void myMain3() throws Exception{
		Scanner scn=new Scanner(System.in);
		final SerialAdmin serial=new SerialAdmin();
		final SerialAdmin serial2=new SerialAdmin();
		
		String[] gg=serial.getPorts();
		System.out.println("* Puertos Detectados ");
		for(int i=0;i<gg.length;i++)
			System.out.println(gg[i]);
		
		System.exit(0);
		
		//estos son los puertos que vamos a utilizar segun la plataforma :3 
		//String nombrePuerto1="COM10";
		//String nombrePuerto2="COM9";
		String nombrePuerto1="/dev/ttyUSB0";
		String nombrePuerto2="/dev/ttyUSB1";
		//String nombrePuerto2="/dev/ttyACM0";

		boolean x=serial.begin(nombrePuerto1, 9600, new LineAvailableListener(){
			public void onLineAvailable(String line) {	System.out.println("desde pic16f887:\t"+line);	}
		});
		boolean y=serial2.begin(nombrePuerto2, 9600, new LineAvailableListener(){
			public void onLineAvailable(String line) {	System.out.println("desde pic16f628a:\t"+line);	}
		});
		
		//boolean y=true;
		if(x&&y){
			System.out.println("Los puertos "+nombrePuerto1+" y "+nombrePuerto2+" abrieron correctamente.");
			System.out.print("Las Lineas se enviaran a ambos pic, ellos deben replicar el mensaje y devolverlo:\n >> ");
		}else{	System.out.println("Algo salio mal");	System.exit(0);	}
		String d=scn.nextLine();
		while(!d.contains("exit")){
			serial.writeln(d);
			serial2.writeln(d);
			Thread.sleep(250);
			System.out.print(" >> ");
			d=scn.nextLine();
		}
		scn.close();
		serial2.finish();
		serial.finish();
		System.out.println("Se termino conexiones Satisfactoriamente");
	}

	public static void main(String args[]){
		System.out.println(SerialAdmin.VERSION);
		myMain1();
		try{
			//myMain2();
			//myMain3();
		}catch(Exception e){e.printStackTrace();}
		
	}
	
	
}


