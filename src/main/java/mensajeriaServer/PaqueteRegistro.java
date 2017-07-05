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

public class PaqueteRegistro extends mensajeriaServer.Paquete{

	public PaqueteRegistro(EscuchaCliente escuchador) {
		super(escuchador);
	}

	@Override
	public void ejecutar() {
		// Paquete que le voy a enviar al usuario
		escuchador.paqueteSv.setComando(Comando.REGISTRO);		
		escuchador.paqueteUsuario = (PaqueteUsuario) (escuchador.gson.fromJson(escuchador.cadenaLeida, PaqueteUsuario.class)).clone();
		try {
			// Si el usuario se pudo registrar le envio un msj de exito
			if (Servidor.getConector().registrarUsuario(escuchador.paqueteUsuario)) {
				escuchador.paqueteSv.setMensaje(Paquete.msjExito);
				escuchador.salida.writeObject(escuchador.gson.toJson(escuchador.paqueteSv));
			// Si el usuario no se pudo registrar le envio un msj de fracaso
			} else {
				escuchador.paqueteSv.setMensaje(Paquete.msjFracaso);
				escuchador.salida.writeObject(escuchador.gson.toJson(escuchador.paqueteSv));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
