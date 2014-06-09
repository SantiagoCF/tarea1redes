package paquete.redes.tarea1;

/* ChatClient.java */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;

import java.net.Socket;
import java.net.UnknownHostException;

public class ChatClient {
	private static int port = 1001; /* port to connect to */
	private static String host = "localhost"; /* host to connect to */
	
	private static String nick=null;
	private static PrintWriter out;
	private static BufferedReader in;
	/**
	 * Read in a nickname from stdin and attempt to authenticate with the 
	 * server by sending a NICK command to @out. If the response from @in
	 * is not equal to "OK" go back and read a nickname again
	 */
	private static String getNick(BufferedReader in, 
	                              PrintWriter out, String Usuario) throws IOException {
	    out.println("NICK " + Usuario);
	    String serverResponse = in.readLine();
	    System.out.print("RESPUESTA SERVIDOR getnick : "+serverResponse);
	    if ("SERVER: OK".equals(serverResponse)) return Usuario;
	    System.out.println(serverResponse);
	    return getNick(in, out, Usuario);
	}
	
	public static void enviar_mensaje(String msg){
		System.out.print("ENVIANDO MENSAJE DE: "+ nick + ": "+ msg +"\n");
		out.println("MSG "+ msg);

	}
	
	public void iniciar_cliente (String Usuario) throws IOException {
	
	    Socket server = null;
	
	    try {
	        server = new Socket(host, port);
	    } catch (UnknownHostException e) {
	        System.err.println(e);
	        System.exit(1);
	    }
	
	    /* obtain an output stream to the server... */
	    out = new PrintWriter(server.getOutputStream(), true);
	    /* ... and an input stream */
	    in = new BufferedReader(new InputStreamReader(
	                server.getInputStream()));
	
	    nick = getNick(in, out, Usuario);
	
	    /* create a thread to asyncronously read messages from the server */
	    ServerConn sc = new ServerConn(server);
	    Thread t = new Thread(sc);
	    t.start();
	
	  }
}

class ServerConn implements Runnable {
	private BufferedReader in = null;
	
	public ServerConn(Socket server) throws IOException {
	    /* obtain an input stream from the server */
	    in = new BufferedReader(new InputStreamReader(
	                server.getInputStream()));
	}
	
	public void run() {
	    String msg;
	    try {
	        /* loop reading messages from the server and show them 
	         * on stdout */
	        while ((msg = in.readLine()) != null) {
	        	String[] msg_parts = msg.split(" ", 3);
	            if(msg_parts[0].equals("ENVIADO:")){
	            	crear_archivo(msg_parts[1], msg_parts[2]);
	            }
	        	System.out.println(msg);
	        }
	    } catch (IOException e) {
	        System.err.println(e);
	    }
	}
	
	public void crear_archivo(String destino, String msg){

		FileWriter archivo = null;
		PrintWriter arch = null;

		try {

			//abre el archivo para escritura
			archivo = new FileWriter(destino+".txt", true);
			arch = new PrintWriter(archivo);

			//escribe un dato por linea
			arch.println(msg);

			//cierra el archivo
			arch.close();
			System.out.println("Archivo "+destino+".txt"+" = "+ msg);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
