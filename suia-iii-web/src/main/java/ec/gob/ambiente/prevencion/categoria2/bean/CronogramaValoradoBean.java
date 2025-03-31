package ec.gob.ambiente.prevencion.categoria2.bean;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.CronogramaValoradoPma;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;
import ec.gob.ambiente.suia.domain.UnidadPeriodicidad;
import ec.gob.ambiente.suia.dto.EntityAdjunto;
import ec.gob.ambiente.suia.dto.EntityPlanCronograma;

import java.util.Date;

public class CronogramaValoradoBean implements Serializable {
	private static final long serialVersionUID = 3796781583972052551L;

	@Getter
	@Setter
	private List<CronogramaValoradoPma> listaCronogramaEliminar;

	@Getter
	@Setter
	private CronogramaValoradoPma actividad;

	@Getter
	@Setter
	private long zoomMax;

	@Getter
	@Setter
	private long zoomMin;

	@Getter
	@Setter
	private Date start;

	@Getter
	@Setter
	private Date end;

	@Getter
	@Setter
	private Double sumaTotal;

	@Getter
	@Setter
	private FichaAmbientalPma ficha;

	@Getter
	@Setter
	private List<EntityPlanCronograma> listaEntityPlanCronograma;

	@Getter
	@Setter
	private EntityPlanCronograma seleccionadoEntityPlanCronograma;

	@Getter
	@Setter
	private EntityAdjunto entityAdjunto;

	@Setter
	private Documento plantillaAgregarPuntosMonitoreo;
	
	/***** cambio periodicidad *****/
	@Getter
	@Setter
	private List<UnidadPeriodicidad> listaUnidadPeriodicidad;
	/***** cambio periodicidad *****/
	

	public Documento getPlantillaAgregarPuntosMonitoreo() {
		return plantillaAgregarPuntosMonitoreo == null ? plantillaAgregarPuntosMonitoreo = new Documento()
				: plantillaAgregarPuntosMonitoreo;
	}
}