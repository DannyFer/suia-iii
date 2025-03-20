package ec.gob.ambiente.retce.model;

import java.io.Serializable;
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ec.gob.ambiente.suia.domain.Coordenada;
import ec.gob.ambiente.suia.domain.TipoForma;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;



/**
 * The persistent class for the project_information_shapes database table.
 * 
 */
@Entity
@Table(name="project_information_shapes", schema="retce")
@NamedQueries({ @NamedQuery(name = FormaInformacionProyecto.FIND_BY_PROJECT, query = "SELECT o FROM FormaInformacionProyecto o WHERE o.estado=true and o.informacionProyecto.id = :informacionProyecto order by 1 desc") })
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "pris_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "pris_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "pris_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "pris_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "pris_user_update")) })
public class FormaInformacionProyecto extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String FIND_BY_PROJECT = "ec.com.magmasoft.business.domain.FormaInformacionProyecto.byProject";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="pris_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="proj_id")
	private InformacionProyecto informacionProyecto;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="shty_id")
	private TipoForma tipoForma;
	
	@Getter
	@Setter
	@Column(name="pris_format")
	private String formato;
	
	@Getter
	@Setter
	@OneToMany(mappedBy = "formaInformacionProyecto")
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<Coordenada> coordenadas;

	
}