/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.domain;

import java.util.List;

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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

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
@Table(name = "regulations", schema = "suia_iii")
@NamedQueries({ @NamedQuery(name = Normativas.LISTAR, query = "SELECT a FROM Normativas a WHERE a.estado = true and a.sector.id = :sector and a.tipoProyecto.id = :tipoProyecto"),
		@NamedQuery(name = Normativas.LISTARNORMATIVAXCOMPONENTE, query = "SELECT a FROM Normativas a WHERE a.estado = true and a.sector.id = :sector and a.tipoProyecto.id = :tipoProyecto and a.componenteFisico = :componente"),
	@NamedQuery(name = Normativas.LISTARCOMPONENTE, query = "SELECT a FROM Normativas a WHERE a.estado = true and a.sector.id = :sector and a.tipoProyecto.id = :tipoProyecto and a.descripcion =:descripcion")  }
		)
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "regu_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "regu_status = 'TRUE'")
public class Normativas extends EntidadBase {

	private static final long serialVersionUID = 273403518787904630L;

	private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
	public static final String LISTAR = PAQUETE + "Normativas.listar";

	public static final String LISTARCOMPONENTE = PAQUETE + "Normativas.listarcomponente";

	public static final String LISTARNORMATIVAXCOMPONENTE = PAQUETE + "Normativas.listarnormativaxcomponente";

	@Id
	@SequenceGenerator(name = "REGULATIONS_GENERATOR", schema = "suia_iii", sequenceName = "seq_regu_id", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REGULATIONS_GENERATOR")
	@Column(name = "regu_id")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@OneToMany(mappedBy = "normativa", fetch = FetchType.LAZY)
	private List<TablasNormativas> tablasNormativas;

	@Getter
	@Setter
	@Column(name = "regu_description")
	private String descripcion;

	@Getter
	@Setter
	@JoinColumn(name = "regu_sector", referencedColumnName = "sety_id")
	@ForeignKey(name = "fk_sector_types_sety_id_regulations_regu_sector")
	@ManyToOne(fetch = FetchType.LAZY)
	private TipoSector sector;

	@Getter
	@Setter
	@Column(name = "regu_component")
	private String componenteFisico;

	@Getter
	@Setter
	@JoinColumn(name = "regu_project_type", referencedColumnName = "stty_id")
	@ForeignKey(name = "fk_study_types_stty_id_regulations_regu_project_type")
	@ManyToOne(fetch = FetchType.LAZY)
	private TipoEstudio tipoProyecto;

	@Transient
	@Getter
	@Setter
	private String etiqueta;

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.descripcion+componenteFisico;
	}
}
