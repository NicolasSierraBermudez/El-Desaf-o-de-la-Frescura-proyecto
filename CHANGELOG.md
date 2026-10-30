# Historial de cambios

Todos los cambios relevantes de **ERP Frescura Total** se documentan en este archivo.

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
