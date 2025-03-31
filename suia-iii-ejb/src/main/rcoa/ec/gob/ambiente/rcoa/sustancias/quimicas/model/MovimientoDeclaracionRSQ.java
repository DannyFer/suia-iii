package ec.gob.ambiente.rcoa.sustancias.quimicas.model;
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

import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="chemical_substances_movements", schema = "coa_chemical_sustances")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "chsm_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "chsm_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "chsm_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "chsm_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "chsm_user_update")) })
public class MovimientoDeclaracionRSQ extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public MovimientoDeclaracionRSQ() {
		  
	}
	
	public MovimientoDeclaracionRSQ(DeclaracionSustanciaQuimica declaracionSustanciaQuimica) {
		this.declaracionSustanciaQuimica=declaracionSustanciaQuimica;
	}
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="chsm_id")
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "chsd_id")	
	@Getter
	@Setter
	private DeclaracionSustanciaQuimica declaracionSustanciaQuimica;
	
	@ManyToOne
	@JoinColumn(name = "geca_id")	
	@Getter
	@Setter
	private CatalogoGeneralCoa tipoMovimiento;
	
	@ManyToOne
	@JoinColumn(name = "geca_id_presentation")	
	@Getter
	@Setter
	private CatalogoGeneralCoa tipoPresentacion;
	
	@ManyToOne
	@JoinColumn(name = "chsm_operator")	
	@Getter
	@Setter
	private Usuario operador;
	
	@Column(name = "chsm_invoice")	
	@Getter
	@Setter
	private String numeroFactura;
	
	@Column(name = "chsm_presentation_option")	
	@Getter
	@Setter
	private String opcionPresentacion;
	
	@Column(name = "chsm_finished_product")	
	@Getter
	@Setter
	private String productoTerminado;
	
	@Column(name = "chsm_observation_status")	
	@Getter
	@Setter
	private String observacionCambioEstado;
	
	@Column(name = "chsm_packaging_serial_number")	
	@Getter
	@Setter
	private String numeroSerieEnvases;
	
	@Column(name = "chsm_entry")	
	@Getter
	@Setter
	private Double valorIngreso;
	
	@Column(name = "chsm_exit")	
	@Getter
	@Setter
	private Double valorEgreso;
	
	@Column(name = "chsm_packaging_quantity")	
	@Getter
	@Setter
	private Double cantidadEnvases;
	
	@Column(name = "chsm_review")	
	@Getter
	@Setter
	private Boolean estadoRevision;
	
	@Column(name = "chsm_review_date")	
	@Getter
	@Setter
	private Date fechaRevision;
	
	@Column(name = "chsm_description")	
	@Getter
	@Setter
	private String descripcion;
	
	@Column(name = "chsm_right")	
	@Getter
	@Setter
	private Boolean correcto;
	
	@Getter
	@Setter
	@Column(name = "chsr_id")
	private Integer idRegistroSustancias;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "inre_id")
	private SolicitudImportacionRSQ solicitudImportacion;
	
	@Getter
	@Setter
	@Transient
	private boolean movimientoComparado;
	
}