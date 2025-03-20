/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.chemistry.opencmis.client.api.Document;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.EntityAdjunto;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.UtilAlfresco;

/**
 * 
 * @author jonathan
 */
@LocalBean
@Stateless
public class AdjuntosRegistroFichaAmbientalFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	@EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
	private AlfrescoServiceBean alfrescoServiceBean;
	@EJB
	private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;

	public void guardarAdjunto(final EntityAdjunto entityAdjunto,
			final String nombreClase, final Integer idTabla,
			final FichaAmbientalPma fichaAmbientalPma, String codigoProyecto,
			String nombreProceso, Long processId) throws Exception {
		if (entityAdjunto.getArchivo() == null) {
			return;
		}
		String folderName = UtilAlfresco.generarEstructuraCarpetas(
				codigoProyecto, nombreProceso, processId.toString());
		String folderId = alfrescoServiceBean.createFolderStructure(folderName,
				Constantes.getRootId());
		Document documentCreate = alfrescoServiceBean.fileSaveStream(
				entityAdjunto.getArchivo(), entityAdjunto.getNombre(),
				folderId, "RFA5", folderName, idTabla);

		Documento documento = crudServiceBean.saveOrUpdate(UtilAlfresco
				.crearDocumento(documentCreate.getName(),
						entityAdjunto.getMimeType(), documentCreate.getId(),
						idTabla, entityAdjunto.getMimeType(),
						entityAdjunto.getNombre(), nombreClase,
						TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));

		crudServiceBean.saveOrUpdate(UtilAlfresco.crearDocumentoTareaProceso(
				documento, 123, 148L));
		fichaAmbientalPmaFacade.guardarSoloFicha(fichaAmbientalPma);
	}

	public void guardarAdjunto(final EntityAdjunto entityAdjunto,
			final String nombreClase, final Integer idTabla,
			String codigoProyecto, String nombreProceso, Long processId)
			throws Exception {
		if (entityAdjunto.getArchivo() == null) {
			return;
		}
		String folderName = UtilAlfresco.generarEstructuraCarpetas(
				codigoProyecto, nombreProceso, processId.toString());
		String folderId = alfrescoServiceBean.createFolderStructure(folderName,
				Constantes.getRootId());
		Document documentCreate = alfrescoServiceBean.fileSaveStream(
				entityAdjunto.getArchivo(), entityAdjunto.getNombre(),
				folderId, "RFA5", folderName, idTabla);

		Documento documento = crudServiceBean.saveOrUpdate(UtilAlfresco
				.crearDocumento(documentCreate.getName(),
						entityAdjunto.getMimeType(), documentCreate.getId(),
						idTabla, entityAdjunto.getMimeType(),
						entityAdjunto.getNombre(), nombreClase,
						TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));

		crudServiceBean.saveOrUpdate(UtilAlfresco.crearDocumentoTareaProceso(
				documento, 123, 148L));
	}

	public void guardarAdjunto(final List<EntityAdjunto> listaAdjunto,
			final String nombreClase, final Integer idTabla,
			String codigoProyecto, String nombreProceso, Long processId)
			throws Exception {
		String folderName = UtilAlfresco.generarEstructuraCarpetas(
				codigoProyecto, nombreProceso, processId.toString());
		String folderId = alfrescoServiceBean.createFolderStructure(folderName,
				Constantes.getRootId());
		for (EntityAdjunto entityAdjunto : listaAdjunto) {
			if (entityAdjunto.getArchivo() != null) {
				Document documentCreate = alfrescoServiceBean.fileSaveStream(
						entityAdjunto.getArchivo(), entityAdjunto.getNombre(),
						folderId, "RFA5", folderName, idTabla);

				Documento documento = crudServiceBean.saveOrUpdate(UtilAlfresco
						.crearDocumento(documentCreate.getName(),
								entityAdjunto.getMimeType(),
								documentCreate.getId(), idTabla,
								entityAdjunto.getMimeType(),
								entityAdjunto.getNombre(), nombreClase,
								TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));

				crudServiceBean.saveOrUpdate(UtilAlfresco
						.crearDocumentoTareaProceso(documento, 123, 148L));
			}
		}
	}
}
