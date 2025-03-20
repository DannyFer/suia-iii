package ec.gob.ambiente.suia.domain;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the fapma_activities database table.
 * 
 */
@NamedQueries({ @NamedQuery(name = MatrizAmbientalMineria.LISTAR_POR_FICHA, query = "SELECT a FROM MatrizAmbientalMineria a WHERE a.idFichaAmbiental = :idFicha AND a.estado = true") })
@Entity
@Table(name = "mining_environmental_matrix", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "miem_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "miem_status = 'TRUE'")
public class MatrizAmbientalMineria extends EntidadBase {

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR_POR_FICHA = PAQUETE + "MatrizAmbientalMineria.listarPorFicha";
	// public static final String SEQUENCE_CODE =
	// "fapma_activities_caac_id_seq";

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "miem_id")
	@SequenceGenerator(name = "MINING_ENVIROMENTAL_MATRIX_GENERATOR", sequenceName = "miem_id_seq", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MINING_ENVIROMENTAL_MATRIX_GENERATOR")
	@Getter
	@Setter
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "acmi_id")
	@ForeignKey(name = "fk_mining_environmental_matrix_acmi_id_activity_mining_acmi_id")
	@Getter
	@Setter
	private ActividadMinera actividadMinera;

	@ManyToOne
	@JoinColumn(name = "mien_id")
	@ForeignKey(name = "fk_mining_environmental_matrix_mien_id_mining_enviromental_record_mien_id")
	@Getter
	@Setter
	private FichaAmbientalMineria fichaAmbientalMineria;

	@Getter
	@Setter
	@Column(name = "mien_id", insertable = false, updatable = false)
	private Integer idFichaAmbiental;

	@Getter
	@Setter
	@Column(name = "acmi_id", insertable = false, updatable = false)
	private Integer idActividadMineria;

	public MatrizAmbientalMineria() {
	}

	public MatrizAmbientalMineria(Integer id) {
		this.id = id;
	}
}
