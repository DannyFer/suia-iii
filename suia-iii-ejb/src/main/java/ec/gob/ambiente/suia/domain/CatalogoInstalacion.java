package ec.gob.ambiente.suia.domain;

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
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name = "installation_catalogs", schema = "suia_iii")
@NamedQuery(name = "CatalogoInstalacion.findAll", query = "SELECT i FROM CatalogoInstalacion i")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "inca_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "inca_status = 'TRUE'")
public class CatalogoInstalacion extends EntidadBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2303980597470184906L;

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "INSTALLATION_CATALOGS_GENERATOR", sequenceName = "installation_catalog_seq", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "INSTALLATION_CATALOGS_GENERATOR")
	@Column(name = "inca_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "inca_name")
	private String nombre;
	
	@Getter
	@Setter
	@Column(name = "inca_description")
	private String descripcion;
	
	@Getter
	@Setter
	@JoinColumn(name = "secl_id", referencedColumnName = "secl_id")
	@ManyToOne(fetch = FetchType.LAZY)
	@ForeignKey(name = "fk_installation_catalogs_secl_id_subsector_secl_id")
	private TipoSubsector tipoSubsector;
	
	@Getter
	@Setter
	@Transient
	private boolean nuevo;
	
}