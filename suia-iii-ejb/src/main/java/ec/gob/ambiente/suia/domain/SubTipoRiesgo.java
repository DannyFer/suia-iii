/**
 * Copyright (c) 2014 MAGMASOFT (Innovando tecnologia)
 * Todos los derechos reservados.
 * Este software es confidencial y debe usarlo de acorde con los términos de uso.
 */
package ec.gob.ambiente.suia.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Filter;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import ec.gob.ambiente.suia.domain.enums.TipoRiesgo;

/**
 * Clase que
 * 
 * @author Juan Gabriel Guzmán
 * @version 1.0
 */
@Entity
@Table(name = "risk_subtypes", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "risu_status")) })
@NamedQueries(value = { @NamedQuery(name = SubTipoRiesgo.BUSCAR_POR_TIPO, query = "SELECT s FROM SubTipoRiesgo s WHERE s.tipo = :paramTipo ORDER BY s.nombre") })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "risu_status = 'TRUE'")
public class SubTipoRiesgo extends EntidadBase implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 3290818435979877465L;
	public static final String PAQUETE = "ec.gob.ambiente.suia.domain";
	public static final String BUSCAR_POR_TIPO = PAQUETE + "SubTipoRiesgo.buscarPorTipo";

	@Id
	@SequenceGenerator(name = "RISK_SUBTYPES_GENERATOR", schema = "suia_iii", sequenceName = "seq_risu_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RISK_SUBTYPES_GENERATOR")
	@Column(name = "risu_id")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "risu_type")
	private TipoRiesgo tipo;

	@Getter
	@Setter
	@Column(name = "risu_name", length = 100)
	private String nombre;

	@OneToMany(mappedBy = "subTipo", fetch = FetchType.LAZY)
	@Getter
	@Setter
	private List<Riesgo> riesgos;

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SubTipoRiesgo)) {
			return false;
		}
		SubTipoRiesgo other = (SubTipoRiesgo) obj;
		if (((this.id == null) && (other.id != null)) || ((this.id != null) && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return getNombre();
	}

}
