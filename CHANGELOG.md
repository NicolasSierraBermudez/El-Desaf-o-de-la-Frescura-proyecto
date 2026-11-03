# Historial de cambios

Todos los cambios relevantes de **ERP Frescura Total** se documentan en este archivo.

## [Mejoras] - 2026-11-02

### Programación funcional y Stream API
- Se incorporó uso significativo de la **Stream API** y **expresiones lambda** en la lógica de
  negocio de los cuatro módulos (interfaces funcionales `Predicate`, `Function`, `Comparator`,
  `Consumer`):
  - **Inventario**: revisión diaria reescrita con `filter`/`forEach`; nuevos reportes
    `countLotsByStatus()` (`groupingBy`+`counting`), `lotsExpiringWithin()` (FEFO con
    `Comparator.comparing`), `totalStockValue()` y `totalWasteUnits()` (`map`/`reduce`).
  - **Ventas**: `suggestFefoLot()` con `min(Comparator...)`, totales de la venta con un helper
    funcional `sumBy(...)` (`map`/`reduce`), y reportes `totalSalesAmount()` /
    `countSalesByStatus()`.
  - **Proveedores**: `productsOfSupplier()` con `map`/`flatMap`, y `countProductsByCategory()`.
  - **Logística**: `completeRoute()` con `filter`/`count` y `pendingDispatchValue()` con
    `map`/`reduce`.
- Nuevas opciones de menú **"Reporte de inventario"** y **"Reporte de ventas"** que muestran
  estos agregados al usuario.

### Manejo de archivos (logging)
- Nueva utilidad **`common/AppLogger`** que escribe un registro de eventos a **`logs/erp.log`**
  con `java.util.logging` (timestamp + nivel) y crea la carpeta `logs/` con `java.nio.file.Files`.
- Se registran eventos en operaciones clave (recepción de lotes, revisión diaria, cuarentenas,
  inspecciones, mermas, ventas, facturas, rutas) y en los **errores de base de datos**.
- `logs/` se añadió al `.gitignore` (no se versiona la bitácora, sí el código que la genera).

### Documentación
- README ampliado con la sección **"Programación funcional y manejo de logs"**.

## [Inicial] - 2026-10-30

Versión inicial del ERP de consola para la bodega de alimentos "Frescura Total", con cuatro módulos principales:

### Inventario
- Control de lotes con fecha de vencimiento y estados (Disponible, Próximo a Vencer, Vencido, En Cuarentena).
- Registro de cuarentena, inspecciones de calidad y mermas.

### Proveedores
- Maestro de proveedores y catálogo de productos.
- Relación producto–proveedor con el precio de suministro.

### Ventas
- Gestión de clientes (mayoristas y minoristas) y registro de ventas.
- Selección de lotes con política FEFO (primero en vencer, primero en salir).
- Promociones automáticas del 50 % para lotes próximos a vencer y cálculo de IVA diferenciado.
- Generación de facturas en PNG y PDF con código QR y CUFE simulado.

### Logística
- Gestión de conductores y vehículos.
- Rutas de entrega, asignación de rutas y relación ventas–ruta para el despacho.
