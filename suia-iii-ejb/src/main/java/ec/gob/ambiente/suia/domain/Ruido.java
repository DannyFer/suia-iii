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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name = "noises", schema = "suia_iii")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "nois_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "nois_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "nois_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "nois_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "nois_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "nois_status = 'TRUE'")
@NamedQueries({ @NamedQuery(name = Ruido.LISTAR_POR_ID_EIA, query = "SELECT r FROM Ruido r WHERE r.estado=true AND r.eiaId = :eiaId order by r.id") })
public class Ruido extends EntidadAuditable {

	private static final long serialVersionUID = 2666439622636103167L;
	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR_POR_ID_EIA = PAQUETE
			+ "Ruido.listarPorIdEia";
	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "NOISES_GENERATOR", initialValue = 1, schema = "suia_iii", sequenceName = "seq_nois_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "NOISES_GENERATOR")
	@Column(name = "nois_id", unique = true, nullable = false)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "nois_code")
	private String codigo;

	@Getter
	@Setter
	@Temporal(TemporalType.DATE)
	@Column(name = "nois_monitoring_date")
	private Date fechaMonitoreo;

	@Getter
	@Setter
	@Column(name = "nois_schedule")
	private String horario;

	@Getter
	@Setter
	@Column(name = "nois_description")
	private String descripcion;
	@Getter
	@Setter
	@Column(name = "nois_limit")
	private Double limite;
	@Getter
	@Setter
	@Column(name = "nois_result")
	private Double resultado;
	@Getter
	@Setter
	@Column(name = "nois_laboratory")
	private Integer idLaboratorio;
	@Getter
	@Setter
	@Column(name="eia_id")
	private Integer eiaId;
	
	@Transient
	@Getter
	@Setter
	private boolean editar;

	@Transient
	@Getter
	@Setter
	private CoordenadaGeneral coordenadaGeneral;
	@Transient
	@Getter
	@Setter
	private CatalogoGeneral laboratorio;
	@Transient
	@Getter
	@Setter
	private String color;

	@OneToOne
	@JoinColumn(name = "geca_id")
	@ForeignKey(name = "fk_noisesgeca_id_general_catalogs_geca_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "geca_status = 'TRUE'")
	@Getter
	@Setter
	private CatalogoGeneral catalogoGeneral;

}
