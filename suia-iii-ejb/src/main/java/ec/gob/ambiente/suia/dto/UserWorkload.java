package ec.gob.ambiente.suia.dto;

import ec.gob.ambiente.suia.domain.Usuario;
import lombok.Getter;
import lombok.Setter;

public class UserWorkload {

	public static final String SOURCE_TYPE_EXTERNAL_SUIA = "source_type_external_suia";
	public static final String SOURCE_TYPE_INTERNAL = "source_type_internal";

	@Getter
	@Setter
	private String id;

	@Getter
	@Setter
	private String userName;

	@Getter
	@Setter
	private String fullName;

	@Getter
	@Setter
	private Integer tramites;

	@Getter
	@Setter
	private double carga;

	@Getter
	@Setter
	private String sourceType;

	public UserWorkload(Object[] array) {
		this.id = ((Integer) array[0]).toString();
		this.userName = (String) array[1];
		this.fullName = (String) array[2];
		this.sourceType = SOURCE_TYPE_INTERNAL;
	}
	
	public UserWorkload(Usuario usuario) {
		this.id = usuario.getId().toString();
		this.userName = usuario.getNombre();
		if(usuario.getPersona() != null)
			this.fullName = usuario.getPersona().getNombre();
		else
			this.fullName = usuario.getNombre();
		this.sourceType = SOURCE_TYPE_INTERNAL;
	}

	public UserWorkload() {
	}

	public boolean isInternal() {
		return getSourceType().equals(SOURCE_TYPE_INTERNAL);
	}

	@Override
	public boolean equals(Object obj) {
		return this.getUserName().equals(((UserWorkload) obj).getUserName())
				&& this.getSourceType().equals(((UserWorkload) obj).getSourceType());
	}
}
