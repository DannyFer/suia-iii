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
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the environmental_certificate_pronouncing database table.
 * 
 */
@NamedQueries({ @NamedQuery(name = CertificadoAmbientalPronunciamiento.FIND_BY_PROJECT, query = "SELECT c FROM CertificadoAmbientalPronunciamiento c WHERE c.proyecto.id = :idProyecto order by 1 desc") })
@Entity
@Table(name = "environmental_certificate_pronouncing", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "ecpr_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "ecpr_date_create")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "ecpr_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "ecpr_user_create")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "ecpr_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "ecpr_status = 'TRUE'")
public class CertificadoAmbientalPronunciamiento extends EntidadAuditable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5120854562491824737L;

	public static final String FIND_BY_PROJECT = "ec.com.magmasoft.business.domain.CertificadoAmbientalPronunciamiento.byProject";

	@Getter
	@Setter
	@Id
	@Column(name = "ecpr_id")	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
    @Getter
    @Setter
    @JoinColumn(name = "pren_id", referencedColumnName = "pren_id")
    @ManyToOne
    @ForeignKey(name = "fk_ecpr_pren")
    private ProyectoLicenciamientoAmbiental proyecto;
    
    @Getter
	@Setter	
	@JoinColumn(name = "docu_id",referencedColumnName = "docu_id")
	@ManyToOne
	@ForeignKey(name = "fk_ecpr_docu")	
	private Documento documento;
    
	@Getter
	@Setter
	@Column(name = "ecpr_code",length = 50)
	private String codigo;
	
	
	@Getter
	@Setter
	@Column(name = "ecpr_favorable_pronouncement")
	private Boolean pronunciamientoFavorable;
	
}