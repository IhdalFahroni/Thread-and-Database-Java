public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("=== TOKO ONLINE - DEMO THREAD & DATABASE ===\n");

        // Setup database
        System.out.println("1. Setup database...");
        DatabaseManager.initDatabase();
        DatabaseManager.resetData();

        // Tampilkan produk
        System.out.println("\n2. Daftar produk:");
        DatabaseManager.tampilkanProduk();

        // Simulasi 8 pembeli
        System.out.println("\n3. Simulasi pembelian bersamaan...\n");

        Thread[] buyers = new Thread[8];
        buyers[0] = new Pembeli(1, "Budi", 10);
        buyers[1] = new Pembeli(1, "Ani", 10);
        buyers[2] = new Pembeli(1, "Citra", 10);
        buyers[3] = new Pembeli(1, "Doni", 10);
        buyers[4] = new Pembeli(1, "Eka", 10);
        buyers[5] = new Pembeli(2, "Fani", 10);
        buyers[6] = new Pembeli(3, "Gita", 10);
        buyers[7] = new Pembeli(3, "Hadi", 10);

        for (Thread t : buyers) {
            t.start();
        }

        for (Thread t : buyers) {
            t.join();
        }

        // Tampilkan hasil
        System.out.println("\n4. Status akhir:");
        DatabaseManager.tampilkanProduk();

        System.out.println("\n5. Riwayat pesanan:");
        DatabaseManager.tampilkanPesanan();

        System.out.println("\n=== SELESAI ===");
    }
}
