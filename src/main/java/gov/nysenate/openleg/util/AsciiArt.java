package gov.nysenate.openleg.util;

public enum AsciiArt
{
    OPENLEG_2_LOGO("\n" +
        "=============================================================================\n" +
        "  .oooooo.                                          .oooo.         .oooo.   \n" +
        " d8P'  `Y8b                                       .dP\"\"Y88b       d8P'`Y8b  \n" +
        "888      888 oo.ooooo.   .ooooo.  ooo. .oo.             ]8P'     888    888 \n" +
        "888      888  888' `88b d88' `88b `888P\"Y88b          .d8P'      888    888 \n" +
        "888      888  888   888 888ooo888  888   888        .dP'         888    888 \n" +
        "`88b    d88'  888   888 888    .o  888   888      .oP     .o .o. `88b  d88' \n" +
        " `Y8bood8P'   888bod8P' `Y8bod8P' o888o o888o     8888888888 Y8P  `Y8bd8P'  \n" +
        "              888                                                           \n" +
        "             o888o                                                          \n" +
        "=============================================================================\n");

    private String text;

    AsciiArt(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
