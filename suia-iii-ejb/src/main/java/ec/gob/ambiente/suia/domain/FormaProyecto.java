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
 * @author Jonathan Guerrero
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Jonathan Guerrero, Fecha: Jan 28, 2015]
 *          </p>
 */
@Entity
@Table(name = "project_shapes", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "prsh_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prsh_status = 'TRUE'")
public class FormaProyecto extends EntidadBase implements CoordinatesContainer {

	private static final long serialVersionUID = -8848052297710885892L;

	public static final String FIND_ALL = "ec.com.magmasoft.business.domain.FormasProyecto.findAll";

	@Getter
	@Setter
	@Id
	@SequenceGenerator(name = "PROJECT_SHAPES_GENERATOR", initialValue = 1, sequenceName = "seq_prsh_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROJECT_SHAPES_GENERATOR")
	@Column(name = "prsh_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "shty_id")
	private TipoForma tipoForma;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "pren_id")
	private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental;

	@Getter
	@Setter
	@Column(name = "pren_id", insertable=false, updatable=false)
	private Integer idProyecto;
	
	@Getter
	@Setter
	@OneToMany(mappedBy = "formasProyecto")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "coor_update_number = '0'")
	private List<Coordenada> coordenadas;
	
	@Getter
	@Setter
	@Column(name = "prsh_update_number")
	private Integer nroActualizacion = 0;
	
	@Getter
	@Setter
	@Column(name = "prsh_type")
	private int tipoCoordenada;
	
	@Getter
	@Setter
	@Column(name = "id_4_cat")
	private String idCuatroCategorias;

	public FormaProyecto() {
		coordenadas = new ArrayList<Coordenada>();
	}
}
