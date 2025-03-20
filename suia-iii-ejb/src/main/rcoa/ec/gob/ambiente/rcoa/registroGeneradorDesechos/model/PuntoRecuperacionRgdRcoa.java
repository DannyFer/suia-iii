package ec.gob.ambiente.rcoa.registroGeneradorDesechos.model;

import javax.persistence.*;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

import java.util.List;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the recovery_points_coa database table.
 * 
 */
@Entity
@Table(name="recovery_points_coa", schema="coa_waste_generator_record")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "repo_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "repo_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "repo_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "repo_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "repo_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "repo_status = 'TRUE'")
@NamedQuery(name="PuntoRecuperacionRgdRcoa.findAll", query="SELECT p FROM PuntoRecuperacionRgdRcoa p")
public class PuntoRecuperacionRgdRcoa extends EntidadAuditable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="repo_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne	
	@JoinColumn(name = "gelo_id")
	@ForeignKey(name = "fk_repo_id_geografical_locationsgelo_id")
	private UbicacionesGeografica ubicacionesGeografica;

	@Getter
	@Setter
	@Column(name="repo_address")
	private String direccion;

	@Getter
	@Setter
	@Column(name="repo_email")
	private String correo;

	@Getter
	@Setter
	@Column(name="repo_name")
	private String nombre;

	@Getter
	@Setter
	@Column(name="repo_phone")
	private String telefono;

	@Getter
	@Setter
	@Column(name="repo_generation_points_other")
	private String generacionOtros;

	@Getter
	@Setter
	@ManyToOne	
	@JoinColumn(name = "gepo_id")
	@ForeignKey(name = "gepo_id")
	private PuntoGeneracionRgdRcoa puntoGeneracion;
	
	@Transient
	@Getter
	@Setter
	private String coordenadasIngresadas;
	
	@Transient
	@Getter
	@Setter
	private String nombresGeneracion;
	
	@Transient
	@Getter
	@Setter
	private boolean utilizado=false;

	@Getter
	@Setter
	@Transient
	private List<UbicacionesGeografica> listaUbicacion;

	//bi-directional many-to-one association to FormaPuntoRecuperacionRgdRcoa
	@Getter
	@Setter
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "puntoRecuperacionRgdRcoa")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "rpsh_status = 'TRUE'")
	private List<FormaPuntoRecuperacionRgdRcoa> formasPuntoRecuperacionRgdRcoa;
	
	
	//bi-directional many-to-one association to RegistroGeneradorDesechosRcoa
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="ware_id")
	private RegistroGeneradorDesechosRcoa registroGeneradorDesechosRcoa;

	public PuntoRecuperacionRgdRcoa() {
	}
	

}
