package ec.gob.ambiente.suia.prevencion.categoria2.facade;

import java.math.BigInteger;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.ConcesionMinera;
import ec.gob.ambiente.suia.domain.ProyectoBloque;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.ZonasFase;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.prevencion.categoria2.service.ProyectoLicenciaAmbientalServiceBean;
import ec.gob.ambiente.suia.utils.Constantes;

/**
 * 
 * @author karla.carvajal
 *
 */
@Stateless
public class ProyectoLicenciaAmbientalFacade {
	
	@EJB
	private ProyectoLicenciaAmbientalServiceBean proyectoLicenciaAmbientalServiceBean;
    @EJB
    private CrudServiceBean crudServiceBean;
	
	public ProyectoLicenciamientoAmbiental getProyectoPorCodigo(String codigo)
	{
		return proyectoLicenciaAmbientalServiceBean.getProyectoPorCodigo(codigo);
	}
	
	public UbicacionesGeografica getUbicacionProyectoPorIdProyecto(Integer idProyecto)
	{
		return proyectoLicenciaAmbientalServiceBean.getUbicacionProyectoPorIdProyecto(idProyecto);
	}

    public ProyectoLicenciamientoAmbiental getProyectoPorId(Integer idProyecto)
    {
        return proyectoLicenciaAmbientalServiceBean.getProyectoPorId(idProyecto);
    }

    public ProyectoLicenciamientoAmbiental getProyectoAreaPorId(Integer idProyecto)
    {
        return proyectoLicenciaAmbientalServiceBean.getProyectoAreaPorId(idProyecto);
    }
    public List<ConcesionMinera> getConcesionesMineraPorIdProyecto(Integer idProyecto) throws ServiceException {
        return proyectoLicenciaAmbientalServiceBean.getConcesionesMineraPorIdProyecto(idProyecto);
    }
    public List<ProyectoBloque> getProyectosBloquesPorIdProyecto(Integer idProyecto) throws ServiceException {
        return proyectoLicenciaAmbientalServiceBean.getProyectosBloques(idProyecto);
    }

    public boolean validarPermisoPorIdProyecto(Integer idProyecto, String tipoPermiso, String sector) throws ServiceException {

        boolean existe = false;
        int totalRegistros = 0;
        try {
            if(tipoPermiso.compareTo("Registro Ambiental") == 0){
                if(sector.compareTo("Minería")!=0)//Si no es un proyecto minero
                totalRegistros = proyectoLicenciaAmbientalServiceBean
                        .getTotalRegistroAmbientalPorProyectoNativeQuery(idProyecto);
                else//Es un proyecto minero
                	if (proyectoLicenciaAmbientalServiceBean.getProyectoPorId(idProyecto).getCatalogoCategoria().getId()==851){
                		totalRegistros = proyectoLicenciaAmbientalServiceBean
                                .getTotalRegistroAmbientalPorProyectoNativeQuery(idProyecto);
                	}else{
                    totalRegistros = proyectoLicenciaAmbientalServiceBean
                            .getTotalRegistroAmbientalPorProyectoMineroNativeQuery(idProyecto);
                	}
            }
            else {
                if (tipoPermiso.compareTo("Licencia Ambiental") == 0) {
                    totalRegistros = proyectoLicenciaAmbientalServiceBean
                            .getTotalLicenciaAmbientalPorProyectoNativeQuery(idProyecto);
                }
            }

            if (totalRegistros > 0) {
                existe = true;
            }

        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return existe;
    }

    public List<ZonasFase> getListaZonas() throws ServiceException{
        return proyectoLicenciaAmbientalServiceBean.getZonasFase();
    }

    public void eliminar(ProyectoLicenciamientoAmbiental proyecto){
        crudServiceBean.delete(proyecto);

    }
    public void guardar(ProyectoLicenciamientoAmbiental proyecto){
       proyecto = crudServiceBean.saveOrUpdate(proyecto);


    }

    /***
     * Método que verifica si un proyecto tiene un RGD en curso.
     * @Autor Denis Linares
     * @param idProyecto
     * @return
     */
    public boolean tienenRgdEnCurso(Integer idProyecto) {
        return proyectoLicenciaAmbientalServiceBean.existeRgdEnCurso(idProyecto);
    }
    
    public boolean validarPermisoPorIdProyecto(ProyectoLicenciamientoAmbiental proyecto) throws ServiceException {

        boolean existe = false;
        int totalRegistros = 0;
        try {
        	String tipoPermiso=proyecto.getCatalogoCategoria().getCategoria().getNombrePublico();
            if(tipoPermiso.equals("Registro Ambiental") ){
            	String sector=proyecto.getCatalogoCategoria().getCatalogoCategoriaPublico().getTipoSector().getNombre();
                if(!sector.equals("Minería"))//Si no es un proyecto minero
                	totalRegistros = proyectoLicenciaAmbientalServiceBean.getTotalRegistroAmbientalPorProyectoNativeQuery(proyecto.getId());
                else//Es un proyecto minero                
	                if (proyecto.getCatalogoCategoria().getCodigo().equals("21.02.03.05") || proyecto.getCatalogoCategoria().getCodigo().equals("21.02.04.03")|| 
	            			proyecto.getCatalogoCategoria().getCodigo().equals("21.02.05.03") || proyecto.getCatalogoCategoria().getCodigo().equals("21.02.02.03") ||
	            			proyecto.getCatalogoCategoria().getCodigo().equals("21.02.01.01") || proyecto.getCatalogoCategoria().getCodigo().equals("21.02.01.02")) {
	                	totalRegistros = proyectoLicenciaAmbientalServiceBean.getTotalRegistroAmbientalPorProyectoMineroNativeQuery(proyecto.getId());
	                }else{
	                totalRegistros = proyectoLicenciaAmbientalServiceBean.getTotalRegistroAmbientalPorProyectoNativeQuery(proyecto.getId());
	            	}
            }
            else if (tipoPermiso.equals("Licencia Ambiental")) {
                    totalRegistros = proyectoLicenciaAmbientalServiceBean.getTotalLicenciaAmbientalPorProyectoNativeQuery(proyecto.getId());                
            }else if (tipoPermiso.equals("Certificado Ambiental") ) {
                totalRegistros = proyectoLicenciaAmbientalServiceBean.getTotalCertificadoAmbientalPorProyectoNativeQuery(proyecto.getId());                
            }

            if (totalRegistros > 0) {
                existe = true;
            }

        } catch (Exception e) {
            throw new ServiceException(e);
        }
        return existe;
    }
    
    public boolean validarPermisoHidrocarburos(String codigoProyecto){
    	Integer countrows = 0;
        try {	
    		String sql="select * "
    				+ "from dblink('"+Constantes.getDblinkBpmsHyD()+"',' "
    				+ "select count(*) "
    				+ "from processinstancelog "
    				+ "where status =2 "
    				+ "and id in(select processinstanceid "    				
    				+ "from variableinstancelog "
    				+ "where processid in(''Hydrocarbons.pagoLicencia'',''Hydrocarbons.pagoLicenciaEnte'') "
    				+ "and value=''"+codigoProyecto+"'')') as ( "
    				+ "count bigint)"; 		
    		Query q = crudServiceBean.getEntityManager().createNativeQuery(sql);		
    		if(q.getResultList().size()>0)
    			countrows=((BigInteger) q.getSingleResult()).intValue();        	
        } catch (Exception e) {
            e.printStackTrace();
        }
        return countrows==0?false:true;
    }
}