package arbuzica.exchange.blockchain.chains;

import arbuzica.exchange.blockchain.IBlockChain;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

public class Litecoin implements IBlockChain {
    @Setter
    @Getter
    private Double satoshiPerByte;

    @Setter
    @Getter
    private Double rateToUSD;

    @SneakyThrows
    @Override
    public double getWalletBalance() {
        return JsonParser.parseString(Request.Post(endpoint())
                .addHeader("Content-Type", "text/plain")
                .body(
                        new StringEntity(
                                "{\"jsonrpc\": \"1.0\", \"id\": \"exchanger\", \"method\": \"getbalance\", \"params\": [\"*\", 0]}",
                                ContentType.APPLICATION_JSON
                        )
                )
                .execute()
                .returnContent().asString()
        ).getAsJsonObject().get("result").getAsDouble();
    }

    @SneakyThrows
    public boolean isSynchronized() {
        try {
            JsonObject result = JsonParser.parseString(Request.Post(endpoint())
                    .addHeader("Content-Type", "text/plain")
                    .body(
                            new StringEntity(
                                    "{\"jsonrpc\": \"1.0\", \"id\": \"curltest\", \"method\": \"getblockchaininfo\", \"params\": []}",
                                    ContentType.APPLICATION_JSON
                            )
                    )
                    .execute()
                    .returnContent().asString()
            ).getAsJsonObject().get("result").getAsJsonObject();

            if (result == null) {
                return false;
            }

            return result.get("blocks").getAsInt() == result.get("headers").getAsInt();
        } catch (Exception e) {
            return false;
        }
    }

    @SneakyThrows
    public String getNewAddress() {
        return JsonParser.parseString(Request.Post(endpoint())
                .addHeader("Content-Type", "text/plain")
                .body(
                        new StringEntity(
                                "{\"jsonrpc\": \"1.0\", \"id\": \"curltest\", \"method\": \"getnewaddress\", \"params\": []}",
                                ContentType.APPLICATION_JSON
                        )
                )
                .execute()
                .returnContent().asString()
        ).getAsJsonObject().get("result").getAsString();
    }

    @SneakyThrows
    public String send(String address, double amount, double txFeeSatoshiPerVbyte) {
        return JsonParser.parseString(Request.Post(endpoint())
                .addHeader("Content-Type", "text/plain")
                .body(
                        new StringEntity(
                                String.format(
                                        """
                                                {
                                                  "jsonrpc": "1.0",
                                                  "id": "curltest",
                                                  "method": "send",
                                                  "params": [
                                                    [{"%s": %s}],
                                                    null,
                                                    "unset",
                                                    %s
                                                  ]
                                                }
                                                """,
                                        address, amount, txFeeSatoshiPerVbyte
                                ),
                                ContentType.APPLICATION_JSON
                        )
                )
                .execute()
                .returnContent().asString()
        ).toString();
    }

    @SneakyThrows
    public String estimateFee(int confTargetBlocks) {
        return JsonParser.parseString(Request.Post(endpoint())
                .addHeader("Content-Type", "text/plain")
                .body(
                        new StringEntity(
                                String.format(
                                        """
                                                {
                                                  "jsonrpc": "1.0",
                                                  "id": "curltest",
                                                  "method": "estimatesmartfee",
                                                  "params": [
                                                    %s
                                                  ]
                                                }
                                                """,
                                        confTargetBlocks
                                ),
                                ContentType.APPLICATION_JSON
                        )
                )
                .execute()
                .returnContent().asString()
        ).toString();
    }

    @SneakyThrows
    public String listUnspent() {
        return JsonParser.parseString(Request.Post(endpoint())
                .addHeader("Content-Type", "text/plain")
                .body(
                        new StringEntity(
                                """
                                        {
                                          "jsonrpc": "1.0",
                                          "id": "curltest",
                                          "method": "listunspent",
                                          "params": [
                                               0
                                          ]
                                        }
                                        """,
                                ContentType.APPLICATION_JSON
                        )
                )
                .execute()
                .returnContent().asString()
        ).toString();
    }

    @Override
    public String endpoint() {
        return String.format("http://%s:%s@127.0.0.1:9332/", USER, PASSWORD);
    }
}
