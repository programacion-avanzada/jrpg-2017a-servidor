package mensajeriaServer;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

//import mensajeria.PaqueteMovimiento;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class PaqueteMovimiento extends mensajeriaServer.Paquete{

	public PaqueteMovimiento(EscuchaCliente escuchador) {
		super(escuchador);
	}

	@Override
	public void ejecutar() {
		escuchador.paqueteMovimiento = (mensajeria.PaqueteMovimiento) (escuchador.gson.fromJson((String) escuchador.cadenaLeida, mensajeria.PaqueteMovimiento.class));
		
		Servidor.getUbicacionPersonajes().get(escuchador.paqueteMovimiento.getIdPersonaje()).setPosX(escuchador.paqueteMovimiento.getPosX());
		Servidor.getUbicacionPersonajes().get(escuchador.paqueteMovimiento.getIdPersonaje()).setPosY(escuchador.paqueteMovimiento.getPosY());
		Servidor.getUbicacionPersonajes().get(escuchador.paqueteMovimiento.getIdPersonaje()).setDireccion(escuchador.paqueteMovimiento.getDireccion());
		Servidor.getUbicacionPersonajes().get(escuchador.paqueteMovimiento.getIdPersonaje()).setFrame(escuchador.paqueteMovimiento.getFrame());
		
		synchronized(Servidor.atencionMovimientos){
			Servidor.atencionMovimientos.notify();
		}
	}

}
