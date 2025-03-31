package ec.gob.ambiente.rcoa.sustancias.quimicas.model;

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

import ec.gob.ambiente.rcoa.enums.TipoInformeOficioEnum;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;

/**
 * The persistent class for the reports_trades database table.
 * 
 */
@Entity
@Table(name = "reports_trades", schema = "coa_chemical_sustances")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "retr_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "retr_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "retr_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "retr_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "retr_user_update")) })
public class InformeOficioRSQ extends EntidadAuditable {
	
	
	private static final long serialVersionUID = -1L;
	
	public InformeOficioRSQ() {}
	
	public InformeOficioRSQ(RegistroSustanciaQuimica registroSustanciaQuimica,TipoInformeOficioEnum tipo,Integer numero,Area area) {
		this.registroSustanciaQuimica=registroSustanciaQuimica;
		this.tipo=new CatalogoGeneralCoa(tipo.getId());
		this.numero=numero;
		this.area=area;
	}

	@Getter
	@Setter
	@Id
	@Column(name = "retr_id")	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
    @Getter
    @Setter
    @JoinColumn(name = "chsr_id")
    @ManyToOne    
    private RegistroSustanciaQuimica registroSustanciaQuimica;

    /*    
    retr_status_date timestamp without time zone, -- Fecha de cambio de estado
    retr_status_observation character varying, -- Observacion de porque el cambio de estado.    
    retr_observation_bd character varying(400), -- Observacion de modificacion de BD*/
    
	@Getter
	@Setter	
	@JoinColumn(name = "geca_id")
	@ManyToOne		
	private CatalogoGeneralCoa tipo;
    
	@Getter
	@Setter
	@Column(name = "retr_code", length = 50)
	private String codigo;
	
	@Getter
	@Setter
	@Column(name = "retr_number")
	private Integer numero;
	
	@Getter
	@Setter
	@Column(name = "retr_request_date")
	private Date fechaSolicitud;
	
	@Getter
	@Setter
	@Column(name = "retr_evaluation_date")
	private Date fechaEvaluacion;
	
	@Getter
	@Setter	
	@JoinColumn(name = "area_id")
	@ManyToOne		
	private Area area;
	
	@Getter
	@Setter
	@Transient	
	private boolean informeApoyo;
	
	@Getter
	@Setter
	@Transient	
	private DocumentosSustanciasQuimicasRcoa documento;
}