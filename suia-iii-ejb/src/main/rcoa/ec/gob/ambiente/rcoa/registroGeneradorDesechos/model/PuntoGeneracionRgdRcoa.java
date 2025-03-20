package ec.gob.ambiente.rcoa.registroGeneradorDesechos.model;

import javax.persistence.*;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the generation_points_coa database table.
 * 
 */
@Entity
@Table(name="generation_points_coa", schema="coa_waste_generator_record")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "gepo_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "gepo_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "gepo_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "gepo_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "gepo_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "gepo_status = 'TRUE'")
@NamedQuery(name="PuntoGeneracionRgdRcoa.findAll", query="SELECT p FROM PuntoGeneracionRgdRcoa p")
public class PuntoGeneracionRgdRcoa extends EntidadAuditable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="gepo_id")
	private Integer id;	

	@Getter
	@Setter
	@Column(name="gepo_key")
	private String clave;

	@Getter
	@Setter
	@Column(name="gepo_name")
	private String nombre;
	
	public PuntoGeneracionRgdRcoa() {
	}

}