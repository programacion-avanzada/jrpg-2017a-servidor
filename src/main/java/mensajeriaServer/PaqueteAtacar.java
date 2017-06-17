package mensajeriaServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import servidor.EscuchaCliente;
import servidor.Servidor;

public class PaqueteAtacar extends EscuchaCliente implements Paquete {

	public PaqueteAtacar(String ip, Socket socket, ObjectInputStream entrada, ObjectOutputStream salida) {
		super(ip, socket, entrada, salida);
	}

	@Override
	public void ejecutar() {
		paqueteAtacar = (mensajeria.PaqueteAtacar) gson.fromJson(cadenaLeida, mensajeria.PaqueteAtacar.class);
		for(EscuchaCliente conectado : Servidor.getClientesConectados()) {
			if(conectado.getIdPersonaje() == paqueteAtacar.getIdEnemigo()) {
				try {
					conectado.getSalida().writeObject(gson.toJson(paqueteAtacar));
				} catch (IOException e) {
					Servidor.log.append("Error al atacar" + System.lineSeparator());
					e.printStackTrace();
				}
			}
		}
	}

}
