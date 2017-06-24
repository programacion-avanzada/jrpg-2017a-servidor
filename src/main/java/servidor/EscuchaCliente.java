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
		System.out.println("escuchaCliente creado");
	}

	public void run() {
		try {

			paqueteSv = new Paquete(null, 0);
			paqueteUsuario = new PaqueteUsuario();

			cadenaLeida = (String) entrada.readObject();
			/*
			 * Mejorar la selección y despacho de mensajes

El método run de la clase servidor.EscuchaCliente, del proyecto Servidor, tiene una gran estructura switch/case. 
Debemos eliminarla, utilizando polimorfismo de mensajería.
Una vez hecho esto, es trivial remover el switch/case del método run de la clase cliente.Cliente. Háganlo también. 
En ese mismo proyecto, encontrará más switch/case en cliente.EscuchaMensajes. También deseamos reemplazar esa estructura.

Por lo tanto, se desean eliminar todos los switch/case que están relacionados con el polimorfismo de mensajería.
			 */
		
			while ((paquete = gson.fromJson(cadenaLeida, Paquete.class)).getComando() != Comando.DESCONECTAR){
				System.out.println("Paquete: " + paquete);
				
				System.out.println("paquete getcomando: " + paquete.getComando());
				if(paquete.getComando() == Comando.INICIOSESION){
					String nombrePaquete = "mensajeriaServer.Paquete" + Comando.getNombre(paquete.getComando());
					Class<?> clazz = Class.forName(nombrePaquete);
					Constructor<?> ctor = clazz.getConstructor(this.getClass());
					mensajeriaServer.Paquete pqte = (mensajeriaServer.Paquete) ctor.newInstance(new Object[] { this });
					pqte.ejecutar();
				}
				
				if(paquete.getComando() == Comando.MOSTRARMAPAS){
					String nombrePaquete = "mensajeria.Paquete" + Comando.getNombre(paquete.getComando());
					Class<?> clazz = Class.forName(nombrePaquete);
					Constructor<?> ctor = clazz.getConstructor(this.getClass());
					mensajeriaServer.Paquete pqte = (mensajeriaServer.Paquete) ctor.newInstance(new Object[] { this });
					pqte.ejecutar();
				}
				
				//System.out.println("clase a castear: " + castearAca.getName());
				/*try {
					//hago un nuevo objeto de la clase que obtuve arriba
					Object nuevo = castearAca.newInstance();
					Method meth = nuevo.getClass().getMethod("ejecutar", castearAca.getClasses());
					System.out.println("meth: " + meth.getName());
					//invoco el método ejecutar del nuevo objeto
					//es decir PaqueteHacerCosa.ejecutar();
					String res = (String) meth.invoke(nuevo, null);
				} catch (InstantiationException e) {
					e.printStackTrace();
				}
				
				Paquete paqVacio = new Paquete();
				mensajeria.PaqueteMostrarMapas casteado = (mensajeria.PaqueteMostrarMapas) castearAca.cast(paqVacio);
				
				Class casteada = casteado.getClass();
				System.out.println("clase del casteado: " + casteada.getName());
				
				//paquete = castearAca.cast(paquete);
				Class.forName("servidor."+nombrePaquete).cast(paquete);
				//Object objeto = Class.forName("servidor."+comando).cast(this);
				Method metodo = paquete.getClass().getMethod("ejecutar", null);
				metodo.invoke(paquete, null);
				*/
				
				
				
				switch (paquete.getComando()) {
									
				case Comando.REGISTRO:
					
					// Paquete que le voy a enviar al usuario
					paqueteSv.setComando(Comando.REGISTRO);
					
					paqueteUsuario = (PaqueteUsuario) (gson.fromJson(cadenaLeida, PaqueteUsuario.class)).clone();

					// Si el usuario se pudo registrar le envio un msj de exito
					if (Servidor.getConector().registrarUsuario(paqueteUsuario)) {
						paqueteSv.setMensaje(Paquete.msjExito);
						salida.writeObject(gson.toJson(paqueteSv));
					// Si el usuario no se pudo registrar le envio un msj de fracaso
					} else {
						paqueteSv.setMensaje(Paquete.msjFracaso);
						salida.writeObject(gson.toJson(paqueteSv));
					}
					break;

				case Comando.CREACIONPJ:
					
					// Casteo el paquete personaje
					paquetePersonaje = (PaquetePersonaje) (gson.fromJson(cadenaLeida, PaquetePersonaje.class));
					
					// Guardo el personaje en ese usuario
					Servidor.getConector().registrarPersonaje(paquetePersonaje, paqueteUsuario);
					
					// Le envio el id del personaje
					salida.writeObject(gson.toJson(paquetePersonaje, paquetePersonaje.getClass()));
					
					break;

				case Comando.SALIR:
					
					// Cierro todo
					entrada.close();
					salida.close();
					socket.close();
					
					// Lo elimino de los clientes conectados
					Servidor.getClientesConectados().remove(this);
					
					// Indico que se desconecto
					Servidor.log.append(paquete.getIp() + " se ha desconectado." + System.lineSeparator());
					
					return;

				case Comando.CONEXION:
					paquetePersonaje = (PaquetePersonaje) (gson.fromJson(cadenaLeida, PaquetePersonaje.class)).clone();

					Servidor.getPersonajesConectados().put(paquetePersonaje.getId(), (PaquetePersonaje) paquetePersonaje.clone());
					Servidor.getUbicacionPersonajes().put(paquetePersonaje.getId(), (PaqueteMovimiento) new PaqueteMovimiento(paquetePersonaje.getId()).clone());
					
					synchronized(Servidor.atencionConexiones){
						Servidor.atencionConexiones.notify();
					}
					
					break;

				case Comando.MOVIMIENTO:					
					
					paqueteMovimiento = (PaqueteMovimiento) (gson.fromJson((String) cadenaLeida, PaqueteMovimiento.class));
					
					Servidor.getUbicacionPersonajes().get(paqueteMovimiento.getIdPersonaje()).setPosX(paqueteMovimiento.getPosX());
					Servidor.getUbicacionPersonajes().get(paqueteMovimiento.getIdPersonaje()).setPosY(paqueteMovimiento.getPosY());
					Servidor.getUbicacionPersonajes().get(paqueteMovimiento.getIdPersonaje()).setDireccion(paqueteMovimiento.getDireccion());
					Servidor.getUbicacionPersonajes().get(paqueteMovimiento.getIdPersonaje()).setFrame(paqueteMovimiento.getFrame());
					
					synchronized(Servidor.atencionMovimientos){
						Servidor.atencionMovimientos.notify();
					}
					
					break;

					
				case Comando.BATALLA:
					
					// Le reenvio al id del personaje batallado que quieren pelear
					paqueteBatalla = (PaqueteBatalla) gson.fromJson(cadenaLeida, PaqueteBatalla.class);
					Servidor.log.append(paqueteBatalla.getId() + " quiere batallar con " + paqueteBatalla.getIdEnemigo() + System.lineSeparator());
					
					//seteo estado de batalla
					Servidor.getPersonajesConectados().get(paqueteBatalla.getId()).setEstado(Estado.estadoBatalla);
					Servidor.getPersonajesConectados().get(paqueteBatalla.getIdEnemigo()).setEstado(Estado.estadoBatalla);
					paqueteBatalla.setMiTurno(true);
					salida.writeObject(gson.toJson(paqueteBatalla));
					for(EscuchaCliente conectado : Servidor.getClientesConectados()){
						if(conectado.getIdPersonaje() == paqueteBatalla.getIdEnemigo()){
							int aux = paqueteBatalla.getId();
							paqueteBatalla.setId(paqueteBatalla.getIdEnemigo());
							paqueteBatalla.setIdEnemigo(aux);
							paqueteBatalla.setMiTurno(false);
							conectado.getSalida().writeObject(gson.toJson(paqueteBatalla));
							break;
						}
					}
					
					synchronized(Servidor.atencionConexiones){
						Servidor.atencionConexiones.notify();
					}
					
					break;
					
				case Comando.ATACAR: 
					paqueteAtacar = (PaqueteAtacar) gson.fromJson(cadenaLeida, PaqueteAtacar.class);
					for(EscuchaCliente conectado : Servidor.getClientesConectados()) {
						if(conectado.getIdPersonaje() == paqueteAtacar.getIdEnemigo()) {
							conectado.getSalida().writeObject(gson.toJson(paqueteAtacar));
						}
					}
					break;
					
				case Comando.FINALIZARBATALLA: 
					paqueteFinalizarBatalla = (PaqueteFinalizarBatalla) gson.fromJson(cadenaLeida, PaqueteFinalizarBatalla.class);
					Servidor.getPersonajesConectados().get(paqueteFinalizarBatalla.getId()).setEstado(Estado.estadoJuego);
					Servidor.getPersonajesConectados().get(paqueteFinalizarBatalla.getIdEnemigo()).setEstado(Estado.estadoJuego);
					for(EscuchaCliente conectado : Servidor.getClientesConectados()) {
						if(conectado.getIdPersonaje() == paqueteFinalizarBatalla.getIdEnemigo()) {
							conectado.getSalida().writeObject(gson.toJson(paqueteFinalizarBatalla));
						}
					}
					
					synchronized(Servidor.atencionConexiones){
						Servidor.atencionConexiones.notify();
					}
					
					break;
					
				case Comando.ACTUALIZARPERSONAJE:
					paquetePersonaje = (PaquetePersonaje) gson.fromJson(cadenaLeida, PaquetePersonaje.class);
					Servidor.getConector().actualizarPersonaje(paquetePersonaje);
					
					Servidor.getPersonajesConectados().remove(paquetePersonaje.getId());
					Servidor.getPersonajesConectados().put(paquetePersonaje.getId(), paquetePersonaje);

					for(EscuchaCliente conectado : Servidor.getClientesConectados()) {
						conectado.getSalida().writeObject(gson.toJson(paquetePersonaje));
					}
					
					break;
				
				default:
					break;
				}
				
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
				conectado.salida.writeObject(gson.toJson(paqueteDePersonajes, PaqueteDePersonajes.class));
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

