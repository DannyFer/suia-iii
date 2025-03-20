package ec.gob.ambiente.rcoa.viabilidadAmbiental.model;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;

/**
 * The persistent class for the sample_site_location database table.
 * 
 */
@Entity
@Table(name="sample_site_location", schema = "coa_viability")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "sasl_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "sasl_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "sasl_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "sasl_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "sasl_user_update")) })

@NamedQueries({
@NamedQuery(name="UbicacionSitioMuestral.findAll", query="SELECT u FROM UbicacionSitioMuestral u"),
@NamedQuery(name=UbicacionSitioMuestral.GET_LISTA_POR_SITIO, query="SELECT u FROM UbicacionSitioMuestral u where u.idSitio = :idSitio and u.estado = true order by id")
})
public class UbicacionSitioMuestral extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAQUETE = "ec.gob.ambiente.rcoa.viabilidadAmbiental.model.";
	
	public static final String GET_LISTA_POR_SITIO = PAQUETE + "UbicacionSitioMuestral.getListaPorSitio";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="sasl_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="sasi_id")
	private Integer idSitio;

	@ManyToOne
	@JoinColumn(name = "gelo_id")
	@ForeignKey(name = "fk_gelo_id")
	@Getter
	@Setter
	private UbicacionesGeografica ubicacionesGeografica;

}