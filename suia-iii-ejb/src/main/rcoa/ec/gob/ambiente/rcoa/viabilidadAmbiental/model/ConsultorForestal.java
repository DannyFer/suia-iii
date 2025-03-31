package ec.gob.ambiente.rcoa.viabilidadAmbiental.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;


/**
 * The persistent class for the titles database table.
 * 
 */
@Entity
@Table(name="consultant", schema = "coa_viability")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "cons_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "cons_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "cons_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "cons_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "cons_user_update")) })

@NamedQueries({
@NamedQuery(name="ConsultorForestal.findAll", query="SELECT c FROM ConsultorForestal c"),
@NamedQuery(name=ConsultorForestal.GET_CONSULTOR_POR_CEDULA, query="SELECT e FROM ConsultorForestal e where e.cedula = :cedula and e.estado = true order by id")
})
public class ConsultorForestal extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final String PAQUETE = "ec.gob.ambiente.rcoa.viabilidadAmbiental.model.";
	
	public static final String GET_CONSULTOR_POR_CEDULA = PAQUETE + "EspeciesInformeForestal.getConsultorPorCedula";

	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="cons_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="cons_identification_card")
	private String cedula;
	
	@Getter
	@Setter
	@Column(name="cons_names")
	private String nombres;

	@Getter
	@Setter
	@Column(name="cons_academic_title")
	private String tituloAcademico;
	
	@Getter
	@Setter
	@Column(name="cons_number_register")
	private String registroSenecyt;

}