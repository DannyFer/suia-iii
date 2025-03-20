package ec.gob.ambiente.rcoa.sustancias.quimicas.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="activity_level", schema = "coa_chemical_sustances")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "acle_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "acle_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "acle_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "acle_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "acle_user_update")) })
public class ActividadNivel extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;	
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="acle_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="acle_name_activity")
	private String nombreActividad;
	
	@Getter
	@Setter
	@Column(name="acle_level")
	private Integer nivel;
	
	@Getter
	@Setter
	@Column(name="acle_order")
	private Integer orden;
	
	
}