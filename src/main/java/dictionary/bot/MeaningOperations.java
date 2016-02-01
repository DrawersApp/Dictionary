package dictionary.bot;

import java.util.ArrayList;
import java.util.Date;
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

    @Override
    public OutputBody makeRestCall(DrawersBotString body) {
        if (validate(body)) {
            Meaning meaning = RetrofitAdapter.getRetrofitAdapter().getDictionaryInterface().getMeaning(word);
            return meaning;
        }
        return null;
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

    private String word;

    private boolean validate(DrawersBotString drawersBotString) {
        if (drawersBotString.getBotStringElements() == null ||
                drawersBotString.getBotStringElements().isEmpty()
                || drawersBotString.getBotStringElements().size() != meaning.getBotStringElements().size()) {
            return false;
        }
        for (int i = 0 ; i< drawersBotString.getBotStringElements().size() ; i++) {
            BotStringElement botStringElement = drawersBotString.getBotStringElements().get(i);
            switch (botStringElement.getType().getDesc()) {
                case "UNEDITABLE":
                    if (!meaning.getBotStringElements().get(i).getText().equals(botStringElement.getText())) {
                        return false;
                    }
                    break;
                case "STRING":
                    word = botStringElement.getText();
                    break;
            }
        }
        if (this.word == null) {
            return false;
        }
        return true;
    }
}
