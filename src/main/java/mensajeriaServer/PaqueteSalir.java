package mensajeriaServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import servidor.EscuchaCliente;
import servidor.Servidor;

public class PaqueteSalir extends mensajeriaServer.Paquete{

	public PaqueteSalir(EscuchaCliente escuchador) {
		super(escuchador);
	}

	@Override
	public void ejecutar() {
		// Cierro todo
		try {
			escuchador.entrada.close();
			escuchador.salida.close();
			escuchador.socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		// Lo elimino de los clientes conectados
		Servidor.getClientesConectados().remove(this);
		
		// Indico que se desconecto
		Servidor.log.append(escuchador.paquete.getIp() + " se ha desconectado." + System.lineSeparator());
	}

}
