package ec.gob.ambiente.suia.dto;


/**
 * <b> Entity para la publicacion de estudios. </b>
 *
 * @author Armary Buergo
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Armary Buergo $, $Date: 19/02/2016 $]
 *          </p>
 */

public class EntityProyectoParticipacionSocial {
    private String id;
    private String id_pps;
    private String codigo;
    private String nombre;
    private String sector;
    private String fecha;

    public EntityProyectoParticipacionSocial() {

    }
    public EntityProyectoParticipacionSocial(String id, String id_pps, String codigo, String nombre, String sector, String fecha) {
        this.id = id;
        this.id_pps = id_pps;
        this.codigo = codigo;
        this.nombre = nombre;
        this.sector = sector;
        this.fecha = fecha;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_pps() {
        return id_pps;
    }

    public void setId_pps(String id_pps) {
        this.id_pps = id_pps;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

}

