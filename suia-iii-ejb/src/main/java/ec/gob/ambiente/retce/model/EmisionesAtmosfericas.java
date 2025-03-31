package ec.gob.ambiente.retce.model;

import java.io.Serializable;
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

import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the atmospheric_emissions database table.
 * 
 */
@Entity
@Table(name="atmospheric_emissions", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "atem_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "atem_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "atem_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "atem_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "atem_user_update")) })
public class EmisionesAtmosfericas extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="atem_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="atem_code")
	private String codigo;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="proj_id")
	private InformacionProyecto informacionProyecto;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="resp_id")
	private TecnicoResponsable tecnicoResponsable;	

	@Getter
	@Setter
	@Column(name="atem_report_date")
	private Date fechaReporte;
	
	@Getter
	@Setter
	@Column(name="atem_history")
	private Boolean historial;
	
    @Getter
	@Setter
	@Column(name="atem_original_record_id")
	private Integer idRegistroOriginal;
    
    @Getter
	@Setter
	@Column(name="atem_observation_number")
	private Integer numeroObservacion;
    
	@Getter
	@Setter
	@Column(name = "atem_sended")
	private Boolean enviado;
	
	@Getter
	@Setter
	@Column(name = "atem_finalized")
	private Boolean finalizado;
	
	@Getter
	@Setter
	@Transient
	private Documento oficio;
}
