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
import ec.gob.ambiente.suia.domain.CatalogoGeneral;
import ec.gob.ambiente.suia.domain.TipoManejoDesechos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.DesechoModalidadReuso;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadReuso;

/**
 * <b> Bean de la pagina modalidad reuso. </b>
 * 
 * @author vero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: vero $, $Date: 10/06/2015 $]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class ModalidadReusoBean {

	@Setter
	@Getter
	private List<CatalogoGeneral> listaTipoTransportes;

	@Setter
	@Getter
	private List<TipoManejoDesechos> listaTipoManejoDesechosReuso;

	@Setter
	@Getter
	private ModalidadReuso modalidadReuso;

	@Setter
	@Getter
	private boolean habilitarNombreEmpresaAutorizada;

	@Setter
	@Getter
	private boolean validacionTipoTransporte;

	@Setter
	@Getter
	private List<TipoManejoDesechos> tiposManejoDesechosSeleccionadas;

	@Setter
	@Getter
	private DesechoModalidadReuso desechoModalidadReuso;

}
