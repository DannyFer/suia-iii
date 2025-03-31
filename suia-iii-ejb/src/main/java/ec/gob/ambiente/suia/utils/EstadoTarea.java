package ec.gob.ambiente.suia.utils;

public enum EstadoTarea {
	Reserved, Completed; 

	public static String getNombreEstado(String estado) {
		if (Reserved != null && Reserved.equals(Reserved)){
			return "Reservado";
		} else if (Completed != null && Completed.equals(Completed)){
			return "Completado";
		} 
		return null;
	}
}
