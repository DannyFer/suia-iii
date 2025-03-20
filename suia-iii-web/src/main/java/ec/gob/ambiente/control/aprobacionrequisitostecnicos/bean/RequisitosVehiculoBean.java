package ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.RequisitosVehiculo;

@ManagedBean
@ViewScoped
public class RequisitosVehiculoBean {

	@Setter
	@Getter
	private List<RequisitosVehiculo> listaRequisitosVehiculo;

	@Setter
	@Getter
	private RequisitosVehiculo requisitoVehiculo;

	@Setter
	@Getter
	private List<RequisitosVehiculo> listaRequisitosVehiculoModificados;

	@Setter
	@Getter
	private List<RequisitosVehiculo> listaRequisitosVehiculoEliminados;

	@Setter
	@Getter
	private boolean editar;

	@Setter
	@Getter
	private boolean vehiculoEncontrado;
	
	@Setter
	@Getter
	private boolean requistosVehiculoRequerido;
	
	@Setter
	@Getter
	private String numPlaca;
	
	@Setter
	@Getter
	private boolean habilitarCertificadoCalibracion;
	

}
