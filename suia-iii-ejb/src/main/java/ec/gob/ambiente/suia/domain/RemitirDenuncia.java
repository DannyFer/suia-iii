package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name = "refer_complaints", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "reco_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "reco_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "reco_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "reco_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "reco_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "reco_status = 'TRUE'")
public class RemitirDenuncia extends EntidadAuditable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Setter
	@Getter
	@SequenceGenerator(name = "REFER_COMPLAINTS_GENERATOR", sequenceName = "seq_recom_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REFER_COMPLAINTS_GENERATOR")
	@Column(name = "reco_id")
	private Integer id;

	@Setter
	@Getter
	@Column(name = "reco_central_plant")
	private boolean plantaCentral;

	@Setter
	@Getter
	@ForeignKey(name = "fk_refer_refer_complaintsrecom_id_areasarea_id")
	@ManyToOne
	@JoinColumn(name = "dipr_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "reco_status = 'TRUE'")
	private Area responsable;

	@Setter
	@Getter
	@Column(name = "reco_sector")
	private String sector;

}
