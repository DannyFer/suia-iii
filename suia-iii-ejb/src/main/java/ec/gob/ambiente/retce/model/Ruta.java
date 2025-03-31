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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the general_catalog database table.
 * 
 */
@Entity
@Table(name="routes", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "rou_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "rou_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "rou_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "rou_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "rou_user_update")) })
@NamedQueries({
	@NamedQuery(name = "Ruta.findAll", query = "SELECT r FROM Ruta r"),
	@NamedQuery(name = Ruta.FIND_BY_ID, query = "SELECT r FROM Ruta r WHERE r.id= :id and estado = true"),
	@NamedQuery(name = Ruta.GET_BY_GESTOR_DESECHO_PELIGROSO, query = "SELECT r FROM Ruta r WHERE r.gestorDesechosPeligrosos.id = :idGestorDesechoPeligroso and estado = true ORDER BY r.id desc")
})
public class Ruta extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String FIND_BY_ID = "ec.gob.ambiente.retce.model.Ruta.getRutaPorId";
	public static final String GET_BY_GESTOR_DESECHO_PELIGROSO = "ec.gob.ambiente.retce.model.Ruta.getRutaPorGestorDesechoPeligroso";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="rou_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="rou_name_route")
	private String nombre;
	
	@Getter
	@Setter
	@Column(name="rou_province_origin")
	private Integer provinciaOrigen;
	
	@Getter
	@Setter
	@Column(name="rou_province_destination")
	private Integer provinciaDestino;
	
	@Getter
	@Setter
	@Column(name="rou_order")
	private Integer orden;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="hwm_id")
	private GestorDesechosPeligrosos gestorDesechosPeligrosos;
	
	@Getter
	@Setter
	@Transient
	private String nombreProvinciaOrigen, nombreProvinciaDestino;
	
	@Getter
	@Setter
	@Column(name="rou_history")
	private Boolean historial;
	
	@Getter
	@Setter
	@Column(name="rou_id_owner")
	private Integer idPertenece;

}