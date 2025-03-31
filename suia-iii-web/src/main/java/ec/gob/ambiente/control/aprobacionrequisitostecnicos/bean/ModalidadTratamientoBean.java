/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean;

import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ActividadProtocoloPrueba;
import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.DesechoModalidadTratamiento;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadTratamiento;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ProgramaCalendarizadoTratamiento;

/**
 * <b> Bean de la pagina modalidad tratamiento. </b>
 * 
 * @author vero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: vero $, $Date: 10/06/2015 $]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class ModalidadTratamientoBean {

	@Setter
	@Getter
	private ModalidadTratamiento modalidadTratamiento;

	@Setter
	@Getter
	private boolean habilitarNombreEmpresaAutorizada;

	@Setter
	@Getter
	private String trataDesechosBiologicos;

	@Setter
	@Getter
	private boolean habilitarTrataDesechosBiologicos;

	@Setter
	@Getter
	private List<ActividadProtocoloPrueba> actividadesProtocoloPrueba;

	@Setter
	@Getter
	private ActividadProtocoloPrueba actividadSeleccionada;

	@Setter
	@Getter
	private List<ProgramaCalendarizadoTratamiento> calendario;

	@Setter
	@Getter
	private ProgramaCalendarizadoTratamiento calendarioActividad;

	@Setter
	@Getter
	private Date fechaIni;

	@Setter
	@Getter
	private Date fechaFin;

	@Setter
	@Getter
	private boolean validacionTipoTransporte;

	@Setter
	@Getter
	private DesechoModalidadTratamiento desechoModalidadTratamiento;
}
