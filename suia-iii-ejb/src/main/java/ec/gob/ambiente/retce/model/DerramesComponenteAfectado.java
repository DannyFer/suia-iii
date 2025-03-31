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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;


/**
 * The persistent class for the spill_affected_component database table.
 * 
 */
@Entity
@Table(name="spill_affected_component", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "spac_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "spac_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "spac_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "spac_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "spac_user_update")) })
public class DerramesComponenteAfectado extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="spac_id")
	private Integer id;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="spil_id")
	private Derrames derrames;	
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="gcde_id_affected_component")
	private DetalleCatalogoGeneral catalogoComponenteAfectado;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="gcde_id_type_affectation")
	private DetalleCatalogoGeneral catalogoTipoAfectacion;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="gcde_id_pollutant")
	private DetalleCatalogoGeneral catalogoContaminante;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="gcde_id_soil_treatment")
	private DetalleCatalogoGeneral catalogoTratamientoSuelo;
				
	@Getter
	@Setter
	@Column(name="spac_initial_concentration")
	private Double concentracionInicial;
	
	@Getter
	@Setter
	@Column(name="spac_unit")
	private String unidad;
	
	@Getter
	@Setter
	@Column(name="spac_name_water_body")
	private String nombreCuerpoHidrico;
	
	@Getter
	@Setter	
	@Column(name="spac_soils_sediments_contaminated")
	private Double suelosSedimentosContaminados;
	
	@Getter
	@Setter	
	@Column(name="spac_soils_sediments_remediated")
	private Double suelosSedimentosRemediados;
	
	@Getter
	@Setter	
	@Column(name="spac_affected_area")
	private Double areaAfectada;
	
	@Getter
	@Setter	
	@Column(name="spac_people_affected")
	private Double personasAfectadas;
	
	@Getter
	@Setter
	@Column(name="spac_other_pollutant")
	private String otroContaminante;
	
	@Getter
	@Setter
	@Column(name="spac_other_type_affected")
	private String otroTipoAfectacion;

	@Getter
	@Setter
	@Transient
	private Documento adjuntoEspeciesAfectadas;

	@Getter
	@Setter
	@Transient
	private List<ProyectoComponente> listProyectoComponente;
	
	public String getContaminante(){		
		return catalogoContaminante==null?null:catalogoContaminante.getDescripcion().contains("Otro")?otroContaminante:catalogoContaminante.getDescripcion();
	}
}