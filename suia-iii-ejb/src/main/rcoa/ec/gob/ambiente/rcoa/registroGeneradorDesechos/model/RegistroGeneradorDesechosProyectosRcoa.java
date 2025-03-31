package ec.gob.ambiente.rcoa.registroGeneradorDesechos.model;

import java.io.Serializable;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.rcoa.digitalizacion.model.AutorizacionAdministrativaAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;


/**
 * The persistent class for the waste_generator_record_project_licencing_coa database table.
 * 
 */
@Entity
@Table(name="waste_generator_record_project_licencing_coa", schema="coa_waste_generator_record")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "wapr_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "wapr_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "wapr_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "wapr_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "wapr_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wapr_status = 'TRUE'")
@NamedQuery(name="RegistroGeneradorDesechosProyectosRcoa.findAll", query="SELECT r FROM RegistroGeneradorDesechosProyectosRcoa r")
public class RegistroGeneradorDesechosProyectosRcoa extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="wapr_id")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="prco_id")
	private ProyectoLicenciaCoa proyectoLicenciaCoa; //proyectos del nuevo esquema 

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="enaa_id")
	private AutorizacionAdministrativaAmbiental proyectoDigitalizado;
		
	@Getter
	@Setter
	@Column(name="wapr_come_from")
	private String formaEmision;
	
	@Getter
	@Setter
	@Column(name="wapr_4cat_sect")
	private String codigoProyecto;
	
	@Getter
	@Setter
	@Column(name = "id_proyect")
	private Integer proyectoId;
	
	@Getter
	@Setter
	@Column(name = "wapr_id_history")
	private Integer idPadreHistorial;
	
	@Getter
	@Setter
	@Column(name = "wapr_description_system")
	private String descripcionSistema; //almacena de donde viene el proyecto sector-subsector, 4categorias, suia, hidrocarburos
	
	//bi-directional many-to-one association to RegistroGeneradorDesechosRcoa
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="ware_id")
	private RegistroGeneradorDesechosRcoa registroGeneradorDesechosRcoa;

	public RegistroGeneradorDesechosProyectosRcoa() {
	}


}
