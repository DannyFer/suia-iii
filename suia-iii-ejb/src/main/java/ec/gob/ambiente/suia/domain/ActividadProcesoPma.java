package ec.gob.ambiente.suia.domain;

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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

import java.util.List;

import javax.persistence.Transient;


/**
 * The persistent class for the fapma_process_activity database table.
 * 
 */
@Entity
@Table(name="fapma_process_activity", schema = "suia_iii")
@NamedQuery(name="ActividadProcesoPma.findAll", query="SELECT a FROM ActividadProcesoPma a")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "fapa_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "fapa_status = 'TRUE'")
@NamedQueries({
    @NamedQuery(name = ActividadProcesoPma.OBTENER_POR_FASE, query = "SELECT c FROM ActividadProcesoPma c WHERE c.actividadComercial.categoriaFase.id = :p_categoriaFaseId"),
    @NamedQuery(name = ActividadProcesoPma.OBTENER_POR_FICHA_ID, query = "SELECT a From ActividadProcesoPma a WHERE a.idFicha =:idFicha and a.estado=true and a.idRegistroOriginal = null")
})
public class ActividadProcesoPma extends EntidadBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String OBTENER_POR_FASE = "ec.com.magmasoft.business.domain.ActividadProcesoPma.obtenerPorFase";
        public static final String OBTENER_POR_FICHA_ID = "ec.com.magmasoft.business.domain.ActividadProcesoPma.obtenerPorFichaId";
	
	@Id
	@SequenceGenerator(name = "PROCCES_ACTIVITY_PMA_FAPDID_GENERATOR", sequenceName = "fapma_process_activity_fapr_id_seq", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROCCES_ACTIVITY_PMA_FAPDID_GENERATOR")
	@Column(name="fapa_id")
	@Getter
	@Setter
	private Integer id;
		
	@Column(name="fapa_start_date")
	@Getter
	@Setter
	@Temporal(TemporalType.DATE)
	private Date fechaInicio;
	
	@Column(name="fapa_end_date")
	@Getter
	@Setter
	@Temporal(TemporalType.DATE)
	private Date fechaFin;
	
	@Column(name="fapa_other_description")
	@Getter
	@Setter
	private String descripcionOtros;

    @Column(name="fapa_other_phase_description")
    @Getter
    @Setter
    private String descripcionFaseOtros;

	@Column(name="fapa_description")
	@Getter
	@Setter
	private String descripcion;
	
	//bi-directional many-to-one association to activity_type_catalog
	@ManyToOne
	@JoinColumn(name="coac_id")
	@ForeignKey(name = "coac_process_activity_fk")
	@Getter
	@Setter
	private CatalogoActividadComercial actividadComercial;
	
	//bi-directional many-to-one association to CatiiFapma
	@ManyToOne
	@JoinColumn(name="cafa_id")
	@ForeignKey(name = "catii_fapma_process_activity_fk")
	@Getter
	@Setter
	private FichaAmbientalPma fichaAmbientalPma;
        
	@Column(name = "cafa_id", insertable = false, updatable = false)
	@Getter
	@Setter
	private Integer idFicha;

	@Getter
	@Setter
	@Transient
	private List<MatrizFactorImpacto> listaMatrizFactorImpacto;

	@Override
	public String toString() {
		return descripcionOtros != null ? descripcionOtros : "";
	}
	
	/**
	 * Cris F: aumento de campos
	 */
	@Getter
	@Setter
	@Column(name = "fapa_original_record_id")
	private Integer idRegistroOriginal;
	
	@Getter
	@Setter
	@Column(name = "fapa_historical_date")
	private Date fechaHistorico;
	
	@Getter
	@Setter
	@Transient
	private String nuevoEnModificacion;

	public ActividadProcesoPma() {
		super();
	}

	public ActividadProcesoPma(Integer id) {
		super();
		this.id = id;
	}
	
}