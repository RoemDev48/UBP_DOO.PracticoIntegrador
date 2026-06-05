package com.mycompany.vitalsa.controller;

import com.mycompany.vitalsa.model.*;
import com.mycompany.vitalsa.dto.*;
import java.util.ArrayList;
import java.util.List;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author RRDev
*/

public class DatabaseController {

    private static final String URL = "jdbc:mysql://localhost:3306/vitalsa_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Por defecto en XAMPP es vacío

    private Connection conexion;

    public void conectar() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión exitosa a la base de datos.");
            
            try (java.sql.Statement stmt = conexion.createStatement()) {
                stmt.execute("ALTER TABLE cliente ADD COLUMN estado ENUM('ACTIVO', 'INACTIVO') NOT NULL DEFAULT 'ACTIVO'");
                System.out.println("Esquema actualizado: columna estado agregada en cliente.");
            } catch (SQLException e) {}
            
            try (java.sql.Statement stmt = conexion.createStatement()) {
                stmt.execute("ALTER TABLE pedido ADD COLUMN distribuidor_id INT NULL");
                stmt.execute("ALTER TABLE pedido ADD CONSTRAINT fk_pedido_distribuidor FOREIGN KEY (distribuidor_id) REFERENCES distribuidor(id)");
                System.out.println("Esquema actualizado: columna distribuidor_id agregada en pedido.");
            } catch (SQLException e) {}
            
