package ec.gob.ambiente.rcoa.registroGeneradorDesechos.model;

import javax.persistence.*;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the waste_policies_coa database table.
 * 
 */
@Entity
@Table(name="waste_policies_coa", schema="coa_waste_generator_record")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "wapo_status")) })
@NamedQuery(name="PoliticaDesechoRgdRcoa.findAll", query="SELECT p FROM PoliticaDesechoRgdRcoa p")
public class PoliticaDesechoRgdRcoa extends EntidadBase {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="wapo_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="wapo_creation_date")
	private Date fechaCreacion;

	@Getter
	@Setter
	@Column(name="wapo_creator_user")
	private String usuarioCreacion;

	@Getter
	@Setter
	@Column(name="wapo_date_update")
	private Date fechaModificacion;

	@Getter
	@Setter
	@Column(name="wapo_name")
	private String nombre;

	@Getter
	@Setter
	@Column(name="wapo_user_update")
	private String usuarioModificacion;

	//bi-directional many-to-one association to RegistroGeneradorDesechosRcoa
	@Getter
	@Setter
	@OneToMany(mappedBy="politicaDesechoRgdRcoa")
	private List<RegistroGeneradorDesechosRcoa> registroGeneradorDesechosRcoa;

	public PoliticaDesechoRgdRcoa() {
	}
	
	@Getter
	@Setter
	@Column(name="wapo_order")
	private Integer orden;
}