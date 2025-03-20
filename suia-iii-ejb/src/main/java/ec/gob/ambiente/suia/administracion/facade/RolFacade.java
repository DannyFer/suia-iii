/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.administracion.service.RolService;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Menu;
import ec.gob.ambiente.suia.domain.MenuRoles;
import ec.gob.ambiente.suia.domain.Rol;
import ec.gob.ambiente.suia.domain.RolUsuario;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;

/**
 *
 * @author christian
 */
@LocalBean
@Stateless
public class RolFacade {

    @EJB
    private RolService rolService;
    @EJB
    private CrudServiceBean crudServiceBean;

    public List<Rol> listarRoles() throws ServiceException {
        return rolService.listarRoles();
    }

    public List<MenuRoles> listarPorRol(final Rol rol) throws ServiceException {
        return rolService.listarPorRol(rol);
    }
    
    public MenuRoles listarPorRolMenu(final Rol rol, final Menu menu) throws ServiceException {
        
    	List<MenuRoles>listRoles=rolService.listarPorRolMenu(rol, menu);
    	
    	if(listRoles!=null && !listRoles.isEmpty()){
    		return listRoles.get(0);
    	}
    	return null;
    }

    public void guardar(final Rol rol, final List<Menu> listaMenu) throws ServiceException {
        try {
        	if(rol.getId()==null){
				rol.setAutoridadUso("false");				
			}
        	rolService.guardar(rol);
        	
        	List<MenuRoles> listaMenuRolInicial= listarPorRol(rol);
        	
        	for (int i = 0; i < listaMenuRolInicial.size(); i++) {
        		boolean eliminado=true;
				for (int j = 0; j < listaMenu.size(); j++) {
					if(listaMenuRolInicial.get(i).getMenuId().equals(listaMenu.get(j))){
						eliminado=false;
						break;
					}
				}
				if(eliminado){
					listaMenuRolInicial.get(i).setEstado(false);
					
					List<Rol> listaRolB =(List<Rol>)crudServiceBean.getEntityManager().createQuery("SELECT r FROM Rol r WHERE r.id = :id and r.estado=true").setParameter("id", listaMenuRolInicial.get(i).getIdRol().intValue()).getResultList();
					List<Menu> listaMenuB = (List<Menu>)crudServiceBean.getEntityManager().createQuery("SELECT r FROM Menu r WHERE r.id = :id and r.estado=true").setParameter("id", listaMenuRolInicial.get(i).getIdMenu().intValue()).getResultList();
//					Rol r= (Rol) crudServiceBean.getEntityManager().createQuery("SELECT r FROM Rol r WHERE r.id = :id and r.estado=true").setParameter("id", listaMenuRolInicial.get(i).getIdRol().intValue()).getSingleResult();
//					Menu m= (Menu) crudServiceBean.getEntityManager().createQuery("SELECT r FROM Menu r WHERE r.id = :id and r.estado=true").setParameter("id", listaMenuRolInicial.get(i).getIdMenu().intValue()).getSingleResult();
					
					Rol r = new Rol();
					Menu m = new Menu();
					
					if(listaRolB != null && !listaRolB.isEmpty()){
						r = listaRolB.get(0);						
					}
					
					if(listaMenuB != null && !listaMenuB.isEmpty()){
						m = listaMenuB.get(0);
					}
					
					if(r != null && r.getId() != null && m !=null && m.getId() != null){
						listaMenuRolInicial.get(i).setRoleId(r);
						listaMenuRolInicial.get(i).setMenuId(m);						
					}	
					crudServiceBean.saveOrUpdate(listaMenuRolInicial.get(i));
				}				
			}
        	
        	for (Menu menu : listaMenu) {
				MenuRoles menuRoles= listarPorRolMenu(rol, menu);
				if(menuRoles==null){
					MenuRoles roles= new MenuRoles();
					roles.setRoleId(rol);
					roles.setMenuId(menu);
					crudServiceBean.saveOrUpdate(roles);
				}
			}     	            
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }

    public List<Rol> listarPorUsuario(Usuario usuario) throws ServiceException {
        return rolService.listarPorUsuario(usuario);
    }

    public void eliminarRolUsuario(final Usuario usuario) throws ServiceException {
        rolService.eliminarRolUsuario(usuario);
    }

    public Rol obtenerRolProponente() throws ServiceException {
        return rolService.obtenerRolProponente();
    }
    
    public List<Rol> listarRolesSinAdmin() throws ServiceException {
        return rolService.listarRolesSinAdmin();
    }
    
    public boolean isUserInRole(Usuario usuario, String roleName) {
        try {
        	List<Rol> roles = rolService.listarRolesActivos(usuario);
                for (Rol rolUsuario : roles) {
                    if (rolUsuario.getNombre()
                            .equalsIgnoreCase(roleName))
                        return true;
                }
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    
    public List<Rol> listarRolesSNAP() throws ServiceException {
        return rolService.listarRolesSNAP();
    }
}
