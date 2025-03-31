/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.control.aprobacionrequisitostecnicos.bean;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.TipoEnvase;
import ec.gob.ambiente.suia.domain.TipoIluminacion;
import ec.gob.ambiente.suia.domain.TipoLocal;
import ec.gob.ambiente.suia.domain.TipoMaterialConstruccion;
import ec.gob.ambiente.suia.domain.TipoVentilacion;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.Almacen;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AlmacenRecepcion;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.dto.EntityRecepcionDesecho;

/**
 * <b> Clase Bean para el almacenamiento temporal. </b>
 * 
 * @author Javier Lucero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Javier Lucero $, $Date: 09/06/2015 $]
 *          </p>
 */
public class AlmacenamientoTemporalBean implements Serializable {

	private static final long serialVersionUID = -4726252198049276182L;

	@Getter
	@Setter
	private AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos;

	@Getter
	@Setter
	private List<Almacen> almacenes;

	@Getter
	@Setter
	private List<Almacen> almacenesEliminar;

	@Getter
	@Setter
	private Almacen almacen;

	@Getter
	@Setter
	private AlmacenRecepcion almacenRecepcion;

	@Getter
	@Setter
	private List<AlmacenRecepcion> almacenRecepcionEliminar;

	@Getter
	@Setter
	private List<TipoLocal> locales;

	@Getter
	@Setter
	private List<TipoMaterialConstruccion> materiales;

	@Getter
	@Setter
	private List<TipoVentilacion> ventilaciones;

	@Getter
	@Setter
	private List<TipoIluminacion> iluminaciones;

	@Getter
	@Setter
	private List<TipoEnvase> tipoEnvases;

	@Getter
	@Setter
	private List<EntityRecepcionDesecho> listaEntityRecepcionDesecho;

	@Getter
	@Setter
	private Almacen almacenVer;

	@Getter
	@Setter
	private Almacen almacenAux;

	@Getter
	@Setter
	private AlmacenRecepcion almacenRecepcionAux;

	@Getter
	@Setter
	private List<Almacen> almacenesModificacion;

	@Setter
	@Getter
	private int indiceModificadoExiste;

	@Setter
	@Getter
	private boolean mostrarFosasRetencion;

}
