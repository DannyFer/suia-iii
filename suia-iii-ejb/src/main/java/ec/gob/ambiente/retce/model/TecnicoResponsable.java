package ec.gob.ambiente.retce.model;


import java.io.Serializable;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
/**
 * The persistent class for the retce.responsible_technician database table.
 * 
 */
@Entity
@Table(name="responsible_technician", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "resp_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "resp_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "resp_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "resp_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "resp_user_update")) })
public class TecnicoResponsable extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="resp_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name="resp_identifier")
	private String identificador;
	
	@Getter
	@Setter
	@Column(name="resp_name")
	private String nombre;
	
	@Getter
	@Setter
	@Column(name="resp_email")
	private String correo;
	
	@Getter
	@Setter
	@Column(name="resp_phone")
	private String telefono;
	
	@Getter
	@Setter
	@Column(name="resp_cell_phone")
	private String celular;
	
	@Getter
	@Setter
	@Column(name="resp_history")
	private Boolean historial;
    
    @Getter
	@Setter
	@Column(name="resp_original_record_id")
	private Integer idRegistroOriginal;
    
    @Getter
	@Setter
	@Column(name="resp_observation_number")
	private Integer numeroObservacion;
    
    @Getter
	@Setter
	@Transient
	private List<TecnicoResponsable> historialLista;
		
	public boolean getTieneHistorial(){
		return historialLista==null || historialLista.isEmpty()? false:true;
	}

}
