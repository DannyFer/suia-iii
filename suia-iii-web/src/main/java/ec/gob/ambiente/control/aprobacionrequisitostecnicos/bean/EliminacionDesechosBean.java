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
import ec.gob.ambiente.suia.domain.TipoEliminacionDesecho;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.EliminacionDesecho;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.EliminacionRecepcion;
import ec.gob.ambiente.suia.dto.EntityRecepcionDesecho;

/**
 * <b> Clase bean para la eliminacion de desechos. </b>
 * 
 * @author Javier Lucero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Javier Lucero $, $Date: 11/06/2015 $]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class EliminacionDesechosBean {

	@Setter
	@Getter
	private EliminacionRecepcion eliminacionRecepcion;

	@Setter
	@Getter
	private List<EliminacionRecepcion> eliminacionesRecepciones;

	@Setter
	@Getter
	private List<EliminacionRecepcion> eliminacionesRecepcionesModificacion;

	@Setter
	@Getter
	private EliminacionDesecho eliminacionDesecho;

	@Setter
	@Getter
	private List<EliminacionDesecho> eliminacionDesechos;

	@Setter
	@Getter
	private List<EliminacionDesecho> eliminacionDesechosVer;

	@Setter
	@Getter
	private DesechoPeligroso desechoPeligroso;

	@Setter
	@Getter
	private List<DesechoPeligroso> desechoPeligrosos;

	@Setter
	@Getter
	private List<TipoEliminacionDesecho> tipoEliminacionDesechos;

	@Setter
	@Getter
	private List<EliminacionRecepcion> eliminacionRecepcionesEliminar;

	@Setter
	@Getter
	private List<EliminacionDesecho> eliminacionesDesechosEliminar;

	@Getter
	@Setter
	private List<EntityRecepcionDesecho> listaEntityRecepcionDesecho;

	@Setter
	@Getter
	private List<UbicacionesGeografica> provincias;

	@Setter
	@Getter
	private List<UbicacionesGeografica> cantones;

	@Getter
	@Setter
	private UbicacionesGeografica provinciaDestino;

	@Setter
	@Getter
	private boolean editar;

	@Setter
	@Getter
	private EliminacionRecepcion eliminacionRecepcionAux;

	@Setter
	@Getter
	private EliminacionDesecho eliminacionDesechoAux;

	@Setter
	@Getter
	private int indiceModificadoExiste;

	@Setter
	@Getter
	private String codigoGenerador;

}
