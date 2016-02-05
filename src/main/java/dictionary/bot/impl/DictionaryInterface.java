package dictionary.bot.impl;

import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Query;

/**
 * Interface to define all rest api.
 * http://drawersapp.github.io/Retrofit/website/index.html
 * http://www.javadoc.io/doc/com.squareup.retrofit/retrofit/1.9.0
 * Retrofit 1.9
 */
public interface DictionaryInterface {

    @Headers({
            "Accept: application/json",
            "X-Mashape-Key: YgXFx8fyJemshaRWbzMMcZ21Vcpqp1CE4jAjsnAFa0RBW99WmV"
    })
    @GET("/define")
    Meaning getMeaning(@Query("word") String word);
}
