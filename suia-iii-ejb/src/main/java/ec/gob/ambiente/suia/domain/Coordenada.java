/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.retce.model.FormaInformacionProyecto;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: 23/01/2015]
 *          </p>
 */
@Entity
@Table(name = "coordinates", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "coor_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "coor_status = 'TRUE'")
public class Coordenada extends EntidadBase implements Comparable<Coordenada> {

	private static final long serialVersionUID = 7190290069033775093L;

	@Id
	@Column(name = "coor_id")
	@SequenceGenerator(name = "COORDINATES_COOR_ID_GENERATOR", sequenceName = "seq_coor_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COORDINATES_COOR_ID_GENERATOR")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@Column(name = "coor_order")
	private Integer orden;

	@Getter
	@Setter
	@Column(name = "coor_x")
	private Double x;

	@Getter
	@Setter
	@Column(name = "coor_y")
	private Double y;

	@Getter
	@Setter
	@Column(name = "coor_description")
	private String descripcion;

	//aqui
	@Getter
	@Setter
	@Column(name = "coor_zone")
	private String zona;
	//fin aqui
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "prsh_id")
	private FormaProyecto formasProyecto;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "cooe_id")
	private FormaCoordenadasEIA formasCoordinatesEIA;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "rpsh_id")
	private FormaPuntoRecuperacion formasPuntoRecuperacion;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "crfs_id")
	private CertificadoAmbientalInformeForestalFormaCoordenada formasInformeForestalCA;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "pris_id")
	private FormaInformacionProyecto formaInformacionProyecto;

	public boolean isDataComplete() {
		return (orden != null && x != null && y != null);
	}

	public boolean isEqualsPoints(Object obj) {
		Coordenada coordenada = (Coordenada) obj;
		return coordenada.getX().equals(getX()) && coordenada.getY().equals(getY());
	}

	@Override
	public int compareTo(Coordenada o) {
		if (this.getOrden() < o.getOrden())
			return -1;
		if (this.getOrden() > o.getOrden())
			return 1;
		return 0;
	}

	/**
	 * SE INVOCA PARA LA TRANSFORMACION
	 */
	@Override
	public String toString() {
		return x + " " + y;
	}
	
	@Getter
	@Setter
	@Column(name = "coor_update_number")
	private Integer nroActualizacion = 0;
	
	@Getter
	@Setter
	@Column(name = "prsh_type")
	private int tipoCoordenada;
	
	@Getter
	@Setter
	@Column(name = "coor_geographic_area")
	private Integer areaGeografica;
	
}
