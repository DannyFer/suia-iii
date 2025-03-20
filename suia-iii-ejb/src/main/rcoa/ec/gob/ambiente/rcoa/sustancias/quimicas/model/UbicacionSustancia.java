package ec.gob.ambiente.rcoa.sustancias.quimicas.model;

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
import javax.persistence.Transient;

import ec.gob.ambiente.rcoa.model.GestionarProductosQuimicosProyectoAmbiental;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="location_substances", schema = "coa_chemical_sustances")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "losu_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "losu_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "losu_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "losu_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "losu_user_update")) })
public class UbicacionSustancia extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;	
	
	public UbicacionSustancia() {}
	
	public UbicacionSustancia(RegistroSustanciaQuimica registroSustanciaQuimica) {
		this.registroSustanciaQuimica=registroSustanciaQuimica;
	}
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="losu_id")
	private Integer id;
	
	@Column(name = "losu_place")	
	@Getter
	@Setter
	private String lugar;
		
	@Getter
	@Setter
	@Column(name="losu_email")
	private String correo;
	
	@Getter
	@Setter
	@Column(name="losu_phone")
	private String telefono;
	
	@Getter
	@Setter
	@Column(name="losu_adress")
	private String direccion;
		
	@Getter
	@Setter
	@Column(name="losu_process_activity")
	private String proceso;
	
	@Getter
	@Setter
	@Column(name="losu_average_product")
	private String cantidadProducida;
	
	@Getter
	@Setter
	@Column(name="losu_average_sustance")
	private String sustanciaEmpleada;
	
	@Getter
	@Setter
	@Column(name="losu_observations")
	private String observaciones;
	
	@Getter
	@Setter
	@Column(name="losu_inspection")
	private Boolean necesitaInspeccion;
	
	@Getter
	@Setter
	@Column(name="losu_support")
	private Boolean necesitaApoyo;
	
	@Getter
	@Setter
	@Column(name="losu_support_date")
	private Date fechaSolicitudApoyo;
	
	@ManyToOne
	@Getter
	@Setter
	@JoinColumn(name="user_id_support")
	private Usuario usuarioApoyo;
	
	@Getter
	@Setter
	@Column(name="losu_complies")
	private Boolean cumpleValor;	
	
	@ManyToOne
	@JoinColumn(name = "chsr_id")	
	@Getter
	@Setter
	private RegistroSustanciaQuimica registroSustanciaQuimica;
	
	//CANTON
	@ManyToOne
	@JoinColumn(name = "gelo_id")	
	@Getter
	@Setter
	private UbicacionesGeografica ubicacionesGeografica;
	
	@ManyToOne
	@JoinColumn(name = "mach_id")	
	@Getter
	@Setter
	private GestionarProductosQuimicosProyectoAmbiental gestionarProductosQuimicosProyectoAmbiental;
	
	@Getter
	@Setter	
	@JoinColumn(name = "area_id_support")
	@ManyToOne		
	private Area area;
	
	@Getter
	@Setter
	@Column(name="losu_coor_x")
	private Double coordenadaX;
	
	@Getter
	@Setter
	@Column(name="losu_coor_y")
	private Double coordenadaY;

	@Getter
	@Setter
	@Column(name="losu_observations_complies")
	private String observacionesInforme;
	
	@Getter
	@Setter
	@Transient
	private DocumentosSustanciasQuimicasRcoa documentoJustificativo;
	
	@Getter
	@Setter
	@Transient
	private Boolean registroEdicion = false;
	
	@Transient	
	@Getter	
	private String[] lugaresArray;
	
	public void setLugaresArray(String[] lugaresArray) {
		this.lugaresArray=lugaresArray;
		if(lugaresArray==null || lugaresArray.length==0) {
			this.lugar=null;
		}else {
			lugar="";
			for (String la : lugaresArray) {
				lugar+=lugar.isEmpty()?la:";"+la;
			}
		}
	}
	
	public String getLugares(){
		if(this.lugar!=null) {
			return this.lugar.replace(";", " ");
		}
		return "";
	}
}