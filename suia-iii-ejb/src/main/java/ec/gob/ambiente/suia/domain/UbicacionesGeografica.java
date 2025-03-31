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
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

import javax.persistence.NamedQueries;

/**
 * The persistent class for the ubicaciones_geografica database table.
 *
 */
@Entity
@Table(name = "geographical_locations", schema = "public")
@NamedQueries({
		@NamedQuery(name = "UbicacionesGeografica.findAll", query = "SELECT u FROM UbicacionesGeografica u"),
		@NamedQuery(name = UbicacionesGeografica.FIND_PROVINCE, query = "SELECT u FROM UbicacionesGeografica u WHERE u.zona IS NOT NULL ORDER BY u.nombre"),
		@NamedQuery(name = UbicacionesGeografica.FIND_PARROQUIA, query = "SELECT u FROM UbicacionesGeografica u WHERE u.codificacionInec = :codeInec  and u.codificacionInec not in ('9001','9002','9003','9004') and u.estado=true "),
		@NamedQuery(name = UbicacionesGeografica.FIND_BY_FATHER, query = "SELECT u FROM UbicacionesGeografica u WHERE u.ubicacionesGeografica = :padre  and u.estado=true  ORDER BY u.nombre"),
		@NamedQuery(name = UbicacionesGeografica.FIND_FATHER,    query = "SELECT u FROM UbicacionesGeografica u WHERE u.ubicacionesGeografica = :hijo  and u.estado=true  ORDER BY u.nombre"),
		@NamedQuery(name = UbicacionesGeografica.FIND_AGUAS_TERRITORIALES, query = "SELECT u FROM UbicacionesGeografica u WHERE u.id IN (:identificadores) order by u.nombre "),
		@NamedQuery(name = UbicacionesGeografica.FIND_PROYECTO_NO_REGULADO, query = "SELECT u FROM ProyectoNoRegularizadoUbicacionGeografica u WHERE u.proyectoAmbientalNoRegulado.id = :idProyNoRegul"),
		@NamedQuery(name = UbicacionesGeografica.FIND_PAISES, query = "SELECT u FROM UbicacionesGeografica u WHERE u.ubicacionesGeografica is null  and u.estado=true  ORDER BY u.nombre")})
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "gelo_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "gelo_status = 'TRUE'")
public class UbicacionesGeografica extends EntidadBase {

	public static final String FIND_PROVINCE = "ec.com.magmasoft.business.domain.UbicacionesGeografica.findProvince";
	public static final String FIND_PARROQUIA = "ec.com.magmasoft.business.domain.UbicacionesGeografica.findParroquia";
	public static final String FIND_PROYECTO_NO_REGULADO = "ec.com.magmasoft.business.domain.UbicacionesGeografica.findProyectoNoRegulado";
	public static final String FIND_BY_FATHER = "ec.com.magmasoft.business.domain.UbicacionesGeografica.findByFhater";
	public static final String FIND_FATHER = "ec.com.magmasoft.business.domain.UbicacionesGeografica.findFhater";
	public static final String FIND_PAISES = "ec.com.magmasoft.business.domain.UbicacionesGeografica.findPaises";
	private static final long serialVersionUID = 5447770111996781102L;
	public static final String COSTA = "Costa";
	public static final String SIERRA = "Sierra";
	public static final String ORIENTE = "Oriente";
	public static final String INSULAR = "Insular";

	public static final String FIND_AGUAS_TERRITORIALES = "ec.com.magmasoft.business.domain.UbicacionesGeografica.findAguasTerritoriales";
	public static final Integer[] AGUAS_TERRITORIALES = { 23, 27, 15, 20, 11, 29 };

	@Id
	@SequenceGenerator(name = "GEOGRAPHICAL_LOCATIONS_GENERATOR", sequenceName = "seq_gelo_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GEOGRAPHICAL_LOCATIONS_GENERATOR")
	@Column(name = "gelo_id")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@Column(name = "gelo_codification_inec")
	private String codificacionInec;

	@Getter
	@Setter
	@Column(name = "gelo_name")
	private String nombre;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.EAGER)
	@ForeignKey(name = "fk_geografical_location_gelo_id_geografical_location_parent_id")
	@JoinColumn(name = "gelo_parent_id")
	private UbicacionesGeografica ubicacionesGeografica;

	@Getter
	@Setter
	@OneToMany(mappedBy = "ubicacionesGeografica", fetch = FetchType.LAZY)
	private List<UbicacionesGeografica> ubicacionesGeograficas;

	@Getter
	@Setter
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ubicacion")
	private List<Denuncia> denuncias;

	@Setter
	@Getter
	@ForeignKey(name = "fk_geograficall_locationgeograficall_locationzone_idzoneszone_i")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "zone_id")
	private Zona zona;

	@Setter
	@Getter
	@ForeignKey(name = "fk_geografical_location_pglo_id_projects_general_locationpglo_id")
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "pglo_id")
	private Region region;

	@Setter
	@Getter
	@Column(name = "pglo_id", insertable = false, updatable = false)
	private Integer idRegion;

	@Setter
	@Getter
	@ForeignKey(name = "fk_geograficall_locationgeograficall_areas_area_i")
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "area_id")
	private Area enteAcreditado;

	@Setter
	@Getter
	@ForeignKey(name = "fk_geograficall_locationgeograficallmunicipio_areas_area_i")
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "area_id_municipio")
	private Area enteAcreditadomunicipio;
	
	@Transient
	@Getter
	@Setter
	private Integer orden;
	
	public UbicacionesGeografica() {
	}

	public UbicacionesGeografica(Integer id) {
		this.id = id;
	}

	public UbicacionesGeografica addUbicacionesGeografica(UbicacionesGeografica ubicacionesGeografica) {
		getUbicacionesGeograficas().add(ubicacionesGeografica);
		ubicacionesGeografica.setUbicacionesGeografica(this);

		return ubicacionesGeografica;
	}

	public UbicacionesGeografica removeUbicacionesGeografica(UbicacionesGeografica ubicacionesGeografica) {
		getUbicacionesGeograficas().remove(ubicacionesGeografica);
		ubicacionesGeografica.setUbicacionesGeografica(null);

		return ubicacionesGeografica;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof UbicacionesGeografica)) {
			return false;
		}
		UbicacionesGeografica other = (UbicacionesGeografica) obj;
		if (((this.id == null) && (other.id != null)) || ((this.id != null) && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

	@Override
	public String toString() {
		return getNombre();
	}
	
	
	@Setter
	@Getter
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "area_id_zonal")
	private Area areaCoordinacionZonal;

}
