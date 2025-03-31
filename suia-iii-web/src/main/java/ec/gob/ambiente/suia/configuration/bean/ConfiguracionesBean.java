/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.configuration.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.configuration.domain.ConfigEntry;
import ec.gob.ambiente.suia.configuration.facade.ConfigurationFacade;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.utils.JsfUtil;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: 03/03/2015]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class ConfiguracionesBean implements Serializable {

	private static final long serialVersionUID = 3841517261909051746L;

	@EJB
	private ConfigurationFacade configurationFacade;

	@Setter
	private List<ConfigEntry> entries;

	@Setter
	private ConfigEntry configEntry;

	@Getter
	@Setter
	private String entryKey;
	
	@Getter
	@Setter
	private boolean edit;

	@PostConstruct
	private void init() throws Exception {
		if(JsfUtil.getLoggedUser()!=null && Usuario.isUserInRole(JsfUtil.getLoggedUser(), "admin")){
			entries = configurationFacade.getConfigEntries();
		}else {
			JsfUtil.redirectTo("/start.jsf");
		}
	}

	public void clear() {
		this.entries = null;
		this.edit = false;
	}

	public void clearEntry() {
		configEntry = null;
		this.entryKey = null;
	}

	public void editEntry(ConfigEntry configEntry) {
		this.entryKey = configEntry.getKey();
		this.configEntry = configEntry;
	}

	public ConfigEntry getConfigEntry() {
		return configEntry == null ? configEntry = new ConfigEntry() : configEntry;
	}

	public List<ConfigEntry> getEntries() {
		return entries == null ? entries = new ArrayList<ConfigEntry>() : entries;
	}
}
