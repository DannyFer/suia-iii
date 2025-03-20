package ec.gob.ambiente.suia.domain;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

@NamedQueries({ @NamedQuery(name = TipoOrganizacion.FIND_BY_STATE, query = "SELECT t FROM TipoOrganizacion t WHERE t.estado = :estado") })
@Entity
@Table(name = "organizations_types", schema = "public")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "orty_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "orty_status = 'TRUE'")
public class TipoOrganizacion extends EntidadBase {

	private static final long serialVersionUID = 6725390790878011384L;

	public static final String FIND_BY_STATE = "ec.com.magmasoft.business.domain.TipoOrganizacion.findByState";
	
	// tipo de empresa
	public static Integer publica = 1;
	public static Integer privada = 2;
	public static Integer mixta = 3;
		
	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "ORGANIZATION_TYPES_GENERATOR", initialValue = 1, sequenceName = "seq_orty_id", schema = "public")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ORGANIZATION_TYPES_GENERATOR")
	@Column(name = "orty_id")
	private Integer id;
	@Getter
	@Setter
	@Column(name = "orty_name")
	private String nombre;
	@Getter
	@Setter
	@Column(name = "orty_description")
	private String descripcion;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "orty_parent_id")
	private TipoOrganizacion tipoOrganizacion;

	@Getter
	@Setter
	@OneToMany(mappedBy = "tipoOrganizacion", fetch = FetchType.LAZY)
	@ForeignKey(name = "fk_organization_types_orga_id_organization_types_orga_parent_id")
	private List<TipoOrganizacion> tipoOrganizaciones;

	@Getter
	@Setter
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tipoOrganizacion")
	private List<Organizacion> organizaciones;
	
	@Getter
	@Setter
	@Column(name = "orty_type")
	private Integer tipoEmpresa;

	public TipoOrganizacion() {
	}

	public TipoOrganizacion(Integer id) {
		this.id = id;
	}

}
