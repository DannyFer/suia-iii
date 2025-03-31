package ec.gob.ambiente.suia.prevencion.categoria2.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.chemistry.opencmis.client.api.Document;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CronogramaValoradoPma;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.FichaAmbientalMineria;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;
import ec.gob.ambiente.suia.domain.InventarioForestalPma;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.EntityAdjunto;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.prevencion.categoria2.service.CronogramaValoradoServiceBean;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.UtilAlfresco;

@Stateless
public class CronogramaValoradoFacade {

    @EJB
    CronogramaValoradoServiceBean cronogramaValoradoServiceBean;
    @EJB
    private DocumentosFacade documentosFacade;
    @EJB
    private CrudServiceBean crudServiceBean;
    @EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
    private AlfrescoServiceBean alfrescoServiceBean;

    public void guardar(List<CronogramaValoradoPma> listaCronogramaValoradoPma, List<CronogramaValoradoPma> listaCronogramaValoradoPmaEliminar, FichaAmbientalPma fichaAmbientalPma) {
        cronogramaValoradoServiceBean.guardar(listaCronogramaValoradoPma, listaCronogramaValoradoPmaEliminar, fichaAmbientalPma);
    }

    public void guardarMineria(List<CronogramaValoradoPma> listaCronogramaValoradoPma, List<CronogramaValoradoPma> listaCronogramaValoradoPmaEliminar, FichaAmbientalMineria fichaAmbientalMineria) {
        cronogramaValoradoServiceBean.guardarMineria(listaCronogramaValoradoPma, listaCronogramaValoradoPmaEliminar, fichaAmbientalMineria);
    }

    public void guardarMineriaAdjunto(List<CronogramaValoradoPma> listaCronogramaValoradoPma, List<CronogramaValoradoPma> listaCronogramaValoradoPmaEliminar, FichaAmbientalMineria fichaAmbientalMineria, EntityAdjunto entityAdjunto, Documento documentoOriginal) throws ServiceException {
        try {
            List<Documento> listaDocumentos = documentosFacade.documentoXTablaId(fichaAmbientalMineria.getId(), FichaAmbientalMineria.class.getSimpleName() + "-1");
            for (Documento d : listaDocumentos) {
                d.setEstado(false);
            }
            crudServiceBean.saveOrUpdate(listaDocumentos);
            String folderName = UtilAlfresco.generarEstructuraCarpetas(fichaAmbientalMineria.getProyectoLicenciamientoAmbiental().getCodigo(), "registroAmbiental", null);
            String folderId = alfrescoServiceBean
                    .createFolderStructure(folderName,
                            Constantes.getRootId());

            Document documentCreate = alfrescoServiceBean.fileSaveStream(entityAdjunto.getArchivo(), entityAdjunto.getNombre(), folderId, "registroAmbiental", fichaAmbientalMineria.getProyectoLicenciamientoAmbiental().getCodigo(), fichaAmbientalMineria.getId());
            Documento documento = crudServiceBean.saveOrUpdate(UtilAlfresco.crearDocumento(
                    documentCreate.getName(), entityAdjunto.getMimeType(),
                    documentCreate.getId(), fichaAmbientalMineria.getId(), entityAdjunto.getMimeType(),
                    entityAdjunto.getNombre(),
                    fichaAmbientalMineria.getClass().getSimpleName() + "-1",
                    TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));
            
			if (documentoOriginal != null) {
				documentoOriginal.setIdHistorico(documento.getId());
				crudServiceBean.saveOrUpdate(documentoOriginal);
			}

            crudServiceBean.saveOrUpdate(UtilAlfresco
                    .crearDocumentoTareaProceso(documento,
                            0, 0L));

            cronogramaValoradoServiceBean.guardarMineria(listaCronogramaValoradoPma, listaCronogramaValoradoPmaEliminar, fichaAmbientalMineria);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }
    
    public void guardarAdjunto(List<CronogramaValoradoPma> listaCronogramaValoradoPma, List<CronogramaValoradoPma> listaCronogramaValoradoPmaEliminar, FichaAmbientalPma fichaAmbientalPma, EntityAdjunto entityAdjunto, Boolean isHistorial, Documento documentoOriginal) throws ServiceException {
        try {
            List<Documento> listaDocumentos = documentosFacade.documentoXTablaId(fichaAmbientalPma.getId(), FichaAmbientalMineria.class.getSimpleName() + "-1");
            for (Documento d : listaDocumentos) {
                d.setEstado(false);
            }
            crudServiceBean.saveOrUpdate(listaDocumentos);
            String folderName = UtilAlfresco.generarEstructuraCarpetas(fichaAmbientalPma.getProyectoLicenciamientoAmbiental().getCodigo(), "registroAmbiental", null);
            String folderId = alfrescoServiceBean
                    .createFolderStructure(folderName,
                            Constantes.getRootId());

            Document documentCreate = alfrescoServiceBean.fileSaveStream(entityAdjunto.getArchivo(), entityAdjunto.getNombre(), folderId, "registroAmbiental", fichaAmbientalPma.getProyectoLicenciamientoAmbiental().getCodigo(), fichaAmbientalPma.getId());
            Documento documento = crudServiceBean.saveOrUpdate(UtilAlfresco.crearDocumento(
                    documentCreate.getName(), entityAdjunto.getMimeType(),
                    documentCreate.getId(), fichaAmbientalPma.getId(), entityAdjunto.getMimeType(),
                    entityAdjunto.getNombre(),
                    fichaAmbientalPma.getClass().getSimpleName() + "-1",
                    TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));
            
            if(isHistorial){
				if (documentoOriginal != null) {
					documentoOriginal.setIdHistorico(documento.getId());
					crudServiceBean.saveOrUpdate(documentoOriginal);
				}
            }
            
            crudServiceBean.saveOrUpdate(UtilAlfresco
                    .crearDocumentoTareaProceso(documento,
                            0, 0L));

            cronogramaValoradoServiceBean.guardar(listaCronogramaValoradoPma, listaCronogramaValoradoPmaEliminar, fichaAmbientalPma);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public List<CronogramaValoradoPma> recuperarPorFichaPma(FichaAmbientalPma fichaAmbientalPma) throws ServiceException {
        return cronogramaValoradoServiceBean.recuperarPorFichaPma(fichaAmbientalPma);
    }

    public List<CronogramaValoradoPma> recuperarPorFichaMineria(FichaAmbientalMineria fichaAmbientalMineria) throws ServiceException {
        return cronogramaValoradoServiceBean.recuperarPorFichaMineria(fichaAmbientalMineria);
    }
    
    public List<CronogramaValoradoPma> listarTodoPorFichaPma(FichaAmbientalPma fichaAmbientalPma) throws ServiceException {
        return cronogramaValoradoServiceBean.listarTodoPorFichaPma(fichaAmbientalPma);
    }
    
    public List<CronogramaValoradoPma> recuperarTodoPorFichaMineria(FichaAmbientalMineria fichaAmbientalMineria) throws ServiceException {
        return cronogramaValoradoServiceBean.recuperarTodoPorFichaMineria(fichaAmbientalMineria);
    }
    
    public void guardarOriginal(List<CronogramaValoradoPma> listaCronograma){
		crudServiceBean.saveOrUpdate(listaCronograma);
	}
}
