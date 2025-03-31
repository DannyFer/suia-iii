/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.bean;

import ec.gob.ambiente.suia.domain.Menu;
import ec.gob.ambiente.suia.domain.MenuRoles;
import ec.gob.ambiente.suia.domain.Rol;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.TreeNode;

/**
 *
 * @author christian
 */
public class RolBean implements Serializable {

	private static final long serialVersionUID = 4291925729269901488L;
	
	@Getter
    @Setter
    private List<Rol> listaRoles;
    @Getter
    @Setter
    private Rol rolSeleccionado;
    @Getter
    @Setter
    private TreeNode root;
    @Getter
    @Setter
    private boolean apareceTabla;
    @Getter
    @Setter
    private boolean soloLectura;
    @Getter
    @Setter
    private TreeNode[] selectedNodes;
    @Getter
    @Setter
    private List<Menu> menuGuardarList;
    @Getter
    @Setter
    private List<MenuRoles> rolMenuList;
    
    public void iniciarDatos() {
        setListaRoles(null);
        setRolSeleccionado(null);
        setRoot(null);
        setApareceTabla(true);
        setSoloLectura(false);
        setSelectedNodes(null);
        setMenuGuardarList(new ArrayList<Menu>());
        setRolMenuList(null);
    }

    public void validarDatos() throws ServiceException {
        if (getSelectedNodes() == null || getSelectedNodes().length == 0) {
            throw new ServiceException("Debe seleccionar permisos para asignar al rol");
        }
    }
}
