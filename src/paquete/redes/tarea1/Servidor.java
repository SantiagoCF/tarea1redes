package paquete.redes.tarea1;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

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
	private String datos_contacto;
	private int puerto = 8013;
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
				metodo = entrada.next();
				System.out.println(metodo);
				
				//Recepcion del objeto 
				objeto = "." + entrada.next();
				
				//Recepcion de la version del protocolo
				version = entrada.next();
				
				// si el metodo de entrada es POST
				if(metodo.equals("POST")){
					
					//si el archivo existe, envia los datos al cliente
					if((archivo = new FileInputStream(objeto)) != null){
						
						if(objeto.length() > 2){
							while((bytes = archivo.read(buffer)) != -1){
								socket.getOutputStream().write(buffer, 0, bytes);
							}
						}
					}
					
					// en caso contrario envia un error
					else{
						System.out.println("Error archivo\n");
						salida.println(version + " 404 Not Found");
						salida.println();
					}
					
					//cierra el socket
					socket.close();
					archivo.close();
					
					//expresion regular para buscar los datos del nuevo contacto
					Boolean encontrado = false;
					Pattern ER = Pattern.compile("^nom=");
					while(!encontrado){
						datos_contacto = entrada.next();
						Matcher mat = ER.matcher(datos_contacto);
						
						//si encuentra la informacion del contacto, guarda solo los datos necesarios
						if(mat.find()){
							
							encontrado = true;
							
							//quita del String los nombres de las variables y los "=" y "&", dejando solo los datos necesarios
							datos_contacto = datos_contacto.replaceAll("\\bnom=\\b", "");
							datos_contacto = datos_contacto.replaceAll("\\b&dir=\\b", " ");
							datos_contacto = datos_contacto.replaceAll("\\b&puer=\\b", " ");
							agregar_contacto(datos_contacto);
						}

					}		
					
					entrada.close();
					salida.close();					
				}
				
				//si el metodo es GET 
				else{
					//si el archivo existe, envia los datos al cliente
					if((archivo = new FileInputStream(objeto)) != null){
						
						if(objeto.length() > 2){
							while((bytes = archivo.read(buffer)) != -1){
								socket.getOutputStream().write(buffer, 0, bytes);
							}
						}
					}
					
					// en caso contrario envia un error
					else{
						System.out.println("Error archivo\n");
						salida.println(version + " 404 Not Found");
						salida.println();
					}
				
					//Cerramos la conexion con el cliente
					entrada.close();
					salida.close();
					socket.close();
					archivo.close();
				}
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
			System.out.println("contacto agregado exitosamente:\n" + contacto);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	//retorna una lista con los datos de cada contacto agregado
	public String[] cargar_contactos(){
		
		File archivo = null;
		FileReader arch = null;
		BufferedReader buffer = null;
		String linea = new String();
		String listaContactos[] = new String[200];
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
