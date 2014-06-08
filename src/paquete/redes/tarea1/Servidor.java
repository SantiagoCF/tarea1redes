package paquete.redes.tarea1;

import java.net.*;
import java.awt.Desktop;
import java.io.*;
import java.util.*;
import java.util.regex.*;


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
	private int puerto = 0;
	byte[] buffer = new byte[1024];
	private int bytes;
	private FileInputStream archivo = null;
	private Boolean existe_temporal = false;

	//variables para la conexion con servidor tcp/udp
	private ChatClient chatClient;


	public void iniciar_servidor(){

		try{
			//creamos el socket servidor en el puerto 4000
			S_socket = new ServerSocket(0);
			puerto = S_socket.getLocalPort();
			System.out.print("Puerto inicializado: "+puerto +"\n");
			socket = new Socket();
			
			//abrimos la web instantaneamente
			if(Desktop.isDesktopSupported())
			{
			  Desktop.getDesktop().browse(new URI("http://localhost:"+puerto+"/index.html"));
			}
			
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
				//System.out.println("Conexion confirmada!");

				//Recepcion del metodo (get o post)	
				metodo = entrada.next();
				//System.out.println(metodo);

				//Recepcion del objeto 
				objeto = "." + entrada.next();
				//System.out.println(objeto);

				//Recepcion de la version del protocolo
				version = entrada.next();

				// si el metodo de entrada es POST
				if(metodo.equals("POST")){

					if(objeto.equals("./login_ok.html")){
						String nombreUsuario = null;

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
								nombreUsuario=(datos_contacto);
							}

						}

						chatClient = new ChatClient();
						chatClient.iniciar_cliente(nombreUsuario);

					}

					if(objeto.equals("./chat.html")){
						String msg = null;

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
								datos_contacto = datos_contacto.replaceAll("\\b&contenido=\\b", " ");
								msg=(datos_contacto);
							}

						}


						ChatClient.enviar_mensaje(msg + "\n");

					}

					if(objeto.equals("./agregar_contacto_ok.html")){
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
					}

					entrada.close();
					salida.close();					
				}

				//si el metodo es GET 
				else{

					//si el el html es "ver_contacto.html"
					if(objeto.equals("./ver_contacto.html")){


						//variables para la lectura del html "ver_contactos.html"
						File original = null;
						FileReader arch_original = null;
						BufferedReader buffer = null;
						String linea = new String();

						//variables para escritura de html temporal
						FileWriter temporal = null;
						PrintWriter arch_temporal = null;

						//cargamos los contactos del archivo "contactos.txt"
						String listaContactos[] = cargar_contactos();

						int i;

						try {
							//System.out.println("Entra al try");
							//abre el html para lectura
							original = new File(objeto);
							arch_original = new FileReader(original);
							buffer = new BufferedReader(arch_original);

							//crea el html temporal para escritura
							temporal = new FileWriter("temporal.html", true);
							arch_temporal = new PrintWriter(temporal);

							//Leemos el archivo y escribimos el html temporal con los datos cargados
							while((linea = buffer.readLine()) != null){


								//System.out.println(linea);
								//copia la linea leida del html original								
								arch_temporal.println(linea);

								//si "linea" es igual a "<tbody>", escribe los datos de los contactos
								if(linea.equals("                        <tbody>")){

									int contactos = cantidad_contactos();

									for(i=0; i<contactos; i++){

										//System.out.println("Entra al for");

										//variable para separar el string en 3 datos 
										StringTokenizer dat_separados = new StringTokenizer(listaContactos[i]);

										arch_temporal.println("<tr>");
										arch_temporal.print("<td>");
										arch_temporal.print(i);
										arch_temporal.println("</td>");
										arch_temporal.print("<td>");
										arch_temporal.print(dat_separados.nextToken());
										arch_temporal.println("</td>");
										arch_temporal.print("<td>");
										arch_temporal.print(dat_separados.nextToken());
										arch_temporal.println("</td>");
										arch_temporal.print("<td>");
										arch_temporal.print(dat_separados.nextToken());
										arch_temporal.println("</td>");
										arch_temporal.println("</tr>");

										//System.out.println(i+" "+dat_separados.nextToken()+" "+dat_separados.nextToken()+" "+dat_separados.nextToken());
									}
								}								
							}

							//cierra los archivos
							arch_original.close();
							arch_temporal.close();

							//reemplazamos el valor de la variable "objeto" por el archivo "temporal.html"
							objeto = "./temporal.html";
							existe_temporal = true;

						} catch (IOException e) {
							e.printStackTrace();
						}

					}

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

					//si se creó un temporal, se elimina
					if(existe_temporal){

						File temporal = new File("temporal.html"); 
						temporal.delete(); 
					}
				}
				//System.out.println("Cierre de conexion!!!");
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
				i++;
			}
			//cierra el archivo
			arch.close();

		} catch (IOException e)

		{
			e.printStackTrace();
		}

		return listaContactos;
	}

	//retorna una lista con los datos de cada contacto agregado
	public int cantidad_contactos(){

		File archivo = null;
		FileReader arch = null;
		BufferedReader buffer = null;
		String linea = new String();
		int contactos = 0;

		try {

			//abre el archivo para escritura
			archivo = new File("contactos.txt");
			arch = new FileReader(archivo);
			buffer = new BufferedReader(arch);

			//Leemos el archivo
			while((linea = buffer.readLine()) != null){
				contactos++;
			}
			//cierra el archivo
			arch.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return contactos;
	}
}

