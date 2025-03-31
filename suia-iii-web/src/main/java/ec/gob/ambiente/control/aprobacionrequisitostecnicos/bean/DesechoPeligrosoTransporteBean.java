/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.DesechoEspecialRecoleccion;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.DesechoPeligrosoTransporte;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.DesechoPeligrosoTransporteUbicacionGeografica;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.RequisitosVehiculo;
import java.util.ArrayList;

/**
 * <b> Clase Bean para los desechos peligrosos para la transportacion. </b>
 * 
 * @author Javier Lucero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Javier Lucero $, $Date: 22/06/2015 $]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class DesechoPeligrosoTransporteBean {

	@Getter
	@Setter
	private DesechoPeligrosoTransporte desechoPeligrosoTransporte;
	
	@Getter
	@Setter
	private DesechoPeligrosoTransporte desechoPeligrosoTransporteAux;

	@Getter
	@Setter
	private List<DesechoPeligrosoTransporte> desechosPeligrososTransportes;

	@Getter
	@Setter
	private List<UbicacionesGeografica> origenes;;

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
	private List<UbicacionesGeografica> cantonesDestino;

	@Getter
	@Setter
	private List<UbicacionesGeografica> provinciasDestino;

	@Getter
	@Setter
	private UbicacionesGeografica provinciaDestino;

	@Getter
	@Setter
	private UbicacionesGeografica provinciaOrigenSeleccionada;

	@Getter
	@Setter
	private UbicacionesGeografica cantonOrigenSeleccionado;

	@Getter
	@Setter
	private List<DesechoPeligrosoTransporteUbicacionGeografica> desechosubicacionesGeograficasEliminar;

	@Getter
	@Setter
	private List<DesechoPeligrosoTransporte> desechoPeligrosoTransporteEliminar;
	
	/*@Getter
	@Setter
	private List<DesechoPeligrosoTransporte> desechoPeligrosoTransporteModificados;*/
	
	@Getter
	@Setter
	private List<DesechoEspecialRecoleccion> desechosEspecialesRecoleccionEliminar;

	@Setter
	@Getter
	private boolean editar;
	
	@Getter
	@Setter
	private DesechoPeligrosoTransporte desechoPeligrosoTransporteVer;

	/**
	 * 
	 * <b> Metodo que reinicia la provincia y canton de origen. </b>
	 * <p>
	 * [Author: Javier Lucero, Date: 23/06/2015]
	 * </p>
	 * 
	 */
	public void resetSelections() {
		provinciaOrigenSeleccionada = null;
		cantonOrigenSeleccionado = null;
                setCantones(new ArrayList<UbicacionesGeografica>());
	}

}
