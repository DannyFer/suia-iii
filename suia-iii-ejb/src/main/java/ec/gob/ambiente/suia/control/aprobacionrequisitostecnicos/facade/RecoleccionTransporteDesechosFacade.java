/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.service.LavadoContenedoresTratamientoEfluentesService;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.Periodicidad;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.LavadoContenedoresYTratamientoEfluentes;

@Stateless
public class RecoleccionTransporteDesechosFacade {

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private LavadoContenedoresTratamientoEfluentesService lavadoContenedoresTratamientoEfluentesService;

	@EJB
	private DocumentosFacade documentosFacade;

	@SuppressWarnings("unchecked")
	public List<LavadoContenedoresYTratamientoEfluentes> guardarListaLavadoContenedor(
			List<LavadoContenedoresYTratamientoEfluentes> listaLavadoContenedor) {

		return (List<LavadoContenedoresYTratamientoEfluentes>) crudServiceBean.saveOrUpdate(listaLavadoContenedor);
	}

	public void eliminarListaLavadoContenedor(List<LavadoContenedoresYTratamientoEfluentes> listaLavadoContenedor) {
		crudServiceBean.delete(listaLavadoContenedor);
	}

	public List<LavadoContenedoresYTratamientoEfluentes> getListaLavadoContenedor(
			AprobacionRequisitosTecnicos aprobacionRequisitosTecnico) {
		return lavadoContenedoresTratamientoEfluentesService
				.getListaLavadoContenedoresYTratamientoEfluentes(aprobacionRequisitosTecnico);
	}

	public void eliminarListaLavadoContenedorExistentes(AprobacionRequisitosTecnicos aprobacionRequisitosTecnico) {
		List<LavadoContenedoresYTratamientoEfluentes> lavados = null;
		lavados = getListaLavadoContenedor(aprobacionRequisitosTecnico);
		if (lavados != null)
			eliminarListaLavadoContenedor(lavados);
	}

	@SuppressWarnings("unchecked")
	public List<Periodicidad> getPeriodicidad() {
		return (List<Periodicidad>) crudServiceBean.findAll(Periodicidad.class);
	}

	public byte[] descargarFile(Documento documento) throws CmisAlfrescoException {
		return documentosFacade.descargar(documento.getIdAlfresco());
	}

}
