package ec.gob.ambiente.rcoa.registroGeneradorDesechos.model;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the generation_points_waste database table.
 * 
 */
@Entity
@Table(name="generation_points_waste", schema="coa_waste_generator_record")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "gewa_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "gewa_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "gewa_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "gewa_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "gewa_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "gewa_status = 'TRUE'")
@NamedQuery(name="PuntoGeneracionDesecho.findAll", query="SELECT p FROM PuntoGeneracionDesecho p")
public class PuntoGeneracionDesecho extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="gewa_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="wwgp_id")
	private DesechosRegistroGeneradorRcoa desechoRegistroGeneradorRcoa;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "gepo_id")
	private PuntoGeneracionRgdRcoa puntoGeneracionRgdRcoa;
	

	public PuntoGeneracionDesecho() {
	}

}