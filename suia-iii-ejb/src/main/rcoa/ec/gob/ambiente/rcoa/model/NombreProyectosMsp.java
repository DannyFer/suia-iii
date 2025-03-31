package ec.gob.ambiente.rcoa.model;

import java.io.Serializable;

import javax.persistence.*;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the project_names_msp database table.
 * 
 */
@Entity
@Table(name="project_names_msp", schema="coa_mae")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "prms_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "prms_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "prms_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "prms_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "prms_user_update")) })
@NamedQuery(name="NombreProyectosMsp.findAll", query="SELECT n FROM NombreProyectosMsp n")
public class NombreProyectosMsp extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="prms_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="prms_observation")
	private String observacion;

	//bi-directional many-to-one association to RucMsp
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="msru_id")
	private RucMsp rucMsp;

	//bi-directional many-to-one association to NombreProyectos
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="prna_id")
	private NombreProyectos nombreProyectos;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="prco_id")
	private ProyectoLicenciaCoa proyectoLicenciaCoa;

	public NombreProyectosMsp() {
	}	
}