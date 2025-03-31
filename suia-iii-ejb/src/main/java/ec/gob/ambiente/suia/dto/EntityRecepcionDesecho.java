/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author ishmael
 */
public class EntityRecepcionDesecho implements Serializable {

	private static final long serialVersionUID = 7550177845009672921L;

	@Getter
	@Setter
	private Integer idRecepcion;
	
	@Getter
	@Setter
	private Integer idDesechoPeligroso;

	@Getter
	@Setter
	private String codigo;

	@Getter
	@Setter
	private String descripcionDesecho;

	@Getter
	@Setter
	private String tipoEstado;

	public EntityRecepcionDesecho(String idRecepcion, String codigo, String descripcionDesecho, String tipoEstado) {
		this.idRecepcion = Integer.valueOf(idRecepcion);
		this.codigo = codigo;
		this.descripcionDesecho = descripcionDesecho;
		this.tipoEstado = tipoEstado;
	}
	
	public EntityRecepcionDesecho(String idRecepcion, String codigo, String descripcionDesecho, String tipoEstado, String idDesechoPeligroso) {
		this.idRecepcion = Integer.valueOf(idRecepcion);
		this.codigo = codigo;
		this.descripcionDesecho = descripcionDesecho;
		this.tipoEstado = tipoEstado;
		this.idDesechoPeligroso = Integer.valueOf(idDesechoPeligroso);
	}

	@Override
	public String toString() {
		return codigo+" - "+descripcionDesecho;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof EntityRecepcionDesecho)) {
			return false;
		}
		EntityRecepcionDesecho other = (EntityRecepcionDesecho) obj;
		if (((this.idRecepcion == null) && (other.idRecepcion != null))
				|| ((this.idRecepcion != null) && !this.idRecepcion.equals(other.idRecepcion))) {
			return false;
		}
		return true;
	}
}
