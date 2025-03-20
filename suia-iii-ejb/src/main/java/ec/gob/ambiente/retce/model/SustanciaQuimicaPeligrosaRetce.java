package ec.gob.ambiente.retce.model;

import java.io.Serializable;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the hazardous_chemicals database table.
 * 
 */
@Entity
@Table(name="hazardous_chemicals", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "hach_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "hach_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "hach_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "hach_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "hach_user_update")) })
public class SustanciaQuimicaPeligrosaRetce extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="hach_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="gcde_id")
	private DetalleCatalogoGeneral catalogoGeneralTipoSustancia;
	
	@Getter
	@Setter
	@Column(name="hach_mixture_name")
	private String nombreMezcla;

	@Getter
	@Setter
	@Column(name="hach_other_container")
	private String otroEnvase;

	@Getter
	@Setter
	@OneToMany(mappedBy = "sustanciaQuimicaPeligrosa")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "hcde_status = 'TRUE'")
	private List<SustanciaQuimicaPeligrosaDetalle> detalleList;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "dcsr_id")
	private ReporteSustanciasQuimicasPeligrosas reporteSustanciaQuimica;
	
	@Getter
	@Setter
	@Transient
	private Double cantidadAnual;
	
	@Getter
	@Setter
	@Transient
	private Double total;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="gcde_container_id")
	private DetalleCatalogoGeneral catalogoGeneralEnvase;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="gcde_process_id")
	private DetalleCatalogoGeneral catalogoGeneralProceso;
	
	@Getter
	@Setter
	@Transient
	private String estadoFisico;
}