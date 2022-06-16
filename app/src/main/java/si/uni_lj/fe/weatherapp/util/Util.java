package si.uni_lj.fe.weatherapp.util;

import java.util.concurrent.ExecutionException;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public final class Util {

    public static final OkHttpClient CLIENT;
    public static final int FORECAST_REQUEST_CODE = 1;

    static {
        CLIENT = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .build();
    }

    public static Response getCurrentWeatherResponse(String cityName) throws ExecutionException, InterruptedException {
        String url = UrlBuilder.getCurrentWeatherUrl(cityName);
        CallbackFuture future = new CallbackFuture();
        CLIENT.newCall(buildRequest(url)).enqueue(future);
        return future.get();
    }

    public static void makeOneCallRequest(double latitude, double longitude, Callback callback) {
        String url = UrlBuilder.getOneCallWeatherUrl(latitude, longitude);
        CLIENT.newCall(buildRequest(url)).enqueue(callback);
    }

    private static Request buildRequest(String url) {
        return new Request.Builder()
                .url(url)
                .build();
    }
}
