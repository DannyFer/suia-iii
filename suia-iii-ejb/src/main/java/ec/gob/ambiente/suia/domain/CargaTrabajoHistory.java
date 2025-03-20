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
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "wlhi_status"))
})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wlhi_status = 'TRUE'")
public class CargaTrabajoHistory extends EntidadBase {

	private static final long serialVersionUID = -2509239615514438566L;
	private static final String PAQUETE_CLASE = "ec.gob.ambiente.suia.domain.CargaTrabajoHistory.";
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@Column(name = "wlhi_id")
	private Integer id;
	
	@Getter
	@Setter
	@Temporal(TemporalType.DATE)
	@Column(name = "wlhi_register_date")
	private Date fechaRegistro;

	@Getter
	@Setter
	@Column(name = "wlhi_administrative_unit_id")
	private Integer unidadAdministrativaId;
	

	@Getter
	@Setter
	@Column(name = "pren_id")
	private Integer proyectoId;
	
	@Getter
	@Setter
	@Column(name = "wlhi_priority", length = 1)
	private String prioridad;

	@Getter
	@Setter
	@Column(name = "wlhi_code")
	private String codigo;

	
	@Getter
	@Setter
	@Column(name = "wlhi_project_name")
	private String nombre;

	@Getter
	@Setter
	@Column(name = "wlhi_number_resolution", length = 32)
	private String numeroResolucion;
	
	@Getter
	@Setter
	@Temporal(TemporalType.DATE)
	@Column(name = "wlhi_resolution_date")
	private Date fechaResolucion;

	@Getter
	@Setter
	@Column( name = "wlhi_sender")
	private String remitente;
	
	@Getter
	@Setter
	@Column( name = "wlhi_affair")
	private String asunto;
	
	@Getter
	@Setter
	@Column(name = "wlhi_status_processed")
	private String estadoTramite;

	@Getter
	@Setter
	@Column(name = "wlhi_input_document")
	private String documentoEntrada;

	@Getter
	@Setter
	@Temporal(TemporalType.DATE)
	@Column(name = "wlhi_input_date")
	private Date fechaEntrada;
	
	@Getter
	@Setter
	@Column(name = "wlhi_output_document")
	private String documentoSalida;
	
	@Getter
	@Setter
	@Temporal(TemporalType.DATE)
	@Column(name = "wlhi_output_date")
	private Date fechaSalida;

	@Getter
	@Setter
	@Column(name = "wlhi_pronouncement")
	private String pronunciamiento;

	@Getter
	@Setter
	@Column(name = "wlhi_term")
	private Integer plazo;

	@Getter
	@Setter
	@Column(name = "wlhi_exists_code_suia")
	private boolean existeCodigoSuia;

	@Getter
	@Setter
	@Column(name = "wlhi_complaints")
	private Integer quejas;

	@Getter
	@Setter
	@Column(name = "wlhi_observation")
	private String observacion;

	@Getter
	@Setter
	@Column(name = "user_id")
	private Integer usuario;

	@Getter
	@Setter
	@Column(name = "user_update")
	private String usuarioActualiza;

	@Getter
	@Setter
	@Column(name = "user_update_date")
	private Date fechaActualiza;

	@Getter
	@Setter
	@Column(name = "gelo_id")
	private Integer provincia;

	@Getter
	@Setter
	@Column(name = "wlhi_type_procedure")
	private Integer tipoTramite;

	@Getter
	@Setter
	@Column(name = "wlhi_type_sector")
	private Integer tipoSector;
	
	@Getter
	@Setter
	@Column( name = "wlhi_block")
	private Integer bloque;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "wolo_id")
	@ForeignKey(name = "fk_work_load_history_work_load")
	private CargaTrabajo cargaTrabajo;
	
	@Override
	public String toString() {
		return nombre;
	}

	public CargaTrabajoHistory() {
	}

	public CargaTrabajoHistory(Integer id) {
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
		if (!(object instanceof CargaTrabajoHistory)) {
			return false;
		}
		CargaTrabajoHistory other = (CargaTrabajoHistory) object;
		return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
	}
}