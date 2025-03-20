/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.bean;

import ec.gob.ambiente.suia.domain.Rol;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.dto.EntityAdjuntoUsuario;
import ec.gob.ambiente.suia.dto.EntityUsuario;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.DualListModel;

/**
 *
 * @author ishmael
 */
public class UsuarioBean implements Serializable {

	private static final long serialVersionUID = 2500202642652218475L;

	@Getter
    @Setter
    private boolean verListaUsuario;
    
    @Getter
    @Setter
    private boolean verDatosUsuario;
    
    @Getter
    @Setter
    private boolean verAsignarRol;
    
    @Getter
    @Setter
    private List<EntityUsuario> listaEntityUsuario;
    
    @Getter
    @Setter
    private Usuario usuario;
    
    @Getter
    @Setter
    private DualListModel<Rol> listaDualRoles;
    
    @Getter
    @Setter
    private List<Rol> listaRol;

    @Getter
    @Setter
    private List<Usuario> usuariosArea;

    @Getter
    @Setter
    private Boolean mostrarOpcionesJefe;
    
    @Getter
	@Setter
	private EntityAdjuntoUsuario entityAdjuntoUsuario;
    
    @Getter
    @Setter
    private String areasResponsables;
    
}
