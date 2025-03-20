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
@Table(name = "approval_observation_filing_office", schema = "coa_mae")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "aofo_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "aofo_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "aofo_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "aofo_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "aofo_user_update")) 
})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "aofo_status = 'TRUE'")

public class OficioPronunciamientoDiagnostico extends EntidadAuditable{
	
	private static final long serialVersionUID = 1801511545851715084L;

	@Getter
	@Setter
	@Id
	@Column(name = "aofo_id")
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
	@Column(name="aofo_office_code")
	private String codigo;
	
	@Setter
	@Getter
	@Column(name="aofo_office_date")
	private Date fechaElaboracion;
	
	@Getter
	@Setter
	@Column(name="aofo_affair")
	private String asunto;
	
	@Getter
	@Setter
	@Column(name="aofo_pronouncement")
	private String pronunciamiento;
	
	@Getter
	@Setter
	@Column(name="aofo_type_pronouncement")
	private Integer tipoPronunciamiento; // 1. APROBACIÓN 2.- OBSERVACIÓN 3. ARCHIVACIÓN OBSERVACIONES DIAGNOSTICO AMBIENTAL 4. ARCHIVACIÓN  NO INICIA REGULARIZACION
	
	@Getter
	@Setter
	@Column(name="id_tarea")
	private Integer idTarea;
	
	@Getter
	@Setter
	@Column(name="aofo_revision_number")
	private Integer numeroRevision;

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
