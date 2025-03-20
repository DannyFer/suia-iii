package ec.gob.ambiente.rcoa.digitalizacion.model;

import javax.persistence.*;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the protected_areas database table.
 * 
 */
@Entity
@Table(name="protected_areas", schema = "coa_digitalization_linkage")
@NamedQuery(name="AreasProtegidasBosques.findAll", query="SELECT a FROM AreasProtegidasBosques a")

@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "prar_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "prar_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "prar_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "prar_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "prar_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prar_status = 'TRUE'")

public class AreasProtegidasBosques extends EntidadAuditable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="prar_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="enaa_id")
	private AutorizacionAdministrativaAmbiental autorizacionAdministrativaAmbiental;

	@Getter
	@Setter	
	@Column(name="prar_name")
	private String nombre;

	@Getter
	@Setter
	@Column(name="prar_order")
	private Integer orden;

	@Getter
	@Setter
	@Column(name="prar_type")
	private Integer tipo;

	@Getter
	@Setter
	@Column(name="prar_type_entry")
	private Integer tipoIngreso;

	public AreasProtegidasBosques() {
	}
}
	
