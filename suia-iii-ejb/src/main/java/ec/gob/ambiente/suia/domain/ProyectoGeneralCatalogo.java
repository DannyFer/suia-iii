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

/**
 * 
 * @author lili
 * 
 */
@Entity
@NamedQueries({
		@NamedQuery(name = "ProyectoGeneralCatalogo.findAll", query = "SELECT p FROM ProyectoGeneralCatalogo p"),
		@NamedQuery(name = ProyectoGeneralCatalogo.FIND_BY_PROJECT, query = "SELECT p FROM ProyectoGeneralCatalogo p WHERE p.proyectoLicenciamientoAmbiental.id = :idProyecto"),
		@NamedQuery(name = ProyectoGeneralCatalogo.FIND_BY_PROJECT_CATE, query = "SELECT p FROM ProyectoGeneralCatalogo p WHERE p.proyectoLicenciamientoAmbiental.id = :idProyecto AND p.catalogoGeneral.tipoCatalogo.id = :idCaty") })
@Table(name = "projects_general_catalogs", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "prgc_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prgc_status = 'TRUE'")
public class ProyectoGeneralCatalogo extends EntidadBase {

	public static final String FIND_ALL = "ec.com.magmasoft.business.domain.ProyectoGeneralCatalogo.findAll";
	public static final String FIND_BY_PROJECT = "ec.com.magmasoft.business.domain.ProyectoGeneralCatalogo.findByProject";
	public static final String FIND_BY_PROJECT_CATE = "ec.com.magmasoft.business.domain.ProyectoGeneralCatalogo.findByProjectCate";
	/**
	 * 
	 */
	private static final long serialVersionUID = -489980578805678177L;

	@Getter
	@Setter
	@Id
	@Column(name = "prgc_id")
	@SequenceGenerator(name = "PROJECTS_GENERAL_CATALOGS_PRGCID_GENERATOR", sequenceName = "seq_prgc_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROJECTS_GENERAL_CATALOGS_PRGCID_GENERATOR")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "pren_id", referencedColumnName = "pren_id")
	@ForeignKey(name = "fk_projects_enviroment_licensingpren_id_general_catalogspren_id")
	private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "geca_id", referencedColumnName = "geca_id")
	@ForeignKey(name = "fk_generals_catalogs_geca_id_generals_catalogs_geca_id")
	private CatalogoGeneral catalogoGeneral;
        
        @Getter
	@Setter
        @Column(name = "geca_id",insertable = false, updatable = false)
        private Integer idCatalogo;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "tdal_id", referencedColumnName = "tdal_id")
	@ForeignKey(name = "fk_projects_general_catalogs_tdal_id_tdr_audi_licenses_tdal_id")
	private TdrAuditoriaLicencia tdrAuditoriaLicencia;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "tdel_id", referencedColumnName = "tdel_id")
	@ForeignKey(name = "fk_projects_general_catalogs_tdel_id_tdr_audi_licenses_tdel_id")
	private TdrEiaLicencia tdrEiaLicencia;
	
	@Getter
	@Setter
//	@Enumerated(EnumType.STRING)
	@Column(name="prgc_information_type")
	private String tipoInformacion;
	
	@Getter
	@Setter
	@Type(type = "text")
	@Column(name="prgc_methodology")
	private String metodogia;
	
	@Getter
	@Setter
	@Column(name="prgc_justification")
	private String justificacion;

	@Getter
	@Setter
	@Column(name="prgc_applicable")
	private String aplicable;
}
