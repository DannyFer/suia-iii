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
@Table(name = "demo_notas", schema = "public")
//@AttributeOverrides({
//	@AttributeOverride(name = "estado", column = @Column(name = "nota_status")),
//	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "nota_creation_date"))
//})

@NamedQuery(name = "Nota.findAll", query = "SELECT n FROM Nota n")

public class Nota extends EntidadBase{
	private static final long serialVersionUID = 1L;
	
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "nota_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "nota_nombre")
	private String nombre;
	
	@Getter
	@Setter
	@Column(name = "nota_descripcion")
	private String descripcion;
}
