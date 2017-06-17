package mensajeriaServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import servidor.EscuchaCliente;
import servidor.Servidor;

public class PaqueteSalir extends EscuchaCliente implements Paquete{

	public PaqueteSalir(String ip, Socket socket, ObjectInputStream entrada, ObjectOutputStream salida) {
		super(ip, socket, entrada, salida);
	}

	@Override
	public void ejecutar() {
		// Cierro todo
		try {
			entrada.close();
			salida.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		// Lo elimino de los clientes conectados
		Servidor.getClientesConectados().remove(this);
		
		// Indico que se desconecto
		Servidor.log.append(paquete.getIp() + " se ha desconectado." + System.lineSeparator());
		
	}

}
