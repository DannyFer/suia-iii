package ec.gob.ambiente.suia.domain;

import javax.persistence.*;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the scdr_environmental_record_biotic_component database table.
 * 
 */
@Entity
@Table(name="scdr_environmental_record_biotic_component", schema="suia_iii")

@AttributeOverrides({@AttributeOverride(name = "estado", column = @Column(name = "erbc_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "erbc_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "erbc_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "erbc_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "erbc_user_update"))})

@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "erbc_status = 'TRUE'")
public class ComponenteBioticoSD extends EntidadAuditable {
	private static final long serialVersionUID = 1L;

	@Id
	@Getter
	@Setter
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="erbc_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="erbc_biotic_type")
	private String tipoBiotico;	

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "gcbi_id")
	@ForeignKey(name = "fk_biotic_componente_general_catalogs_biotic")
	private CatalogoGeneralBiotico catalogoGeneralBiotico;

	@Getter
	@Setter
	@Column(name = "scdr_id")
	private Integer perforacionExplorativa;

	public ComponenteBioticoSD() {
	}

	

}