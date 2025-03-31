package ec.gob.ambiente.prevencion.categoriaii.mineria.bean;

import ec.gob.ambiente.suia.domain.CronogramaValoradoPma;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.FichaAmbientalMineria;
import ec.gob.ambiente.suia.domain.UnidadPeriodicidad;
import ec.gob.ambiente.suia.dto.EntityAdjunto;
import ec.gob.ambiente.suia.dto.EntityPlanCronograma;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class DetalleMineriaBean implements Serializable {

	private static final long serialVersionUID = -1420606882308368416L;

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
	private FichaAmbientalMineria ficha;

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
