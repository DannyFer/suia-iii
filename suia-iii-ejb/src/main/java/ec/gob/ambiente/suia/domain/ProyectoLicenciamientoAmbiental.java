package ec.gob.ambiente.suia.domain;

import java.util.ArrayList;
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the proyectos_licenciamiento_ambiental database table.
 * 
 */
@NamedQueries({ @NamedQuery(name = ProyectoLicenciamientoAmbiental.GET_REPRESENTANTE_PROYECTO, query = "SELECT p.usuario FROM ProyectoLicenciamientoAmbiental p WHERE p.id = :idProyecto") })
@Entity
@Table(name = "projects_environmental_licensing", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "pren_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "pren_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "pren_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "pren_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "pren_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "pren_status = 'TRUE'")
public class ProyectoLicenciamientoAmbiental extends EntidadAuditable {

	private static final long serialVersionUID = -2509239615514438566L;

	public static final String GET_REPRESENTANTE_PROYECTO = "ec.com.magmasoft.business.domain.ProyectoLicenciamientoAmbiental.getRepresentanteProyecto";
	public static final String SEQUENCE_CODE = "seq_pren_code";

	@Getter
	@Setter
	@Id
	@Column(name = "pren_id")
	@SequenceGenerator(name = "PROJECTS_ENVIRONMENTAL_LICENSING_PRENID_GENERATOR", sequenceName = "seq_pren_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROJECTS_ENVIRONMENTAL_LICENSING_PRENID_GENERATOR")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "pren_address", length = 255)
	private String direccionProyecto;

	@Getter
	@Setter
	@Column(name = "pren_delete_reason", length = 10485750)
	private String motivoEliminar;

	@Column(length = 10485760, name = "pren_name")
	@Getter
	@Setter
	private String nombre;

	@Getter
	@Setter
	@Column(name = "pren_routes", length = 500)
	private String rutas;

	@Getter
	@Setter
	@Column(length = 10485750, name = "pren_resume")
	private String resumen;

	@Getter
	@Setter
	@Column(name = "pren_unit")
	private String unidad;

	@Getter
	@Setter
	@Column(name = "pren_code")
	private String codigo;

	@Getter
	@Setter
	@Column(name = "pren_mining_code")
	private String codigoMinero;

	@Getter
	@Setter
	@Column(name = "pren_resolution_number")
	private String numeroDeResolucion;

	@Getter
	@Setter
	@Column(name = "pren_project_status")
	private boolean estadoProyecto = true;

	@Getter
	@Setter
	@Column(name = "pren_mining_grants")
	private boolean concesionesMinerasMultiples;

	@Getter
	@Setter
	@Column(name = "pren_artisanal_miners")
	private boolean minerosArtesanales;

	@Getter
	@Setter
	@Column(name = "pren_concession_areas")
	private boolean mineriaAreasConcesionadas;

	@Getter
	@Setter
	@Column(name = "pren_project_data_of_main_office")
	private boolean datosOficinaPrincipal;

	@Getter
	@Setter
	@Column(name = "pren_project_finalized")
	private boolean finalizado;
	
	@Getter
	@Setter
	@Column(name = "pren_project_finished")
	private boolean completado;

	@Getter
	@Setter
	@Column(name = "pren_is_hazardous_wastes_management")
	private Boolean gestionaDesechosPeligrosos;

	@Getter
	@Setter
	@Column(name = "pren_is_transport_of_chemicals_and_hazardous")
	private Boolean transporteSustanciasQuimicasPeligrosos;

	@Getter
	@Setter
	@Column(name = "pren_is_waste_generated")
	private Boolean generaDesechos;

	@Getter
	@Setter
	@Column(name = "pren_is_rgd_in_course")
	private boolean rgdEncurso;

	@Getter
	@Setter
	@Column(name = "pren_is_chemical_sustances_used")
	private Boolean utilizaSustaciasQuimicas;

	@Getter
	@Setter
	@Column(name = "pren_is_planted_used")
	private Boolean utilizaPlantados;

	@Getter
	@Setter
	@Column(name = "pren_is_drinking_water_sewerage")
	private Boolean sistemasAguaPotableAlcantarillado;
	
	@Getter
	@Setter
	@Column(name = "pren_status_map")
	private Boolean estadoMapa;
	

	@Getter
	@Setter
	@Column(name = "pren_is_water_treatment_plant")
	private Boolean plantaTratamientoAgua;

	@Getter
	@Setter
	@Column(name = "pren_is_wastewater_treatment")
	private Boolean tratamientoAguasResiduales;

	@Getter
	@Setter
	@Column(name = "pren_is_solid_waste")
	private Boolean residuosSolidos;

	@Getter
	@Setter
	@Column(name = "pren_area")
	private double area;

	@Getter
	@Setter
	@Column(name = "pren_altitude")
	private int altitud;

	@Getter
	@Setter
	@Temporal(TemporalType.DATE)
	@Column(name = "pren_end_date")
	private Date fechaFin;

	@Getter
	@Setter
	@Temporal(TemporalType.DATE)
	@Column(name = "pren_start_date")
	private Date fechaInicio;

	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "pren_register_date")
	private Date fechaRegistro;

	@Getter
	@Setter
	@Temporal(TemporalType.DATE)
	@Column(name = "pren_start_operations")
	private Date fechaInicioOperaciones;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "ubty_id")
	@ForeignKey(name = "fk_projectspren_id_ubication_typesubty_id")
	private TipoUbicacion tipoUbicacion;

	@ManyToOne
	@JoinColumn(name = "user_id")
	@ForeignKey(name = "fk_projectspren_id_usersuser_id")
	@Getter
	@Setter
	private Usuario usuario;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "stty_id")
	@ForeignKey(name = "fk_projectspren_id_study_typesstty_id")
	private TipoEstudio tipoEstudio;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "suus_id")
	@ForeignKey(name = "fk_projectspren_id_substance_use_typessuus_id")
	private TipoUsoSustancia tipoUsoSustancia;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "sety_id")
	@ForeignKey(name = "fk_projectspren_id_sector_typessety_id")
	private TipoSector tipoSector;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "poty_id")
	@ForeignKey(name = "fk_projectspren_id_population_typespoty_id")
	private TipoPoblacion tipoPoblacion;

	@ManyToOne
	@JoinColumn(name = "enin_id")
	@ForeignKey(name = "fk_projectspren_id_inclusion_emission_typesenin_id")
	@Getter
	@Setter
	private TipoEmisionInclusionAmbiental tipoEmisionInclusionAmbiental;

	@ManyToOne
	@JoinColumn(name = "maty_id")
	@ForeignKey(name = "fk_projectspren_id_material_typesmaty_id")
	@Getter
	@Setter
	private TipoMaterial tipoMaterial;

	@ManyToOne
	@JoinColumn(name = "feof_id")
	@ForeignKey(name = "fk_projectspren_id_feasibility_officesfeof_id")
	@Getter
	@Setter
	private OficioViabilidad oficioViabilidad;

	@Getter
	@Setter
	@Column(name = "maty_id", insertable = false, updatable = false)
	private Integer idTipoMaterial;

	@ManyToOne
	@JoinColumn(name = "cacs_id")
	@ForeignKey(name = "fk_projectspren_id_categoriescatalogscacs_id")
	@Getter
	@Setter
	private CatalogoCategoriaSistema catalogoCategoria;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "codare_id")
	@ForeignKey(name = "fk_projectspren_id_contrubutorsdatacodare_id")
	private ContributorData refineria;

	@Getter
	@Setter
	@JoinColumn(name = "codain_id")
	@ManyToOne
	@ForeignKey(name = "fk_projectspren_id_contrubutorsdatacodain_id")
	private ContributorData infraestructura;

	@Getter
	@Setter
	@JoinColumn(name = "codatr_id")
	@ManyToOne
	@ForeignKey(name = "fk_projectspren_id_contrubutorsdatacodatr_id")
	private ContributorData comercializadora;

	@Getter
	@Setter
	@JoinColumn(name = "codass_id")
	@ManyToOne
	@ForeignKey(name = "fk_projectspren_id_contrubutorsdatacodass_id")
	private ContributorData estacionServicio;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "area_id")
	@ForeignKey(name = "fk_projectspren_id_area_id")
	private Area areaResponsable;

	@Getter
	@Setter
	@OneToMany(mappedBy = "proyectoLicenciamientoAmbiental")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filters( {
		@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prlo_status = 'TRUE'"),
		@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prlo_update_number = '0'")
	} )
	@OrderBy("prlo_order")
	private List<ProyectoUbicacionGeografica> proyectoUbicacionesGeograficas;

	@Getter
	@Setter
	@OneToMany(mappedBy = "proyectoLicenciamientoAmbiental", fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prrl_status = 'TRUE'")
	private List<ProyectoRutaUbicacionGeografica> proyectoRutaUbicacionesGeograficas;

	@Getter
	@Setter
	@OneToMany(mappedBy = "proyectoLicenciamientoAmbiental")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prwp_status = 'TRUE'")
	private List<ProyectoFaseDesecho> fasesDesechos;

	@Getter
	@Setter
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "proyectoLicenciamientoAmbiental")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prwd_status = 'TRUE'")
	private List<ProyectoDesechoPeligroso> proyectoDesechoPeligrosos;

	@Getter
	@Setter
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "proyectoLicenciamientoAmbiental")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prbl_status = 'TRUE'")
	private List<ProyectoBloque> proyectoBloques;

	@Getter
	@Setter
	@OneToMany(mappedBy = "proyectoLicenciamientoAmbiental")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prcs_status = 'TRUE'")
	private List<ProyectoSustanciaQuimica> proyectoSustanciasQuimicas;

	@Getter
	@Setter
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "proyectoLicenciamientoAmbiental")
	@Filters( {
		@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prsh_status = 'TRUE'"),
		@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prsh_update_number = '0'")
	} )
	private List<FormaProyecto> formasProyectos;

	@Getter
	@Setter
	@OneToMany(mappedBy = "proyectoLicenciamientoAmbiental")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prgc_status = 'TRUE'")
	private List<ProyectoGeneralCatalogo> proyectoGeneralCatalogo;

	@Getter
	@Setter
	@OneToMany(mappedBy = "proyectoLicenciamientoAmbiental")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "migr_status = 'TRUE'")
	private List<ConcesionMinera> concesionesMineras;

	@Getter
	@Setter
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "proyectoLicenciamientoAmbiental")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "pram_status = 'TRUE'")
	private List<ProyectoMineroArtesanal> proyectoMinerosArtesanales;

