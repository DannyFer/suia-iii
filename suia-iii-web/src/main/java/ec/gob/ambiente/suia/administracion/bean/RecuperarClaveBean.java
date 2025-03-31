/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.bean;

import ec.gob.ambiente.suia.domain.Usuario;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ishmael
 */
public class RecuperarClaveBean implements Serializable {

	private static final long serialVersionUID = -6038349578457113849L;

	@Getter
    @Setter
    private String login;

    @Getter
    @Setter
    private String mail;

    @Getter
    @Setter
    private Usuario usuario;
    
    @Getter
    @Setter
    private String pass;
    
    @Getter
    @Setter
    private String passConfir;

}
