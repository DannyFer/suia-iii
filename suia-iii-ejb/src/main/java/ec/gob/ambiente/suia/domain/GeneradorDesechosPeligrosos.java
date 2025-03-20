/*
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * <b> Entity Bean. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: Jun 08, 2015]
 *          </p>
 */
@Entity
@NamedQueries({
		@NamedQuery(name = GeneradorDesechosPeligrosos.GET_BY_CODE, query = "SELECT g FROM GeneradorDesechosPeligrosos g WHERE g.codigo = :codigo AND g.usuario.nombre = :usuario order by g.id"),
		@NamedQuery(name = GeneradorDesechosPeligrosos.GET_BY_SOLICITUD, query = "SELECT g FROM GeneradorDesechosPeligrosos g WHERE g.solicitud = :solicitud order by fechaCreacion desc"),
		@NamedQuery(name = GeneradorDesechosPeligrosos.GET_BY_PROYECTO_LICENCIA_AMBIENTAL, query = "SELECT g FROM GeneradorDesechosPeligrosos g WHERE g.proyecto = :proyecto"),
		@NamedQuery(name = GeneradorDesechosPeligrosos.GET_BY_DESECHO_FINALIZADO, query = "SELECT g FROM GeneradorDesechosPeligrosos g, GeneradorDesechosDesechoPeligroso gdp WHERE gdp.desechoPeligroso.id = g.id AND g.usuario.nombre like :nombreUsuario AND g.finalizado = true AND gdp.desechoPeligroso.id = :idDesecho") })
