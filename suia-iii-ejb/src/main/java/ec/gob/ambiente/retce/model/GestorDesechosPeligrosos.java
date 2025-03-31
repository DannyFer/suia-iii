package ec.gob.ambiente.retce.model;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;


/**
 * The persistent class for the general_catalog database table.
 * 
 */
@Entity
@Table(name="hazardous_waste_manager", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "hwm_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "hwm_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "hwm_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "hwm_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "hwm_user_update")) })
@NamedQueries({
	@NamedQuery(name = "GestorDesechosPeligrosos.findAll", query = "SELECT g FROM GestorDesechosPeligrosos g WHERE estado = true"),
	@NamedQuery(name = GestorDesechosPeligrosos.FIND_BY_ID, query = "SELECT t FROM GestorDesechosPeligrosos t WHERE t.id = :id and estado = true ORDER BY t.id desc"),
	@NamedQuery(name = GestorDesechosPeligrosos.FIND_BY_CODE, query = "SELECT t FROM GestorDesechosPeligrosos t WHERE t.codigo = :codigo and estado = true ORDER BY t.id desc"),
	@NamedQuery(name = GestorDesechosPeligrosos.GET_BY_INFORMACION_BASICA, query = "SELECT t FROM GestorDesechosPeligrosos t WHERE t.informacionProyecto.id = :idInformacionBasica and estado = true ORDER BY t.id desc")
})
public class GestorDesechosPeligrosos extends EntidadAuditable {
	private static final long serialVersionUID = 1L;
	
	public static final String FIND_BY_ID = "ec.gob.ambiente.retce.model.GestorDesechosPeligrosos.getGestorDesechoPeligrosoPorId";
	public static final String FIND_BY_CODE = "ec.gob.ambiente.retce.model.GestorDesechosPeligrosos.getGestorDesechoPeligrosoPorCodigo";
	public static final String GET_BY_INFORMACION_BASICA = "ec.gob.ambiente.retce.model.GestorDesechosPeligrosos.getGestorDesechoPeligrosoPorInformacionBasica";
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="hwm_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name="hwm_code")
	private String codigo;

	@Getter
	@Setter
	@Column(name="hwm_year")
	private Integer anio;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="proj_id")
	private InformacionProyecto informacionProyecto;
	
	@Getter
	@Setter
	@Column(name="area_id")
	private Integer area;
	
	@Getter
	@Setter
	@Column(name="hwm_storage")
	private Boolean faseAlmacenamiento;
	
	@Getter
	@Setter
	@Column(name="hwm_transport")
	private Boolean faseTransporte;
	
	@Getter
	@Setter
	@Column(name="hwm_elimination")
	private Boolean faseEliminacion;
	
	@Getter
	@Setter
	@Column(name="hwm_final_diposition")
	private Boolean faseDisposicionFinal;
	
	@Getter
	@Setter
	@Column(name="hwm_history")
	private Boolean historial;
	
	@Getter
	@Setter
	@Column(name="hwm_id_owner")
	private Integer idPertenece;
	
	@Getter
	@Setter
	@Column(name="hwm_sended")
	private Boolean enviado;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "resp_id")
	private TecnicoResponsable tecnicoResponsable;

}