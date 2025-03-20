package ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.RequisitosVehiculo;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.SustanciaQuimicaPeligrosaTransporte;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.SustanciaQuimicaPeligrosaTransporteUbicacionGeografica;

@ManagedBean
@ViewScoped
public class SustanciaQuimicaPeligrosaBean {

	@Getter
	@Setter
	private SustanciaQuimicaPeligrosaTransporte sustanciaQuimicaPeligrosaTransporte;

	@Getter
	@Setter
	private SustanciaQuimicaPeligrosaTransporte sustanciaQuimicaPeligrosaTransporteAux;
	
	@Getter
    @Setter
    private LazyDataModel<SustanciaQuimicaPeligrosaTransporte> sustanciaQuimicaPeligrosaTransporteLazy;

	@Getter
	@Setter
	private List<SustanciaQuimicaPeligrosaTransporte> sustanciasQuimicasPeligrosasTransportesModificados;

	@Getter
	@Setter
	private List<UbicacionesGeografica> destinos;

	@Setter
	@Getter
	private List<UbicacionesGeografica> provincias;

	@Setter
	@Getter
	private List<UbicacionesGeografica> cantones;

	@Getter
	@Setter
	private List<RequisitosVehiculo> requisitosVehiculos;

	@Getter
	@Setter
	private List<UbicacionesGeografica> cantonesOrigen;

	@Getter
	@Setter
	private UbicacionesGeografica provinciaDestinoSeleccionada;

	@Getter
	@Setter
	private UbicacionesGeografica cantonDestinoSeleccionado;

	@Getter
	@Setter
	private List<SustanciaQuimicaPeligrosaTransporte> sustanciaQuimicaTransporteEliminar;

	@Getter
	@Setter
	private List<SustanciaQuimicaPeligrosaTransporteUbicacionGeografica> sustanciasUbicacionesEliminar;

	@Setter
	@Getter
	private boolean editar;

	@Setter
	@Getter
	private boolean mostrarManual;

	@Setter
	@Getter
	private UploadedFile file;

	@Getter
	@Setter
	private SustanciaQuimicaPeligrosaTransporte sustanciaQuimicaPeligrosaTransporteVer;

	public void resetSelections() {
		provinciaDestinoSeleccionada = null;
		cantonDestinoSeleccionado = null;
		setCantones(new ArrayList<UbicacionesGeografica>());
	}

}
