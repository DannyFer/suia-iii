package ec.gob.ambiente.suia.domain.base;

import java.util.Date;

import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

/**
 * Entidad de la que heredan todas las entidades que se manejen en formulaios
 * 
 * @author Liliana Chacha
 * 
 */

@MappedSuperclass
public abstract class EntidadAuditable extends EntidadBase {

	private static final long serialVersionUID = -7015471629526726517L;

	@Temporal(TemporalType.TIMESTAMP)
	@Getter
	@Setter
	protected Date fechaCreacion;

	@Temporal(TemporalType.TIMESTAMP)
	@Getter
	@Setter	
	protected Date fechaModificacion;

	@Getter
	@Setter
	protected String usuarioCreacion;

	@Getter
	@Setter
	protected String usuarioModificacion;

}
