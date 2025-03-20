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
import ec.gob.ambiente.suia.domain.CertificadoAmbientalInformeForestal;
import ec.gob.ambiente.suia.domain.CertificadoAmbientalInformeForestalFormaCoordenada;
import ec.gob.ambiente.suia.domain.Coordenada;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.DocumentosTareasProceso;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class InformeInspeccionForestalCAFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private DocumentosFacade documentosFacade;
	
	private static final Logger LOG = Logger.getLogger(InformeInspeccionForestalCAFacade.class);	
			
	public void guardarInforme(CertificadoAmbientalInformeForestal informe) {
		crudServiceBean.saveOrUpdate(informe);
	}
	
	public void guardarFormasCoordenadas(List<CertificadoAmbientalInformeForestalFormaCoordenada> formaCoordenadas) {		
		desactivarFormasCoordenadasInforme(formaCoordenadas.get(0).getInformeForestal());		
		
		
		for (CertificadoAmbientalInformeForestalFormaCoordenada formaCoordenada : formaCoordenadas) {
			List<Coordenada> coordenadas=formaCoordenada.getCoordenadas();
			for (Coordenada coordenada : coordenadas) {
				coordenada.setFormasInformeForestalCA(formaCoordenada);
			}
			
			crudServiceBean.saveOrUpdate(formaCoordenada);
			crudServiceBean.saveOrUpdate(coordenadas);			
		}
	}
	
	private void desactivarFormasCoordenadasInforme(CertificadoAmbientalInformeForestal informe)
	{
		List<CertificadoAmbientalInformeForestalFormaCoordenada> formaCoordenadas=getCoordenadasPorIdInformeForestal(informe.getId());
		if(formaCoordenadas!=null){
			for (CertificadoAmbientalInformeForestalFormaCoordenada formaCoordenada : formaCoordenadas) {
				formaCoordenada.setEstado(false);				
			}
			crudServiceBean.saveOrUpdate(formaCoordenadas);
		}
	
	}

	@SuppressWarnings("unchecked")
	public CertificadoAmbientalInformeForestal getInformePorIdProyecto(int idProyecto) {
		Map<String, Object> parameters = new ConcurrentHashMap<String, Object>();
		parameters.put("idProyecto", idProyecto);

		List<CertificadoAmbientalInformeForestal> result = (List<CertificadoAmbientalInformeForestal>) crudServiceBean.findByNamedQuery(CertificadoAmbientalInformeForestal.FIND_BY_PROJECT,parameters);
		if (result != null && !result.isEmpty()) {
			return result.get(0);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private CertificadoAmbientalInformeForestal getInformePorIdProyecto(String codigoInforme) {
		Map<String, Object> parameters = new ConcurrentHashMap<String, Object>();
		parameters.put("codigoInforme", codigoInforme);

		List<CertificadoAmbientalInformeForestal> result = (List<CertificadoAmbientalInformeForestal>) crudServiceBean.findByNamedQuery(CertificadoAmbientalInformeForestal.FIND_BY_CODE,parameters);
		if (result != null && !result.isEmpty()) {
			return result.get(0);
		}
		return null;
	}	
	
	public boolean codigoUsado(CertificadoAmbientalInformeForestal informe)
	{
		CertificadoAmbientalInformeForestal i=getInformePorIdProyecto(informe.getCodigo());		
		if(i!=null && i.getId().compareTo(informe.getId())!=0)
			return true;
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public List<CertificadoAmbientalInformeForestalFormaCoordenada> getCoordenadasPorIdInformeForestal(int idInformeForestal) {
		Map<String, Object> parameters = new ConcurrentHashMap<String, Object>();
		parameters.put("idInformeForestal", idInformeForestal);

		List<CertificadoAmbientalInformeForestalFormaCoordenada> result = (List<CertificadoAmbientalInformeForestalFormaCoordenada>) crudServiceBean.findByNamedQuery(CertificadoAmbientalInformeForestalFormaCoordenada.FIND_BY_REPORT,parameters);
		if (result != null && !result.isEmpty()) {
			return result;
		}
		return null;
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
	
    public Documento guardarDocumento(String codigo,Documento documento,TipoDocumentoSistema tipoDocumento,DocumentosTareasProceso documentoTarea){
    	try {
    		return documentosFacade.guardarDocumentoAlfresco(codigo,Constantes.CARPETA_CATEGORIA_UNO,documentoTarea.getProcessInstanceId(),documento,tipoDocumento,documentoTarea);
    	} catch (Exception e) {
    		LOG.error(e.getMessage());
    		return null;
    	}    	
    }
    
    public byte[] descargarFile(Documento documento) throws CmisAlfrescoException {
        return documentosFacade.descargar(documento.getIdAlfresco());
    }
	
	
}