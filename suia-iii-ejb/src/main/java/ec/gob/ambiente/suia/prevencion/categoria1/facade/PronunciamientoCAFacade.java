package ec.gob.ambiente.suia.prevencion.categoria1.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.CertificadoAmbientalPronunciamiento;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.DocumentosTareasProceso;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class PronunciamientoCAFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private DocumentosFacade documentosFacade;
	
	private static final Logger LOG = Logger.getLogger(PronunciamientoCAFacade.class);	
			
	public void guardar(CertificadoAmbientalPronunciamiento informe) {
		crudServiceBean.saveOrUpdate(informe);
	}


	@SuppressWarnings("unchecked")
	public CertificadoAmbientalPronunciamiento getpronunciamientoPorIdProyecto(int idProyecto) {
		Map<String, Object> parameters = new ConcurrentHashMap<String, Object>();
		parameters.put("idProyecto", idProyecto);

		List<CertificadoAmbientalPronunciamiento> result = (List<CertificadoAmbientalPronunciamiento>) crudServiceBean.findByNamedQuery(CertificadoAmbientalPronunciamiento.FIND_BY_PROJECT,parameters);
		if (result != null && !result.isEmpty()) {
			return result.get(0);
		}
		return null;
	}
	
    public byte[] descargarFile(Documento documento) throws CmisAlfrescoException {
        return documentosFacade.descargar(documento.getIdAlfresco());
    }    
    
    public Documento guardarDocumento(CertificadoAmbientalPronunciamiento informe,Documento documento,TipoDocumentoSistema tipoDocumento,DocumentosTareasProceso documentoTarea){
    	try {
    		return documentosFacade.guardarDocumentoAlfresco(informe.getProyecto().getCodigo(),Constantes.CARPETA_CATEGORIA_UNO,documentoTarea.getProcessInstanceId(),documento,tipoDocumento,documentoTarea);
    	} catch (Exception e) {
    		LOG.error(e.getMessage());
    		return null;
    	}    	
    }
    
    
	public PlantillaReporte obtenerPlantillaReporte(Integer tipoDocumentoId) {
		try {
			Map<String, Object> parametros = new HashMap();
			parametros.put("p_tipoDocumentoId", tipoDocumentoId);

			List<PlantillaReporte> lista = this.crudServiceBean.findByNamedQueryGeneric(PlantillaReporte.OBTENER_PLANTILLA_POR_INFORME, parametros);
			if ((lista != null) && (!lista.isEmpty())) {
				return (PlantillaReporte) lista.get(0);
			}
		} catch (RuntimeException e) {
			throw new RuntimeException(e);
		}
		return null;
	}
	
	
}