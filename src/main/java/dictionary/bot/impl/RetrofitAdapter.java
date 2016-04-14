package dictionary.bot.impl;

import retrofit.RestAdapter;

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

    private DictionaryInterface dictionaryInterface;


    public DictionaryInterface getDictionaryInterface() {
        return dictionaryInterface;
    }

    /**
     * Create rest adapter to make rest call.
     * http://drawersapp.github.io/Retrofit/website/index.html
     */
    private void createDictionaryInterface() {
        // TODO - Set your end point.
        RestAdapter restAdapter = new RestAdapter.Builder()
              //  .setEndpoint("https://montanaflynn-dictionary.p.mashape.com")
                .setEndpoint("https://mashape-community-urban-dictionary.p.mashape.com")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        dictionaryInterface = restAdapter.create(DictionaryInterface.class);
    }

}
