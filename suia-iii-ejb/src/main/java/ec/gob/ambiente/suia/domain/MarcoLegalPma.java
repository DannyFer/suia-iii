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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;


/**
 * The persistent class for the fapma_legal_framework database table.
 * 
 */
@NamedQueries({ @NamedQuery(name = MarcoLegalPma.GET_ALL, query = "SELECT m FROM MarcoLegalPma m") })
@Entity
@Table(name = "fapma_legal_framework", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "fale_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "fale_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "fale_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "fale_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "fale_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "fale_status = 'TRUE'")
public class MarcoLegalPma extends EntidadAuditable {
	private static final long serialVersionUID = 1L;

	public static final String GET_ALL = "ec.com.magmasoft.business.domain.MarcoLegalPma.getAll";
	public static final String SEQUENCE_CODE = "fapma_legal_framework_fale_id_seq";

	@Id
	@Column(name="fale_id")
	@SequenceGenerator(name = "LEGAL_FRAMEWORK_PMA_FALEID_GENERATOR", sequenceName = "fapma_legal_framework_fale_id_seq", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LEGAL_FRAMEWORK_PMA_FALEID_GENERATOR")
	@Getter
	@Setter
	private Integer id;

	//bi-directional many-to-one association to CatiiFapma
	@ManyToOne
	@JoinColumn(name="cafa_id")
	@ForeignKey(name = "catii_fapma_fapma_legal_framework_fk")
	@Getter
	@Setter
	private FichaAmbientalPma fichaAmbientalPma;
	
	
	
	@ManyToOne
	@JoinColumn(name="faca_id")
	@ForeignKey(name = "fapma_catalogo_article_fapma_legal_framework_fk")
	@Getter
	@Setter
	private ArticuloCatalogo articuloCatalogo;

	@Column(name="fale_regulations")
	@Getter
	@Setter
	private String normativa;
	
	@Column(name="fale_article")
	@Getter
	@Setter
	private String articulo;
}