package ec.gob.ambiente.suia.configuration.facade;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.suia.configuration.domain.ConfigEntry;
import ec.gob.ambiente.suia.configuration.service.ServiceBean;

@Stateless
public class ConfigurationFacade {

	@EJB
	private ServiceBean serviceBean;

	@SuppressWarnings("unchecked")
	public List<ConfigEntry> getConfigEntries() throws Exception {
		List<ConfigEntry> result = null;
		result = (List<ConfigEntry>) serviceBean.findAll(ConfigEntry.class);
		if (result != null && !result.isEmpty()) {
			Collections.sort(result, new Comparator<ConfigEntry>() {

				@Override
				public int compare(ConfigEntry o1, ConfigEntry o2) {
					return o1.getKey().compareTo(o2.getKey());
				}
			});
		}
		return result;
	}

	public void saveConfigEntry(ConfigEntry configEntry) throws Exception {
		serviceBean.save(configEntry);
	}

	public void updateConfigEntry(ConfigEntry configEntry) throws Exception {
		serviceBean.update(configEntry);
	}

	public void deleteConfigEntry(ConfigEntry configEntry) throws Exception {
		serviceBean.delete(configEntry);
	}

	public void deleteAllConfigurations() throws Exception {
		Query queryEntries = serviceBean.getEntityManager().createQuery("DELETE FROM ConfigEntry");
		queryEntries.executeUpdate();
	}

	public void createAllConfigurations(List<ConfigEntry> configEntries) throws Exception {
		for (ConfigEntry configEntry : configEntries) {
			saveConfigEntry(configEntry);
		}
	}

}
