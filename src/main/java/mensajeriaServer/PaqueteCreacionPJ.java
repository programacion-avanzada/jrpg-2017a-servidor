package mensajeriaServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import mensajeria.PaquetePersonaje;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class PaqueteCreacionPJ extends mensajeriaServer.Paquete{

	public PaqueteCreacionPJ(EscuchaCliente escuchador) {
		super(escuchador);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void ejecutar() {
		// Casteo el paquete personaje
		escuchador.paquetePersonaje = (PaquetePersonaje) (escuchador.gson.fromJson(escuchador.cadenaLeida, PaquetePersonaje.class));
		
		// Guardo el personaje en ese usuario
		Servidor.getConector().registrarPersonaje(escuchador.paquetePersonaje, escuchador.paqueteUsuario);
		
		// Le envio el id del personaje
		try {
			escuchador.salida.writeObject(escuchador.gson.toJson(escuchador.paquetePersonaje, escuchador.paquetePersonaje.getClass()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
