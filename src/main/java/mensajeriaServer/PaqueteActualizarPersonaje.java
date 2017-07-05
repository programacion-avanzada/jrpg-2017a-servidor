package mensajeriaServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import mensajeria.PaquetePersonaje;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class PaqueteActualizarPersonaje extends mensajeriaServer.Paquete {

	public PaqueteActualizarPersonaje(EscuchaCliente escuchador) {
		super(escuchador);
	}

	@Override
	public void ejecutar() {
		escuchador.paquetePersonaje = (PaquetePersonaje) escuchador.gson.fromJson(escuchador.cadenaLeida, PaquetePersonaje.class);
		Servidor.getConector().actualizarPersonaje(escuchador.paquetePersonaje);
		
		//Servidor.getPersonajesConectados().remove(escuchador.paquetePersonaje.getId());
		Servidor.getPersonajesConectados().put(escuchador.paquetePersonaje.getId(), escuchador.paquetePersonaje);

		for(EscuchaCliente conectado : Servidor.getClientesConectados()) {
			try {
				conectado.getSalida().writeObject(escuchador.gson.toJson(escuchador.paquetePersonaje));
			} catch (IOException e) {
				Servidor.log.append("Error al actualizar personaje" + System.lineSeparator());
				e.printStackTrace();
			}
		}
	}

}
