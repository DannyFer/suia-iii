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
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;


/**
 * The persistent class for the monitoring_point_characteristics database table.
 * 
 */
@Entity
@Table(name="catalog_receiver_monitoring_point", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "crmp_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "crmp_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "crmp_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "crmp_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "crmp_user_update")) })
public class CatalogoTipoCuerpoReceptorCaracteristicasPunto extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="crmp_id")
	private Integer id;

		
	@Getter
	@Setter
	@ManyToOne
    @JoinColumn(name="crbt_id")
	private CatalogoTipoCuerpoReceptor catalogoTipoCuerpoReceptor;
	
	@Getter
	@Setter
	@ManyToOne
    @JoinColumn(name="mpch_id")
	private CaracteristicasPuntoMonitoreo caracteristicasPuntoMonitoreo;

}