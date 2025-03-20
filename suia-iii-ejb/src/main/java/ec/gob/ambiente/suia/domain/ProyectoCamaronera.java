package ec.gob.ambiente.suia.domain;

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

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 	   Autor: Santiago Flores
 * <b> Entity que representa la tabla projects_shrimp
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "projects_shrimp", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "prshri_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prshri_status = 'TRUE'")
public class ProyectoCamaronera extends EntidadBase {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "prshri_id")
	@Getter
	@Setter
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "shri_id")
	@ForeignKey(name = "fk_shri_id")
    @Getter
	@Setter
	private Camaroneras camaroneras;
	
	@ManyToOne
	@JoinColumn(name = "pren_id")
	@ForeignKey(name = "fk_pren_id")
    @Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental;
	
	@ManyToOne
	@JoinColumn(name = "docu_id")
	@ForeignKey(name = "fk_docu_id")
    @Getter
	@Setter
	private Documento documento;
	
	@Column(name = "prshri_code")
	@Getter
	@Setter
	private String codigoCamaronera;
	
	@Column(name = "prshri_extension")
	@Getter
	@Setter
	private Double extensionCamaronera;
	
//	@Column(name="prshri_status")
//	@Getter
//	@Setter
//	private boolean estado; 
}