/*
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.composite.bean;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: 23/12/2014]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class ComentarioEntidadCC implements Serializable{
    
    private static final long serialVersionUID = -3574585287113629686L;

	@Getter
	private Map<String, String> values = new ConcurrentHashMap<String, String>();
	
	public boolean getValue(String name) {
		if(!values.containsKey(name)) {
			values.put(name, "true");
		}
		return Boolean.parseBoolean(values.get(name));
	}
}
