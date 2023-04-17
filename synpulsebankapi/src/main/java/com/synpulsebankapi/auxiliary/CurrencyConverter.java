package com.synpulsebankapi.auxiliary;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

/**
 * Class utilized to call the Abstract API to get the credit and debit
 * values in USD
 * 
 * When created, it requires the list of the transactions of the month, then
 * reads from there to pass it to the Abstract API
 */
@Service
public class CurrencyConverter {

    private List<Transaction> transactionList;
    private String API_KEY;

    /**
     * Constructor for the Currency Converter.
     * Stores a transaction list 
     * Stores the api key into a private variable, or leaves it empty if
     * there's an error.
     * 
     * @param transactionList
     */
    public void assignList(List<Transaction> transactionList) {
        this.transactionList = transactionList;

        // Read the api key from the apiKey file for the third party API
        try {
            BufferedReader reader = new BufferedReader(new FileReader("src/main/java/com/synpulsebankapi/apiKey.txt"));
            this.API_KEY = reader.lines().collect(Collectors.joining());
            reader.close();
        }
        catch (IOException e) {
            this.API_KEY = "";
        }
    }

    public List<Transaction> getTransactionList() {
        return this.transactionList;
    }

    // Don't unit test this because it depends on an external service
    public JSONObject callAPI() {
        // String jsonObject = "{\"EUR\":0.917011,\"JPY\":133.223292,\"BGN\":1.793489,\"CZK\":21.52132,\"DKK\":6.832187,\"GBP\":0.804567,\"HUF\":344.089867,\"PLN\":4.281522,\"RON\":4.52774,\"SEK\":10.477304,\"CHF\":0.904906,\"ISK\":137.093077,\"NOK\":10.585511,\"HRK\":7.06591,\"RUB\":104.99999999999999,\"TRY\":19.287758,\"AUD\":1.502705,\"BRL\":5.05016,\"CAD\":1.35094,\"CNY\":6.884915,\"HKD\":7.849885,\"IDR\":14913.571756,\"ILS\":3.642549,\"INR\":82.091243,\"KRW\":1322.164145,\"MXN\":18.14773,\"MYR\":4.418524,\"NZD\":1.610821,\"PHP\":55.016965,\"SGD\":1.33205,\"THB\":34.260431,\"ZAR\":18.249702,\"ARS\":75.269373,\"DZD\":124.445887,\"MAD\":8.83269,\"TWD\":27.466513,\"BTC\":5.2e-05,\"ETH\":0.000764,\"BNB\":0.003633,\"DOGE\":16.654888,\"XRP\":2.07083,\"BCH\":0.008601,\"LTC\":0.018906}";

        try {
            URL url = new URL("https://exchange-rates.abstractapi.com/v1/live?api_key="+API_KEY+"&base=USD");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                con.getInputStream()
            ));

            // Obtains the JSON object from the API call
            String finalString = in.lines().collect(Collectors.joining());

            // Logs the object to the console
            System.out.println("Response from third party API is: " + finalString);

            // Extracts only the exchange_rates nested object from the response and returns it
            JSONObject object = new JSONObject(finalString).getJSONObject("exchange_rates");
            return object;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Method to get the conversions to USD and store them in a JSONObject
    public List<Float> convert() {
        // Integer list to define the answer
        List<Float> responseList = new ArrayList<>();

        // Calls the API to get the latest currency exchange rates
        JSONObject currenciesToUSD = callAPI();

        // To store the total credit and debit and send it back as response
        float credit = 0;
        float debit = 0;

        // Iterates through the transactions and converts them accordingly
        for (Transaction t: transactionList) {
            // Expected format: GBP 100-
            // Separates value from currency
            String value = t.getAmount();
            String parts[] = value.split(" ");

            // Only check if the currency is valid (if USD, rate = 1)
            if (currenciesToUSD.has(parts[0]) || parts[0].equals("USD")) {
                float valueInt = 0;
                float rate = (parts[0].equals("USD")) ? 1 : currenciesToUSD.getFloat(parts[0]);

                // Negative goes into debit, positive to credit
                if (parts[1].contains("-")) {
                    // Further splits if negative
                    String numberParts[] = parts[1].split("-");
                    valueInt = Float.parseFloat(numberParts[0]);
                    debit = debit + valueInt * rate;
                }
                else {
                    valueInt = Float.parseFloat(parts[1]);
                    credit = credit + valueInt * rate;
                }
            }
        }

        // Adds it to the responseList and returns the final object
        responseList.add(credit);
        responseList.add(debit);
        return responseList;
    }

    
}
