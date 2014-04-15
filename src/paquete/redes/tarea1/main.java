package paquete.redes.tarea1;

import java.net.*;
import java.io.*;
import java.util.*;

public class main {

	public static void main(String[] args){
		
		//inicia el servidor
		Servidor servidor = new Servidor();
		servidor.iniciar_servidor();
		
	}
}
