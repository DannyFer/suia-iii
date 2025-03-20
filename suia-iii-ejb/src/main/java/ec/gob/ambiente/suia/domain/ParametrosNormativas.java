/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;

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
@Table(name = "regulations_parameters", schema = "suia_iii")
@NamedQueries({ @NamedQuery(name = ParametrosNormativas.LISTAR_POR_TABLA, query = "SELECT a FROM ParametrosNormativas a WHERE a.estado = true AND a.tablaNormativas.id = :idTablaNormativa ORDER BY  a.descripcion") })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "repa_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "repa_status = 'TRUE'")
public class ParametrosNormativas extends EntidadBase {

	private static final long serialVersionUID = 273403518780994630L;

    private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
    public static final String LISTAR_POR_TABLA = PAQUETE + "ParametrosNormativas.listarPorTabla";

	@Id
	@SequenceGenerator(name = "REG_PARAMETERS_GENERATOR", schema = "suia_iii", sequenceName = "seq_repa_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REG_PARAMETERS_GENERATOR")
	@Column(name = "repa_id")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@JoinColumn(name = "reta_id", referencedColumnName = "reta_id")
	@ForeignKey(name = "fk_regulations_tables_reta_id_regulations_parameters_repa_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private TablasNormativas tablaNormativas;

	@Getter
	@Setter
	@Column(name = "repa_description")
	private String descripcion;

	@Getter
	@Setter
	@Column(name = "repa_limit_min")
	private Integer limiteInferior;

	@Getter
	@Setter
	@Column(name = "repa_limit_max")
	private Integer limiteSuperior;

	@Getter
	@Setter
	@Column(name = "exceptional_type")
	private boolean tipoExcepcional;

	@Getter
	@Setter
	@Column(name = "exceptional_criteria")
	private String criterioExcepcional;

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.descripcion;
	}



	@Getter
	@Setter
	@Column(name = "repa_unit")
	private String unidad;
}
