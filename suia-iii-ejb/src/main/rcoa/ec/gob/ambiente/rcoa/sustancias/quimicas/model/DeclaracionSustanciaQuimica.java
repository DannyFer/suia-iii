package ec.gob.ambiente.rcoa.sustancias.quimicas.model;

import java.io.Serializable;

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

import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.suia.domain.SustanciaQuimicaPeligrosa;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "chemical_substances_declaration", schema = "coa_chemical_sustances")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "chsd_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "chsd_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "chsd_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "chsd_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "chsd_user_update")) })
public class DeclaracionSustanciaQuimica extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	public static Integer enRegistro = 1;
	public static Integer enviada = 2;
	public static Integer aprobada = 3;

	public DeclaracionSustanciaQuimica() {
	}

	public DeclaracionSustanciaQuimica(RegistroSustanciaQuimica registroSustanciaQuimica) {
		this.registroSustanciaQuimica = registroSustanciaQuimica;
	}

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "chsd_id")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "chsr_id")
	@Getter
	@Setter
	private RegistroSustanciaQuimica registroSustanciaQuimica;

	// fk, de la tabla general_catalogs_coa, entre el gaca_id 158_161
//	@ManyToOne
//	@JoinColumn(name = "geca_id_parameter_ci")
//	@Getter
//	@Setter
//	private CatalogoGeneralCoa parametroCantidadInicio;

	@ManyToOne
	@JoinColumn(name = "chsd_operator")
	@Getter
	@Setter
	private Usuario operador;

	@ManyToOne
	@JoinColumn(name = "dach_id")
	@Getter
	@Setter
	private SustanciaQuimicaPeligrosa sustanciaQuimica;

	@Column(name = "chsd_year")
	@Getter
	@Setter
	private Integer anioDeclaracion;

	@Column(name = "chsd_month")
	@Getter
	@Setter
	private Integer mesDeclaracion;

	@Column(name = "chsd_unit")
	@Getter
	@Setter
	private String unidadSustancia;

	@Column(name = "chsd_starting_amount")
	@Getter
	@Setter
	private Double cantidadInicio;

	@Column(name = "chsd_end_quantity")
	@Getter
	@Setter
	private Double cantidadFin;

	@Column(name = "chsd_fine_statement")
	@Getter
	@Setter
	private Double valorMulta;

	// Declaracion a tiempo, status true si es una declaracion en los 10 primeros
	// dias del mes que le corresponde.
	@Column(name = "chsd_declaration_on_time")
	@Getter
	@Setter
	private Boolean declaracionATiempo;

	@Column(name = "chsd_pending_payment")
	@Setter
	private Boolean pagoPendiente;

	public boolean getPagoPendiente() {
		return (pagoPendiente != null && pagoPendiente == true) ? true : false;
	}

	// Estado de la declaracion, toma los valores de la tabla
	// coa_mae.general_catalogs_coa, los valores estan entre gaca_id 211-214
	@JoinColumn(name = "chsd_declaration_status_geca_id")
	@ManyToOne
	@Getter
	@Setter
	private CatalogoGeneralCoa estadoDeclaracion;
	
	@Getter
	@Setter
	@Column(name="chsd_procedure_code")
	private String tramite;

}