/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ec.gob.ambiente.suia.administracion.bean.RolBean;
import ec.gob.ambiente.suia.administracion.facade.EntidadFacade;
import ec.gob.ambiente.suia.administracion.facade.EntidadRolFacade;
import ec.gob.ambiente.suia.administracion.facade.MenuFacade;
import ec.gob.ambiente.suia.administracion.facade.ModuloFacade;
import ec.gob.ambiente.suia.administracion.facade.ModuloRolFacade;
import ec.gob.ambiente.suia.administracion.facade.RolFacade;
import ec.gob.ambiente.suia.domain.Entidad;
import ec.gob.ambiente.suia.domain.EntidadRol;
import ec.gob.ambiente.suia.domain.Menu;
import ec.gob.ambiente.suia.domain.MenuRoles;
import ec.gob.ambiente.suia.domain.Modulo;
import ec.gob.ambiente.suia.domain.ModuloRol;
import ec.gob.ambiente.suia.domain.Rol;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 *
 * @author christian
 */
@ManagedBean
@ViewScoped
public class RolController implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1228354609196090636L;
    @EJB
    private RolFacade rolFacade;
    @EJB
    private MenuFacade menuFacade;
    @EJB
    private ModuloFacade moduloFacade;
    @EJB
    private EntidadFacade entidadFacade;
    @EJB
    private EntidadRolFacade entidadRolFacade;
    @EJB
    private ModuloRolFacade moduloRolFacade;
    @Getter
    @Setter
    private RolBean rolBean;
    @Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;    
    
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(RolController.class);

    @Getter
    @Setter
    private List<Modulo> lista = new ArrayList<Modulo>();
    
    @Getter
    @Setter
    private List<Modulo> listaSeleccionado = new ArrayList<Modulo>();
    
    @Getter
    @Setter
    private List<Entidad> listaEntidades = new ArrayList<>();
    
    @Getter
    @Setter
    private List<Entidad> listaEntidadesSeleccionadas = new ArrayList<>();   
    
    
    @Getter
    @Setter
    private List<Entidad> listaEntidadesGuardadas = new ArrayList<>(); 
    
    @Getter
    @Setter
    private List<Modulo> listaModulosGuardados = new ArrayList<Modulo>();
    
    @Getter
    @Setter
    private String nombreBusqueda = "";
    
    @Getter
    @Setter
    private String unico;
    
    @Getter
    @Setter
    private List<ModuloRol> listaSeleccionadoAux;
    
    @Getter
    @Setter
    private List<EntidadRol> listaEntidadesSeleccionadasAux;
    
    @PostConstruct
    private void cargarDatos() {
        try {
            JsfUtil.validarPagina("rol.jsf");
            setRolBean(new RolBean());
            getRolBean().iniciarDatos();
            getRolBean().setListaRoles(rolFacade.listarRoles());
            
            lista = moduloFacade.listarModulosActivos();
            listaEntidades = entidadFacade.listarEntidades();   
            
            
        } catch (ServiceException e) {
            LOG.error(e, e);
        }
    }

    public void nuevo() {
        getRolBean().setRolSeleccionado(new Rol());
        getRolBean().setApareceTabla(false);
        iniciarArbol();
    }

    private void iniciarArbol() {
        getRolBean().setRoot(new DefaultTreeNode());
        cargarArbol(null, null);
    }

    private void cargarArbol(List<Menu> menus, TreeNode nodo) {
        try {
            if (nodo == null) {
                List<Menu> menusH = menuFacade.listarMenuPadre();
                cargarArbol(menusH, getRolBean().getRoot());
            } else {
                for (Menu m : menus) {
                    TreeNode nodoH = new DefaultTreeNode(m, nodo);
                    nodoH.setSelected(seleccionarNodo(m));
                    nodoH.setSelectable(!getRolBean().isSoloLectura());
                    nodoH.setExpanded(true);
                    List<Menu> menusN = menuFacade.listarPorMenuPadre(m);
                    cargarArbol(menusN, nodoH);
                }
            }
        } catch (ServiceException e) {
            LOG.error(e, e);
        }
    }

    public void guardar() {
        try {
        	
        	if(listaSeleccionado == null || listaSeleccionado.isEmpty()){
        		JsfUtil.addMessageError("Debe seleccionar al menos un módulo");
        		return;
        	}
        	
//            getRolBean().validarDatos();
            
            if(getRolBean().getSelectedNodes() == null || getRolBean().getSelectedNodes().length == 0){
            	JsfUtil.addMessageError("Debe seleccionar al menos un permiso");
        		return;
            }
            
            for (TreeNode tn : getRolBean().getSelectedNodes()) {
                Menu m = (Menu) tn.getData();
                if (m.isNodoFinal()) {
                    cargarListaGuardar(m);
                }
            }
            
            if(getUnico().equals("zonal")){
            	getRolBean().getRolSeleccionado().setUnicoPorDireccionZonal(true);
            	getRolBean().getRolSeleccionado().setUnicoPorOficinaTecnica(false);
            }else if(getUnico().equals("oficina")){
            	getRolBean().getRolSeleccionado().setUnicoPorDireccionZonal(false);
            	getRolBean().getRolSeleccionado().setUnicoPorOficinaTecnica(true);
            }else{
            	getRolBean().getRolSeleccionado().setUnicoPorDireccionZonal(false);
            	getRolBean().getRolSeleccionado().setUnicoPorOficinaTecnica(false);
            }
            
            rolFacade.guardar(getRolBean().getRolSeleccionado(), getRolBean().getMenuGuardarList());
            guardarModulosEntidades();
            setRolBean(null);
            cargarDatos();
            JsfUtil.addMessageInfo(JsfUtil.REGISTRO_ACTUALIZADO);
        } catch (ServiceException e) {
            JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO);
            LOG.error(e, e);
        }
    }
    
    private void guardarModulosEntidades(){
    	   
    	if(listaSeleccionadoAux != null && !listaSeleccionadoAux.isEmpty()){
    		for(ModuloRol modRol : listaSeleccionadoAux){
        		modRol.setEstado(false);
        		moduloRolFacade.guardarModuloRol(modRol, loginBean.getUsuario());
        	}
    	}   	
    	
    	for(Modulo mod :listaSeleccionado){   		
    		ModuloRol modRol = new ModuloRol();
    		modRol.setRol(rolBean.getRolSeleccionado());
    		modRol.setModulo(mod);
    		modRol.setEstado(true);
    		moduloRolFacade.guardarModuloRol(modRol,loginBean.getUsuario());    		
    	} 
    	
    	if(listaEntidadesSeleccionadasAux != null && !listaEntidadesSeleccionadasAux.isEmpty()){
    		for(EntidadRol entRol : listaEntidadesSeleccionadasAux){
        		entRol.setEstado(false);
    			entidadRolFacade.guardar(entRol, loginBean.getUsuario());			
    		}
    	}    	
    	
    	for(Entidad ent: listaEntidadesSeleccionadas){   		
    		
    		EntidadRol entRol = new EntidadRol();
    		entRol.setRol(rolBean.getRolSeleccionado());
    		entRol.setEntidad(ent);
    		entRol.setEstado(true);
    		entidadRolFacade.guardar(entRol, loginBean.getUsuario());    		
    	}    	
    }

    private void cargarListaGuardar(Menu men) {
        if (!getRolBean().getMenuGuardarList().contains(men) && men.getPadreId() != null) {
            getRolBean().getMenuGuardarList().add(men);
            cargarListaGuardar(men.getPadreId());
        } else if (!getRolBean().getMenuGuardarList().contains(men)) {
            getRolBean().getMenuGuardarList().add(men);
        }
    }

    public void cancelar() {
        setRolBean(null);
        cargarDatos();
    }

    public void seleccionarEditar() {
        try {
            getRolBean().setRolMenuList(rolFacade.listarPorRol(getRolBean().getRolSeleccionado()));
            iniciarArbol();
            getRolBean().setApareceTabla(false);
            getRolBean().setSoloLectura(false);        
            
            
            if(getRolBean().getRolSeleccionado() != null && getRolBean().getRolSeleccionado().getId() != null){
            	
				if (getRolBean().getRolSeleccionado().getUnicoPorDireccionZonal() != null && getRolBean().getRolSeleccionado().getUnicoPorDireccionZonal()) {
					setUnico("zonal");
				} else if(getRolBean().getRolSeleccionado().getUnicoPorOficinaTecnica() != null && getRolBean().getRolSeleccionado().getUnicoPorOficinaTecnica()) {
					setUnico("oficina");
				}
            	           	
            	listaSeleccionadoAux = new ArrayList<>();
            	listaSeleccionadoAux = moduloRolFacade.buscarPorRol(getRolBean().getRolSeleccionado());
            	listaEntidadesSeleccionadasAux = new ArrayList<>();
            	listaEntidadesSeleccionadasAux = entidadRolFacade.buscarPorRol(getRolBean().getRolSeleccionado());
            	List<EntidadRol> listaEntidadesSeleccionadasGuardadosAux = new ArrayList<>();
            	listaEntidadesSeleccionadasGuardadosAux.addAll(listaEntidadesSeleccionadasAux);
            	
            	for(ModuloRol modRol: listaSeleccionadoAux){
            		if(!listaSeleccionado.contains(modRol.getModulo())){
            			listaSeleccionado.add(modRol.getModulo());
                		listaModulosGuardados.add(modRol.getModulo());
            		}            		
            	}
            	
            	for(EntidadRol entRol: listaEntidadesSeleccionadasAux){
            		if(!listaEntidadesSeleccionadas.contains(entRol.getEntidad())){
            			listaEntidadesSeleccionadas.add(entRol.getEntidad());
                		listaEntidadesGuardadas.add(entRol.getEntidad());
            		}            		
            	}
            }
        } catch (ServiceException e) {
            JsfUtil.addMessageError(e.getMessage());
            LOG.error(e, e);
        }
    }

    public void seleccionarVer() {
        try {
            getRolBean().setRolMenuList(rolFacade.listarPorRol(getRolBean().getRolSeleccionado()));
            getRolBean().setApareceTabla(false);
            getRolBean().setSoloLectura(true);
            iniciarArbol();
        } catch (ServiceException e) {
            JsfUtil.addMessageError(e.getMessage());
            LOG.error(e, e);
        }
    }

    private boolean seleccionarNodo(Menu menu) {
        boolean retorno = false;
        if (getRolBean().getRolMenuList() != null && !getRolBean().getRolMenuList().isEmpty()) {
            for (MenuRoles m : getRolBean().getRolMenuList()) {
                if (menu.getId().equals(m.getIdMenu())) {
                    retorno = true;
                    break;
                }
            }
        } else {
            retorno = false;
        }
        return retorno;
    }
    
    public void busqueda(){
    	try {
			if(nombreBusqueda.isEmpty() || nombreBusqueda.equals("")){
				lista = moduloFacade.listarModulosActivos();
			}else{
				lista = moduloFacade.listarModulosBuscados(nombreBusqueda);
			}  		
    		
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

}
