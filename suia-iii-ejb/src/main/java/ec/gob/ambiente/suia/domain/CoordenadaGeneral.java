package ec.gob.ambiente.suia.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import ec.gob.ambiente.suia.utils.Archivo;

@Entity
@Table(name = "general_coordinates", schema = "suia_iii")
@AttributeOverrides({@AttributeOverride(name = "estado", column = @Column(name = "geco_status")),
        @AttributeOverride(name = "fechaCreacion", column = @Column(name = "geco_creation_date")),
        @AttributeOverride(name = "fechaModificacion", column = @Column(name = "geco_date_update")),
        @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "geco_creator_user")),
        @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "geco_user_update"))})
@NamedQueries({
        @NamedQuery(name = CoordenadaGeneral.LISTAR_POR_ID_NOMBRE_TABLA, query = "SELECT c FROM CoordenadaGeneral c WHERE c.nombreTabla = :nombreTabla AND c.idTable = :idTabla AND c.estado = true AND c.idHistorico = null order by c.orden asc"),
        @NamedQuery(name = CoordenadaGeneral.LISTAR_POR_NOMBRE_TABLA_PIN_USUARIO, query = "SELECT c FROM CoordenadaGeneral c WHERE c.nombreTabla = :nombreTabla AND c.descripcion LIKE :pinUsuario AND c.estado = true AND c.idHistorico = null"),
        @NamedQuery(name = CoordenadaGeneral.LISTAR_TODOS_POR_ID_NOMBRE_TABLA, query = "SELECT c FROM CoordenadaGeneral c WHERE c.nombreTabla = :nombreTabla AND c.idTable = :idTabla AND c.estado = true order by c.orden asc"),
})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "geco_status = 'TRUE'")
public class CoordenadaGeneral extends EntidadAuditable implements Serializable, Cloneable {

    private static final String PAQUETE = "ec.gob.ambiente.suia.domain.";
    public static final String LISTAR_POR_ID_NOMBRE_TABLA = PAQUETE + "CoordenadaGeneral.listarPorIdNombreTabla";
    public static final String LISTAR_POR_NOMBRE_TABLA_PIN_USUARIO = PAQUETE
            + "CoordenadaGeneral.listarPorNombreTablaPinUsuario";
    public static final String LISTAR_TODOS_POR_ID_NOMBRE_TABLA = PAQUETE + "CoordenadaGeneral.listarTodosPorIdNombreTabla";

    public static final String PREFIX_FILE_DESCRIPCION = "DESC_";
    public static final String PREFIX_FILE_IMAGE = "IMG_";
    public static final String PREFIX_EIA_ACUATICO = "ACUT_";
    public static final String PREFIX_EIA_ZONAS_SENSIBLES = "ZOSE_";
    public static final String PREFIX_EIA_AMENAZAS = "AMEN_";

    public static final String ACTIVITY_LICENSING_TABLE_CLASS = "actividadLicenciamiento";

    public static final String SQL_ACTUALIZA = "UPDATE suia_iii.general_coordinates";

    private static final long serialVersionUID = 7190290069033775093L;
    
   
	@Id
    @Column(name = "geco_id")
    @SequenceGenerator(name = "COORDINATE_GENERAL_GECO_ID_GENERATOR", sequenceName = "seq_geco_id", schema = "suia_iii", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COORDINATE_GENERAL_GECO_ID_GENERATOR")
    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    @Column(name = "geco_x")
    private BigDecimal x;

    @Getter
    @Setter
    @Column(name = "geco_y")
    private BigDecimal y;

    @Getter
    @Setter
    @Column(name = "geco_z")
    private BigDecimal z;

    @Getter
    @Setter
    @Column(name = "geco_description")
    private String descripcion;

    @Getter
    @Setter
    @Column(name = "geco_point_vertex")
    private String puntoVertice;

    @Column(name = "geco_table_id")
    @Getter
    @Setter
    private Integer idTable;

    @Column(name = "geco_table_class")
    @Getter
    @Setter
    private String nombreTabla;

    @Transient
    @Getter
    @Setter
    private int indice;

    @Transient
    @Getter
    @Setter
    private boolean editar;

    @Column(name = "geco_order")
    @Getter
    @Setter
    private Integer orden;
    
	@Getter
	@Setter
	@Column(name = "geco_historical_id")
	private Integer idHistorico;
	
	@Getter
	@Setter
	@Column(name = "geco_notification_number")
	private Integer numeroNotificacion;
	

    @Getter
    @Setter
    @ManyToOne
    @ForeignKey(name = "fk_general_coordinatesshty_id_shape_typesshty_id")
    @JoinColumn(name = "shty_id")
    private TipoForma tipoForma;

    @Column(name = "shty_id", insertable = false, updatable = false)
    @Getter
    @Setter
    private Integer idTipoForma;

    @Transient
    @Getter
    @Setter
    private Map<String, Archivo> archivos;

    @Getter
    @Setter
    @ManyToOne
    @ForeignKey(name = "fk_general_coordinates_acli_id_activities_licensing_acli_id")
    @JoinColumn(name = "acli_id")
    private ActividadLicenciamiento actividadLicenciamiento;

    @Getter
    @Setter
    @ManyToOne
    @ForeignKey(name = "fk_general_coordinates_foip_id_forest_inventory_points_foip_id")
    @JoinColumn(name = "foip_id")
    private InventarioForestalPuntos inventarioForestalPuntos;

    @Getter
    @Setter
    @ManyToOne
    @ForeignKey(name = "fk_general_coordinates_foip_id_water_bodies_wabo_id")
    @JoinColumn(name = "wabo_id")
    private CuerpoHidrico cuerpoHidrico;

    public CoordenadaGeneral() {
        archivos = new HashMap<String, Archivo>();
    }

    public CoordenadaGeneral(BigDecimal x, BigDecimal y) {
        this.x = x;
        this.y = y;
    }
    
    public CoordenadaGeneral(Integer id) {
       this.id = id;
    }
    
    @Override
	public CoordenadaGeneral clone() throws CloneNotSupportedException {

		 CoordenadaGeneral clone = (CoordenadaGeneral)super.clone();
		 clone.setId(null);		 
		 return clone;
	}
    
    @Getter
	@Setter
	@Transient
	private Boolean nuevoEnModificacion;
	
	@Getter
	@Setter
	@Transient
	private Boolean registroModificado;

}
