package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name = "technicals_catalog", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "teca_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "teca_status = 'TRUE'")
public class CatalogoTecnica extends EntidadBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2303965377470184906L;

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "TECHNICALS_CATALOGS_GENERATOR", sequenceName = "technicals_catalog_seq", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TECHNICALS_CATALOGS_GENERATOR")
	@Column(name = "teca_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "teca_name")
	private String nombre;
	
	@Getter
	@Setter
	@Column(name = "teca_description")
	private String descripcion;
	
	@Getter
	@Setter
	@Transient
	private boolean nuevo;
		
}