@Table(name = "hazardous_wastes_generators", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "hwge_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "hwge_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "hwge_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "hwge_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "hwge_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "hwge_status = 'TRUE'")
public class GeneradorDesechosPeligrosos extends EntidadAuditable implements Serializable{

	private static final long serialVersionUID = 1519464152493852222L;

	public static final String VARIABLE_NUMERO_SOLICITUD = "numeroSolicitud";
	public static final String VARIABLE_ID_GENERADOR = "idRegistroGenerador";
	public static final String VARIABLE_ID_GENERADOR_ACCION_GUARDAR = "idRegistroGeneradorAccionGuardar";
	public static final String VARIABLE_RESPONSABILIDAD_EXTENDIDA = "esResponsabilidadExtendida";
	public static final String VARIABLE_APOYO_REQUERIDO = "esApoyoRequerido";
	public static final String VARIABLE_CANTIDAD_OBSERVACIONES = "cantidadObservaciones";

	public static final String GET_BY_CODE = "ec.gob.ambiente.suia.domain.GeneradorDesechosPeligrosos.get_by_code";
	public static final String GET_BY_SOLICITUD = "ec.gob.ambiente.suia.domain.GeneradorDesechosPeligrosos.get_by_solicitud";
	public static final String GET_BY_DESECHO_FINALIZADO = "ec.gob.ambiente.suia.domain.GeneradorDesechosPeligrosos.get_by_desecho_finalizado";
	public static final String GET_BY_PROYECTO_LICENCIA_AMBIENTAL = "ec.gob.ambiente.suia.domain.GeneradorDesechosPeligrosos.get_by_proyecto_licencia_ambiental";

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "HAZARDOUS_WASTES_GENERATORS_HWGE_ID_GENERATOR", sequenceName = "seq_hwge_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "HAZARDOUS_WASTES_GENERATORS_HWGE_ID_GENERATOR")
	@Column(name = "hwge_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "hwge_extended_responsibility")
	private Boolean responsabilidadExtendida;

	@Getter
	@Setter
	@Column(name = "hwge_removal_inside")
	private boolean eliminacionDentroEstablecimiento;

	@Getter
	@Setter
	@Column(name = "hwge_finalized")
	private boolean finalizado;

	@Getter
	@Setter
	@Column(name = "hwge_code")
	private String codigo;

	@Getter
	@Setter
	@Column(name = "hwge_observations")
	private String observaciones;

	@Getter
	@Setter
	@Column(name = "hwge_request")
	private String solicitud;
	
	@Getter
	@Setter
	@Column(name = "hwge_delete_reason")
	private String motivoEliminacion;
	

	@Getter
	@Setter
	@Column(name = "hwge_explanatory_answers")
	private String respuestasAclaratorias;
	
	@Getter
	@Setter
	@Column (name = "hwge_physical")
	private boolean fisico;
	
	@Getter
	@Setter
	@Column (name = "hwge_input_system")
	private Boolean tipoIngreso;
	
	@Getter
	@Setter
	@Column (name = "enaa_id")
	private Integer enaaId;
	
	@Getter
	@Setter
	@Column (name = "hwge_id_history")
	private Integer idPadreHistorial;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "pren_id")
	@ForeignKey(name = "fk_h_w_generatorshwge_id_projectspren_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "pren_status = 'TRUE'")
	private ProyectoLicenciamientoAmbiental proyecto;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "sety_id")
	@ForeignKey(name = "fk_h_w_generatorshwge_id_sector_typessety_id")
	private TipoSector tipoSector;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "wapo_id")
	@ForeignKey(name = "fk_h_w_generatorshwge_id_waste_policieswapo_id")
	private PoliticaDesecho politicaDesecho;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "waac_id")
	@ForeignKey(name = "fk_h_w_generatorshwge_id_waac_id")
	private PoliticaDesechoActividad politicaDesechoActividad;

	@ManyToOne
	@JoinColumn(name = "user_id")
	@ForeignKey(name = "fk_h_w_generatorshwge_id_usershazardous_wastes_generatorsuser_id")
	@Getter
	@Setter
	private Usuario usuario;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "area_id")
	@ForeignKey(name = "fk_hwge_id_area_id")
	private Area areaResponsable;
	
	@Getter
	@Setter
	@Transient
	private String areaAbreviacion;

	@Getter
	@Setter
	@OneToMany(mappedBy = "generadorDesechosPeligrosos")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "hwwd_status = 'TRUE'")
	private List<GeneradorDesechosDesechoPeligroso> generadorDesechosDesechoPeligrosos;

	@Getter
	@Setter
	@OneToMany(mappedBy = "generadorDesechosPeligrosos")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "repo_status = 'TRUE'")
	private List<PuntoRecuperacion> puntosRecuperacion;

	@Getter
	@Setter
	@Transient
	private List<PuntoRecuperacion> puntosRecuperacionActuales;

	@ManyToOne
	@JoinColumn(name = "docu_id")
	@ForeignKey(name = "fk_hwge_id_docu_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	@Getter
	@Setter
	private Documento documentoBorrador;

	@ManyToOne
	@JoinColumn(name = "docu_elim_id")
	@ForeignKey(name = "fk_hwge_id_docu_elim_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "docu_status = 'TRUE'")
	@Getter
	@Setter
	private Documento documentoJustificacionProponente;

	@Getter
	@Setter
	@Column(name = "hwge_justifications")
	private String justificacionProponente;

	@Getter
	@Setter
	@Column (name = "hwge_linkage")
	private Boolean esVinculado;
	
	@Getter
	@Setter
	@Transient
	private String provincia;

	public GeneradorDesechosDesechoPeligroso getFromDesecho(DesechoPeligroso desechoPeligroso) {
		for (GeneradorDesechosDesechoPeligroso generadorDesechosDesechoPeligroso : generadorDesechosDesechoPeligrosos) {
			if (generadorDesechosDesechoPeligroso.getDesechoPeligroso().equals(desechoPeligroso))
				return generadorDesechosDesechoPeligroso;
		}
		return null;
	}

	public List<DesechoPeligroso> getDesechosPeligrosos() {
		List<DesechoPeligroso> desechos = new ArrayList<DesechoPeligroso>();
		for (GeneradorDesechosDesechoPeligroso desecho : generadorDesechosDesechoPeligrosos)
			desechos.add(desecho.getDesechoPeligroso());
		return desechos;
	}

	public List<GeneradorDesechosDesechoPeligrosoDatosGenerales> getDatosGenerales() {
		List<GeneradorDesechosDesechoPeligrosoDatosGenerales> datos = new ArrayList<GeneradorDesechosDesechoPeligrosoDatosGenerales>();
		for (GeneradorDesechosDesechoPeligroso desecho : generadorDesechosDesechoPeligrosos) {
			if (desecho.getGeneradorDesechosDesechoPeligrosoDatosGenerales() != null) {
				desecho.getGeneradorDesechosDesechoPeligrosoDatosGenerales().setDesechoPeligroso(
						desecho.getDesechoPeligroso());
				datos.add(desecho.getGeneradorDesechosDesechoPeligrosoDatosGenerales());
			}
		}
		return datos;
	}

	public List<GeneradorDesechosDesechoPeligrosoEtiquetado> getEnvasadosEtiquetados() {
		List<GeneradorDesechosDesechoPeligrosoEtiquetado> envasados = new ArrayList<GeneradorDesechosDesechoPeligrosoEtiquetado>();
		for (GeneradorDesechosDesechoPeligroso desecho : generadorDesechosDesechoPeligrosos) {
			if (desecho.getGeneradorDesechosDesechoPeligrosoEtiquetado() != null) {
				desecho.getGeneradorDesechosDesechoPeligrosoEtiquetado().setDesechoPeligroso(
						desecho.getDesechoPeligroso());
				envasados.add(desecho.getGeneradorDesechosDesechoPeligrosoEtiquetado());
			}
		}
		return envasados;
	}

	public Map<DesechoPeligroso, List<IncompatibilidadDesechoPeligroso>> getIncompatibilidades() {
		Map<DesechoPeligroso, List<IncompatibilidadDesechoPeligroso>> incompatibilidades = new HashMap<DesechoPeligroso, List<IncompatibilidadDesechoPeligroso>>();
		for (GeneradorDesechosDesechoPeligroso desecho : generadorDesechosDesechoPeligrosos)
			incompatibilidades.put(desecho.getDesechoPeligroso(), desecho.getIncompatibilidadesDesechoPeligro());
		return incompatibilidades;
	}

	public List<AlmacenGeneradorDesechos> getAlmacenes() {
		List<AlmacenGeneradorDesechos> almacenes = new ArrayList<AlmacenGeneradorDesechos>();
		for (GeneradorDesechosDesechoPeligroso desecho : generadorDesechosDesechoPeligrosos) {
			if (desecho.getAlmacenGeneradorDesechoPeligrosos() != null) {
				for (AlmacenGeneradorDesechoPeligroso almacen : desecho.getAlmacenGeneradorDesechoPeligrosos()) {
					AlmacenGeneradorDesechos almacenDesechos = almacen.getAlmacenGeneradorDesechos();
					if (!almacenes.contains(almacenDesechos) && almacenDesechos.getEstado())
						almacenes.add(almacenDesechos);
				}
			}
		}
		return almacenes;
	}

	public List<PuntoEliminacion> getPuntosEliminacion() {
		List<PuntoEliminacion> eliminadores = new ArrayList<PuntoEliminacion>();
		for (GeneradorDesechosDesechoPeligroso desecho : generadorDesechosDesechoPeligrosos) {
			if (desecho.getPuntosEliminacion() != null) {
				for (PuntoEliminacion eliminador : desecho.getPuntosEliminacion()) {
					if (!eliminadores.contains(eliminador)) {
						eliminador.setDesechoPeligroso(desecho.getDesechoPeligroso());
						eliminadores.add(eliminador);
					}
				}
			}
		}
		return eliminadores;
	}

	public List<GeneradorDesechosRecolector> getGeneradoresDesechosRecolectores() {
		List<GeneradorDesechosRecolector> recolectores = new ArrayList<GeneradorDesechosRecolector>();
		for (GeneradorDesechosDesechoPeligroso desecho : generadorDesechosDesechoPeligrosos) {
			if (desecho.getGeneradoresDesechosRecolectores() != null) {
				for (GeneradorDesechosRecolector recolector : desecho.getGeneradoresDesechosRecolectores()) {
					if (!recolectores.contains(recolector)) {
						recolector.setDesechoPeligroso(desecho.getDesechoPeligroso());
						recolectores.add(recolector);
					}
				}
			}
		}
		return recolectores;
	}

	public List<GeneradorDesechosEliminador> getGeneradoresDesechosEliminadores() {
		List<GeneradorDesechosEliminador> eliminadores = new ArrayList<GeneradorDesechosEliminador>();
		for (GeneradorDesechosDesechoPeligroso desecho : generadorDesechosDesechoPeligrosos) {
			if (desecho.getGeneradoresDesechosEliminadores() != null) {
				for (GeneradorDesechosEliminador eliminador : desecho.getGeneradoresDesechosEliminadores()) {
					if (!eliminadores.contains(eliminador)) {
						eliminador.setDesechoPeligroso(desecho.getDesechoPeligroso());
						eliminadores.add(eliminador);
					}
				}
			}
		}
		return eliminadores;
	}

	@Getter
	@Setter
	@Transient
	private String nombreReporte;

	@Getter
	@Setter
	@Transient
	private String nombreFichero;

	@Getter
	@Setter
	@Transient
	private String registroPath;

	@Getter
	@Setter
	@Transient
	private byte[] archivoGenerador;

	@Getter
	@Setter
	@Transient
	private String fecha;

	@Getter
	@Setter
	@Transient
	private String empresa;

	@Getter
	@Setter
	@Transient
	private String cargo;

	@Getter
	@Setter
	@Transient
	private String responsable;

	@Getter
	@Setter
	@Transient
	private String campoCargoResponsable;

	@Getter
	@Setter
	@Transient
	private String cargoResponsable;

	@Getter
	@Setter
	@Transient
	private String direccion;

	@Getter
	@Setter
	@Transient
	private String campoSeLeOtorga;

	@Getter
	@Setter
	@Transient
	private String numeroRegistro;

	@Getter
	@Setter
	@Transient
	private String tablaDesechos;

	@Getter
	@Setter
	@Transient
	private String tablaInstalaciones;

	@Getter
	@Setter
	@Transient
	private String normativaLegal;

	@Getter
	@Setter
	@Transient
	private String autoridad;

	@Getter
	@Setter
	@Transient
	private String cargoAutoridad;

}
