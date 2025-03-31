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
@Table(name = "work_load_revision", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "wlre_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "user_creation_date")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "user_update_date")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "user_creator")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "user_update"))  })
@NamedQueries({ @NamedQuery(name = CargaTrabajoRevision.LISTAR_POR_CARGATRABAJO, query = "SELECT r FROM CargaTrabajoRevision r WHERE r.cargaTrabajo.id =:cargaTrabajoId  ORDER BY  r.id "),
	 })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wlre_status = 'TRUE'")
public class CargaTrabajoRevision  extends EntidadAuditable  {

	private static final long serialVersionUID = 1L;
	private static final String PAQUETE_CLASE = "ec.gob.ambiente.suia.domain.CargaTrabajoTrabajo.";
	public static final String LISTAR_POR_CARGATRABAJO = PAQUETE_CLASE + "obtenerPorCargaTrabajo";
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@Column(name = "wlre_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "wlre_status_processed")
	private String estadoRevision;

	@Getter
	@Setter
	@Column(name = "wlre_input_document")
	private String documentoEntrada;

	@Getter
	@Setter
	@Temporal(TemporalType.DATE)
	@Column(name = "wlre_input_date")
	private Date fechaEntrada;
	
	@Getter
	@Setter
	@Column(name = "wlre_output_document")
	private String documentoSalida;
	
	@Getter
	@Setter
	@Temporal(TemporalType.DATE)
	@Column(name = "wlre_output_date")
	private Date fechaSalida;

	@Getter
	@Setter
	@Column(name = "wlre_pronouncement")
	private String pronunciamiento;

	@Getter
	@Setter
	@Column(name = "wlre_term")
	private Integer plazo;

	@Getter
	@Setter
	@Column(name = "wlre_complaints")
	private Integer quejas;

	@Getter
	@Setter
	@Column(name = "wlre_goal_year")
	private Integer anioMeta;

	@Getter
	@Setter
	@Column(name = "wlre_observation")
	private String observacion;

	@Getter
	@Setter
	@Column(name = "wlre_number_trained")
	private Integer capacitaciones;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "wlre_type_procedure")
	@ForeignKey(name = "fk_work_load_revision_general_catalogs_tipo_tramite")
	private CatalogoGeneral tipoTramite;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "wlre_goal")
	@ForeignKey(name = "fk_work_load_revision_general_catalogs_mes")
	private CatalogoGeneral meta;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "wolo_id")
	@ForeignKey(name = "fk_work_load_work_load_revision")
	private CargaTrabajo cargaTrabajo;
	
	public CargaTrabajoRevision() {
	}

	public CargaTrabajoRevision(Integer id) {
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
		if (!(object instanceof CargaTrabajoRevision)) {
			return false;
		}
		CargaTrabajoRevision other = (CargaTrabajoRevision) object;
		return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
	}
}