import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import deserializers.DeserializerBITFINEX;
import deserializers.DeserializerEXMO;
import models.ComplexValue;
import models.PairListBITFINEX;
import models.PairListEXMO;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Controller {

    private URL url;
    private String urlAddress;
    private ComplexValue complexValue;
    private boolean isEXMO = false;
    private boolean isBITFINEX = false;
    private GsonBuilder builder = new GsonBuilder();

    Controller(String URL) {
        urlAddress = URL;
    }

    public void setEXMO(boolean EXMO) {
        isEXMO = EXMO;
    }

    public void setBITFINEX(boolean BITFINEX) {
        isBITFINEX = BITFINEX;
    }

    public ComplexValue run () throws IOException {
        complexValue = new ComplexValue();
        String result = requestToApi();
        changeToModel(result);
        //System.out.println("pairList: " + pairListEXMO.getListPair().get("BTCEUR").getBuyPrice());
//        if (isEXMO)
//            complexValue.setPairList(pairListEXMO);
//        if (isBITFINEX)
//            complexValue.setPairList(pairListEXMO);//TODO change
        return complexValue;
    }

    private void changeToModel(String result) {
        if (isEXMO) {
            builder.registerTypeAdapter(PairListEXMO.class, new DeserializerEXMO());
            Gson gson = builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
            PairListEXMO pairListEXMO = gson.fromJson(result, PairListEXMO.class);
            complexValue.setPairListEXMO(pairListEXMO);
        }
        if (isBITFINEX) {
            builder.registerTypeAdapter(PairListBITFINEX.class, new DeserializerBITFINEX());
            Gson gson = builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
            PairListBITFINEX pairListBITFINEX = gson.fromJson(result, PairListBITFINEX.class);
            complexValue.setPairListBITFINEX(pairListBITFINEX);
        }
    }

    private String requestToApi() throws IOException {
        try {
            url = new URL(urlAddress);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder response = new StringBuilder();
        String text;

        while ((text = reader.readLine()) != null) {
            response.append(text);
        }
        reader.close();
        return response.toString();
    }
}
