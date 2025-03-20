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
@Table(name="physical_environments", schema="suia_iii")
@NamedQueries({
	@NamedQuery(name = MedioFisico.FIND_BY_PROJECT_CATE, query = "SELECT p FROM MedioFisico p WHERE p.proyectoLicenciamientoAmbiental.id = :idProyecto AND p.catalogoGeneral.tipoCatalogo.id = :idCaty"),
	@NamedQuery(name = MedioFisico.FIND_BY_TDR_EIA, query = "SELECT mf FROM MedioFisico mf WHERE mf.tdrEiaLicencia.id = :idTdr") })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "phen_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "phen_status = 'TRUE'")
public class MedioFisico extends EntidadBase{

	public static final String FIND_BY_PROJECT_CATE = "ec.com.magmasoft.business.domain.MedioFisico.findByProjectCate";
	public static final String FIND_BY_TDR_EIA = "ec.com.magmasoft.business.domain.MedioFisico.findByTdrEia";
	/**
	 * 
	 */
	private static final long serialVersionUID = 4944043129949634874L;
	
		
	@Getter
	@Setter
	@Id
	@Column(name = "phen_id")
	@SequenceGenerator(name = "PHYSICAL_ENVITOMENTS_PHENID_GENERATOR", sequenceName = "seq_phen_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PHYSICAL_ENVITOMENTS_PHENID_GENERATOR")
	private Integer id;
	
	@Getter
	@Setter
    @Column(name = "geca_id",insertable = false, updatable = false)
    private Integer idCatalogo;
	
	@Getter
	@Setter
	@Column(name="phen_information_type")
	private String tipoInformacion;
	
	@Getter
	@Setter
	@Type(type = "text")
	@Column(name="phen_methodology")
	private String metodogia;
	
	@Getter
	@Setter
	@Column(name="phen_justification")
	private String justificacion;

	@Getter
	@Setter
	@Column(name="phen_applicable")
	private String aplicable;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "pren_id", referencedColumnName = "pren_id")
	@ForeignKey(name = "fk_physical_environments_projects_enviroment_licensing_pren_id")
	private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "geca_id", referencedColumnName = "geca_id")
	@ForeignKey(name = "fk_physical_environments_general_catalogs_geca_id")
	private CatalogoGeneral catalogoGeneral;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "tdel_id", referencedColumnName = "tdel_id")
	@ForeignKey(name = "fk_physical_environments_tdr_eia_licenses_tdel_id")
	private TdrEiaLicencia tdrEiaLicencia;
}
