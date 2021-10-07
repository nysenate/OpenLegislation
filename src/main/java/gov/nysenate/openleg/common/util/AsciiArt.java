package gov.nysenate.openleg.common.util;

public enum AsciiArt
{
    // ASCII Art generated at http://patorjk.com/software/taag/
    // OpenLeg 3.x font = Big
    // Start Elastic Search font = Star Wars

    OPENLEG_LOGO("""


               ____  _____  ______ _   _ _      ______ _____   ____           \s
              / __ \\|  __ \\|  ____| \\ | | |    |  ____/ ____| |___ \\          \s
             | |  | | |__) | |__  |  \\| | |    | |__ | |  __    __) |__  __   \s
             | |  | |  ___/|  __| | . ` | |    |  __|| | |_ |  |__ < \\ \\/ /   \s
             | |__| | |    | |____| |\\  | |____| |___| |__| |  ___) | >  <    \s
              \\____/|_|    |______|_| \\_|______|______\\_____| |____(_)_/\\_\\   \s
            =====================================================================
            Deployed on DATE\s
            =====================================================================
            """
    ),

    START_ELASTIC_SEARCH("""


                 _______.___________.    ___      .______     .___________.      \s
                /       |           |   /   \\     |   _  \\    |           |      \s
               |   (----`---|  |----`  /  ^  \\    |  |_)  |   `---|  |----`      \s
                \\   \\       |  |      /  /_\\  \\   |      /        |  |           \s
            .----)   |      |  |     /  _____  \\  |  |\\  \\----.   |  |           \s
            |_______/       |__|    /__/     \\__\\ | _| `._____|   |__|           \s
                                                                                 \s
             _______  __          ___           _______.___________. __    ______\s
            |   ____||  |        /   \\         /       |           ||  |  /      |
            |  |__   |  |       /  ^  \\       |   (----`---|  |----`|  | |  ,----'
            |   __|  |  |      /  /_\\  \\       \\   \\       |  |     |  | |  |    \s
            |  |____ |  `----./  _____  \\  .----)   |      |  |     |  | |  `----.
            |_______||_______/__/     \\__\\ |_______/       |__|     |__|  \\______|
                                                                                 \s
                 _______. _______     ___      .______        ______  __    __   \s
                /       ||   ____|   /   \\     |   _  \\      /      ||  |  |  |  \s
               |   (----`|  |__     /  ^  \\    |  |_)  |    |  ,----'|  |__|  |  \s
                \\   \\    |   __|   /  /_\\  \\   |      /     |  |     |   __   |  \s
            .----)   |   |  |____ /  _____  \\  |  |\\  \\----.|  `----.|  |  |  |  \s
            |_______/    |_______/__/     \\__\\ | _| `._____| \\______||__|  |__|  \s

            """
    );

    private final String text;

    AsciiArt(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
