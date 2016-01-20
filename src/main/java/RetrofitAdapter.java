import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by harshit on 20/1/16.
 */
public class RetrofitAdapter {
    private RetrofitAdapter() {
        createDictionaryInterface();
    }

    private static RetrofitAdapter retrofitAdapter;
    public synchronized static RetrofitAdapter getRetrofitAdapter() {
        if (retrofitAdapter == null) {
            retrofitAdapter = new RetrofitAdapter();
        }
        return retrofitAdapter;
    }

    private DictionaryInterface  dictionaryInterface;


    public DictionaryInterface getDictionaryInterface() {
        return dictionaryInterface;
    }

    private void createDictionaryInterface() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://montanaflynn-dictionary.p.mashape.com")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        dictionaryInterface = restAdapter.create(DictionaryInterface.class);
    }


}
