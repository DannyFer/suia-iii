/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.crud.listener;

import java.util.Date;

import javax.ejb.SessionContext;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.utils.BeanLocator;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: 19/01/2015]
 *          </p>
 */
public class EntityAuditable {

	public String getAuthenticateUserName() {
		String userName = null;
		try {
			SessionContext context = BeanLocator.getSessionContext();
			userName = context.getCallerPrincipal().getName();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (userName == null || userName.isEmpty())
			userName = "<desconocido>";
		return userName;
	}

	@PrePersist
	public void prePersist(Object object) {
		if (object instanceof EntidadAuditable) {
			EntidadAuditable entidadAuditable = (EntidadAuditable) object;
			entidadAuditable.setFechaCreacion(new Date());
			entidadAuditable.setUsuarioCreacion(getAuthenticateUserName());
		}
	}

	@PreUpdate
	public void preUpdate(Object object) {
		if (object instanceof EntidadAuditable) {
			EntidadAuditable entidadAuditable = (EntidadAuditable) object;
			entidadAuditable.setFechaModificacion(new Date());
			entidadAuditable.setUsuarioModificacion(getAuthenticateUserName());
		}
	}
}
