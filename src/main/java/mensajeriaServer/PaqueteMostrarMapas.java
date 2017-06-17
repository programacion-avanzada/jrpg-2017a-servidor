package mensajeriaServer;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import mensajeria.PaquetePersonaje;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class PaqueteMostrarMapas extends EscuchaCliente implements Paquete {

	public PaqueteMostrarMapas(String ip, Socket socket, ObjectInputStream entrada, ObjectOutputStream salida) {
		super(ip, socket, entrada, salida);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void ejecutar() {
		// Indico en el log que el usuario se conecto a ese mapa
		paquetePersonaje = (PaquetePersonaje) gson.fromJson(cadenaLeida, PaquetePersonaje.class);
		Servidor.log.append(socket.getInetAddress().getHostAddress() + " ha elegido el mapa " + paquetePersonaje.getMapa() + System.lineSeparator());
		System.out.println("funca guacho! ");
	}

}
