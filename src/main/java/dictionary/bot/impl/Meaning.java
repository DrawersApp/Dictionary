package dictionary.bot.impl;

import dictionary.bot.OutputBody;

import java.util.List;

/**
 * Created by harshit on 20/1/16.
 */
public class Meaning implements OutputBody {
    @Override
    public String toString() {
        return "dictionary.bot.impl.Meaning{" +
                "list=" + list +
                '}';
    }

    private List<Definitions> list;

    public List<Definitions> getList() {
        return list;
    }

    public void setList(List<Definitions> list) {
        this.list = list;
    }

    public class Definitions implements Comparable<Definitions> {

        @Override
        public int compareTo(Definitions o2) {
            if (Math.abs(this.getThumbs_up() - this.getThumbs_down()) < Math.abs(o2.getThumbs_up() - o2.getThumbs_down())) return -1;
            if (this == o2) return 0;
            else return 1;
        }

        @Override
        public String toString() {
            return "Definitions{" +
                    "definition='" + definition + '\'' +
                    ", thumbs_up=" + thumbs_up +
                    ", thumbs_down=" + thumbs_down +
                    '}';
        }

        private String definition;

        private int thumbs_up;

        private int thumbs_down;

        public int getThumbs_up() {
            return thumbs_up;
        }

        public void setThumbs_up(int thumbs_up) {
            this.thumbs_up = thumbs_up;
        }

        public int getThumbs_down() {
            return thumbs_down;
        }

        public void setThumbs_down(int thumbs_down) {
            this.thumbs_down = thumbs_down;
        }

        public String getDefinition() {
            return definition;
        }

        public void setDefinition(String definition) {
            this.definition = definition;
        }
    }

    @Override
    public String toUserString() {
        if (list != null) {
            return list.stream().sorted().findFirst().map(w -> w.getDefinition()).get();
        } else {
            return "";
        }
    }
}
