/*
 * Copyright 2015 MAGMASOFT
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.service.DetalleCapacidadCapacidadTotalAlmacenamientoDesechoPeligrosoService;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.service.DetalleCapacidadConfinamientoDesechoPeligrosoService;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.service.ModalidadDisposicionFinalService;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.ModalidadGestionDesechos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.DetalleCapacidadConfinamientoDesechoPeligroso;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.DetalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligroso;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.ModalidadDisposicionFinal;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.validacionseccion.facade.ValidacionSeccionesFacade;

/**
 * <b> <b> Clase facade para modalidad disposición final. </b> </b>
 * 
 * @author Jonathan Guerrero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Jonathan Guerrero $, $Date: 22/06/2015 $]
 *          </p>
 */
@Stateless
public class ModalidadDisposicionFinalFacade {

	@EJB
	private DetalleCapacidadConfinamientoDesechoPeligrosoService detalleCapacidadConfinamientoDesechoPeligrosoService;

	@EJB
	private DetalleCapacidadCapacidadTotalAlmacenamientoDesechoPeligrosoService detalleCapacidadCapacidadTotalAlmacenamientoDesechoPeligrosoService;

	@EJB
	DocumentosFacade documentosFacade;

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private ModalidadDisposicionFinalService modalidadDisposicionFinalService;

	@EJB
	private ModalidadesFacade modalidadesFacade;

	@EJB
	private ValidacionSeccionesFacade validacionSeccionesFacade;

	public byte[] descargarFile(Documento documento) throws CmisAlfrescoException {
		return documentosFacade.descargar(documento.getIdAlfresco());
	}

	public ModalidadDisposicionFinal guardar(ModalidadDisposicionFinal modalidad, long idProceso, long idTarea)
			throws ServiceException, CmisAlfrescoException {

		modalidad.setPlanoPlanta(documentosFacade.subirFileAlfrescoSinProyectoAsociado(modalidad.getPlanoPlanta(),
				modalidad.getAprobacionRequisitosTecnicos().getSolicitud(), idProceso, idTarea,
				ModalidadDisposicionFinal.class.getSimpleName(),
				AprobacionRequisitosTecnicosFacade.NOMBRE_CARPETA_MODALIDAD_DISPOSICION_FINAL,
				TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));

		modalidad.setRequisitos(documentosFacade.subirFileAlfrescoSinProyectoAsociado(modalidad.getRequisitos(),
				modalidad.getAprobacionRequisitosTecnicos().getSolicitud(), idProceso, idTarea,
				ModalidadDisposicionFinal.class.getSimpleName(),
				AprobacionRequisitosTecnicosFacade.NOMBRE_CARPETA_MODALIDAD_DISPOSICION_FINAL,
				TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));

		modalidad.setAnexos(documentosFacade.subirFileAlfrescoSinProyectoAsociado(modalidad.getAnexos(), modalidad.getAprobacionRequisitosTecnicos().getSolicitud(),
				idProceso, idTarea, ModalidadDisposicionFinal.class
				.getSimpleName(), AprobacionRequisitosTecnicosFacade.NOMBRE_CARPETA_MODALIDAD_DISPOSICION_FINAL,
				TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));
		modalidad = crudServiceBean.saveOrUpdate(modalidad);
		return modalidad;
	}

	public DetalleCapacidadConfinamientoDesechoPeligroso guardarDetalleCapacidadConfinamientoDesechoPeligroso(
			DetalleCapacidadConfinamientoDesechoPeligroso detalleCapacidadConfinamientoDesechoPeligroso)
			throws ServiceException {
		detalleCapacidadConfinamientoDesechoPeligroso = crudServiceBean
				.saveOrUpdate(detalleCapacidadConfinamientoDesechoPeligroso);
		return detalleCapacidadConfinamientoDesechoPeligroso;
	}

	public List<DetalleCapacidadConfinamientoDesechoPeligroso> obtenerDetalleCapacidadConfinamientoDPXModalidadDispFin(
			Integer idModalidadDispFinal) throws ServiceException {
		return detalleCapacidadConfinamientoDesechoPeligrosoService
				.getDetalleCapacidadConfinamientoDesechoPeligrosos(idModalidadDispFinal);
	}

	public DetalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligroso guardarDetalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligroso(
			DetalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligroso detalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligroso)
			throws ServiceException {
		detalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligroso = crudServiceBean
				.saveOrUpdate(detalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligroso);
		return detalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligroso;
	}

	public List<DetalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligroso> obtenerDetalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligrosoXModalidadDispFin(
			Integer idModalidadDispFinal) throws ServiceException {
		return detalleCapacidadCapacidadTotalAlmacenamientoDesechoPeligrosoService
				.getDetalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligrosos(idModalidadDispFinal);
	}

	public ModalidadDisposicionFinal getModalidadDisposicionFinal(
			AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) throws ServiceException {
		ModalidadDisposicionFinal modalidad = modalidadDisposicionFinalService
				.getModalidadDisposicionFinal(aprobacionRequisitosTecnicos);
		return modalidad;
	}

	public void eliminarModalidadDisposicionFinalExistente(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) {
		ModalidadDisposicionFinal modalidadDisposicionFinal = null;
		try {
			modalidadDisposicionFinal = getModalidadDisposicionFinal(aprobacionRequisitosTecnicos);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		if (modalidadDisposicionFinal != null)
			crudServiceBean.delete(modalidadDisposicionFinal);
	}

	public List<DesechoPeligroso> getDesechosDisposicionFinal(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos)
			throws ServiceException {
		return (List<DesechoPeligroso>) modalidadesFacade.getDesechosPorTipoElimiancion(
				ModalidadGestionDesechos.ID_MODALIDAD_DISPOSICION_FINAL, aprobacionRequisitosTecnicos);

	}

	public void guardarPaginaComoCompleta(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos)
			throws ServiceException {
		validacionSeccionesFacade.guardarValidacionSeccion("AprobacionRequisitosTecnicos", "modalidadDisposicionFinal",
				aprobacionRequisitosTecnicos.getId().toString());
	}

	public void guardarPaginaComoInCompleta(AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos)
			throws ServiceException {
		validacionSeccionesFacade.guardarValidacionSeccion("AprobacionRequisitosTecnicos", "modalidadDisposicionFinal",
				aprobacionRequisitosTecnicos.getId().toString(), false);

	}

	public boolean validarFormulario(AprobacionRequisitosTecnicos aprobacionRequisitos) {
		try {
			ModalidadDisposicionFinal modalidadDisposicionFinal = getModalidadDisposicionFinal(aprobacionRequisitos);
			if(modalidadDisposicionFinal == null)
				return false;

			if(!modalidadDisposicionFinal.isTransportePropio() && !modalidadDisposicionFinal.isTransporteContratado())
				return false;

			if(modalidadDisposicionFinal.isTransporteContratado())
				return !modalidadDisposicionFinal.getNombreEmpresaAutorizada().equals("");

			if(modalidadDisposicionFinal.getDetalleCapacidadConfinamientoDesechoPeligrosos().isEmpty())
				return false;

			if(modalidadDisposicionFinal.getDetalleCapacidadTotalAlmacenamientoMateriaPrimaDesechoPeligrosos().isEmpty())
				return false;

			if(modalidadDisposicionFinal.getRequisitos() == null)
				return false;

			if(modalidadDisposicionFinal.getAnexos() == null)
				return false;

			return true;
		} catch (ServiceException e) {
			e.printStackTrace();
			return false;
		}
	}
}
