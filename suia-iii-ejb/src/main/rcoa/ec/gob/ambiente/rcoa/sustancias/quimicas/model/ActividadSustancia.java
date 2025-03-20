package ec.gob.ambiente.rcoa.sustancias.quimicas.model;

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

import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.model.GestionarProductosQuimicosProyectoAmbiental;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="activity_chemical_sustances", schema = "coa_chemical_sustances")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "achs_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "achs_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "achs_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "achs_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "achs_user_update")) })
public class ActividadSustancia extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public ActividadSustancia() {}
	
	public ActividadSustancia(
			RegistroSustanciaQuimica registroSustanciaQuimica,
			GestionarProductosQuimicosProyectoAmbiental gestionarProductosQuimicosProyectoAmbiental,
			CaracteristicaActividad caracteristicaActividad) {
		this.actividadSeleccionada=false;
		this.registroSustanciaQuimica=registroSustanciaQuimica;
		this.gestionarProductosQuimicosProyectoAmbiental=gestionarProductosQuimicosProyectoAmbiental;
		this.caracteristicaActividad=caracteristicaActividad;
	}
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="achs_id")
	private Integer id;
	
	@Getter
	@Column(name="achs_check_value")
	private Boolean actividadSeleccionada;
	
	public void setActividadSeleccionada(Boolean actividadSeleccionada) {
		this.actividadSeleccionada=actividadSeleccionada;
		if(actividadSeleccionada==null || !actividadSeleccionada) {
			this.descripcion=null;
		}
	}
	
	@Getter
	@Setter
	@Column(name="achs_purity")
	private String rangoConcentracion;
	
	@Getter
	@Setter
	@Column(name="achs_quantity")
	private Double cupo;
	
	@Getter
	@Setter
	@Column(name="achs_description")
	private String descripcion;
	
	@ManyToOne
	@JoinColumn(name = "chsr_id")	
	@Getter
	@Setter
	private RegistroSustanciaQuimica registroSustanciaQuimica;
	
	@ManyToOne
	@JoinColumn(name = "acch_id")	
	@Getter
	@Setter
	private CaracteristicaActividad caracteristicaActividad;
	
	@ManyToOne
	@JoinColumn(name = "mach_id")	
	@Getter
	@Setter
	private GestionarProductosQuimicosProyectoAmbiental gestionarProductosQuimicosProyectoAmbiental;	
	
	@ManyToOne
	@JoinColumn(name = "geca_id_unit")	
	@Getter
	@Setter
	private CatalogoGeneralCoa unidadMedida;
	
	@Getter
	@Setter
	@Column(name="achs_quantity_control")
	private Double cupoControl;
}