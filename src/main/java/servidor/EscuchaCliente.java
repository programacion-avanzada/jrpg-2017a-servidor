package servidor;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

import com.google.gson.Gson;

import estados.Estado;
import mensajeria.*;

public class EscuchaCliente extends Thread {

	public final Socket socket;
	public final ObjectInputStream entrada;
	public final ObjectOutputStream salida;
	public int idPersonaje;
	public final Gson gson = new Gson();
	
	public PaquetePersonaje paquetePersonaje;
	public PaqueteMovimiento paqueteMovimiento;
	public PaqueteBatalla paqueteBatalla;
	public PaqueteAtacar paqueteAtacar;
	public PaqueteFinalizarBatalla paqueteFinalizarBatalla;
	
	private PaqueteDeMovimientos paqueteDeMovimiento;
	private PaqueteDePersonajes paqueteDePersonajes;
	public String cadenaLeida;
	
	public Paquete paquete;
	public Paquete paqueteSv = new Paquete(null, 0);
	public PaqueteUsuario paqueteUsuario = new PaqueteUsuario();
	
	public EscuchaCliente(String ip, Socket socket, ObjectInputStream entrada, ObjectOutputStream salida) {
		this.socket = socket;
		this.entrada = entrada;
		this.salida = salida;
		paquetePersonaje = new PaquetePersonaje();
	}

	public void run() {
		try {

			paqueteSv = new Paquete(null, 0);
			paqueteUsuario = new PaqueteUsuario();

			cadenaLeida = (String) entrada.readObject();
			String nombrePaquete;
			Class<?> clazz;
			Constructor<?> ctor;
			mensajeriaServer.Paquete pqte;
			/*Mejorar la selección y despacho de mensajes
El método run de la clase servidor.EscuchaCliente, del proyecto Servidor, tiene una gran estructura switch/case. 
Debemos eliminarla, utilizando polimorfismo de mensajería.
Una vez hecho esto, es trivial remover el switch/case del método run de la clase cliente.Cliente. Háganlo también. 
En ese mismo proyecto, encontrará más switch/case en cliente.EscuchaMensajes. También deseamos reemplazar esa estructura.
Por lo tanto, se desean eliminar todos los switch/case que están relacionados con el polimorfismo de mensajería.*/
		
			while ((paquete = gson.fromJson(cadenaLeida, Paquete.class)).getComando() != Comando.DESCONECTAR){
				nombrePaquete = "mensajeriaServer.Paquete" + Comando.getNombre(paquete.getComando());
				clazz = Class.forName(nombrePaquete);
				ctor = clazz.getConstructor(this.getClass());
				pqte = (mensajeriaServer.Paquete) ctor.newInstance(new Object[] { this });
				pqte.ejecutar();				
				cadenaLeida = (String) entrada.readObject();
			}

			entrada.close();
			salida.close();
			socket.close();

			Servidor.getPersonajesConectados().remove(paquetePersonaje.getId());
			Servidor.getUbicacionPersonajes().remove(paquetePersonaje.getId());
			Servidor.getClientesConectados().remove(this);

			for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
				paqueteDePersonajes = new PaqueteDePersonajes(Servidor.getPersonajesConectados());
				paqueteDePersonajes.setComando(Comando.CONEXION);
				//conectado.salida.writeObject(gson.toJson(paqueteDePersonajes, PaqueteDePersonajes.class));
			}

			Servidor.log.append(paquete.getIp() + " se ha desconectado." + System.lineSeparator());

		} catch (IOException | ClassNotFoundException e) {
			Servidor.log.append("Error de conexion: " + e.getMessage() + System.lineSeparator());
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public ObjectInputStream getEntrada() {
		return entrada;
	}
	
	public ObjectOutputStream getSalida() {
		return salida;
	}
	
	public PaquetePersonaje getPaquetePersonaje(){
		return paquetePersonaje;
	}
	
	public int getIdPersonaje() {
		return idPersonaje;
	}
}

