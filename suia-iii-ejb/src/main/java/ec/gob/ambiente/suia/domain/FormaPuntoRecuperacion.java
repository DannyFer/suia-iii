/*
 * Copyright 2014 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import ec.gob.ambiente.suia.domain.interfaces.CoordinatesContainer;

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
@Table(name = "recovery_point_shapes", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "rpsh_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "rpsh_status = 'TRUE'")
public class FormaPuntoRecuperacion extends EntidadBase implements CoordinatesContainer {

	private static final long serialVersionUID = 3200901777244850048L;

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "RECOVERY_POINT_SHAPES_GENERATOR", initialValue = 1, sequenceName = "seq_rpsh_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RECOVERY_POINT_SHAPES_GENERATOR")
	@Column(name = "rpsh_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "shty_id")
	private TipoForma tipoForma;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "repo_id")
	private PuntoRecuperacion puntoRecuperacion;

	@Getter
	@Setter
	@OneToMany(mappedBy = "formasPuntoRecuperacion")
	@OrderBy("orden ASC")
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<Coordenada> coordenadas;

	public FormaPuntoRecuperacion() {
		coordenadas = new ArrayList<Coordenada>();
	}
}
