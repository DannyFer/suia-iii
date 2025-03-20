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
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadIncineracion;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadIncineracionDesecho;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadIncineracionDesechoProcesar;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadIncineracionFormulacion;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ProgramaCalendarizadoIncineracion;

/**
 * <b> Bean de la pagina modalidad incineracion. </b>
 * 
 * @author vero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: vero $, $Date: 10/06/2015 $]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class ModalidadIncineracionBean {

	@Setter
	@Getter
	private ModalidadIncineracion modalidadIncineracion;

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
	private List<ProgramaCalendarizadoIncineracion> calendario;

	@Setter
	@Getter
	private ProgramaCalendarizadoIncineracion calendarioActividad;

	@Setter
	@Getter
	private ActividadProtocoloPrueba actividadSeleccionada;

	@Setter
	@Getter
	private List<ActividadProtocoloPrueba> actividadesProtocolo;

	@Setter
	@Getter
	private Date fechaInicio;

	@Setter
	@Getter
	private Date fechaFin;

	@Setter
	@Getter
	private boolean validacionTipoTransporte;

	@Setter
	@Getter
	private List<DesechoPeligroso> listaDesechos;

	@Setter
	@Getter
	private List<ModalidadIncineracionDesecho> ListaModalidadIncineracionDesechos;

	@Setter
	@Getter
	private ModalidadIncineracionDesecho modalidadIncineracionDesecho;

	@Setter
	@Getter
	private List<ModalidadIncineracionDesechoProcesar> ListaModalidadIncineracionDesechoProcesados;

	@Setter
	@Getter
	private ModalidadIncineracionDesechoProcesar modalidadIncineracionDesechoProcesar;

	@Setter
	@Getter
	private ModalidadIncineracionFormulacion modalidadIncineracionFormulacion;

	@Setter
	@Getter
	private List<ModalidadIncineracionFormulacion> listaModalidadIncineracionFormulacionEliminados;

}
