/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.builders;

import ec.fugu.ambiente.consultoring.retasking.UsuarioCarga;
import ec.gob.ambiente.suia.dto.UserWorkload;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: 13/03/2015]
 *          </p>
 */
public class UserWorkloadBuilder {

	private UserWorkload userWorkload;

	public UserWorkloadBuilder() {
		userWorkload = new UserWorkload();
	}

	public UserWorkloadBuilder conNombreDeUsuario(String nombreUsuario) {
		userWorkload.setUserName(nombreUsuario);
		return this;
	}
	
	public UserWorkloadBuilder conNumeroTareas(int numeroTareas) {
		userWorkload.setTramites(numeroTareas);;
		return this;
	}
	
	public UserWorkload build() {
		return userWorkload;
	}

	public UserWorkloadBuilder fromSuiaII(UsuarioCarga usuarioCarga) {
		userWorkload.setCarga(0);
		userWorkload.setFullName(usuarioCarga.getNombre());
		userWorkload.setId(usuarioCarga.getId());
		userWorkload.setTramites(usuarioCarga.getCarga());
		userWorkload.setUserName(usuarioCarga.getId());
		userWorkload.setSourceType(UserWorkload.SOURCE_TYPE_EXTERNAL_SUIA);

		return this;
	}
}
