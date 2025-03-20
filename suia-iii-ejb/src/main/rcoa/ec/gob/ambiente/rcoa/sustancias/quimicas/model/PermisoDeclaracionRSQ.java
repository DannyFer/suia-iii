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
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.SustanciaQuimicaPeligrosa;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;


/**
 * The persistent class for the permission_statement database table.
 * 
 */
@Entity
@Table(name="permission_statement", schema = "coa_chemical_sustances")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "pest_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "pest_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "pest_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "pest_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "pest_user_update")) })
@NamedQuery(name="PermisoDeclaracionRSQ.findAll", query="SELECT p FROM PermisoDeclaracionRSQ p")
public class PermisoDeclaracionRSQ extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="pest_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "chsr_id")
	private RegistroSustanciaQuimica registroSustanciaQuimica;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "dach_id")
	private SustanciaQuimicaPeligrosa sustanciaQuimica;

	@Getter
	@Setter
	@Column(name="pest_quota")
	private Double cupo;

	@Getter
	@Setter
	@Column(name="pest_quota_check")
	private Boolean editarCupo;

	@Getter
	@Setter
	@Column(name="pest_stock_check")
	private Boolean editarStock;

	@Getter
	@Setter
	@Column(name="pest_stock")
	private Double stock;

	@Getter
	@Setter
	@Column(name="pest_year")
	private Integer anio;

	@Getter
	@Setter
	@Column(name="pest_month")
	private Integer mes;

	@Getter
	@Setter
	@Column(name="pest_monthly_statement_substances")
	private Boolean declaracionSustancias;

	@Getter
	@Setter
	@Column(name="pest_import_license")
	private Boolean licenciaImportacion;

	@Getter
	@Setter
	@Column(name="pest_route_guides")
	private Boolean guiasRuta;

	@Getter
	@Setter
	@Column(name="pest_current_stock")
	private Double stockActual;

	@Getter
	@Setter
	@Column(name="pest_quota_increase")
	private Double ampliacionCupo;

	@Getter
	@Setter
	@Column(name="pest_quota_previous")
	private Double cupoAnterior;

	@Getter
	@Setter
	@Column(name="pest_parent_id")
	private Integer idPadre;

	@Getter
	@Setter
	@Transient
	private String tipoActivacion;
	
	@Getter
	@Setter
	@Transient
	private Boolean ampliacion;
	
	@Getter
	@Setter
	@Transient
	private PermisoDeclaracionRSQ permisoAnterior;
	
	@Getter
	@Setter
	@Transient
	private DocumentosSustanciasQuimicasRcoa documentoRespaldo;
	
	@Getter
	@Setter
	@Transient
	private Boolean realizaImportacion;
	

	public PermisoDeclaracionRSQ() {
	}

}