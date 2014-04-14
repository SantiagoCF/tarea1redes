package paquete.redes.tarea1;

import java.net.*;
import java.io.*;

public class Servidor {

	//variables de clase
	private ServerSocket S_socket;
	private Socket socket;
	private DataInputStream entrada;
	private DataOutputStream salida;
	private String mensajeRecibido;
		
	//contructor
	public void iniciar_servidor(){
		
		try{
			//creamos el socket servidor en el puerto 4000
			S_socket = new ServerSocket(4003);
			socket = new Socket();
				
			System.out.println("Espera cliente:");
			//Iniciamos el socket... Espera al cliente
			
			while(true){
				
				socket = S_socket.accept();
				
				// CLIENTE SE CONECTA //
				System.out.println("Cliente conectado.");
				//Crea los canales de entrada y salida			 
				entrada = new DataInputStream(socket.getInputStream());
				salida = new DataOutputStream(socket.getOutputStream());
				System.out.println("Conexion confirmada!");
							
				//Recepcion de mensaje		 
				mensajeRecibido = entrada.readUTF();			
						
				entrada.close();
				salida.close();
				//Cerramos la conexion con el cliente
				socket.close();
			
				System.out.println("Cierre de conexion!!!");
			}
		}catch(Exception e ){		 
				System.out.println("Error: "+e.getMessage());
		}		
	}
	
	public void agregar_contacto(String contacto){
		
		FileWriter archivo = null;
		PrintWriter arch = null;
		
		try {
			
			//abre el archivo para escritura
			archivo = new FileWriter("contactos.txt", true);
			arch = new PrintWriter(archivo);
			
			//escribe un dato por linea
			arch.println(contacto);
			
			//cierra el archivo
			arch.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public String[] cargar_contactos(){
		
		File archivo = null;
		FileReader arch = null;
		BufferedReader buffer = null;
		String linea = new String();
		String listaContactos[] = new String[100];
		int i = 0;
		
		try {
			
			//abre el archivo para escritura
			archivo = new File("contactos.txt");
			arch = new FileReader(archivo);
			buffer = new BufferedReader(arch);
			
			
			//Leemos el archivo
			while((linea = buffer.readLine()) != null){
				
				listaContactos[i] = linea;
				System.out.println(i);
				i++;
			}
			//cierra el archivo
			arch.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return listaContactos;
	}
}
