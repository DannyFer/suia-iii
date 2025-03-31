package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@NamedQueries({ @NamedQuery(name = PagoFacilitadoresAmbientales.GET_ALL, query = "SELECT m FROM PagoFacilitadoresAmbientales m") })
@Entity
@Table(name = "environmental_facilitators_taxes", schema = "suia_iii")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "enft_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "enft_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "enft_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "enft_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "enft_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "enft_status = 'TRUE'")
public class PagoFacilitadoresAmbientales extends EntidadAuditable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2184700503719001903L;
	public static final String GET_ALL = "ec.com.magmasoft.business.domain.PagoFacilitadoresAmbientales.getAll";

	@Id
	@SequenceGenerator(name = "ENVIRONMENTAL_FACILITATORS_TAXES_ENFTID_GENERATOR", sequenceName = "seq_enft_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ENVIRONMENTAL_FACILITATORS_TAXES_ENFTID_GENERATOR")
	@Column(name = "enft_id")
	@Getter
	@Setter
	private Integer id;
	//revisar ya que esta considera la ubccacion del facilitador y esta mal
	@Getter
	@Setter
	@Column(name = "enfl_facilitator_outside")
	private boolean valorPagar;
	
//	@Getter
//	@Setter
//	@Column(name = "enfl_status")
//	private boolean estado;
	
	@Getter
	@Setter
	@Column(name = "enfl_code")
	private String codigoPago;
	
	@Getter
	@Setter
	@Column(name = "gelo_codification_inec")
	private String ubicacionInec;
	//revisar ya que esta considera la ubccacion del facilitador y esta mal
	@Getter
	@Setter
	@Column(name = "enfl_facilitator_inside")
	private String pagoEspecial;
	  
}
