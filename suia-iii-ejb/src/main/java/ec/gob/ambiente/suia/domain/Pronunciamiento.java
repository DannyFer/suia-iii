/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import ec.gob.ambiente.suia.domain.enums.EstadoPronunciamiento;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 12/01/2015]
 *          </p>
 */
@NamedQueries({
		@NamedQuery(name = Pronunciamiento.FIND_ALL, query = "SELECT p FROM Pronunciamiento p"),
		@NamedQuery(name = Pronunciamiento.FIND_BY_USER_AND_PROCESS, query = "SELECT p FROM Pronunciamiento p WHERE p.usuario.id = :idUsuario AND p.id IN (:identificadores)"),
		@NamedQuery(name = Pronunciamiento.FIND_BY_USER_TYPE_CLASS, query = "SELECT p FROM Pronunciamiento p  WHERE p.usuario.id = :idUsuario AND p.tipo= :tipo AND p.nombreClase = :nombreClase AND p.idClase = :idClase"),
		@NamedQuery(name = Pronunciamiento.FIND_BY_TYPE_CLASS, query = "SELECT p FROM Pronunciamiento p  WHERE p.tipo= :tipo AND p.nombreClase = :nombreClase AND p.idClase = :idClase"),
		@NamedQuery(name = Pronunciamiento.FIND_BY_CLASS, query = "SELECT p FROM Pronunciamiento p  WHERE p.nombreClase = :nombreClase AND p.idClase = :idClase") })
@Entity
@Table(name = "pronouncings", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "pron_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "pron_status = 'TRUE'")
public class Pronunciamiento extends EntidadBase {

	private static final long serialVersionUID = -7060848631817566374L;

	public static final String FIND_ALL = "ec.com.magmasoft.business.domain.Pronunciamiento.findAll";
	public static final String FIND_BY_USER_AND_PROCESS = "ec.com.magmasoft.business.domain.Pronunciamiento.byUserAndProcess";

	public static final String FIND_BY_USER_TYPE_CLASS = "ec.com.magmasoft.business.domain.Pronunciamiento.byUserTypeClass";

	public static final String FIND_BY_TYPE_CLASS = "ec.com.magmasoft.business.domain.Pronunciamiento.byTypeClass";

	public static final String FIND_BY_CLASS = "ec.com.magmasoft.business.domain.Pronunciamiento.byClass";

	@Id
	@SequenceGenerator(name = "PRONOUNCING_GENERATOR", schema = "suia_iii", sequenceName = "seq_pron_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PRONOUNCING_GENERATOR")
	@Column(name = "pron_id")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@Column(name = "pron_cont")
	private String contenido;

	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "pron_date")
	private Date fecha;

	@OneToMany(mappedBy = "pronunciamiento")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prdo_status = 'TRUE'")
	@Getter
	@Setter
	private List<PronunciamientoDocumentos> pronunciamientoDocumentos;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "user_id")
	@ForeignKey(name = "fk_pronouncingspron_id_usersuser_id")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "user_status = 'TRUE'")
	private Usuario usuario;

	//No se usa actualmente
	@Enumerated(EnumType.STRING)
	@Column(name = "pron_pronouncing_status")
	@Getter
	@Setter
	private EstadoPronunciamiento estadoPronunciamiento;

	//No se usa actualmente
	@Getter
	@Setter
	@Column(name = "pron_approved")
	private boolean aprobado = true;

	//No se usa actualmente
	@Getter
	@Setter
	@Column(name = "pron_observations")
	private String observaciones;

	@Getter
	@Setter
	@Column(name = "pron_class_name")
	private String nombreClase;

	@Getter
	@Setter
	@Column(name = "pron_type")
	private String tipo;
	
	@Getter
	@Setter
	@Column(name = "pron_subject")
	private String asunto;

	@Getter
	@Setter
	@Column(name = "pron_tramit_number")
	private String numeroTramite;

	@Getter
	@Setter
	@Column(name = "pron_class_id")
	private long idClase;
	
	
	@Transient
	@Getter
	@Setter
	private String tratamiento;
	
	@Transient
	@Getter
	@Setter
	private String titulo;
	
	@Transient
	@Getter
	@Setter
	private String nombreAutoridad;
	
	@Transient
	@Getter
	@Setter
	private String numeroMemorando;
	
	@Transient
	@Getter
	@Setter
	private String lugarEmision;
	
	@Transient
	@Getter
	@Setter
	private String fechaDocumento;
	
	@Transient
	@Getter
	@Setter
	private String rolAutoridad;
	
	@Transient
	@Getter
	@Setter
	private String codigoProyecto;
	
	@Transient
	@Getter
	@Setter
	private String tratamientoDNF;
	
	@Transient
	@Getter
	@Setter
	private String nombreAutoridadDNF;
	
	@Transient
	@Getter
	@Setter
	private String rolDNF;
	
	@Transient
	@Getter
	@Setter
	private String pronunciamiento;
	
	@Transient
	@Getter
	@Setter
	private String elaboradoTecnico;
	
	@Transient
	@Getter
	@Setter
	private String areaResponsable;
	
	@Transient
	@Getter
	@Setter
	private String memorando;
	
	@Transient
	@Getter
	@Setter
	private String tituloTecnico;
	
	@Transient
	@Getter
	@Setter
	private String tituloDNF;
	
}
