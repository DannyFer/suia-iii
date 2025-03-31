/*
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.domain;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

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
@Table(name = "h_w_service_providers_wastes", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "hwpw_status")) })
@NamedQueries({
		@NamedQuery(name = PrestadorServiciosDesechoPeligroso.FILTRAR_POR_FASE_TIPO_DESECHO_RUC_UBICACION, query = "SELECT p FROM PrestadorServiciosDesechoPeligroso p,SedePrestadorServiciosDesechos s,PrestadorServiciosDesechos ps where s.id = p.sedePrestadorServiciosDesechos.id and s.prestadorServiciosDesechos.id = ps.id and p.faseGestionDesecho.id IN (:idFasesGestion) and p.desechoPeligroso.id = :idDesecho and ps.ruc = :ruc and s.ubicacionesGeografica.id IN (:idUbicaciones)"),
		@NamedQuery(name = PrestadorServiciosDesechoPeligroso.FILTRAR_POR_FASE_TIPO_DESECHO_RUC_UBICACION_TIPO_ELIMINACION, query = "SELECT p FROM PrestadorServiciosDesechoPeligroso p,SedePrestadorServiciosDesechos s,PrestadorServiciosDesechos ps where s.id = p.sedePrestadorServiciosDesechos.id and s.prestadorServiciosDesechos.id = ps.id and p.faseGestionDesecho.id IN (:idFasesGestion) and p.desechoPeligroso.id = :idDesecho and ps.ruc = :ruc and p.tipoEliminacionDesecho.id = :idTipoEliminacion and s.ubicacionesGeografica.id IN (:idUbicaciones)") })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "hwpw_status = 'TRUE'")
public class PrestadorServiciosDesechoPeligroso extends EntidadBase {

	private static final long serialVersionUID = -4835123531950575014L;

	public static final String FILTRAR_POR_FASE_TIPO_DESECHO_RUC_UBICACION = "ec.gob.ambiente.suia.domain.PrestadorServiciosDesechoPeligroso.FiltrarPorFaseTipoDesechoRucUbicacion";
	public static final String FILTRAR_POR_FASE_TIPO_DESECHO_RUC_UBICACION_TIPO_ELIMINACION = "ec.gob.ambiente.suia.domain.PrestadorServiciosDesechoPeligroso.FiltrarPorFaseTipoDesechoRucUbicacionTipoEliminacion";

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "H_W_SERVICE_PROVIDER_WASTES_HWPW_ID_GENERATOR", sequenceName = "seq_hwpw_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "H_W_SERVICE_PROVIDER_WASTES_HWPW_ID_GENERATOR")
	@Column(name = "hwpw_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "wdty_id")
	@ForeignKey(name = "fk_hwpw_id_wdty_id")
	private TipoEliminacionDesecho tipoEliminacionDesecho;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "wmph_id")
	@ForeignKey(name = "fk_hwpw_id_wmph_id")
	private FaseGestionDesecho faseGestionDesecho;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "wada_id")
	@ForeignKey(name = "fk_hwpw_id_wada_id")
	private DesechoPeligroso desechoPeligroso;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "hwpl_id")
	@ForeignKey(name = "fk_hwpw_id_hwpl_id")
	private SedePrestadorServiciosDesechos sedePrestadorServiciosDesechos;
}
