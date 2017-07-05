package mensajeriaServer;

import mensajeria.PaquetePersonaje;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class PaqueteMostrarMapas extends mensajeriaServer.Paquete {

	public PaqueteMostrarMapas(EscuchaCliente escuchador) {
		super(escuchador);
	}

	@Override
	public void ejecutar() {
		escuchador.paquetePersonaje = (PaquetePersonaje) escuchador.gson.fromJson(escuchador.cadenaLeida, PaquetePersonaje.class);
		Servidor.log.append(escuchador.socket.getInetAddress().getHostAddress() + " ha elegido el mapa " + escuchador.paquetePersonaje.getMapa() + System.lineSeparator());
	}

}
