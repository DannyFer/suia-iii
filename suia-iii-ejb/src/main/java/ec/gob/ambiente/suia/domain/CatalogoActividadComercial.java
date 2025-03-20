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
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;


/**
 * The persistent class for the commercial_activity_catalog database table.
 * 
 */
@Entity
@Table(name="commercial_activity_catalog", schema = "suia_iii")
@NamedQuery(name="CatalogoActividadComercial.findAll", query="SELECT a FROM CatalogoActividadComercial a")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "coac_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "coac_status = 'TRUE'")
public class CatalogoActividadComercial extends EntidadBase {

	private static final long serialVersionUID = 4218698700749092817L;

	@Id
	@SequenceGenerator(name = "COMMERCIAL_ACTIVITY_CATALOG_GENERATOR", sequenceName = "commercial_activity_catalog_id_seq", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMMERCIAL_ACTIVITY_CATALOG_GENERATOR")
	@Column(name="coac_id")
	@Getter
	@Setter
	private Integer id;
	
	@Column(name="coac_activity_name")
	@Getter
	@Setter
	private String nombreActividad;
	
	@Column(name="coac_description")
	@Getter
	@Setter
	private String descripcion;
	
	//bi-directional many-to-one association to phase
	@ManyToOne
	@JoinColumn(name="secp_id")
	@ForeignKey(name = "sectors_classifications_phases_commercial_activity_catalog_fk")
	@Getter
	@Setter
	private CatalogoCategoriaFase categoriaFase;

        public CatalogoActividadComercial() {
        }

        public CatalogoActividadComercial(Integer id) {
            this.id = id;
        }
        
        
        
	
}