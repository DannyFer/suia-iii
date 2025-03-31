package ec.gob.ambiente.rcoa.viabilidadAmbiental.model;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

import java.util.List;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the titles database table.
 * 
 */
@Entity
@Table(name="titles", schema = "coa_viability")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "titl_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "titl_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "titl_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "titl_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "titl_user_update")) })

@NamedQueries({
@NamedQuery(name="CabeceraFormulario.findAll", query="SELECT c FROM CabeceraFormulario c"),
@NamedQuery(name=CabeceraFormulario.GET_LISTA_PREGUNTAS_POR_ORDEN_TIPO, query="SELECT c FROM CabeceraFormulario c where c.orden = :orden and c.tipo = :tipo and c.estado = true"),
@NamedQuery(name=CabeceraFormulario.GET_LISTA_PREGUNTAS_POR_TIPO, query="SELECT c FROM CabeceraFormulario c where c.tipo = :tipo and c.estado = true order by c.orden") })

public class CabeceraFormulario extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final String PAQUETE = "ec.gob.ambiente.rcoa.viabilidadAmbiental.model.";
	
	public static final String GET_LISTA_PREGUNTAS_POR_ORDEN_TIPO = PAQUETE + "CabeceraFormulario.getListaPreguntasPorOrdenTipo";
	public static final String GET_LISTA_PREGUNTAS_POR_TIPO = PAQUETE + "CabeceraFormulario.getListaPreguntasPorTipo";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="titl_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="titl_description")
	private String descripcion;
	
	@Getter
	@Setter
	@Column(name="titl_sub_description")
	private String subDescripcion;

	@Getter
	@Setter
	@Column(name="titl_observation_bd")
	private String observacionBd;

	@Getter
	@Setter
	@Column(name="titl_order")
	private Integer orden;

	@Getter
	@Setter
	@Column(name="titl_type")
	private Integer tipo;

	@Getter
	@Setter
	@OneToMany(mappedBy = "idCabecera")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "ques_status = 'TRUE'")
	@OrderBy("ques_order")
	private List<PreguntaFormulario> listaPreguntas;
}