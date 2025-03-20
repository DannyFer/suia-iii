/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.util.List;

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
@Table(name = "regulations_tables", schema = "suia_iii")
@NamedQueries({ @NamedQuery(name = TablasNormativas.LISTAR_POR_NORMATIVA, query = "SELECT a FROM TablasNormativas a WHERE a.estado = true AND a.normativa.id = :idNormativa") })
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "reta_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "reta_status = 'TRUE'")
public class TablasNormativas extends EntidadBase {

	private static final long serialVersionUID = 273403518780994630L;

    private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
    public static final String LISTAR_POR_NORMATIVA = PAQUETE + "TablasNormativas.listarPorNormativa";

	@Id
	@SequenceGenerator(name = "REG_TABLES_GENERATOR", schema = "suia_iii", sequenceName = "seq_reta_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REG_TABLES_GENERATOR")
	@Column(name = "reta_id")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@JoinColumn(name = "regu_id", referencedColumnName = "regu_id")
	@ForeignKey(name = "fk_regulations_regu_id_regulations_tables_regu_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Normativas normativa;

	@Getter
	@Setter
	@OneToMany(mappedBy = "tablaNormativas", fetch = FetchType.LAZY)
	private List<ParametrosNormativas> parametrosNormativas;

	@Getter
	@Setter
	@Column(name = "reta_description")
	private String descripcion;

	@Getter
	@Setter
	@OneToMany(mappedBy = "tablaNormativa", fetch = FetchType.LAZY)
	private List<UsoTablaNormativa> usosTablasNormativas;

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
