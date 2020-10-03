package app.fluky.ml.fluk.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Answers {
    private List<String> ans;
    private String right;

    public Answers(String a1, String a2, String a3, String a4) {
        ans = new ArrayList<>();
        ans.add(a1);
        ans.add(a2);
        ans.add(a3);
        ans.add(a4);
        shuffleAnswers();
        right = a1;
    }
    private void shuffleAnswers() {
        Collections.shuffle(ans);
    }
    public String getRight() {
        return right;
    }
    public String getA1() {
        return ans.get(0);
    }
    public String getA2() {
        return ans.get(1);
    }
    public String getA3() {
        return ans.get(2);
    }
    public String getA4() {
        return ans.get(3);
    }
}
