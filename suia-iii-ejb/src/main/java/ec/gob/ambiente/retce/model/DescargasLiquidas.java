package ec.gob.ambiente.retce.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;


/**
 * The persistent class for the liquid_dowloads database table.
 * 
 */
@Entity
@Table(name="liquid_downloads", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "lido_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "lido_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "lido_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "lido_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "lido_user_update")) })
public class DescargasLiquidas extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="lido_id")
	private Integer id;	

	@Getter
	@Setter
	@Column(name="lido_code")
	private String codigo;
	
	@Getter
	@Setter
	@Column(name="lido_report_date")
	private Date fechaReporte;
/*
	@Getter
	@Setter
	@Column(name = "lido_start_date_monitoring")
	private String fechaInicioMonitoreo;
	
	@Getter
	@Setter
	@Column(name = "lido_end_date_monitoring")
	private String fechaFinMonitoreo;
	*/
	
	@Getter
	@Setter
	@Column(name="lido_sended")
	private Boolean enviado;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="proj_id")
	private InformacionProyecto informacionProyecto;
	
	@Getter
	@Setter
	@Transient
	private Documento adjuntoJustificacion;
	
	@Getter
	@Setter
	@Transient
	private Documento oficioPronunciamiento;
	
	@Getter
	@Setter
	@Column(name="lido_history")
	private Boolean historial;
    
    @Getter
	@Setter
	@Column(name="lido_original_record_id")
	private Integer idRegistroOriginal;
    
    @Getter
	@Setter
	@Column(name="lido_observation_number")
	private Integer numeroObservacion;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="resp_id")
	private TecnicoResponsable tecnicoResponsable;
    
    @Getter
	@Setter
	@Transient
	private List<DescargasLiquidas> historialLista;
		
	public boolean getTieneHistorial(){
		return historialLista==null || historialLista.isEmpty()? false:true;
	}

}
