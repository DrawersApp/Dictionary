package dictionary.bot.impl;

import dictionary.bot.*;
import org.drawers.bot.lib.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by harshit on 1/2/16.
 */
public class MeaningOperations implements Operation {
    private static DrawersBotString meaning;

    public static DrawersBotString getMeaning() {
        return meaning;
    }

    public static void setMeaning(DrawersBotString meaning) {
        MeaningOperations.meaning = meaning;
    }

    static {
        List<BotStringElement> botStringElements = new ArrayList<>();
        botStringElements.add(new BotStringElement(BotStringType.U, "Explain:"));
        botStringElements.add(new BotStringElement(BotStringType.S, "onomatopoeia", null));
        meaning = new DrawersBotString(botStringElements, OperationsType.MEANING.name());
        OperationsManager.getOperationsManager().registerOperations(OperationsType.MEANING,
                MeaningOperations.class);
        DrawersBotStringHelp.getDrawersBotStringHelp().getDrawersBotStrings().add(meaning);
    }

    @Override
    public Response operateInternal(DrawersBotString drawersBotString) {
        String word = null;
        if (drawersBotString.getBotStringElements() == null ||
                drawersBotString.getBotStringElements().isEmpty()
                || drawersBotString.getBotStringElements().size() != meaning.getBotStringElements().size()) {
            return new BadResponse();
        }
        for (int i = 0 ; i< drawersBotString.getBotStringElements().size() ; i++) {
            BotStringElement botStringElement = drawersBotString.getBotStringElements().get(i);
            switch (botStringElement.getType().getDesc()) {
                case "UNEDITABLE":
                    if (!meaning.getBotStringElements().get(i).getText().equals(botStringElement.getText())) {
                        return new BadResponse();
                    }
                    break;
                case "STRING":
                    word = botStringElement.getText();
                    break;
            }
        }
        if (word == null) {
            return new BadResponse();
        }
        Meaning meaning = RetrofitAdapter.getRetrofitAdapter().getDictionaryInterface().getMeaning(word);
        return meaning;
    }

    @Override
    public boolean validateAndParse(DrawersBotString drawersBotString) {
        return false;
    }

    public static class BadResponse implements Response {

        @Override
        public String toUserString() {
            return "Something went wrong";
        }
    }
}
