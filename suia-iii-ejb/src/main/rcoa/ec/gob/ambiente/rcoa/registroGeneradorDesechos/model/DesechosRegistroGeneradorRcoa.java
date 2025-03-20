package ec.gob.ambiente.rcoa.registroGeneradorDesechos.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

/**
 * The persistent class for the waste_waste_generation_points database table.
 * 
 */
@Entity
@Table(name = "waste_waste_generation_points", schema = "coa_waste_generator_record")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "wwgp_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "wwgp_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "wwgp_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "wwgp_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "wwgp_user_update")) })
@NamedQuery(name = "DesechosRegistroGeneradorRcoa.findAll", query = "SELECT d FROM DesechosRegistroGeneradorRcoa d")
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wwgp_status = 'TRUE'")
public class DesechosRegistroGeneradorRcoa extends EntidadAuditable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "wwgp_id")
	private Integer id;

	@Setter
	@Getter
	@ManyToOne
	@JoinColumn(name = "ware_id")
	private RegistroGeneradorDesechosRcoa registroGeneradorDesechosRcoa;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "wada_id")
	private DesechoPeligroso desechoPeligroso;

	@Getter
	@Setter
	@Column(name = "wwgp_generate_waste")
	private Boolean generaDesecho;

	@Getter
	@Setter
	@Column(name = "wwgp_quantity_kilograms")
	private BigDecimal cantidadKilos;

	@Getter
	@Setter
	@Column(name = "wwgp_quantity_tons")
	private BigDecimal cantidadToneladas;

	@Getter
	@Setter
	@Column(name = "wwgp_internal_management")
	private Boolean gestionInterna;

	public DesechosRegistroGeneradorRcoa() {
	}

	@Getter
	@Setter
	@Column(name = "wwgp_other")
	private String otroGeneracion;

	@Getter
	@Setter
	@Column(name = "wwgp_search_added")
	private Boolean agregadoPorOperador;

	@Getter
	@Setter
	@Column(name = "wwgp_waste_description")
	private String descripcionDesecho;

	@Getter
	@Setter
	@Column(name = "wwgp_quantity_unit")
	private BigDecimal cantidadUnidades;

	@Getter
	@Setter
	@Column(name = "wwgp_management_individual")
	private Boolean sistemaGestionIndividual;

	@Getter
	@Setter
	@Column(name = "wwgp_management_system_name")
	private String sistemaGestionNombre;

	@Getter
	@Setter
	@Column(name = "wwgp_management_system_date")
	private Date sistemaGestionFecha;
	
	@Transient
	@Getter
	@Setter
	private Boolean otroGeneracionVer;

	@Transient
	@Getter
	@Setter
	private DocumentosRgdRcoa documentoGenera;

	@Transient
	@Getter
	@Setter
	private Boolean mostrarEliminar;

	@Transient
	@Getter
	@Setter
	private Integer idGeneracion;

	@Transient
	@Getter
	@Setter
	private List<PuntoGeneracionRgdRcoa> puntoGeneracionRgdRcoaList;

	@Transient
	@Getter
	@Setter
	private List<String> puntoGeneracionIdList = new ArrayList<String>();

	@Transient
	@Getter
	@Setter
	private String nombreOrigen = "Seleccione..";

	@Transient
	@Getter
	@Setter
	private boolean seleccionarDesecho;

	@Transient
	@Getter
	@Setter
	private String nombresGeneracion;
	
	@Transient
	@Setter
	@Getter
	private List<DocumentosRgdRcoa> documentosGestion;
	
	@Transient
	@Getter
	@Setter
	private Integer identificador;

}
