package ec.gob.ambiente.rcoa.inventarioForestal.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;


/**
 * <b> MODEL. </b>
 *
 * @author Luis Lema
 * @version Revision: 1.0
*/

@Entity
@Table(name="reports_forest_inventory", schema = "coa_forest_inventory")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "refi_status")),	
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "refi_user_creator")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "refi_date_creation")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "refi_user_update")), 
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "refi_date_update"))
})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "refi_status = 'TRUE'")
public class ReporteInventarioForestal extends EntidadAuditable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@Setter
	@Getter
	@Column(name = "refi_id")
	@SequenceGenerator(name = "REPORT_FOREST_INVENTORY_GENERATOR", sequenceName = "reports_forest_inventory_refi_id_seq", schema = "coa_forest_inventory", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REPORT_FOREST_INVENTORY_GENERATOR")
	private Integer id;	
	
	@Setter
	@Getter
	@ManyToOne
	@JoinColumn(name = "foin_id")
	@ForeignKey(name = "fk_refi_foin")
	private InventarioForestalAmbiental inventarioForestalAmbiental;
	
	@Setter
	@Getter
	@ManyToOne
	@JoinColumn(name = "tyre_id")
	@ForeignKey(name = "fk_tyre_refi")
	private TipoReporteInventarioForestal tipoReporteInventarioForestal;
	
	@Setter
	@Getter
	@Column(name="refi_code_of_report")
	private String codigoReporte;
	
	@Setter
	@Getter
	@Column(name="refi_field_inspection_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaInspeccion;
	
	@Setter
	@Getter
	@Column(name="refi_end_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaFin;
	
	@Setter
	@Getter
	@Column(name="refi_operator_delegates_for_inspection")
	private String delegadosInspecion;
	
	@Setter
	@Getter
	@Column(name="refi_user_delegates_inspection")
	private String nombresDelegadoInspeccion;
	
	@Setter
	@Getter
	@Column(name="refi_area_user_delegates_inspection")
	private String areaDelegado;
	
	@Setter
	@Getter
	@Column(name="refi_position_user_delegates_inspection")
	private String cargoDelegado;
	
	@Setter
	@Getter
	@Column(name="refi_background")
	private String antecedentes;
	
	@Setter
	@Getter
	@Column(name="refi_object")
	private String objetivo;
	
	@Setter
	@Getter
	@Column(name="refi_number_coordinate")
	private Integer numeroCoordenadas;
	
	@Setter
	@Getter
	@Column(name="refi_process")
	private String procedimiento;
	
	@Setter
	@Getter
	@Column(name="refi_characterization_ecosystems")
	private String caracterizacionEcosistemas;
	
	@Setter
	@Getter
	@Column(name="refi_characterization_implantation_area")
	private String caracterizacionAreaImplantacion;
	
	@Setter
	@Getter
	@Column(name="refi_conclusions")
	private String conclusiones;
	
	@Setter
	@Getter
	@Column(name="refi_recommendations")
	private String recomendaciones;
	
	@Setter
	@Getter
	@Column(name="refi_annexes")
	private String anexos;
	
	@Setter
	@Getter
	@Column(name="refi_surface_clearing")
	private BigDecimal superficieCoberturaVegetal;
	
	@Setter
	@Getter
	@Column(name="refi_pronouncement_process_file", columnDefinition = "boolean default false")
	private Boolean pronunciamientoParaArchivo;
	
	@Setter
	@Getter
	@Column(name="refi_issues")
	private String asuntoOficio;
	
	@Setter
	@Getter
	@Column(name="refi_question_corrections",columnDefinition = "boolean default false")
	private Boolean realizarCorrecion;
	
	@Setter
	@Getter
	@Column(name="refi_observations_reports")
	private String observacionesInforme;
	
	@Setter
	@Getter
	@Column(name="refi_favorable_pronouncement", columnDefinition = "boolean default false")
	private Boolean pronunciamientoFavorable;
	
	@Setter
	@Getter
	@Column(name="refi_legal_framework")
	private String marcoLegal;
	
	@Setter
	@Getter
	@Column(name="refi_characterization_and_state_vegetal")
	private String  caracterizacionEstadoVegetal;
	
	@Setter
	@Getter
	@Column(name="refi_pronouncement")
	private String pronunciamiento;
	
	@Setter
	@Getter
	@Column(name="refi_review_results")
	private String resultadoRevision;
	
	@Setter
	@Getter
	@Column(name="refi_place_report")
	private String ciudad;
	
	@Setter
	@Getter
	@Column(name="refi_date_report")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fecha;
	
	@Setter
	@Getter
	@OneToMany(mappedBy = "reporteInventarioForestal", fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.MERGE})
    private List<DocumentoInventarioForestal> listaDocumentoInventarioForestal;
	
	@Getter
	@Setter
	@Transient
	private byte[] archivoInforme;
	
	@Getter
	@Setter
	@Transient
	private String informePath;
	
	@Getter
	@Setter
	@Transient
	private String nombreFichero;
	
}