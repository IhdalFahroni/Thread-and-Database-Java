import java.sql.*;

public class Pembeli extends Thread {
    private int idProduk;
    private String nama;
    private int jumlah;
    private static final Object lock = new Object();

    public Pembeli(int idProduk, String nama, int jumlah) {
        this.idProduk = idProduk;
        this.nama = nama;
        this.jumlah = jumlah;
    }

    @Override
    public void run() {
        try {
            System.out.println("[" + nama + "] Mencoba membeli " + jumlah + " item...");
            Thread.sleep((long) (Math.random() * 500));

            synchronized (lock) {
                if (beliProduk()) {
                    System.out.println("✓ [" + nama + "] BERHASIL!");
                } else {
                    System.out.println("✗ [" + nama + "] GAGAL - Stok habis!");
                }
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private boolean beliProduk() {
        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false);

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT stok, terjual, harga FROM produk WHERE id = ? FOR UPDATE");
            ps.setInt(1, idProduk);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int stok = rs.getInt("stok");
                double harga = rs.getDouble("harga");
                System.out.println("  [" + nama + "] Stok saat ini: " + stok + ", butuh: " + jumlah);

                if (stok >= jumlah) {
                    PreparedStatement update = conn.prepareStatement(
                            "UPDATE produk SET stok = stok - ?, terjual = terjual + ? WHERE id = ?");
                    update.setInt(1, jumlah);
                    update.setInt(2, jumlah);
                    update.setInt(3, idProduk);
                    update.executeUpdate();

                    PreparedStatement insert = conn.prepareStatement(
                            "INSERT INTO pesanan (id_produk, pembeli, jumlah, total) VALUES (?, ?, ?, ?)");
                    insert.setInt(1, idProduk);
                    insert.setString(2, nama);
                    insert.setInt(3, jumlah);
                    insert.setDouble(4, harga * jumlah);
                    insert.executeUpdate();

                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            }
            conn.rollback();
            return false;

        } catch (SQLException e) {
            System.err.println("ERROR SQL [" + nama + "]: " + e.getMessage());
            e.printStackTrace();
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException ex) {
            }
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
            }
        }
    }
}
