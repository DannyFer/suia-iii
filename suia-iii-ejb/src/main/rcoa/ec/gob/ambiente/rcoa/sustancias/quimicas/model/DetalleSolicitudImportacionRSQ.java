package ec.gob.ambiente.rcoa.sustancias.quimicas.model;

import java.io.Serializable;
import java.math.BigDecimal;

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

import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;

/**
 * The persistent class for the detail_import_request database table.
 * 
 */
@Entity
@Table(name = "detail_import_request", schema = "coa_chemical_sustances")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "deir_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "deir_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "deir_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "deir_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "deir_user_update")) })
public class DetalleSolicitudImportacionRSQ extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "deir_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "deir_available_space")
	private BigDecimal cupoCantidad;

	@Getter
	@Setter
	@Column(name = "deir_gross_weight")
	private BigDecimal pesoBruto;

	@Getter
	@Setter
	@Column(name = "deir_gross_weight_unit")
	private String unidadPesoBruto;

	@Getter
	@Setter
	@Column(name = "deir_net_weight")
	private BigDecimal pesoNeto;

	@Getter
	@Setter
	@Column(name = "deir_net_weight_unit")
	private String unidadPesoNeto;

	@Getter
	@Setter
	@Column(name = "deir_tariff_subheading")
	private String subPartidaArancelaria;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "gelo_id")
	private UbicacionesGeografica ubicacionGeografica;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "inre_id")
	private SolicitudImportacionRSQ solicitudImportacionRSQ;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "achs_id")
	private ActividadSustancia actividadSustancia;
	
	@Getter
	@Setter
	@Column(name = "geca_name")
	private String tipoRecipiente;
	
	public DetalleSolicitudImportacionRSQ() {
	}
}