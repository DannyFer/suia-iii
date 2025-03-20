package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * <b> Entity. </b>
 * 
 * @author carlos.pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: carlos.pupo, Fecha: 08/06/2015]
 *          </p>
 */
@Entity
@Table(name = "hazardous_wastes_collectors_provider_locations", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "wcpl_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wcpl_status = 'TRUE'")
public class GeneradorDesechosRecolectorSede extends EntidadBase {

	private static final long serialVersionUID = 1549490935970893457L;

	@Getter
	@Setter
	@Id
	@Column(name = "wcpl_id")
	@SequenceGenerator(name = "HAZARDOUS_WASTES_COLLECTORS_PROVIDER_LOCATIONS_WCPL_ID_GENERATOR", sequenceName = "seq_wcpl_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "HAZARDOUS_WASTES_COLLECTORS_PROVIDER_LOCATIONS_WCPL_ID_GENERATOR")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "wcpl_provider")
	private String otraEmpresa;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "hwco_id")
	@ForeignKey(name = "fk_wepl_id_hwco_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "hwco_status = 'TRUE'")
	private GeneradorDesechosRecolector generadorDesechosRecolector;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "hwpl_id")
	@ForeignKey(name = "fk_wepl_id_hwpl_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wepl_status = 'TRUE'")
	private SedePrestadorServiciosDesechos sedePrestadorServiciosDesechos;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "docu_id")
	@ForeignKey(name = "fk_hazardous_wastes_collectors_provider_locations_documents")
	private Documento permisoAmbiental;
	
}
