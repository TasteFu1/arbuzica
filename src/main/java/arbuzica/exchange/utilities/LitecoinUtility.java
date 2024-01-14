package arbuzica.exchange.utilities;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LitecoinUtility {
    public static Double getRateToUSD() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://api.coingecko.com/api/v3/simple/price?ids=litecoin&vs_currencies=usd").build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            String responseBody = response.body().string();
            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();

            return jsonObject.getAsJsonObject("litecoin").get("usd").getAsDouble();
        }

        response.close();
        return null;
    }

    public static Double getSatoshiPerByte() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://blockchair.com/litecoin").build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            String[] split = response.body().string().split("\n");
            String regex = "satoshi per byte                                            </span>";

            for (String s : split) {
                if (s.contains(regex)) {
                    return Double.parseDouble(s.trim().replace(regex, "").replace(" ", ""));
                }
            }
        }

        return null;
    }
}