//	@Getter
//	@Setter
//	@OneToMany(mappedBy = "proyectoLicenciamientoAmbiental")
//	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "eist_status = 'TRUE'")
//	private List<EstudioImpactoAmbiental> estudioImpactoAmbientales;

	@Getter
	@Setter
	@OneToMany(mappedBy = "proyectoLicenciamientoAmbiental")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "paym_status = 'TRUE'")
	private List<Pago> pagos;

//	@OneToMany(mappedBy = "proyectoLicenciamientoAmbiental")
//	@LazyCollection(LazyCollectionOption.FALSE)
//	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prdo_status = 'TRUE'")
//	@Getter
//	@Setter
//	private List<DocumentoProyecto> documentoProyectos;

	@Getter
	@Setter
	@Column(name = "pren_intersection_project_successful")
	private Boolean proyectoInterseccionExitosa;
	
	@Getter
	@Setter
	@Column(name = "pren_is_highlands")
	private Boolean proyectoTierrasAltas;
	
	@Getter
	@Setter
	@Column(name = "pren_is_beaches")
	private Boolean proyectoPlayasBahias;
	
	@Getter
	@Setter
	@Column(name = "pren_financed_by_the_state")
	private String financiadoEstado;
	
	@Getter
	@Setter
	@Column(name = "astc_id")
	private Integer idEstadoAprobacionTdr;

	@Getter
	@Setter
	@Column(name = "pren_format")
	private String formato;
	
