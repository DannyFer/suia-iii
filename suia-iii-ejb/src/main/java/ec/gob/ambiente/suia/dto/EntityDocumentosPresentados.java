package ec.gob.ambiente.suia.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

public class EntityDocumentosPresentados implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7207459603550005577L;

	@Getter
	@Setter
	private String codigo;

	@Getter
	@Setter
	private String nombre;

	@Getter
	@Setter
	private String id;
	
	@Getter
	@Setter
	private String descripcioncatdoc;	

	
	@Getter
	@Setter
	private String presentado;
	
	@Getter
	@Setter
	private String fechapresentado;
	
	public EntityDocumentosPresentados() {
	}

	public EntityDocumentosPresentados(String codigo, String nombre, String id, String descripcioncatdoc,String presentado , String fechapresentado) {
		this.codigo = codigo;
		this.nombre = nombre;
		this.id = id;
		this.descripcioncatdoc=descripcioncatdoc;
		this.presentado= presentado;
		this.fechapresentado=fechapresentado;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof EntityDocumentosPresentados)) {
			return false;
		}
		EntityDocumentosPresentados other = (EntityDocumentosPresentados) object;
		if ((this.id == null && other.id != null)
				|| (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

}
