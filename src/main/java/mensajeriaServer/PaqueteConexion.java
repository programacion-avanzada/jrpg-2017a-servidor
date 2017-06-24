package mensajeriaServer;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import mensajeria.PaqueteMovimiento;
import mensajeria.PaquetePersonaje;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class PaqueteConexion extends mensajeriaServer.Paquete{

	public PaqueteConexion(EscuchaCliente escuchador) {
		super(escuchador);
	}

	@Override
	public void ejecutar() {
		escuchador.paquetePersonaje = (PaquetePersonaje) (escuchador.gson.fromJson(escuchador.cadenaLeida, PaquetePersonaje.class)).clone();

		Servidor.getPersonajesConectados().put(escuchador.paquetePersonaje.getId(), (PaquetePersonaje) escuchador.paquetePersonaje.clone());
		Servidor.getUbicacionPersonajes().put(escuchador.paquetePersonaje.getId(), (PaqueteMovimiento) new PaqueteMovimiento(escuchador.paquetePersonaje.getId()).clone());
		
		synchronized(Servidor.atencionConexiones){
			Servidor.atencionConexiones.notify();
		}	
	}

}
