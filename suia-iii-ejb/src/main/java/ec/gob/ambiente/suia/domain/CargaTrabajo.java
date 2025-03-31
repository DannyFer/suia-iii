package ec.gob.ambiente.suia.domain;

import java.util.Date;

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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the proyectos_licenciamiento_ambiental database table.
 * 
 */
@Entity
@Table(name = "work_load", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "wolo_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "user_creation_date")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "user_update_date")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "user_creator")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "user_update"))  })
@NamedQueries({ @NamedQuery(name = CargaTrabajo.LISTAR_TODO, query = "SELECT r FROM CargaTrabajo r ORDER BY r.id "),
	@NamedQuery(name = CargaTrabajo.OBTENER_POR_USUARIO, query = "SELECT w FROM CargaTrabajo w WHERE w.estado = true and w.usuario.id = :usuarioId order by w.fechaRegistro desc"),
	@NamedQuery(name = CargaTrabajo.OBTENER_TODOS, query = "SELECT w FROM CargaTrabajo w WHERE w.estado = true  ") })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wolo_status = 'TRUE'")
public class CargaTrabajo  extends EntidadAuditable  {

	private static final long serialVersionUID = -2509239615514438566L;
	private static final String PAQUETE_CLASE = "ec.gob.ambiente.suia.domain.CargaTrabajo.";
	public static final String LISTAR_TODO = PAQUETE_CLASE + "findAll";
	public static final String OBTENER_POR_USUARIO = PAQUETE_CLASE + "obtenerPorUsuario";
	public static final String OBTENER_TODOS = PAQUETE_CLASE + "obtenerTodos";
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@Column(name = "wolo_id")
	private Integer id;
	
	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "wolo_register_date")
	private Date fechaRegistro;

	@Getter
	@Setter
	@Column(name = "wolo_administrative_unit_id")
	private Integer unidadAdministrativaId;

	@Getter
	@Setter
	@Column(name = "pren_id")
	private Integer proyectoId;
	
	@Getter
	@Setter
	@Column(name = "wolo_priority", length = 1)
	private String prioridad;

	@Getter
	@Setter
	@Column(name = "wolo_code")
	private String codigo;

	@Getter
	@Setter
	@Column(name = "wolo_code_procedure")
	private String codigoTramite;

	@Getter
	@Setter
	@Column(name = "wolo_project_name")
	private String nombre;

	@Getter
	@Setter
	@Column(name = "wolo_number_resolution", length = 32)
	private String numeroResolucion;
	
	@Getter
	@Setter
	@Temporal(TemporalType.DATE)
	@Column(name = "wolo_resolution_date")
	private Date fechaResolucion;

	@Getter
	@Setter
	@Column( name = "wolo_sender")
	private String remitente;
	
	@Getter
	@Setter
	@Column( name = "wolo_affair")
	private String asunto;
	
	@Getter
	@Setter
	@Column(name = "wolo_status_processed")
	private String estadoTramite;

	@Getter
	@Setter
	@Column(name = "wolo_exists_code_suia")
	private boolean existeCodigoSuia;

	@Getter
	@Setter
	@Column(name = "wolo_level_national")
	private boolean nivelNacional;

	@ManyToOne
	@JoinColumn(name = "user_id")
	@ForeignKey(name = "fk_work_load_user")
	@Getter
	@Setter
	private Usuario usuario;

	@Getter
	@Setter
	@Column(name = "wolo_obligations_monitoring")
	private boolean monitoreo;

	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "wolo_obligations_monitoring_date")
	private Date fechaMonitoreo;

	@Getter
	@Setter
	@Column(name = "wolo_obligations_audit")
	private boolean auditoria;

	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "wolo_obligations_audit_date")
	private Date fechaAuditoria;

	@Getter
	@Setter
	@Column(name = "wolo_obligations_informe")
	private boolean informe;

	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "wolo_obligations_informe_date")
	private Date fechaInforme;

	@Getter
	@Setter
	@Column(name = "wolo_obligations_tdr")
	private boolean tdr;

	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "wolo_obligations_tdr_date")
	private Date fechaTdr;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "gelo_id")
	@ForeignKey(name = "fk_work_load_geographical_locations")
	private UbicacionesGeografica provincia;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "wolo_type_sector")
	@ForeignKey(name = "fk_work_load_general_catalogs_tipo_sector")
	private CatalogoGeneral tipoSector;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "wolo_service")
	@ForeignKey(name = "fk_work_load_general_catalogs_tipo_servicio")
	private CatalogoGeneral tipoServicio;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "wolo_sender_id")
	@ForeignKey(name = "fk_work_load_general_catalogs_operadora")
	private CatalogoGeneral operadora;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "wolo_block")
	@ForeignKey(name = "fk_work_load_blocks")
	private CatalogoGeneral bloque;
	
	@Override
	public String toString() {
		return nombre;
	}

	public CargaTrabajo() {
	}

	public CargaTrabajo(Integer id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof CargaTrabajo)) {
			return false;
		}
		CargaTrabajo other = (CargaTrabajo) object;
		return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
	}
}