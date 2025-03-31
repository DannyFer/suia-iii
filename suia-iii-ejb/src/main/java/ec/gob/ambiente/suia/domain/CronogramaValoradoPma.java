package ec.gob.ambiente.suia.domain;

import java.util.Date;

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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the fapma_valued_schedule database table.
 *
 */
@NamedQueries({
    @NamedQuery(name = CronogramaValoradoPma.GET_ALL, query = "SELECT c FROM CronogramaValoradoPma c"),
    @NamedQuery(name = CronogramaValoradoPma.OBTENER_POR_FICHA, query = "SELECT c FROM CronogramaValoradoPma c WHERE c.fichaAmbientalPma = :fichaAmbientalPma and c.idRegistroOriginal is null ORDER BY c.idPlan"),
@NamedQuery(name = CronogramaValoradoPma.OBTENER_POR_FICHA_MINERIA, query = "SELECT c FROM CronogramaValoradoPma c WHERE c.fichaAmbientalMineria = :fichaAmbientalMineria and c.idRegistroOriginal is null ORDER BY c.idPlan")})
@Entity
@Table(name = "catii_valued_schedule", schema = "suia_iii")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "cava_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "cava_creation_date")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "cava_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "cava_creator_user")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "cava_user_update"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "cava_status = 'TRUE'")
public class CronogramaValoradoPma extends EntidadAuditable {

    private static final long serialVersionUID = 1L;

    public static final String GET_ALL = "ec.com.magmasoft.business.domain.categoriaii.CronogramaValoradoPma.getAll";
    public static final String OBTENER_POR_FICHA = "ec.com.magmasoft.business.domain.categoriaii.CronogramaValoradoPma.obtenerPorFicha";
    public static final String OBTENER_POR_FICHA_MINERIA = "ec.com.magmasoft.business.domain.categoriaii.CronogramaValoradoPma.obtenerPorFichaMineria";
    public static final String SEQUENCE_CODE = "fapma_valued_schedule_cava_id_seq";

    @Id
    @Column(name = "cava_id")
    @SequenceGenerator(name = "VALUED_SCHEDULE_PMA_CAVAID_GENERATOR", sequenceName = "fapma_valued_schedule_cava_id_seq", schema = "suia_iii", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "VALUED_SCHEDULE_PMA_CAVAID_GENERATOR")
    @Getter
    @Setter
    private Integer id;

    @Column(name = "cava_start_date")
    @Getter
    @Setter
    @Temporal(TemporalType.DATE)
    private Date fechaInicio;

    @Column(name = "cava_activity", length = 150)
    @Getter
    @Setter
    private String actividad;

    @Column(name = "cava_responsible", length = 150)
    @Getter
    @Setter
    private String responsable;

    @Column(name = "cava_end_date")
    @Getter
    @Setter
    @Temporal(TemporalType.DATE)
    private Date fechaFin;

    @Column(name = "cava_budget")
    @Getter
    @Setter
    private Double presupuesto;
    
    @Column(name = "cava_frequency")
    @Getter
    @Setter
    private Integer frecuencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cafa_id")
    @ForeignKey(name = "catii_fapma_catii_valued_schedule_fk")
    @Getter
    @Setter
    private FichaAmbientalPma fichaAmbientalPma;

    @Getter
    @Setter
    @JoinColumn(name = "mien_id", referencedColumnName = "mien_id")
    @ForeignKey(name = "mining_enviromental_record_catii_valued_schedule_fk")
    @ManyToOne
    private FichaAmbientalMineria fichaAmbientalMineria;

    @ManyToOne
    @JoinColumn(name = "empt_id")
    @Getter
    @Setter
    private TipoPlanManejoAmbiental plan;

    @Column(name = "empt_id", insertable = false, updatable = false)
    @Getter
    @Setter
    private Integer idPlan;

    @Getter
    @Setter
    @Transient
    private boolean editar;

    @Getter
    @Setter
    @Transient
    private int indice;
    
    /****** campo periodicidad ******/
    @JoinColumn(name = "freun_id")
    @ManyToOne
    @Getter
    @Setter
    private UnidadPeriodicidad unidadPeriodo;
    /****** campo periodicidad ******/
    
    @Column(name = "cava_justify", length = 150)
    @Getter
    @Setter
    private String justificativo;             
    
    @Getter
    @Setter
    @Column(name = "cava_enter_information")
    private Boolean ingresaInformacion;
    
    
    @Column(name = "cava_observation", length = 255)
    @Getter
    @Setter
    private String observacion;
    
    @Getter
    @Setter
    @Transient
    private boolean nuevoEnModificacion = false;
    
    @Getter
    @Setter
    @Transient
    private boolean historialModificaciones = false;
    
    @Getter
	@Setter
	@Column(name = "cava_original_record_id")
	private Integer idRegistroOriginal;
	
	@Getter
	@Setter
	@Column(name = "cava_historical_date")
	private Date fechaHistorico;
	
	//para validar si se han realizado cambios en el objeto para guardar historial
	public boolean equalsObject(Object obj) {
		if (obj == null)
			return false;
		CronogramaValoradoPma base = (CronogramaValoradoPma) obj;
		if (this.getId() == null && base.getId() == null)
			return super.equals(obj);
		if (this.getId() == null || base.getId() == null)
			return false;
		
		if (this.getId().equals(base.getId())
				&& ((this.getFechaInicio() == null && base.getFechaInicio() == null) || 
						(this.getFechaInicio() != null && base.getFechaInicio() != null && this.getFechaInicio().equals(base.getFechaInicio())))
				&& ((this.getFechaFin() == null && base.getFechaFin() == null) || 
						(this.getFechaFin() != null && base.getFechaFin() != null && this.getFechaFin().equals(base.getFechaFin())))
				&& ((this.getFrecuencia() == null && base.getFrecuencia() == null) || 
						(this.getFrecuencia() != null && base.getFrecuencia() != null && this.getFrecuencia().equals(base.getFrecuencia())))
				&& ((this.getUnidadPeriodo() == null && base.getUnidadPeriodo() == null) || 
						(this.getUnidadPeriodo() != null && base.getUnidadPeriodo() != null && this.getUnidadPeriodo().getId().equals(base.getUnidadPeriodo().getId())))
				&& this.getPresupuesto().equals(base.getPresupuesto())
				&& this.getActividad().equals(base.getActividad())
				&& this.getResponsable().equals(base.getResponsable())
				&& this.getJustificativo().equals(base.getJustificativo())
				&& this.getIngresaInformacion().equals(base.getIngresaInformacion())
				&& this.getObservacion().equals(base.getObservacion()))
			return true;
		else
			return false;
	}
}
