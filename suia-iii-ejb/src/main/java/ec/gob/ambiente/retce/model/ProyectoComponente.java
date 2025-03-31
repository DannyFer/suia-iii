package ec.gob.ambiente.retce.model;


import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
/**
 * The persistent class for the retce.product database table.
 * 
 */
@Entity
@Table(name="project_information_component", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "pric_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "pric_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "pric_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "pric_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "pric_user_update")) })
public class ProyectoComponente extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="pric_id")
	private Integer id;
	  
	@Getter
	@Setter
	@Column(name="pric_name")
	private String nombre;
	
	@Getter
	@Setter
	@Column(name="pric_family")
	private String familia;
	
	@Getter
	@Setter
	@Column(name="pric_species")
	private String especies;
	
	@Getter
	@Setter
	@Column(name="pric_gender")
	private String genero;
	
	@Getter
	@Setter
	@Column(name="pric_type")
	private String tipo;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="spac_id")
	private DerramesComponenteAfectado derrameComponenteAfectado;
		
}

