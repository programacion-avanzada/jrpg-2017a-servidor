package mensajeriaServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import mensajeria.PaquetePersonaje;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class PaqueteCreacionPJ extends EscuchaCliente implements Paquete {

	public PaqueteCreacionPJ(String ip, Socket socket, ObjectInputStream entrada, ObjectOutputStream salida) {
		super(ip, socket, entrada, salida);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String ejecutar() {
		// Casteo el paquete personaje
		paquetePersonaje = (PaquetePersonaje) (gson.fromJson(cadenaLeida, PaquetePersonaje.class));
		
		// Guardo el personaje en ese usuario
		Servidor.getConector().registrarPersonaje(paquetePersonaje, paqueteUsuario);
		
		// Le envio el id del personaje
		try {
			salida.writeObject(gson.toJson(paquetePersonaje, paquetePersonaje.getClass()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;

	}

}
