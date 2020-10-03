package app.fluky.ml.fluk.frags;

public class StaticProfile {
    // static variable single_instance of type Singleton
    private static StaticProfile single_instance = null;

    // variable of type String


    // private constructor restricted to this class itself
    private StaticProfile() {

    }

    // static method to create instance of Singleton class
    public static StaticProfile getInstance() {
        if (single_instance == null)
            single_instance = new StaticProfile();

        return single_instance;
    }
}
