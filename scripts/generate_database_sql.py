#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Genera docs/database.sql para el proyecto "ERP Frescura Total".

El archivo resultante tiene DOS secciones:
  (A) DDL: tomado VERBATIM del script fuente del esquema entregado por el docente
      (/home/ubuntu/Uploads/Script Proyecto ERP Frescura Total.sql), incluidos los
      INSERT de los catalogos esenciales que ya trae.
  (B) DML: datos de prueba coherentes (>=50 registros en cada tabla principal)
      generados proceduralmente para garantizar volumen e integridad referencial.

REQUISITO DE AUDITORIA (NTC-ISO/IEC 25010): el proveedor id_proveedor = 33 DEBE
tener nit = 'SYN-FT-900427-AG'. Los demas usan NIT colombiano numerico (NNNNNNNNN-D).

Uso:  python3 scripts/generate_database_sql.py
"""

import os
import random
from datetime import date, timedelta

random.seed(20240427)  # reproducibilidad

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SCHEMA_SRC = "/home/ubuntu/Uploads/Script Proyecto ERP Frescura Total.sql"
OUTPUT = os.path.join(BASE_DIR, "docs", "database.sql")

TODAY = date(2026, 10, 29)

# ---------------- Volumenes ----------------
N_SUPPLIERS = 60
N_PRODUCTS = 60
N_CUSTOMERS = 60
N_LOTS = 90
N_SALES = 60
N_DRIVERS = 18
N_VEHICLES = 18
N_ROUTES = 25

# Catalogos ya poblados por el DDL
N_CATEGORIES = 8        # 1 Frutas .. 8 Panaderia
LOT_STATES = {1: "Disponible", 2: "Proximo a Vencer", 3: "Vencido", 4: "En Cuarentena"}
CUSTOMER_TYPES = [1, 2]  # 1 Mayorista, 2 Minorista
SALE_STATES = [1, 2, 3, 4, 5]  # Registrada, En Preparacion, En Transito, Entregada, Cancelada


def sql_str(value):
    """Escapa y encomilla un valor de texto para SQL; NULL si es None."""
    if value is None:
        return "NULL"
    return "'" + str(value).replace("\\", "\\\\").replace("'", "''") + "'"


def dec(value):
    return f"{value:.2f}"


def colombian_nit():
    """Genera un NIT colombiano NNNNNNNNN-D con digito de verificacion valido."""
    digits = [random.randint(0, 9) for _ in range(9)]
    while digits[0] == 0:
        digits[0] = random.randint(1, 9)
    weights = [41, 37, 29, 23, 19, 17, 13, 7, 3]
    total = sum(w * d for w, d in zip(weights, digits))
    rem = total % 11
    dv = rem if rem in (0, 1) else 11 - rem
    return "".join(str(d) for d in digits) + "-" + str(dv)


FIRST_NAMES = ["Juan", "Maria", "Carlos", "Ana", "Luis", "Laura", "Andres", "Sofia",
               "Diego", "Valentina", "Jorge", "Camila", "Miguel", "Daniela", "Pedro",
               "Lucia", "Felipe", "Natalia", "Santiago", "Paula"]
LAST_NAMES = ["Gomez", "Rodriguez", "Martinez", "Lopez", "Garcia", "Perez", "Sanchez",
              "Ramirez", "Torres", "Diaz", "Vargas", "Castro", "Rojas", "Moreno",
              "Munoz", "Ortiz", "Gutierrez", "Jimenez", "Alvarez", "Mendoza"]
COMPANY_SUFFIX = ["S.A.S.", "Ltda.", "S.A.", "E.U.", "& Cia."]
COMPANY_WORDS = ["Agro", "Frutas", "Campo", "Distribuidora", "Alimentos", "Verde",
                 "Cosecha", "Fresco", "Andes", "Valle", "Sabana", "Natural", "Tropical",
                 "Granja", "Horizonte", "Pacifico", "Dorado", "Sierra", "Oriente", "Sur"]
CITIES = ["Bogota", "Medellin", "Cali", "Barranquilla", "Bucaramanga", "Pereira",
          "Manizales", "Cartagena", "Cucuta", "Ibague", "Villavicencio", "Neiva"]

CATEGORY_PRODUCTS = {
    1: ["Manzana", "Banano", "Pera", "Uva", "Fresa", "Mango", "Naranja", "Papaya", "Piña", "Mora"],
    2: ["Tomate", "Lechuga", "Zanahoria", "Cebolla", "Papa", "Brocoli", "Espinaca", "Pepino", "Pimenton", "Apio"],
    3: ["Leche entera", "Queso campesino", "Yogurt", "Mantequilla", "Kumis", "Crema de leche", "Cuajada", "Arequipe"],
    4: ["Carne de res", "Pollo entero", "Cerdo", "Pechuga", "Costilla", "Molida", "Chorizo", "Tocino"],
    5: ["Arroz", "Frijol", "Lenteja", "Garbanzo", "Maiz", "Arveja", "Avena", "Quinua"],
    6: ["Jugo de naranja", "Agua mineral", "Gaseosa", "Te frio", "Limonada", "Malta", "Jugo de mango"],
    7: ["Atun enlatado", "Sardinas", "Maiz dulce", "Duraznos en lata", "Frijoles en lata", "Champiñones"],
    8: ["Pan tajado", "Croissant", "Mogolla", "Baguette", "Pan integral", "Tostadas", "Ponque"],
}
UNITS = {1: "kg", 2: "kg", 3: "litro", 4: "kg", 5: "kg", 6: "litro", 7: "unidad", 8: "unidad"}

AISLES = ["A", "B", "C", "D", "E", "F"]


def build_suppliers():
    rows = []
    used_nits = set()
    for i in range(1, N_SUPPLIERS + 1):
        name = f"{random.choice(COMPANY_WORDS)} {random.choice(COMPANY_WORDS)} {random.choice(COMPANY_SUFFIX)}"
        if i == 33:
            nit = "SYN-FT-900427-AG"  # REQUISITO DE AUDITORIA OBLIGATORIO
        else:
            nit = colombian_nit()
            while nit in used_nits:
                nit = colombian_nit()
        used_nits.add(nit)
        contact = f"{random.choice(FIRST_NAMES)} {random.choice(LAST_NAMES)}"
        slug = name.split()[0].lower()
        email = f"ventas{i}@{slug}.com"
        phone = f"3{random.randint(10,29)}{random.randint(1000000,9999999)}"
        address = f"Calle {random.randint(1,120)} #{random.randint(1,80)}-{random.randint(1,99)}"
        city = random.choice(CITIES)
        active = "TRUE" if random.random() > 0.1 else "FALSE"
        rows.append((name, nit, contact, email, phone, f"{address}, {city}", active))
    return rows


def build_products():
    rows = []
    for i in range(1, N_PRODUCTS + 1):
        cat = random.randint(1, N_CATEGORIES)
        base = random.choice(CATEGORY_PRODUCTS[cat])
        name = f"{base} {random.choice(['Premium','Nacional','Importado','Organico','Seleccion','Clasico'])}"
        desc = f"{base} de categoria {cat}, calidad controlada."
        unit = UNITS[cat]
        price = round(random.uniform(1500, 45000), 2)
        rows.append((name, desc, cat, unit, price))
    return rows


def build_product_supplier():
    pairs = set()
    for product_id in range(1, N_PRODUCTS + 1):
        for _ in range(random.randint(1, 3)):
            pairs.add((random.randint(1, N_SUPPLIERS), product_id))
    return sorted(pairs)


def build_customers():
    rows = []
    used_doc = set()
    for i in range(1, N_CUSTOMERS + 1):
        type_id = random.choice(CUSTOMER_TYPES)
        if type_id == 1:
            name = f"{random.choice(COMPANY_WORDS)} Market {random.choice(COMPANY_SUFFIX)}"
            doc = colombian_nit()
        else:
            name = f"{random.choice(FIRST_NAMES)} {random.choice(LAST_NAMES)} {random.choice(LAST_NAMES)}"
            doc = str(random.randint(10000000, 1200000000))
        while doc in used_doc:
            doc = str(random.randint(10000000, 1200000000))
        used_doc.add(doc)
        email = f"cliente{i}@correo.com"
        phone = f"3{random.randint(10,29)}{random.randint(1000000,9999999)}"
        address = f"Carrera {random.randint(1,120)} #{random.randint(1,80)}-{random.randint(1,99)}, {random.choice(CITIES)}"
        rows.append((name, doc, email, phone, address, type_id))
    return rows


def build_lots():
    """Devuelve filas de lotes con estado coherente respecto a la fecha de vencimiento."""
    rows = []
    used_numbers = set()
    for i in range(1, N_LOTS + 1):
        product_id = random.randint(1, N_PRODUCTS)
        supplier_id = random.randint(1, N_SUPPLIERS)
        # Fechas de vencimiento variadas: vencidos, proximos a vencer y vigentes.
        bucket = random.random()
        if bucket < 0.2:
            exp = TODAY - timedelta(days=random.randint(1, 40))      # vencido
        elif bucket < 0.45:
            exp = TODAY + timedelta(days=random.randint(0, 7))       # proximo a vencer
        else:
            exp = TODAY + timedelta(days=random.randint(8, 180))     # vigente
        entry = exp - timedelta(days=random.randint(10, 90))
        if entry > TODAY:
            entry = TODAY - timedelta(days=random.randint(1, 10))
        days_to_exp = (exp - TODAY).days
        # Estado coherente
        if random.random() < 0.08:
            state = 4  # En Cuarentena (independiente de la fecha)
        elif days_to_exp < 0:
            state = 3  # Vencido
        elif days_to_exp <= 7:
            state = 2  # Proximo a Vencer
        else:
            state = 1  # Disponible
        initial = round(random.uniform(50, 500), 2)
        if state in (3, 4):
            quantity = round(initial * random.uniform(0.3, 1.0), 2)
        else:
            quantity = round(initial * random.uniform(0.2, 1.0), 2)
        purchase = round(random.uniform(1000, 30000), 2)
        sale = round(purchase * random.uniform(1.2, 1.8), 2)
        datestr = entry.strftime("%Y%m%d")
        number = f"LOT-{datestr}-{i:04d}"
        while number in used_numbers:
            number = f"LOT-{datestr}-{random.randint(0,9999):04d}"
        used_numbers.add(number)
        aisle = f"Pasillo {random.choice(AISLES)}"
        shelf = f"Estante {random.randint(1,20)}"
        rows.append({
            "number": number, "product_id": product_id, "supplier_id": supplier_id,
            "entry": entry, "exp": exp, "initial": initial, "quantity": quantity,
            "purchase": purchase, "sale": sale, "aisle": aisle, "shelf": shelf,
            "state": state,
        })
    return rows


def tax_rate_for_product(product_id, product_category):
    cat = product_category[product_id]
    return 0.05 if cat in (1, 2, 5) else 0.19


def build_sales(lots, product_category):
    """Genera ventas con sus detalles coherentes (totales = suma de lineas)."""
    sales = []
    details = []
    used_numbers = set()
    detail_id = 0
    for i in range(1, N_SALES + 1):
        customer_id = random.randint(1, N_CUSTOMERS)
        sale_date = TODAY - timedelta(days=random.randint(0, 120))
        status = random.choice(SALE_STATES)
        n_lines = random.randint(1, 4)
        subtotal = disc_total = tax_total = grand_total = 0.0
        sale_details = []
        for _ in range(n_lines):
            lot_idx = random.randint(0, len(lots) - 1)
            lot = lots[lot_idx]
            lot_id = lot_idx + 1  # id_lote autoincrement = indice + 1
            qty = round(random.uniform(1, 15), 2)
            unit_price = lot["sale"]
            base = round(unit_price * qty, 2)
            # Descuento automatico 50% si el lote esta Proximo a Vencer
            disc_rate = 0.50 if lot["state"] == 2 else 0.0
            disc_amount = round(base * disc_rate, 2)
            taxable = base - disc_amount
            tax_rate = tax_rate_for_product(lot["product_id"], product_category)
            tax_amount = round(taxable * tax_rate, 2)
            line_total = round(taxable + tax_amount, 2)
            detail_id += 1
            sale_details.append({
                "lot_id": lot_id, "qty": qty, "unit_price": unit_price,
                "disc_rate": disc_rate, "disc_amount": disc_amount,
                "tax_rate": tax_rate, "tax_amount": tax_amount,
                "subtotal": base, "line_total": line_total,
            })
            subtotal += base
            disc_total += disc_amount
            tax_total += tax_amount
            grand_total += line_total
        datestr = sale_date.strftime("%Y%m%d")
        number = f"VEN-{datestr}-{i:04d}"
        while number in used_numbers:
            number = f"VEN-{datestr}-{random.randint(0,9999):04d}"
        used_numbers.add(number)
        user = f"{random.choice(FIRST_NAMES).lower()}.{random.choice(LAST_NAMES).lower()}"
        sales.append({
            "number": number, "customer_id": customer_id, "date": sale_date,
            "subtotal": round(subtotal, 2), "disc_total": round(disc_total, 2),
            "tax_total": round(tax_total, 2), "grand_total": round(grand_total, 2),
            "status": status, "user": user,
        })
        for d in sale_details:
            d["sale_id"] = i
            details.append(d)
    return sales, details


def build_invoices(sales):
    """Factura para ventas que no esten Canceladas (estado 5)."""
    rows = []
    idx = 0
    for sale_id, sale in enumerate(sales, start=1):
        if sale["status"] == 5:
            continue
        idx += 1
        datestr = sale["date"].strftime("%Y%m%d")
        number = f"FAC-{datestr}-{idx:04d}"
        cufe = f"{random.getrandbits(256):064x}"
        qr = f"https://factura.frescuratotal.com/validar?cufe={cufe}"
        rows.append({
            "sale_id": sale_id, "number": number, "cufe": cufe, "qr": qr,
            "subtotal": sale["subtotal"], "disc_total": sale["disc_total"],
            "tax_total": sale["tax_total"], "grand_total": sale["grand_total"],
            "date": sale["date"],
        })
    return rows


def build_drivers():
    rows = []
    used = set()
    for i in range(1, N_DRIVERS + 1):
        name = f"{random.choice(FIRST_NAMES)} {random.choice(LAST_NAMES)}"
        lic = f"LIC{random.randint(100000, 999999)}"
        while lic in used:
            lic = f"LIC{random.randint(100000, 999999)}"
        used.add(lic)
        phone = f"3{random.randint(10,29)}{random.randint(1000000,9999999)}"
        email = f"conductor{i}@frescuratotal.com"
        active = "TRUE" if random.random() > 0.15 else "FALSE"
        rows.append((name, lic, phone, email, active))
    return rows


def build_vehicles():
    rows = []
    used = set()
    models = ["NPR", "Hino 300", "JAC 1040", "Kodiak", "Sprinter", "Daily", "FVR", "Canter"]
    states = ["Activo", "Activo", "Activo", "Inactivo", "Mantenimiento"]
    for i in range(1, N_VEHICLES + 1):
        letters = "".join(random.choice("ABCDEFGHJKLMNPQRSTUVWXYZ") for _ in range(3))
        plate = f"{letters}{random.randint(100,999)}"
        while plate in used:
            letters = "".join(random.choice("ABCDEFGHJKLMNPQRSTUVWXYZ") for _ in range(3))
            plate = f"{letters}{random.randint(100,999)}"
        used.add(plate)
        model = f"{random.choice(models)} {random.randint(2015, 2024)}"
        capacity = round(random.uniform(800, 6000), 2)
        state = random.choice(states)
        rows.append((plate, model, capacity, state))
    return rows


def build_routes():
    rows = []
    states = ["Planificada", "En Transito", "Completada", "Completada", "Cancelada"]
    for i in range(1, N_ROUTES + 1):
        name = f"Ruta {random.choice(CITIES)} {i:02d}"
        user = f"{random.choice(FIRST_NAMES).lower()}.logistica"
        state = random.choice(states)
        rows.append((name, user, state))
    return rows


def main():
    suppliers = build_suppliers()
    products = build_products()
    product_category = {i + 1: products[i][2] for i in range(len(products))}
    prod_sup = build_product_supplier()
    customers = build_customers()
    lots = build_lots()
    sales, details = build_sales(lots, product_category)
    invoices = build_invoices(sales)
    drivers = build_drivers()
    vehicles = build_vehicles()
    routes = build_routes()

    # Registros de calidad coherentes
    quarantine_lots = [i + 1 for i, l in enumerate(lots) if l["state"] == 4]
    # aseguramos minimo de registros aunque haya pocos en cuarentena
    extra_q = [random.randint(1, N_LOTS) for _ in range(max(0, 15 - len(quarantine_lots)))]
    quarantine_targets = quarantine_lots + extra_q

    with open(SCHEMA_SRC, "r", encoding="utf-8") as fh:
        ddl = fh.read()

    out = []
    out.append("-- =====================================================================")
    out.append("-- ERP FRESCURA TOTAL - Script completo de base de datos")
    out.append("-- Autor del proyecto: Nicolas Sierra Bermudez")
    out.append("-- Archivo generado por scripts/generate_database_sql.py")
    out.append("--")
    out.append("-- Contiene DOS secciones:")
    out.append("--   (A) DDL  : estructura de la base de datos (tomada del script fuente).")
    out.append("--   (B) DML  : datos de prueba coherentes (>=50 registros por tabla principal).")
    out.append("-- =====================================================================")
    out.append("")
    out.append("-- #####################################################################")
    out.append("-- # (A) DDL - ESTRUCTURA (fuente: Script Proyecto ERP Frescura Total) #")
    out.append("-- #####################################################################")
    out.append("")
    out.append(ddl.rstrip())
    out.append("")
    out.append("")
    out.append("-- #####################################################################")
    out.append("-- # (B) DML - DATOS DE PRUEBA (generados proceduralmente, coherentes)  #")
    out.append("-- #####################################################################")
    out.append("")
    out.append("USE erp_frescura_total;")
    out.append("")

    # proveedores
    out.append("-- Proveedores (>=50). NOTA DE AUDITORIA: id_proveedor=33 usa NIT 'SYN-FT-900427-AG'.")
    out.append("INSERT INTO proveedores (nombre, nit, persona_contacto, email, telefono, direccion, activo) VALUES")
    vals = []
    for (name, nit, contact, email, phone, address, active) in suppliers:
        vals.append(f"({sql_str(name)}, {sql_str(nit)}, {sql_str(contact)}, {sql_str(email)}, "
                    f"{sql_str(phone)}, {sql_str(address)}, {active})")
    out.append(",\n".join(vals) + ";")
    out.append("")

    # productos
    out.append("-- Productos (>=50)")
    out.append("INSERT INTO productos (nombre, descripcion, id_categoria, unidad_medida, precio_base) VALUES")
    vals = []
    for (name, desc, cat, unit, price) in products:
        vals.append(f"({sql_str(name)}, {sql_str(desc)}, {cat}, {sql_str(unit)}, {dec(price)})")
    out.append(",\n".join(vals) + ";")
    out.append("")

    # productos_proveedor
    out.append("-- Catalogo producto-proveedor")
    out.append("INSERT INTO productos_proveedor (id_proveedor, id_producto) VALUES")
    vals = [f"({sid}, {pid})" for (sid, pid) in prod_sup]
    out.append(",\n".join(vals) + ";")
    out.append("")

    # clientes
    out.append("-- Clientes (>=50)")
    out.append("INSERT INTO clientes (nombre, documento, email, telefono, direccion, id_tipo) VALUES")
    vals = []
    for (name, doc, email, phone, address, type_id) in customers:
        vals.append(f"({sql_str(name)}, {sql_str(doc)}, {sql_str(email)}, {sql_str(phone)}, "
                    f"{sql_str(address)}, {type_id})")
    out.append(",\n".join(vals) + ";")
    out.append("")

    # lotes_inventario
    out.append("-- Lotes de inventario (>=50). Fechas y estados coherentes (vencidos/proximos/vigentes).")
    out.append("INSERT INTO lotes_inventario (numero_lote, id_producto, id_proveedor, fecha_ingreso, "
               "fecha_vencimiento, cantidad_inicial, cantidad, precio_compra, precio_venta, pasillo, "
               "estante, id_estado) VALUES")
    vals = []
    for l in lots:
        vals.append(f"({sql_str(l['number'])}, {l['product_id']}, {l['supplier_id']}, "
                    f"{sql_str(l['entry'].strftime('%Y-%m-%d %H:%M:%S'))}, "
                    f"{sql_str(l['exp'].strftime('%Y-%m-%d'))}, {dec(l['initial'])}, {dec(l['quantity'])}, "
                    f"{dec(l['purchase'])}, {dec(l['sale'])}, {sql_str(l['aisle'])}, "
                    f"{sql_str(l['shelf'])}, {l['state']})")
    out.append(",\n".join(vals) + ";")
    out.append("")

    # promociones
    out.append("-- Promociones (incluye la promo automatica 50% para lotes Proximos a Vencer)")
    out.append("INSERT INTO promociones (nombre, descripcion, tasa_descuento, aplica_estado_lote, "
               "aplica_categoria_producto, activa, fecha_inicio, fecha_fin) VALUES")
    promo_vals = [
        f"('Descuento proximo a vencer', 'Descuento automatico del 50% para lotes proximos a vencer', "
        f"0.50, 2, NULL, TRUE, '2026-01-01', '2026-12-31')",
        f"('Promo frutas', 'Descuento en frutas seleccionadas', 0.15, NULL, 1, TRUE, '2026-01-01', '2026-12-31')",
        f"('Promo verduras', 'Descuento en verduras', 0.10, NULL, 2, TRUE, '2026-01-01', '2026-12-31')",
        f"('Liquidacion granos', 'Descuento en granos', 0.20, NULL, 5, TRUE, '2026-01-01', '2026-12-31')",
        f"('Promo lacteos vencimiento', 'Descuento lacteos proximos a vencer', 0.30, 2, 3, FALSE, '2026-01-01', '2026-06-30')",
    ]
    out.append(",\n".join(promo_vals) + ";")
    out.append("")

    # ventas
    out.append("-- Ventas (>=50). Totales = suma de las lineas de detalle.")
    out.append("INSERT INTO ventas (numero_venta, id_cliente, fecha_venta, subtotal, total_descuento, "
               "total_impuesto, monto_total, id_estado, usuario_registro) VALUES")
    vals = []
    for s in sales:
        vals.append(f"({sql_str(s['number'])}, {s['customer_id']}, "
                    f"{sql_str(s['date'].strftime('%Y-%m-%d %H:%M:%S'))}, {dec(s['subtotal'])}, "
                    f"{dec(s['disc_total'])}, {dec(s['tax_total'])}, {dec(s['grand_total'])}, "
                    f"{s['status']}, {sql_str(s['user'])})")
    out.append(",\n".join(vals) + ";")
    out.append("")

    # detalles_venta
    out.append("-- Detalles de venta (FK validas hacia ventas y lotes)")
    out.append("INSERT INTO detalles_venta (id_venta, id_lote, cantidad_vendida, precio_unitario, "
               "descuento_aplicado, monto_descuento, tasa_impuesto, monto_impuesto, subtotal, total_linea) VALUES")
    vals = []
    for d in details:
        vals.append(f"({d['sale_id']}, {d['lot_id']}, {dec(d['qty'])}, {dec(d['unit_price'])}, "
                    f"{dec(d['disc_rate'])}, {dec(d['disc_amount'])}, {dec(d['tax_rate'])}, "
                    f"{dec(d['tax_amount'])}, {dec(d['subtotal'])}, {dec(d['line_total'])})")
    out.append(",\n".join(vals) + ";")
    out.append("")

    # facturas
    out.append("-- Facturas electronicas (una por venta no cancelada; CUFE simulado unico)")
    out.append("INSERT INTO facturas (id_venta, numero_factura, cufe, enlace_qr, fecha_emision, "
               "subtotal, total_descuento, monto_impuesto, monto_total) VALUES")
    vals = []
    for f in invoices:
        vals.append(f"({f['sale_id']}, {sql_str(f['number'])}, {sql_str(f['cufe'])}, {sql_str(f['qr'])}, "
                    f"{sql_str(f['date'].strftime('%Y-%m-%d %H:%M:%S'))}, {dec(f['subtotal'])}, "
                    f"{dec(f['disc_total'])}, {dec(f['tax_total'])}, {dec(f['grand_total'])})")
    out.append(",\n".join(vals) + ";")
    out.append("")

    # conductores
    out.append("-- Conductores")
    out.append("INSERT INTO conductores (nombre, numero_licencia, telefono, email, activo) VALUES")
    vals = [f"({sql_str(n)}, {sql_str(l)}, {sql_str(p)}, {sql_str(e)}, {a})"
            for (n, l, p, e, a) in drivers]
    out.append(",\n".join(vals) + ";")
    out.append("")

    # vehiculos
    out.append("-- Vehiculos")
    out.append("INSERT INTO vehiculos (placa, modelo, capacidad, estado) VALUES")
    vals = [f"({sql_str(pl)}, {sql_str(m)}, {dec(c)}, {sql_str(st)})"
            for (pl, m, c, st) in vehicles]
    out.append(",\n".join(vals) + ";")
    out.append("")

    # rutas_entrega
    out.append("-- Rutas de entrega")
    out.append("INSERT INTO rutas_entrega (nombre_ruta, usuario_creacion, estado) VALUES")
    vals = [f"({sql_str(n)}, {sql_str(u)}, {sql_str(st)})" for (n, u, st) in routes]
    out.append(",\n".join(vals) + ";")
    out.append("")

    # asignaciones_entrega (una por ruta)
    out.append("-- Asignaciones de entrega (vehiculo + conductor por ruta)")
    out.append("INSERT INTO asignaciones_entrega (id_ruta, id_vehiculo, id_conductor, usuario_asignacion, notas) VALUES")
    vals = []
    for route_id in range(1, N_ROUTES + 1):
        vid = random.randint(1, N_VEHICLES)
        did = random.randint(1, N_DRIVERS)
        vals.append(f"({route_id}, {vid}, {did}, {sql_str('coordinador.logistica')}, "
                    f"{sql_str('Asignacion automatica de prueba')})")
    out.append(",\n".join(vals) + ";")
    out.append("")

    # ventas_ruta: asigna ventas (En Transito/Entregada) a rutas, par unico (id_ruta,id_venta)
    out.append("-- Ventas asignadas a rutas (par id_ruta,id_venta unico)")
    out.append("INSERT INTO ventas_ruta (id_ruta, id_venta, orden_entrega, direccion_destino, recibido_por) VALUES")
    vals = []
    used_pairs = set()
    deliverable_sales = [i + 1 for i, s in enumerate(sales) if s["status"] in (3, 4)]
    if not deliverable_sales:
        deliverable_sales = list(range(1, N_SALES + 1))
    for sale_id in deliverable_sales:
        route_id = random.randint(1, N_ROUTES)
        if (route_id, sale_id) in used_pairs:
            continue
        used_pairs.add((route_id, sale_id))
        order = random.randint(1, 10)
        addr = f"Calle {random.randint(1,120)} #{random.randint(1,80)}-{random.randint(1,99)}, {random.choice(CITIES)}"
        received = f"{random.choice(FIRST_NAMES)} {random.choice(LAST_NAMES)}" if random.random() > 0.5 else None
        vals.append(f"({route_id}, {sale_id}, {order}, {sql_str(addr)}, {sql_str(received)})")
    out.append(",\n".join(vals) + ";")
    out.append("")

    # registro_cuarentena
    out.append("-- Registros de cuarentena (lotes bloqueados pendientes de inspeccion)")
    out.append("INSERT INTO registro_cuarentena (id_lote, motivo, usuario, resuelta) VALUES")
    reasons = ["Empaque dañado", "Sospecha de contaminacion", "Temperatura fuera de rango",
               "Revision sanitaria", "Diferencia en inventario fisico"]
    vals = []
    for lot_id in quarantine_targets:
        resolved = "TRUE" if random.random() > 0.6 else "FALSE"
        vals.append(f"({lot_id}, {sql_str(random.choice(reasons))}, {sql_str('inspector.calidad')}, {resolved})")
    out.append(",\n".join(vals) + ";")
    out.append("")

    # inspecciones_calidad
    out.append("-- Inspecciones de calidad")
    out.append("INSERT INTO inspecciones_calidad (id_lote, resultado, nombre_inspector, motivo_rechazo, notas) VALUES")
    vals = []
    inspection_targets = [random.randint(1, N_LOTS) for _ in range(20)]
    reject_reasons = ["Producto en mal estado", "No cumple estandar", "Fecha de vencimiento alterada",
                      "Contaminacion cruzada", "Peso incorrecto"]
    for lot_id in inspection_targets:
        approved = random.random() > 0.4
        result = "Aprobado" if approved else "Rechazado"
        reason = None if approved else random.choice(reject_reasons)
        notes = "Inspeccion rutinaria de calidad."
        inspector = f"{random.choice(FIRST_NAMES)} {random.choice(LAST_NAMES)}"
        vals.append(f"({lot_id}, {sql_str(result)}, {sql_str(inspector)}, {sql_str(reason)}, {sql_str(notes)})")
    out.append(",\n".join(vals) + ";")
    out.append("")

    # registro_mermas
    out.append("-- Registros de mermas (algunas vinculadas a inspecciones)")
    out.append("INSERT INTO registro_mermas (id_lote, id_inspeccion, cantidad_perdida, motivo, usuario_registro) VALUES")
    vals = []
    merma_reasons = ["Producto vencido", "Daño en manipulacion", "Rechazo de calidad",
                     "Deterioro por temperatura", "Rotura de empaque"]
    n_inspections = len(inspection_targets)
    for _ in range(25):
        lot_id = random.randint(1, N_LOTS)
        if random.random() > 0.5 and n_inspections > 0:
            insp_id = random.randint(1, n_inspections)
        else:
            insp_id = None
        qty = round(random.uniform(1, 50), 2)
        insp_sql = str(insp_id) if insp_id is not None else "NULL"
        vals.append(f"({lot_id}, {insp_sql}, {dec(qty)}, {sql_str(random.choice(merma_reasons))}, "
                    f"{sql_str('operario.bodega')})")
    out.append(",\n".join(vals) + ";")
    out.append("")

    os.makedirs(os.path.dirname(OUTPUT), exist_ok=True)
    with open(OUTPUT, "w", encoding="utf-8") as fh:
        fh.write("\n".join(out))

    # Resumen por consola
    print("docs/database.sql generado.")
    print(f"  proveedores:        {len(suppliers)} (id_proveedor=33 nit='SYN-FT-900427-AG')")
    print(f"  productos:          {len(products)}")
    print(f"  productos_proveedor:{len(prod_sup)}")
    print(f"  clientes:           {len(customers)}")
    print(f"  lotes_inventario:   {len(lots)}")
    print(f"  ventas:             {len(sales)}")
    print(f"  detalles_venta:     {len(details)}")
    print(f"  facturas:           {len(invoices)}")
    print(f"  conductores:        {len(drivers)}")
    print(f"  vehiculos:          {len(vehicles)}")
    print(f"  rutas_entrega:      {len(routes)}")


if __name__ == "__main__":
    main()
