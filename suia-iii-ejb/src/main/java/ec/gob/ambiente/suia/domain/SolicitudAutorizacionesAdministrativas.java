package ec.gob.ambiente.suia.domain;

import java.util.Date;
import java.util.List;

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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the categories_catalog database table.
 * 
 */
@Entity
@Table(name = "request_administrative_authorizations", catalog = "", schema = "suia_iii")
@NamedQuery(name = "SolicitudAutorizacionesAdministrativas.findAll", query = "SELECT s FROM SolicitudAutorizacionesAdministrativas s")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "reaa_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "reaa_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "reaa_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "reaa_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "reaa_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "reaa_status = 'TRUE'")
public class SolicitudAutorizacionesAdministrativas extends EntidadAuditable {

	private static final long serialVersionUID = -7863626682551741822L;

	public static final String FIND_ALL = "ec.com.magmasoft.business.domain.SolicitudAutorizacionesAdministrativas.findAll";

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "REQUEST_ADMINISTRATIVE_AUTORIZATIONS_REAAID_GENERATOR", sequenceName = "seq_reaa_id", schema = "suia_iii")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REQUEST_ADMINISTRATIVE_AUTORIZATIONS_REAAID_GENERATOR")
	@Column(name = "reaa_id", unique = true, nullable = false)
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "user_id")
	@ForeignKey(name = "fk_request_administrative_authorizationsuser_idusersuser_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "user_status = 'TRUE'")
	private Usuario promotor;

	@Getter
	@Setter
	@Column(name = "reaa_record", length = 45)
	private String registro;

	@Getter
	@Setter
	@Column(name = "reaa_category", length = 45)
	private String categoria;

	@Getter
	@Setter
	@Column(name = "reaa_resolution_number", length = 45)
	private String numeroResolucion;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "pren_id")
	@ForeignKey(name = "fk_request_admin_authnspren_idprojects_environmental_licensingpren_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "pren_status = 'TRUE'")
	private ProyectoLicenciamientoAmbiental proyecto;

	@Getter
	@Setter
	@Column(name = "reaa_emisión_date")
	private Date fechaEmision;

	@Getter
	@Setter
	@Column(name = "reaa_process_id")
	private Long proceso;

	@Getter
	@Setter
	// bi-directional many-to-one association to CatalogoCategorias
	@OneToMany(mappedBy = "solicitudAutorizacionesAdministrativas")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "ragl_status = 'TRUE'")
	private List<SolicitudAutorizacionesAdministrativasCoordenada> cantones;

}