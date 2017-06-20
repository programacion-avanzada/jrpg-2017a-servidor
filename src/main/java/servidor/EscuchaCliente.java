package servidor;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

import com.google.gson.Gson;

import mensajeria.Comando;
import mensajeria.Paquete;
import mensajeria.PaqueteAtacar;
import mensajeria.PaqueteBatalla;
import mensajeria.PaqueteDeMovimientos;
import mensajeria.PaqueteDePersonajes;
import mensajeria.PaqueteFinalizarBatalla;
import mensajeria.PaqueteMovimiento;
import mensajeria.PaquetePersonaje;
import mensajeria.PaqueteUsuario;

public class EscuchaCliente extends Thread {

	protected final Socket socket;
	protected final ObjectInputStream entrada;
	protected final ObjectOutputStream salida;
	protected int idPersonaje;
	protected final Gson gson = new Gson();
	
	protected PaquetePersonaje paquetePersonaje;
	protected PaqueteMovimiento paqueteMovimiento;
	protected PaqueteBatalla paqueteBatalla;
	protected PaqueteAtacar paqueteAtacar;
	protected PaqueteFinalizarBatalla paqueteFinalizarBatalla;
	
	private PaqueteDeMovimientos paqueteDeMovimiento;
	private PaqueteDePersonajes paqueteDePersonajes;
	protected String cadenaLeida;
	
	protected Paquete paquete;
	protected Paquete paqueteSv = new Paquete(null, 0);
	protected PaqueteUsuario paqueteUsuario = new PaqueteUsuario();
	
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
		
			while (!((paquete = gson.fromJson(cadenaLeida, Paquete.class)).getComando() == Comando.DESCONECTAR)){
						
				String comando = "Paquete" + Comando.getNombre(paquete.getComando());
				
				System.out.println("clase de paquete: " + Paquete.class.getName());
				
				
				//Object objeto = null;
				Class<?> castearAca = Class.forName("servidor."+comando);
				System.out.println("clase a castear: " + castearAca.getName());
				try {
					Object nuevo = castearAca.newInstance();
					System.out.println("clase de nuevo: " + nuevo.getClass());
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Paquete paqVacio = new Paquete();
				PaqueteMostrarMapas casteado = (PaqueteMostrarMapas) castearAca.cast(paqVacio);
				
				Class casteada = casteado.getClass();
				System.out.println("clase del casteado: " + casteada.getName());
				
				//paquete = castearAca.cast(paquete);
				Class.forName("servidor."+comando).cast(paquete);
				//Object objeto = Class.forName("servidor."+comando).cast(this);
				Method metodo = paquete.getClass().getMethod("ejecutar", null);
				metodo.invoke(paquete, null);
				
				/*
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

				case Comando.INICIOSESION:
					paqueteSv.setComando(Comando.INICIOSESION);
					
					// Recibo el paquete usuario
					paqueteUsuario = (PaqueteUsuario) (gson.fromJson(cadenaLeida, PaqueteUsuario.class));
					
					// Si se puede loguear el usuario le envio un mensaje de exito y el paquete personaje con los datos
					if (Servidor.getConector().loguearUsuario(paqueteUsuario)) {
						
						paquetePersonaje = new PaquetePersonaje();
						paquetePersonaje = Servidor.getConector().getPersonaje(paqueteUsuario);
						paquetePersonaje.setComando(Comando.INICIOSESION);
						paquetePersonaje.setMensaje(Paquete.msjExito);
						idPersonaje = paquetePersonaje.getId();
						
						salida.writeObject(gson.toJson(paquetePersonaje));
						
					} else {
						paqueteSv.setMensaje(Paquete.msjFracaso);
						salida.writeObject(gson.toJson(paqueteSv));
					}
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

				case Comando.MOSTRARMAPAS:
					
					// Indico en el log que el usuario se conecto a ese mapa
					paquetePersonaje = (PaquetePersonaje) gson.fromJson(cadenaLeida, PaquetePersonaje.class);
					Servidor.log.append(socket.getInetAddress().getHostAddress() + " ha elegido el mapa " + paquetePersonaje.getMapa() + System.lineSeparator());
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
				*/
				
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

