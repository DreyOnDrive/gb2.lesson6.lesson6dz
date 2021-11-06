package gb2.lesson6.lesson6dz;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;

public class Main {

    private static final String BASE_HOST = "dataservice.accuweather.com";
    private static final String FORECAST = "forecasts";
    private static final String API_VERSION = "v1";
    private static final String FORECAST_TYPE = "daily";
    private static final String FORECAST_PERIOD = "5day";
    private static final String SAINT_PETERSBURG_KEY = "474212_PC";
    private static final String API_KEY = "sn6EPnFOADso262QAul9HAmAARdwOtMD";

    public static void main(String[] args) throws IOException {

        OkHttpClient client = new OkHttpClient();

        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(BASE_HOST)
                .addPathSegment(FORECAST)
                .addPathSegment(API_VERSION)
                .addPathSegment(FORECAST_TYPE)
                .addPathSegment(FORECAST_PERIOD)
                .addPathSegment(SAINT_PETERSBURG_KEY)
                .addQueryParameter("apikey", API_KEY)
                .addQueryParameter("language", "ru-ru")
                .addQueryParameter("metric", "true")
                .build();

        Request requestHttp = new Request.Builder()
                .addHeader("accept", "application/json")
                .url(url)
                .build();

        String jsonResponse = client.newCall(requestHttp).execute().body().string();
        System.out.println(jsonResponse);
        System.out.println();

        int indexBody = jsonResponse.indexOf("[{\"Date\"");
        String jsonWOHeader = jsonResponse.substring(indexBody + 1);

        String [] days = new String [5];

        int i = days.length - 1;
        while (i >= 0){
            int indexDay = jsonWOHeader.lastIndexOf(",{\"Date\"");
            days[i] = jsonWOHeader.substring(indexDay + 1);
            int indexEnd = days[i].indexOf(",\"Sources\":[");
            days[i] = days[i].substring(0, indexEnd);
            if (i>0){
                jsonWOHeader = jsonWOHeader.substring(0,indexDay);
            }
            i--;
        }

        for (int j = 0; j < days.length; j++) {
            days[j] = parser(days[j]);
        }

        for (String day : days) {
            System.out.println(day);
        }
    }

    private static String parser(String string){
        string = string.replace("{\"Date\":\"", "Дата ");
        string = stringCut(string, "T07", "\"Tem", 0, 0, " ");
        string = string.replace("\"Temperature\":{\"Minimum\":{\"Value\":", "- Темепература: Минимум ");
        string = string.replace(",\"Unit\":\"C\",\"UnitType\":17},", "°С,");
        string = string.replace("\"Maximum\":{\"Value\":", " Максимум ");
        string = string.replace(",\"Unit\":\"C\",\"UnitType\":17}},", "°С. ");
        string = string.replace("\"Day\"", "Днём");
        string = stringCut(string, ":{\"", ":\"", 0, 2, " ");
        string = stringCut(string, "\",\"", "\"Ni", 0, 0, "");
        string = string.replace("\"Night\"", ", Ночью ");
        string = stringCut(string, ":{\"", "\":\"", 0, +3, "");
        string = stringCut(string, "\",\"", "}", 0, +1, "");
        string = string.replace("", "");
        string = stringCut(string, "", "", 0, 0, "");
        return string;
    }

    private static String stringCut(String string, String startFirstPart, String EndSecondPart,int swingFirstPart, int swingSecondPart, String separator) {
        int index1 = string.indexOf(startFirstPart);
        int index2 = string.indexOf(EndSecondPart);
        String partOne = string.substring(0, index1 + swingFirstPart);
        String partTwo = string.substring(index2 + swingSecondPart);
        String newString = partOne + separator + partTwo;
        return newString;
    }
}
