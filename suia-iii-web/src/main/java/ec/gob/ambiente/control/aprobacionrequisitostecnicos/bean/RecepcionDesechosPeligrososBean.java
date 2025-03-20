package ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.TipoEstadoFisico;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.RecepcionDesechoPeligroso;

@ManagedBean
@ViewScoped
public class RecepcionDesechosPeligrososBean {

	@Setter
	@Getter
	private RecepcionDesechoPeligroso recepcionDesechoPeligroso;

	@Setter
	@Getter
	private List<TipoEstadoFisico> listaEstados;

	@Setter
	@Getter
	private List<RecepcionDesechoPeligroso> listaRecepcionDesechoPeligroso;

	@Setter
	@Getter
	private List<RecepcionDesechoPeligroso> listaRecepcionDesechoPeligrosoModificados;

	@Setter
	@Getter
	private List<RecepcionDesechoPeligroso> listaRecepcionDesechoPeligrosoEliminados;

	@Setter
	@Getter
	private boolean editar;
}
