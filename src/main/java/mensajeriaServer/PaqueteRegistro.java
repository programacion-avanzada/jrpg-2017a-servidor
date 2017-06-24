package mensajeriaServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import mensajeria.Comando;
import mensajeria.Paquete;
import mensajeria.PaqueteUsuario;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class PaqueteRegistro extends EscuchaCliente implements mensajeriaServer.Paquete{

	public PaqueteRegistro(String ip, Socket socket, ObjectInputStream entrada, ObjectOutputStream salida) {
		super(ip, socket, entrada, salida);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String ejecutar() {
		// Paquete que le voy a enviar al usuario
		paqueteSv.setComando(Comando.REGISTRO);		
		paqueteUsuario = (PaqueteUsuario) (gson.fromJson(cadenaLeida, PaqueteUsuario.class)).clone();
		try {
			// Si el usuario se pudo registrar le envio un msj de exito
			if (Servidor.getConector().registrarUsuario(paqueteUsuario)) {
				paqueteSv.setMensaje(Paquete.msjExito);
				salida.writeObject(gson.toJson(paqueteSv));
			// Si el usuario no se pudo registrar le envio un msj de fracaso
			} else {
				paqueteSv.setMensaje(Paquete.msjFracaso);
				salida.writeObject(gson.toJson(paqueteSv));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
