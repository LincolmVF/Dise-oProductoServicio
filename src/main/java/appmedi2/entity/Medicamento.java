package appmedi2.entity;



import jakarta.persistence.*;

@Entity
@Table(name = "medicamentos")
public class Medicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    private String descripcion;

    private Integer stock;

    // Nombres alternativos para reconocimiento de voz (separados por comas)
    private String nombresAlternativos;

    // Constructores
    public Medicamento() {}

    public Medicamento(String nombre, String descripcion, Integer stock, String nombresAlternativos) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.stock = stock;
        this.nombresAlternativos = nombresAlternativos;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getNombresAlternativos() {
        return nombresAlternativos;
    }

    public void setNombresAlternativos(String nombresAlternativos) {
        this.nombresAlternativos = nombresAlternativos;
    }

    // toString
    @Override
    public String toString() {
        return "Medicamento{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", stock=" + stock +
                ", nombresAlternativos='" + nombresAlternativos + '\'' +
                '}';
    }

    // equals y hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Medicamento that = (Medicamento) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}