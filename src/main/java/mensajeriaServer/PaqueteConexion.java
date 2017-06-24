package mensajeriaServer;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import mensajeria.PaqueteMovimiento;
import mensajeria.PaquetePersonaje;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class PaqueteConexion extends EscuchaCliente implements Paquete {

	public PaqueteConexion(String ip, Socket socket, ObjectInputStream entrada, ObjectOutputStream salida) {
		super(ip, socket, entrada, salida);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String ejecutar() {
		paquetePersonaje = (PaquetePersonaje) (gson.fromJson(cadenaLeida, PaquetePersonaje.class)).clone();

		Servidor.getPersonajesConectados().put(paquetePersonaje.getId(), (PaquetePersonaje) paquetePersonaje.clone());
		Servidor.getUbicacionPersonajes().put(paquetePersonaje.getId(), (PaqueteMovimiento) new PaqueteMovimiento(paquetePersonaje.getId()).clone());
		
		synchronized(Servidor.atencionConexiones){
			Servidor.atencionConexiones.notify();
		}	
		
		return null;
	}

}
