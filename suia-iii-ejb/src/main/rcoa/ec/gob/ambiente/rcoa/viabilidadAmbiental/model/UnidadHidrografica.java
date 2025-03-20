package ec.gob.ambiente.rcoa.viabilidadAmbiental.model;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

import java.io.Serializable;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name="hydrographic_units", schema = "coa_viability")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "hyun_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "hyun_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "hyun_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "hyun_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "hyun_user_update")) })

@NamedQueries({
@NamedQuery(name="UnidadHidrografica.findAll", query="SELECT u FROM UnidadHidrografica u"),
@NamedQuery(name=UnidadHidrografica.GET_LISTA_POR_INFORME_INSPECCION, query="SELECT u FROM UnidadHidrografica u where u.idInformeInspeccion = :idInformeInspeccion and u.estado = true order by id"),
@NamedQuery(name=UnidadHidrografica.GET_LISTA_POR_INFORME_FACTIBILIDAD, query="SELECT u FROM UnidadHidrografica u where u.idInformeFactibilidad = :idInformeFactibilidad and u.estado = true order by id")
})

public class UnidadHidrografica extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAQUETE = "ec.gob.ambiente.rcoa.viabilidadAmbiental.model.";
	
	public static final String GET_LISTA_POR_INFORME_INSPECCION = PAQUETE + "UnidadHidrografica.getListaPorInformeInspeccion";
	public static final String GET_LISTA_POR_INFORME_FACTIBILIDAD = PAQUETE + "UnidadHidrografica.getListaPorInformeFactibilidad";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="hyun_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="fere_id")
	private Integer idInformeFactibilidad;
	
	@Getter
	@Setter
	@Column(name="inrf_id")
	private Integer idInformeInspeccion;

	@Getter
	@Setter
	@Column(name="hyun_name")
	private String nombre;	

	@Getter
	@Setter
	@Column(name="hyun_coordinate_x")
	private Double coordenadaX;

	@Getter
	@Setter
	@Column(name="hyun_coordinate_y")
	private Double coordenadaY;

	@Getter
	@Setter
	@Column(name="hyun_elevation")
	private Double elevacion;

	@Getter
	@Setter
	@Column(name="hyun_affectation")
	private Boolean afectacion;

	@Getter
	@Setter
	@Column(name="hyun_characteristic")
	private Integer caracteristica; //1 = Permanente, 2 = Estacional

}