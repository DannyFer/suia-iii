package ec.gob.ambiente.retce.model;

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

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name="waste_manager_elimination_danger", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "wmed_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "wmed_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "wmed_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "wmed_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "wmed_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "wmed_status = 'TRUE'")
@NamedQueries({
	@NamedQuery(name = GestorDesechosEliminacionDesechoPeligroso.GET_BY_DESECHO_ELIMINACION, query = "SELECT d FROM GestorDesechosEliminacionDesechoPeligroso d WHERE d.gestorDesechosEliminacion.id = :idDesechoEliminacion and estado = true ORDER BY d.id desc")
})
public class GestorDesechosEliminacionDesechoPeligroso extends EntidadAuditable{
	
	private static final long serialVersionUID = 6243073450803722369L;
	
	public static final String GET_BY_DESECHO_ELIMINACION = "ec.com.magmasoft.business.domain.GestorDesechosEliminacionDesechoPeligroso.getPorDesechoEliminacion";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="wmed_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name="wmed_cant")
	private Double cantidad;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="wame_id")
	private GestorDesechosEliminacion gestorDesechosEliminacion;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="wada_id")
	private DesechoPeligroso desechoPeligroso;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="gcde_id")
	private DetalleCatalogoGeneral unidadMedida;
}
