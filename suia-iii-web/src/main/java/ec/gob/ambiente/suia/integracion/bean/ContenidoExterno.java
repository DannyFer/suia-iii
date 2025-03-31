/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.integracion.bean;

import java.util.List;

import ec.gob.ambiente.integracion.facade.IntegracionFacade;
import ec.gob.ambiente.suia.AutorizacionCatalogo.facade.AutorizacionCatalogoFacade;
import ec.gob.ambiente.suia.domain.AreasAutorizadasCatalogo;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.dto.ProyectoCustom;
import ec.gob.ambiente.suia.dto.TaskSummaryCustom;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.persistence.Query;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 *
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: 12/03/2015]
 *          </p>
 */
@ManagedBean
@SessionScoped
public class ContenidoExterno {

    @Getter
    private String action;

    @Getter
    @Setter
    private String projectId;

    @Getter
    private TaskSummaryCustom taskSummaryCustom;

    private int servlet;

    private static final int SERVLET_HYDROCARBONS = 0;
    private static final int SERVLET_SUIA = 1;

    @EJB
    private IntegracionFacade integracionFacade;
    
    @EJB
    private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade; 
    
    @EJB
    private AutorizacionCatalogoFacade autorizacionCatalogoFacade;
    
	@EJB
    private CrudServiceBean crudServiceBean;
    
    public String getUrl() {
        return (servlet == 1 ? Constantes.getAppIntegrationServletSuia() : Constantes
                .getAppIntegrationServletHydrocarbons()) + "?time=" + System.currentTimeMillis();
    }

    public Usuario getUsuario() {
        return JsfUtil.getLoggedUser();
    }

    public void reporteProvincial() {
        execute(IntegracionFacade.IntegrationActions.mostrar_reporte_provincial);
    }

    public void reasignacion() {
        execute(IntegracionFacade.IntegrationActions.reasignacion);
    }

    public void reasignacionSuia() {
        this.servlet = SERVLET_SUIA;
        execute(IntegracionFacade.IntegrationActions.reasignacion_masiva);
    }

    public void reasignacionDirectoresSuia() {
        this.servlet = SERVLET_SUIA;
        execute(IntegracionFacade.IntegrationActions.reasignacion);
    }

    public void executeAction(TaskSummaryCustom taskSummaryCustom, IntegracionFacade.IntegrationActions action) {
        this.taskSummaryCustom = taskSummaryCustom;
        if (taskSummaryCustom.getSourceType().equals(TaskSummaryCustom.SOURCE_TYPE_EXTERNAL_HYDROCARBONS))
            servlet = SERVLET_HYDROCARBONS;
        else if (taskSummaryCustom.getSourceType().equals(TaskSummaryCustom.SOURCE_TYPE_EXTERNAL_SUIA)) {
            servlet = SERVLET_SUIA;
            this.projectId = taskSummaryCustom.getProjectId();
        }
        execute(action);
    }
    
//    public void executeAction(ProyectoCustom proyectoCustom, IntegracionFacade.IntegrationActions action) {
//    	this.projectId = proyectoCustom.getCodigo();
//    	boolean isHidrocarburos = false;
//    	try {
//    		isHidrocarburos = integracionFacade.isProjectHydrocarbons(proyectoCustom.getCodigo(),
//    				JsfUtil.getLoggedUser().getNombre(), JsfUtil.getLoggedUser().getPasswordSha1Base64());
//    	} catch (Exception ex) {
//    		ex.printStackTrace();
//    	}
//    	//Hidrocarburos para entes-------------------
//    	//Actividad 840 - Estaciones de servicios
//    	//Area responsable 
//    	//585 - Gobierno provincial de los Rios 
//    	//552 --"GOBIERNO PROVINCIAL DEL GUAYAS"
//    	//573 --"GOBIERNO AUTÓNOMO DESCENTRALIZADO PROVINCIAL DE EL ORO"
//    	//554 --"GAD MUNICIPAL DE CUENCA" no
//    	//719 --"MUY ILUSTRE MUNICIPALIDAD DE GUAYAQUIL" no
//    	//556 --"MUNICIPIO DE QUITO" no
//    	if(proyectoLicenciamientoAmbientalFacade.getProyectoPorCodigo(projectId)!=null)
//    	{
//    		Integer codigoActividad=proyectoLicenciamientoAmbientalFacade.getProyectoPorCodigo(projectId).getCatalogoCategoria().getId();
//    		Integer codigoArea=proyectoLicenciamientoAmbientalFacade.getProyectoPorCodigo(projectId).getAreaResponsable().getId();
//    		
//	    	if(codigoActividad.equals(840) && (codigoArea.equals(585) || codigoArea.equals(552) || codigoArea.equals(573)))
//	    		isHidrocarburos=true;
//    	}
//    	//-------------------------
//
//    	String desactivarH= action.toString();
//    	if (isHidrocarburos){//santiago cambio para redirección a suia verde
//    		if (!(desactivarH.equals("eliminar_proyecto"))){
//    			servlet = SERVLET_HYDROCARBONS;
//    		}
//    		else {
//    			servlet = SERVLET_SUIA;
//    		}
//    	}//fin de modificación Santiago.
//    	else{
//    		servlet = SERVLET_SUIA;
//    	}
//
//    	execute(action);
//    }
    
