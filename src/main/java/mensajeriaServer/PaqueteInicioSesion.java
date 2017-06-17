package mensajeriaServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import mensajeria.Comando;
import mensajeria.PaquetePersonaje;
import mensajeria.PaqueteUsuario;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class PaqueteInicioSesion extends EscuchaCliente implements Paquete {

	public PaqueteInicioSesion(String ip, Socket socket, ObjectInputStream entrada, ObjectOutputStream salida) {
		super(ip, socket, entrada, salida);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void ejecutar() {

		paqueteSv.setComando(Comando.INICIOSESION);
		
		// Recibo el paquete usuario
		paqueteUsuario = (PaqueteUsuario) (gson.fromJson(cadenaLeida, PaqueteUsuario.class));
		
		// Si se puede loguear el usuario le envio un mensaje de exito y el paquete personaje con los datos
		if (Servidor.getConector().loguearUsuario(paqueteUsuario)) {
			
			paquetePersonaje = new PaquetePersonaje();
			paquetePersonaje = Servidor.getConector().getPersonaje(paqueteUsuario);
			paquetePersonaje.setComando(Comando.INICIOSESION);
			//paquetePersonaje.setMensaje(Paquete.msjExito);
			paquetePersonaje.setMensaje("1");
			idPersonaje = paquetePersonaje.getId();
			
			try {
				salida.writeObject(gson.toJson(paquetePersonaje));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} else {
			//paqueteSv.setMensaje(Paquete.msjFracaso);
			paqueteSv.setMensaje("0");
			try {
				salida.writeObject(gson.toJson(paqueteSv));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
	}

}
