package ec.gob.ambiente.rcoa.model;

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

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;


@Entity
@Table(name = "project_environmental_certificate", schema = "coa_environmental_certificate")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "pece_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "pece_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "pece_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "pece_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "pece_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "pece_status = 'TRUE'")
public class ProyectoCertificadoAmbiental extends EntidadAuditable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7515502448093764077L;

	@Getter
	@Setter
	@Id
	@Column(name = "pece_id")
	@SequenceGenerator(name = "PROJECTS_ENVIRONMENTAL_LICENSING_PECE_ID_GENERATOR", sequenceName = "project_environmental_certificate_coa_pece_id_seq", schema = "coa_environmental_certificate", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROJECTS_ENVIRONMENTAL_LICENSING_PECE_ID_GENERATOR")
	private Integer id;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "prco_id")
	@ForeignKey(name = "fk_prco_id")
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
	@Setter
	@Column(name = "pece_code", length = 255)
	private String codigoCertificado;
	
	@Getter
	@Setter
	@Column(name = "pece_name", length = 255)
	private String nombreCertificado;
	
	@Getter
	@Setter
	@Column(name = "pece_description", length = 255)
	private String descripcion;
	
	@Getter
	@Setter
	@Column(name = "pece_observation_bd", length = 255)
	private String observacionDB;
	
	@Getter
	@Setter
	@Column(name = "pece_url_code_validation")
	private String url;
	
	@Getter
	@Setter
	@Column(name = "pece_area_id")
	public Integer idArea;
	
	@Getter
	@Setter
	@Column(name = "pece_user_sign")
	public String nombreAutoridad;
	
	@Getter
	@Setter
	@Column(name = "user_id")
	public Integer idUsuario;
	

}
