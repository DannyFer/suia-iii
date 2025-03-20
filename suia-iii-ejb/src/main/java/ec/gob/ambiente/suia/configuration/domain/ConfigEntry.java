package ec.gob.ambiente.suia.configuration.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "config_entries", catalog = "suia_configuration")
public class ConfigEntry implements Serializable {

	private static final long serialVersionUID = -6910615407773396263L;

	@Getter
	@Setter
	@Column(name = "coen_key")
	@Id
	private String key;

	@Getter
	@Setter
	@Column(name = "coen_value")
	private String value;

	@Getter
	@Setter
	@Column(name = "coen_active")
	private boolean active = true;
	
	public ConfigEntry(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public ConfigEntry() {
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof ConfigEntry))
			return false;
		return ((ConfigEntry) obj).getKey().equals(getKey());
	}
}
