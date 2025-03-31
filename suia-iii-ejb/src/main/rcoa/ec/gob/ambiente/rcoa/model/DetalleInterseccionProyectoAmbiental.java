package ec.gob.ambiente.rcoa.model;

import java.io.Serializable;

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
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;




@Entity
@Table(name = "details_intersection_project_licencing_coa", schema = "coa_mae")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "dein_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "dein_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "dein_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "dein_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "dein_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "dein_status = 'TRUE'")
public class DetalleInterseccionProyectoAmbiental extends EntidadAuditable implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1127154388057272334L;
	
	@Getter
	@Setter
	@Id
	@Column(name = "dein_id")
	@SequenceGenerator(name = "PROJECTS_ENVIRONMENTAL_LICENSING_DEIN_ID_GENERATOR", sequenceName = "details_intersection_project_licencing_coa_den_id_seq", schema = "coa_mae", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROJECTS_ENVIRONMENTAL_LICENSING_DEIN_ID_GENERATOR")
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "inpr_id")
	@ForeignKey(name = "fk_details_intersect_project_inpr_id_intersect_project_inpr_id")
	@Getter
	@Setter
	private InterseccionProyectoLicenciaAmbiental interseccionProyectoLicenciaAmbiental;
		
	@Getter
	@Setter
	@Column(name = "dein_geometry_name", length = 255)
	private String nombreGeometria;	
	
	@Getter
	@Setter
	@Column(name = "dein_geometry_id")
	private int idGeometria;	
	
	@Getter
	@Setter
	@Column(name = "dein_subsystem_layer", length = 255)
	private String capas;
	
	@Getter
	@Setter
	@Column(name = "dein_code", length = 100)
	private String codigoConvenio;
	
	@Getter
	@Setter
	@Column(name = "dein_cap", length = 100)
	private String codigoUnicoCapa;
	
	@Getter
	@Setter
	@Column(name = "dein_zonal")
	private String zona;

	@Getter
	@Setter
	@Column(name = "dein_total_surface")
	private Double areaTotalCapa;

	@Getter
	@Setter
	@Column(name = "dein_surface_percentage")
	private Double porcentajeAreaInterseccion;
	
	@Getter
	@Setter
	@Column(name = "dein_surface")
	private Double areaInterseccion;

	@Getter
	@Setter
	@Column(name = "dein_agreement_type")
	private String tipoConvenio;

	@Getter
	@Setter
	@Column(name = "dein_beneficiary")
	private String beneficiarioConvenio;
	
	@Getter
	@Setter
	@Transient
	private Double areaInterseccionProyecto;

	
	public String getNombreAreaCompleto() {
		if(zona != null && zona.equals("NO APLICA")) {
			return nombreGeometria;
		} else {
			return nombreGeometria + " " + zona;
		}
	}

}
