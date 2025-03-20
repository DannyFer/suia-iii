package ec.gob.ambiente.suia.campanias.bajalealplastico.domain;

import java.io.Serializable;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;


/**
 * The persistent class for the institution_information database table.
 * 
 */
@Entity
@Table(name="institution_information", schema="environmental_education_campaign")
@NamedQuery(name="Institucion.findAll", query="SELECT i FROM Institucion i")
public class Institucion implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@Column(name="cod_institution")
	private String codigo;
	
	@Getter
	@Setter
	@Column(name="cod_parish")
	private String codigoParroquia;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="gelo_id")
	private UbicacionesGeografica parroquia;

	@Getter
	@Setter
	@Column(name="info_institution_name")
	private String nombre;

	@Getter
	@Setter
	@Column(name="info_instution_address")
	private String direccion;
	
	@Getter
	@Setter
	@Column(name="info_institution_email")
	private String correo;
	
	@Getter
	@Setter
	@Column(name="info_institution_sustenance")
	private String sostenimiento;
	
}