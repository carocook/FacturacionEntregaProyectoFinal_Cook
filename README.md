# 💻 Facturación Entrega Final - Proyecto Spring Boot

Este proyecto es la implementación de un sistema de facturación utilizando Spring Boot 3, Spring Data JPA, y una base de datos relacional. El objetivo principal fue construir una API RESTful que maneje la lógica transaccional (stock, precios) y cumpla con requisitos específicos de integración externa y validaciones de negocio.

## 🚀 Requisitos y Características Implementadas

El proyecto cumple con los siguientes puntos específicos de la entrega:

### 1. Estructura de Petición para Factura (DTOs Anidados)

Se diseñó la estructura de DTOs para recibir un JSON con la estructura anidada requerida, separando la información del cliente y las líneas de detalle:
* `FacturaRequestDTO` (Principal)
* `ClienteIdDTO`
* `ItemFacturaDTO`
* `ProductoIdDTO`

### 2. Validaciones de Stock y Lógica Transaccional

La lógica de negocio implementada en `FacturaService` garantiza la integridad transaccional:
* Verificación de que el **Cliente** y los **Productos** existan.
* Validación crítica: La **cantidad solicitada debe ser menor o igual al stock disponible**.
* Si la validación de stock falla en cualquier ítem, la transacción es **deshecha (rollback)**.
* Si la transacción es exitosa, el **stock de los productos se reduce** en la cantidad vendida.

### 3. Inmutabilidad del Precio de Venta

Para garantizar que el precio de venta de un producto no se altere si el precio del catálogo cambia después de la venta:
* Se agregó la columna `precioUnitario` a la tabla intermedia `FacturaProducto`.
* El precio del producto es **capturado y guardado** en `FacturaProducto` al momento de la creación de la factura (Punto 3).

### 4. Respuesta Enriquecida y Consumo de API Externa

El sistema se diseñó para ofrecer información completa en la respuesta y utilizar servicios externos:
* **Fecha de Comprobante:** La fecha de la factura (`Factura.fecha`) se obtiene consumiendo una API externa (`http://worldclockapi.com/`). Si la conexión falla, se utiliza la fecha local como fallback.
* **Respuesta Estándar:** El endpoint `POST /api/facturas/create` devuelve `FacturaResponseDTO`, que incluye el total de la venta, la cantidad total de productos vendidos y un mensaje de estado.
* **Manejo de Errores:** En caso de fallos (ej.: stock insuficiente), se devuelve un `HTTP 409 Conflict` o `404 Not Found` (Cliente/Producto no encontrado) y el `FacturaResponseDTO` contiene un mensaje descriptivo del error.

## ⚙️ Tecnologías

* **Lenguaje:** Java 17+
* **Framework:** Spring Boot 3.x
* **ORM:** Spring Data JPA / Hibernate
* **Base de Datos:** MySQL
* **Herramientas:** Lombok (simplificación de código), RestTemplate (consumo de API externa).

## 🗄️ Estructura de la Base de Datos (JPA Entities)

El proyecto utiliza una relación **Many-to-Many con datos adicionales** a través de una tabla intermedia.

### Tablas Principales:

* `Cliente`
* `Producto`
* `Factura`
* `Factura_Producto` (Tabla intermedia con `cantidad` y `precio_unitario`).

## 🔑 Endpoints de Facturación

A continuación, se detallan los endpoints principales y la estructura de la petición de creación de factura:

### 1. Crear Factura

Permite registrar una nueva venta, validando stock y actualizando la base de datos de manera transaccional.

| Método | URL | Descripción |
| :--- | :--- | :--- |
| **POST** | `/api/facturas/create` | Crea una nueva factura. |

**Cuerpo de la Petición (`FacturaRequestDTO`):**
```json
{
    "cliente": {
        "clienteid": 1 
    },
    "lineas": [
        {
            "cantidad": 3,
            "producto": {
                "productoid": 1 
            }
        },
        {
            "cantidad": 10,
            "producto": {
                "productoid": 5
            }
        }
    ]
}