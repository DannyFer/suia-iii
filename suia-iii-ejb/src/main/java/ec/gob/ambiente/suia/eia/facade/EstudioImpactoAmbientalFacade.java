/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.eia.facade;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.DocumentosTareasProceso;
import ec.gob.ambiente.suia.domain.EstudioImpactoAmbiental;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;

/**
 *
 * @author christian
 */
@LocalBean
@Stateless
public class EstudioImpactoAmbientalFacade {

    @EJB
    private CrudServiceBean crudServiceBean;

    @EJB
    private DocumentosFacade documentosFacade;

    public EstudioImpactoAmbiental obtenerPorProyectoTipo(final ProyectoLicenciamientoAmbiental proyecto, final String tipo) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("p_idProyecto", proyecto.getId());
            params.put("p_tipo", tipo);
            List<EstudioImpactoAmbiental> lista = (List<EstudioImpactoAmbiental>) crudServiceBean.findByNamedQuery(EstudioImpactoAmbiental.BUSCAR_ESTUDIO_POR_ID_PROYECTO_TIPO, params);
            if (lista != null && !lista.isEmpty()) {
                return lista.get(0);
            } else {
                return null;
            }
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    public EstudioImpactoAmbiental obtenerPorIdProceso(final long idProceso, final String tipo) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idProceso", idProceso);
            params.put("p_tipo", tipo);
            List<EstudioImpactoAmbiental> lista = (List<EstudioImpactoAmbiental>) crudServiceBean.findByNamedQuery(EstudioImpactoAmbiental.BUSCAR_POR_ID_PROCESO, params);
            if (lista != null && !lista.isEmpty()) {
                return lista.get(0);
            } else {
                return null;
            }
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    public EstudioImpactoAmbiental guardar(final EstudioImpactoAmbiental estudioImpactoAmbiental) throws ServiceException {
        try {
            return crudServiceBean.saveOrUpdate(estudioImpactoAmbiental);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }

    }
    
    /**
     * Obtener estudio de impacto ambiental por Id
     * @param id
     * @return EstudioImpactoAmbiental
     */
    public EstudioImpactoAmbiental obtenerEIAPorId(final Integer id) {
        return crudServiceBean.find(EstudioImpactoAmbiental.class, id);
    }

    /**
     * Busca estudio de impacto ambiental por proyecto
     * @param proyectoLicenciamientoAmbiental
     * @return
     * @throws ServiceException
     */
    public EstudioImpactoAmbiental obtenerPorProyecto(final ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental) throws ServiceException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("idProyecto", proyectoLicenciamientoAmbiental.getId());
            List<EstudioImpactoAmbiental> lista = crudServiceBean.findByNamedQueryGeneric(EstudioImpactoAmbiental.BUSCAR_POR_PROYECTO, params);
            return lista != null && !lista.isEmpty() ? lista.get(0) : null;
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    public void ingresarDocumento(File file, Integer idEntidad,
                                             String codProyecto, long idProceso, long idTarea,
                                             TipoDocumentoSistema tipoDocumento, String nombreDocumento) throws Exception {

        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        byte[] data = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
        Documento documento = new Documento();
        documento.setIdTable(idEntidad);
        documento.setNombre(nombreDocumento);
        documento.setExtesion(mimeTypesMap.getContentType(file));
        documento.setNombreTabla(EstudioImpactoAmbiental.class.getSimpleName());
        documento.setMime(mimeTypesMap.getContentType(file));
        documento.setContenidoDocumento(data);
        DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
        documentoTarea.setIdTarea(idTarea);
        documentoTarea.setProcessInstanceId(idProceso);

        documentosFacade.guardarDocumentoAlfresco(codProyecto,
                "LicenciaAmbiental", idProceso, documento, tipoDocumento,
                documentoTarea);
    }
    
    /**
     * Cristina Flores 
     * Busqueda de registro historico 
     * @throws ServiceException 
     */
    
    public List<EstudioImpactoAmbiental> busquedaRegistrosHistorico(Integer idHistorico) throws ServiceException{
    	
    	try {
    		Map<String, Object> params = new HashMap<String, Object>();
            params.put("idHistorico", idHistorico);
                        
            List<EstudioImpactoAmbiental> lista = (List<EstudioImpactoAmbiental>) crudServiceBean.findByNamedQuery(EstudioImpactoAmbiental.BUSCAR_POR_HISTORICO, params);
            return (lista != null && !lista.isEmpty() ? lista : null);
            
			
		} catch (RuntimeException e) {
            //throw new ServiceException(e);
			e.printStackTrace();
			return null;
        }
    }




}
