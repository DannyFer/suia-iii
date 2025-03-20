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
@Table(name="waste_manager_final_disposition", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "wmfd_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "wmfd_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "wmfd_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "wmfd_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "wmfd_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wmfd_status = 'TRUE'")
public class GestorDesechosDisposicionFinal extends EntidadAuditable{
	
	private static final long serialVersionUID = 3279726565742463177L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="wmfd_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "wmfd_cant")
	private Double cantidad;
	
	@Getter
	@Setter
	@Column(name = "wmfd_es0406")
	private Integer cantidadEs0406;
	
	@Getter
	@Setter
	@Column(name = "wmfd_cant_retce")
	private Double cantidadDesechoRetce;
	 
	@Getter
	@Setter
	@Column(name = "wmfd_estimation_method")
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
	@Column(name = "wmfd_cant_tons")
	private Double cantidadTonelada;
	
	@Getter
	@Setter
	@Column(name = "wmfd_cant_kilo")
	private Double cantidadKilo;
	
	@Getter
	@Setter
	@Column(name = "wmfd_docu_id")
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
	private List<GestorDesechosDisposicionFinal> listGestorDesechosEliminacionSustanciasRETCE;

}
