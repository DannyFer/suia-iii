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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the environmental_certificate_report_snap_species database table.
 * 
 */
@NamedQueries({ @NamedQuery(name = CertificadoAmbientalInformeSnapEspecie.FIND_BY_REPORT_SNAP, query = "SELECT c FROM CertificadoAmbientalInformeSnapEspecie c WHERE c.informeSnap.id = :idInforme order by 1 desc") })
@Entity
@Table(name = "environmental_certificate_report_snap_species", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "crss_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "crss_date_create")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "crss_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "crss_user_create")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "crss_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "crss_status = 'TRUE'")
public class CertificadoAmbientalInformeSnapEspecie extends EntidadAuditable {

	private static final long serialVersionUID = 5965534811695061950L;

	public static final String FIND_BY_REPORT_SNAP = "ec.com.magmasoft.business.domain.CertificadoAmbientalInformeSnapEspecie.byReport";

	@Getter
	@Setter
	@Id
	@Column(name = "crss_id")	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
    @Getter
    @Setter
    @JoinColumn(name = "ecrs_id", referencedColumnName = "ecrs_id")
    @ManyToOne(fetch = FetchType.LAZY)
    //@ForeignKey(name = "fk_certificate_intersection_pren_id_projects_environmental_lice_pren_id")
    private CertificadoAmbientalInformeSnap informeSnap;
	
	@Getter
	@Setter
	@Column(name = "crss_specie_name", length = 255)
	private String nombreEspecie;
	
	@Getter
	@Setter
	@Column(name = "crss_scientific_name", length = 255)
	private String nombreCientifico;
	
	@Getter
	@Setter
	@Column(name = "crss_type", length = 255)
	private String tipo;//Nativa(pregunta4) o Amenazada(Pregunta7)
	
	@Getter
	@Setter
	@Column(name = "crss_threat", length = 50)
	private String amenazaCategoriaLibroRojo;
	
	@Getter
	@Setter
	@Column(name = "crss_endemic", length = 50)
	private String endemica;
	
	@Getter
	@Setter
	@Column(name = "crss_sensitive", length = 50)
	private String sensible;
	
	@Getter
	@Setter
	@Column(name = "crss_migratory", length = 50)
	private String migratoria;
	
	@Getter
	@Setter
	@Column(name = "crss_cites", length = 50)
	private String cites;	

}