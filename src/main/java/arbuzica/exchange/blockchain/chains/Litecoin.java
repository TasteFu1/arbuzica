package arbuzica.exchange.blockchain.chains;

import arbuzica.exchange.blockchain.IBlockChain;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

public class Litecoin implements IBlockChain {

    @SneakyThrows
    @Override
    public double getWalletBalance() {
        return JsonParser.parseString(Request.Post(endpoint())
                .addHeader("Content-Type", "text/plain")
                .body(
                        new StringEntity(
                                "{\"jsonrpc\": \"1.0\", \"id\": \"exchanger\", \"method\": \"getbalance\", \"params\": [\"*\", 1]}",
                                ContentType.APPLICATION_JSON
                        )
                )
                .execute()
                .returnContent().asString()
        ).getAsJsonObject().get("result").getAsDouble();
    }

    @Override
    public String endpoint() {
        return String.format("http://%s:%s@127.0.0.1:9332/", USER, PASSWORD);
    }



}
