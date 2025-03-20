/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean;

import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ActividadProtocoloPrueba;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadCoprocesamiento;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadCoprocesamientoDesecho;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadCoprocesamientoFormulacion;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadCoprocesamientoDesechoProcesar;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ProgramaCalendarizadoCoprocesamiento;

/**
 * <b> Bean de la página modalidad coprocesamiento. </b>
 * 
 * @author Jonathan Guerrero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Jonathan Guerrero $, $Date: 22/06/2015 $]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class ModalidadCoprocesamientoBean {

	@Setter
	@Getter
	private ModalidadCoprocesamiento modalidadCoprocesamiento;

	@Setter
	@Getter
	private String nombreFilePlano;

	@Setter
	@Getter
	private String nombreFileRequisitosCoprocesamientoDesechoPeligroso;

	@Setter
	@Getter
	private String nombreFileResumenEjecutivoProtocoloPruebas;

	@Setter
	@Getter
	private String nombreFileSistemaAlimentacionDesechosYOperacionesActividad;

	@Setter
	@Getter
	private String nombreFileProcedimientoProtocoloPrueba;

	@Setter
	@Getter
	private String nombreFileRequisitosProtocoloPrueba;

	@Setter
	@Getter
	private List<ProgramaCalendarizadoCoprocesamiento> calendario;

	@Setter
	@Getter
	private ProgramaCalendarizadoCoprocesamiento calendarioActividad;

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
	private List<ModalidadCoprocesamientoDesecho> listaCoprocesamientoDesecho;

	@Setter
	@Getter
	private ModalidadCoprocesamientoDesecho modalidadCoprocesamientoDesecho;

	@Setter
	@Getter
	private List<ModalidadCoprocesamientoDesechoProcesar> listaDesechoProcesar;

	@Setter
	@Getter
	private ModalidadCoprocesamientoDesechoProcesar modalidadCoprocesamientoDesechoProcesar;

	@Setter
	@Getter
	private List<ModalidadCoprocesamientoFormulacion> listaModalidadCoprocesamientoFormulacion;

	@Setter
	@Getter
	private List<ModalidadCoprocesamientoFormulacion> listaModalidadCoprocesamientoFormulacionEliminados;

	@Setter
	@Getter
	private ModalidadCoprocesamientoFormulacion modalidadCoprocesamientoFormulacion;

}
