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

public class PaqueteInicioSesion extends mensajeriaServer.Paquete {

	public PaqueteInicioSesion(EscuchaCliente escuchador) {
		super(escuchador);
	}

	@Override
	public void ejecutar() {

		escuchador.paqueteSv.setComando(Comando.INICIOSESION);
		
		// Recibo el paquete usuario
		escuchador.paqueteUsuario = (PaqueteUsuario) (escuchador.gson.fromJson(escuchador.cadenaLeida, PaqueteUsuario.class));
		
		// Si se puede loguear el usuario le envio un mensaje de exito y el paquete personaje con los datos
		if (Servidor.getConector().loguearUsuario(escuchador.paqueteUsuario)) {
			
			escuchador.paquetePersonaje = new PaquetePersonaje();
			escuchador.paquetePersonaje = Servidor.getConector().getPersonaje(escuchador.paqueteUsuario);
			escuchador.paquetePersonaje.setComando(Comando.INICIOSESION);
			//paquetePersonaje.setMensaje(Paquete.msjExito);
			escuchador.paquetePersonaje.setMensaje("1");
			escuchador.idPersonaje = escuchador.paquetePersonaje.getId();
			
			try {
				escuchador.salida.writeObject(escuchador.gson.toJson(escuchador.paquetePersonaje));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} else {
			//paqueteSv.setMensaje(Paquete.msjFracaso);
			escuchador.paqueteSv.setMensaje("0");
			try {
				escuchador.salida.writeObject(escuchador.gson.toJson(escuchador.paqueteSv));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
