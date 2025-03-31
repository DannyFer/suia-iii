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
 * The persistent class for the waste_generator_record_document database table.
 * 
 */
@Entity
@Table(name="waste_generator_record_document", schema="coa_waste_generator_record")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "wgrd_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "wgrd_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "wgrd_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "wgrd_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "wgrd_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wgrd_status = 'TRUE'")
@NamedQuery(name="PermisoRegistroGeneradorDesechos.findAll", query="SELECT p FROM PermisoRegistroGeneradorDesechos p")
public class PermisoRegistroGeneradorDesechos extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="wgrd_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="ware_id")
	private RegistroGeneradorDesechosRcoa registroGeneradorDesechosRcoa;
	
	@Getter
	@Setter
	@Column(name = "wgrd_document_number")
	private String numeroDocumento;
	
	@Getter
	@Setter
	@Column(name = "wgrd_date_docuement_number")
	private Date fechaCreacionDocumento;
	
	@Getter
	@Setter
	@Column(name = "wgrd_is_final")
	private Boolean esRegistroFinal;

	public PermisoRegistroGeneradorDesechos() {
	}

}