    public String dblinkSuiaVerde=Constantes.getDblinkSuiaVerde();
    
    public void executeAction(ProyectoCustom proyectoCustom, IntegracionFacade.IntegrationActions action) {
    	this.projectId = proyectoCustom.getCodigo();
    	boolean isHidrocarburos = false;
    	try {
    		isHidrocarburos = integracionFacade.isProjectHydrocarbons(proyectoCustom.getCodigo(),
    				JsfUtil.getLoggedUser().getNombre(), JsfUtil.getLoggedUser().getPasswordSha1Base64());
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
    	if(proyectoLicenciamientoAmbientalFacade.getProyectoPorCodigo(projectId)!=null)
    	{
    		AreasAutorizadasCatalogo estacionServicio=autorizacionCatalogoFacade.getaAreasAutorizadasCatalogo(proyectoLicenciamientoAmbientalFacade.getProyectoPorCodigo(projectId).getCatalogoCategoria(), proyectoLicenciamientoAmbientalFacade.getProyectoPorCodigo(projectId).getAreaResponsable());
    		if(estacionServicio!=null)
    		{    
    			if ((proyectoLicenciamientoAmbientalFacade.consultaTaskhyd4cat(projectId)>0)){
    				isHidrocarburos=false;
    			}else{
    				if ((proyectoLicenciamientoAmbientalFacade.consultaTaskhyd(projectId)>0)){
    					isHidrocarburos=true;
    				}else{
    					if(!estacionServicio.getActividadBloqueada()){
    						isHidrocarburos=true;
    					}
    				}
    			}
    		}
    	}
    	//-------------------------
    	String desactivarH= action.toString();
    	if (isHidrocarburos){//santiago cambio para redirección a suia verde
    		if (!(desactivarH.equals("eliminar_proyecto"))){
    			servlet = SERVLET_HYDROCARBONS;
    		}
    		else {
    			servlet = SERVLET_SUIA;
    		}
    	}//fin de modificación Santiago.
    	else{
    		servlet = SERVLET_SUIA;
    	}execute(action);
    }

    private void execute(IntegracionFacade.IntegrationActions action) {
        this.action = action.toString();
        JsfUtil.redirectTo("/integracion/contenido.jsf");
    }


    public void execute(String action, Boolean suia) {
        if (suia) {
            servlet = SERVLET_SUIA;
        } else {
            servlet = SERVLET_HYDROCARBONS;
        }
        this.action = action;
        JsfUtil.redirectTo("/integracion/contenido.jsf");
    }

    public boolean isShowParameters() {
        return Constantes.getAppIntegrationShowParameters() && Usuario.isUserInRole(JsfUtil.getLoggedUser(), "admin");
    }

    public static void sendExternalCallHidrocarburos(Usuario usuario, IntegracionFacade.IntegrationActions action,
                                                     String... parameters) throws Exception {
        IntegracionFacade.sendExternalCallHidrocarburos(JsfUtil.getLoggedUser().getNombre(), JsfUtil.getLoggedUser().getPasswordSha1Base64(), action, parameters);
    }

    public static void sendExternalCallSuia(Usuario usuario, IntegracionFacade.IntegrationActions action,
                                            String... parameters) throws Exception {
        IntegracionFacade.sendExternalCallSuia(JsfUtil.getLoggedUser().getNombre(), JsfUtil.getLoggedUser().getPasswordSha1Base64(), action, parameters);
    }
    
    public boolean esHidrocarburos(ProyectoCustom proyectoCustom) {
    	String projectId = proyectoCustom.getCodigo();
    	boolean isHidrocarburos = false;
    	try {
    		isHidrocarburos = integracionFacade.isProjectHydrocarbons(proyectoCustom.getCodigo(),
    				JsfUtil.getLoggedUser().getNombre(), JsfUtil.getLoggedUser().getPasswordSha1Base64());
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
    	if(proyectoLicenciamientoAmbientalFacade.getProyectoPorCodigo(projectId)!=null)
    	{
    		AreasAutorizadasCatalogo estacionServicio=autorizacionCatalogoFacade.getaAreasAutorizadasCatalogo(proyectoLicenciamientoAmbientalFacade.getProyectoPorCodigo(projectId).getCatalogoCategoria(), proyectoLicenciamientoAmbientalFacade.getProyectoPorCodigo(projectId).getAreaResponsable());
    		if(estacionServicio!=null)
    		{    
    			if ((proyectoLicenciamientoAmbientalFacade.consultaTaskhyd4cat(projectId)>0)){
    				isHidrocarburos=false;
    			}else{
    				if ((proyectoLicenciamientoAmbientalFacade.consultaTaskhyd(projectId)>0)){
    					isHidrocarburos=true;
    				}else{
    					if(!estacionServicio.getActividadBloqueada()){
    						isHidrocarburos=true;
    					}
    				}
    			}
    		}
    	}
    	return isHidrocarburos;
    }
    //verifica si un proyecto es hidrocarburos
    public boolean verificaEsHidrocarburos(String prenCode){
    	String sql="select * from dblink('"+dblinkSuiaVerde+"','select pla.id from proyectolicenciaambiental pla inner join catalogo_categoria c on c.id_catalogo=pla.id_catalogo where cata_sector=''hidrocarburos'' and id=''"+prenCode+"''') as (id character varying(255))";
    	Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
    	if(query.getResultList().size()>0){
			return true;
		}else
			return false;
    }
    
    //rgd
    public void executeAction(String codigo, IntegracionFacade.IntegrationActions action) {
    	this.projectId = codigo;
    	boolean isHidrocarburos = false;
    	try {
    		isHidrocarburos = integracionFacade.isProjectHydrocarbons(codigo,JsfUtil.getLoggedUser().getNombre(), JsfUtil.getLoggedUser().getPasswordSha1Base64());
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
    	if(proyectoLicenciamientoAmbientalFacade.getProyectoPorCodigo(projectId)!=null)
    	{
    		AreasAutorizadasCatalogo estacionServicio=autorizacionCatalogoFacade.getaAreasAutorizadasCatalogo(proyectoLicenciamientoAmbientalFacade.getProyectoPorCodigo(projectId).getCatalogoCategoria(), proyectoLicenciamientoAmbientalFacade.getProyectoPorCodigo(projectId).getAreaResponsable());
    		if(estacionServicio!=null)
    		{    
    			if ((proyectoLicenciamientoAmbientalFacade.consultaTaskhyd4cat(projectId)>0)){
    				isHidrocarburos=false;
    			}else{
    				if ((proyectoLicenciamientoAmbientalFacade.consultaTaskhyd(projectId)>0)){
    					isHidrocarburos=true;
    				}else{
    					if(!estacionServicio.getActividadBloqueada()){
    						isHidrocarburos=true;
    					}
    				}
    			}
    		}
    	}
    	//-------------------------
    	String desactivarH= action.toString();
    	if (isHidrocarburos){//santiago cambio para redirección a suia verde
    		if (!(desactivarH.equals("eliminar_proyecto"))){
    			servlet = SERVLET_HYDROCARBONS;
    		}
    		else {
    			servlet = SERVLET_SUIA;
    		}
    	}//fin de modificación Santiago.
    	else{
    		servlet = SERVLET_SUIA;
    	}execute(action);
    }
    
}
