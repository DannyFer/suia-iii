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
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.DetalleCapacidadConfinamientoDesechoPeligroso;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.DetalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligroso;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadDisposicionFinal;

/**
 * <b> Bean de la página modalidad disposición final. </b>
 * 
 * @author Jonathan Guerrero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Jonathan Guerrero $, $Date: 22/06/2015 $]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class ModalidadDisposicionFinalBean {

	@Setter
	@Getter
	private List<DetalleCapacidadConfinamientoDesechoPeligroso> detalleCapacidadConfinamientoDesechoPeligrosos;

	@Setter
	@Getter
	private List<DetalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligroso> detalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligrosos;

	@Setter
	@Getter
	private ModalidadDisposicionFinal modalidadDisposicionFinal;

	@Setter
	@Getter
	private DetalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligroso detalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligroso;

	@Setter
	@Getter
	private DetalleCapacidadConfinamientoDesechoPeligroso detalleCapacidadConfinamientoDesechoPeligroso;

	@Setter
	@Getter
	private boolean habilitarNombreEmpresaAutorizada;

	@Setter
	@Getter
	private String nombreFilePlano;

	@Setter
	@Getter
	private String nombreFileRequisitos;

	@Setter
	@Getter
	private String nombreFileAnexos;

	@Setter
	@Getter
	private boolean validacionTipoTransporte;
	
	@Setter
	@Getter
	private List<DesechoPeligroso> listaDesechos;

}
