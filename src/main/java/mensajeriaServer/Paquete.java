package mensajeriaServer;

import servidor.EscuchaCliente;

public abstract class Paquete {

	protected EscuchaCliente escuchador;
	
	public Paquete(EscuchaCliente escuchador) {
		this.escuchador = escuchador;
	}
	
	public abstract void ejecutar();
}

