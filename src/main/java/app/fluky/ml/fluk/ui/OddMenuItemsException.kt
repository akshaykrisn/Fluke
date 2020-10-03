package app.fluky.ml.fluk.ui

/**
 * Created by andrewkhristyan on 11/22/16.
 */
class OddMenuItemsException : Exception() {
    override val message: String? = """Your menu should have non-odd size ¯\_(ツ)_/¯"""
}