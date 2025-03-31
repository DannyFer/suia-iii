package ec.gob.ambiente.rcoa.registroGeneradorDesechos.model;

import javax.persistence.*;

import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.interfaces.CoordinatesContainerRgd;
import ec.gob.ambiente.suia.domain.TipoForma;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the recovery_point_shapes_coa database table.
 * 
 */
@Entity
@Table(name="recovery_point_shapes_coa", schema="coa_waste_generator_record")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "rpsh_status")) })
@NamedQuery(name="FormaPuntoRecuperacionRgdRcoa.findAll", query="SELECT f FROM FormaPuntoRecuperacionRgdRcoa f")
public class FormaPuntoRecuperacionRgdRcoa extends EntidadBase implements CoordinatesContainerRgd {
	
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="rpsh_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="rpsh_creation_date")
	private Date fechaCreacion;

	@Getter
	@Setter
	@Column(name="rpsh_creator_user")
	private String usuarioCreacion;

	@Getter
	@Setter
	@Column(name="rpsh_date_update")
	private Date fechaModificacion;

	@Getter
	@Setter
	@Column(name="rpsh_user_update")
	private String usuarioModificacion;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "shty_id")
	private TipoForma tipoForma;

	//bi-directional many-to-one association to CoordenadaRgdCoa
	@Getter
	@Setter
	@OneToMany(mappedBy="formasPuntoRecuperacionRgdRcoa")
	private List<CoordenadaRgdCoa> coordenadas;

	//bi-directional many-to-one association to PuntoRecuperacionRgdRcoa
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="repo_id")
	private PuntoRecuperacionRgdRcoa puntoRecuperacionRgdRcoa;

		

}