package ec.gob.ambiente.suia.domain;

import java.util.List;

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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * <b> Entity que representa la tabla areas. </b>
 * 
 * @author pganan
 * @version Revision: 1.0
 *          <p>
 *          [Autor: pganan, Fecha: 22/12/2014]
 *          </p>
 */
@Entity
@Table(name = "areas", schema = "public")
@NamedQueries({
		@NamedQuery(name = Area.LISTAR_PADRES, query = "SELECT a FROM Area a WHERE a.area IS NULL AND a.estado = true ORDER BY a.areaName"),
		@NamedQuery(name = Area.LISTAR_HIJOS, query = "SELECT a FROM Area a WHERE a.area = :area AND a.estado = true and a.tipoArea.id != 7 ORDER BY a.areaName") })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "area_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "area_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "area_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "area_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "area_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "area_status = 'TRUE'")
public class Area extends EntidadAuditable {

	private static final long serialVersionUID = -1756612644167548934L;

	private static final String PAQUETE_CLASE = "ec.gob.ambiente.suia.domain.Area.";
	public static final String LISTAR_PADRES = PAQUETE_CLASE + "listarPadres";
	public static final String LISTAR_HIJOS = PAQUETE_CLASE + "listarHijos";

	public static final int UNIDAD_DE_PRODUCTOS_DESECHOS_PELIGROSOS_Y_NO_PELIGROSOS_SUB_DNCA = 370;
	public static final int PNGIDS = 134;
    public static final int DIRECCION_NACIONAL_PREVENCION_CONTAMINACION_AMBIENTAL = 257;

	@Getter
	@Setter
	@Column(name = "area_id")
	@Id
	@SequenceGenerator(name = "AREAS_GENERATOR", initialValue = 1, sequenceName = "seq_area_id", schema = "public")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AREAS_GENERATOR")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "area_abbreviation")
	private String areaAbbreviation;

	@Getter
	@Setter
	@Column(name = "area_email")
	private String areaEmail;

	@Getter
	@Setter
	@Column(name = "area_name")
	private String areaName;

	@Getter
	@Setter
	@Column(name = "area_ente_identification")
	private String identificacionEnte;

	@Getter
	@Setter
	@JoinColumn(name = "gelo_id", referencedColumnName = "gelo_id")
	@ManyToOne
	@ForeignKey(name = "fk_areas_gelo_id_geografic_location_gelo_id")
	private UbicacionesGeografica ubicacionesGeografica;

	@Getter
	@Setter
	@JoinColumn(name = "arty_id", referencedColumnName = "arty_id")
	@ManyToOne
	@ForeignKey(name = "fk_areas_arty_id_areas_types_arty_id")
	private TipoArea tipoArea;

	@Getter
	@Setter
	@OneToMany(mappedBy = "area", fetch = FetchType.LAZY)
	private List<Area> areaList;

	@Getter
	@Setter
	@JoinColumn(name = "area_parent_id", referencedColumnName = "area_id")
	@ManyToOne //(fetch = FetchType.LAZY)
	@ForeignKey(name = "fk_areasarea_idareasarea_id")
	private Area area;

	@Getter
	@Setter
	@OneToMany(mappedBy = "areaResponsable", fetch = FetchType.LAZY)
	private List<AprobacionRequisitosTecnicos> requisitosTecnicos;

	@Getter
	@Setter
	@Column(name = "entity_type")
	private String tipoEnteAcreditado;
	
	@Getter
	@Setter
	@Column(name = "area_locked")
	private Boolean bloqueado;
	
	
	@Getter
	@Setter
	@Column(name = "area_habilitar")
	private Boolean habilitarArea;

	@Getter
	@Setter
	@Column(name = "area_issue")
	private Boolean areaEmisora;

	@Getter
	@Setter
	@Column(name = "area_tracing")
	private Boolean areaSeguimiento;
	
	@Getter
	@Setter
	@Column(name = "area_resolution_ministerial")
	private String resolucionMinisterial;
	
	@Getter
	@Setter
	@Column(name = "area_id_dp")
	private String idDireccionesProvinciales;

	@Getter
	@Setter
	@Column(name = "area_campus")
	private String areaSedeZonal;
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Area)) {
			return false;
		}
		Area other = (Area) obj;
		if (((this.id == null) && (other.id != null)) || ((this.id != null) && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return getAreaName();
	}
	
	@Getter
	@Setter
	@Transient
	private Integer idUnidadArea;
	
	@Getter
	@Setter
	@Transient
	private String padre;
	
	@Getter
	@Setter
	@Transient
	private List<Area> listaUnidades;
	
	@Getter
	@Setter
	@Transient
	private List<UnidadArea> listaUnidadesGuardadas;

	public Area(Integer id) {
		super();
		this.id = id;
	}

	public Area() {
		super();
	}
	
	
	

}
