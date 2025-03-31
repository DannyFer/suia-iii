package ec.gob.ambiente.rcoa.viabilidadAmbiental.model;

import java.io.Serializable;

import javax.persistence.*;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the reference_coordinates_forest database table.
 * 
 */
@Entity
@Table(name="reference_coordinates_forest", schema = "coa_viability")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "recf_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "recf_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "recf_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "recf_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "recf_user_update")) })

@NamedQueries({
@NamedQuery(name="CoordenadaForestal.findAll", query="SELECT c FROM CoordenadaForestal c"),
@NamedQuery(name=CoordenadaForestal.GET_POR_INFORME, query="SELECT c FROM CoordenadaForestal c where c.idInformeInspeccion = :idInforme and c.estado = true"),
@NamedQuery(name=CoordenadaForestal.GET_POR_INFORME_ORDEN, query="SELECT c FROM CoordenadaForestal c where c.idInformeInspeccion = :idInforme and c.estado = true order by orden asc")})
public class CoordenadaForestal extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAQUETE = "ec.gob.ambiente.rcoa.viabilidadAmbiental.model.";

	public static final String GET_POR_INFORME = PAQUETE + "CoordenadaForestal.getPorInforme";
	public static final String GET_POR_INFORME_ORDEN = PAQUETE + "CoordenadaForestal.getPorInformeOrden";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="recf_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="inrf_id")
	private Integer idInformeInspeccion;

	@Getter
	@Setter
	@Column(name="recf_coordinate_x")
	private Double coordenadaX;

	@Getter
	@Setter
	@Column(name="recf_coordinate_y")
	private Double coordenadaY;
	
	@Getter
	@Setter
	@Column(name="recf_description")
	private String descripcion;	

	@Getter
	@Setter
	@Column(name="recf_order")
	private Integer orden;

	@Getter
	@Setter
	@Column(name="recf_elevation")
	private Double elevacion;

}