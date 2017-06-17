package mensajeriaServer;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import estados.Estado;
import mensajeria.PaqueteBatalla;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class PaqueteBatalla extends EscuchaCliente implements Paquete {

	public PaqueteBatalla(String ip, Socket socket, ObjectInputStream entrada, ObjectOutputStream salida) {
		super(ip, socket, entrada, salida);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void ejecutar() {
		// Le reenvio al id del personaje batallado que quieren pelear
		paqueteBatalla = (PaqueteBatalla) gson.fromJson(cadenaLeida, PaqueteBatalla.class);
		Servidor.log.append(paqueteBatalla.getId() + " quiere batallar con " + paqueteBatalla.getIdEnemigo() + System.lineSeparator());
		
		//seteo estado de batalla
		Servidor.getPersonajesConectados().get(paqueteBatalla.getId()).setEstado(Estado.estadoBatalla);
		Servidor.getPersonajesConectados().get(paqueteBatalla.getIdEnemigo()).setEstado(Estado.estadoBatalla);
		paqueteBatalla.setMiTurno(true);
		salida.writeObject(gson.toJson(paqueteBatalla));
		for(EscuchaCliente conectado : Servidor.getClientesConectados()){
			if(conectado.getIdPersonaje() == paqueteBatalla.getIdEnemigo()){
				int aux = paqueteBatalla.getId();
				paqueteBatalla.setId(paqueteBatalla.getIdEnemigo());
				paqueteBatalla.setIdEnemigo(aux);
				paqueteBatalla.setMiTurno(false);
				conectado.getSalida().writeObject(gson.toJson(paqueteBatalla));
				break;
			}
		}
		
		synchronized(Servidor.atencionConexiones){
			Servidor.atencionConexiones.notify();
		}
		
	}

}
