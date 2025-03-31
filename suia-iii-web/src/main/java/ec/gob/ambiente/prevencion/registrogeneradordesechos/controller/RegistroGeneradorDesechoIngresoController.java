/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.prevencion.registrogeneradordesechos.controller;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.jbpm.process.audit.ProcessInstanceLog;

import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * <b> imgresop de RGD fisicos. </b>
 *
 * @author byron.burbano
 * @version Revision: 1.0
 * 
 */
@ManagedBean
@ViewScoped
public class RegistroGeneradorDesechoIngresoController implements Serializable {

    private static final long serialVersionUID = 6792843606389699865L;

    private static final Logger LOG = Logger.getLogger(RegistroGeneradorDesechoIngresoController.class);

    @EJB
    private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;

    @EJB
    private ProcesoFacade procesoFacade;
	@EJB
	private AreaFacade areaFacade;
    @EJB
    private UsuarioFacade usuarioFacade;

    @Getter
    @Setter
    private String codigo;

	@Getter
	@Setter
	private String tipoSectorId;

	@Getter
	@Setter
	private String areaId;

	@Getter
	@Setter
	private String usuarioId;

	@Getter
	@Setter
	private String usuarioNombre;
	
	@Getter
	@Setter
	private String empresaNombre;
	
	@Getter
	@Setter
	private String representanteCI;

	@Getter
	@Setter
	private Date fechaActual;

    @Getter
    private boolean permitirMotificar;
    
	@Getter
	@Setter
	private boolean mostrarFormulario;

    @Getter
    @Setter
    private Usuario usuarioGenerador;

    @Getter
    @Setter
    private GeneradorDesechosPeligrosos generadorDesechosPeligrosos;

    @Getter
    @Setter
    private List<GeneradorDesechosPeligrosos> listaGeneradorDesechosPeligrosos;

    @Getter
    private List<TipoSector> tiposSectores;
    
    @Getter
    private List<Area> ListaAreas;
    

    @EJB
    CrudServiceBean crudServiceBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @PostConstruct
    private void init() throws ServiceException {
    	mostrarFormulario = false;
    	permitirMotificar = true;
    	fechaActual = new Date();
        FacesContext ctx = FacesContext.getCurrentInstance();
        String urls = ctx.getViewRoot().getViewId();
        if(urls.contains("formularioIngresoRGD")){
            tiposSectores = registroGeneradorDesechosFacade.getTiposSectores();
			ListaAreas = areaFacade.getAreasAll();
        	ingresoRGD();
        	if (loginBean.getUsuario().getListaAreaUsuario().get(0).getArea().getAreaAbbreviation().contains("DP")){
        		listaGeneradorDesechosPeligrosos = registroGeneradorDesechosFacade.getGeneradoresPorCodigoPorUsuarioList("", 0, loginBean.getUsuario().getNombre());
			}else{
				listaGeneradorDesechosPeligrosos = registroGeneradorDesechosFacade.getGeneradoresPorCodigoPorUsuarioList("", 0, "");
			}
        }
    	HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		if (request.getParameter("nuevo") != null){	
	    	mostrarFormulario = true;
		}
    }
    		
    public void ingresoRGD(){
    	generadorDesechosPeligrosos = new GeneradorDesechosPeligrosos();
    	generadorDesechosPeligrosos.setEliminacionDentroEstablecimiento(false);
    	generadorDesechosPeligrosos.setResponsabilidadExtendida(false);
    	generadorDesechosPeligrosos.setFinalizado(true);
    	generadorDesechosPeligrosos.setEstado(true);
    	generadorDesechosPeligrosos.setTipoIngreso(true);
    	usuarioGenerador = new Usuario();
    	tipoSectorId = "";
    	areaId = "";
    	usuarioId = "";
    	usuarioNombre = "";
    	empresaNombre = "";
    	representanteCI = "";
    }

    public void nuevo(){
		JsfUtil.redirectTo("/prevencion/registrogeneradordesechos/formularioIngresoRGD.jsf?nuevo=0");
    }

