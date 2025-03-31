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
import ec.gob.ambiente.suia.domain.EiaOpciones;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.EntityAdjunto;
import ec.gob.ambiente.suia.eia.descripcion.proyecto.facade.EiaOpcionesFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.UtilAlfresco;

/**
 *
 * @author christian
 */
@LocalBean
@Stateless
public class AdjuntosEiaFacade {

    @EJB
    private CrudServiceBean crudServiceBean;
    @EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
    private AlfrescoServiceBean alfrescoServiceBean;
    @EJB
    private EiaOpcionesFacade eiaOpcionesFacade;

    public void guardarAdjunto(final EntityAdjunto entityAdjunto, final String nombreClase, final Integer idTabla, final EiaOpciones eiaOpciones, final EstudioImpactoAmbiental estudioImpactoAmbiental) throws Exception {
        if (entityAdjunto.getArchivo() == null) {
            return;
        }
        String folderName = UtilAlfresco.generarEstructuraCarpetas(
                EstudioImpactoAmbiental.PROJECT_CODE,
                EstudioImpactoAmbiental.PROCESS_NAME,
                EstudioImpactoAmbiental.PROCESS_ID
                .toString());
        String folderId = alfrescoServiceBean.createFolderStructure(
                folderName, Constantes.getRootId());
        Document documentCreate = alfrescoServiceBean.fileSaveStream(
                entityAdjunto.getArchivo(), entityAdjunto.getNombre(), folderId, "EIA",
                folderName, idTabla);

        Documento documento = crudServiceBean.saveOrUpdate(UtilAlfresco.crearDocumento(
                documentCreate.getName(), entityAdjunto.getMimeType(),
                documentCreate.getId(), idTabla, entityAdjunto.getMimeType(),
                entityAdjunto.getNombre(),
                nombreClase,
                TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));

        crudServiceBean.saveOrUpdate(UtilAlfresco
                .crearDocumentoTareaProceso(documento,
                        123, 148L));
        eiaOpcionesFacade.guardar(estudioImpactoAmbiental, eiaOpciones);
    }
    
    public void guardarAdjunto(final EntityAdjunto entityAdjunto, final String nombreClase, final Integer idTabla) throws Exception {
        if (entityAdjunto.getArchivo() == null) {
            return;
        }
        String folderName = UtilAlfresco.generarEstructuraCarpetas(
                EstudioImpactoAmbiental.PROJECT_CODE,
                EstudioImpactoAmbiental.PROCESS_NAME,
                EstudioImpactoAmbiental.PROCESS_ID
                .toString());
        String folderId = alfrescoServiceBean.createFolderStructure(
                folderName, Constantes.getRootId());
        Document documentCreate = alfrescoServiceBean.fileSaveStream(
                entityAdjunto.getArchivo(), entityAdjunto.getNombre(), folderId, "EIA",
                folderName, idTabla);

        Documento documento = crudServiceBean.saveOrUpdate(UtilAlfresco.crearDocumento(
                documentCreate.getName(), entityAdjunto.getMimeType(),
                documentCreate.getId(), idTabla, entityAdjunto.getMimeType(),
                entityAdjunto.getNombre(),
                nombreClase,
                TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));

        crudServiceBean.saveOrUpdate(UtilAlfresco
                .crearDocumentoTareaProceso(documento,
                        123, 148L));
    }
    

    public void guardarAdjunto(final List<EntityAdjunto> listaAdjunto, final String nombreClase, final Integer idTabla) throws Exception {
        String folderName = UtilAlfresco.generarEstructuraCarpetas(
                EstudioImpactoAmbiental.PROJECT_CODE,
                EstudioImpactoAmbiental.PROCESS_NAME,
                EstudioImpactoAmbiental.PROCESS_ID
                .toString());
        String folderId = alfrescoServiceBean.createFolderStructure(
                folderName, Constantes.getRootId());
        for (EntityAdjunto entityAdjunto : listaAdjunto) {
            if (entityAdjunto.getArchivo() != null) {
                Document documentCreate = alfrescoServiceBean.fileSaveStream(
                        entityAdjunto.getArchivo(), entityAdjunto.getNombre(), folderId, "EIA",
                        folderName, idTabla);

                Documento documento = crudServiceBean.saveOrUpdate(UtilAlfresco.crearDocumento(
                        documentCreate.getName(), entityAdjunto.getMimeType(),
                        documentCreate.getId(), idTabla, entityAdjunto.getMimeType(),
                        entityAdjunto.getNombre(),
                        nombreClase,
                        TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));

                crudServiceBean.saveOrUpdate(UtilAlfresco
                        .crearDocumentoTareaProceso(documento,
                                123, 148L));
            }
        }

    }
    
}
