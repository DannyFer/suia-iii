package ec.gob.ambiente.rcoa.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "demo_mesatrabajo", schema = "public")
//@AttributeOverrides({
//	@AttributeOverride(name = "estado", column = @Column(name = "sude_status")),
//	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "sude_creation_date"))
//})

@NamedQuery(name = "MesaDeTrabajo.findAll", query = "SELECT mt FROM MesaDeTrabajo mt")


public class MesaDeTrabajo extends EntidadBase {
	
private static final long serialVersionUID = 1L;
	
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "mesa_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "mesa_name")
	private String nombre;
	
	@Getter
	@Setter
	@Column(name = "mesa_idmesa")
	private Integer mesa;
	
	@Getter
	@Setter
	@Column(name = "mesa_idarticulo")
	private Integer articulo;
	
	@Getter
	@Setter
	@Column(name = "mesa_detallearticulo")
	private String detalleArticulo;
	
	@Getter
	@Setter
	@Column(name = "mesa_esmesa")
	private Boolean esMesa;
	
	@Getter
	@Setter
	@Column(name = "mesa_esarticulo")
	private Boolean esArticulo;
	

}