    public void guardar(){
    	boolean guardar = true;
    	List<GeneradorDesechosPeligrosos> objRGD = null;
		// valido el usuario ingresado
		if (usuarioGenerador != null && usuarioGenerador.getId() != null && usuarioId.equals(usuarioGenerador.getNombre())){
			// seteo el usuario ingresado
			generadorDesechosPeligrosos.setUsuario(usuarioGenerador);
			if (generadorDesechosPeligrosos.getId() == null){
				objRGD = registroGeneradorDesechosFacade.getGeneradoresPorCodigoPorUsuarioList(generadorDesechosPeligrosos.getCodigo(), 0, "");
			}else{
				objRGD = registroGeneradorDesechosFacade.getGeneradoresPorCodigoPorUsuarioList(generadorDesechosPeligrosos.getCodigo(), generadorDesechosPeligrosos.getId(), "");
			}
	    	if (objRGD == null || objRGD.size() == 0 ){
				// seteo el tipo de sector seleccionado 
				if(!"".equals(tipoSectorId) ){
					for (TipoSector objTipoSector : tiposSectores) {
						if (Integer.valueOf(tipoSectorId) == objTipoSector.getId()){
							generadorDesechosPeligrosos.setTipoSector(objTipoSector);
							break;
						}
					}
				}
				// seteo el area seleccionada 
				if(!"".equals(areaId) ){
					Area objArea = areaFacade.getArea(Integer.valueOf(areaId));
					if(objArea != null){
						generadorDesechosPeligrosos.setAreaResponsable(objArea);
					}
				}
				generadorDesechosPeligrosos.setSolicitud(generadorDesechosPeligrosos.getCodigo());
				if (guardar){
					Date fechaCreacion = generadorDesechosPeligrosos.getFechaCreacion();
					generadorDesechosPeligrosos.setFisico(true);
			    	crudServiceBean.saveOrUpdate(generadorDesechosPeligrosos);
			    	// para guardar la fecha de creacion que se ingresa por que se crea con la fecha actual ya que la tabla hereda de la entidadauditable y se puierde la fecha de ingreso
			    	if (!fechaCreacion.equals(generadorDesechosPeligrosos.getFechaCreacion()) ){
			    		generadorDesechosPeligrosos.setFechaCreacion(fechaCreacion);
			    		crudServiceBean.saveOrUpdate(generadorDesechosPeligrosos);
			    	}
			    	ingresoRGD();
		        	if (loginBean.getUsuario().getListaAreaUsuario().get(0).getArea().getAreaAbbreviation().contains("DP")){
		        		listaGeneradorDesechosPeligrosos = registroGeneradorDesechosFacade.getGeneradoresPorCodigoPorUsuarioList("", 0, loginBean.getUsuario().getNombre());
					}else{
						listaGeneradorDesechosPeligrosos = registroGeneradorDesechosFacade.getGeneradoresPorCodigoPorUsuarioList("", 0, "");
					}		        	
		        	mostrarFormulario = false;
		            JsfUtil.addMessageInfo("Registro guardado correctamente.");
				}
	    	}else{
	            JsfUtil.addMessageError("El código del registro generador ya existe.");
	    	}
		}else{
            JsfUtil.addMessageError("Debe validar el usuario.");
			guardar = false;
		}
    }
    
    public void editar(GeneradorDesechosPeligrosos objGenerador) throws JbpmException{
    	if (objGenerador.isFisico()==true);
    	if (procesoFacade.getProcessInstancesLogsVariableValue(objGenerador.getUsuario(), Constantes.VARIABLE_PROCESO_TRAMITE, objGenerador.getCodigo()).size() == 0){
    		mostrarFormulario = true;
        	permitirMotificar = true;
			generadorDesechosPeligrosos = objGenerador;
			if(objGenerador.getTipoSector() != null){
				tipoSectorId = objGenerador.getTipoSector().getId().toString();
			}
			if (objGenerador.getAreaResponsable() != null){
				areaId = objGenerador.getAreaResponsable().getId().toString();
			}
			if(objGenerador.getUsuario() != null){
				usuarioId = objGenerador.getUsuario().getNombre();
				validarRUC();
			}
    	}else {
    		if (objGenerador.isFisico()==true && objGenerador.isFinalizado()== true);
    			if (procesoFacade.getProcessInstancesLogsVariableValue(objGenerador.getUsuario(), Constantes.VARIABLE_PROCESO_TRAMITE, objGenerador.getCodigo()).size() == 1){
    	            mostrarFormulario = true;
    	        	permitirMotificar = true;
    				generadorDesechosPeligrosos = objGenerador;
    				if(objGenerador.getTipoSector() != null){
    					tipoSectorId = objGenerador.getTipoSector().getId().toString();
    				}
    				if (objGenerador.getAreaResponsable() != null){
    					areaId = objGenerador.getAreaResponsable().getId().toString();
    				}
    				if(objGenerador.getUsuario() != null){
    					usuarioId = objGenerador.getUsuario().getNombre();
    					validarRUC();
    				}
    	}else{
            JsfUtil.addMessageError("El registro ya se encuentra iniciado.");
    	}
    	}
    	    
    }
    
    public void ver(GeneradorDesechosPeligrosos objGenerador){
        mostrarFormulario = true;
    	permitirMotificar = false;
    	generadorDesechosPeligrosos = objGenerador;
    	if(objGenerador.getTipoSector() != null){
    		tipoSectorId = objGenerador.getTipoSector().getId().toString();
    	}
    	if (objGenerador.getAreaResponsable() != null){
    		areaId = objGenerador.getAreaResponsable().getId().toString();
    	}
    	if(objGenerador.getUsuario() != null){
    		usuarioId = objGenerador.getUsuario().getNombre();
    		validarRUC();
    	}
    }
    
    public void validarRUC(){
    	usuarioGenerador = usuarioFacade.buscarUsuario(usuarioId);
		usuarioNombre = "";
		representanteCI = "";
		empresaNombre = "";	
    	if (usuarioGenerador == null){
            JsfUtil.addMessageError("El usuario no existe.");
    	}else{
    		usuarioNombre = usuarioGenerador.getPersona().getNombre();
    		representanteCI = usuarioGenerador.getPin();
    		if (usuarioGenerador.getPersona().getOrganizaciones().size() > 0 ){
    			
    			for(Organizacion organizacion : usuarioGenerador.getPersona().getOrganizaciones()){
    				if(organizacion.getRuc().equals(usuarioId)){
    					empresaNombre = organizacion.getNombre();	
    					break;
    				}
    			}        		
    		}
    	}
    }

	public void cancelar(){
        mostrarFormulario = false;
    	if (loginBean.getUsuario().getListaAreaUsuario().get(0).getArea().getAreaAbbreviation().contains("DP")){
    		listaGeneradorDesechosPeligrosos = registroGeneradorDesechosFacade.getGeneradoresPorCodigoPorUsuarioList("", 0, loginBean.getUsuario().getNombre());
		}else{
			listaGeneradorDesechosPeligrosos = registroGeneradorDesechosFacade.getGeneradoresPorCodigoPorUsuarioList("", 0, "");
		}
	}
	
	public void limpiar(){
        mostrarFormulario = true;
	}
}