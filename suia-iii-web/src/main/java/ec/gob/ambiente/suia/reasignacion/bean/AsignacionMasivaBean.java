/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.reasignacion.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.AreaUsuarioFacade;
import ec.gob.ambiente.suia.administracion.facade.RolFacade;
import ec.gob.ambiente.suia.comun.classes.Selectable;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.AreaUsuario;
import ec.gob.ambiente.suia.domain.ReasignacionTareaProyectos;
import ec.gob.ambiente.suia.domain.Rol;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.dto.EntityUsuario;
import ec.gob.ambiente.suia.dto.TaskSummaryCustom;
import ec.gob.ambiente.suia.dto.UserWorkload;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.reasignacion.classes.LazyUserWorkloadDataModel;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: 14/01/2015]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class AsignacionMasivaBean implements Serializable {

	private static final long serialVersionUID = -3526371287558629686L;
	
	private static final Logger LOG = Logger.getLogger(AsignacionMasivaBean.class);

	@Getter
	@Setter
	private List<Selectable<TaskSummaryCustom>> tasksToAssign;

	@Getter
	@Setter
	private LazyUserWorkloadDataModel lazyUserWorkloadDataModel;

	@Getter
	@Setter
	private boolean loadUsers;

	@Getter
	@Setter
	private Usuario usuario;
	
	@Getter
	@Setter
	private ReasignacionTareaProyectos reasignacionProyectos;

	@Getter
	@Setter
	private UserWorkload selectedUser;

	@Getter
	@Setter
	private String nombre;
	
	@Getter
	@Setter
	private String nombreUsuario;

	@Getter
	@Setter
	private String userNameFilter;
	
	@Getter
	@Setter
	private String motivoReasignacion;

	@PostConstruct
	public void init() {
		tasksToAssign = new ArrayList<Selectable<TaskSummaryCustom>>();
		usuario = null;
		reasignacionProyectos=new ReasignacionTareaProyectos();
		nombre = "";
		motivoReasignacion="";
//		setMotivoReasignacion(getMotivoReasignacion());
		nombreUsuario="";
		loadUsers = false;
		cargarUsuarios();
		
		
	}

	@Getter
    @Setter
    private List<EntityUsuario> listaEntityUsuario;
	@EJB
	private RolFacade rolFacade;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private AreaUsuarioFacade areaUsuarioFacade;
	@EJB
	private AreaFacade areaFacade;
	
	private void cargarUsuarios() {
		
		try {
			setListaEntityUsuario(new ArrayList<EntityUsuario>());
			List<EntityUsuario> listaAux;
			List<Rol> rolesusuario = rolFacade.listarPorUsuario(loginBean.getUsuario());
			Integer adminInst=0;
			for (Rol rol : rolesusuario) {
				if (rol.getDescripcion().contains("admin")){
					adminInst=1;
					break;
				}else if (rol.getDescripcion().contains("ADMINISTRADOR INSTITUCIONAL")){
					adminInst=2;
					break;
				}else if (rol.getDescripcion().contains("ADMINISTRADOR AREAS")){
					adminInst=3;
					break;
				}else{
					adminInst=0;
				}
				
			}
			if (adminInst==2){
				listaAux = new ArrayList<>();
				Area areaUsuario = loginBean.getUsuario().getListaAreaUsuario().get(0).getArea();
        		if(areaUsuario.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT)) {
        			//buscar usuarios de area padre
        			List<EntityUsuario> listaAuxAreaPadre = usuarioFacade.listarUsuarioEntityReasignacion(areaUsuario.getArea().getAreaName());
            		listaAux.addAll(listaAuxAreaPadre);
            		
            		//buscar usuarios de areas hijas
            		List<Area> areasHijas = areaFacade.listarAreasHijos(areaUsuario.getArea());
            		for (Area areaHija : areasHijas) {
	            		List<EntityUsuario> listaAuxArea = usuarioFacade.listarUsuarioEntityReasignacion(areaHija.getAreaName());
	            		
	            		listaAux.addAll(listaAuxArea);
					}
            		
        		} else {
					for (AreaUsuario areaUser : loginBean.getUsuario().getListaAreaUsuario()) {
	            		Area areaU = areaUser.getArea();
	            		List<EntityUsuario> listaAuxArea = usuarioFacade.listarUsuarioEntityReasignacion(areaU.getAreaName());
	            		
	            		listaAux.addAll(listaAuxArea);
					}
        		}
			}else if (adminInst==3){				
				List<EntityUsuario> listaAdmin = new ArrayList<EntityUsuario>();
				List<EntityUsuario> listaResponsableAP = new ArrayList<EntityUsuario>();
				listaAux = new ArrayList<EntityUsuario>();
				
				listaAdmin = areaUsuarioFacade.listarUsuarioEntityJefesArea("ADMINISTRADOR ÁREA PROTEGIDA");
				listaResponsableAP = areaUsuarioFacade.listarUsuarioEntityJefesArea("RESPONSABLE DE ÁREAS PROTEGIDAS");								
				
				listaAux.addAll(listaResponsableAP);
				listaAux.addAll(listaAdmin);
			
			}else{
				listaAux = usuarioFacade.listarUsuarioEntityReasigancion();
			}
			
			try {
				
				EntityUsuario uAux = new EntityUsuario();
				int tamaniLista = listaAux.size();
				if (listaAux != null && !listaAux.isEmpty()) {
					int i = 0;
					for (EntityUsuario uI : listaAux) {
						i++;
						if (uAux.getId() == null) {
							uAux = comparaUsuario(uAux, uI, i, tamaniLista);
							continue;
						}
						uAux = comparaUsuario(uAux, uI, i, tamaniLista);
					}
				}

			} catch (Exception e) {
				LOG.error(e, e);
			}			
			
		} catch (ServiceException e1) {
			e1.printStackTrace();
		}
	}
	
	private EntityUsuario comparaUsuario(EntityUsuario uAux, EntityUsuario uI, int i, int tamanioLista) {
		if (uAux.getId() != null && uAux.getId().equals(uI.getId())) {
			uAux.setRoles(uAux.getRoles() + "," + uI.getRoles());
			if (tamanioLista == i) {
				getListaEntityUsuario().add(uAux);
			}
			return uAux;
		}
		if (uAux.getId() != null) {
			getListaEntityUsuario().add(uAux);
		}
		if (tamanioLista == i) {
			getListaEntityUsuario().add(uI);
		}
		return uI;
	}

	public void setTasks(List<TaskSummaryCustom> list) {
		tasksToAssign = new ArrayList<Selectable<TaskSummaryCustom>>();
		if (list != null)
			for (TaskSummaryCustom taskSummaryCustom : list) {
				tasksToAssign.add(new Selectable<TaskSummaryCustom>(taskSummaryCustom));
			}
		loadUsers = !tasksToAssign.isEmpty();
		if (!loadUsers)
			JsfUtil.addMessageInfo("Este usuario no cuenta con tareas pendientes");
		setLazyUserWorkloadDataModel(null);
	}

	public boolean isTasksSelected() {
		for (Selectable<TaskSummaryCustom> selectable : tasksToAssign) {
			if (selectable.isSelected())
				return true;
		}
		return false;
	}

	public boolean isTasksInternalSelected() {
		for (Selectable<TaskSummaryCustom> selectable : tasksToAssign) {
			if (selectable.getValue().isInternal() && selectable.isSelected())
				return true;
		}
		return false;
	}
	
	public boolean isTasksExternalSelected() {
		for (Selectable<TaskSummaryCustom> selectable : tasksToAssign) {
			if (!selectable.getValue().isInternal() && selectable.isSelected())
				return true;
		}
		return false;
	}

}
