package ec.gob.ambiente.suia.domain;

import javax.persistence.*;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the holidays database table.
 * 
 */
@Entity
@Table(name = "holidays", schema = "public")
@NamedQuery(name="Holiday.findAll", query="SELECT h FROM Holiday h")
@NamedQueries({ @NamedQuery(name = Holiday.LISTAR_TODO, query = "SELECT h FROM Holiday h ORDER BY h.fechaInicio desc "),
	@NamedQuery(name = Holiday.LISTAR_POR_RANGO, query = "SELECT h FROM Holiday h where h.fechaInicio >= :fechaInicio and h.fechaFin <= :fechaFin ORDER BY h.fechaInicio desc "),
	@NamedQuery(name = Holiday.LISTAR_POR_RANGO_LOCALIDAD, query = "SELECT h FROM Holiday h where h.fechaInicio >= :fechaInicio and h.fechaFin <= :fechaFin and h.idLocalidad = :localidad ORDER BY h.fechaInicio desc ")})
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "holi_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "holi_date_create")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "holi_status = 'TRUE'")

public class Holiday extends EntidadBase {
	private static final long serialVersionUID = 1L;
	
	private static final String PAQUETE_CLASE = "ec.gob.ambiente.suia.domain.Holiday.";
	public static final String LISTAR_TODO = PAQUETE_CLASE + "listarTodo";
	public static final String LISTAR_POR_RANGO = PAQUETE_CLASE + "listarPorRango";
	public static final String LISTAR_POR_RANGO_LOCALIDAD = PAQUETE_CLASE + "listarPorRangoLocalidad";

	@Id
	@SequenceGenerator(name = "HOLIDAYS_GENERATOR", sequenceName="holidays_holi_id_seq", schema="public", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "HOLIDAYS_GENERATOR")
	@Column(name = "holi_id")
	@Getter
	@Setter
	private Integer id;
	
	@Getter
    @Setter
	@Column(name="holi_name")
	private String nombre;
	
	@Getter
    @Setter
	@Temporal(TemporalType.DATE)
	@Column(name="holi_start_date")
	private Date fechaInicio;

	@Getter
    @Setter
	@Temporal(TemporalType.DATE)
	@Column(name="holi_end_date")
	private Date fechaFin;

	@Getter
	@Setter
	@Column(name="holi_is_national_holiday")
	private Boolean esFeriadoNacional;
	
	@Getter
	@Setter
	@Column(name="holi_total_days")
	private Integer totalDias;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "gelo_id", insertable = false, updatable = false)
	@ForeignKey(name = "fk_gelo_id")
	private UbicacionesGeografica localidad;

	@Getter
	@Setter
	@Column(name = "gelo_id")
	private Integer idLocalidad;
	
	@Getter
    @Setter
	@Column(name="holi_description")
	private String descripcion;

	@Getter
	@Setter
	@Column(name="holi_date_update")
	private Date fechaModificacion;
	
	@Getter
	@Setter
	@Column(name="holi_date_delete")
	private Date fechaEliminacion;

	@Getter
	@Setter
	@Column(name="holi_user_create")
	private String usuarioCreacion;

	@Getter
	@Setter
	@Column(name="holi_user_update")
	private String usuarioModificacion;
	
	@Getter
    @Setter
	@Column(name="holi_user_delete")
	private String usuarioEliminacion;
	
	@Getter
    @Setter
	@Column(name="holi_justification_deletion")
	private String justificacionEliminacion;
	
	@Getter
    @Setter
    @Transient
    private Integer idProvincia;
	
	@Getter
    @Setter
    @Transient
    private Integer idCanton;
	
	@Getter
    @Setter
    @Transient
    private Boolean feriadoLocal;

}