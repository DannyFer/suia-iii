package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model;

import java.io.Serializable;
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
import javax.persistence.OrderBy;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name="training_topics", schema="chemical_pesticides")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "trto_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "trto_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "trto_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "trto_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "trto_user_update")) })

@NamedQueries({
@NamedQuery(name="TemaCapacitacion.findAll", query="SELECT t FROM TemaCapacitacion t"),
@NamedQuery(name=TemaCapacitacion.GET_PADRES, query="SELECT t FROM TemaCapacitacion t WHERE temaPadre = null and t.estado = true order by 1")
})

@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "trto_status = 'TRUE'")
public class TemaCapacitacion extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAQUETE = "ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.";
	
	public static final String GET_PADRES = PAQUETE + "TemaCapacitacion.getPadres";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="trto_id")
	private Integer id;

	@Getter
	@Setter
	@JoinColumn(name = "trto_parent_id", referencedColumnName = "trto_id")
	@ManyToOne
	@ForeignKey(name = "fk_trto_id")
	private TemaCapacitacion temaPadre;

	@Getter
	@Setter
	@OneToMany(mappedBy = "temaPadre", fetch = FetchType.EAGER)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "trto_status = 'TRUE'")
	@OrderBy("id ASC")
	private List<TemaCapacitacion> temas;

	@Getter
	@Setter
	@Column(name="trto_description")
	private String descripcion;
	
	@Getter
	@Setter	
	@Column(name="trto_final_node")
	private Boolean nodoFinal;

	@Getter
	@Setter
	@Column(name="trto_unique_code")
	private String codigo;
	
}