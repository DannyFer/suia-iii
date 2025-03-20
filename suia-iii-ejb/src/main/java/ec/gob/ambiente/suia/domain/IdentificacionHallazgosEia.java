/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain;

import java.io.Serializable;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * @author Oscar Campana
 */
@Entity
@Table(name = "findings_identification", schema = "suia_iii")
@NamedQueries({ @NamedQuery(name = IdentificacionHallazgosEia.LISTAR_POR_EIA, query = "SELECT a FROM IdentificacionHallazgosEia a WHERE a.estado = true AND a.eistId.id = :idEstudioImpactoAmbiental  and a.idHistorico = null"),
	@NamedQuery(name = IdentificacionHallazgosEia.LISTAR_TODOS_POR_EIA, query = "SELECT a FROM IdentificacionHallazgosEia a WHERE a.estado = true AND a.eistId.id = :idEstudioImpactoAmbiental")})
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "fiid_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "fiid_status = 'TRUE'")
public class IdentificacionHallazgosEia extends EntidadBase implements Serializable, Cloneable {

	private static final long serialVersionUID = -8848052343710885892L;

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR_POR_EIA = PAQUETE + "IdentificacionHallazgosEia.listarPorEIA";
	public static final String LISTAR_TODOS_POR_EIA = PAQUETE + "IdentificacionHallazgosEia.listarTodosPorEIA";

	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "IDENTIFICACION_HALLAZGOS_ID_GENERATOR", initialValue = 1, sequenceName = "seq_fiid_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "IDENTIFICACION_HALLAZGOS_ID_GENERATOR")
	@Column(name = "fiid_id")
	private Integer id;

	@Getter
	@Setter
	@JoinColumn(name = "eist_id", referencedColumnName = "eist_id")
	@ForeignKey(name = "fk_findings_identification_eist_id_environmental_impact_studies_eist_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private EstudioImpactoAmbiental eistId;

	@Getter
	@Setter
	@Column(name = "fiid_regulation")
	private String normativa;

	@Getter
	@Setter
	@Column(name = "fiid_article")
	private String articulo;

	@Getter
	@Setter
	@Column(name = "fiid_conformity")
	private String Conformidad;

	@Getter
	@Setter
	@Column(name = "fiid_evidence", length = 1000)
	private String evidencia;

	@Getter
	@Setter
	@Column(name = "fiid_document")
	private String documento;

	/*
	 * @Getter
	 * 
	 * @Setter
	 * 
	 * @OneToMany(mappedBy = "hallazgos", fetch = FetchType.LAZY) private
	 * List<PlanHallazgosEia> planHallazgos;
	 */

	public IdentificacionHallazgosEia() {

	}

	public IdentificacionHallazgosEia(Integer id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof IdentificacionHallazgosEia)) {
			return false;
		}
		IdentificacionHallazgosEia other = (IdentificacionHallazgosEia) obj;
		if (((this.id == null) && (other.id != null)) || ((this.id != null) && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return getEvidencia();
	}
	
	@Getter
	@Setter
	@Column(name = "fiid_historical_id")
	private Integer idHistorico;
	
	@Getter
	@Setter
	@Column(name = "fiid_notification_number")
	private Integer numeroNotificacion;
	
	@Getter
	@Setter
	@Transient
	private Boolean nuevoEnModificacion;
	
	@Getter
	@Setter
	@Transient
	private Boolean registroModificado;
	
	@Override
	public IdentificacionHallazgosEia clone() throws CloneNotSupportedException {
		
		IdentificacionHallazgosEia clone = (IdentificacionHallazgosEia) super.clone();
		clone.setId(null);
		return clone;
	}
	
	//Cris F: aumento de campo de fecha de creación para el historial  fiid_date_create
	@Getter
	@Setter
	@Column(name = "fiid_date_create")
	private Date fechaCreacion;
}
