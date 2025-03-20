package ec.gob.ambiente.rcoa.viabilidadAmbiental.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

/**
 * The persistent class for the possible_effects_forest database table.
 * 
 */
@Entity
@Table(name="possible_effects_forest", schema = "coa_viability")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "poef_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "poef_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "poef_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "poef_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "poef_user_update")) })

@NamedQueries({
@NamedQuery(name="AfectacionForestal.findAll", query="SELECT a FROM AfectacionForestal a"),
@NamedQuery(name=AfectacionForestal.GET_LISTA_POR_INFORME, query="SELECT a FROM AfectacionForestal a where a.idInformeFactibilidad = :idInformeFactibilidad and a.estado = true order by id")
})

public class AfectacionForestal extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAQUETE = "ec.gob.ambiente.rcoa.viabilidadAmbiental.model.";

	public static final String GET_LISTA_POR_INFORME = PAQUETE + "AfectacionForestal.getListaPorInforme";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="poef_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="fere_id")
	private Integer idInformeFactibilidad;

	@Getter
	@Setter
	@Column(name="poef_activity")
	private String actividad;

	@Getter
	@Setter
	@Column(name="poef_zpp")
	private Boolean zppZonaProteccion;

	@Getter
	@Setter
	@Column(name="poef_zmbn")
	private Boolean zmbnZonaManejoBosque;

	@Getter
	@Setter
	@Column(name="poef_zr")
	private Boolean zrZonaRecuperacion;

	@Getter
	@Setter
	@Column(name="poef_zou")
	private Boolean zouZonaOtrosUsos;

	@Getter
	@Setter
	@Column(name="poef_ecosystem")
	private String tipoEcosistemas;

	@Getter
	@Setter
	@Column(name="poef_vegetable_cover")
	private String tipoCoberturaVegetal;

	@Getter
	@Setter
	@Column(name="poef_agreement")
	private String tipoConvenio;

	@Getter
	@Setter
	@Column(name="poef_cites")
	private Boolean esCites;

	@Getter
	@Setter
	@Column(name="poef_uicn")
	private Boolean esUicn;

	@Getter
	@Setter
	@Column(name="poef_endemic")
	private Boolean esEndemica;

	@Getter
	@Setter
	@Column(name="poef_conditional_use")
	private Boolean esAprovechamientoCondicionado;

	@Getter
	@Setter
	@Column(name="poef_description_pfn")
	private String descripcionAfectacion;

	@Getter
	@Setter
	@Column(name="poef_proposed_measure")
	private String medidaPropuesta;

	@Getter
	@Setter
	@Column(name="poef_description_pfn_tec")
	private String descripcionAfectacionTecnico;

	@Getter
	@Setter
	@Column(name="poef_proposed_measure_tec")
	private String medidaPropuestaTecnico;

	@Getter
	@Setter
	@Column(name="poef_hydrographic_units")
	private String tipoUnidadesHidrograficas;

	@Getter
	@Setter
	@Transient
	private List<String> listaIdEcosistemas;

	@Getter
	@Setter
	@Transient
	private List<String> listaIdCoberturaVegetal;

	@Getter
	@Setter
	@Transient
	private List<String> listaIdConvenio;
	
	@Getter
	@Setter
	@Transient
	private List<String> listaIdUnidadHidrograficas;

	@Getter
	@Setter
	@Transient
	private String nombresEcosistemas;

	@Getter
	@Setter
	@Transient
	private String nombresCoberturaVegetal;

	@Getter
	@Setter
	@Transient
	private String nombresConvenio;
	
	@Getter
	@Setter
	@Transient
	private String nombresUnidadHidrograficas;

}