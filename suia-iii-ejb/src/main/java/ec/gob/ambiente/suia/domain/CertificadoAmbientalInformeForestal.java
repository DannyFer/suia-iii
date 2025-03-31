package ec.gob.ambiente.suia.domain;

import java.util.Date;

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
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the environmental_certificate_report_forest database table.
 * 
 */
@NamedQueries({ @NamedQuery(name = CertificadoAmbientalInformeForestal.FIND_BY_PROJECT, query = "SELECT c FROM CertificadoAmbientalInformeForestal c WHERE c.estado=true and c.proyecto.id = :idProyecto order by 1 desc"),
	@NamedQuery(name = CertificadoAmbientalInformeForestal.FIND_BY_CODE, query = "SELECT c FROM CertificadoAmbientalInformeForestal c WHERE c.estado=true and c.codigo = :codigoInforme order by 1 desc")})
@Entity
@Table(name = "environmental_certificate_report_forest", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "ecrf_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "ecrf_date_create")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "ecrf_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "ecrf_user_create")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "ecrf_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "ecrf_status = 'TRUE'")
public class CertificadoAmbientalInformeForestal extends EntidadAuditable {
	
	
	private static final long serialVersionUID = -8567167520518599567L;

	public static final String FIND_BY_PROJECT = "ec.com.magmasoft.business.domain.CertificadoAmbientalInformeForestal.byProject";
	public static final String FIND_BY_CODE = "ec.com.magmasoft.business.domain.CertificadoAmbientalInformeForestal.byCode";

	@Getter
	@Setter
	@Id
	@Column(name = "ecrf_id")	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
    @Getter
    @Setter
    @JoinColumn(name = "pren_id", referencedColumnName = "pren_id")
    @ManyToOne
    @ForeignKey(name = "fk_ecrf_pren")
    private ProyectoLicenciamientoAmbiental proyecto;
    
	@Getter
	@Setter
	@Column(name = "ecrf_code", length = 50)
	private String codigo;
   
	@Getter
	@Setter
	@Column(name = "ecrf_project_description", length = 2500)
	private String descripcionProyecto;
	
	@Getter
	@Setter
	@Column(name = "ecrf_objective", length = 2500)
	private String objetivo;
	   
	@Getter
	@Setter
	@Column(name = "ecrf_legal_framework", length = 2500)
	private String marcoLegal;
    
	@Getter
	@Setter
	@Column(name = "ecrf_vegetation_description", length = 2500)
	private String descripcionVegetacion;
	
	@Getter
	@Setter
	@Column(name = "ecrf_conclusions", length = 2500)
	private String conclusiones;
	
	@Getter
	@Setter
	@Column(name = "ecrf_recommendations", length = 2500)
	private String recomendaciones;
	
	@Getter
	@Setter
	@Column(name = "ecrf_inspection_date")
	private Date fechaInspeccion;
	
	@Getter
	@Setter
	@Column(name = "ecrf_removal_vegetation_cover")
	private Boolean remocionCoberturaVegetal;
	
	@Getter
	@Setter	
	@JoinColumn(name = "ecrf_attachments_docu_id",referencedColumnName = "docu_id")
	@ManyToOne
	@ForeignKey(name = "fk_ecrf_docu_attachments")	
	private Documento adjunto;

	
}