# 📝 Proyecto Facturación | Proyecto Final

## 1. Descripción del Proyecto
Este proyecto implementa un sistema básico de facturación utilizando **Spring Boot** y **Spring Data JPA/Hibernate**.  
El objetivo es:

- Gestionar la creación de **Facturas**.
- Registrar **Clientes** y **Productos**.
- Manejar la asociación de productos a una factura mediante una **Entidad de Asociación (`FacturaProducto`)** que incluye campos de negocio como `cantidad` y `subtotal`.

El código incorpora las correcciones necesarias para manejar relaciones **Many-to-Many con campos extra** y evita los errores de recursividad en la serialización JSON.

---

## 2. Arquitectura y Modelo de Dominio
La característica clave del modelo es la relación entre **Factura** y **Producto**, implementada como una **Entidad de Asociación** para manejar los atributos extra (`cantidad`, `subtotal`).

### Relaciones Clave
| Entidad        | Mapeo                        | Nota                                                                 |
|----------------|------------------------------|----------------------------------------------------------------------|
| Factura        | `@OneToMany → FacturaProducto` | Administra la lista de ítems de la factura.                          |
| FacturaProducto| `@ManyToOne ← Factura`        | Rompe el ciclo de serialización con `@JsonBackReference`.            |
| FacturaProducto| `@ManyToOne → Producto`       | Referencia al producto.                                              |

---

## 3. Tecnologías y Configuración

### 3.1. Stack Tecnológico
- **Framework:** Spring Boot 3.x
- **Lenguaje:** Java
- **Persistencia:** Spring Data JPA / Hibernate
- **Base de Datos:** MySQL
- **Librerías:** Lombok, Jackson (Serialización)
