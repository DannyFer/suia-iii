package ec.gob.ambiente.rcoa.registroGeneradorDesechos.model;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

import lombok.Getter;
import lombok.Setter;

/**
 * The persistent class for the waste_policies_coa_waste_dangerous database table.
 * 
 */
@Entity
@Table(name="waste_policies_coa_waste_dangerous", schema="coa_waste_generator_record")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "wawa_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "wawa_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "wawa_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "wawa_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "wawa_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wawa_status = 'TRUE'")
@NamedQuery(name="PoliticaRcoaDesechoaPeligroso.findAll", query="SELECT p FROM PoliticaRcoaDesechoaPeligroso p")
public class PoliticaRcoaDesechoaPeligroso implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="wawa_id")
	private Integer id;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "wada_id")
	private DesechoPeligroso desechoPeligroso;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "wapo_id")	
	private PoliticaDesechoRgdRcoa politicaDesechoRgdRcoa;

	public PoliticaRcoaDesechoaPeligroso() {
	}

}