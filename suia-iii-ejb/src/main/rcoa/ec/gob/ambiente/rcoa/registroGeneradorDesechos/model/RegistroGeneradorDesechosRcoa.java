package ec.gob.ambiente.rcoa.registroGeneradorDesechos.model;

import javax.persistence.*;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the waste_generator_record_coa database table.
 * 
 */
@Entity
@Table(name="waste_generator_record_coa", schema="coa_waste_generator_record")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "ware_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "ware_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "ware_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "ware_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "ware_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "ware_status = 'TRUE'")
@NamedQuery(name="RegistroGeneradorDesechosRcoa.findAll", query="SELECT r FROM RegistroGeneradorDesechosRcoa r")
public class RegistroGeneradorDesechosRcoa extends EntidadAuditable {
	private static final long serialVersionUID = 1L;

	@Id
	@Getter
	@Setter
	@Column(name="ware_id")
	@SequenceGenerator(name = "WASTE_GENERATOR_RECORD_COA_ID_GENERATOR", sequenceName = "waste_generator_record_ware_id_seq", schema = "coa_waste_generator_record", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WASTE_GENERATOR_RECORD_COA_ID_GENERATOR")
	private Integer id;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="user_id")
	private Usuario usuario;

	@Getter
	@Setter
	@Column(name="ware_code")
	private String codigo;

	@Getter
	@Setter
	@Column(name="ware_deactivation_date")
	private Date fechaDesactivacion;

	@Getter
	@Setter
	@Column(name="ware_delete_reason")
	private String motivoEliminacion;
	
	@Getter
	@Setter
	@Column(name="ware_responsibility_held")
	private Boolean esResponsabilidadExtendida;
	
	@Getter
	@Setter
	@Column(name = "ware_finalized")
	private Boolean finalizado;
	
	@Getter
	@Setter
	@Column(name = "ware_send_date_information")
	private Date fechaEnvioInformacion;

	@Getter
	@Setter
	@Transient
	private String fecha;

	@Getter
	@Setter
	@Transient
	private Area areaResponsable;

	//bi-directional many-to-one association to PuntoRecuperacionRgdRcoa
	@Getter
	@Setter
	@OneToMany(mappedBy="registroGeneradorDesechosRcoa")
	private List<PuntoRecuperacionRgdRcoa> puntoRecuperacionRgdRcoaList;

	//bi-directional many-to-one association to PoliticaDesechoRgdRcoa
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="wapo_id")
	private PoliticaDesechoRgdRcoa politicaDesechoRgdRcoa;

	//bi-directional many-to-one association to RegistroGeneradorDesechosProyectosRcoa
	@Getter
	@Setter
	@OneToMany(mappedBy="registroGeneradorDesechosRcoa")
	private List<RegistroGeneradorDesechosProyectosRcoa> registroGeneradorDesechosProyectoList;

	public RegistroGeneradorDesechosRcoa() {
	}	

}
