/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.dto;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author christian
 */
public class EntityMenu implements Serializable {

    private static final long serialVersionUID = -255450371999778705L;

    @Getter
    @Setter
    private Long idMenu;
    @Getter
    @Setter
    private String etiquetaMenu;
    @Getter
    @Setter
    private String actionMenu;
    @Getter
    @Setter
    private String urlMenu;
    @Getter
    @Setter
    private Long idMenuPadre;
    @Getter
    @Setter
    private Long idEstadoUsuario;
    @Getter
    @Setter
    private Long idUsuario;
    @Getter
    @Setter
    private Boolean nodoFinal;
    @Getter
    @Setter
    private Integer orden;
    @Getter
    @Setter
    private String icono;

    public EntityMenu(Long idMenu, String etiquetaMenu, String actionMenu, String urlMenu, Long idMenuPadre, Long idUsuario, Boolean nodoFinal, Integer orden, String icono) {
        this.idMenu = idMenu;
        this.etiquetaMenu = etiquetaMenu;
        this.actionMenu = actionMenu;
        this.urlMenu = urlMenu;
        this.idMenuPadre = idMenuPadre;
        this.idUsuario = idUsuario;
        this.nodoFinal = nodoFinal;
        this.orden = orden;
        this.icono = icono;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idMenu != null ? idMenu.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof EntityMenu)) {
            return false;
        }
        EntityMenu other = (EntityMenu) object;
        if ((this.idMenu == null && other.idMenu != null) || (this.idMenu != null && !this.idMenu.equals(other.idMenu))) {
            return false;
        }
        return true;
    }

}
