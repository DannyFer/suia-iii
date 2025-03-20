/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.Extension;
import javax.persistence.AttributeOverrides;
import javax.persistence.Entity;

import org.hibernate.annotations.Filter;
import org.reflections.Reflections;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: 20/01/2015]
 *          </p>
 */
public class Validator implements Extension {

	private Map<Class<?>, Boolean> scanned = new HashMap<Class<?>, Boolean>();
	private CrudServiceBean instance = new CrudServiceBean();
	
	private boolean show = true;

	public void onStartup(@Observes AfterDeploymentValidation afterDeploymentValidation) {
		Reflections reflections = new Reflections("ec.gob.ambiente");
		Iterator<Class<?>> iteratorAnnotated = reflections.getTypesAnnotatedWith(Entity.class).iterator();
		while (iteratorAnnotated.hasNext()) {
			Class<?> type = (Class<?>) iteratorAnnotated.next();
			if(type.getSimpleName().equals("TipoProyeccion") || type.getSimpleName().equals("NotificacionesMail"))
				continue;
			scan(type);
		}
	}

	public void scan(Class<?> clazz) {
		try {
			if (!scanned.containsKey(clazz)) {
				scanned.put(clazz, true);
				if (clazz.getName().startsWith("ec.gob.ambiente.suia.domain")) {
					Object object = clazz.newInstance();
					if (!(object instanceof EntidadBase)) {
						instance.invalidEntity("Entity " + object.getClass() + " does not inherit from "
								+ EntidadBase.class + " or " + EntidadAuditable.class, show);
					}

					if (clazz.getGenericSuperclass().equals(EntidadBase.class)
							|| clazz.getGenericSuperclass().equals(EntidadAuditable.class)) {
						if (object.getClass().getAnnotation(AttributeOverrides.class) == null)
							instance.invalidEntity("Entity " + object.getClass()
									+ " does not overrides attributes (@AttributeOverrides) from entity superclass", show);
						if (object.getClass().getAnnotation(Filter.class) == null)
							instance.invalidEntity("Entity " + object.getClass()
									+ " does not have a @Filter annotation", show);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
