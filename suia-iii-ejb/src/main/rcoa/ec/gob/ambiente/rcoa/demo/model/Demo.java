package ec.gob.ambiente.rcoa.demo.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "demo", schema = "public")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "demo_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "demo_creation_date"))
})

@NamedQuery(name = "Demo.findAll", query = "SELECT d FROM Demo d")


public class Demo extends EntidadBase{

	private static final long serialVersionUID = 1L;
	
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "demo_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "demo_cedula")
	private String cedula;
	
	@Getter
	@Setter
	@Column(name = "demo_name")
	private String nombre;
	
	@Getter
	@Setter
	@Column(name = "demo_email")
	private String correo;
	
	@Getter
	@Setter
	@Column(name = "demo_org")
	private Boolean esOrg;
	
	@Getter
	@Setter
	@Column(name = "demo_org_ruc")
	private String orgRuc;
	
	@Getter
	@Setter
	@Column(name = "demo_org_description")
	private String orgDescripcion;
	
	@Getter
	@Setter
	@Column(name = "demo_id_mesatrabajo")
	private String mesaDeTrabajo;
	
	@Getter
	@Setter
	@Column(name = "demo_id_articulos")
	private String articulo;
	
	@Getter
	@Setter
	@Column(name = "demo_id_detallearticulo")
	private String detalleAtriculo;
	
	@Getter
	@Setter
	@Column(name = "demo_propuesta")
	private String propuesta;
	
	@Getter
	@Setter
	@Column(name = "demo_justificacion")
	private String justificacion;
	
	
	@Getter
	@Setter
	@Column(name = "demo_aceptar")
	private Boolean aceptar;
	
	@ManyToOne
	@JoinColumn(name = "gelo_id")	
	@Getter
	@Setter
	private UbicacionesGeografica ubicacionesGeografica;
	
	
}
