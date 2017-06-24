package mensajeriaServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import mensajeria.PaquetePersonaje;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class PaqueteActualizarPersonaje extends EscuchaCliente implements Paquete {

	public PaqueteActualizarPersonaje(String ip, Socket socket, ObjectInputStream entrada, ObjectOutputStream salida) {
		super(ip, socket, entrada, salida);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String ejecutar() {
		paquetePersonaje = (PaquetePersonaje) gson.fromJson(cadenaLeida, PaquetePersonaje.class);
		Servidor.getConector().actualizarPersonaje(paquetePersonaje);
		
		Servidor.getPersonajesConectados().remove(paquetePersonaje.getId());
		Servidor.getPersonajesConectados().put(paquetePersonaje.getId(), paquetePersonaje);

		for(EscuchaCliente conectado : Servidor.getClientesConectados()) {
			try {
				conectado.getSalida().writeObject(gson.toJson(paquetePersonaje));
			} catch (IOException e) {
				Servidor.log.append("Error al actualizar personaje" + System.lineSeparator());
				e.printStackTrace();
			}
		}
		return null;
	}

}
