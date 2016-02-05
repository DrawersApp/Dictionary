package dictionary.bot;

import dictionary.bot.impl.DictionaryInterface;
import retrofit.RestAdapter;

/**
 * Created by harshit on 5/2/16.
 */
public class TextPublishAdapter {
    private TextPublishAdapter() {
        createDictionaryInterface();
    }

    private static TextPublishAdapter retrofitAdapter;
    public synchronized static TextPublishAdapter getRetrofitAdapter() {
        if (retrofitAdapter == null) {
            retrofitAdapter = new TextPublishAdapter();
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
                .setEndpoint("https://montanaflynn-dictionary.p.mashape.com")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        dictionaryInterface = restAdapter.create(DictionaryInterface.class);
    }
}
