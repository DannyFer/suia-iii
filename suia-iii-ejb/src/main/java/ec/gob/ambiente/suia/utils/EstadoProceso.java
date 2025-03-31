package ec.gob.ambiente.suia.utils;

public enum EstadoProceso {
	PENDING(0), ACTIVE(1), COMPLETED(2), ABORTED(3), SUSPENDED(4);

	private Integer estado;

	private EstadoProceso(Integer estado) {
		this.estado = estado;
	}

	public static String getNombreEstado(int estado) {
		if (PENDING.estado != null && PENDING.estado == estado) {
			return "Pendiente";
		} else if (ACTIVE.estado != null && ACTIVE.estado == estado) {
			return "Activo";
		} else if (COMPLETED.estado != null && COMPLETED.estado == estado) {
			return "Completado";
		} else if (ABORTED.estado != null && ABORTED.estado == estado) {
			return "Abortado";
		} else if (SUSPENDED.estado != null && SUSPENDED.estado == estado) {
			return "Suspendido";
		}
		return null;
	}
}
