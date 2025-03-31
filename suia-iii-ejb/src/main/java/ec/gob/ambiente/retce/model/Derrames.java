package ec.gob.ambiente.retce.model;

import java.io.Serializable;
import java.util.Date;

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
import ec.gob.ambiente.suia.domain.SustanciaQuimicaPeligrosa;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;


/**
 * The persistent class for the spillover database table.
 * 
 */
@Entity
@Table(name="spillover", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "spil_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "spil_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "spil_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "spil_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "spil_user_update")) })
public class Derrames extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="spil_id")
	private Integer id;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="proj_id")
	private InformacionProyecto informacionProyecto;	
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="gcde_id_emergency_report")
	private DetalleCatalogoGeneral catalogoReporteEmergencia;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="gcde_id_cause")
	private DetalleCatalogoGeneral catalogoCausa;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="gcde_id_infrastructure")
	private DetalleCatalogoGeneral catalogoInfraestructura;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="gcde_id_spilled_product")
	private DetalleCatalogoGeneral catalogoProductoDerramado;
		
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="dach_id")
	SustanciaQuimicaPeligrosa sustanciaQuimicaPeligrosa;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="gelo_id")
	private UbicacionesGeografica ubicacionesGeografica;

	@Getter
	@Setter
	@Column(name="spil_occurrence_start_date")
	private Date fechaOcurrenciaDesde;
	
	@Getter
	@Setter
	@Column(name="spil_occurrence_end_date")
	private Date fechaOcurrenciaHasta;
	
	@Getter
	@Setter
	@Column(name="spil_notification_date")
	private Date fechaNotificacion;
		
	@Getter
	@Setter
	@Column(name="spli_address")
	private String direccion;
	
	@Getter
	@Setter
	@Column(name="spil_code")
	private String codigo;
	
	@Getter
	@Setter
	@Column(name="spil_spillover_code")
	private String codigoDerrame;
	
	@Getter
	@Setter
	@Column(name="spil_other_emergency_report")
	private String otroReporteEmergencia;
	
	@Getter
	@Setter	
	@Column(name="spil_other_cause")
	private String otroCausa;
	
	@Getter
	@Setter
	@Column(name="spil_other_infrastructure")
	private String otroInfraestructura;
	
	@Getter
	@Setter
	@Column(name="spil_other_spilled_product")
	private String otroProductoDerramado;
	
	@Getter
	@Setter
	@Column(name="spil_coordinate_x")
	private Double coordenadaX;

	@Getter
	@Setter
	@Column(name="spil_coordinate_y")
	private Double coordenadaY;
	
	@Getter
	@Setter
	@Column(name="spil_volume_spilled")
	private Double volumenDerramado;
	
	@Getter
	@Setter
	@Column(name="spil_volume_recovered")
	private Double volumenRecuperado;
	
	@Getter
	@Setter
	@Column(name="spil_sended")
	private Boolean enviado;
		
	public String getCausa(){
		return catalogoCausa==null?null:catalogoCausa.getDescripcion().contains("Otro")?otroCausa:catalogoCausa.getDescripcion();
	}
	
	public String getInfraestructura(){
		return catalogoInfraestructura==null?null:catalogoInfraestructura.getDescripcion().contains("Otro")?otroInfraestructura:catalogoInfraestructura.getDescripcion();
	}
		
	public String getProductoDerramado(){
		return catalogoProductoDerramado==null?null:catalogoProductoDerramado.getDescripcion().contains("Otro")?otroProductoDerramado:catalogoProductoDerramado.getDescripcion().contains("Acuerdo Ministerial 142")?sustanciaQuimicaPeligrosa.getDescripcion():catalogoProductoDerramado.getDescripcion();
	}

}