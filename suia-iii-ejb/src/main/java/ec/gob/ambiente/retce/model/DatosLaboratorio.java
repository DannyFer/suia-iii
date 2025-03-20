package ec.gob.ambiente.retce.model;

import java.io.Serializable;
import java.util.Date;
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
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;


/**
 * The persistent class for the laboratory_data database table.
 * 
 */
@Entity
@Table(name="laboratory_data", schema="retce")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "lada_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "lada_date_create")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "lada_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "lada_user_create")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "lada_user_update")) })

@NamedQueries({
	@NamedQuery(name = DatosLaboratorio.GET_HISTORIAL_POR_ID_NRO_OBSERVACION, query = "SELECT t FROM DatosLaboratorio t WHERE t.idRegistroOriginal = :idLaboratorio and estado = true and historial = true and numeroObservacion = :nroObservacion ORDER BY t.id desc"),
	@NamedQuery(name = DatosLaboratorio.GET_HISTORIAL_POR_ID, query = "SELECT t FROM DatosLaboratorio t WHERE t.idRegistroOriginal = :idLaboratorio and estado = true and historial = true ORDER BY t.id desc") })
public class DatosLaboratorio extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String GET_HISTORIAL_POR_ID_NRO_OBSERVACION = "ec.gob.ambiente.retce.model.DatosLaboratorio.getHistorialPorIdNroObservacion";
	public static final String GET_HISTORIAL_POR_ID = "ec.gob.ambiente.retce.model.DatosLaboratorio.getHistorialPorId";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="lada_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name="lada_name")
	private String nombre;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="atem_id")
	private EmisionesAtmosfericas emisionesAtmosfericas;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="lido_id")
	private DescargasLiquidas descargasLiquidas;

	@Getter
	@Setter
	@Column(name="lada_date_registration_validity")
	private Date fechaVigenciaRegistro;

	@Getter
	@Setter
	@Column(name="lada_registration_number_sae")
	private String numeroRegistroSAE;

	@Getter
	@Setter
	@Column(name="lada_ruc")
	private String ruc;
		
	@Getter
	@Setter
	@Transient
	private Documento documentoLaboratorio;
	
	@Getter
	@Setter
	@Transient
	private List<Documento> listaDocumentosLaboratorios;
	
	@Getter
	@Setter
	@Column(name="lada_history")
	private Boolean historial;
    
    @Getter
	@Setter
	@Column(name="lada_original_record_id")
	private Integer idRegistroOriginal;
    
    @Getter
	@Setter
	@Column(name="lada_observation_number")
	private Integer numeroObservacion;
    
    @Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="gcde_id_type")
	private DetalleCatalogoGeneral tipoLaboratorio;
    
    @Getter
	@Setter
	@Transient
	private List<DatosLaboratorio> historialLista;
    
    @Getter
	@Setter
	@Column(name="lada_methodology")
	private String metodologia;
		
	public boolean getTieneHistorial(){
		return historialLista==null || historialLista.isEmpty()? false:true;
	}

}
