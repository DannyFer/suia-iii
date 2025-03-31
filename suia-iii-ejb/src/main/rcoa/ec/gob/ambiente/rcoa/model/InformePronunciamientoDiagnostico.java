package ec.gob.ambiente.rcoa.model;

import java.util.Date;

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
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "approval_observation_reports", schema = "coa_mae")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "aore_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "aore_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "aore_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "aore_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "aore_user_update")) 
})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "aore_status = 'TRUE'")

public class InformePronunciamientoDiagnostico extends EntidadAuditable{
	
	private static final long serialVersionUID = 1801511545851715084L;

	@Getter
	@Setter
	@Id
	@Column(name = "aore_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "prco_id")
	@ForeignKey(name = "fk_prco_id")
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
	@Setter
	@Column(name="aore_report_code")
	private String codigo;
	
	@Setter
	@Getter
	@Column(name="aore_report_date")
	private Date fechaElaboracion;
	
	@Getter
	@Setter
	@Column(name="aore_affair")
	private String asunto;
	
	@Getter
	@Setter
	@Column(name="aore_pronouncement")
	private String pronunciamiento;
	
	@Getter
	@Setter
	@Column(name="aore_type_pronouncement")
	private Integer tipoPronunciamiento;
	
	@Getter
	@Setter
	@Column(name="id_tarea")
	private Integer idTarea;
	
	@Getter
	@Setter
	@Column(name="aore_revision_number")
	private Integer numeroRevision;

	@Getter
	@Setter
	@Column(name="aore_id_previous_report")
	private Integer idInformePrincipal;

	@Getter
	@Setter
	@Transient
	private String nombreReporte;
	
	@Getter
	@Setter
	@Transient
	private String pathReporte;
	
	@Getter
	@Setter
	@Transient
	private byte[] archivoReporte;
	
}
