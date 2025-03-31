package ec.gob.ambiente.rcoa.model;

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

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;


/**
 * The persistent class for the tasks_mass_signing database table.
 * 
 */
@Entity
@Table(name="tasks_mass_signing", schema = "coa_mae")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "tams_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "tams_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "tams_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "tams_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "tams_user_update")) })

@NamedQueries({
@NamedQuery(name="TareaFirmaMasiva.findAll", query="SELECT t FROM TareaFirmaMasiva t"),
@NamedQuery(name=TareaFirmaMasiva.GET_POR_FLUJO_TAREA, query="SELECT t FROM TareaFirmaMasiva t where t.idFlujo = :idFlujo and t.nombreTarea = :nombreTarea and t.estado = true order by id desc") })

public class TareaFirmaMasiva extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final String PAQUETE = "ec.gob.ambiente.rcoa.model.";

	public static final String GET_POR_FLUJO_TAREA = PAQUETE + "TareaFirmaMasiva.getPorFlujoTarea";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="tams_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="flow_id")
	private Integer idFlujo;
	
	@Getter
	@Setter
	@Column(name="task_name")
	private String nombreTarea;
	
	@Getter
	@Setter
	@Column(name="doty_id")
	private Integer idTipoDocumento;
	
	@Getter
	@Setter
	@Column(name="tams_code_task")
	private String codigoTarea;


}