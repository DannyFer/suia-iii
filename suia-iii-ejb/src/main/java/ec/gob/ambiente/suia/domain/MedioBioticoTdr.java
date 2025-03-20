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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Type;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name="biotic_environments", schema="suia_iii")
@NamedQueries({
	@NamedQuery(name = MedioBioticoTdr.FIND_BY_PROJECT_CATE, query = "SELECT p FROM MedioBioticoTdr p WHERE p.proyectoLicenciamientoAmbiental.id = :idProyecto AND p.catalogoGeneral.tipoCatalogo.id = :idCaty"),
	@NamedQuery(name = MedioBioticoTdr.FIND_BY_TDR_EIA, query = "SELECT mb FROM MedioBioticoTdr mb WHERE mb.tdrEiaLicencia.id = :idTdr") })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "bien_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "bien_status = 'TRUE'")
public class MedioBioticoTdr extends EntidadBase{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4420269698601401328L;
	
	public static final String FIND_BY_PROJECT_CATE = "ec.com.magmasoft.business.domain.MedioBioticoTdr.findByProjectCate";
	public static final String FIND_BY_TDR_EIA = "ec.com.magmasoft.business.domain.MedioBioticoTdr.findByTdrEia";
	
	@Getter
	@Setter
	@Id
	@Column(name = "bien_id")
	@SequenceGenerator(name = "BIOTIC_ENVITOMENTS_BIENID_GENERATOR", sequenceName = "seq_bien_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BIOTIC_ENVITOMENTS_BIENID_GENERATOR")
	private Integer id;
	
	@Getter
	@Setter
    @Column(name = "geca_id",insertable = false, updatable = false)
    private Integer idCatalogo;
	
	@Getter
	@Setter
	@Column(name="bien_information_type")
	private String tipoInformacion;
	
	@Getter
	@Setter
	@Type(type = "text")
	@Column(name="bien_methodology")
	private String metodogia;
	
	@Getter
	@Setter
	@Column(name="bien_justification")
	private String justificacion;

	@Getter
	@Setter
	@Column(name="bien_applicable")
	private String aplicable;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "pren_id", referencedColumnName = "pren_id")
	@ForeignKey(name = "fk_biotic_environments_projects_enviroment_licensing_pren_id")
	private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "geca_id", referencedColumnName = "geca_id")
	@ForeignKey(name = "fk_biotic_environments_general_catalogs_geca_id")
	private CatalogoGeneral catalogoGeneral;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "tdel_id", referencedColumnName = "tdel_id")
	@ForeignKey(name = "fk_biotic_environments_tdr_eia_licenses_tdel_id")
	private TdrEiaLicencia tdrEiaLicencia;
}