//	@ManyToOne
//	@JoinColumn(name = "shri_id")
//	@ForeignKey(name = "shri_id")
//	@Getter
//	@Setter
//	private Camaroneras oficioCamaronera;	
	
	/**
	 * walter
	 */
//	@Getter
//	@Setter
//	@Column(name = "pren_community_pps")
//	private String comunidadPps;
	
	@Getter
	@Setter
	@Column(name = "pren_count_coordinates")
	private Integer cuentaCoordenadas;	
	
//	@Getter
//	@Setter
//	@Column(name = "pren_manually_archived")
//	private Boolean archivadoManualmente;
	
	@Override
	public String toString() {
		return nombre;
	}

	public String getStringDesechosPeligrosos() {
		StringBuffer resultado = new StringBuffer();
		if (proyectoDesechoPeligrosos != null && !proyectoDesechoPeligrosos.isEmpty()) {
			for (int i = 0; i < proyectoDesechoPeligrosos.size(); i++) {
				resultado.append(proyectoDesechoPeligrosos.get(i).getDesechoPeligroso().getDescripcion());
			}
			resultado.deleteCharAt(resultado.length() - 1);
		}
		return resultado.toString();
	}

	public String getStringFasesDesechos() {
		StringBuffer resultado = new StringBuffer();
		if (fasesDesechos != null && !fasesDesechos.isEmpty()) {
			for (int i = 0; i < fasesDesechos.size(); i++) {
				resultado.append(fasesDesechos.get(i));
				resultado.append(",");
			}
			resultado.deleteCharAt(resultado.length() - 1);
		}
		return resultado.toString();
	}

	public String getStringSustanciasQuimicas() {
		StringBuffer resultado = new StringBuffer();
		if (proyectoSustanciasQuimicas != null && !proyectoSustanciasQuimicas.isEmpty()) {
			for (int i = 0; i < proyectoSustanciasQuimicas.size(); i++) {
				resultado.append(proyectoSustanciasQuimicas.get(i));
				resultado.append(",");
			}
			resultado.deleteCharAt(resultado.length() - 1);
		}
		return resultado.toString();
	}

	public MineroArtesanal getMineroArtesanal() {
		try {
			return proyectoMinerosArtesanales.get(0).getMineroArtesanal();
		} catch (Exception e) {
			return null;
		}
	}

	public ConcesionMinera getConcesionMinera() {
		try {
			return concesionesMineras.get(0);
		} catch (Exception e) {
			return null;
		}
	}

	@Getter
	@Setter
	@Transient
	private Integer sector;

	public ProyectoLicenciamientoAmbiental() {
	}

	public ProyectoLicenciamientoAmbiental(Integer id) {
		this.id = id;
	}

	public void ProyectoLicenciamientoAmbientaltotal(Integer id,Integer d) {
		this.id = id;
	}
	
	public boolean isAreaResponsableEnteAcreditado() {
		return getAreaResponsable().getTipoArea().getId().equals(3);
	}

	public boolean isEliminacionSolicitada() {
		return getMotivoEliminar() != null && !getMotivoEliminar().trim().isEmpty();
	}

	public UbicacionesGeografica getPrimeraParroquia() throws Exception {
		List<ProyectoUbicacionGeografica> ubicaciones = getProyectoUbicacionesGeograficas();
		UbicacionesGeografica parroquia = null;

		for (ProyectoUbicacionGeografica proyectoUbicacionGeografica : ubicaciones) {
			parroquia = proyectoUbicacionGeografica.getUbicacionesGeografica();
			if(proyectoUbicacionGeografica.getOrden()!=null && proyectoUbicacionGeografica.getOrden()==1){
				parroquia = proyectoUbicacionGeografica.getUbicacionesGeografica();
				break;
			}
		}
		return parroquia;
	}

	public UbicacionesGeografica getPrimeraProvincia() throws Exception {
		List<ProyectoUbicacionGeografica> ubicaciones = getProyectoUbicacionesGeograficas();
		UbicacionesGeografica parroquia = null;

			for (ProyectoUbicacionGeografica proyectoUbicacionGeografica : ubicaciones) {
				parroquia = proyectoUbicacionGeografica.getUbicacionesGeografica();
				if(proyectoUbicacionGeografica.getOrden()!=null && proyectoUbicacionGeografica.getOrden()==1){
					parroquia = proyectoUbicacionGeografica.getUbicacionesGeografica();
					break;
				}
			}
			
		return parroquia.getUbicacionesGeografica().getUbicacionesGeografica();
	}

	public List<UbicacionesGeografica> getUbicacionesGeograficas() throws Exception {
		List<UbicacionesGeografica> ubicacionesGeograficas = new ArrayList<UbicacionesGeografica>();
		List<ProyectoUbicacionGeografica> ubicaciones = getProyectoUbicacionesGeograficas();
		/*if (isConcesionesMinerasMultiples()) {
			for (ConcesionMinera concesionMinera : concesionesMineras) {
				for (ConcesionMineraUbicacionGeografica concesionMineraUbicacionGeografica : concesionMinera
						.getConcesionesUbicacionesGeograficas()) {
					ubicacionesGeograficas.add(concesionMineraUbicacionGeografica.getUbicacionesGeografica());
				}
			}
		} else {*/
			for (ProyectoUbicacionGeografica proyectoUbicacionGeografica : ubicaciones) {
				ubicacionesGeograficas.add(proyectoUbicacionGeografica.getUbicacionesGeografica());
			}
		//}
		return ubicacionesGeograficas;
	}
	
	
	@Getter
	@Setter
	@Column(name = "pren_removal_vegetation_cover")
	private Boolean remocionCoberturaVegetal;
	
	@Getter
	@Setter
	@Transient
	private Integer numDiasParaDesactivacion;
	
	@Getter
	@Setter
	@Column(name = "pren_intersection_update_status")
	private Integer estadoActualizacionCertInterseccion; //0 No Solicitado, 1 Solicitado, 2 Aprobado, 3 Finalizado
	
	@Getter
	@Setter
	@Column(name = "pren_deactivation_date")
	private Date fechaDesactivacion;
	
	@Getter
	@Setter
	@Column(name = "pren_for_sing_auth")
	private Boolean paraFirmaAutoridad = true;
	
	@Getter
	@Setter
	@Transient
	private Documento resolucion;
	
}