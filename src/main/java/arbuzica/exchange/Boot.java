package arbuzica.exchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Boot {
    public static void main(String[] args) throws InterruptedException {
        new Instance(SpringApplication.run(Boot.class, args));
    }
}
