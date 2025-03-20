/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.configuration.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.suia.configuration.bean.ConfiguracionesBean;
import ec.gob.ambiente.suia.configuration.domain.ConfigEntry;
import ec.gob.ambiente.suia.configuration.facade.ConfigurationFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.utils.Constantes;
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
public class ConfiguracionesController {

	@Setter
	@ManagedProperty(value = "#{configuracionesBean}")
	private ConfiguracionesBean configuracionesBean;

	@EJB
	private ConfigurationFacade configurationFacade;

	@EJB
	private DocumentosFacade documentosFacade;

	private static final Logger LOGGER = Logger.getLogger(ConfiguracionesController.class);

	public void saveConfigEntry() {
		try {
			if (!validateEntryDuplicate()) {
				if (!configuracionesBean.getEntries().contains(configuracionesBean.getConfigEntry())) {
					configurationFacade.saveConfigEntry(configuracionesBean.getConfigEntry());
					configuracionesBean.setEntries(configurationFacade.getConfigEntries());
					configuracionesBean.setConfigEntry(null);
					configuracionesBean.setEdit(false);
					Constantes.resetConfigurations();
				}
				JsfUtil.addCallbackParam("entry");
			} else
				JsfUtil.addMessageError("Ya existe una entrada con ese key.");

		} catch (Exception exception) {
			LOGGER.error("Error al adicionar la entrada de configuracion", exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	public void updateConfigEntry() {
		try {
			configurationFacade.updateConfigEntry(configuracionesBean.getConfigEntry());
			configuracionesBean.setEntries(configurationFacade.getConfigEntries());
			configuracionesBean.setConfigEntry(null);
			configuracionesBean.setEdit(false);
			Constantes.resetConfigurations();
			JsfUtil.addCallbackParam("entry");

		} catch (Exception exception) {
			LOGGER.error("Error al actualizar la entrada de configuracion", exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	public void edit(ConfigEntry configEntry) {
		configuracionesBean.setConfigEntry(configEntry);
		configuracionesBean.setEdit(true);
	}

	public void resetSystemConfigurations() {
		Constantes.resetConfigurations();
	}

	public void deleteConfigEntry(ConfigEntry configEntry) {
		try {
			configurationFacade.deleteConfigEntry(configEntry);
			configuracionesBean.setEntries(configurationFacade.getConfigEntries());
			configuracionesBean.setEdit(false);
			Constantes.resetConfigurations();
		} catch (Exception exception) {
			LOGGER.error("Error al eliminar la entrada de configuracion", exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	public boolean validateEntryDuplicate() {
		for (int i = 0; i < configuracionesBean.getEntries().size(); i++) {
			if (configuracionesBean.getEntries().get(i).getKey().equals(configuracionesBean.getEntryKey())) {
				return true;
			}
		}
		return false;
	}

	public void importConfiguration(FileUploadEvent event) {
		List<ConfigEntry> entries = new ArrayList<ConfigEntry>();
		try {
			Properties properties = new Properties();
			properties.load(event.getFile().getInputstream());

			for (Object key : properties.keySet()) {
				entries.add(new ConfigEntry(key.toString(), properties.getProperty(key.toString())));
			}

			boolean fileInvalid = validateImport(entries);

			if (!fileInvalid) {
				configurationFacade.deleteAllConfigurations();
				configurationFacade.createAllConfigurations(entries);
				configuracionesBean.setEntries(configurationFacade.getConfigEntries());
				Constantes.resetConfigurations();
			} else
				JsfUtil.addMessageError("Fichero inválido. Existen entradas duplicadas.");
		} catch (Exception exception) {
			LOGGER.error("Error al importar la configuracion", exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	public StreamedContent getStream() throws Exception {
		DefaultStreamedContent content = null;
		try {
			byte[] data = exportConfiguration(configurationFacade.getConfigEntries());
			content = new DefaultStreamedContent(new ByteArrayInputStream(data));
			content.setName("configuration.properties");

		} catch (Exception exception) {
			LOGGER.error("Error al exportar la configuracion", exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		return content;
	}

	public byte[] exportConfiguration(List<ConfigEntry> entries) throws Exception {
		byte[] content = null;
		try {
			Properties properties = new Properties();
			for (ConfigEntry configEntry : entries) {
				properties.put(configEntry.getKey(), configEntry.getValue());
				System.out.println(configEntry.getKey());
			}
			File tempFile = File.createTempFile("configurations", ".properties");
			properties.store(new FileOutputStream(tempFile), "CONFIGURATIONS");
			content = documentosFacade.getBytesFromFile(tempFile);
		} catch (IOException i) {
			i.printStackTrace();
		}
		return content;
	}

	public boolean validateImport(List<ConfigEntry> resultEntries) {
		for (int i = 0; i < resultEntries.size() - 1; i++) {
			for (int j = 0; j < resultEntries.size(); j++) {
				if (i != j && resultEntries.get(i).getKey().equals(resultEntries.get(j).getKey()))
					return true;
			}
		}
		return false;
	}
}
