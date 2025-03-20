package ec.gob.ambiente.rcoa.viabilidadAmbiental.model;

import java.io.Serializable;
import javax.persistence.*;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;

/**
 * The persistent class for the sample_site_coordinates database table.
 * 
 */
@Entity
@Table(name="sample_site_coordinates", schema = "coa_viability")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "sasc_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "sasc_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "sasc_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "sasc_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "sasc_user_update")) })

@NamedQueries({
@NamedQuery(name="CoordenadaSitioMuestral.findAll", query="SELECT c FROM CoordenadaSitioMuestral c"),
@NamedQuery(name=CoordenadaSitioMuestral.GET_POR_SITIO_ORDEN, query="SELECT c FROM CoordenadaSitioMuestral c where c.sitioMuestral.id = :idSitio and c.estado = true order by orden asc")
})
public class CoordenadaSitioMuestral extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAQUETE = "ec.gob.ambiente.rcoa.viabilidadAmbiental.model.";

	public static final String GET_POR_SITIO_ORDEN = PAQUETE + "CoordenadaSitioMuestral.getPorSitioOrden";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="sasc_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="sasi_id")
	private SitioMuestral sitioMuestral;

	@Getter
	@Setter
	@Column(name="sasc_coordinate_x")
	private Double coordenadaX;

	@Getter
	@Setter
	@Column(name="sasc_coordinate_y")
	private Double coordenadaY;
	
	@Getter
	@Setter
	@Column(name="sasc_description")
	private String descripcion;	

	@Getter
	@Setter
	@Column(name="sasc_order")
	private Integer orden;

}