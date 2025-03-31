/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.ModalidadGestionDesechos;
import ec.gob.ambiente.suia.domain.TipoEliminacionDesecho;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.DesechoModalidadReciclaje;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.DesechoModalidadReuso;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.DesechoModalidadTratamiento;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.EliminacionDesecho;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.EliminacionRecepcion;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadCoprocesamiento;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadCoprocesamientoDesecho;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadCoprocesamientoDesechoProcesar;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadIncineracion;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadIncineracionDesecho;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadIncineracionDesechoProcesar;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadReciclaje;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadReuso;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadTratamiento;
import ec.gob.ambiente.suia.exceptions.ServiceException;

/**
 * <b> Incluir aqui la descripcion de la clase. </b>
 * 
 * @author vero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: vero $, $Date: 15/09/2015 $]
 *          </p>
 */
@Stateless
public class ModalidadesFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private ModalidadReciclajeFacade modalidadReciclajeFacade;

	@EJB
	private ModalidadReusoFacade modalidadReusoFacade;

	@EJB
	private ModalidadTratamientoFacade modalidadTratamientoFacade;

	@EJB
	private ModalidadCoprocesamientoFacade modalidadCoprocesamientoFacade;

	@EJB
	private ModalidadIncineracionFacade modalidadIncineracionFacade;

	@EJB
	private ModalidadDisposicionFinalFacade modalidadDisposicionFinalFacade;

	@EJB
	private AlmacenFacade almacenFacade;

	@EJB
	private EliminacionDesechoFacade eliminacionDesechoFacade;

	@SuppressWarnings("unchecked")
	public List<DesechoPeligroso> getDesechosPorTipoElimiancion(int idModalidad,
			AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) throws ServiceException {
		StringBuilder query = new StringBuilder();

		query.append("select wdg.* from suia_iii.waste_disposal wd ");
		query.append("inner join suia_iii.waste_disposal_types wdt on wd.wdty_id=wdt.wdty_id ");
		query.append("inner join suia_iii.waste_disposal_receipt wdr on wdr.wadr_id=wd.wadr_id ");
		query.append("inner join suia_iii.receipt_hazardous_waste rhw on wdr.rehw_id=rhw.rehw_id ");
		query.append("inner join suia_iii.waste_dangerous wdg on wdg.wada_id=rhw.wada_id ");
		query.append("where wdt.wdty_code_modality=" + idModalidad + " and wdr.apte_id="
				+ aprobacionRequisitosTecnicos.getId()
				+ " and wd.wadi_status=true and wdg.wada_status=true and wdr.wadr_status=true order by wdg.wada_key");

		List<DesechoPeligroso> lista = (List<DesechoPeligroso>) crudServiceBean.findNativeQuery(query.toString(),
				DesechoPeligroso.class);

		return lista;
	}

	@SuppressWarnings("unchecked")
	public List<ModalidadGestionDesechos> getModalidades() {
		return (List<ModalidadGestionDesechos>) crudServiceBean.findAll(ModalidadGestionDesechos.class);

	}

	public ModalidadGestionDesechos getModalidadReciclaje() {
		return crudServiceBean.find(ModalidadGestionDesechos.class, ModalidadGestionDesechos.ID_MODALIDAD_RECICLAJE);

	}

	public ModalidadGestionDesechos getModalidadReuso() {
		return crudServiceBean.find(ModalidadGestionDesechos.class, ModalidadGestionDesechos.ID_MODALIDAD_REUSO);
	}

	public ModalidadGestionDesechos getModalidadTratamiento() {
		return crudServiceBean.find(ModalidadGestionDesechos.class, ModalidadGestionDesechos.ID_MODALIDAD_TRATAMIENTO);
	}

	public ModalidadGestionDesechos getModalidadIncineracion() {
		return crudServiceBean.find(ModalidadGestionDesechos.class, ModalidadGestionDesechos.ID_MODALIDAD_INCINERACION);
	}

	public ModalidadGestionDesechos getModalidadOtros() {
		return crudServiceBean.find(ModalidadGestionDesechos.class, ModalidadGestionDesechos.ID_OTROS);
	}

	public ModalidadGestionDesechos getModalidadDisposicionFinal() {
		return crudServiceBean.find(ModalidadGestionDesechos.class,
				ModalidadGestionDesechos.ID_MODALIDAD_DISPOSICION_FINAL);
	}

	public ModalidadGestionDesechos getModalidadTransporte() {
		return crudServiceBean.find(ModalidadGestionDesechos.class, ModalidadGestionDesechos.ID_MODALIDAD_TRANSPORTE);
	}

	public ModalidadGestionDesechos getModalidadCoprocesamiento() {
		return crudServiceBean.find(ModalidadGestionDesechos.class,
				ModalidadGestionDesechos.ID_MODALIDAD_COPROCESAMIENTO);
	}

	public List<String> validarDesechosCompletosPorModalidad(AprobacionRequisitosTecnicos aprobacionRequisitos)
			throws ServiceException {

		List<String> mensajesError = new ArrayList<String>();
		for (ModalidadGestionDesechos modalidad : aprobacionRequisitos.getModalidades()) {
			if (modalidad.getId() == ModalidadGestionDesechos.ID_MODALIDAD_RECICLAJE) {
				if (modalidadReciclajeFacade.getDesechosReciclaje(aprobacionRequisitos).isEmpty()) {
					mensajesError.add("No hay desechos asociados a la modalidad reciclaje.<br/>");
					modalidadReciclajeFacade.guardarPaginaComoInCompleta(aprobacionRequisitos);
				} else if (!modalidadReciclajeFacade.isPageCompleta(aprobacionRequisitos)) {
					mensajesError
							.add("Existen desechos asociados a la modalidad reciclaje que no estan completos.<br/>");
					modalidadReciclajeFacade.guardarPaginaComoInCompleta(aprobacionRequisitos);
				} else {
					modalidadReciclajeFacade.guardarPaginaComoCompleta(aprobacionRequisitos);
				}

			}
			if (modalidad.getId() == ModalidadGestionDesechos.ID_MODALIDAD_REUSO) {
				if (modalidadReusoFacade.getDesechosReuso(aprobacionRequisitos).isEmpty()) {
					mensajesError.add("No hay desechos asociados a la modalidad reuso.<br/>");
					modalidadReusoFacade.guardarPaginaComoInCompleta(aprobacionRequisitos);
				} else if (!modalidadReusoFacade.isPageCompleta(aprobacionRequisitos)) {
					mensajesError.add("Existen desechos asociados a la modalidad reuso que no estan completos.<br/>");
					modalidadReusoFacade.guardarPaginaComoInCompleta(aprobacionRequisitos);
				} else {
					modalidadReusoFacade.guardarPaginaComoCompleta(aprobacionRequisitos);
				}
			}
			if (modalidad.getId() == ModalidadGestionDesechos.ID_MODALIDAD_TRATAMIENTO) {
				if (modalidadTratamientoFacade.getDesechosTratamiento(aprobacionRequisitos).isEmpty()) {
					mensajesError.add("No hay desechos asociados a la modalidad tratamieto.<br/>");
					modalidadTratamientoFacade.guardarPaginaComoInCompleta(aprobacionRequisitos);
				} else if (!modalidadTratamientoFacade.isPageCompleta(aprobacionRequisitos)) {
					mensajesError
							.add("Existen desechos asociados a la modalidad tratamiento que no estan completos.<br/>");
					modalidadTratamientoFacade.guardarPaginaComoInCompleta(aprobacionRequisitos);
				} else {
					modalidadTratamientoFacade.guardarPaginaComoCompleta(aprobacionRequisitos);
				}
			}

			if (modalidad.getId() == ModalidadGestionDesechos.ID_MODALIDAD_COPROCESAMIENTO) {

				if (modalidadCoprocesamientoFacade.getDesechosCoprocesamiento(aprobacionRequisitos).isEmpty()) {
					mensajesError.add("No hay desechos asociados a la modalidad coprocesamiento.<br/>");
					modalidadCoprocesamientoFacade.guardarPaginaComoInCompleta(aprobacionRequisitos);
				} else if (!modalidadCoprocesamientoFacade.isPageCompleta(aprobacionRequisitos)) {
					mensajesError
							.add("Existen desechos asociados a la modalidad coprocesamiento que no estan completos.<br/>");
					modalidadCoprocesamientoFacade.guardarPaginaComoInCompleta(aprobacionRequisitos);
				} else {
					modalidadCoprocesamientoFacade.guardarPaginaComoCompleta(aprobacionRequisitos);
				}
			}
			if (modalidad.getId() == ModalidadGestionDesechos.ID_MODALIDAD_INCINERACION) {

				if (modalidadIncineracionFacade.getDesechosIncineracion(aprobacionRequisitos).isEmpty()) {
					mensajesError.add("No hay desechos asociados a la modalidad incineración.<br/>");
					modalidadCoprocesamientoFacade.guardarPaginaComoInCompleta(aprobacionRequisitos);
				} else if (!modalidadIncineracionFacade.isPageCompleta(aprobacionRequisitos)) {
					mensajesError
							.add("Existen desechos asociados a la modalidad incineración que no estan completos.<br/>");
					modalidadIncineracionFacade.guardarPaginaComoInCompleta(aprobacionRequisitos);
				} else {
					modalidadIncineracionFacade.guardarPaginaComoCompleta(aprobacionRequisitos);
				}
			}

			if (modalidad.getId() == ModalidadGestionDesechos.ID_MODALIDAD_DISPOSICION_FINAL) {
				if (!modalidadDisposicionFinalFacade.validarFormulario(aprobacionRequisitos)) {
					mensajesError
							.add("No existen desechos asociados a la modalidad de disposicion final, por favor usted debe agregar desechos o desabilitar esta modlaidad en la sección Tipo de requisitos.<br/>");
					modalidadDisposicionFinalFacade.guardarPaginaComoInCompleta(aprobacionRequisitos);
				} else {
					modalidadDisposicionFinalFacade.guardarPaginaComoCompleta(aprobacionRequisitos);
				}
			}

		}
		return mensajesError;
	}

	public String validarQueTodoDeschoEstenAlmacenados(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos)
			throws ServiceException {
		if (almacenFacade.validarAlmacenRecepcionDesechos(aprobacionRequisitosTecnicos.getId())) {
			return "Los desechos registrados en la sección no estan registrados en la sección , deben estar todos los desechos almacenados para poder terminar la tarea.";
		}
		return null;
	}

	public void guardarNuevosDesechosPorModalidad(AprobacionRequisitosTecnicos aprobacionRequisitos)
			throws ServiceException {
		for (ModalidadGestionDesechos modalidad : aprobacionRequisitos.getModalidades()) {
			if (modalidad.getId() == ModalidadGestionDesechos.ID_MODALIDAD_RECICLAJE) {
				if (modalidadReciclajeFacade.getModalidadReciclaje(aprobacionRequisitos) != null
						&& modalidadReciclajeFacade.existeNuevosDesechosParaModalidad(aprobacionRequisitos)) {
					modalidadReciclajeFacade.guardarNuevosDesechosParaModalidad(aprobacionRequisitos);

				}
			}
			if (modalidad.getId() == ModalidadGestionDesechos.ID_MODALIDAD_REUSO) {
				if (modalidadReusoFacade.getModalidadReuso(aprobacionRequisitos) != null
						&& modalidadReusoFacade.existeNuevosDesechosParaModalidad(aprobacionRequisitos)) {
					modalidadReusoFacade.guardarNuevosDesechosParaModalidad(aprobacionRequisitos);

				}
			}
			if (modalidad.getId() == ModalidadGestionDesechos.ID_MODALIDAD_TRATAMIENTO) {
				if (modalidadTratamientoFacade.getModalidadTratamiento(aprobacionRequisitos) != null
						&& modalidadTratamientoFacade.existeNuevosDesechosParaModalidad(aprobacionRequisitos)) {
					modalidadTratamientoFacade.guardarNuevosDesechosParaModalidad(aprobacionRequisitos);

				}
			}

			if (modalidad.getId() == ModalidadGestionDesechos.ID_MODALIDAD_INCINERACION) {
				if (modalidadIncineracionFacade.getModalidadIncineracion(aprobacionRequisitos) != null
						&& modalidadIncineracionFacade.existeNuevosDesechosParaModalidad(aprobacionRequisitos)) {
					modalidadIncineracionFacade.guardarNuevosDesechosParaModalidad(aprobacionRequisitos);

				}
			}
			if (modalidad.getId() == ModalidadGestionDesechos.ID_MODALIDAD_COPROCESAMIENTO) {
				if (modalidadCoprocesamientoFacade.getModalidadCoprocesamiento(aprobacionRequisitos) != null
						&& modalidadCoprocesamientoFacade.existeNuevosDesechosParaModalidad(aprobacionRequisitos)) {
					modalidadCoprocesamientoFacade.guardarNuevosDesechosParaModalidad(aprobacionRequisitos);

				}
			}
		}
	}

	public void eliminarDesechoModalidadAsociada(List<EliminacionDesecho> eliminacionesDesechos,
			AprobacionRequisitosTecnicos aprobacionRequisitos) throws ServiceException {
		DesechoPeligroso desecho = null;
		TipoEliminacionDesecho tipoEliminacion;
		for (EliminacionDesecho eliminacionDesecho : eliminacionesDesechos) {

			eliminacionDesecho = crudServiceBean.find(EliminacionDesecho.class, eliminacionDesecho.getId());

			desecho = eliminacionDesecho.getEliminacionRecepcion().getRecepcion().getDesecho();
			tipoEliminacion = eliminacionDesecho.getTipoEliminacionDesecho();

			eliminarDesechoModalidadAsociadaPorTipoEliminacion(aprobacionRequisitos, desecho, tipoEliminacion);
		}
	}

	/**
	 * <b> Incluir aqui­ la descripcion del metodo. </b>
	 * <p>
	 * [Author: vero, Date: 04/11/2015]
	 * </p>
	 * 
	 * @param aprobacionRequisitos
	 * @param desecho
	 * @param tipoEliminacion
	 * @throws ServiceException
	 */
	private void eliminarDesechoModalidadAsociadaPorTipoEliminacion(AprobacionRequisitosTecnicos aprobacionRequisitos,
			DesechoPeligroso desecho, TipoEliminacionDesecho tipoEliminacion) throws ServiceException {
		if (tipoEliminacion.getCodigoModalidad() == ModalidadGestionDesechos.ID_MODALIDAD_RECICLAJE) {
			ModalidadReciclaje modalidad = modalidadReciclajeFacade.getModalidadReciclaje(aprobacionRequisitos);
			if (modalidad != null) {
				eliminarDesechoModalidad(desecho, DesechoModalidadReciclaje.class.getSimpleName(),
						"modalidadReciclaje", modalidad.getId());
			}

		}
		if (tipoEliminacion.getCodigoModalidad() == ModalidadGestionDesechos.ID_MODALIDAD_REUSO) {
			ModalidadReuso modalidad = modalidadReusoFacade.getModalidadReuso(aprobacionRequisitos);
			if (modalidad != null) {
				eliminarDesechoModalidad(desecho, DesechoModalidadReuso.class.getSimpleName(), "modalidadReuso",
						modalidad.getId());
			}

		}
		if (tipoEliminacion.getCodigoModalidad() == ModalidadGestionDesechos.ID_MODALIDAD_TRATAMIENTO) {
			ModalidadTratamiento modalidad = modalidadTratamientoFacade.getModalidadTratamiento(aprobacionRequisitos);
			if (modalidad != null) {
				eliminarDesechoModalidad(desecho, DesechoModalidadTratamiento.class.getSimpleName(),
						"modalidadTratamiento", modalidad.getId());
			}
		}
		if (tipoEliminacion.getCodigoModalidad() == ModalidadGestionDesechos.ID_MODALIDAD_INCINERACION) {
			ModalidadIncineracion modalidad = modalidadIncineracionFacade
					.getModalidadIncineracion(aprobacionRequisitos);
			if (modalidad != null) {
				eliminarDesechoModalidad(desecho, ModalidadIncineracionDesecho.class.getSimpleName(),
						"modalidadIncineracion", modalidad.getId());
				eliminarDesechoModalidad(desecho, ModalidadIncineracionDesechoProcesar.class.getSimpleName(),
						"modalidadIncineracion", modalidad.getId());
			}
		}
		if (tipoEliminacion.getCodigoModalidad() == ModalidadGestionDesechos.ID_MODALIDAD_COPROCESAMIENTO) {
			ModalidadCoprocesamiento modalidad = modalidadCoprocesamientoFacade
					.getModalidadCoprocesamiento(aprobacionRequisitos);
			if (modalidad != null) {
				eliminarDesechoModalidad(desecho, ModalidadCoprocesamientoDesecho.class.getSimpleName(),
						"modalidadCoprocesamiento", modalidad.getId());
				eliminarDesechoModalidad(desecho, ModalidadCoprocesamientoDesechoProcesar.class.getSimpleName(),
						"modalidadCoprocesamiento", modalidad.getId());
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void eliminarDesechoModalidad(DesechoPeligroso desecho, String nombreClaseDesecho, String modalidad,
			Integer idModalidad) {

		String query = "Select dm from " + nombreClaseDesecho + " dm where dm.desecho=:desecho and dm." + modalidad
				+ " .id=" + idModalidad;

		List<DesechoModalidadReciclaje> lista = (List<DesechoModalidadReciclaje>) crudServiceBean.getEntityManager()
				.createQuery(query).setParameter("desecho", desecho).getResultList();

		crudServiceBean.delete(lista);

	}

	public boolean eliminarDeschosPantallaTipoEliminacionSiExisten(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos,
			Integer idModalidad) throws ServiceException {
		boolean isDesechoRecepcionAEliminar = false;
		List<DesechoPeligroso> desechos = getDesechosPorTipoElimiancion(idModalidad, aprobacionRequisitosTecnicos);
		List<EliminacionRecepcion> listaEliminacionRecepcion = eliminacionDesechoFacade
				.listarEliminacionPorAprobacionRequistos(aprobacionRequisitosTecnicos.getId());
		List<EliminacionDesecho> listaEliminacionDesechoParaEliminar = new ArrayList<EliminacionDesecho>();
		List<EliminacionRecepcion> listaEliminacionRecepcionParaEliminar = new ArrayList<EliminacionRecepcion>();
		for (DesechoPeligroso desechoPeligroso : desechos) {
			for (EliminacionRecepcion eliminacionRecepcion : listaEliminacionRecepcion) {
				List<EliminacionDesecho> l = eliminacionRecepcion.getEliminacionDesechos();
				for (EliminacionDesecho eliminacionDesecho : l) {
					isDesechoRecepcionAEliminar = false;
					if (eliminacionDesecho.getEliminacionRecepcion().getRecepcion().getDesecho()
							.equals(desechoPeligroso)
							&& eliminacionDesecho.getTipoEliminacionDesecho().getCodigoModalidad().equals(idModalidad)) {
						listaEliminacionDesechoParaEliminar.add(eliminacionDesecho);
						isDesechoRecepcionAEliminar = true;
						eliminarDesechoModalidadAsociadaPorTipoEliminacion(aprobacionRequisitosTecnicos,
								desechoPeligroso, eliminacionDesecho.getTipoEliminacionDesecho());
					}
				}
				if (isDesechoRecepcionAEliminar) {
					listaEliminacionRecepcionParaEliminar.add(eliminacionRecepcion);
				}

			}
		}
		crudServiceBean.delete(listaEliminacionDesechoParaEliminar);
		crudServiceBean.delete(listaEliminacionRecepcionParaEliminar);
		return isDesechoRecepcionAEliminar;

	}
	
	@SuppressWarnings("unchecked")
    public List<TipoEliminacionDesecho> getTipoEliminacionPorDesechos(DesechoPeligroso desecho,
            AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) throws ServiceException {
        StringBuilder query = new StringBuilder();

        query.append("select wdty.* ");
        query.append("from suia_iii.approval_technical_requirements apte ");
        query.append("inner join suia_iii.waste_disposal_receipt wadr on(apte.apte_id=wadr.apte_id) ");
        query.append("inner join suia_iii.waste_disposal wadi on(wadr.wadr_id=wadi.wadr_id) ");
        query.append("inner join suia_iii.waste_disposal_types wdty on(wdty.wdty_id=wadi.wdty_id) ");
        query.append("inner join suia_iii.receipt_hazardous_waste rehw on(rehw.rehw_id=wadr.rehw_id) ");
        query.append("inner join general_catalogs geca on (geca.geca_id=rehw.rehw_physical_state_id) ");
        query.append("inner join suia_iii.phisical_state_types psty on (psty.psty_id=rehw.rehw_physical_state_id) ");
        query.append("inner join suia_iii.waste_dangerous wada on(wada.wada_id=rehw.wada_id) ");
        query.append("where wadi.wadi_status=true and wada.wada_status=true and wadr.wadr_status=true ");
        query.append("and apte.apte_id="+ aprobacionRequisitosTecnicos.getId()+" ");
        query.append("and wada.wada_id="+ desecho.getId()+" ");        
        query.append("order by 1");

        List<TipoEliminacionDesecho> lista = (List<TipoEliminacionDesecho>) crudServiceBean.findNativeQuery(query.toString(),
        		TipoEliminacionDesecho.class);
        return lista;
    }
	
	public ModalidadGestionDesechos getModalidadPorId(Integer idModalidad) {
		return crudServiceBean.find(ModalidadGestionDesechos.class, idModalidad);

	}

}
