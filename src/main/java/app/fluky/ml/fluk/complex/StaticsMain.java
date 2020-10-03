package app.fluky.ml.fluk.complex;

public class StaticsMain {
    // static variable single_instance of type Singleton
    private static StaticsMain single_instance = null;
    public int loadcount = 0;
    // variable of type String
    public boolean isImageLoaderInitialized = false;

    // private constructor restricted to this class itself
    private StaticsMain() {

    }

    // static method to create instance of Singleton class
    public static StaticsMain getInstance() {
        if (single_instance == null)
            single_instance = new StaticsMain();

        return single_instance;
    }
}
