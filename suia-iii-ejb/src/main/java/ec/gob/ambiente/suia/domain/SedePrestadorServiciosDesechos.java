/*
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * <b> Entity Bean. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: Jun 10, 2015]
 *          </p>
 */
@Entity
@Table(name = "hazardous_wastes_service_provider_locations", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "hwpl_status")) })
@NamedQueries({
		@NamedQuery(name = SedePrestadorServiciosDesechos.FILTRAR_POR_FASE_GESTION_DESECHO, query = "SELECT distinct s FROM SedePrestadorServiciosDesechos s,PrestadorServiciosDesechos ps,PrestadorServiciosDesechoPeligroso p where (p.tipoEliminacionDesecho.id = :idTipoEliminacion or p.tipoEliminacionDesecho.clave = 'N/A') and s.prestadorServiciosDesechos.id = ps.id and p.sedePrestadorServiciosDesechos.id = s.id and p.faseGestionDesecho.id IN (:idFasesGestion) and p.desechoPeligroso.id = :idDesecho and (ps.ruc = :ruc or s.prestadorPublico = true)"),
		@NamedQuery(name = SedePrestadorServiciosDesechos.FILTRAR_POR_FASE_GESTION_DESECHO_Y_FILTRO, query = "SELECT distinct s FROM SedePrestadorServiciosDesechos s,PrestadorServiciosDesechos ps,PrestadorServiciosDesechoPeligroso p where (p.tipoEliminacionDesecho.id = :idTipoEliminacion or p.tipoEliminacionDesecho.clave = 'N/A') and s.prestadorServiciosDesechos.id = ps.id and p.sedePrestadorServiciosDesechos.id = s.id and p.faseGestionDesecho.id IN (:idFasesGestion) and p.desechoPeligroso.id = :idDesecho and (ps.ruc = :ruc or s.prestadorPublico = true) and (lower(ps.nombre) LIKE :filter)"),
		@NamedQuery(name = SedePrestadorServiciosDesechos.FILTRAR_POR_FASE_GESTION, query = "SELECT distinct s FROM SedePrestadorServiciosDesechos s,PrestadorServiciosDesechos ps,PrestadorServiciosDesechoPeligroso p where s.prestadorServiciosDesechos.id = ps.id and p.sedePrestadorServiciosDesechos.id = s.id and p.faseGestionDesecho.id IN (:idFasesGestion) and s.prestadorPublico = true"),
		@NamedQuery(name = SedePrestadorServiciosDesechos.LISTAR_TODAS_SEDES, query = "SELECT distinct s FROM SedePrestadorServiciosDesechos s,PrestadorServiciosDesechos ps where s.prestadorServiciosDesechos.id = ps.id and s.prestadorPublico = true")})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "hwpl_status = 'TRUE'")
public class SedePrestadorServiciosDesechos extends EntidadBase {

	private static final long serialVersionUID = -1204838775383125884L;

	public static final String FILTRAR_POR_FASE_GESTION_DESECHO = "ec.gob.ambiente.suia.domain.SedePrestadorServiciosDesechos.FiltrarPorFaseGestionDesecho";
	public static final String FILTRAR_POR_FASE_GESTION_DESECHO_Y_FILTRO = "ec.gob.ambiente.suia.domain.SedePrestadorServiciosDesechos.FiltrarPorFaseGestionDesechoYFiltro";
	public static final String FILTRAR_POR_FASE_GESTION = "ec.gob.ambiente.suia.domain.SedePrestadorServiciosDesechos.FiltrarPorFaseGestion";
	public static final String LISTAR_TODAS_SEDES = "ec.gob.ambiente.suia.domain.SedePrestadorServiciosDesechos.ListarTodasSedes";

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "HAZARDOUS_WASTES_SERVICE_PROVIDER_LOCATION_HWPL_ID_GENERATOR", sequenceName = "seq_hwpl_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "HAZARDOUS_WASTES_SERVICE_PROVIDER_LOCATION_HWPL_ID_GENERATOR")
	@Column(name = "hwpl_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name = "hwpl_project_name")
	private String nombreProyecto;

	@Getter
	@Setter
	@Column(name = "hwpl_instalations_address")
	private String direccionInstalaciones;

	@Getter
	@Setter
	@Column(name = "hwpl_office_address")
	private String direccionOficina;

	@Getter
	@Setter
	@Column(name = "hwpl_legal_representative")
	private String representanteLegal;

	@Getter
	@Setter
	@Column(name = "hwpl_environmental_permit_code")
	private String codigoPermisoAmbiental;

	@Getter
	@Setter
	@Column(name = "hwpl_anual_capacity_tons")
	private double capacidadGestionAnualToneladas;

	@Getter
	@Setter
	@Column(name = "hwpl_anual_capacity_units")
	private double capacidadGestionAnualUnidades;

	@Getter
	@Setter
	@Column(name = "hwpl_public_provider")
	private boolean prestadorPublico;

	@Getter
	@Setter
	@Column(name = "hwpl_environmental_permit_date")
	@Temporal(TemporalType.DATE)
	private Date fechaPermisoAmbiental;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "epty_id")
	@ForeignKey(name = "fk_hwpl_id_epty_id")
	private TipoPermisoAmbiental tipoPermisoAmbiental;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "gelo_id")
	@ForeignKey(name = "fk_hwpl_id_gelo_id")
	private UbicacionesGeografica ubicacionesGeografica;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "area_id")
	@ForeignKey(name = "fk_hwpl_id_area_id")
	private Area areaResponsable;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "hwsp_id")
	@ForeignKey(name = "fk_hwpl_id_hwsp_id")
	private PrestadorServiciosDesechos prestadorServiciosDesechos;

	@Getter
	@Setter
	@OneToMany(cascade = CascadeType.PERSIST, mappedBy = "sedePrestadorServiciosDesechos")
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<Contacto> contactos;

	@Getter
	@Setter
	@OneToMany(mappedBy = "sedePrestadorServiciosDesechos")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "hwpw_status = 'TRUE'")
	private List<PrestadorServiciosDesechoPeligroso> prestadorServiciosDesechoPeligrosos;
}
