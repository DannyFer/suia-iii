/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.commons.lang.SerializationUtils;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.FichaAmbientalMineria;
import ec.gob.ambiente.suia.domain.HerramientaMinera;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.EntityAdjunto;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.EjecutarSentenciasNativas;
import ec.gob.ambiente.suia.utils.UtilAlfresco;

/**
 *
 * @author christian
 */
@LocalBean
@Stateless
public class FichaAmbientalMineriaFacade {

    @EJB
    private CrudServiceBean crudServiceBean;
    @EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
    private AlfrescoServiceBean alfrescoServiceBean;
    @EJB
    private DocumentosFacade documentosFacade;
    @EJB
    private EjecutarSentenciasNativas ejecutarSentenciasNativas;

    @EJB
    SecuenciasFacade secuenciasFacade;

    public FichaAmbientalMineria guardarFicha(final FichaAmbientalMineria fichaAmbientalMineria) throws ServiceException {
        try {
            return crudServiceBean.saveOrUpdate(fichaAmbientalMineria);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    public FichaAmbientalMineria guardarFichaCaracteristicas(final FichaAmbientalMineria fichaAmbientalMineria) throws ServiceException {
        try {
            String query = "delete from suia_iii.mining_tools where mien_id=" + fichaAmbientalMineria.getId();
            ejecutarSentenciasNativas.ejecutarSentenciasNativasDml(query);        	
            return crudServiceBean.saveOrUpdate(fichaAmbientalMineria);
        } catch (SQLException e) {
            throw new ServiceException(e);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    public FichaAmbientalMineria guardarFichaAdjunto(final FichaAmbientalMineria fichaAmbientalMineria, final EntityAdjunto entityAdjunto) throws ServiceException {
        try {
            FichaAmbientalMineria obj = crudServiceBean.saveOrUpdate(fichaAmbientalMineria);
            List<Documento> listaDocumentos = documentosFacade.documentoXTablaId(obj.getId(), FichaAmbientalMineria.class.getSimpleName());
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
                    fichaAmbientalMineria.getClass().getSimpleName(),
                    TipoDocumentoSistema.TIPO_DOCUMENTO_ANEXOS));
            crudServiceBean.saveOrUpdate(UtilAlfresco
                    .crearDocumentoTareaProceso(documento,
                            0, 0L));
            return obj;
        } catch (Exception e) {
            throw new ServiceException(e);
        }

    }

    @SuppressWarnings("unchecked")
	public FichaAmbientalMineria obtenerPorProyecto(final ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idProyecto", proyectoLicenciamientoAmbiental.getId());
            List<FichaAmbientalMineria> lista = (List<FichaAmbientalMineria>) crudServiceBean.findByNamedQuery(FichaAmbientalMineria.LISTAR_POR_PROYECTO, params);
            return lista != null && !lista.isEmpty() ? lista.get(0) : null;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    public FichaAmbientalMineria obtenerPorId(final Integer id) {
        return crudServiceBean.find(FichaAmbientalMineria.class, id);
    }

    public EntityAdjunto obternerPorFicha(final FichaAmbientalMineria fichaAmbientalMineria) throws ServiceException, CmisAlfrescoException {
        try {
            EntityAdjunto obj = new EntityAdjunto();
            List<Documento> docs = documentosFacade.documentoXTablaId(fichaAmbientalMineria.getId(), fichaAmbientalMineria.getClass().getSimpleName());
            if (docs != null && !docs.isEmpty()) {
                obj.setExtension(docs.get(0).getExtesion());
                obj.setMimeType(docs.get(0).getMime());
                obj.setNombre(docs.get(0).getNombre());
                obj.setArchivo(documentosFacade.descargar(docs.get(0).getIdAlfresco()));
            } else {
                obj = null;
            }
            return obj;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    public EntityAdjunto obternerPorFichaPuntos(final FichaAmbientalMineria fichaAmbientalMineria) throws ServiceException, CmisAlfrescoException {
        try {
            EntityAdjunto obj = new EntityAdjunto();
            List<Documento> docs = documentosFacade.documentoXTablaId(fichaAmbientalMineria.getId(), fichaAmbientalMineria.getClass().getSimpleName() + "-1");
            if (docs != null && !docs.isEmpty()) {
                obj.setExtension(docs.get(0).getExtesion());
                obj.setMimeType(docs.get(0).getMime());
                obj.setNombre(docs.get(0).getNombre());
                obj.setArchivo(documentosFacade.descargar(docs.get(0).getIdAlfresco()));
            } else {
                obj = null;
            }
            return obj;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    // LAST MOD 26/01/2016
    public String generarNoResolucion(ProyectoLicenciamientoAmbiental proyectoActivo){
        String numeroResolucion = null;
        try {
        if (proyectoActivo.getAreaResponsable().getTipoArea().getId().equals(3)) {
            Date fecha = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(fecha);
            int anno = calendar.get(Calendar.YEAR);

                numeroResolucion = proyectoActivo.getAreaResponsable()
                        .getAreaAbbreviation()
                        + "-"+anno+"-"
                        + Long.toString(secuenciasFacade
                        .getNextValueDedicateSequence("entes"));
        } else {

            numeroResolucion = Long
                    .toString(secuenciasFacade
                            .getNextValueDedicateSequence("numeroResolucionCategoriaII"));

    }
    } catch (Exception e) {
        e.printStackTrace();
    }
        return numeroResolucion;
    }
    public void cambiarEstadoFinalizacion(int idProyecto) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idProyecto", idProyecto);
            List<FichaAmbientalMineria> lista = (List<FichaAmbientalMineria>) crudServiceBean.findByNamedQuery(FichaAmbientalMineria.LISTAR_POR_PROYECTO, params);
            if (lista != null && !lista.isEmpty()){
                lista.get(0).setFinalizado(true);
                crudServiceBean.saveOrUpdate(lista.get(0));
            }

        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }

    }
    
    //Cris F: método para encontrar ficha ambiental minera historical
    
    public List<FichaAmbientalMineria> buscarFichaAmbientalMineraHistorial(int idProyecto){
    	try {
    		Query query = crudServiceBean.getEntityManager().createQuery("SELECT f FROM FichaAmbientalMineria f WHERE f.idProyecto = :idProyecto AND f.estado = true and idRegistroOriginal != null order by 1 DESC");
    		query.setParameter("idProyecto", idProyecto);
    		
    		List<FichaAmbientalMineria> resultado = query.getResultList();
    		
    		if(resultado != null){
    			return resultado;
    		}			
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
    }
    
    /**
     * Cris F: historial 
     */
    private boolean compararParaHistorial(FichaAmbientalMineria fichaAmbientalMineria, FichaAmbientalMineria fichaAmbientalMineriaBdd){
    	try {    		
            
    		if(((fichaAmbientalMineria.getCatalogoTipoMaterial() == null && fichaAmbientalMineriaBdd.getCatalogoTipoMaterial() == null) ||
    				(fichaAmbientalMineria.getCatalogoTipoMaterial() != null && fichaAmbientalMineriaBdd.getCatalogoTipoMaterial() != null && 
    				fichaAmbientalMineria.getCatalogoTipoMaterial().equals(fichaAmbientalMineriaBdd.getCatalogoTipoMaterial()))) && 
    				((fichaAmbientalMineria.getPredio() == null && fichaAmbientalMineriaBdd.getPredio() == null) || 
    				(fichaAmbientalMineria.getPredio() != null && fichaAmbientalMineriaBdd.getPredio() != null && 
    				fichaAmbientalMineria.getPredio().equals(fichaAmbientalMineriaBdd.getPredio()))) && 
    				((fichaAmbientalMineria.getEtapa() == null && fichaAmbientalMineriaBdd.getEtapa() == null) || 
    				fichaAmbientalMineria.getEtapa() != null && fichaAmbientalMineriaBdd.getEtapa() != null && 
    				fichaAmbientalMineria.getEtapa().equals(fichaAmbientalMineriaBdd.getEtapa())) &&
    				((fichaAmbientalMineria.getOtrosPredios() == null && fichaAmbientalMineriaBdd.getOtrosPredios() == null) ||
    				(fichaAmbientalMineria.getOtrosPredios() != null && fichaAmbientalMineriaBdd.getOtrosPredios() != null && 
    				fichaAmbientalMineria.getOtrosPredios().equals(fichaAmbientalMineriaBdd.getOtrosPredios())))
    				){
    			//iguales
    			return true;
    		}else{
    			return false;
    		}			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    }
    
    public FichaAmbientalMineria guardarFichaNuevo(final FichaAmbientalMineria fichaAmbientalMineria) throws ServiceException {
        try {
        	FichaAmbientalMineria fichaAmbientalMineriaBdd = obtenerPorId(fichaAmbientalMineria.getId());
        	
        	if(fichaAmbientalMineriaBdd.getCatalogoTipoMaterial() != null){
        		if(!compararParaHistorial(fichaAmbientalMineria, fichaAmbientalMineriaBdd)){
            		FichaAmbientalMineria fichaHistorial = (FichaAmbientalMineria)SerializationUtils.clone(fichaAmbientalMineriaBdd);
            		fichaHistorial.setId(null);
            		fichaHistorial.setFechaHistorico(new Date());
            		fichaHistorial.setIdRegistroOriginal(fichaAmbientalMineriaBdd.getId());
            		crudServiceBean.saveOrUpdate(fichaHistorial);
            	}      
        	}
        	        	        	
            return crudServiceBean.saveOrUpdate(fichaAmbientalMineria);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
    
    public FichaAmbientalMineria guardarFichaCaracteristicasNuevo(final FichaAmbientalMineria fichaAmbientalMineria) throws ServiceException {
        try {        	
        	FichaAmbientalMineria fichaAmbientalMineriaBdd = obtenerPorId(fichaAmbientalMineria.getId());
        	
        	if(fichaAmbientalMineriaBdd.getCatalogoTipoMaterial() != null){
        		if(!compararParaHistorial(fichaAmbientalMineria, fichaAmbientalMineriaBdd)){
            		FichaAmbientalMineria fichaHistorial = (FichaAmbientalMineria)SerializationUtils.clone(fichaAmbientalMineriaBdd);
            		fichaHistorial.setId(null);
            		fichaHistorial.setFechaHistorico(new Date());
            		fichaHistorial.setIdRegistroOriginal(fichaAmbientalMineriaBdd.getId());
            		crudServiceBean.saveOrUpdate(fichaHistorial);
            	}
        	}
        	
        	
        	Query query =crudServiceBean.getEntityManager().createQuery("SELECT h from HerramientaMinera h where fichaAmbientalMineria = :fichaAmbientalMineria");
        	query.setParameter("fichaAmbientalMineria", fichaAmbientalMineria);
        	
        	List<HerramientaMinera> listaHerramientas = query.getResultList();
        	
        	if(listaHerramientas != null && !listaHerramientas.isEmpty()){
        		for(HerramientaMinera herramienta : listaHerramientas){
        			HerramientaMinera herramientaHistorial = (HerramientaMinera)SerializationUtils.clone(herramienta);
        			herramientaHistorial.setId(null);
        			herramientaHistorial.setIdRegistroOriginal(herramienta.getId());
        			herramientaHistorial.setFechaHistorico(new Date());
        			crudServiceBean.saveOrUpdate(herramientaHistorial);
        			
        			herramienta.setEstado(false);
        			crudServiceBean.saveOrUpdate(herramienta);
        		}        		
        	}  
        	
        	if(fichaAmbientalMineriaBdd.getCatalogoTipoMaterial() != null)
        		fichaAmbientalMineria.setFechaHistorico(new Date());
        	
            return crudServiceBean.saveOrUpdate(fichaAmbientalMineria);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
	
	//MarielaG para historial
    public List<FichaAmbientalMineria> obtenerAllFichaAmbientalMineria(Integer idProyecto) throws ServiceException {
        List<FichaAmbientalMineria> lista = null;
        try {
            lista = crudServiceBean.getEntityManager()
                    .createQuery("From FichaAmbientalMineria f where idProyecto = :idProyecto order by id desc")
                    .setParameter("idProyecto", idProyecto).getResultList();
            if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista;
			}
        } catch (Exception e) {
            throw new ServiceException("Ocurrió un problema al recuperar los datos");
        }
    }
    
    //Fin del codigo para historial

	public String generarNoResolucionRcoa() {
		String numeroResolucion = null;
		try {
			numeroResolucion = Long
						.toString(secuenciasFacade
								.getNextValueDedicateSequence("numeroResolucionCategoriaII"));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return numeroResolucion;
	}
}
