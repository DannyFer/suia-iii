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

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name = "catalog_document", schema = "alert_notification")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "cado_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "cado_status = 'TRUE'")
public class CatalogoDocumento extends EntidadBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name = "DOCUMENTS_CATALOG_GENERATOR", sequenceName="catalog_document_cado_id_seq ", schema="alert_notification", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DOCUMENTS_CATALOG_GENERATOR")
	@Column(name = "cado_id")
	@Getter
	@Setter
	private Integer id;
	
	@Column(name = "cado_description")
	@Getter
	@Setter
	private String descripcion;
		
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "lity_id")
	@ForeignKey(name = "lity_id")
	private TipoLicenciamiento tipoLicenciamiento;
	
	public CatalogoDocumento() {
	}

	public CatalogoDocumento(Integer id) {
		this.id = id;
	}

}
