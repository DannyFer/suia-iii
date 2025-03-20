package ec.gob.ambiente.rcoa.demo.facade;

import java.util.Calendar;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.chemistry.opencmis.client.api.Document;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.rcoa.demo.model.DemoDocumentos;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.UtilAlfresco;

@Stateless
public class DemoDocumentosFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
	private AlfrescoServiceBean alfrescoServiceBean;
	
	public DemoDocumentos guardar(DemoDocumentos obj)
	{
		return crudServiceBean.saveOrUpdate(obj);
	}
	
	public DemoDocumentos guardarDocumentoAlfresco(String codigoProyecto, String nombreProceso, Long idProceso, DemoDocumentos documento, TipoDocumentoSistema tipoDocumento) throws ServiceException, CmisAlfrescoException{
		
		nombreProceso = nombreProceso.replace(".", "_");
		String idProces = null;
		if (idProceso != null) {
			idProces = idProceso.toString();
		}
		
		String folderName = UtilAlfresco.generarEstructuraCarpetas(
				codigoProyecto, nombreProceso, idProces);
		String folderId = alfrescoServiceBean.createFolderStructure(folderName,
				Constantes.getRootOpinionPublica());
		Document documentCreate = alfrescoServiceBean.fileSaveStream(documento
				.getContenidoDocumento(),
				Calendar.getInstance().getTimeInMillis() + "-"
						+ documento.getNombreDocumento().replace("/", "-"), folderId,
				nombreProceso, folderName, documento.getIdTabla());
		
		DemoDocumentos documentoAl = UtilAlfresco.crearDemoDocumento(documento.getNombreDocumento() == null ? documentCreate.getName()
				: documento.getNombreDocumento(), documento.getExtencionDocumento(),
				documentCreate.getId(), documento.getIdTabla(), documento.getTipo(),
						 documento.getNombreDocumento(), documento.getNombreTabla(), tipoDocumento);

		
		DemoDocumentos documentoAlfreco = crudServiceBean.saveOrUpdate(documentoAl);
		
		
		return documentoAlfreco;
	}
	
}
