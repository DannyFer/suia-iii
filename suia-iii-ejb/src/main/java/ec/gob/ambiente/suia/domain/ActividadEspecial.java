package ec.gob.ambiente.suia.domain;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the special_activity database table.
 * 
 */
@Entity
@Table(name = "special_activity", schema = "suia_iii")
@NamedQuery(name = "ActividadEspecial.findAll", query = "SELECT s FROM ActividadEspecial s")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "spac_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "spac_status = 'TRUE'")
public class ActividadEspecial extends EntidadBase {

	private static final long serialVersionUID = 4218664800634992817L;

	@Id
	@SequenceGenerator(name = "SPECIAL_ACTIVITY_GENERATOR", sequenceName = "special_activity_id_seq", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SPECIAL_ACTIVITY_GENERATOR")
	@Column(name = "spac_id")
	@Getter
	@Setter
	private Integer id;

    @Getter
    @Setter
    @JoinColumn(name = "secl_id", referencedColumnName = "secl_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "fk_sector_classificationsecl_id_special_activitysecl_id")
    @Filter(name = EntidadBase.FILTER_ACTIVE, condition = "secl_status = 'TRUE'")
    private TipoSubsector tipoSubsector;

}