            try (java.sql.Statement stmt = conexion.createStatement()) {
                stmt.execute("ALTER TABLE factura ADD COLUMN metodo_pago VARCHAR(50) NULL");
                System.out.println("Esquema actualizado: columna metodo_pago agregada en factura.");
            } catch (SQLException e) {}
            
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }

    public void desconectar() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("Conexión cerrada.");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar conexión: " + e.getMessage());
        }
    }
    
    public Connection getConexion() {
        return conexion;
    }

    // ==========================================
    // METODOS DE ACCESO A DATOS (GET)
    // ==========================================

    public List<ClienteDTO> obtenerClientes() {
        List<ClienteDTO> lista = new ArrayList<>();
        if (conexion == null) conectar();
        
        String sql = "SELECT c.id, c.tipo_cliente, c.nombre, c.apellido, c.nro_doc, c.nombre_fantasia, c.razon_social, c.cuit, c.estado, " +
                     "d.calle, d.numeracion, b.zona_id, z.nombre AS zona_nombre, t.numero AS telefono, t.tipo AS tipo_telefono, " +
                     "(SELECT DATE_FORMAT(MAX(fecha_realizacion), '%d %b, %Y') FROM pedido p WHERE p.cliente_id = c.id) AS ultimo_pedido " +
                     "FROM cliente c " +
                     "LEFT JOIN direccion d ON c.direccion_id = d.id " +
                     "LEFT JOIN barrio b ON d.barrio_id = b.id " +
                     "LEFT JOIN zona z ON b.zona_id = z.id " +
                     "LEFT JOIN telefono t ON c.telefono_id = t.id";
                     
        try (java.sql.Statement stmt = conexion.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String tipo = rs.getString("tipo_cliente");
                String estado = rs.getString("estado");
                String nombreMostrar = "";
                String docMostrar = "";

                if ("PARTICULAR".equals(tipo)) {
                    String nom = rs.getString("nombre");
                    String ape = rs.getString("apellido");
                    nombreMostrar = (nom != null ? nom : "") + " " + (ape != null ? ape : "");
                    nombreMostrar = nombreMostrar.trim();
                    docMostrar = rs.getString("nro_doc");
                    if (docMostrar == null) docMostrar = "";
                } else if ("EMPRESA".equals(tipo)) {
                    nombreMostrar = rs.getString("razon_social");
                    String fantasia = rs.getString("nombre_fantasia");
                    if (fantasia != null && !fantasia.trim().isEmpty()) {
                        nombreMostrar += " (" + fantasia.trim() + ")";
                    }
                    docMostrar = rs.getString("cuit");
                    if (docMostrar == null) docMostrar = "";
                }
                
                String dir = rs.getString("calle") != null ? rs.getString("calle") : "";
                String tel = rs.getString("telefono") != null ? rs.getString("telefono") : "";
                
                int num = rs.getInt("numeracion");
                int zId = rs.getInt("zona_id");
                String tipoTel = rs.getString("tipo_telefono") != null ? rs.getString("tipo_telefono") : "";
                
                String zonaNom = rs.getString("zona_nombre") != null ? rs.getString("zona_nombre") : "Sin Zona";
                String ultimoPed = rs.getString("ultimo_pedido") != null ? rs.getString("ultimo_pedido") : "Sin pedidos";
                
                lista.add(new ClienteDTO(id, nombreMostrar, docMostrar, tipo, dir, tel, num, zId, tipoTel, zonaNom, ultimoPed, estado));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener clientes: " + e.getMessage());
        }
        return lista;
    }

    public boolean cambiarEstadoCliente(int id, String nuevoEstado) {
        if (conexion == null) conectar();
        String sql = "UPDATE cliente SET estado = ? WHERE id = ?";
        try (java.sql.PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al cambiar estado: " + e.getMessage());
            return false;
        }
    }

    public List<ZonaDTO> obtenerZonas() {
        List<ZonaDTO> lista = new ArrayList<>();
        if (conexion == null) conectar();
        String sql = "SELECT id, nombre FROM zona";
        try (java.sql.Statement stmt = conexion.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new ZonaDTO(rs.getInt("id"), rs.getString("nombre")));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener zonas: " + e.getMessage());
        }
        return lista;
    }

    public boolean insertarCliente(String tipo, String nombre, String doc, String dir, int numCalle, int zonaId, String tel, String tipoTel) {
        if (conexion == null) conectar();
        try {
            conexion.setAutoCommit(false); 
            
            int telId = -1;
            int dirId = -1;
            int barrioId = 1; 
            
            // TODO: por ahora hardcodeamos el primer barrio de la zona, a futuro estaría bueno que el usuario elija el barrio exacto
            String sqlBarrio = "SELECT id FROM barrio WHERE zona_id = " + zonaId + " LIMIT 1";
            try (java.sql.Statement stmt = conexion.createStatement();
                 java.sql.ResultSet rs = stmt.executeQuery(sqlBarrio)) {
                if (rs.next()) barrioId = rs.getInt("id");
            }
            
            // 1. Primero metemos el teléfono en la DB para generar el ID
            String sqlTel = "INSERT INTO telefono (numero, tipo) VALUES (?, ?)";
            try (java.sql.PreparedStatement ps = conexion.prepareStatement(sqlTel, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, tel);
                ps.setString(2, tipoTel);
                ps.executeUpdate();
                try (java.sql.ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) telId = rs.getInt(1);
                }
            }
            
            // 2. Ahora armamos la dirección usando el barrio que encontramos arriba
            String sqlDir = "INSERT INTO direccion (calle, numeracion, barrio_id) VALUES (?, ?, ?)";
            try (java.sql.PreparedStatement ps = conexion.prepareStatement(sqlDir, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, dir);
                ps.setInt(2, numCalle);
                ps.setInt(3, barrioId);
                ps.executeUpdate();
                try (java.sql.ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) dirId = rs.getInt(1);
                }
            }
            
            // 3. Finalmente guardamos al cliente uniendo todas las FK (ojo con el tipo de cliente)
            String sqlCli;
            if ("PARTICULAR".equals(tipo)) {
                sqlCli = "INSERT INTO cliente (tipo_cliente, direccion_id, telefono_id, nombre, apellido, tipo_documento, nro_doc) VALUES (?, ?, ?, ?, '', 'DNI', ?)";
            } else {
                sqlCli = "INSERT INTO cliente (tipo_cliente, direccion_id, telefono_id, razon_social, nombre_fantasia, cuit) VALUES (?, ?, ?, ?, '', ?)";
            }
            
            try (java.sql.PreparedStatement ps = conexion.prepareStatement(sqlCli)) {
                ps.setString(1, tipo);
                if (dirId != -1) ps.setInt(2, dirId); else ps.setNull(2, java.sql.Types.INTEGER);
                if (telId != -1) ps.setInt(3, telId); else ps.setNull(3, java.sql.Types.INTEGER);
                ps.setString(4, nombre);
                ps.setString(5, doc);
                ps.executeUpdate();
            }
            
            conexion.commit();
            return true;
        } catch (SQLException e) {
            try { conexion.rollback(); } catch (SQLException ex) {}
            System.err.println("Error al insertar cliente: " + e.getMessage());
            return false;
        } finally {
            try { conexion.setAutoCommit(true); } catch (SQLException ex) {}
        }
    }

    public List<ProductoDTO> obtenerProductos() {
        List<ProductoDTO> lista = new ArrayList<>();
        if (conexion == null) conectar();
        
        // Traemos todos los productos (con join a presentacion porque es lo que vendemos)
        String sql = "SELECT pr.id, p.nombre AS categoria, pr.descripcion AS nombre, pr.precio, pr.stock " +
                     "FROM presentacion pr " +
                     "JOIN producto p ON pr.producto_id = p.id";
                     
        try (java.sql.Statement stmt = conexion.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String sku = String.format("#PRD-%03d", id);
                String categoria = rs.getString("categoria");
                String nombre = rs.getString("nombre");
                String precio = "$" + rs.getString("precio");
                int stock = rs.getInt("stock");
                
                lista.add(new ProductoDTO(id, sku, nombre, categoria, precio, stock));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener productos: " + e.getMessage());
        }
        return lista;
    }

    // Cuenta cuántos productos tenemos dados de alta
    public int contarProductosActivos() {
        return contar("SELECT COUNT(*) FROM presentacion");
    }


    // Actualiza los datos de un cliente (medio lio porque toca actualizar varias tablas)
    public boolean actualizarCliente(int id, String tipo, String nombre, String doc, String dir, int numCalle, int zonaId, String tel, String tipoTel) {
        if (conexion == null) conectar();
        try {
            conexion.setAutoCommit(false);
            
            int telId = -1, dirId = -1;
            String sqlSel = "SELECT telefono_id, direccion_id FROM cliente WHERE id = " + id;
            try (java.sql.Statement s = conexion.createStatement(); java.sql.ResultSet rs = s.executeQuery(sqlSel)) {
                if (rs.next()) {
                    telId = rs.getInt("telefono_id");
                    dirId = rs.getInt("direccion_id");
                }
            }
            
            if (telId != -1) {
                String sqlTel = "UPDATE telefono SET numero = ?, tipo = ? WHERE id = ?";
                try (java.sql.PreparedStatement ps = conexion.prepareStatement(sqlTel)) {
                    ps.setString(1, tel); ps.setString(2, tipoTel); ps.setInt(3, telId);
                    ps.executeUpdate();
                }
            }
            
            if (dirId != -1) {
                int barrioId = 1;
                String sqlBarrio = "SELECT id FROM barrio WHERE zona_id = " + zonaId + " LIMIT 1";
                try (java.sql.Statement stmt = conexion.createStatement(); java.sql.ResultSet rs = stmt.executeQuery(sqlBarrio)) {
                    if (rs.next()) barrioId = rs.getInt("id");
                }
                String sqlDir = "UPDATE direccion SET calle = ?, numeracion = ?, barrio_id = ? WHERE id = ?";
                try (java.sql.PreparedStatement ps = conexion.prepareStatement(sqlDir)) {
                    ps.setString(1, dir); ps.setInt(2, numCalle); ps.setInt(3, barrioId); ps.setInt(4, dirId);
                    ps.executeUpdate();
                }
            }
            
            String sqlCli = "PARTICULAR".equals(tipo) 
                ? "UPDATE cliente SET tipo_cliente = ?, nombre = ?, nro_doc = ? WHERE id = ?"
                : "UPDATE cliente SET tipo_cliente = ?, razon_social = ?, cuit = ? WHERE id = ?";
            try (java.sql.PreparedStatement ps = conexion.prepareStatement(sqlCli)) {
                ps.setString(1, tipo); ps.setString(2, nombre); ps.setString(3, doc); ps.setInt(4, id);
                ps.executeUpdate();
            }
            
            conexion.commit();
            return true;
        } catch (SQLException e) {
            try { conexion.rollback(); } catch (SQLException ex) {}
            return false;
        } finally {
            try { conexion.setAutoCommit(true); } catch (SQLException ex) {}
        }
    }

    // Devuelve el total de clientes para el dashboard
    public int contarClientesTotal() {
        return contar("SELECT COUNT(*) FROM cliente");
    }

    // Devuelve solo los clientes activos
    public int contarClientesActivos() {
        return contar("SELECT COUNT(*) FROM cliente WHERE estado = 'ACTIVO'");
    }

    private int contar(String sql) {
        if (conexion == null) conectar();
        try (java.sql.Statement stmt = conexion.createStatement(); java.sql.ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {}
        return 0;
    }

    // Cuenta las zonas que tenemos cargadas
    public int contarZonas() {
        return contar("SELECT COUNT(*) FROM zona");
    }

    public String obtenerTiempoUltimaActualizacion() {
        if (conexion == null) conectar();
        // Magia de MySQL: usamos TIMESTAMPDIFF para sacar los minutos directos
        String sql = "SELECT TIMESTAMPDIFF(MINUTE, MAX(fecha_realizacion), NOW()) as mins FROM pedido";
        
        // Verificamos si existe la columna en tiempo de ejecución (si no, la creamos)
        try (java.sql.Statement checkStmt = conexion.createStatement()) {
            checkStmt.executeQuery("SELECT ultima_actualizacion FROM presentacion LIMIT 1");
        } catch (SQLException e) {
            System.out.println("Migrando esquema: Creando columna ultima_actualizacion en presentacion...");
            try (java.sql.Statement alterStmt = conexion.createStatement()) {
                alterStmt.executeUpdate("ALTER TABLE presentacion ADD COLUMN ultima_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP");
            } catch (SQLException ex) {
                return "Desconocido";
            }
        }

        try (java.sql.Statement stmt = conexion.createStatement(); java.sql.ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                int mins = rs.getInt("mins");
                if (rs.wasNull()) return "Nunca";
                
                if (mins < 1) return "Justo ahora";
                if (mins < 60) return "Hace " + mins + " min";
                
                int horas = mins / 60;
                if (horas < 24) return "Hace " + horas + " h";
                
                int dias = horas / 24;
                return "Hace " + dias + " días";
            }
        } catch (SQLException e) {
            System.err.println("Error al calcular tiempo: " + e.getMessage());
        }
        return "Nunca";
    }

    public boolean insertarProducto(String categoria, String nombre, double precio, int stock) {
        if (conexion == null) conectar();
        try {
            // Buscamos si ya existe la categoría (producto padre)
            int productoId = -1;
            String sqlBusqueda = "SELECT id FROM producto WHERE nombre = '" + categoria + "'";
            try (java.sql.Statement stmt = conexion.createStatement();
                 java.sql.ResultSet rs = stmt.executeQuery(sqlBusqueda)) {
                if (rs.next()) {
                    productoId = rs.getInt("id");
                }
            }

            // Si no existe la categoría, la creamos al vuelo
            if (productoId == -1) {
                String sqlInsertProducto = "INSERT INTO producto (nombre) VALUES ('" + categoria + "')";
                try (java.sql.Statement stmt = conexion.createStatement()) {
                    stmt.executeUpdate(sqlInsertProducto, java.sql.Statement.RETURN_GENERATED_KEYS);
                    try (java.sql.ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) productoId = rs.getInt(1);
                    }
                }
            }

            // Ahora si, insertamos la presentación con su precio y stock
            if (productoId != -1) {
                String sqlInsert = "INSERT INTO presentacion (producto_id, descripcion, precio, stock) VALUES (" +
                                   productoId + ", '" + nombre + "', " + precio + ", " + stock + ")";
                try (java.sql.Statement stmt = conexion.createStatement()) {
                    return stmt.executeUpdate(sqlInsert) > 0;
                }
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error al insertar producto: " + e.getMessage());
            return false;
        }
    }

    // Actualiza solo el precio de un producto (desde el modal)
    public boolean actualizarPrecio(int id, double nuevoPrecio) {
        if (conexion == null) conectar();
        String sql = "UPDATE presentacion SET precio = " + nuevoPrecio + " WHERE id = " + id;
        try (java.sql.Statement stmt = conexion.createStatement()) {
            return stmt.executeUpdate(sql) > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar precio: " + e.getMessage());
            return false;
        }
    }

    // ================= MÉTODOS DE PEDIDOS (Acá arranca lo complejo) ================= //

    // KPIs generales para el tablero de pedidos
    public int contarPedidosTotales() {
        return contar("SELECT COUNT(*) FROM pedido");
    }

    // Pedidos que están pendientes de armado
    public int contarPedidosPendientes() {
        return contar("SELECT COUNT(*) FROM pedido WHERE estado = 'PENDIENTE'");
    }

    // Pedidos que ya salieron (ENVIADO o CONFIRMADO)
    public int contarPedidosEnTransito() {
        return contar("SELECT COUNT(*) FROM pedido WHERE estado = 'ENVIADO' OR estado = 'CONFIRMADO'");
    }

    // Pedidos que se entregaron HOY (ojo que compara con CURDATE)
    public int contarPedidosEntregadosHoy() {
        return contar("SELECT COUNT(*) FROM pedido WHERE estado = 'ENTREGADO' AND DATE(fecha_realizacion) = CURDATE()");
    }

    // Cambia el estado de un pedido (ej: de PENDIENTE a ENVIADO)
    public boolean cambiarEstadoPedido(int id, String nuevoEstado) {
        if (conexion == null) conectar();
        String sql = "UPDATE pedido SET estado = ? WHERE id = ?";
        try (java.sql.PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (java.sql.SQLException e) {
            System.err.println("Error al cambiar estado pedido: " + e.getMessage());
            return false;
        }
    }

    // Trae los pedidos pero filtrados por la combobox de arriba
    public List<com.mycompany.vitalsa.dto.PedidoDTO> obtenerPedidos(String filtroEstado) {
        List<com.mycompany.vitalsa.dto.PedidoDTO> lista = new ArrayList<>();
        if (conexion == null) conectar();
        
        try (java.sql.Statement alterStmt = conexion.createStatement()) {
            alterStmt.executeUpdate("ALTER TABLE pedido MODIFY COLUMN estado ENUM('PENDIENTE', 'CONFIRMADO', 'EN_PREPARACION', 'ENVIADO', 'ENTREGADO', 'NO_ENTREGADO', 'CANCELADO') NOT NULL DEFAULT 'PENDIENTE'");
        } catch (SQLException e) {}
        
        String sql = "SELECT p.id, p.fecha_realizacion, p.estado, p.monto_total, " +
                     "COALESCE(NULLIF(c.nombre_fantasia, ''), NULLIF(c.razon_social, ''), CONCAT(c.nombre, ' ', c.apellido)) AS cliente_nombre " +
                     "FROM pedido p " +
                     "JOIN cliente c ON p.cliente_id = c.id";
                     
        if (filtroEstado != null && !filtroEstado.equals("Todos los estados")) {
            sql += " WHERE p.estado = '" + filtroEstado.replace(" ", "_").toUpperCase() + "'";
        }
        
        sql += " ORDER BY p.fecha_realizacion DESC";
        
        try (java.sql.Statement stmt = conexion.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(sql)) {
             
            String[] meses = {"Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};
             
            while (rs.next()) {
                int id = rs.getInt("id");
                String sku = String.format("#ORD-2024-%03d", id);
                String clienteNombre = rs.getString("cliente_nombre");
                if (clienteNombre == null) clienteNombre = "Cliente #" + id;
                
                java.sql.Timestamp fechaSql = rs.getTimestamp("fecha_realizacion");
                String fechaFormat = "";
                if (fechaSql != null) {
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    cal.setTime(fechaSql);
                    int dia = cal.get(java.util.Calendar.DAY_OF_MONTH);
                    String mes = meses[cal.get(java.util.Calendar.MONTH)];
                    int anio = cal.get(java.util.Calendar.YEAR);
                    fechaFormat = dia + " " + mes + " " + anio;
                }
                
                String estado = rs.getString("estado").replace("_", " ");
                String total = "$" + String.format("%.2f", rs.getDouble("monto_total"));
                
                lista.add(new com.mycompany.vitalsa.dto.PedidoDTO(id, sku, clienteNombre, fechaFormat, estado, total));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener pedidos: " + e.getMessage());
        }
        return lista;
    }

    // Guarda el pedido y sus detalles todo en una misma transacción para no romper nada
    public int guardarPedidoConRetorno(int clienteId, int operadorId, double montoTotal, List<com.mycompany.vitalsa.dto.DetallePedidoDTO> detalles) {
        if (conexion == null) conectar();
        try {
            conexion.setAutoCommit(false);
            
            String sqlPedido = "INSERT INTO pedido (operador_id, cliente_id, estado, monto_total) VALUES (?, ?, 'PENDIENTE', ?)";
            int pedidoId = -1;
            try (java.sql.PreparedStatement pstmt = conexion.prepareStatement(sqlPedido, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, operadorId);
                pstmt.setInt(2, clienteId);
                pstmt.setDouble(3, montoTotal);
                pstmt.executeUpdate();
                
                try (java.sql.ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        pedidoId = rs.getInt(1);
                    }
                }
            }
            
            if (pedidoId == -1) {
                conexion.rollback();
                return -1;
            }
            
            // Vamos insertando detalle por detalle...
            String sqlDetalle = "INSERT INTO detalle_pedido (pedido_id, presentacion_id, cantidad, precio_venta, subtotal) VALUES (?, ?, ?, ?, ?)";
            // ... y de paso descontamos el stock (espero que no quede en negativo jaja)
            String sqlUpdateStock = "UPDATE presentacion SET stock = stock - ? WHERE id = ?";
            
            try (java.sql.PreparedStatement pstmtDetalle = conexion.prepareStatement(sqlDetalle);
                 java.sql.PreparedStatement pstmtStock = conexion.prepareStatement(sqlUpdateStock)) {
                
                for (com.mycompany.vitalsa.dto.DetallePedidoDTO detalle : detalles) {
                    pstmtDetalle.setInt(1, pedidoId);
                    pstmtDetalle.setInt(2, detalle.getProductoId());
                    pstmtDetalle.setInt(3, detalle.getCantidad());
                    pstmtDetalle.setDouble(4, detalle.getPrecioUnitario());
                    pstmtDetalle.setDouble(5, detalle.getSubtotal());
                    pstmtDetalle.addBatch();
                    
                    pstmtStock.setInt(1, detalle.getCantidad());
                    pstmtStock.setInt(2, detalle.getProductoId());
                    pstmtStock.addBatch();
                }
                
                pstmtDetalle.executeBatch();
                pstmtStock.executeBatch();
            }
            
            conexion.commit();
            return pedidoId;
            
        } catch (java.sql.SQLException e) {
            System.err.println("Error al guardar pedido transaccional: " + e.getMessage());
            try {
                if (conexion != null) conexion.rollback();
            } catch (java.sql.SQLException ex) {}
            return -1;
        } finally {
            try {
                if (conexion != null) conexion.setAutoCommit(true);
            } catch (java.sql.SQLException e) {}
        }
    }

    // Registra una factura nueva (por defecto entra como PENDIENTE de pago)
    public boolean generarFacturaPendiente(int pedidoId, double total) {
        if (conexion == null) conectar();
        String sql = "INSERT INTO factura (pedido_id, estado, total) VALUES (?, 'PENDIENTE', ?)";
        try (java.sql.PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, pedidoId);
            ps.setDouble(2, total);
            return ps.executeUpdate() > 0;
        } catch (java.sql.SQLException e) {
            System.err.println("Error al generar factura pendiente: " + e.getMessage());
            return false;
        }
    }

    // Recupera el detalle de un pedido (útil para el modal de edición)
    public List<com.mycompany.vitalsa.dto.DetallePedidoDTO> obtenerDetallesPedido(int pedidoId) {
        List<com.mycompany.vitalsa.dto.DetallePedidoDTO> lista = new ArrayList<>();
        if (conexion == null) conectar();
        String sql = "SELECT dp.presentacion_id, pr.descripcion AS nombre, dp.precio_venta, dp.cantidad " +
                     "FROM detalle_pedido dp " +
                     "JOIN presentacion pr ON dp.presentacion_id = pr.id " +
                     "WHERE dp.pedido_id = ?";
        try (java.sql.PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, pedidoId);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new com.mycompany.vitalsa.dto.DetallePedidoDTO(
                        rs.getInt("presentacion_id"),
                        rs.getString("nombre"),
                        rs.getDouble("precio_venta"),
                        rs.getInt("cantidad")
                    ));
                }
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error al obtener detalles: " + e.getMessage());
        }
        return lista;
    }

    // Esto se llama al editar un pedido completo. Hacemos la fácil: borramos todo y volvemos a insertar.
    public boolean actualizarPedidoCompleto(int pedidoId, double montoTotal, List<com.mycompany.vitalsa.dto.DetallePedidoDTO> nuevosDetalles) {
        if (conexion == null) conectar();
        try {
            conexion.setAutoCommit(false);
            
            // 1. Devolvemos el stock de los productos que estaban en el pedido viejo
            String sqlRevertirStock = "UPDATE presentacion pr JOIN detalle_pedido dp ON pr.id = dp.presentacion_id " +
                                      "SET pr.stock = pr.stock + dp.cantidad WHERE dp.pedido_id = ?";
            try (java.sql.PreparedStatement ps = conexion.prepareStatement(sqlRevertirStock)) {
                ps.setInt(1, pedidoId);
                ps.executeUpdate();
            }
            
            // 2. Volamos los detalles viejos
            String sqlDelete = "DELETE FROM detalle_pedido WHERE pedido_id = ?";
            try (java.sql.PreparedStatement ps = conexion.prepareStatement(sqlDelete)) {
                ps.setInt(1, pedidoId);
                ps.executeUpdate();
            }
            
            // 3. Actualizamos la cabecera (el monto total nuevo)
            String sqlUpdatePedido = "UPDATE pedido SET monto_total = ? WHERE id = ?";
            try (java.sql.PreparedStatement ps = conexion.prepareStatement(sqlUpdatePedido)) {
                ps.setDouble(1, montoTotal);
                ps.setInt(2, pedidoId);
                ps.executeUpdate();
            }
            
            // 4. Cargamos los detalles nuevos y volvemos a restar stock
            String sqlDetalle = "INSERT INTO detalle_pedido (pedido_id, presentacion_id, cantidad, precio_venta, subtotal) VALUES (?, ?, ?, ?, ?)";
            String sqlUpdateStock = "UPDATE presentacion SET stock = stock - ? WHERE id = ?";
            
            try (java.sql.PreparedStatement pstmtDetalle = conexion.prepareStatement(sqlDetalle);
                 java.sql.PreparedStatement pstmtStock = conexion.prepareStatement(sqlUpdateStock)) {
                
                for (com.mycompany.vitalsa.dto.DetallePedidoDTO detalle : nuevosDetalles) {
                    pstmtDetalle.setInt(1, pedidoId);
                    pstmtDetalle.setInt(2, detalle.getProductoId());
                    pstmtDetalle.setInt(3, detalle.getCantidad());
                    pstmtDetalle.setDouble(4, detalle.getPrecioUnitario());
                    pstmtDetalle.setDouble(5, detalle.getSubtotal());
                    pstmtDetalle.addBatch();
                    
                    pstmtStock.setInt(1, detalle.getCantidad());
                    pstmtStock.setInt(2, detalle.getProductoId());
                    pstmtStock.addBatch();
                }
                pstmtDetalle.executeBatch();
                pstmtStock.executeBatch();
            }
            
            conexion.commit();
            return true;
        } catch (java.sql.SQLException e) {
            System.err.println("Error al actualizar pedido completo: " + e.getMessage());
            try { conexion.rollback(); } catch (java.sql.SQLException ex) {}
            return false;
        } finally {
            try { conexion.setAutoCommit(true); } catch (java.sql.SQLException ex) {}
        }
    }

    // ================= MÉTODOS DE LOGÍSTICA (Gestión de envíos) ================= //

    // Trae los pedidos que ya están listos para salir pero aún no tienen camión asignado
    public List<com.mycompany.vitalsa.dto.LogisticaPedidoPendienteDTO> obtenerPedidosPendientesLogistica() {
        List<com.mycompany.vitalsa.dto.LogisticaPedidoPendienteDTO> lista = new ArrayList<>();
        if (conexion == null) conectar();
        String sql = "SELECT p.id, p.fecha_realizacion, " +
                     "COALESCE(z.nombre, 'Sin Zona') AS zona_destino, " +
                     "COALESCE(CONCAT(d.calle, ' ', d.numeracion), 'Sin Dirección') AS direccion " +
                     "FROM pedido p " +
                     "JOIN cliente c ON p.cliente_id = c.id " +
                     "LEFT JOIN direccion d ON c.direccion_id = d.id " +
                     "LEFT JOIN barrio b ON d.barrio_id = b.id " +
                     "LEFT JOIN zona z ON b.zona_id = z.id " +
                     "WHERE p.estado IN ('PENDIENTE', 'CONFIRMADO') " +
                     "ORDER BY p.fecha_realizacion ASC";
        try (java.sql.Statement stmt = conexion.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String sku = String.format("#VT-%04d", 8800 + id);
                String zona = rs.getString("zona_destino");
                String dir = rs.getString("direccion");
                
                java.sql.Timestamp fechaSql = rs.getTimestamp("fecha_realizacion");
                String tiempoEspera = "Reciente";
                if (fechaSql != null) {
                    long mins = (System.currentTimeMillis() - fechaSql.getTime()) / 60000;
                    if (mins < 60) tiempoEspera = mins + " min ago";
                    else if (mins < 1440) tiempoEspera = (mins / 60) + "h ago";
                    else tiempoEspera = (mins / 1440) + "d ago";
                }
                
                List<String> tags = new ArrayList<>();
                if (id % 3 == 0) tags.add("FRAGILE");
                if (id % 2 == 0) tags.add("COLD CHAIN");
                
                lista.add(new com.mycompany.vitalsa.dto.LogisticaPedidoPendienteDTO(id, sku, zona, dir, tiempoEspera, tags));
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error al obtener pedidos pendientes para logística: " + e.getMessage());
        }
        return lista;
    }

    // Lista los camiones/distribuidores y calcula qué tan llenos están en porcentaje
    public List<com.mycompany.vitalsa.dto.DistribuidorCargaDTO> obtenerDistribuidoresLogistica() {
        List<com.mycompany.vitalsa.dto.DistribuidorCargaDTO> lista = new ArrayList<>();
        if (conexion == null) conectar();
        String sql = "SELECT d.id, d.nombre, z.nombre AS zona_cargo, d.capacidad_diaria, " +
                     "(SELECT COUNT(*) FROM pedido p WHERE p.distribuidor_id = d.id AND p.estado IN ('EN_PREPARACION', 'ENVIADO', 'ENTREGADO') AND DATE(p.fecha_realizacion) = CURDATE()) AS asignados " +
                     "FROM distribuidor d " +
                     "JOIN zona z ON d.zona_a_cargo_id = z.id";
        try (java.sql.Statement stmt = conexion.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String zona = rs.getString("zona_cargo");
                int capacidad = rs.getInt("capacidad_diaria");
                if (capacidad <= 0) capacidad = 10; 
                int asignados = rs.getInt("asignados");
                
                double porcentaje = (double) asignados / capacidad;
                if (porcentaje > 1.0) porcentaje = 1.0;
                
                lista.add(new com.mycompany.vitalsa.dto.DistribuidorCargaDTO(id, nombre, zona, porcentaje, asignados, capacidad));
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error al obtener distribuidores para logística: " + e.getMessage());
        }
        return lista;
    }

    // Trae los pedidos que están literal en la calle
    public List<com.mycompany.vitalsa.dto.EnvioTransitoDTO> obtenerEnviosEnTransito() {
        List<com.mycompany.vitalsa.dto.EnvioTransitoDTO> lista = new ArrayList<>();
        if (conexion == null) conectar();
        String sql = "SELECT p.id, p.estado, d.nombre AS distribuidor_nombre, z.nombre AS zona_ruta " +
                     "FROM pedido p " +
                     "JOIN distribuidor d ON p.distribuidor_id = d.id " +
                     "LEFT JOIN cliente c ON p.cliente_id = c.id " +
                     "LEFT JOIN direccion dir ON c.direccion_id = dir.id " +
                     "LEFT JOIN barrio b ON dir.barrio_id = b.id " +
                     "LEFT JOIN zona z ON b.zona_id = z.id " +
                     "WHERE p.estado IN ('ENVIADO', 'ENTREGADO', 'CANCELADO') AND p.distribuidor_id IS NOT NULL " +
                     "ORDER BY p.fecha_realizacion DESC LIMIT 50";
        try (java.sql.Statement stmt = conexion.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String sku = String.format("#VT-%04d", 8800 + id);
                String estado = rs.getString("estado").replace("_", " ");
                if ("ENVIADO".equals(estado)) estado = "EN RUTA";
                String distribuidor = rs.getString("distribuidor_nombre");
                String zona = rs.getString("zona_ruta");
                if (zona == null) zona = "Sin Zona";
                
                lista.add(new com.mycompany.vitalsa.dto.EnvioTransitoDTO(id, sku, distribuidor, zona, estado));
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error al obtener envíos en tránsito: " + e.getMessage());
        }
        return lista;
    }

    // Agarra los pedidos marcados y se los enchufa a un repartidor
    public boolean asignarPedidosADistribuidor(List<Integer> pedidosIds, int distribuidorId) {
        if (pedidosIds == null || pedidosIds.isEmpty()) return false;
        if (conexion == null) conectar();
        try {
            conexion.setAutoCommit(false);
            String sql = "UPDATE pedido SET distribuidor_id = ?, estado = 'ENVIADO' WHERE id = ?";
            try (java.sql.PreparedStatement ps = conexion.prepareStatement(sql)) {
                for (int pid : pedidosIds) {
                    ps.setInt(1, distribuidorId);
                    ps.setInt(2, pid);
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            conexion.commit();
            return true;
        } catch (java.sql.SQLException e) {
            System.err.println("Error al asignar pedidos a distribuidor: " + e.getMessage());
            try { conexion.rollback(); } catch (java.sql.SQLException ex) {}
            return false;
        } finally {
            try { conexion.setAutoCommit(true); } catch (java.sql.SQLException ex) {}
        }
    }

    // ================= MÉTODOS DE FACTURACIÓN (La plata) ================= //

    // Calcula los numeritos de arriba en la pantalla de facturación
    public com.mycompany.vitalsa.dto.FacturacionKpiDTO obtenerKpisFacturacion() {
        if (conexion == null) conectar();
        double totalMes = 0.0;
        double crecimientoPct = 12.4; // TODO: Esto quedó hardcodeado, nos faltó tiempo para la lógica real vs mes pasado
        double cuentasCobrar = 0.0;
        int pendientes = 0;
        int verificados = 0;
        int totalFacturas = 0;
        double cobradoHoy = 0.0;
        
        try {
            // Truquito: si la tabla de facturas está vacía, inventamos algunas de prueba en base a los pedidos
            try (java.sql.Statement s = conexion.createStatement(); 
                 java.sql.ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM factura")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    s.execute("INSERT INTO factura (pedido_id, estado, total) SELECT id, IF(id%2=0, 'PAGADA', 'PENDIENTE'), monto_total FROM pedido WHERE estado IN ('ENTREGADO', 'ENVIADO')");
                }
            } catch (Exception ignore) {}

            try (java.sql.Statement stmt = conexion.createStatement()) {
                // Total Mes
                try (java.sql.ResultSet rs = stmt.executeQuery("SELECT SUM(total) FROM factura WHERE MONTH(fecha) = MONTH(CURDATE())")) {
                    if (rs.next()) totalMes = rs.getDouble(1);
                }
                // Cuentas por cobrar
                try (java.sql.ResultSet rs = stmt.executeQuery("SELECT SUM(total), COUNT(*) FROM factura WHERE estado = 'PENDIENTE'")) {
                    if (rs.next()) {
                        cuentasCobrar = rs.getDouble(1);
                        pendientes = rs.getInt(2);
                    }
                }
                // Pagos
                try (java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM factura WHERE estado = 'PAGADA'")) {
                    if (rs.next()) verificados = rs.getInt(1);
                }
                try (java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM factura")) {
                    if (rs.next()) totalFacturas = rs.getInt(1);
                }
                // Cobrado Hoy
                try (java.sql.ResultSet rs = stmt.executeQuery("SELECT SUM(total) FROM factura WHERE estado = 'PAGADA' AND DATE(fecha) = CURDATE()")) {
                    if (rs.next()) cobradoHoy = rs.getDouble(1);
                }
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error al obtener KPIs de facturacion: " + e.getMessage());
        }
        return new com.mycompany.vitalsa.dto.FacturacionKpiDTO(totalMes, crecimientoPct, cuentasCobrar, pendientes, verificados, totalFacturas, cobradoHoy);
    }

    // Trae las facturas para cargar la tabla principal
    public List<com.mycompany.vitalsa.dto.FacturaRecienteDTO> obtenerFacturasEmitidas() {
        List<com.mycompany.vitalsa.dto.FacturaRecienteDTO> lista = new ArrayList<>();
        if (conexion == null) conectar();
        String sql = "SELECT f.id, f.estado, " +
                     "COALESCE(NULLIF(c.nombre_fantasia, ''), NULLIF(c.razon_social, ''), CONCAT(c.nombre, ' ', c.apellido)) AS cliente_nombre " +
                     "FROM factura f " +
                     "JOIN pedido p ON f.pedido_id = p.id " +
                     "JOIN cliente c ON p.cliente_id = c.id " +
                     "ORDER BY f.fecha DESC LIMIT 50";
        try (java.sql.Statement stmt = conexion.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String sku = String.format("#F-2024-%04d", id);
                String estado = rs.getString("estado");
                String cliente = rs.getString("cliente_nombre");
                if (cliente == null || cliente.trim().isEmpty()) cliente = "Cliente Múltiple";
                
                lista.add(new com.mycompany.vitalsa.dto.FacturaRecienteDTO(id, sku, cliente, estado));
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error al obtener facturas recientes: " + e.getMessage());
        }
        return lista;
    }

    // Pasa una factura a PAGADA (recibe el número de código o el ID crudo)
    public boolean registrarPagoFactura(String idOCodigo, String metodoPago) {
        if (conexion == null) conectar();
        int idReal = -1;
        try {
            if (idOCodigo.startsWith("#F-2024-")) {
                idReal = Integer.parseInt(idOCodigo.replace("#F-2024-", ""));
            } else {
                idReal = Integer.parseInt(idOCodigo);
            }
        } catch (NumberFormatException e) {
            return false;
        }

        String sql = "UPDATE factura SET estado = 'PAGADA', metodo_pago = ? WHERE id = ?";
        try (java.sql.PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, metodoPago);
            ps.setInt(2, idReal);
            return ps.executeUpdate() > 0;
        } catch (java.sql.SQLException e) {
            System.err.println("Error al registrar pago: " + e.getMessage());
            return false;
        }
    }
}