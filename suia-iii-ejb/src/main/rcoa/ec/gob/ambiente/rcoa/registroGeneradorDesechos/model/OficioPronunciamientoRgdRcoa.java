package ec.gob.ambiente.rcoa.registroGeneradorDesechos.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the waste_generator_record_office_pronouncement database table.
 * 
 */
@Entity
@Table(name="waste_generator_record_office_pronouncement", schema="coa_waste_generator_record")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "wgro_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "wgro_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "wgro_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "wgro_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "wgro_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wgro_status = 'TRUE'")
@NamedQuery(name="OficioPronunciamientoRgdRcoa.findAll", query="SELECT o FROM OficioPronunciamientoRgdRcoa o")
public class OficioPronunciamientoRgdRcoa extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="wgro_id")
	private Integer id;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="ware_id")
	private RegistroGeneradorDesechosRcoa registroGeneradorDesechosRcoa;

	@Getter
	@Setter
	@Column(name="wgro_document_number")
	private String numeroDocumento;
	
	@Getter
	@Setter
	@Column(name = "wgro_date_document_number")
	private Date fechaCreacionDocumento;

	public OficioPronunciamientoRgdRcoa() {
	}

}