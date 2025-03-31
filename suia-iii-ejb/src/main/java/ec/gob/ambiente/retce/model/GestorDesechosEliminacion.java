package ec.gob.ambiente.retce.model;

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

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.EliminacionDesecho;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;


@Entity
@Table(name="waste_manager_elimination", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "wame_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "wame_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "wame_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "wame_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "wame_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wame_status = 'TRUE'")
public class GestorDesechosEliminacion extends EntidadAuditable{	
	
	private static final long serialVersionUID = -3696645952141534501L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="wame_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "wame_cant")
	private Double cantidad;
	
	@Getter
	@Setter
	@Column(name = "wame_waste_generates")
	private Boolean generaDesecho;
	
	@Getter
	@Setter
	@Column(name = "wame_waste_type")
	private Integer tipoDesecho;
	
	@Getter
	@Setter
	@Column(name = "wame_es0406")
	private Integer cantidadEs0406;
	
	@Getter
	@Setter
	@Column(name = "wame_cant_retce")
	private Double cantidadDesechoRetce;
	 
	@Getter
	@Setter
	@Column(name = "wame_estimation_method")
	private String metodoEstimacion;
	 
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="hwm_id")
	private GestorDesechosPeligrosos gestorDesechosPeligrosos;  
	  
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="wadi_id")
	private EliminacionDesecho eliminacionDesecho;  
	 
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="gcde_id")
	private DetalleCatalogoGeneral unidadMedida;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="csre_id")
	private CatalogoSustanciasRetce catalogoSustancias;
	
	@Getter
	@Setter
	@Column(name = "wame_cant_tons")
	private Double cantidadTonelada;
	
	@Getter
	@Setter
	@Column(name = "wame_cant_kilo")
	private Double cantidadKilo;
	
	@Getter
	@Setter
	@Column(name = "wame_docu_id")
	private Integer docuId;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="wada_id")
	private DesechoPeligroso desechoPeligroso;
	
	@Getter
	@Setter
	@Transient
	private Documento adjuntoCalculo;
	
	@Getter
	@Setter
	@Transient
	private List<GestorDesechosEliminacionDesechoPeligroso> listGestorDesechosEliminacionDesechoPeligroso;
	
	@Getter
	@Setter
	@Transient
	private List<GestorDesechosEliminacionDesechoNoPeligroso> listGestorDesechosEliminacionDesechoNoPeligroso;
	
	@Getter
	@Setter
	@Transient
	private List<GestorDesechosEliminacion> listGestorDesechosEliminacionSustanciasRETCE;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="lage_id")
	private LaboratorioGeneral laboratorioGeneral;

}
