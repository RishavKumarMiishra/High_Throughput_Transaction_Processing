import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class TradeDataGenerator {

    public static void main(String[] args) {
        long totalRows = 200000L;
        Path path = Path.of("trades.csv");

        String[] symbols = {"AAPL", "GOOGL", "MSFT", "TSLA", "INFY", "NVDA", "RELIANCE", "HDFCBANK", "ICICIBANK", "TCS", "AMZN"};
        double[] basePrices = {182.0, 2750.0, 410.0, 720.0, 1450.0, 680.0, 2850.0, 1650.0, 980.0, 4100.0, 3300.0};
        String[] actions = {"BUY", "SELL"};

        Random random = new Random();
        LocalDateTime time = LocalDateTime.of(2026, 2, 20, 9, 15, 0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        try (BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

            for (int i = 1; i <= totalRows; i++) {
                // Generate random data based on your parameters
                int accountId = 1001 + random.nextInt(20); // Users 1001 to 1020
                int symbolIndex = random.nextInt(symbols.length);
                String symbol = symbols[symbolIndex];

                // Quantity between 5 and 200 (rounded to nearest 5)
                int quantity = (random.nextInt(40) + 1) * 5;

                // Fluctuate price slightly by up to +/- 2%
                double priceFluctuation = 1.0 + (random.nextDouble() * 0.04 - 0.02);
                double price = basePrices[symbolIndex] * priceFluctuation;

                String action = actions[random.nextInt(2)];

                // Increment time by 1 to 3 seconds per trade
                time = time.plusSeconds(random.nextInt(3) + 1);

                // Format: TradeID,AccountID,Symbol,Quantity,Price,Action,Timestamp
                String row = String.format("%d,%d,%s,%d,%.2f,%s,%s%n",
                        i, accountId, symbol, quantity, price, action, time.format(formatter));

                bw.write(row);
            }
            System.out.println("Successfully generated " + totalRows + " rows in trades.csv");

        } catch (IOException e) {
            System.err.println("Error creating test data: " + e.getMessage());
        }
    }
}