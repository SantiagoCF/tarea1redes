package paquete.redes.tarea1;

import java.net.*;
import java.io.*;
import java.util.*;

import javax.sound.sampled.Line;

public class Servidor {

	//variables de clase
	private ServerSocket S_socket;
	private Socket socket;
	private Scanner entrada;
	private PrintWriter salida;
	private String objeto;
	private String metodo;
	private String version;
	private String mensaje;
	private int puerto = 8012;
	byte[] buffer = new byte[1024];
	private int bytes;
	private FileInputStream archivo = null;
		
	public void iniciar_servidor(){
		
		try{
			//creamos el socket servidor en el puerto 4000
			S_socket = new ServerSocket(puerto);
			socket = new Socket();
	
			while(true){
				
				//Iniciamos el socket... Espera al cliente
				System.out.println("Espera cliente:");
				socket = S_socket.accept();
				
				// CLIENTE SE CONECTA //
				System.out.println("Cliente conectado.");
				
				//aseguramos que el fin de linea sea el correcto
				System.setProperty("line.separator", "\r\n");
				
				//Crea los canales de entrada y salida			 
				entrada = new Scanner(socket.getInputStream());
				salida = new PrintWriter(socket.getOutputStream());
				System.out.println("Conexion confirmada!");
							
				//Recepcion del metodo (get o post)	
				//mensaje = entrada.toString();
				//System.out.println("/////"+mensaje+"///////");
				metodo = entrada.next();
				System.out.println(metodo);
				
				//Recepcion del objeto 
				objeto = "." + entrada.next();
				System.out.println(objeto);
				
				//Recepcion de la version del protocolo
				version = entrada.next();
				System.out.println(version);
				
				//ESTO LO HICE SOLO PARA PROBAR QUE DATOS TENIA LA CABECERA DEL MENSAJE Q MANDABA EL BROWSER... SON 13 (lo probé en Chrome)
				if(metodo.equals("POST")){
					System.out.println("2"+entrada.next() + " " + entrada.next() + "\n3" + entrada.next() + " " + entrada.next());
					System.out.println("4"+entrada.next() + " " + entrada.next() + "\n5" + entrada.next() + " " + entrada.next());
					System.out.println("6"+entrada.next() + " " + entrada.next() + "\n7" + entrada.next() + " " + entrada.next());
					System.out.print("8"+entrada.next() + " " + entrada.next() + "\n9" + entrada.next() + " " + entrada.next());
					System.out.print(" "+entrada.next() + " " + entrada.next() + entrada.next() + " " + entrada.next());
					System.out.print(" " + entrada.next() + " " + entrada.next() + entrada.next() + "\n10" + entrada.next());
					System.out.print(" " + entrada.next() + "\n11" + entrada.next() + " " + entrada.next() + "\n12" + entrada.next());
					System.out.println(" " + entrada.next() + "\n13" + entrada.next() + " " + entrada.next());
					
					// HASTA AQUI IMRPIME TODOS LODA DATOS DE LA CABECERA
					
					//ACA SE QUEDA PEGADO
					System.out.println("//DATOS//");
					Calendar fecha = Calendar.getInstance();
					
					salida.println(version + " 200 OK");
					
					//el siguiente entrada.next(); devuelve los datos q ingresas al form de la siguiente forma 
					//ej: nom=Pablo&dir=123&puer=111
					System.out.println(entrada.next());
					System.out.println("//DATOS//");

				}

				//si el archivo existe, envia los datos al cliente
				if((archivo = new FileInputStream(objeto)) != null){
					System.out.println("Existe el objeto!!");
					if(objeto.length() > 2){
						while((bytes = archivo.read(buffer)) != -1){
							socket.getOutputStream().write(buffer, 0, bytes);
						}
					}
				}
				// en caso contrario envia un error
				else{
					System.out.println("NO!!!Existe el objeto!!");
					System.out.println("Error archivo\n");
					salida.println(version + " 404 Not Found");
					salida.println();
				}
				
				//Cerramos la conexion con el cliente
				entrada.close();
				salida.close();
				socket.close();
				archivo.close();
				
				System.out.println("Cierre de conexion!!!");
			}
		}catch(Exception e){
			System.out.println("Error:" + e.getMessage());
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
