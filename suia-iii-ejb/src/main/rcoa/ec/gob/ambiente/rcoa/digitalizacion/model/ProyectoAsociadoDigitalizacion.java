package ec.gob.ambiente.rcoa.digitalizacion.model;


import javax.persistence.*;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.rcoa.agrupacionAutorizaciones.model.AutorizacionAdministrativa;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the associated_projects database table.
 * 
 */
@Entity
@Table(name="associated_projects", schema = "coa_digitalization_linkage")
@NamedQuery(name="ProyectoAsociadoDigitalizacion.findAll", query="SELECT p FROM ProyectoAsociadoDigitalizacion p")

@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "aspr_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "aspr_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "aspr_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "aspr_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "aspr_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "aspr_status = 'TRUE'")

public class ProyectoAsociadoDigitalizacion extends EntidadAuditable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="aspr_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="aspr_code")
	private String codigo;

	@Getter
	@Setter
	@Column(name="aspr_system")
	private Integer sistemaOriginal;

	@Getter
	@Setter
	@Column(name="aspr_table_name")
	private String nombreTabla;

	@Getter
	@Setter
	@Column(name="aspr_type")
	private Integer tipoProyecto; //RGD, RSQ, ART

	@Getter
	@Setter
	@Column(name="aspr_code_id")
	private Integer proyectoAsociadoId;

	@Getter
	@Setter
	@Column(name="aspr_intersection_certificate")
	private boolean esActualizacionCI;

	@Getter
	@Setter
	@Transient
	private Integer idProcesos;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="enaa_id")
	private AutorizacionAdministrativaAmbiental autorizacionAdministrativaAmbiental;

	public ProyectoAsociadoDigitalizacion() {
	}

	@Getter
	@Setter
	@Transient
	private AutorizacionAdministrativa datosProyectosAsociados;
}