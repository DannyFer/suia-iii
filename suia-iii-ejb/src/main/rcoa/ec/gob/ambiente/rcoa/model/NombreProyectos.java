package ec.gob.ambiente.rcoa.model;

import java.io.Serializable;

import javax.persistence.*;

import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the project_names database table.
 * 
 */
@Entity
@Table(name="project_names", schema="coa_mae")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "prna_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "prna_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "prna_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "prna_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "prna_user_update")) })
@NamedQuery(name="NombreProyectos.findAll", query="SELECT n FROM NombreProyectos n")
public class NombreProyectos extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="prna_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="geca_id")
	private CatalogoGeneralCoa catalogoGeneralCoa;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="gelo_id")
	private UbicacionesGeografica ubicacionesGeografica;	

	@Getter
	@Setter
	@Column(name="prna_project_name")
	private String nombreProyectoMsp;

	@Getter
	@Setter
	@Column(name="prna_status_used")
	private Boolean proyectoUsado;

	public NombreProyectos() {
	}

}