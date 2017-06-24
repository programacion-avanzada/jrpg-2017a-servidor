package mensajeriaServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import estados.Estado;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class PaqueteFinalizarBatalla extends mensajeriaServer.Paquete{

	public PaqueteFinalizarBatalla(EscuchaCliente escuchador) {
		super(escuchador);
	}
	
	public void ejecutar(){
		
		escuchador.paqueteFinalizarBatalla = (mensajeria.PaqueteFinalizarBatalla) escuchador.gson.fromJson(escuchador.cadenaLeida, mensajeria.PaqueteFinalizarBatalla.class);
		Servidor.getPersonajesConectados().get(escuchador.paqueteFinalizarBatalla.getId()).setEstado(Estado.estadoJuego);
		Servidor.getPersonajesConectados().get(escuchador.paqueteFinalizarBatalla.getIdEnemigo()).setEstado(Estado.estadoJuego);
		for(EscuchaCliente conectado : Servidor.getClientesConectados()) {
			if(conectado.getIdPersonaje() == escuchador.paqueteFinalizarBatalla.getIdEnemigo()) {
				try {
					conectado.getSalida().writeObject(escuchador.gson.toJson(escuchador.paqueteFinalizarBatalla));
				} catch (IOException e) {
					Servidor.log.append("Error al finalizar batalla" + System.lineSeparator());
					e.printStackTrace();
				}
			}
		}
		
		synchronized(Servidor.atencionConexiones){
			Servidor.atencionConexiones.notify();
		}
	}


}