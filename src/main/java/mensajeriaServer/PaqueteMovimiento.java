package mensajeriaServer;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

//import mensajeria.PaqueteMovimiento;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class PaqueteMovimiento extends EscuchaCliente implements Paquete {

	public PaqueteMovimiento(String ip, Socket socket, ObjectInputStream entrada, ObjectOutputStream salida) {
		super(ip, socket, entrada, salida);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String ejecutar() {
		paqueteMovimiento = (mensajeria.PaqueteMovimiento) (gson.fromJson((String) cadenaLeida, mensajeria.PaqueteMovimiento.class));
		
		Servidor.getUbicacionPersonajes().get(paqueteMovimiento.getIdPersonaje()).setPosX(paqueteMovimiento.getPosX());
		Servidor.getUbicacionPersonajes().get(paqueteMovimiento.getIdPersonaje()).setPosY(paqueteMovimiento.getPosY());
		Servidor.getUbicacionPersonajes().get(paqueteMovimiento.getIdPersonaje()).setDireccion(paqueteMovimiento.getDireccion());
		Servidor.getUbicacionPersonajes().get(paqueteMovimiento.getIdPersonaje()).setFrame(paqueteMovimiento.getFrame());
		
		synchronized(Servidor.atencionMovimientos){
			Servidor.atencionMovimientos.notify();
		}
		
		return null;
	}

}
