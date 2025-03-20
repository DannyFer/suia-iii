/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;

import javax.persistence.*;
import java.util.List;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author martinvc
 * @version Revision: 1.0
 *          <p>
 *          [Autor: martinvc, Fecha: 26/06/2015]
 *          </p>
 */
@Entity
@Table(name = "participation_component_catalog", catalog = "", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "pacc_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "pacc_status = 'TRUE'")
public class CatalogoComponenteParticipacion extends EntidadBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3204053949319999533L;

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "PARTICIPATION_COMPONET_CATALOGID_GENERATOR", sequenceName = "seq_pacc_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PARTICIPATION_COMPONET_CATALOGID_GENERATOR")
	@Column(name = "pacc_id", unique = true, nullable = false)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "pacc_name", length = 400)
	private String nombre;

	@Getter
	@Setter
	@Column(name = "pacc_description", length = 1000)
	private String descripcion;

	@Getter
	@Setter
	@OneToMany(mappedBy = "componentePaticipacion", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<ConsultorNoCalificado> consultorNoCalificadoList;

	@Getter
	@Setter
	@Column(name = "pacc_weight")
	private Integer peso;

	@Override
	public String toString() {
		return this.nombre;
	}
}
