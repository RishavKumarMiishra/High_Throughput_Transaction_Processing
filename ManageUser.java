import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ManageUser {

    ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> tradeList = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Lock> locks = new ConcurrentHashMap<>();

    public void readRow(String row) {
        String[] rowData = row.split(",");
        String accountId = rowData[1];
        int tradeId = Integer.parseInt(rowData[0].trim());
        String symbol = rowData[2];
        int quantity = Integer.parseInt(rowData[3]);
        double price = Double.parseDouble(rowData[4]);
        boolean isBuy = rowData[5].equals("BUY");
        StringBuilder t = new StringBuilder(rowData[6]);
        t.replace(10, 11, " ");
        String filePath = accountId+".txt";

        Lock lock = locks.computeIfAbsent(accountId, k->new ReentrantLock());

        lock.lock();
        try {
            Path path = Path.of(filePath);
            boolean isNewFile = !Files.exists(path);
            try (BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {

                if (isNewFile) {
                    String header = String.format("%-10s %-10s %-10s %-10s %-10s%n",
                            "TradeID", "Symbol", "Quantity", "Price", "Action");
                    bw.write(header);
                    bw.write("-----------------------------------------------------\n");
                }
                if (quantity > 0 && price > 0) {
                    String formattedRow = String.format("%-10d %-10s %-10d %-10.2f %-10s%n",
                            tradeId, symbol, quantity, price, isBuy ? "BUY" : "SELL");
                    bw.write(formattedRow);
                }

            } catch (IOException e) {
                System.err.println("Error writing to file for account " + accountId + ": " + e.getMessage());
            }
        } finally {
            lock.unlock();
        }
    }
}
