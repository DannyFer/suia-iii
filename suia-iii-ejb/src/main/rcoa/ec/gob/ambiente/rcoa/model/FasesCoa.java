package ec.gob.ambiente.rcoa.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

@Entity
@Table(name="phases", schema="coa_mae")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "phas_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "phas_creation_date")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "phas_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "phas_creator_user")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "phas_user_update")) })
public class FasesCoa extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	@Id
	@Column(name="phas_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="phas_name")
	private String descripcion;

	@Getter
	@Setter
	@Column(name="phas_code")
	private String codigo;
	
}