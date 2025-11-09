import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/toko_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static void initDatabase() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", DB_USER, DB_PASSWORD);
                Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("DROP DATABASE IF EXISTS toko_db");
            stmt.executeUpdate("CREATE DATABASE toko_db");
            System.out.println("✓ Database berhasil dibuat");

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }

        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement()) {

            String createProductTable = "CREATE TABLE IF NOT EXISTS produk (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "nama VARCHAR(100)," +
                    "stok INT," +
                    "terjual INT DEFAULT 0," +
                    "harga DECIMAL(10,2))";
            stmt.executeUpdate(createProductTable);

            String createOrderTable = "CREATE TABLE IF NOT EXISTS pesanan (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "id_produk INT," +
                    "pembeli VARCHAR(100)," +
                    "jumlah INT," +
                    "total DECIMAL(15,2)," +
                    "waktu TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (id_produk) REFERENCES produk(id))";
            stmt.executeUpdate(createOrderTable);

            System.out.println("✓ Tabel berhasil dibuat");

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static void resetData() {
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 0");
            stmt.executeUpdate("TRUNCATE TABLE pesanan");
            stmt.executeUpdate("TRUNCATE TABLE produk");
            stmt.executeUpdate("SET FOREIGN_KEY_CHECKS = 1");

            stmt.executeUpdate(
                    "INSERT INTO produk (nama, stok, harga) VALUES " +
                            "('Laptop Gaming', 50, 15000000)," +
                            "('Smartphone', 100, 5000000)," +
                            "('Headset', 200, 500000)");

            System.out.println("✓ Data berhasil direset");

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static void tampilkanProduk() {
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM produk")) {

            System.out.println("\n=== DAFTAR PRODUK ===");
            System.out.println("ID | Nama Produk      | Stok | Terjual | Harga");
            System.out.println("------------------------------------------------");

            while (rs.next()) {
                System.out.printf("%d  | %-16s | %4d | %7d | Rp %,.0f\n",
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getInt("stok"),
                        rs.getInt("terjual"),
                        rs.getDouble("harga"));
            }

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static void tampilkanPesanan() {
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT p.id, pr.nama, p.pembeli, p.jumlah, p.total " +
                                "FROM pesanan p JOIN produk pr ON p.id_produk = pr.id")) {

            System.out.println("\n=== RIWAYAT PESANAN ===");
            System.out.println("ID | Produk       | Pembeli | Jumlah | Total");
            System.out.println("-----------------------------------------------");

            while (rs.next()) {
                System.out.printf("%d  | %-12s | %-7s | %6d | Rp %,.0f\n",
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getString("pembeli"),
                        rs.getInt("jumlah"),
                        rs.getDouble("total"));
            }

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
