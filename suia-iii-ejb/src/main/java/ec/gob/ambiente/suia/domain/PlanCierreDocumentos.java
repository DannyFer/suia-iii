package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * <b> Entity que representa la tabla clousure_plans_documents. </b>
 * 
 * @author carlos.pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: carlos.pupo, Fecha: 07/01/2015]
 *          </p>
 */
@Entity
@Table(name = "clousure_plans_documents", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "clpd_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "clpd_status = 'TRUE'")
public class PlanCierreDocumentos extends EntidadBase {

	private static final long serialVersionUID = -1282940751128789397L;

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name="CLOUSURE_PLANS_DOCUMENTS_CLPDID_GENERATOR" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CLOUSURE_PLANS_DOCUMENTS_CLPDID_GENERATOR")
	@Column(name = "clpd_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "clpd_section_type")
	private String tipoSeccion;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "clpl_id")
	@ForeignKey(name="fk_clousure_plansclpl_id_documentsdocuid")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "clpl_status = 'TRUE'")
	private PlanCierre planCierre;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "docu_id")
	@ForeignKey(name="fk_clousure_plans_documentsclpd_id_documentsdocuid")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	private Documento documento;

}
