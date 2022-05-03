package si.uni_lj.fe.weatherapp.util;

import okhttp3.HttpUrl;
import si.uni_lj.fe.weatherapp.BuildConfig;

public class UrlBuilder {

    public static String getCurrentWeatherUrl(double lat, double lon) {
        HttpUrl.Builder builder = new HttpUrl.Builder();
        builder.scheme("https")
                .host("api.openweathermap.org")
                .addPathSegments("data/2.5/weather")
                .addQueryParameter("appid", BuildConfig.API_OPENWEATHER)
                .addQueryParameter("lat", lat + "")
                .addQueryParameter("lon", lon + "")
                .addQueryParameter("units", "metric");
        return builder.build().toString();
    }

    public static String getCurrentWeatherUrl(String city) {
        HttpUrl.Builder builder = new HttpUrl.Builder();
        builder.scheme("https")
                .host("api.openweathermap.org")
                .addPathSegments("data/2.5/weather")
                .addQueryParameter("appid", BuildConfig.API_OPENWEATHER)
                .addQueryParameter("q", city)
                .addQueryParameter("units", "metric");
        //Log.e("Info", builder.build().toString());
        return builder.build().toString();
    }

    public static String getWeatherUrl(double lat, double lon, Forecast forecastType) {
        HttpUrl.Builder builder = new HttpUrl.Builder();
        builder.scheme("https")
                .host("api.openweathermap.org")
                .addPathSegments("data/2.5/" + forecastType)
                .addQueryParameter("appid", BuildConfig.API_OPENWEATHER)
                .addQueryParameter("lat", lat + "")
                .addQueryParameter("lon", lon + "")
                .addQueryParameter("units", "metric");
        return builder.build().toString();
    }

    public static String getWeatherUrl(String city, Forecast forecastType) {
        HttpUrl.Builder builder = new HttpUrl.Builder();
        builder.scheme("https")
                .host("api.openweathermap.org")
                .addPathSegments("data/2.5/" + forecastType)
                .addQueryParameter("appid", BuildConfig.API_OPENWEATHER)
                .addQueryParameter("q", city)
                .addQueryParameter("units", "metric");
        return builder.build().toString();
    }

    public static String getGeocodeUrl(String city) {
        HttpUrl.Builder builder = new HttpUrl.Builder();
        builder.scheme("https")
                .host("api.opencagedata.com")
                .addPathSegments("geocode/v1/json")
                .addQueryParameter("q", city)
                .addQueryParameter("key", BuildConfig.API_OPENCAGEDATA);
        return builder.build().toString();
    }
}
