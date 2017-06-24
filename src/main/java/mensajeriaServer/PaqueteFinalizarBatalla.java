package mensajeriaServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import estados.Estado;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class PaqueteFinalizarBatalla extends EscuchaCliente implements mensajeriaServer.Paquete{

	public PaqueteFinalizarBatalla(String ip, Socket socket, ObjectInputStream entrada,
			ObjectOutputStream salida) {
		super(ip, socket, entrada, salida);
	}
	
	public String ejecutar(){
		
		paqueteFinalizarBatalla = (mensajeria.PaqueteFinalizarBatalla) gson.fromJson(cadenaLeida, mensajeria.PaqueteFinalizarBatalla.class);
		Servidor.getPersonajesConectados().get(paqueteFinalizarBatalla.getId()).setEstado(Estado.estadoJuego);
		Servidor.getPersonajesConectados().get(paqueteFinalizarBatalla.getIdEnemigo()).setEstado(Estado.estadoJuego);
		for(EscuchaCliente conectado : Servidor.getClientesConectados()) {
			if(conectado.getIdPersonaje() == paqueteFinalizarBatalla.getIdEnemigo()) {
				try {
					conectado.getSalida().writeObject(gson.toJson(paqueteFinalizarBatalla));
				} catch (IOException e) {
					Servidor.log.append("Error al finalizar batalla" + System.lineSeparator());
					e.printStackTrace();
				}
			}
		}
		
		synchronized(Servidor.atencionConexiones){
			Servidor.atencionConexiones.notify();
		}
		
		return null;
	}


}