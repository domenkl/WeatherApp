package si.uni_lj.fe.weatherapp.util;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@SuppressLint("NewApi")
public class CallbackFuture extends CompletableFuture<Response> implements Callback {

    @Override
    public void onFailure(@NonNull Call call, @NonNull IOException e) {
        super.completeExceptionally(e);
    }

    @Override
    public void onResponse(@NonNull Call call, @NonNull Response response) {
        super.complete(response);
    }
}
