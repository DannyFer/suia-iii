package ec.gob.ambiente.suia.domain;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the process_suspended database table.
 * 
 */

@Entity
@Table(name = "process_suspended", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "prsu_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "prsu_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "prsu_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "prsu_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "prsu_user_update")) })
public class ProcesoSuspendido extends EntidadAuditable {
	
	private static final long serialVersionUID = 356670983410972065L;
	public static String TIPO_PROYECTO_REGURALIZACION="RegularizacionAmbiental";
	public static String TIPO_PROYECTO_HIDROCARBUROS="Hidrocarburos";
	public static String TIPO_PROYECTO_4CATEGORIAS="4Categorias";
	public static String TIPO_RGD_NO_ASOCIADO="RegistroGeneradorDesechosNoAsociado";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "prsu_id")	
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "prsu_code")
	private String codigo;//Codigo Proyecto
	
	@Getter
	@Setter
	@Column(name = "prsu_desciption")
	private String descripcion;
	
	@Getter
	@Setter
	@Column(name = "prsu_suspended")
	private Boolean suspendido;
	
	
	@Getter
	@Setter
	@Column(name = "prsu_type_project")
	private String tipoProyecto;

	@Getter
	@Setter
	@Column(name = "prsu_reactivated_days")
	private Integer diasReactivados;
	
	@Getter
	@Setter
	@Column(name = "prsu_reactivated_date")
	private Date fechaActivacion;
}