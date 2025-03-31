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
import ec.gob.ambiente.suia.domain.CertificadoAmbientalInformeSnap;
import ec.gob.ambiente.suia.domain.CertificadoAmbientalInformeSnapEspecie;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.DocumentosTareasProceso;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class InformeInspeccionSnapCAFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private DocumentosFacade documentosFacade;
	
	private static final Logger LOG = Logger.getLogger(InformeInspeccionSnapCAFacade.class);	
			
	public void guardarInforme(CertificadoAmbientalInformeSnap informe) {
		crudServiceBean.saveOrUpdate(informe);
	}
	
	public void guardarInformeEspecies(CertificadoAmbientalInformeSnapEspecie especie) {
		crudServiceBean.saveOrUpdate(especie);
	}

	@SuppressWarnings("unchecked")
	public CertificadoAmbientalInformeSnap getInformePorIdProyecto(int idProyecto) {
		Map<String, Object> parameters = new ConcurrentHashMap<String, Object>();
		parameters.put("idProyecto", idProyecto);

		List<CertificadoAmbientalInformeSnap> result = (List<CertificadoAmbientalInformeSnap>) crudServiceBean.findByNamedQuery(CertificadoAmbientalInformeSnap.FIND_BY_PROJECT,parameters);
		if (result != null && !result.isEmpty()) {
			return result.get(0);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private CertificadoAmbientalInformeSnap getInformePorIdProyecto(String codigoInforme) {
		Map<String, Object> parameters = new ConcurrentHashMap<String, Object>();
		parameters.put("codigoInforme", codigoInforme);

		List<CertificadoAmbientalInformeSnap> result = (List<CertificadoAmbientalInformeSnap>) crudServiceBean.findByNamedQuery(CertificadoAmbientalInformeSnap.FIND_BY_CODE,parameters);
		if (result != null && !result.isEmpty()) {
			return result.get(0);
		}
		return null;
	}
	
	public boolean codigoUsado(CertificadoAmbientalInformeSnap informe)
	{
		CertificadoAmbientalInformeSnap i=getInformePorIdProyecto(informe.getCodigo());
		if(i!=null && i.getId().compareTo(informe.getId())!=0)
			return true;
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public List<CertificadoAmbientalInformeSnapEspecie> getEspeciesPorInforme(int idInforme) {
		Map<String, Object> parameters = new ConcurrentHashMap<String, Object>();
		parameters.put("idInforme", idInforme);

		List<CertificadoAmbientalInformeSnapEspecie> result = (List<CertificadoAmbientalInformeSnapEspecie>) crudServiceBean.findByNamedQuery(CertificadoAmbientalInformeSnapEspecie.FIND_BY_REPORT_SNAP,parameters);
		if (result != null && !result.isEmpty()) {
			return result;
		}
		return null;
	}
	
    public byte[] descargarFile(Documento documento) throws CmisAlfrescoException {
        return documentosFacade.descargar(documento.getIdAlfresco());
    }  
    
    public Documento guardarDocumento(String codigoProyecto,Documento documento,TipoDocumentoSistema tipoDocumento,DocumentosTareasProceso documentoTarea){
    	try {
    		return documentosFacade.guardarDocumentoAlfresco(codigoProyecto,Constantes.CARPETA_CATEGORIA_UNO,documentoTarea.getProcessInstanceId(),documento,tipoDocumento,documentoTarea);
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