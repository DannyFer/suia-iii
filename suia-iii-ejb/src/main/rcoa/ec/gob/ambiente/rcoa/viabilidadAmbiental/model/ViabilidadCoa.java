package ec.gob.ambiente.rcoa.viabilidadAmbiental.model;

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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.rcoa.model.AreasSnapProvincia;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;


/**
 * The persistent class for the project_viability_coa database table.
 * 
 */
@Entity
@Table(name="project_viability_coa", schema = "coa_viability")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "prvi_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "prvi_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "prvi_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "prvi_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "prvi_user_update")) })

@NamedQueries({
@NamedQuery(name="ViabilidadCoa.findAll", query="SELECT v FROM ViabilidadCoa v"),
@NamedQuery(name=ViabilidadCoa.GET_POR_PROYECTO, query="SELECT v FROM ViabilidadCoa v where v.idProyectoLicencia = :proyecto and v.estado = true"),
@NamedQuery(name=ViabilidadCoa.GET_FORESTAL_POR_PROYECTO, query="SELECT v FROM ViabilidadCoa v where v.idProyectoLicencia = :proyecto and v.esViabilidadSnap = false and v.estado = true"),
@NamedQuery(name=ViabilidadCoa.GET_SNAP_POR_PROYECTO_TIPO, query="SELECT v FROM ViabilidadCoa v where v.idProyectoLicencia = :proyecto and v.esViabilidadSnap = true and v.esAdministracionMae = :esAdminMae and v.estado = true"),
@NamedQuery(name=ViabilidadCoa.GET_POR_TIPO_PROYECTO, query="SELECT v FROM ViabilidadCoa v where v.idProyectoTipoViabilidad = :tipoProyecto and v.estado = true"),
@NamedQuery(name=ViabilidadCoa.GET_SNAP_POR_PROYECTO, query="SELECT v FROM ViabilidadCoa v where v.idProyectoLicencia = :proyecto and v.esViabilidadSnap = true and v.estado = true") })
public class ViabilidadCoa extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final String PAQUETE = "ec.gob.ambiente.rcoa.viabilidadAmbiental.model.";
	
	public static final String GET_POR_PROYECTO = PAQUETE + "ViabilidadCoa.getPorProyecto";
	public static final String GET_FORESTAL_POR_PROYECTO = PAQUETE + "ViabilidadCoa.getForestalPorProyecto";
	public static final String GET_SNAP_POR_PROYECTO_TIPO = PAQUETE + "ViabilidadCoa.getSnapPorProyectoTipo";
	public static final String GET_POR_TIPO_PROYECTO = PAQUETE + "ViabilidadCoa.getPorTipoProyecto";
	public static final String GET_SNAP_POR_PROYECTO = PAQUETE + "ViabilidadCoa.getSnapPorProyecto";
	
	public static final String flujoOriginal = "1";
	public static final String flujoBypass = "2";
	public static final String flujosIndependientes = "3";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="prvi_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="prco_id")
	private Integer idProyectoLicencia;
	
	@Getter
	@Setter
	@Column(name="prtv_id")
	private Integer idProyectoTipoViabilidad;

	@Getter
	@Setter
	@Column(name="prvi_conflict_detail")
	private String detalleConflictoLegal;

	@Getter
	@Setter
	@Column(name="prvi_legal_conflict")
	private Boolean existeConflictoLegal;

	@Getter
	@Setter
	@Column(name="prvi_observation_bd")
	private String observacionBd;

	@Getter
	@Setter
	@Column(name="prvi_technical_inspection")
	private Boolean requiereInspeccionTecnica;

	@Getter
	@Setter
	@Column(name="prvi_type")
	private Boolean esViabilidadSnap;
	
	@Getter
	@Setter
	@Column(name="prvi_is_managed_mae")
	private Boolean esAdministracionMae;
	
	@Getter
	@Setter
	@Column(name="prvi_viability_completed")
	private Boolean viabilidadCompletada;
	
	@Getter
	@Setter
	@Column(name="prvi_viability_favorable")
	private Boolean esPronunciamientoFavorable;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "area_id")
	@ForeignKey(name = "area_id")
	private Area areaResponsable;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "prar_id")
	@ForeignKey(name = "prar_id")
	private AreasSnapProvincia areaSnap;
	
	@Getter
	@Setter
	@Column(name="prvi_type_register_entry")
	private String tipoFlujoViabilidad = flujoOriginal; //1 = Biodiversidad y forestal	2 = Bypass	3 = Biodiversidad y forestal Independiente
	
	@Getter
	@Setter
	@Column(name="prvi_explanatory_information_status")
	private Boolean requiereAclaratoria;
	
	@Getter
	@Setter
	@Column(name="prvi_explanatory_information_detailed")
	private String detalleAclaratoria;

	@Getter
	@Setter
	@Column(name="prvi_requires_support")
	private Boolean requiereApoyo;

	@Getter
	@Setter
	@Column(name="prvi_support_requirement_detail")
	private String detalleApoyo;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "area_id_support")
	@ForeignKey(name = "fk_area_id_support")
	private Area areaApoyo;

	@Getter
	@Setter
	@Column(name="prvi_intersection_zones_number")
	private Integer nroZonalesInterseccion;
	
	@Getter
	@Setter
	@Transient
	private String respuestasOperadorHtml;
	
}