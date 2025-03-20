package ec.gob.ambiente.rcoa.bypassParticipacionCiudadana.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="reports_bypass", schema = "coa_bypass_ppc")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "reby_status")),	
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "reby_creator_user")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "reby_creation_date")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "reby_user_update")), 
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "reby_date_update"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "reby_status = 'TRUE'")
public class ReporteBPC extends EntidadAuditable implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@Setter
	@Getter
	@Column(name = "reby_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;	
	
	@Setter
	@Getter
	@ManyToOne
	@JoinColumn(name = "expe_id")
	private ExpedienteBPC expedienteBPC;
	
	@Setter
	@Getter
	@Column(name = "reby_code")
	private String codigoReporte;
	
	@Setter
	@Getter
	@Column(name = "reby_type")
	private Integer tipoDocumento;
	
	@Setter
	@Getter
	@Column(name="reby_affair")
	private String asunto;
	
	@Setter
	@Getter
	@Column(name="reby_background")
	private String antecedente;
	
	@Setter
	@Getter
	@Column(name="reby_conclusion")
	private String pronunciamiento;
	
	
	@Setter
	@Getter
	@Transient
	private byte[] contenidoDocumento;
	
}