/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 *
 * @author oscar campana
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Oscar Campana, Fecha: 27/07/2015]
 *          </p>
 */
@Entity
@Table(name = "monitoring_plan_parameters", schema = "suia_iii")
@NamedQueries({ @NamedQuery(name = ParametrosPlanMonitoreo.LISTAR_POR_TABLA, query = "SELECT a FROM ParametrosPlanMonitoreo a WHERE a.estado = true AND a.tablasPlanMonitoreo.id = :idTablaMonitoreo") })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "mopp_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "mopp_status = 'TRUE'")
public class ParametrosPlanMonitoreo extends EntidadBase {

	private static final long serialVersionUID = 273403518780994630L;

    private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
    public static final String LISTAR_POR_TABLA = PAQUETE + "ParametrosPlanMonitoreo.listarPorTabla";

	@Id
	@SequenceGenerator(name = "PLAN_PARAMETERS_GENERATOR", schema = "suia_iii", sequenceName = "seq_mopp_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PLAN_PARAMETERS_GENERATOR")
	@Column(name = "mopp_id")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@JoinColumn(name = "mota_id", referencedColumnName = "mota_id")
	@ForeignKey(name = "fk_monitoring_tables_mota_id_monitoring_plan_parameters_mota_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private TablasPlanMonitoreo tablasPlanMonitoreo;


	@Getter
	@Setter
	@Column(name = "mopp_description")
	private String descripcion;

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.descripcion;
	}
}
