package mensajeriaServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import servidor.EscuchaCliente;
import servidor.Servidor;

public class PaqueteAtacar extends mensajeriaServer.Paquete{

	public PaqueteAtacar(EscuchaCliente escuchador) {
		super(escuchador);
	}

	@Override
	public void ejecutar() {
		escuchador.paqueteAtacar = (mensajeria.PaqueteAtacar) escuchador.gson.fromJson(escuchador.cadenaLeida, mensajeria.PaqueteAtacar.class);
		for(EscuchaCliente conectado : Servidor.getClientesConectados()) {
			if(conectado.getIdPersonaje() == escuchador.paqueteAtacar.getIdEnemigo()) {
				try {
					conectado.getSalida().writeObject(escuchador.gson.toJson(escuchador.paqueteAtacar));
				} catch (IOException e) {
					Servidor.log.append("Error al atacar" + System.lineSeparator());
					e.printStackTrace();
				}
			}
		}
	}

}
