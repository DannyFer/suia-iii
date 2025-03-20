/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

/**
 * 
 * @author christian
 */
@Entity
@Table(name = "prevented", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "prev_status")) })
@NamedQueries({
		@NamedQuery(name = Impedido.LISTAR_TODO, query = "SELECT i FROM Impedido i ORDER BY i.nombre"),
		@NamedQuery(name = Impedido.LISTAR_FECHA_NOT_NULL, query = "SELECT i FROM Impedido i WHERE i.fechaInactivo IS NOT NULL ORDER BY i.nombre"),
		@NamedQuery(name = Impedido.LISTAR_NUMERO_DOCUMENTO_TIPO_IMPEDIMENTO_ACTIVO, query = "SELECT i.causa FROM Impedido i, TipoImpedimento t WHERE i.estado = true AND i.idTipoImpedimento = t.id AND i.numeroDocumento = :numeroDocumento AND t.nombre IN :tipoImpedimento") })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prev_status = 'TRUE'")
public class Impedido extends EntidadBase {

	private static final long serialVersionUID = -4205802445684379819L;

	private static final String PAQUETE_CLASE = "ec.gob.ambiente.suia.domain.Impedido.";
	public static final String LISTAR_TODO = PAQUETE_CLASE + "findAll";
	public static final String LISTAR_FECHA_NOT_NULL = PAQUETE_CLASE + "listarFechaNotNull";
	public static final String LISTAR_NUMERO_DOCUMENTO_TIPO_IMPEDIMENTO_ACTIVO = PAQUETE_CLASE
			+ "listarNumeroDocumentoTipoImpedimentoActivo";

	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "IMPEDIDO_ID_GENERATOR", initialValue = 1, sequenceName = "seq_prev_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "IMPEDIDO_ID_GENERATOR")
	@Column(name = "prev_id", nullable = false)
	private Integer id;
	@Getter
	@Setter
	@Column(name = "prev_pin", nullable = false, length = 20)
	private String numeroDocumento;
	@Getter
	@Setter
	@Column(name = "prev_name", nullable = false, length = 100)
	private String nombre;
	@Getter
	@Setter
	@Column(name = "prev_cause", nullable = false, length = 200)
	private String causa;
	@Getter
	@Setter
	@Column(name = "prev_date")
	@Temporal(TemporalType.DATE)
	private Date fechaInactivo;
	@Getter
	@Setter
	@JoinColumn(name = "prty_id", referencedColumnName = "prty_id", nullable = false)
	@ForeignKey(name = "fk_prevented_prty_id_prevented_type_prty_id")
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private TipoImpedimento prtyId;
	@Column(name = "prty_id", insertable = false, updatable = false)
	@Getter
	@Setter
	private Integer idTipoImpedimento;

	public Impedido() {
	}

	public Impedido(Integer id) {
		this.id = id;
	}
}
