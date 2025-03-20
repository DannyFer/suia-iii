/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 04/01/2015]
 *          </p>
 */
@Entity
@Table(name = "monitoring_points", catalog = "", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "mopo_status")) })
@Inheritance(strategy = InheritanceType.JOINED)
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "mopo_status = 'TRUE'")
public class PuntoMonitoreo extends EntidadBase {

	private static final long serialVersionUID = 2569836553648148262L;

	@Id
	@SequenceGenerator(name = "MONITORING_POINTS_GENERATOR", schema = "suia_iii", sequenceName = "seq_mopo_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MONITORING_POINTS_GENERATOR")
	@Column(name = "mopo_id")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@Column(name = "mopo_name")
	private String nombre;

	@Getter
	@Setter
	@Column(name = "mopo_utm_coordinate_x")
	private float coordenadaUtmX;

	@Getter
	@Setter
	@Column(name = "mopo_utm_coordinate_y")
	private float coordenadaUtmY;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "user_id")
	@ForeignKey(name = "fk_monitoring_pointsmopo_id_usersuser_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "user_status = 'TRUE'")
	private Usuario usuario;

	public static enum EstadoPuntoMonitoreo {
		APROBADO, APROBADO_SIN_VERIFICAR, PENDIENTE_APROBACION, RECIEN_INSERTADO;
	}

	@Enumerated(EnumType.STRING)
	@Getter
	@Setter
	private EstadoPuntoMonitoreo estadoPuntoMonitoreo;

	@Getter
	@Setter
	@Column(name = "mopo_posts_number")
	private String numeroOficio;

	@Getter
	@Setter
	@Column(name = "mopo_monitoring_start_date")
	private String fechaInicioMonitoreo;

	@Getter
	@Setter
	@Column(name = "mopo_approved")
	private boolean aprobado = false;
}
