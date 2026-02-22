import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        Path p = Path.of("trades.csv");
        ManageUser manageUser = new ManageUser();
        ExecutorService executor = Executors.newFixedThreadPool(100);
        long startTime = System.currentTimeMillis();
        try (BufferedReader br = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
            String line;
            line = br.readLine();
            try {
                while((line = br.readLine()) != null) {
                    String finalLine = line;
                    executor.execute(() -> {
                        manageUser.readRow(finalLine);
                    });
                }
            } finally {
                executor.shutdown();
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            }
            while((line = br.readLine()) != null) {
                String finalLine = line;
                executor.execute(() -> {
                    manageUser.readRow(finalLine);
                });
            }
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
        long endTime = System.currentTimeMillis();
        System.out.println((endTime-startTime)/1000);
        executor.shutdown();
    }
}
