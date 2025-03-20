package ec.gob.ambiente.suia.domain;

import java.util.Date;
import java.util.List;

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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * @author carlos.pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: carlos.pupo, Fecha: 18/02/2015]
 *          </p>
 */


@NamedQueries({
	@NamedQuery(name = ConcesionMinera.OBTENER_CONSECION_MINERA, 
			query = "select i from ConcesionMinera i where i.proyectoLicenciamientoAmbiental = :p_proyecto ")})

@Entity
@Table(name = "mining_grants", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "migr_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "migr_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "migr_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "migr_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "migr_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "migr_status = 'TRUE'")
public class ConcesionMinera extends EntidadAuditable {

	private static final long serialVersionUID = 8870061076971299674L;

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String OBTENER_CONSECION_MINERA = PAQUETE + "ConcesionMinera.ObtenerConsecionMinera";
	
	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "MINING_GRANTS_MIGR_ID_GENERATOR", sequenceName = "seq_migr_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MINING_GRANTS_MIGR_ID_GENERATOR")
	@Column(name = "migr_id")
	private Integer id;

	@Column(name = "migr_name")
	@Getter
	@Setter
	private String nombre;

	@Getter
	@Setter
	@Column(name = "migr_code")
	private String codigo;

	@Getter
	@Setter
	@Column(name = "migr_area")
	private double area;
	
	@Getter
	@Setter
	@Column(name = "migr_altitude")
	private int altitud;

	@Getter
	@Setter
	@Column(name = "migr_unit")
	private String unidad;

	@Getter
	@Setter
	@Column(name = "migr_address")
	private String direccion;
	
	@Getter
	@Setter
	@Column(name = "migr_phase")
	private String fase;

	@Getter
	@Setter
	@Column(name = "migr_material")
	private String material;

	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "migr_date_inscription")
	private Date fechaInscripcion;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "pren_id")
	@ForeignKey(name = "fk_projects_blocksmining_grantsmigr_id_projectspren_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "pren_status = 'TRUE'")
	private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental;

	@Getter
	@Setter
	@OneToMany(mappedBy = "concesionMinera")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "mglo_status = 'TRUE'")
	private List<ConcesionMineraUbicacionGeografica> concesionesUbicacionesGeograficas;
}
