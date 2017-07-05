package mensajeriaServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import estados.Estado;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class PaqueteBatalla extends mensajeriaServer.Paquete{

	public PaqueteBatalla(EscuchaCliente escuchador) {
		super(escuchador);
	}

	@Override
	public void ejecutar() {
		// Le reenvio al id del personaje batallado que quieren pelear
		escuchador.paqueteBatalla = (mensajeria.PaqueteBatalla) escuchador.gson.fromJson(escuchador.cadenaLeida, mensajeria.PaqueteBatalla.class);
		Servidor.log.append(escuchador.paqueteBatalla.getId() + " quiere batallar con " + escuchador.paqueteBatalla.getIdEnemigo() + System.lineSeparator());
		
		//seteo estado de batalla
		Servidor.getPersonajesConectados().get(escuchador.paqueteBatalla.getId()).setEstado(Estado.estadoBatalla);
		Servidor.getPersonajesConectados().get(escuchador.paqueteBatalla.getIdEnemigo()).setEstado(Estado.estadoBatalla);
		escuchador.paqueteBatalla.setMiTurno(true);
		try {
			escuchador.salida.writeObject(escuchador.gson.toJson(escuchador.paqueteBatalla));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(EscuchaCliente conectado : Servidor.getClientesConectados()){
			if(conectado.getIdPersonaje() == escuchador.paqueteBatalla.getIdEnemigo()){
				int aux = escuchador.paqueteBatalla.getId();
				escuchador.paqueteBatalla.setId(escuchador.paqueteBatalla.getIdEnemigo());
				escuchador.paqueteBatalla.setIdEnemigo(aux);
				escuchador.paqueteBatalla.setMiTurno(false);
				try {
					conectado.getSalida().writeObject(escuchador.gson.toJson(escuchador.paqueteBatalla));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}
		
		synchronized(Servidor.atencionConexiones){
			Servidor.atencionConexiones.notify();
		}
	}

}
